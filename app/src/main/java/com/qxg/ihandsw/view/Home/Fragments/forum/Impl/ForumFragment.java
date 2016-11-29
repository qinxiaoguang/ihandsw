package com.qxg.ihandsw.view.Home.Fragments.forum.Impl;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.qxg.ihandsw.Config;
import com.qxg.ihandsw.R;
import com.qxg.ihandsw.model.UserModel;
import com.qxg.ihandsw.presenter.Home.Fragments.forum.Impl.FragmentForumPresenter;
import com.qxg.ihandsw.utils.Log;
import com.qxg.ihandsw.view.BaseFragment;
import com.qxg.ihandsw.view.Home.Fragments.forum.IFForumView;
import com.qxg.ihandsw.view.Home.Impl.HomeActivity;
import com.roger.catloadinglibrary.CatLoadingView;
import com.umeng.comm.core.CommunitySDK;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.constants.ErrorCode;
import com.umeng.comm.core.impl.CommunityFactory;
import com.umeng.comm.core.login.LoginListener;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class ForumFragment extends BaseFragment implements IFForumView {

    @Inject
    FragmentForumPresenter presenter;
    @BindView(R.id.get_in)
    TextView getIn;

    UserModel userModel;
    CatLoadingView loadingView;

    public ForumFragment() {
        // Required empty public constructor
        loadingView = new CatLoadingView();
        loadingView.setCancelable(false);
    }

    public static ForumFragment newInstance() {
        Bundle args = new Bundle();
        ForumFragment fragment = new ForumFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int setContentView() {
        return R.layout.fragment_forum;
    }

    @Override
    public void initInjector() {
        mFragmentComponent.inject(this);

    }

    @Override
    public void attachView() {
        presenter.attachView(this);
    }


    @OnClick(R.id.get_in)
    public void onClick() {
        userModel = new UserModel(getActivity());
        if (userModel.isLogin()) {
            final CommunitySDK mCommSDK = CommunityFactory.getCommSDK(getActivity());
            CommUser user = new CommUser();
            String nowId = getActivity().getSharedPreferences(Config.CARD_LOGIN_NAME, Context.MODE_PRIVATE).getString(Config.USER_ID,"");
            String tmpName;
            if(nowId.length() == 12){
                //学生
                tmpName = "游客" + new StringBuilder(nowId.substring(2,4)).reverse().toString()
                        + new StringBuilder(nowId.substring(6)).reverse().toString() ;
            }else{
                tmpName = "游客" + new StringBuilder(nowId).reverse().toString();
            }
            user.id = nowId;
            user.name = tmpName;

            mCommSDK.loginToUmengServerBySelfAccount(getActivity(), user, new LoginListener() {
                @Override
                public void onStart() {
                    loadingView.show(((AppCompatActivity)getActivity()).getSupportFragmentManager(),"");
                }

                @Override
                public void onComplete(int i, CommUser commUser) {
                    loadingView.dismiss();
                    Log.d("tag", "login result is"+i);          //获取登录结果状态码
                    if (ErrorCode.NO_ERROR==i) {
                        //登录成功，可以打开社区，也可以进行其他的操作，开发者自己定义
                        mCommSDK.openCommunity(getActivity());
                    }else{
                        Toast.makeText(getActivity(),"啊哦出错了，请稍后再试~",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            ((HomeActivity) getActivity()).login();
        }
    }
}
