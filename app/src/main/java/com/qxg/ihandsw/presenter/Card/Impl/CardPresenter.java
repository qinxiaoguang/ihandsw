package com.qxg.ihandsw.presenter.Card.Impl;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;

import com.qxg.ihandsw.Config;
import com.qxg.ihandsw.model.CardModel;
import com.qxg.ihandsw.presenter.BasePresenter;
import com.qxg.ihandsw.presenter.Card.ICardPresenter;
import com.qxg.ihandsw.utils.Log;
import com.qxg.ihandsw.view.Card.ICardView;
import com.qxg.ihandsw.view.IView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.text.DecimalFormat;

import javax.inject.Inject;

/**
 * Created by Qking on 2016/11/3.
 */

public class CardPresenter extends BasePresenter implements ICardPresenter {

    ICardView cardView;
    Activity activity;
    CardModel cardModel;
    Handler handler;
    @Inject
    public CardPresenter(Activity activity){
        this.activity = activity;
        cardModel = new CardModel(activity);
        handler = new Handler();
    }

    @Override
    public void attachView(IView iView) {
        cardView = (ICardView) iView;
    }

    @Override
    public void getCardUserInfo() {
        cardModel.setOnGetCardUserInfo(new CardModel.OnGetCardUserInfo() {
            @Override
            public void onStart() {
                cardView.startLoading();
            }

            @Override
            public void onSucess(String result) {
                //加载完成
                Log.i("---------------",result);
                if(result.contains("location")){
                    //说明失败，需要重新展示验证码，并重新登录
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            cardView.reLogin();
                        }
                    });
                }else{
                    //说明成功，需要对result进行解析
                    /*
                                <div class="userInfoR">
                    <p class="Jbinfo">
                        <span>姓&nbsp;&nbsp;&nbsp;&nbsp;名：</span><em>秦晓光</em> <span>学工号：</span><em>201400820119</em>
                    </p>
                    <p class="Jbinfo">
                        <span>校园卡号：</span><em> 618872</em> <span>校园卡余额：</span><em>31.27</em>
                    </p>
                    <p class="Jbinfo">
                        <span>过渡余额：</span><em>0</em> <span class="red">（说明：在餐厅等处的卡机上进行刷卡操作后，过渡余额即会转入校园卡。）</span>
                    </p>
                    <p class="careful">
                        <span>挂失状态：</span><em> 正常卡</em><span>冻结状态：</span><em>正常</em></p>    </div>

                     */
                    Document doc = Jsoup.parse(result);
                    final Elements ems = doc.select("em");

                    //保存结果
                    SharedPreferences sp = activity.getSharedPreferences(Config.CARD_INFO_FILE_NAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    final String name = ems.get(0).text();
                    final String balance = ems.get(3).text();
                    final String transBalance = ems.get(4).text();
                    final String resultBalance =
                            new DecimalFormat("#.##").format(Double.parseDouble(balance) + Double.parseDouble(transBalance));
                        // 秦晓光
                    editor.putString(Config.CARD_INFO_NAME,name);
                        //201400820119
                    editor.putString(Config.CARD_INFO_SID,ems.get(1).text());
                        //618872
                    editor.putString(Config.CARD_INFO_ID,ems.get(2).text());
                        //25.18
                    editor.putString(Config.CARD_INFO_BALANCE,balance);
                        //0
                    editor.putString(Config.CARD_INFO_TRANSLATION_BALANCE,transBalance);
                        //正常卡
                    editor.putString(Config.CARD_INFO_STATE,ems.get(5).text());
                        //正常
                    editor.putString(Config.CARD_INFO_FREEZE_STATE,ems.get(6).text());
                    editor.commit();
                    Log.i("-------------->","执行到这了！！！");
                    //因为当前还是在子线程中运行，cardView.loadSucess是需要在主线程中运行，所以使用handler.post方法，将其放在主线程
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("---------->",Thread.currentThread()+"");
                            cardView.loadSucess(name,resultBalance);
                        }
                    });

                }
            }

            @Override
            public void onFaile() {

            }
        });
        cardModel.getCardUserInfo();
    }
}
