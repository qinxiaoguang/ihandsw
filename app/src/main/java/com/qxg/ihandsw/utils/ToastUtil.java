package com.qxg.ihandsw.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.qxg.ihandsw.R;

import javax.inject.Inject;

/**
 * Created by Qking on 2016/10/10.
 * 对Toast进行封装,默认使用showMsg显示long时间长度的
 * 而带有short关键字的都是显示short时间长度的
 */

public class ToastUtil {

    Context context;

    @Inject
    public ToastUtil(Context context){
        this.context = context;
    }


    public void showMsg(String msg){
        showMsg(msg, Gravity.BOTTOM);
    }

    public void showShortMsg(String msg){
        showShortMsg(msg, Gravity.BOTTOM);
    }

    public void showMsg(String msg, int flag)
    {
        showMsg(msg,flag,0,0);
    }

    public void showShortMsg(String msg, int flag)
    {
        showShortMsg(msg,flag,0,0);
    }

    /**
     * flag 为Gravity.Top|Gravity.Left 等这种形式，x为向右移动，y为向下移动
     */
    public void showMsg(String msg, int flag, int x, int y)
    {
        View toastView = LayoutInflater.from(context).inflate(R.layout.toast_layout,null);
        TextView tv = (TextView) toastView.findViewById(R.id.TextViewInfo);
        Toast toast = new Toast(context);
        tv.setText(msg);
        toast.setView(toastView);
        toast.setGravity(Gravity.BOTTOM,0,0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }

    public void showShortMsg(String msg, int flag, int x, int y)
    {
        View toastView = LayoutInflater.from(context).inflate(R.layout.toast_layout,null);
        TextView tv = (TextView) toastView.findViewById(R.id.TextViewInfo);
        Toast toast = new Toast(context);
        tv.setText(msg);
        toast.setGravity(Gravity.BOTTOM,0,0);
        toast.setView(toastView);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * 默认显示方式，即不添加任何杂质
     */
    public void show(String msg)
    {
        Toast toast = Toast.makeText(context,msg, Toast.LENGTH_LONG);
        toast.show();
    }

    public void showShort(String msg){
        Toast toast = Toast.makeText(context,msg, Toast.LENGTH_SHORT);
        toast.show();
    }

}
