package com.qxg.ihandsw.widget.CardWidget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.qxg.ihandsw.R;
import com.qxg.ihandsw.model.CardModel;
import com.roger.catloadinglibrary.CatLoadingView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Qking on 2016/11/6.
 */
@SuppressLint("ValidFragment")
public class UpLossDialog extends BaseBottomDialog {

    @BindView(R.id.address)
    EditText address;
    @BindView(R.id.phone)
    EditText phone;
    @BindView(R.id.des)
    EditText des;
    @BindView(R.id.confirm)
    Button confirm;
    @BindView(R.id.err_msg)
    TextView errMsg;

    private CatLoadingView loadingView;

    public UpLossDialog(Context context) {
        super(context);
    }

    @Override
    public void initView() {
        super.initView();
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_up_loss, null);
        ButterKnife.bind(this, view);


        setContentView(view);
    }

    @OnClick(R.id.confirm)
    public void onClick() {
        final String sAddress = address.getText().toString();  //丢卡地点
        final String sPhone = phone.getText().toString();        //联系电话
        final String sDes = des.getText().toString();         //描述

        if(sAddress.isEmpty()){
            errMsg.setText("请输入地点");
            errMsg.setVisibility(View.VISIBLE);
            return;
        }
        if(sPhone.length()>11||sPhone.length()<7){
            errMsg.setText("请输入正确的电话号码");
            errMsg.setVisibility(View.VISIBLE);
            return;
        }
        if(sDes.length() > 256){
            errMsg.setText("说明不得超过256个字");
            errMsg.setVisibility(View.VISIBLE);
            return;
        }

        //如果全通过，就开始吧！
        cardModel.setOnUpLossCardInfoListener(new CardModel.OnUpLossCardInfoListener() {
            @Override
            public void onSucess(final String msg) {
                //成功返回数据，将数据打印出来
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        hideLoading();
                        if(msg.contains("该卡号发布丢失卡信息，请查看捡到卡信息")){
                            Toast.makeText(context,"您已挂失，请不要重复提交",Toast.LENGTH_SHORT).show();
                            dismiss();
                        }else{
                            //发布成功
                            Toast.makeText(context,"提交成功，祝您好运~",Toast.LENGTH_SHORT).show();
                            dismiss();
                        }
                    }
                });
            }
        });

        new AlertDialog.Builder(context)
                .setMessage("确认登记?")
                .setCancelable(false)
                .setPositiveButton("是", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showLoading();
                        cardModel.upLossCardInfo(sAddress,sPhone,sDes);
                    }
                })
                .setNegativeButton("否",null)
                .show();
    }
}
