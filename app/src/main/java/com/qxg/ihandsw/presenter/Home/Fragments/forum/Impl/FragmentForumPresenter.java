package com.qxg.ihandsw.presenter.Home.Fragments.forum.Impl;

import com.qxg.ihandsw.presenter.BasePresenter;
import com.qxg.ihandsw.presenter.Home.Fragments.forum.IFragmentForumPresenter;
import com.qxg.ihandsw.view.Home.Fragments.forum.IFForumView;
import com.qxg.ihandsw.view.IView;

import javax.inject.Inject;

/**
 * Created by Qking on 2016/11/1.
 */

public class FragmentForumPresenter extends BasePresenter implements IFragmentForumPresenter{

    IFForumView forumView;
    @Inject
    public FragmentForumPresenter(){

    }
    @Override
    public void attachView(IView iView) {
        forumView = (IFForumView) iView;
    }
}
