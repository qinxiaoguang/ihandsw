package com.qxg.ihandsw.widget.LibraryWidget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Qking on 2016/11/8.
 */

public class ShowWebViewDialog extends Dialog {

    @BindView(R.id.loading_layout)
    View loading;
    @BindView(R.id.layout)
    RelativeLayout layout;

    Context context;
    LibraryModel libraryModel;
    Handler handler;
    int flag;
    public static final int LOAD_ORDER = 1;
    public static final int LOAD_UN_ORDER = -1;
    public static int floor;
    private String roomId;
    private String date;
    private WebView webView;

    public ShowWebViewDialog(Context context,int flag) {
        this(context,flag,null,null);
    }

    public ShowWebViewDialog(Context context,int flag,String roomId,String date){
        super(context, R.style.FullScreenDialog);
        this.context = context;
        webView = new WebView(context);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        webView.setLayoutParams(lp);
        handler = new Handler(context.getMainLooper());
        libraryModel = new LibraryModel((Activity) context);
        this.flag = flag;
        if(flag == LOAD_ORDER){
            this.roomId = roomId;
            this.date = date;
        }
    }

    public ShowWebViewDialog initView() {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_webview, null);
        ButterKnife.bind(this, view);
        layout.addView(webView);
        final Map<String,String> headers = new HashMap<>();
        String cookie = context.getSharedPreferences(Config.LIBRARY_INFO_FILE_NAME,Context.MODE_PRIVATE).getString(Config.LIBRARY_LOGIN_COOKIE,"");
        headers.put("Cookie",cookie);
        headers.put("Referer","http://202.194.46.22/Florms/FormSYS.aspx");

        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        CookieSyncManager.createInstance(context);
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
//        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
//        webView.getSettings().setLoadWithOverviewMode(true);

        webView.setWebViewClient(new WebViewClient(){

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                loading.setVisibility(View.VISIBLE);
                webView.animate().alpha(0.2f).setDuration(1000).start();
                super.onPageStarted(view, url, favicon);
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
            Log.i("---->将要加载的页面是",Config.ORDER_ITEM_BASE_URL+"?roomId="+roomId+"&date=" + date);
            webView.loadUrl(Config.ORDER_ITEM_BASE_URL+"?roomId="+roomId+"&date=" + date,headers);
        }else if(flag == LOAD_UN_ORDER){
            webView.loadUrl(Config.ORDER_RECORD_ADDRESS,headers);
        }

        setContentView(view);
        return this;
    }

    @Override
    public void onBackPressed() {
        webView.removeAllViews();
        webView.destroy();
        super.onBackPressed();
    }

}
