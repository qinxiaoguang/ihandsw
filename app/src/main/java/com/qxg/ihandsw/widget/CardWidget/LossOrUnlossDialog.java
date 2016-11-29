package com.qxg.ihandsw.widget.CardWidget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.qxg.ihandsw.Config;
import com.qxg.ihandsw.R;
import com.qxg.ihandsw.model.CardModel;
import com.qxg.ihandsw.utils.Log;
import com.qxg.ihandsw.widget.ShowCheckDialog;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Response;

import static android.R.attr.width;

/**
 * Created by Qking on 2016/11/6.
 */
@SuppressLint("ValidFragment")
public class LossOrUnlossDialog extends BaseBottomDialog {
    @BindView(R.id.load_pwd_table_progress)
    ProgressBar loadPwdTableProgress;
    @BindView(R.id.img0)
    ImageView img0;
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
    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.img9)
    ImageView img9;
    @BindView(R.id.img_quit)
    ImageView imgQuit;
    @BindView(R.id.load_pwd_table)
    LinearLayout loadPwdTable;
    @BindView(R.id.pwd_edit)
    EditText pwdEdit;
    @BindView(R.id.jiegua)
    Button jiegua;
    @BindView(R.id.guashi)
    Button guashi;
    @BindView(R.id.err_msg)
    TextView errMsg;
    @BindView(R.id.card_state)
    TextView cardState;

    public LossOrUnlossDialog(Context context) {
        super(context);
    }

    @Override
    public void initView() {
        super.initView();
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_loss_or_unloss, null);
        ButterKnife.bind(this, view);
        //查看该卡的状态
        if(getCardState()){
            //如果正常，将解挂按钮隐藏，并显示提示
            cardState.setText("您的校园卡状态正常");
            jiegua.setVisibility(View.GONE);
            guashi.setVisibility(View.VISIBLE);
        }else{
            cardState.setText("您的校园卡已挂失");
            jiegua.setVisibility(View.VISIBLE);
            guashi.setVisibility(View.GONE);
        }

        //读取 密码表
        cardModel.setOnReadPwdTableListener(new CardModel.OnReadPwdTableListener() {
            @Override
            public void onStart() {
                loadPwdTableProgress.setVisibility(View.VISIBLE);
                loadPwdTable.setVisibility(View.GONE);
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
                        loadPwdTableProgress.setVisibility(View.GONE);
                        loadPwdTable.setVisibility(View.VISIBLE);
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

    @OnClick({R.id.jiegua, R.id.guashi})
    public void btnClick(View view) {
        errMsg.setVisibility(View.GONE);
        if(queryPwd.isEmpty()){
            errMsg.setText("请输入查询密码");
            errMsg.setVisibility(View.VISIBLE);
            return;
        }

        switch (view.getId()) {
            case R.id.jiegua:
                //解挂
                new AlertDialog.Builder(context)
                        .setTitle("注意")
                        .setMessage("确定要解挂吗？")
                        .setCancelable(false)
                        .setPositiveButton("确定", new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                releaseCard();
                            }
                        }).setNegativeButton("否",null)
                        .show();
                break;
            case R.id.guashi:
                //挂失
                new AlertDialog.Builder(context)
                        .setTitle("注意")
                        .setMessage("确定要挂失吗？")
                        .setCancelable(false)
                        .setPositiveButton("确定", new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                setCardLoss();
                            }
                        }).setNegativeButton("否",null)
                        .show();
                break;
        }
    }

    //解挂操作
    private void releaseCard(){
        showLoading();
        cardModel.setOnReleaseCardListener(new CardModel.OnReleaseCardListener() {
            @Override
            public void onSucess(final String result) {
                Log.i("--->解挂返回结果",result);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        hideLoading();
                        try {
                            JSONObject jsonRes = new JSONObject(result);
                            Boolean ret = jsonRes.getBoolean("ret");
                            String msg = jsonRes.getString("msg");
                            Log.i("--->JSON",ret + msg);
                            //成功
                            if(ret) updateCardState("正常卡");
                            if(msg.contains("验证码")){
                                ShowCheckDialog showCheckDialog = ShowCheckDialog.newInstance();
                                showCheckDialog.setOnValidateCheckListener(new ShowCheckDialog.OnValidateCheckListener() {
                                    @Override
                                    public void onSucess() {
                                        dismiss();
                                    }
                                });
                                showCheckDialog.show(((AppCompatActivity)context).getSupportFragmentManager(),"");
                                dismiss();
                                return;
                            }
                            if(msg.contains("查询密码错误")){
                                Toast.makeText(context,"查询密码错误，请重新输入~",Toast.LENGTH_SHORT).show();
                                queryPwd = "";
                                pwdEdit.setText("");
                                return;
                            }
                            Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
                            dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });
        cardModel.releaseCard(queryPwd);
    }

    //挂失操作
    private void setCardLoss(){
        showLoading();
        cardModel.setOnSetCardLossListener(new CardModel.OnSetCardLossListener() {
            @Override
            public void onSucess(final String result) {
                Log.i("--->挂失返回结果",result);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        hideLoading();
                        try {
                            JSONObject jsonRes = new JSONObject(result);
                            Boolean ret = jsonRes.getBoolean("ret");
                            String msg = jsonRes.getString("msg");
                            Log.i("--->JSON",ret + msg);
                            //成功
                            if (ret)updateCardState("已挂失");
                            if(msg.contains("验证码")){
                                ShowCheckDialog showCheckDialog = ShowCheckDialog.newInstance();
                                showCheckDialog.setOnValidateCheckListener(new ShowCheckDialog.OnValidateCheckListener() {
                                    @Override
                                    public void onSucess() {
                                        dismiss();
                                    }
                                });
                                showCheckDialog.show(((AppCompatActivity)context).getSupportFragmentManager(),"");
                                dismiss();
                                return;
                            }
                            if(msg.contains("查询密码错误")){
                                Toast.makeText(context,"查询密码错误，请重新输入~",Toast.LENGTH_SHORT).show();
                                queryPwd = "";
                                pwdEdit.setText("");
                                return;
                            }
                            Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
                            dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        });
        cardModel.setCardLoss(queryPwd);
    }

    private void  updateCardState(String state){
        context.getSharedPreferences(Config.CARD_INFO_FILE_NAME,Context.MODE_PRIVATE)
                .edit()
                .putString(Config.CARD_INFO_STATE,state)
                .commit();
    }
    private boolean getCardState(){
        //正常返回true,挂失返回false
        String state =
                context.getSharedPreferences(Config.CARD_INFO_FILE_NAME, Context.MODE_PRIVATE).getString(Config.CARD_INFO_STATE,"");
        if(state.contains("正常"))return true;
        else return false;
    }
}
