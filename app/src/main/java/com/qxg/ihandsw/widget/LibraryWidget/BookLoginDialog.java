package com.qxg.ihandsw.widget.LibraryWidget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qxg.ihandsw.Config;
import com.qxg.ihandsw.R;
import com.qxg.ihandsw.model.BookModel;
import com.qxg.ihandsw.view.Library.Impl.LibraryActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Qking on 2016/11/9.
 */

@SuppressLint("ValidFragment")
public class BookLoginDialog extends DialogFragment {


    @BindView(R.id.notice)
    TextView notice;
    @BindView(R.id.pwd)
    EditText pwd;
    @BindView(R.id.load_check)
    ProgressBar loadCheck;
    @BindView(R.id.checkImg)
    ImageView checkImg;
    @BindView(R.id.check)
    EditText check;
    @BindView(R.id.check_progress)
    ProgressBar checkProgress;
    @BindView(R.id.check_msg)
    TextView checkMsg;
    @BindView(R.id.login2check)
    RelativeLayout login2check;
    private Activity activity;
    private BookModel bookModel;
    private Handler handler;
    private int flag;
    public static final int CHANGE_PWD_ONCLICK = 1;
    public static final int NOW_BORROW_PWD_ONCLICK = 2;


    public BookLoginDialog() {
    }

    public static BookLoginDialog newInstance(int flag) {
        Bundle args = new Bundle();
        args.putInt("flag",flag);
        BookLoginDialog fragment = new BookLoginDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.activity = getActivity();
        bookModel = new BookModel(activity);
        handler = new Handler(activity.getMainLooper());
        flag = getArguments().getInt("flag");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.dialog_book_login, container, false);
        ButterKnife.bind(this, view);

        //初始化就把之前登录的密码放在密码框中
        String sPwd =
                activity.getSharedPreferences(Config.BOOK_INFO_FILE_NAME, Context.MODE_PRIVATE).getString(Config.BOOK_PWD, "");
        pwd.setText(sPwd);

        //首先加载验证码
        bookModel.setOnLoadCheckListener(new BookModel.OnLoadCheckListener() {
            @Override
            public void onSucess(final Bitmap bitmap) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //加载成功
                        loadCheck.setVisibility(View.GONE);
                        checkImg.setImageBitmap(bitmap);
                        check.setText("");
                    }
                });
            }
        });
        //加载前
        loadCheck.setVisibility(View.VISIBLE);
        bookModel.loadCheck();
        return view;
    }

    @OnClick(R.id.checkImg)
    public void refreshCheck(){
        loadCheck.setVisibility(View.VISIBLE);
        bookModel.loadCheck();
    }

    @OnClick(R.id.login2check)
    public void onClick() {
        //判断密码或验证码是否为空
        String sPwd = pwd.getText().toString();
        String sCheck = check.getText().toString();
        if(sPwd.isEmpty()||sCheck.isEmpty()){
            notice.setText("请输入数据！");
            return;
        }

        //否则就开始登录吧
        bookModel.setOnLoginListener(new BookModel.OnLoginListener() {
            @Override
            public void onSucess() {
                //登录成功
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        switch (flag){
                            case CHANGE_PWD_ONCLICK:
                                ((LibraryActivity)activity).showChangePwdDialog();
                                break;
                            case NOW_BORROW_PWD_ONCLICK:
                                ((LibraryActivity)activity).showNowBorrowDialog();
                                break;
                        }

                         dismiss();
                    }
                });
            }

            @Override
            public void onFailed(final String msg) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        refreshCheck();
                        notice.setText(msg);
                        checkProgress.setVisibility(View.GONE);
                        checkMsg.setText("验证");
                    }
                });
            }
        });
        //登录前把loading显示
        checkProgress.setVisibility(View.VISIBLE);
        checkMsg.setText("");
        bookModel.login(sPwd,sCheck);
    }

}
