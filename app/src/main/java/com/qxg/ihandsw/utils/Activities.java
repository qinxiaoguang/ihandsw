package com.qxg.ihandsw.utils;

import android.app.Activity;

import java.util.ArrayList;

/**
 * Created by Qking on 2016/10/12.
 */

public class Activities {
    public static ArrayList<Activity> activities = new ArrayList<>();

    public static void add(Activity activity){
        activities.add(activity);
    }

    public static void remove(Activity activity){
        activities.remove(activity);
    }

    public static void removeAll(){
        activities.clear();
    }

    public static void finish(){
        for(Activity activity:activities){
            activity.finish();
        }
    }
}
