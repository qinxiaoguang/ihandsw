package com.qxg.ihandsw.injector.module;

import android.app.Application;
import android.content.Context;

import com.qxg.ihandsw.Config;
import com.qxg.ihandsw.utils.ToastUtil;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Qking on 2016/10/31.
 */

@Module
public class ApplicationModule {
    private final Application mApplication;
    public ApplicationModule(Application application){mApplication = application;}

    @Provides
    @Singleton
    Context provideContext(){
        return mApplication.getApplicationContext();
    }

    @Provides
    @Singleton
    Retrofit provideRetrofit(){
        return new Retrofit.Builder()
                .baseUrl(Config.SERVER_ADDRESS)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Provides
    @Singleton
    ToastUtil provideToastUtil(){
        return new ToastUtil(mApplication.getApplicationContext());
    }

}
