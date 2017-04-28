package com.qxg.ihandsw.widget.CardWidget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.qxg.ihandsw.Config;
import com.qxg.ihandsw.R;
import com.qxg.ihandsw.model.CardModel;
import com.qxg.ihandsw.utils.Log;
import com.roger.catloadinglibrary.CatLoadingView;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Qking on 2016/11/4.
 */
@SuppressLint("ValidFragment")
public class NetFeeDialog extends BaseBottomDialog {

    @BindView(R.id.errMsg)
    TextView errMsg;
    @BindView(R.id.net_fee_balance)
    TextView netFeeBalance;
    @BindView(R.id.net_fee_num)
    EditText netFeeNum;
    @BindView(R.id.queryPwd)
    EditText queryPwd;
    @BindView(R.id.confirm)
    Button confirm;


    public NetFeeDialog(Context context) {
        super(context);
    }

    public void initView() {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_netfee, null);
        ButterKnife.bind(this, view);

        cardModel.setOnGetNetFeeListener(new CardModel.OnGetNetFeeListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSucess(final String balance) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //将余额打印出来
                        netFeeBalance.setText("网费余额：" + balance);
                    }
                });
            }
        });
        cardModel.getNetFee();
        setContentView(view);
    }

    @OnClick(R.id.confirm)
    public void onClick() {
        //首先判断时间是否实在22~次日6点，如果是就直接返回，并提示错误
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        Log.i("当前小时",""+hour);
        if(hour>=22||hour <=6){
            errMsg.setText("请在6点~22点时间段内缴费~");
            errMsg.setVisibility(View.VISIBLE);
            return;
        }
        final CatLoadingView catLoading = new CatLoadingView();

        //开始缴费
        String netFee = netFeeNum.getText().toString();
        String pwd = queryPwd.getText().toString();

        //进行判断
        if (netFee.isEmpty() || pwd.isEmpty()) {
            errMsg.setText("数据不得为空");
            errMsg.setVisibility(View.VISIBLE);
            return;
        }

        //判断查询密码是否正确
        if (!pwd.equals(context.getSharedPreferences(Config.CARD_LOGIN_NAME, Context.MODE_PRIVATE).getString(Config.CARD_PWD, ""))) {
            errMsg.setText("查询密码错误");
            errMsg.setVisibility(View.VISIBLE);
            return;
        }
        //测试转1块
        //开始转账

        cardModel.setOnTransNetFeeListener(new CardModel.OnTransNetFeeListener() {
            @Override
            public void onStart() {
                catLoading.setCancelable(false);
                catLoading.show(((AppCompatActivity)context).getSupportFragmentManager(),"");
            }

            @Override
            public void onSucess() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(),"转账成功，欢迎下次使用~",Toast.LENGTH_SHORT).show();
                        catLoading.dismiss();
                        dismiss();
                    }
                });

            }

            @Override
            public void onFailed(final String msg) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
                        catLoading.dismiss();
                        dismiss();
                    }
                });

            }
        });
        cardModel.transNetFee(netFee);
    }
}
