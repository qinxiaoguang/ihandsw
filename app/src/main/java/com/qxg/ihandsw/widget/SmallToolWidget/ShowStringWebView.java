package com.qxg.ihandsw.widget.SmallToolWidget;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.qxg.ihandsw.R;
import com.qxg.ihandsw.utils.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Qking on 2016/11/15.
 */

public class ShowStringWebView extends Dialog {

    @BindView(R.id.loading_layout)
    View loading;
    @BindView(R.id.layout)
    RelativeLayout layout;
    Context context;
    private WebView webView;
    Handler handler;
    String url;

    public ShowStringWebView(Context context,String url) {
        super(context, R.style.FullScreenDialog);
        webView = new WebView(context);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        webView.setLayoutParams(lp);
        handler = new Handler(context.getMainLooper());
        this.url = url;
        this.context = context;
    }

    public ShowStringWebView initView(){
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_webview, null);
        ButterKnife.bind(this, view);
        layout.addView(webView);

        //webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        loading.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Document doc = Jsoup.parse(new URL(url),8000);
                    Element e = doc.select(".news_detail_cnt").first();
                    final String result = e.html();//取得html代码
                    Log.i("--->返回的html代码",result);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //成功后
                            loading.setVisibility(View.GONE);
                            webView.loadData(result,"text/html; charset=UTF-8",null);
                            webView.setVisibility(View.VISIBLE);
                        }
                    });

                }catch (Exception e){
                    Toast.makeText(context,"解析失败，请稍后重试",Toast.LENGTH_SHORT).show();
                }
            }
        }).start();
        setContentView(view);
        return this;
    }
}
