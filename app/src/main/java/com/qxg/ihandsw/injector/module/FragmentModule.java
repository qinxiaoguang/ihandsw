package com.qxg.ihandsw.injector.module;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Qking on 2016/11/1.
 */

@Module
public class FragmentModule {
    private Fragment fragment;
    public FragmentModule(Fragment fragment){
        this.fragment = fragment;
    }

    @Provides
    FragmentManager provideFragmentManager(){
        return fragment.getFragmentManager();
    }

    @Provides Fragment provideFragment(){
        return fragment;
    }

    @Provides
    Activity provideActivity(){
        return fragment.getActivity();
    }
}
