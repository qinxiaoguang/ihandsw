package com.qxg.ihandsw.widget.LibraryWidget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.qxg.ihandsw.Config;
import com.qxg.ihandsw.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Qking on 2016/11/8.
 */

public class ShowSearchWebViewDialog extends Dialog {
    Context context;
    @BindView(R.id.layout)
    RelativeLayout layout;
    @BindView(R.id.loading_layout)
    RelativeLayout loadingLayout;

    private WebView webView;

    public ShowSearchWebViewDialog(Context context) {
        super(context,R.style.FullScreenDialog);
        this.context = context;
        webView = new WebView(context);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        webView.setLayoutParams(lp);
    }

    public ShowSearchWebViewDialog initView() {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_webview, null);
        ButterKnife.bind(this, view);
        layout.addView(webView);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.setWebViewClient(new WebViewClient(){

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                loadingLayout.setVisibility(View.VISIBLE);
                webView.animate().alpha(0.2f).setDuration(1000).start();
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                loadingLayout.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
                webView.animate().alpha(1).setDuration(300).start();
                super.onPageFinished(view, url);
            }
        });

        webView.loadUrl(Config.LIBRARY_SEARCH_BOOK_URL);

        setContentView(view);
        return this;
    }

    @Override
    public void onBackPressed() {
        if(webView.canGoBack()){
            webView.goBack();
        }else{
            webView.removeAllViews();
            webView.destroy();
            super.onBackPressed();
        }
    }
}
