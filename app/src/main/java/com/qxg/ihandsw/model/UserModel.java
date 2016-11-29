package com.qxg.ihandsw.model;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import com.qxg.ihandsw.utils.Log;

import com.qxg.ihandsw.Config;
import com.umeng.analytics.MobclickAgent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Qking on 2016/11/2.
 */

public class UserModel {

    public interface OnCardLoginListener{
        public void onSucess(Response response);
        public void onStart();
        public void onFaile(String msg);
    }

    public interface OnLoadingCheckListener{
        public void onSucess(Response response);
        public void onStart();
        public void onFaile();
    }

    //对外提供的接口
    private OnCardLoginListener onCardLoginListener;
    private OnLoadingCheckListener onLoadingCheckListener;

    public void setOnCardLoginListener(OnCardLoginListener onCardLoginListener) {
        this.onCardLoginListener = onCardLoginListener;
    }

    public void setOnLoadingCheckListener(OnLoadingCheckListener onLoadingCheckListener) {
        this.onLoadingCheckListener = onLoadingCheckListener;
    }

    private Activity activity;
    private Handler handler;
    private SharedPreferences sp;

    public UserModel(Activity activity){
        this.activity = activity;
        sp = activity.getSharedPreferences(Config.CARD_LOGIN_NAME,Context.MODE_PRIVATE);
        handler = new Handler(activity.getMainLooper());
        okHttpClient = new OkHttpClient.Builder().cookieJar(new CookieJar() {
            @Override
            public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                cookieStore.put(url, cookies);
            }

            @Override
            public List<Cookie> loadForRequest(HttpUrl url) {
                cookies = cookieStore.get(url);
                return cookies != null ? cookies : new ArrayList<Cookie>();
            }
        }).build();
    }

    private HashMap<HttpUrl,List<Cookie>> cookieStore = new HashMap<HttpUrl, List<Cookie>>();
    List<Cookie> cookies;
    private OkHttpClient okHttpClient;

    public void validateCardLogin(String check){
        String user = sp.getString(Config.USER_ID,"");
        String pwd = sp.getString(Config.CARD_PWD,"");
        Boolean isRem = sp.getBoolean(Config.IS_REM,false);

        validateCardLogin(user,pwd,check,isRem);
    }

    /**
     * 使用一卡通查询密码是否能登录
     * @param user 用户名
     * @param pwd 查询密码
     * @param check 验证码
     * @param isRem 是否记住密码
     */
    public void validateCardLogin(final String user, final String pwd, final String check, final boolean isRem){
        if(onCardLoginListener!=null){
            onCardLoginListener.onStart();
        }
        Log.i("--->当前cookie",cookie);
        Log.i("--->当前check",check);
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("signtype","SynSno")
                .add("username",user)
                .add("password",pwd)
                .add("checkcode", check)
                .add("isUsedKeyPad","false")
                .build();

        Headers headers = new Headers.Builder()
                .add("Cookie", cookie)
                .build();

        Request request = new Request.Builder()
                .url(Config.CARD_ADDRESS+"/Account/MiniCheckIn")
                .headers(headers)
                .post(body)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("--------->","请求失败");
                if(onCardLoginListener!=null)
                    onCardLoginListener.onFaile("请求失败，请稍后再试~");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Headers resHeader = response.headers();
                Log.i("----------->返回header",resHeader.toString());
                Log.i("---------->",getSetCookie(resHeader.get("Set-Cookie")));
                String result = response.body().string();
                Log.i("---------->返回结果",result);

                //返回成功，就把SetCookie保存在文件中
                if(result.equals("success|False")){
                    String setCookie = getSetCookie(resHeader.get("Set-Cookie"));

                    Log.i("--->","保存结果");
                    Log.i("-------->setCookie",setCookie);
                    //友盟记录登录id
                    MobclickAgent.onProfileSignIn(user);

                    SharedPreferences sp
                            = activity.getSharedPreferences(Config.CARD_LOGIN_NAME, Context.MODE_APPEND);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString(Config.USER_ID,user);
                    editor.putString(Config.COOKIE,cookie);
                    editor.putString(Config.CHECK_NUM,check);
                    editor.putString(Config.CARD_PWD,pwd);
                    editor.putString(Config.SET_COOKIE,setCookie);
                    editor.putBoolean(Config.IS_LOGIN,true);
                    editor.putBoolean(Config.IS_REM,isRem);
                    editor.commit();
                    getUserName();
                }else{
                    if(onCardLoginListener!=null){
                        onCardLoginListener.onFaile(result);
                    }
                }
            }
        });
    }



    /**
     * 取得返回的SetCookie
     */
    private String getSetCookie(String result){
        String tmp[];
        if(result != null) {
            tmp = result.split(";");
            return tmp[0];
        }
        return "";
    }


    public void getUserName(){
        if(!sp.getBoolean(Config.IS_LOGIN,false)){
            throw new RuntimeException("您没登录，根本就不能获取您的姓名好嘛！！");
        }
        OkHttpClient client = new OkHttpClient();
        String cookie = sp.getString(Config.COOKIE,"");
        String setCookie = sp.getString(Config.SET_COOKIE,"");

        //iPlanetDirectoryPro=U3luU25vXzIwMTQwMDgyMDExOV8yMDE2MTEwMzEwNDQ1NTk2MzM%3d
        //在这就可以用Get方法来获取名字了,主要通过setCookie登录，注意
        Headers headers = new Headers.Builder()
                .add("Cookie",cookie + "; " +setCookie)
                .build();

        Request request = new Request.Builder()
                .url(Config.CARD_ADDRESS)
                .headers(headers)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("---->获取名字失败","失败");
            }

            @Override
            public void onResponse(Call call, final Response response) {
                //成功登录
                //将来cookie,check保存
                onCardLoginListener.onSucess(response);

            }
        });
    }


    /**
     * 退出当前帐号
     */
    public void quit(){
        //退出的话，文件中的内容要删除
        sp.edit().putBoolean(Config.IS_LOGIN,false).commit();
        activity.getSharedPreferences(Config.LIBRARY_INFO_FILE_NAME,Context.MODE_PRIVATE).edit().clear().commit();
        activity.getSharedPreferences(Config.BOOK_INFO_FILE_NAME,Context.MODE_PRIVATE).edit().clear().commit();

        MobclickAgent.onProfileSignOff();
    }

    /**
     *  加载验证码
     */
    private String cookie;
    public void loadCheckImg(){
        //加载前显示加载图标

        final Request request = new Request.Builder().url("http://iecard.wh.sdu.edu.cn/Account/GetCheckCodeImg").build();
        Call call = okHttpClient.newCall(request);
        onLoadingCheckListener.onStart();
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                onLoadingCheckListener.onFaile();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                //将cookie保存;
                List<Cookie> tmp;
                tmp = cookieStore.get(request.url());

                cookie = tmp.get(tmp.size()-1).toString().split(";")[0];
                //成功加载
                onLoadingCheckListener.onSucess(response);
            }
        });
    }

    //判断是否已经记录了登录
    public boolean isLogin(){
        return activity.getSharedPreferences(Config.CARD_LOGIN_NAME,Context.MODE_PRIVATE).getBoolean(Config.IS_LOGIN,false);
    }
}
