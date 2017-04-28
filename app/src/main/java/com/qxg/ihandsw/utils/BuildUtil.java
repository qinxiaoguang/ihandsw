package com.qxg.ihandsw.utils;

import android.os.Build;

/**
 * Created by Qking on 2016/10/11.
 */

public class BuildUtil {

    public static boolean isLargeThanAPI21(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }
    public static boolean isLowerThanAPI19(){
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT;
    }
}
