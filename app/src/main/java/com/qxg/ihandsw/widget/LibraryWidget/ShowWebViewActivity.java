package com.qxg.ihandsw.widget.LibraryWidget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.qxg.ihandsw.Config;
import com.qxg.ihandsw.R;
import com.qxg.ihandsw.model.LibraryModel;
import com.qxg.ihandsw.utils.Log;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Qking on 2016/11/18.
 */

public class ShowWebViewActivity extends AppCompatActivity {

    @BindView(R.id.loading_layout)
    View loading;
    @BindView(R.id.layout)
    RelativeLayout layout;

    LibraryModel libraryModel;
    Handler handler;
    int flag;
    public static final int LOAD_ORDER = 1;
    public static final int LOAD_UN_ORDER = -1;
    public static int floor;
    private String roomId;
    private String param;
    private String date;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //设置全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_webview);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        webView = new WebView(this);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        webView.setLayoutParams(lp);
        handler = new Handler();
        libraryModel = new LibraryModel(this);
        flag = intent.getIntExtra("flag",LOAD_ORDER);
        if(flag == LOAD_ORDER){
            roomId = intent.getStringExtra("roomId");
            date = intent.getStringExtra("date");
            param = intent.getStringExtra("param");
        }
        initView();
    }


    public void initView() {
        layout.addView(webView);
        final Map<String,String> headers = new HashMap<>();
        String cookie = getSharedPreferences(Config.LIBRARY_INFO_FILE_NAME,Context.MODE_PRIVATE).getString(Config.LIBRARY_LOGIN_COOKIE,"");
        headers.put("Cookie",cookie);
        headers.put("Referer","http://202.194.46.22/Florms/FormSYS.aspx");

        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        CookieSyncManager.createInstance(this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeSessionCookie();//移除
        cookieManager.setCookie(Config.ORDER_ITEM_BASE_URL+"?roomId="+roomId+"&date=" + date, cookie);
        cookieManager.setCookie(Config.ORDER_RECORD_ADDRESS, cookie);
        CookieSyncManager.getInstance().sync();
        if (Build.VERSION.SDK_INT < 21) {
            CookieSyncManager.getInstance().sync();
        } else {
            CookieManager.getInstance().flush();
        }

        // 设置可以支持缩放
        webView.getSettings().setSupportZoom(true);
        // 设置出现缩放工具
        webView.getSettings().setBuiltInZoomControls(true);
        //扩大比例的缩放
        webView.getSettings().setUseWideViewPort(true);
//        //自适应屏幕
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.getSettings().setLoadWithOverviewMode(true);

        webView.setWebViewClient(new WebViewClient(){

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                loading.setVisibility(View.VISIBLE);
                webView.animate().alpha(0.2f).setDuration(1000).start();
                super.onPageStarted(view, url, favicon);
                Log.i("访问的网站是--->",url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                loading.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
                webView.animate().alpha(1).setDuration(300).start();
                super.onPageFinished(view, url);
            }
        });

        if (flag == LOAD_ORDER){
            //加载预约界面
            //http://202.194.46.22/FunctionPages/SeatBespeak/BespeakSeatLayout.aspx?roomId=000103&date=63614246400000000,
            Log.i("---->将要加载的页面是",Config.ORDER_ITEM_BASE_URL+"?" + param);
            webView.loadUrl(Config.ORDER_ITEM_BASE_URL+"?"+param,headers);
        }else if(flag == LOAD_UN_ORDER){
            webView.loadUrl(Config.ORDER_RECORD_ADDRESS,headers);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }


    @Override
    protected void onDestroy() {
        if(webView!=null){
            webView.removeAllViews();
            webView.destroy();
        }
        super.onDestroy();
    }
}
