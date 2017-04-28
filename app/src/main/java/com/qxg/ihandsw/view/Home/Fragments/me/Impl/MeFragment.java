package com.qxg.ihandsw.view.Home.Fragments.me.Impl;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.qxg.ihandsw.Config;
import com.qxg.ihandsw.R;
import com.qxg.ihandsw.model.UserModel;
import com.qxg.ihandsw.presenter.Home.Fragments.me.Impl.FragmentMePresenter;
import com.qxg.ihandsw.utils.DataCleanUtil;
import com.qxg.ihandsw.utils.Log;
import com.qxg.ihandsw.view.BaseFragment;
import com.qxg.ihandsw.view.Home.Fragments.me.IFMeView;
import com.qxg.ihandsw.widget.AboutUsActivity;
import com.qxg.ihandsw.widget.LoginDialogActivity;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import top.wefor.circularanim.CircularAnim;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class MeFragment extends BaseFragment implements IFMeView {


    @Inject
    FragmentMePresenter presenter;
    @BindView(R.id.login_please)
    TextView loginPlease;
    @BindView(R.id.sid)
    TextView sid;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.login_info)
    LinearLayout loginInfo;
    @BindView(R.id.login)
    RelativeLayout login;
    @BindView(R.id.quit)
    RelativeLayout quit;
    @BindView(R.id.share_layout)
    LinearLayout share;
    @BindView(R.id.feed_back)
    LinearLayout feedBack;
    @BindView(R.id.about_us_layout)
    LinearLayout aboutUsLayout;
    @BindView(R.id.share_us)
    LinearLayout shareUs;
    @BindView(R.id.data_size)
    TextView dataSize;
    @BindView(R.id.clear_data)
    LinearLayout clearData;

    private UserModel userModel;
    Activity activity;

    public MeFragment() {
    }

    public static MeFragment newInstance() {
        Bundle args = new Bundle();
        MeFragment fragment = new MeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        userModel = new UserModel(activity);
    }

    @Override
    public int setContentView() {
        return R.layout.fragment_me;
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
    public void custom() {
        super.custom();
        //该方式是在returne view前操作的，所以可以在这个地方对view中的对象进行操作
        //首先判断用户是否已经登录
        initLogin();

        //取得缓存的大小
        dataSize.setText("(" + new DecimalFormat("#.##").format(DataCleanUtil.calDataSize(getActivity())) + "MB)");

    }

    private void initLogin() {
        if (userModel.isLogin()) {
            SharedPreferences sp
                    = getActivity().getSharedPreferences(Config.CARD_LOGIN_NAME, Context.MODE_PRIVATE);
            name.setText(sp.getString("name", "外星人?"));
            sid.setText(sp.getString("userid", "**********"));
            loginPlease.setVisibility(View.GONE);
            loginInfo.setVisibility(View.VISIBLE);
            login.setVisibility(View.GONE);
            quit.setVisibility(View.VISIBLE);
        } else {
            //没有登录，就显示请登录提示
            loginPlease.setText("请登录");
            loginPlease.setVisibility(View.VISIBLE);
            loginInfo.setVisibility(View.GONE);
            login.setVisibility(View.VISIBLE);
            quit.setVisibility(View.GONE);
        }
    }

    public void showLogin() {
        startActivityForResult(new Intent(getActivity(),
                LoginDialogActivity.class), Activity.RESULT_FIRST_USER);

    }

    @OnClick({R.id.login, R.id.quit, R.id.feed_back, R.id.about_us_layout, R.id.share_us,R.id.clear_data})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login:
                //startActivityForResult(intent, Activity.RESULT_FIRST_USER);
                showLogin();
                break;
            case R.id.quit:
                new AlertDialog.Builder(activity)
                        .setTitle("确定退出？")
                        .setPositiveButton("赶紧退，别废话！", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                userModel.quit();
                                initLogin();
                            }
                        })
                        .setNegativeButton("点错了!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).create().show();
                break;
            case R.id.feed_back:
                FeedbackAPI.openFeedbackActivity();
                break;
            case R.id.about_us_layout:
                CircularAnim.fullActivity(getActivity(), aboutUsLayout)
                        .colorOrImageRes(R.color.night)
                        .go(new CircularAnim.OnAnimationEndListener() {
                            @Override
                            public void onAnimationEnd() {
                                startActivity(new Intent(getActivity(), AboutUsActivity.class));
                            }
                        });
                break;
            case R.id.share_us:
                final BottomSheetDialog dialog = new BottomSheetDialog(activity);
                View itemView = LayoutInflater.from(activity).inflate(R.layout.dialog_share,null);
                itemView.findViewById(R.id.share2friend).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        shareToWeixin();
                        dialog.dismiss();
                    }
                });
                itemView.findViewById(R.id.share2quan).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        shareToWXQuan();
                        dialog.dismiss();
                    }
                });
                dialog.setContentView(itemView);
                dialog.show();
                break;

            case R.id.clear_data:
                clearData();
                break;
        }
    }

    private void clearData(){
        new AlertDialog.Builder(getActivity())
                .setTitle("注意")
                .setMessage("清除缓存数据不会影响您正常使用，是否继续？")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //开始清除缓存
                        DataCleanUtil.cleanSharedPreference(getActivity());
                        DataCleanUtil.cleanCustomCache("/data/data/"+getActivity().getPackageName() + "/app_webview");

                        //清除完毕后，将数据显示为0
                        Toast.makeText(getActivity(),"清除完毕",Toast.LENGTH_SHORT).show();
                        dataSize.setText("(0.00MB)");
                    }
                }).setNegativeButton("否",null)
                .show();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Activity.RESULT_FIRST_USER:
                //操作一番后，
                if (resultCode == LoginDialogActivity.HAS_LOGIN) {
                    //登录成功
                    initLogin();
                }
                break;
        }
    }

    private byte[] getBitmapByte(Bitmap bitmap){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        //参数1转换类型，参数2压缩质量，参数3字节流资源
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        try {
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }

    private void shareToWXQuan(){

        new ShareAction(getActivity()).setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                .withText("掌动山威：最好用的山威助手，快来下载吧！~")
                .withMedia(new UMImage(activity, R.drawable.two_code))
                .setCallback(new UMShareListener() {
                    @Override
                    public void onResult(SHARE_MEDIA share_media) {
                        Log.d("plat", "platform" + share_media);
                        Toast.makeText(getActivity(), " 分享成功~", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                        Toast.makeText(getActivity(), share_media + "分享失败啦", Toast.LENGTH_SHORT).show();
                        /*UmengTool.getSignature(getActivity());*/
                        if (throwable != null) {
                            Log.d("throw", "throw:" + throwable.getMessage());
                        }
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA share_media) {
                        Toast.makeText(getActivity(), "您取消了分享", Toast.LENGTH_SHORT).show();
                    }
                }).share();
    }

    private void shareToWeixin() {
        new ShareAction(getActivity()).setPlatform(SHARE_MEDIA.WEIXIN)
                .withText("掌动山威：最好用的山威助手，快来下载吧！~")
                .withTargetUrl("https://www.pgyer.com/zdsw")
                .setCallback(new UMShareListener() {
                    @Override
                    public void onResult(SHARE_MEDIA share_media) {
                        Log.d("plat", "platform" + share_media);
                        Toast.makeText(getActivity(), " 分享成功~", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                        Toast.makeText(getActivity(), share_media + "分享失败啦", Toast.LENGTH_SHORT).show();
                        /*UmengTool.getSignature(getActivity());*/
                        if (throwable != null) {
                            Log.d("throw", "throw:" + throwable.getMessage());
                        }
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA share_media) {
                        Toast.makeText(getActivity(), "您取消了分享", Toast.LENGTH_SHORT).show();
                    }
                }).share();
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis())
                :type + System.currentTimeMillis();
    }

}
