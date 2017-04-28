package com.qxg.ihandsw.view.Home.Impl;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.qxg.ihandsw.Config;
import com.qxg.ihandsw.R;
import com.qxg.ihandsw.model.UserModel;
import com.qxg.ihandsw.presenter.BasePresenter;
import com.qxg.ihandsw.presenter.Home.Impl.HomePresenter;
import com.qxg.ihandsw.utils.Activities;
import com.qxg.ihandsw.utils.BuildUtil;
import com.qxg.ihandsw.utils.Log;
import com.qxg.ihandsw.utils.NetWorkUtil;
import com.qxg.ihandsw.view.BaseActivity;
import com.qxg.ihandsw.view.Home.Fragments.forum.Impl.ForumFragment;
import com.qxg.ihandsw.view.Home.Fragments.home.Impl.HomeFragment;
import com.qxg.ihandsw.view.Home.Fragments.me.Impl.MeFragment;
import com.qxg.ihandsw.view.Home.IHomeView;
import com.roger.catloadinglibrary.CatLoadingView;
import com.umeng.comm.core.CommunitySDK;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.constants.ErrorCode;
import com.umeng.comm.core.impl.CommunityFactory;
import com.umeng.comm.core.login.LoginListener;
import com.umeng.socialize.UMShareAPI;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends BaseActivity implements IHomeView {

    @Inject
    HomePresenter presenter;
    @BindView(R.id.frameLayout)
    FrameLayout frameLayout;
    @BindView(R.id.bottom_nav)
    BottomNavigationBar bottomNav;
    @BindView(R.id.coordinator_layout)
    CoordinatorLayout layout;

    private UserModel userModel;
    private CatLoadingView loadingView;

    private long pressBackTime = 0;

    private List<Fragment> fragments;
    private static Fragment currentFragment;

    @Override
    public int setContentView() {
        return R.layout.activity_home;
    }

    @Override
    protected void custom() {
        super.custom();

        //检查网络
        NetWorkUtil.checkNetWork(getApplicationContext());

        //初始化fragments
        if(currentFragment == null){
            Log.i("---","");
        }
        initFragments();

        userModel = new UserModel(HomeActivity.this);
        loadingView = new CatLoadingView();
        loadingView.setCancelable(false);

        //初始化bottomNav
        bottomNav.setMode(BottomNavigationBar.MODE_FIXED);
        bottomNav.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        bottomNav.addItem(new BottomNavigationItem(R.drawable.home, "主页").setActiveColorResource(R.color.colorPrimary))
                .addItem(new BottomNavigationItem(R.drawable.forum, "话山威").setActiveColorResource(R.color.colorAccent))
                .addItem(new BottomNavigationItem(R.drawable.personal, "我").setActiveColorResource(R.color.colorPrimaryDark))
                .setFirstSelectedPosition(0)
                .initialise();
        bottomNav.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                replaceFragment(R.id.frameLayout,fragments.get(position));
                if(position == 1){
                    //点击了论坛
                    loadingView.show(((AppCompatActivity)HomeActivity.this).getSupportFragmentManager(),"");
                    if (userModel.isLogin()) {
                        final CommunitySDK mCommSDK = CommunityFactory.getCommSDK(HomeActivity.this);
                        CommUser user = new CommUser();
                        String nowId = HomeActivity.this.getSharedPreferences(Config.CARD_LOGIN_NAME, Context.MODE_PRIVATE).getString(Config.USER_ID,"");
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

                        mCommSDK.loginToUmengServerBySelfAccount(HomeActivity.this, user, new LoginListener() {
                            @Override
                            public void onStart() {
                            }

                            @Override
                            public void onComplete(int i, CommUser commUser) {
                                loadingView.dismiss();
                                Log.d("tag", "login result is"+i);          //获取登录结果状态码
                                if (ErrorCode.NO_ERROR==i) {
                                    //登录成功，可以打开社区，也可以进行其他的操作，开发者自己定义
                                    mCommSDK.openCommunity(HomeActivity.this);
                                }else{
                                    Toast.makeText(HomeActivity.this,"啊哦出错了，请稍后再试~",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        loadingView.dismiss();
                        HomeActivity.this.login();
                    }
                }
            }

            @Override
            public void onTabUnselected(int position) {

            }

            @Override
            public void onTabReselected(int position) {

            }
        });

        //安卓6.0检查权限
        checkPermission();
    }

    // TODO: 2016/11/19   新加代码开始
    private static final int GRANT_PERMISSION_REQUEST_CODE = 13123;

    private void checkPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.GET_ACCOUNTS)
                        != PackageManager.PERMISSION_GRANTED) {
            //开始授权
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE
                            ,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.GET_ACCOUNTS},
                    GRANT_PERMISSION_REQUEST_CODE);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case GRANT_PERMISSION_REQUEST_CODE: {
                int size = grantResults.length;
                if (size > 0) {
                    for(int i=0;i<size;i++){
                        if(grantResults[i] !=  PackageManager.PERMISSION_GRANTED){
                            //只要有一个不通过，那么就让程序崩溃
                            new AlertDialog.Builder(this)
                                    .setMessage("由于安卓6.0以上的系统需要用户授权，而当前询问的权限是该软件正常运行必须使用的，如果您选择了取消，软件可能会有崩溃的问题，(软件不存在安全隐患，请不要担心)。是否重新授权?")
                                    .setNegativeButton("否", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            new AlertDialog.Builder(HomeActivity.this)
                                                    .setMessage("您未授予该软件权限，将要退出")
                                                    .setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            Activities.finish();
                                                        }
                                                    })
                                                    .show();
                                        }
                                    })
                                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            //重新授权
                                            ActivityCompat.requestPermissions(HomeActivity.this,
                                                    new String[]{Manifest.permission.READ_PHONE_STATE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE
                                                            ,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.GET_ACCOUNTS},
                                                    GRANT_PERMISSION_REQUEST_CODE);
                                        }
                                    })
                                    .show();
                            return;
                        }
                    }
                }
                return;
            }
        }
    }

    // TODO: 2016/11/19   新加代码结束

    /**
     * 替换fragment
     */
    private void replaceFragment(int frameId,Fragment fragment){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        //设置转场动画
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        if(null != currentFragment){
            //ft.hide(currentFragment);
            ft.hide(currentFragment);
        }
        currentFragment = fragment;

        if(!fragment.isAdded()){
            ft.add(frameId,fragment,fragment.getClass().getName());
        }else{
            //ft.show(fragment);
            ft.show(fragment);
        }
        ft.commitAllowingStateLoss();
    }

    private void initFragments() {
        fragments = new ArrayList<>();
        fragments.add(HomeFragment.newInstance());

        //CommunityMainFragment mFeedsFragment = new CommunityMainFragment();
        //mFeedsFragment.setBackButtonVisibility(View.INVISIBLE);

        fragments.add(ForumFragment.newInstance());
        fragments.add(MeFragment.newInstance());
        //将初次显示的fragment设置为第一个
        replaceFragment(R.id.frameLayout, fragments.get(0));
    }

    @Override
    public void attachView() {
        presenter.attachView(this);
    }

    @Override
    public void initInjector() {
        ButterKnife.bind(this);
        mActivityComponent.inject(this);
    }

    @Override
    protected void beforeSetView() {
        super.beforeSetView();
        setTransStatusBar(true);
    }

    @Override
    public void onBackPressed() {
        long tmp = System.currentTimeMillis();
        if(pressBackTime != 0 && tmp - pressBackTime <=1000){
            Activities.finish();
        }else{
            if(BuildUtil.isLargeThanAPI21()){
                Snackbar.make(layout,"再按一次退出!",Snackbar.LENGTH_SHORT)
                        .setAction("直接退出", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Activities.finish();
                            }
                        }).show();
            }else{
                Toast.makeText(this,"再按一次退出!",Toast.LENGTH_SHORT).show();
            }

            pressBackTime = tmp;
        }
    }

    @Override
    public BasePresenter getPresenter() {
        return presenter;
    }

    public void login(){
        //
        bottomNav.selectTab(2);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ((MeFragment)fragments.get(2)).showLogin();
            }
        },100);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode,resultCode,data);
    }

}
