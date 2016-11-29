package com.qxg.ihandsw.presenter.SmallTool.Impl;

import com.qxg.ihandsw.presenter.BasePresenter;
import com.qxg.ihandsw.view.IView;
import com.qxg.ihandsw.view.SmallTool.ISmallToolView;

import javax.inject.Inject;

/**
 * Created by Qking on 2016/11/15.
 */

public class SmallToolPresenter extends BasePresenter {

    ISmallToolView view;

    @Inject
    public SmallToolPresenter(){

    }

    @Override
    public void attachView(IView iView) {
        view = (ISmallToolView) iView;
    }
}
