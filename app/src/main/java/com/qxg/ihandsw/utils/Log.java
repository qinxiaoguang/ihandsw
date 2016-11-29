package com.qxg.ihandsw.utils;

import com.qxg.ihandsw.BuildConfig;

/**
 * Created by Qking on 2016/11/17.
 */

public class Log {
    public static void i(String tag,String msg){
        if(BuildConfig.DEBUG){
            android.util.Log.i(tag,msg);
        }
    }

    public static void d(String tag,String msg){
        if(BuildConfig.DEBUG){
            android.util.Log.d(tag,msg);
        }
    }
}
