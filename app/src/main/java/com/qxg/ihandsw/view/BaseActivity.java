package com.qxg.ihandsw.view;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;
import android.transition.Fade;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.qxg.ihandsw.IHandSwApplication;
import com.qxg.ihandsw.injector.component.ActivityComponent;
import com.qxg.ihandsw.injector.component.ApplicationComponent;
import com.qxg.ihandsw.injector.component.DaggerActivityComponent;
import com.qxg.ihandsw.injector.module.ActivityModule;
import com.qxg.ihandsw.presenter.BasePresenter;
import com.qxg.ihandsw.utils.Activities;
import com.qxg.ihandsw.utils.BuildUtil;
import com.qxg.ihandsw.utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

import retrofit2.Retrofit;

/**
 * Created by Qking on 2016/10/9.
 * 创建该BaseActivity的目的是要放置共同函数以及配置信息，并且所有Activity共有的配置信息
 */

public abstract class BaseActivity extends AppCompatActivity implements IView{
    String tmp = "qxg";  //临时存放的值，以后想到再添加
    public ActivityComponent mActivityComponent;
    public static ApplicationComponent mApplicationComponent;
    public ToastUtil mToastUtil;
    public Retrofit mRetrofit;
    private boolean transStatusFlag = false;
    public abstract int setContentView();
    /**
     * 如果在onCreate完成创建后，需要其他额外的操作，并且该操作必须在onCreate生命周期中完成
     * 子类不需重写该方法，需要重写presenter的afterCreate()方法
     * 如果有些任务无法在presenter中完成，而必须在Activity中完成，那么可以重写该方法，重写的最后要记得调用super.afterCreate();
     */
    protected void afterCreate(){
        Log.i("afterCreate","afterCreate is running");
        BasePresenter bp = getPresenter();
        if(bp!=null) bp.afterCreate();
    }

    /**
     * 该方法是以便书写自己想要的额外操作，并在afterCreate()方法前
     */
    protected void custom(){
        Log.i("custome","custome() is running");
    }


    protected void beforeSetView(){
        Log.i("beforeSetView","beforeSetView() is running");
    }
    /**
     * 重写父类的onCreate方法，在该方法中调用initInjector，afterCreate等方法，在以后的activity代码中就不用书写这些
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(BuildUtil.isLargeThanAPI21()){
            Fade fade = new Fade();
            Explode explode = new Explode();
            fade.setDuration(100);
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().setEnterTransition(explode);
            getWindow().setExitTransition(fade);
        }

        super.onCreate(savedInstanceState);
        beforeSetView();
        //设定透明状态栏
        if(!BuildUtil.isLowerThanAPI19() && transStatusFlag){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        int layout = setContentView();
        setContentView(layout);
        mApplicationComponent = ((IHandSwApplication)getApplication()).getComponent();
        mActivityComponent = DaggerActivityComponent.builder()
                .activityModule(new ActivityModule(this))
                .applicationComponent(mApplicationComponent)
                .build();
        mToastUtil = mActivityComponent.getToastUtil();
        mRetrofit = mActivityComponent.getRetrofit();
        Activities.add(this);
        initInjector();
        attachView();
        custom();
        afterCreate();
        //推送必调用
        PushAgent.getInstance(this).onAppStart();
    }


    /**
     * 重新加载
     */
    @Override
    public void refresh() {
        finish();
        startActivity(getIntent());
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
        super.onDestroy();
        Activities.remove(this);
    }

    /**
     * 使用的是自定义的Toast实现，如果子类想要调用ToastUtil的不同方法，需要重写该方法
     */
    @Override
    public void showMsg(String msg) {
        mToastUtil.showShortMsg(msg);
    }

    /**
     * 将Presenter与IView进行关联
     */
    public abstract void attachView();

    /**
     * 初始化inject，包括ButterKnife.如果项目自动生成的ButterKnife.bind(this)则迁移到此方法中
     */
    public abstract void initInjector();

    /**
     *  得到presenter，如果没有重写该方法，就不会调用afterCreate()。
     */
    public abstract BasePresenter getPresenter();

    /**
     * 如果想让状态栏置为透明，需要在beforeSetView使用该方法
     */
    public void setTransStatusBar(boolean flag){
        transStatusFlag = flag;
    }

    public void startActivity(Activity one,Class two){
        Intent intent = new Intent(one,two);
        if(BuildUtil.isLargeThanAPI21()){
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(one).toBundle());
        }else{
            startActivity(intent);
        }
    }

}
