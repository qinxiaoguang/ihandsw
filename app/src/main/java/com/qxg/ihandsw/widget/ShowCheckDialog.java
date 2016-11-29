package com.qxg.ihandsw.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.qxg.ihandsw.R;
import com.qxg.ihandsw.model.UserModel;
import com.qxg.ihandsw.utils.Log;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Response;

/**
 * Created by Qking on 2016/11/4.
 */
@SuppressLint("ValidFragment")
public class ShowCheckDialog extends DialogFragment {
    @BindView(R.id.load_check)
    ProgressBar loadCheck;
    @BindView(R.id.checkImg)
    ImageView checkImg;
    @BindView(R.id.check_input)
    EditText checkInput;
    @BindView(R.id.login2check)
    RelativeLayout login2check;
    @BindView(R.id.check_progress)
    ProgressBar checkProgress;
    @BindView(R.id.check_btn_text)
    TextView checkBtnText;

    Activity activity;

    UserModel userModel;
    Handler handler;
    @BindView(R.id.notice)
    TextView notice;

    private String noticeText;

    public static ShowCheckDialog newInstance() {

        Bundle args = new Bundle();

        ShowCheckDialog fragment = new ShowCheckDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.activity = getActivity();
        //用于显示验证码
        userModel = new UserModel(activity);
        handler = new Handler(activity.getMainLooper());
    }

    public interface OnValidateCheckListener{
        public void onSucess(); //验证成功
    }

    OnValidateCheckListener onValidateCheckListener;

    public void setOnValidateCheckListener(OnValidateCheckListener onValidateCheckListener) {
        this.onValidateCheckListener = onValidateCheckListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //无标题
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        View view = inflater.inflate(R.layout.dialog_check, container, false);
        ButterKnife.bind(this, view);
        userModel.setOnLoadingCheckListener(new UserModel.OnLoadingCheckListener() {
            @Override
            public void onSucess(Response response) {
                final Bitmap bmp = BitmapFactory.decodeStream(response.body().byteStream());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        checkImg.setImageBitmap(bmp);
                        loadCheck.setVisibility(View.GONE);
                        checkImg.setVisibility(View.VISIBLE);
                    }
                });
                response.close();
            }

            @Override
            public void onStart() {
                loadCheck.setVisibility(View.VISIBLE);
                checkImg.setVisibility(View.GONE);
            }

            @Override
            public void onFaile() {

            }
        });
        userModel.setOnCardLoginListener(new UserModel.OnCardLoginListener() {
            @Override
            public void onSucess(Response response) {
                Log.i("------>","成功");
                if(onValidateCheckListener != null)onValidateCheckListener.onSucess();
            }

            @Override
            public void onStart() {
                checkProgress.setVisibility(View.VISIBLE);
                checkBtnText.setText("");
            }

            @Override
            public void onFaile(final String msg) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        checkProgress.setVisibility(View.GONE);
                        checkBtnText.setText("验证");
                        //验证失败
                        notice.setText(msg);
                    }
                });

            }
        });
        loadCheck();
        return view;
    }

    private void loadCheck() {
        userModel.loadCheckImg();
    }

    public void setNoticeText(String msg){
        noticeText = msg;
        notice.setText(msg);
    }

    @OnClick({R.id.checkImg, R.id.login2check})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.checkImg:
                loadCheck();
                break;
            case R.id.login2check:
                //验证
                noticeText = "验证中...";
                notice.setText("");
                String checkNum = checkInput.getText().toString();
                if(checkNum.isEmpty()) {
                    notice.setText("请输入数据");
                    return;
                }
                //开始验证
                userModel.validateCardLogin(checkNum);
                break;
        }
    }
}
