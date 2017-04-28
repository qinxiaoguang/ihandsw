package com.qxg.ihandsw.injector.module;

import android.app.Activity;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Qking on 2016/10/31.
 */

@Module
public class ActivityModule {
    private final Activity activity;

    public ActivityModule(Activity activity){
        this.activity = activity;
    }

    @Provides
    Activity provideActivity(){
        return activity;
    }
}
