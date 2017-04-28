package com.qxg.ihandsw.widget.LibraryWidget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qxg.ihandsw.R;
import com.qxg.ihandsw.model.LibraryModel;
import com.qxg.ihandsw.view.Library.Impl.LibraryActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Qking on 2016/11/7.
 */
@SuppressLint("ValidFragment")
public class LibraryLoginDialog extends DialogFragment {

    @BindView(R.id.notice)
    TextView notice;
    @BindView(R.id.pwd)
    EditText pwd;
    @BindView(R.id.check_progress)
    ProgressBar checkProgress;
    @BindView(R.id.login2check)
    RelativeLayout login2check;
    @BindView(R.id.check_msg)
    TextView checkMsg;

    private LibraryModel libraryModel;
    private Activity activity;
    private Handler handler;
    private int choseFlag;
    public static final int CHANGE_PWD_ONCLICK = 1;
    public static final int ORDER_SEAT_ONCLICK = 2;
    public static final int RELEASE_SEAT_ONCLICK = 3;

    public LibraryLoginDialog(){
    }

    public static LibraryLoginDialog newInstance(int flag) {
        Bundle args = new Bundle();
        args.putInt("flag",flag);
        LibraryLoginDialog fragment = new LibraryLoginDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.activity = getActivity();
        libraryModel = new LibraryModel(activity);
        handler = new Handler(activity.getMainLooper());
        choseFlag = getArguments().getInt("flag");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.dialog_library_login, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @OnClick(R.id.login2check)
    public void onClick() {
        //点击登录
        String sPwd = pwd.getText().toString();
        if(sPwd.isEmpty()){
            notice.setText("请输入密码！");
            return;
        }

        //开始登录
        //记得设置监听器
        libraryModel.setOnLoginListener(new LibraryModel.OnLoginListener() {
            @Override
            public void onSucess(final boolean flag, final String msg) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        checkProgress.setVisibility(View.GONE);
                        checkMsg.setText("验证");
                        if(flag){
                            //登录成功
                            switch(choseFlag){
                                case CHANGE_PWD_ONCLICK:
                                    ((LibraryActivity)activity).showChangePwdDialog();
                                    break;
                                case ORDER_SEAT_ONCLICK:
                                    ((LibraryActivity)activity).showOrderDialog();
                                    break;
                                case RELEASE_SEAT_ONCLICK:
                                    ((LibraryActivity)activity).showReleaseDialog();
                                    break;
                                default:break;
                            }
                            dismiss();
                        }else{
                            notice.setText(msg);
                        }
                    }
                });
            }
        });
        //开始登录前把loading 给显示出来
        checkMsg.setText("");
        checkProgress.setVisibility(View.VISIBLE);
        libraryModel.login(sPwd);
    }
}
