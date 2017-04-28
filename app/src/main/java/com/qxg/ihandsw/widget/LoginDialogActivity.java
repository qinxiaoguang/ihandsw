package com.qxg.ihandsw.widget;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.qxg.ihandsw.Config;
import com.qxg.ihandsw.R;
import com.qxg.ihandsw.model.UserModel;
import com.qxg.ihandsw.utils.Log;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Response;

import static android.text.InputType.TYPE_CLASS_NUMBER;
import static android.text.InputType.TYPE_CLASS_TEXT;
import static android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD;

public class LoginDialogActivity extends AppCompatActivity {


    public static int HAS_LOGIN = 1;

    @BindView(R.id.dismiss)
    ImageView dismiss;
    @BindView(R.id.userid)
    EditText userid;
    @BindView(R.id.user_layout)
    LinearLayout userLayout;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.pwd_layout)
    LinearLayout pwdLayout;
    @BindView(R.id.info)
    TextView info;
    @BindView(R.id.login_btn)
    Button loginBtn;
    @BindView(R.id.checkImg)
    ImageView checkImg;
    @BindView(R.id.check)
    EditText check;
    @BindView(R.id.rememberPwd)
    CheckBox rememPwd;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.see_pwd)
    ImageView seePwd;
    @BindView(R.id.check_img_loading)
    ProgressBar checkImgLoading;

    private UserModel userModel;
    private Handler handler;
    private String userId; //学号
    private String name;  //姓名

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_dialog);
        ButterKnife.bind(this);

        userModel = new UserModel(this);
        handler = new Handler();
        //加载用户
        loadUser();
        //加载验证码
        loadingCheck();
        setFinishOnTouchOutside(false);
    }

    private void loadUser() {
        SharedPreferences sp = getSharedPreferences(Config.CARD_LOGIN_NAME, Context.MODE_PRIVATE);
        String id = sp.getString(Config.USER_ID, "");
        String pwd = "";
        if (sp.getBoolean(Config.IS_REM, false)) {
            pwd = sp.getString(Config.CARD_PWD, "");
            rememPwd.setChecked(true);
        }
        userid.setText(id);
        password.setText(pwd);
    }

    private boolean isPwdSee = false;

    @OnClick({R.id.dismiss, R.id.user_layout, R.id.pwd_layout, R.id.login_btn, R.id.checkImg, R.id.see_pwd})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dismiss:
                finish();
                break;
            case R.id.user_layout:
                //让userid获取焦点
                userid.requestFocus();
                userid.setSelection(userid.getText().toString().length());
                info.setVisibility(View.GONE);
                break;
            case R.id.pwd_layout:
                password.requestFocus();
                password.setSelection(password.getText().toString().length());
                info.setVisibility(View.GONE);
                break;
            case R.id.checkImg:
                checkImg.setVisibility(View.GONE);
                checkImgLoading.setVisibility(View.VISIBLE);
                userModel.loadCheckImg();
                break;
            case R.id.login_btn:
                String user = userid.getText().toString();
                String pwd = password.getText().toString();
                String checkNum = check.getText().toString();
                Boolean remFlag = rememPwd.isChecked();
                if (user.isEmpty() || pwd.isEmpty() || checkNum.isEmpty()) {
                    info.setText("数据不能为空！");
                    info.setVisibility(View.VISIBLE);
                } else {
                    userId = user;
                    validateLogin(user, pwd, checkNum, remFlag);
                }
                break;
            case R.id.see_pwd:
                if (isPwdSee) {
                    seePwd.setImageResource(R.drawable.see_pwd);
                    password.setInputType(TYPE_CLASS_NUMBER | TYPE_NUMBER_VARIATION_PASSWORD);
                    password.setSelection(password.getText().toString().length());
                    isPwdSee = false;
                } else {
                    isPwdSee = true;
                    seePwd.setImageResource(R.drawable.has_see_pwd);
                    password.setInputType(TYPE_CLASS_TEXT);
                    password.setSelection(password.getText().toString().length());
                }
                break;
        }
    }

    private void validateLogin(String user, String pwd, String checkNum, boolean isRem) {
        userModel.setOnCardLoginListener(new UserModel.OnCardLoginListener() {
            @Override
            public void onSucess(Response response) {
                //成功
                String result = null;
                try {
                    result = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(result!=null){
                    String tmp[] = result.split("你好，");
                    if(tmp.length <2){
                        name = "未知";
                    }else{
                        name = tmp[1].split("（<")[0];
                    }
                }
                //name = result.split("你好，")[1].split("（<")[0];
                Log.i("---->您的姓名", name);
                Log.i("---->返回结果", result);

                //保存name
                SharedPreferences sp
                        = getSharedPreferences(Config.CARD_LOGIN_NAME, Context.MODE_APPEND);
                sp.edit().putString(Config.NAME, name).commit();
                //返回

                setResult(HAS_LOGIN);
                finish();
            }

            @Override
            public void onStart() {
                loginBtn.setText("");
                progressBar.setVisibility(View.VISIBLE);
                loginBtn.setClickable(false);
            }

            @Override
            public void onFaile(final String msg) {
                //请求失败
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        info.setText(msg);
                        info.setVisibility(View.VISIBLE);
                        //将点击按钮回归
                        loginBtn.setText("登录");
                        loginBtn.setClickable(true);
                        progressBar.setVisibility(View.GONE);
                    }
                });

            }
        });
        userModel.validateCardLogin(user, pwd, checkNum, isRem);
    }

    private void loadingCheck() {
        checkImg.setVisibility(View.GONE);
        checkImgLoading.setVisibility(View.VISIBLE);
        userModel.setOnLoadingCheckListener(new UserModel.OnLoadingCheckListener() {
            @Override
            public void onSucess(Response response) {
                final Bitmap bmp = BitmapFactory.decodeStream(response.body().byteStream());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        checkImg.setImageBitmap(bmp);
                        checkImg.setVisibility(View.VISIBLE);
                        checkImgLoading.setVisibility(View.GONE);
                    }
                });
                response.close();
            }

            @Override
            public void onStart() {

            }

            @Override
            public void onFaile() {

            }
        });
        userModel.loadCheckImg();
    }
}
