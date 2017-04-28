package com.qxg.ihandsw.presenter.Splash.Impl;

import android.os.Handler;

import com.qxg.ihandsw.presenter.BasePresenter;
import com.qxg.ihandsw.presenter.Splash.ISplashPresenter;
import com.qxg.ihandsw.view.IView;
import com.qxg.ihandsw.view.Splash.ISplashView;

import javax.inject.Inject;

/**
 * Created by Qking on 2016/10/31.
 */

public class SplashPresenter extends BasePresenter implements ISplashPresenter{

    ISplashView splashView;
    Handler handler;
    Runnable r;
    @Inject
    public SplashPresenter(){
        handler = new Handler();
        r = new Runnable() {
            @Override
            public void run() {
                stepOut();
            }
        };
    }

    @Override
    public void attachView(IView iView) {
        splashView = (ISplashView) iView;
    }

    @Override
    public void afterCreate() {
        super.afterCreate();
        //等待3s后跳转到主页面
        handler.postDelayed(r,3000);
    }

    @Override
    public void stepOut() {
        //将三秒后自动跳转的处理删除
        handler.removeCallbacks(r);
        splashView.stepOut();
    }

    @Override
    public void stop() {
        //将3秒自动跳转删除
        handler.removeCallbacks(r);
    }

}
