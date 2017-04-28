package com.qxg.ihandsw;

import android.app.Application;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.qxg.ihandsw.injector.component.ApplicationComponent;
import com.qxg.ihandsw.injector.component.DaggerApplicationComponent;
import com.qxg.ihandsw.injector.module.ApplicationModule;
import com.umeng.analytics.MobclickAgent;
import com.umeng.comm.core.CommunitySDK;
import com.umeng.comm.core.impl.CommunityFactory;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

/**
 * Created by Qking on 2016/10/31.
 */

public class IHandSwApplication extends Application {

    ApplicationComponent appComponent;


    @Override
    public void onCreate() {
        super.onCreate();
        appComponent = DaggerApplicationComponent.builder().applicationModule(new ApplicationModule(this)).build();

        CommunitySDK mCommSDK = CommunityFactory.getCommSDK(this);
        mCommSDK.initSDK(this);




        //调试模式
        MobclickAgent.setDebugMode( true );
        //反馈
        FeedbackAPI.init(this,"23526673");

        PlatformConfig.setWeixin("wxa125ff868cf025a9","9f2987ea3df7a0d11a63804a7fb7d49a");

        UMShareAPI.get(this);

        //LocationSDKManager.getInstance().addAndUse(new DefaultLocationImpl());

        final PushAgent mPushAgent = PushAgent.getInstance(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                mPushAgent.register(new IUmengRegisterCallback() {
                    @Override
                    public void onSuccess(String s) {
                    }

                    @Override
                    public void onFailure(String s, String s1) {
                    }
                });
            }
        }).start();
    }

    public ApplicationComponent getComponent(){
        return appComponent;
    }

}
