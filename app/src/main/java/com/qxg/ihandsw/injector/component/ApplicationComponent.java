package com.qxg.ihandsw.injector.component;

import android.content.Context;

import com.qxg.ihandsw.injector.module.ApplicationModule;
import com.qxg.ihandsw.utils.ToastUtil;

import javax.inject.Singleton;

import dagger.Component;
import retrofit2.Retrofit;

/**
 * Created by Qking on 2016/10/31.
 */
@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {
    Context getContext();
    Retrofit getRetrofit();
    ToastUtil getToastUtil();
}
