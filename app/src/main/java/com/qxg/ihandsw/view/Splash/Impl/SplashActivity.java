package com.qxg.ihandsw.view.Splash.Impl;

import android.content.Intent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.qxg.ihandsw.R;
import com.qxg.ihandsw.presenter.BasePresenter;
import com.qxg.ihandsw.presenter.Splash.Impl.SplashPresenter;
import com.qxg.ihandsw.view.BaseActivity;
import com.qxg.ihandsw.view.Home.Impl.HomeActivity;
import com.qxg.ihandsw.view.Splash.ISplashView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SplashActivity extends BaseActivity implements ISplashView {

    @Inject
    SplashPresenter presenter;
    @BindView(R.id.splash_step_out)
    Button splashStepOut;
    @BindView(R.id.splash_bottom_info)
    RelativeLayout splashBottomInfo;
    @BindView(R.id.splash_image)
    ImageView splashImage;
    @BindView(R.id.activity_splash)
    RelativeLayout activitySplash;

    @Override
    public int setContentView() {
        return R.layout.activity_splash;
    }

    @Override
    protected void afterCreate() {
        super.afterCreate();
        //显示进厂图片
        splashImage.animate().alpha(1).setDuration(2000).start();
    }

    @Override
    public void attachView() {
        presenter.attachView(this);
    }

    @Override
    public void initInjector() {
        mActivityComponent.inject(this);
        ButterKnife.bind(this);
    }

    @Override
    public BasePresenter getPresenter() {
        return presenter;
    }


    //点击跳过
    @OnClick(R.id.splash_step_out)
    public void onClick() {
        presenter.stepOut();
    }

    @Override
    public void stepOut() {
        finish();
        startActivity(new Intent(SplashActivity.this, HomeActivity.class));
    }

    @Override
    protected void onDestroy() {
        presenter.stop();
        super.onDestroy();
    }

    @Override
    protected void beforeSetView() {
        super.beforeSetView();
        setTransStatusBar(true);
    }
}
