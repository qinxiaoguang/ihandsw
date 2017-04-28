package com.qxg.ihandsw.view;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qxg.ihandsw.IHandSwApplication;
import com.qxg.ihandsw.injector.component.ApplicationComponent;
import com.qxg.ihandsw.injector.component.DaggerFragmentComponent;
import com.qxg.ihandsw.injector.component.FragmentComponent;
import com.qxg.ihandsw.injector.module.FragmentModule;
import com.qxg.ihandsw.utils.BuildUtil;
import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;

/**
 * Created by Qking on 2016/11/1.
 */

public abstract class BaseFragment extends Fragment implements IView{


    public FragmentComponent mFragmentComponent;
    public static ApplicationComponent mApplicationComponent;
    public abstract int setContentView();
    public abstract void initInjector();
    public abstract void attachView();

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("HomeScreen");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("HomeScreen");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        int layout = setContentView();
        View view =  inflater.inflate(setContentView(), container, false);
        ButterKnife.bind(this,view);
        mApplicationComponent = ((IHandSwApplication)getActivity().getApplication()).getComponent();
        mFragmentComponent = DaggerFragmentComponent.builder()
                .fragmentModule(new FragmentModule(this))
                .applicationComponent(mApplicationComponent)
                .build();
        initInjector();
        attachView();
        custom();
        return view;
    }

    //自定义操作
    public void custom(){

    }


    @Override
    public void showMsg(String msg) {

    }

    @Override
    public void refresh() {

    }

    public void startActivity(Activity one, Class two){
        Intent intent = new Intent(one,two);
        if(BuildUtil.isLargeThanAPI21()){
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(one).toBundle());
        }else{
            startActivity(intent);
        }
    }

}
