package com.qxg.ihandsw.presenter.Home.Fragments.me.Impl;

import com.qxg.ihandsw.presenter.BasePresenter;
import com.qxg.ihandsw.presenter.Home.Fragments.me.IFragmentMePresenter;
import com.qxg.ihandsw.view.Home.Fragments.me.IFMeView;
import com.qxg.ihandsw.view.IView;

import javax.inject.Inject;

/**
 * Created by Qking on 2016/11/1.
 */

public class FragmentMePresenter extends BasePresenter implements IFragmentMePresenter{
    IFMeView meView;

    @Inject
    public FragmentMePresenter(){

    }
    @Override
    public void attachView(IView iView) {
        meView = (IFMeView) iView;
    }
}
