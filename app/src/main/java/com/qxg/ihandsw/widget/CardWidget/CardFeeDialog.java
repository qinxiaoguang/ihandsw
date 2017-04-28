package com.qxg.ihandsw.widget.CardWidget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.qxg.ihandsw.R;
import com.qxg.ihandsw.model.CardModel;
import com.qxg.ihandsw.utils.Log;
import com.qxg.ihandsw.widget.ShowCheckDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Response;

import static android.R.attr.width;

/**
 * Created by Qking on 2016/11/4.
 */
@SuppressLint("ValidFragment")
public class CardFeeDialog extends BaseBottomDialog {

    @BindView(R.id.img1)
    ImageView img1;
    @BindView(R.id.img2)
    ImageView img2;
    @BindView(R.id.img3)
    ImageView img3;
    @BindView(R.id.img4)
    ImageView img4;
    @BindView(R.id.img5)
    ImageView img5;
    @BindView(R.id.img6)
    ImageView img6;
    @BindView(R.id.img7)
    ImageView img7;
    @BindView(R.id.img8)
    ImageView img8;
    @BindView(R.id.img9)
    ImageView img9;
    @BindView(R.id.img0)
    ImageView img0;
    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.img_quit)
    ImageView imgQuit;
    @BindView(R.id.pwd_edit)
    EditText pwdEdit;
    @BindView(R.id.trans_fee)
    EditText transFee;
    @BindView(R.id.load_pwd_table_progress)
    ProgressBar loadTableProgress;
    @BindView(R.id.load_pwd_table)
    View loadTableLayout;
    @BindView(R.id.confirm_trans_btn)
    Button confirmBtn;
    @BindView(R.id.confirm_loading)
    ProgressBar confirmLoading;
    @BindView(R.id.err_msg)
    TextView errMsg;
    @BindView(R.id.balance_err_msg)
    TextView balanceErrMsg;
    @BindView(R.id.confirm_balance)
    ImageButton confirmBalance;
    @BindView(R.id.input_balance_layout)
    LinearLayout inputBalanceLayout;
    @BindView(R.id.input_pwd_layout)
    LinearLayout inputPwdLayout;


    public CardFeeDialog(Context context) {
        super(context);
    }


    public void initView() {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_card_fee, null);
        ButterKnife.bind(this, view);
        imgBack.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                queryPwd = "";
                pwdEdit.setText(queryPwd);
                return false;
            }
        });
        //读取 密码表
        cardModel.setOnReadPwdTableListener(new CardModel.OnReadPwdTableListener() {
            @Override
            public void onStart() {
                loadTableProgress.setVisibility(View.VISIBLE);
                loadTableLayout.setVisibility(View.GONE);
            }

            @Override
            public void onSucess(Response response) {
                final Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
                int itemWidth = bitmap.getWidth() / 10;
                int itemHeight = bitmap.getHeight() / 2 - 16;
                int shouldWidth = itemWidth - 18;
                Log.i("-------->", "获取图片的width:" + width);
                final Bitmap b0 = Bitmap.createBitmap(bitmap, 8, 8, shouldWidth, itemHeight);
                final Bitmap b1 = Bitmap.createBitmap(bitmap, itemWidth * 1 + 8, 8, shouldWidth, itemHeight);
                final Bitmap b2 = Bitmap.createBitmap(bitmap, itemWidth * 2 + 8, 8, shouldWidth, itemHeight);
                final Bitmap b3 = Bitmap.createBitmap(bitmap, itemWidth * 3 + 7, 8, shouldWidth, itemHeight);
                final Bitmap b4 = Bitmap.createBitmap(bitmap, itemWidth * 4 + 7, 8, shouldWidth, itemHeight);
                final Bitmap b5 = Bitmap.createBitmap(bitmap, itemWidth * 5 + 7, 8, shouldWidth, itemHeight);
                final Bitmap b6 = Bitmap.createBitmap(bitmap, itemWidth * 6 + 7, 8, shouldWidth, itemHeight);
                final Bitmap b7 = Bitmap.createBitmap(bitmap, itemWidth * 7 + 6, 8, shouldWidth, itemHeight);
                final Bitmap b8 = Bitmap.createBitmap(bitmap, itemWidth * 8 + 6, 8, shouldWidth, itemHeight);
                final Bitmap b9 = Bitmap.createBitmap(bitmap, itemWidth * 9 + 5, 8, shouldWidth, itemHeight);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        img0.setImageBitmap(b0);
                        img1.setImageBitmap(b1);
                        img2.setImageBitmap(b2);
                        img3.setImageBitmap(b3);
                        img4.setImageBitmap(b4);
                        img5.setImageBitmap(b5);
                        img6.setImageBitmap(b6);
                        img7.setImageBitmap(b7);
                        img8.setImageBitmap(b8);
                        img9.setImageBitmap(b9);
                        loadTableProgress.setVisibility(View.GONE);
                        loadTableLayout.setVisibility(View.VISIBLE);

                    }
                });
            }
        });
        cardModel.readPwdTable();
        setContentView(view);
    }


    String queryPwd = "";

    @OnClick({R.id.img1, R.id.img2, R.id.img3, R.id.img4, R.id.img5, R.id.img6, R.id.img7, R.id.img8, R.id.img9, R.id.img0, R.id.img_back, R.id.img_quit})
    public void onClick(View view) {

        int now = -3;
        switch (view.getId()) {
            case R.id.img0:
                now = 0;
                break;
            case R.id.img1:
                now = 1;
                break;
            case R.id.img2:
                now = 2;
                break;
            case R.id.img3:
                now = 3;
                break;
            case R.id.img4:
                now = 4;
                break;
            case R.id.img5:
                now = 5;
                break;
            case R.id.img6:
                now = 6;
                break;
            case R.id.img7:
                now = 7;
                break;
            case R.id.img8:
                now = 8;
                break;
            case R.id.img9:
                now = 9;
                break;
            case R.id.img_back:
                now = -1;
                break;
            case R.id.img_quit:
                now = -2;
                break;
        }


        if (now == -1) {
            //退格
            if (queryPwd.length() == 0) return;
            queryPwd = queryPwd.substring(1, queryPwd.length());
        } else if (now == -2) {
            dismiss();
        } else {
            if (queryPwd.length() == 6) {
                return;
            }
            queryPwd = now + queryPwd;
        }
        //显示
        pwdEdit.setText(queryPwd);
        pwdEdit.setClickable(false);
    }

    @OnClick(R.id.confirm_trans_btn)
    public void confirm() {
        //确定缴费
        confirmBtn.setClickable(false);
        String pwd = pwdEdit.getText().toString();
        String tranNum = transFee.getText().toString();

        if (pwd.isEmpty()) {
            errMsg.setText("信息不得为空！");
            errMsg.setVisibility(View.VISIBLE);
            return;
        }

        if (Integer.parseInt(tranNum) >= 500) {
            errMsg.setText("单笔金额不得超过500！");
            errMsg.setVisibility(View.VISIBLE);
            return;
        }


        if (queryPwd.length() != 6) {
            errMsg.setText("查询密码必须6位");
            errMsg.setVisibility(View.VISIBLE);
            return;
        }
        cardModel.setOnTransCardFeeListener(new CardModel.OnTransCardFeeListener() {
            @Override
            public void onStart() {
                confirmLoading.setVisibility(View.VISIBLE);
                confirmBtn.setText("");
                errMsg.setVisibility(View.GONE);
            }

            @Override
            public void onSucess(String msg) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "转账成功，欢迎下次使用", Toast.LENGTH_LONG).show();
                        dismiss();
                    }
                });
            }

            @Override
            public void onFailed(final String msg) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        confirmBtn.setClickable(true);
                        if (msg.equals("验证码错误")) {

                            ShowCheckDialog showCheckDialog = ShowCheckDialog.newInstance();
                            showCheckDialog.setOnValidateCheckListener(new ShowCheckDialog.OnValidateCheckListener() {
                                @Override
                                public void onSucess() {
                                    dismiss();
                                }
                            });

                            showCheckDialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "");
                            dismiss();
                            return;
                        }
                        confirmLoading.setVisibility(View.GONE);
                        confirmBtn.setText("确认");
                        errMsg.setText(msg);
                        errMsg.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
        cardModel.transCardFee(tranNum, pwd);
    }

    @OnClick(R.id.confirm_balance)
    public void onClick() {
        //确认转账金额是否正确
        balanceErrMsg.setVisibility(View.GONE);
        String tranNum = transFee.getText().toString();
        if(tranNum.isEmpty()){
            balanceErrMsg.setText("信息不得为空");
            balanceErrMsg.setVisibility(View.VISIBLE);
            return;
        }
        if (Integer.parseInt(tranNum) >= 500) {
            balanceErrMsg.setText("单笔金额不得超过500！");
            balanceErrMsg.setVisibility(View.VISIBLE);
            return;
        }

        //以上都通过后，就让该layout消失，让另一个layout
        inputBalanceLayout.setVisibility(View.GONE);
        inputPwdLayout.setVisibility(View.VISIBLE);
        inputPwdLayout.animate().alpha(1).setDuration(500).start();
        inputPwdLayoutFlag = true;
    }
    private boolean inputPwdLayoutFlag = false;

    @Override
    public void onBackPressed() {
        if(inputPwdLayoutFlag){
            inputPwdLayout.setVisibility(View.GONE);
            inputPwdLayout.setAlpha(0);
            inputBalanceLayout.setVisibility(View.VISIBLE);
            inputPwdLayoutFlag = false;
            return;
        }
        super.onBackPressed();
    }
}
