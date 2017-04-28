package com.qxg.ihandsw.widget.CardWidget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSpinner;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.qxg.ihandsw.Config;
import com.qxg.ihandsw.R;
import com.qxg.ihandsw.model.CardModel;
import com.qxg.ihandsw.utils.Log;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Qking on 2016/11/5.
 */
@SuppressLint("ValidFragment")
public class ElectricFeeDialog extends BaseBottomDialog {

    @BindView(R.id.build_num)
    AppCompatSpinner buildNum;
    @BindView(R.id.confirm_btn)
    Button confirmBtn;
    @BindView(R.id.room_num)
    EditText roomNum;
    @BindView(R.id.err_msg)
    TextView errMsg;
    @BindView(R.id.balance)
    EditText balance;
    @BindView(R.id.pwd)
    EditText pwd;


    public ElectricFeeDialog(Context context) {
        super(context);
    }

    public void initView() {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_electric_fee, null);
        ButterKnife.bind(this, view);

        setContentView(view);
    }

    @OnClick(R.id.confirm_btn)
    public void onClick() {
        getElectricInfo();
    }

    private void getElectricInfo() {
        errMsg.setVisibility(View.GONE);

        final int build = (int) (buildNum.getSelectedItemId() + 1);  //楼号
        final String room = roomNum.getText().toString();           //房间号
        final String tranNum = balance.getText().toString();  //转账金额
        String queryPwd = pwd.getText().toString();//查询密码
        //判断查询密码是否正确


        if (room.isEmpty()) {
            errMsg.setText("房间号不得为空");
            errMsg.setVisibility(View.VISIBLE);
            return;
        }
        if (tranNum.isEmpty()) {
            errMsg.setText("请输入转账金额");
            errMsg.setVisibility(View.VISIBLE);
            return;
        }

        if(!confirmPwd(queryPwd)){
            errMsg.setText("查询密码错误");
            errMsg.setVisibility(View.VISIBLE);
            return;
        }

        showLoading();

        cardModel.setOnGetElectricCnt(new CardModel.OnGetElectricCnt() {
            @Override
            public void onSucess(final String result) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        hideLoading();
                        if (result.contains("房间号不存在")) {
                            //房间号不存在，就显示Alert信息告知
                            new AlertDialog.Builder(context)
                                    .setMessage("房间号不存在，请填写正确房间号")
                                    .setCancelable(false)
                                    .setPositiveButton("知道了", null)
                                    .show();
                        } else {
                            new AlertDialog.Builder(context)
                                    .setMessage("房间余额为:" + result + ",确定转账吗?")
                                    .setCancelable(false)
                                    .setPositiveButton("嗯", new OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            //开始转账
                                            Calendar c = Calendar.getInstance();
                                            int hour = c.get(Calendar.HOUR_OF_DAY);
                                            Log.i("当前小时",""+hour);
                                            if(hour>=22||hour <=6){
                                                errMsg.setText("请在6点~22点时间段内缴费~");
                                                errMsg.setVisibility(View.VISIBLE);
                                                return;
                                            }

                                            cardModel.setOnTransElectricFeeListener(new CardModel.OnTransElectricFeeListener() {
                                                @Override
                                                public void onStart() {
                                                    showLoading();
                                                }

                                                @Override
                                                public void onSucess() {
                                                    handler.post(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            hideLoading();
                                                            Toast.makeText(getContext(), "转账成功，欢迎下次使用~", Toast.LENGTH_SHORT).show();
                                                            dismiss();
                                                        }
                                                    });
                                                }

                                                @Override
                                                public void onFailed(final String msg) {
                                                    handler.post(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                                                            hideLoading();
                                                            dismiss();
                                                        }
                                                    });
                                                }
                                            });
                                            cardModel.transElectricFee(build, room, tranNum);
                                        }
                                    })
                                    .setNegativeButton("不", null)
                                    .show();
                        }
                    }
                });
            }
        });
        cardModel.getElectricCnt(build, room);
    }

    private boolean confirmPwd(String pwd){
        return context.getSharedPreferences(Config.CARD_LOGIN_NAME,Context.MODE_PRIVATE).getString(Config.CARD_PWD,"").equals(pwd);
    }
}
