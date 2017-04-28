package com.qxg.ihandsw.view.Home.Fragments.home.Impl;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.flaviofaria.kenburnsview.KenBurnsView;
import com.qxg.ihandsw.Config;
import com.qxg.ihandsw.R;
import com.qxg.ihandsw.model.UserModel;
import com.qxg.ihandsw.presenter.Home.Fragments.home.Impl.FragmentHomePresenter;
import com.qxg.ihandsw.utils.Log;
import com.qxg.ihandsw.view.BaseFragment;
import com.qxg.ihandsw.view.Card.Impl.CardActivity;
import com.qxg.ihandsw.view.Home.Fragments.home.IFHomeView;
import com.qxg.ihandsw.view.Home.Impl.HomeActivity;
import com.qxg.ihandsw.view.Library.Impl.LibraryActivity;
import com.qxg.ihandsw.view.SmallTool.Impl.SmallToolActivity;
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

public class HomeFragment extends BaseFragment implements IFHomeView {


    @Inject
    FragmentHomePresenter presenter;
    @BindView(R.id.images)
    KenBurnsView images;
    @BindView(R.id.card_layout)
    View cardLayout;
    @BindView(R.id.library_layout)
    View libraryLayout;
    @BindView(R.id.small_tool)
    View smallTool;
    @BindView(R.id.forum_layout)
    View forumLayout;

    private Activity activity;
    private UserModel userModel;
    private Runnable r;
    private CatLoadingView loadingView;

    public HomeFragment(){

    }

    public static HomeFragment newInstance() {
        Bundle args = new Bundle();
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadingView = new CatLoadingView();
        loadingView.setCancelable(false);
        this.activity = getActivity();
        userModel = new UserModel(activity);
    }

    @Override
    public int setContentView() {
        return R.layout.fragment_home;
    }

    @Override
    public void initInjector() {
        mFragmentComponent.inject(this);
    }

    @Override
    public void attachView() {
        presenter.attachView(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        presenter.startImageAnim(images);
    }


    private boolean imagesFlag = true; //初始是运动着的


    @OnClick({R.id.card_layout, R.id.library_layout, R.id.small_tool, R.id.forum_layout, R.id.images})
    public void onClick(View view) {
        Log.i("0--------------0", "有人点击！");
        switch (view.getId()) {
            case R.id.card_layout:
                //点击一卡通，首先判断是否已经登录
                if (userModel.isLogin()) {
                    startActivity(getActivity(), CardActivity.class);
                } else {
                    ((HomeActivity) activity).login();
                }

                break;
            case R.id.library_layout:
                //显示
                if (userModel.isLogin()) {
                    startActivity(getActivity(), LibraryActivity.class);
                } else {
                    ((HomeActivity) activity).login();
                }
                break;
            case R.id.small_tool:
                startActivity(getActivity(), SmallToolActivity.class);
                break;
            case R.id.forum_layout:
                loadingView.show(((AppCompatActivity)getActivity()).getSupportFragmentManager(),"");
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
                    loadingView.dismiss();
                    ((HomeActivity) activity).login();
                }
                break;
            case R.id.images:
                if (imagesFlag) {
                    images.pause();
                    imagesFlag = false;
                } else {
                    images.resume();
                    imagesFlag = true;
                }
                break;
        }
    }

}
