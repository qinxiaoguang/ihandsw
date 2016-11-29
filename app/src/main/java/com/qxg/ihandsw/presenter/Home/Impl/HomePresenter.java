package com.qxg.ihandsw.presenter.Home.Impl;

import com.qxg.ihandsw.presenter.BasePresenter;
import com.qxg.ihandsw.presenter.Home.IHomePresenter;
import com.qxg.ihandsw.view.Home.IHomeView;
import com.qxg.ihandsw.view.IView;

import javax.inject.Inject;

/**
 * Created by Qking on 2016/10/31.
 */

public class HomePresenter extends BasePresenter implements IHomePresenter {

    IHomeView homeView;

    @Inject
    public HomePresenter(){

    }
    @Override
    public void attachView(IView iView) {
        homeView = (IHomeView) iView;
    }
}
