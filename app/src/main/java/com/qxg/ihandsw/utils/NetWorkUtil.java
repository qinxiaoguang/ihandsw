package com.qxg.ihandsw.utils;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * Created by Qking on 2016/7/10.
 * 判断是否有网络的工具类
 */
public class NetWorkUtil {
    public static int NO_CONNECT = -1;  //没有网络
    public static int NETWORK_CONNECT = 0; //数据连接
    public static int WIFI_CONNECT = 1;   //wifi连接

    //注意参数中的view的目的是为了postRunnable。也就是所可以在主线程中Toast消息
    public static boolean checkNetWork(final Context context)
    {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);  //获取网络通信服务
        if(cm == null) {}
        else {
            NetworkInfo info = cm.getActiveNetworkInfo();
            if(info==null || !cm.getActiveNetworkInfo().isAvailable())
            {
                //没有网络
                Toast.makeText(context,"请检查您的网络~",Toast.LENGTH_LONG).show();
            }
            else return false;
        }
        return false;
    }
}
