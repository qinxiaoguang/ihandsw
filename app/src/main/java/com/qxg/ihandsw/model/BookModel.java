package com.qxg.ihandsw.model;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.qxg.ihandsw.Config;
import com.qxg.ihandsw.model.bean.NowBorrow;
import com.qxg.ihandsw.utils.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Qking on 2016/11/9.
 */

public class BookModel {

    private Activity activity;
    private OkHttpClient client;
    private SharedPreferences userSp;
    private SharedPreferences cardSp;
    private SharedPreferences librarySp;
    private SharedPreferences bookSp;
    private HashMap<HttpUrl,List<Cookie>> cookieStore = new HashMap<HttpUrl, List<Cookie>>();
    List<Cookie> cookies;


    public BookModel(Activity activity){
        this.activity = activity;
        client = new OkHttpClient();
        userSp = activity.getSharedPreferences(Config.CARD_LOGIN_NAME, Context.MODE_PRIVATE);
        cardSp = activity.getSharedPreferences(Config.CARD_INFO_FILE_NAME,Context.MODE_PRIVATE);
        librarySp = activity.getSharedPreferences(Config.LIBRARY_INFO_FILE_NAME,Context.MODE_PRIVATE);
        bookSp = activity.getSharedPreferences(Config.BOOK_INFO_FILE_NAME,Context.MODE_PRIVATE);
        client = new OkHttpClient.Builder().cookieJar(new CookieJar() {
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

    String loginCookie;

    public interface OnLoadCheckListener{
        public void onSucess(Bitmap bitmap);
    }

    private OnLoadCheckListener onLoadCheckListener;

    public void setOnLoadCheckListener(OnLoadCheckListener onLoadCheckListener) {
        this.onLoadCheckListener = onLoadCheckListener;
    }

    public void loadCheck(){
        final Request request = new Request.Builder()
                .url(Config.BOOK_LOAD_CHECK_URL)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //加载成功后首先获取cookie
                List<Cookie> tmp = cookieStore.get(request.url());
                loginCookie = tmp.get(tmp.size()-1).toString();
                Log.i("加载的验证码cookie",loginCookie);

                //接着，获取图片
                Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
                if(onLoadCheckListener!=null)onLoadCheckListener.onSucess(bitmap);
            }
        });
    }

    public interface OnLoginListener{
        public void onSucess();
        public void onFailed(String msg);
    }

    private OnLoginListener onLoginListener;

    public void setOnLoginListener(OnLoginListener onLoginListener) {
        this.onLoginListener = onLoginListener;
    }

    public void login(final String pwd, final String check){

        //获取userId
        String id = userSp.getString(Config.USER_ID,"");

        if(loginCookie == null){
            loginCookie = bookSp.getString(Config.BOOK_LOGIN_COOKIE,"");
        }

        //开始login
        RequestBody body = new FormBody.Builder()
                .add("number",id)
                .add("passwd",pwd)
                .add("captcha",check)
                .add("select","cert_no")
                .add("returnUrl","")
                .build();

        Log.i("-->登录信息:",id + "," + pwd + "," + check +"," + loginCookie);

        Request request = new Request.Builder()
                .url(Config.BOOK_LOGIN_URL)
                .addHeader("Cookie",loginCookie)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //取得数据后
                String result = response.body().string();
                Log.i("图书登录返回结果",result.substring(500));

                if(result.contains("注销")){
                    SharedPreferences.Editor editor = bookSp.edit();
                    editor.putString(Config.BOOK_PWD,pwd);
                    editor.putString(Config.BOOK_LOGIN_COOKIE,loginCookie);
                    editor.putString(Config.BOOK_LOGIN_CHECK,check);
                    editor.commit();
                    if(onLoginListener!=null)onLoginListener.onSucess();
                    return;
                }
                if(onLoginListener==null)return;
                if(result.contains("验证码错误")){
                    onLoginListener.onFailed("验证码错误");
                }else if(result.contains("密码错误")){
                    onLoginListener.onFailed("密码错误");
                }else{
                    //登录异常
                    onLoginListener.onFailed("登录异常，请稍后再试");
                }
            }
        });
    }

    public interface OnLoadNowBorrow{
        public void onSucess(List<NowBorrow> borrows);
        public void checkName();
    }

    private OnLoadNowBorrow onLoadNowBorrow;

    public void setOnLoadNowBorrow(OnLoadNowBorrow onLoadNowBorrow) {
        this.onLoadNowBorrow = onLoadNowBorrow;
    }

    public void loadNowBorrow(){
        //开始加载当前借阅的信息
        loginCookie = bookSp.getString(Config.BOOK_LOGIN_COOKIE,"");

        Request request = new Request.Builder()
                .url(Config.BOOK_NOW_BORROW_URL)
                .addHeader("Cookie",loginCookie)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //解析数据
                String result = response.body().string();
                if(result.contains("登录")){
                    //需要核实姓名，重新登录图书馆
                    if(onLoadNowBorrow!=null)onLoadNowBorrow.checkName();
                    return;
                }
                Document doc = Jsoup.parse(result);
                Elements ems = doc.select("tr");
                List<NowBorrow> borrows = new ArrayList<NowBorrow>();
                for(int i=1; i<ems.size()-2;i++){
                    Elements tdEms = ems.get(i).select("td");
                    if(tdEms.size()<8)continue;
                    String id = tdEms.get(0).text();
                    String name = tdEms.get(1).text();
                    String borrowDate = tdEms.get(2).text();
                    String returnDate = tdEms.get(3).text();
                    String address = tdEms.get(5).text();

                    String tmp = tdEms.get(7).select("input").attr("onclick");
                    String  check = tmp.split("'")[3];
                    Log.i("--->借阅信息",id + "," + name + "," + borrowDate + "," + returnDate + "," + address + "," + check);
                    //完毕后，就开始保存吧
                    borrows.add(new NowBorrow(id,name,borrowDate,returnDate,address,check));
                }

                if(onLoadNowBorrow!=null)onLoadNowBorrow.onSucess(borrows);
            }
        });
    }


    public interface OnContinueBorrowListener{
        public void onSucess(boolean flag,String msg);
    }

    private OnContinueBorrowListener onContinueBorrowListener;

    public void setOnContinueBorrowListener(OnContinueBorrowListener onContinueBorrowListener) {
        this.onContinueBorrowListener = onContinueBorrowListener;
    }

    public void continueBorrow(String barCode, String check){

        String captcha = bookSp.getString(Config.BOOK_LOGIN_CHECK,"");
        Long nowTime = System.currentTimeMillis();
        Log.i("当前时间",nowTime+ "");
        Request request = new Request.Builder()
                .url(Config.BOOK_CONTINUE_BORROW_URL + "?bar_code=" + barCode + "&check=" + check + "&captcha=" +captcha + "&time=" + nowTime)
                .addHeader("Cookie",bookSp.getString(Config.BOOK_LOGIN_COOKIE,""))
                .build();
        Log.i("---->将要访问的url",request.url().toString());
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Log.i("续借返回信息",result);
                String msg = Jsoup.parse(result).select("font").first().text();
                if(onContinueBorrowListener==null)return;
                if (msg.contains("续借成功")){
                    onContinueBorrowListener.onSucess(true,msg);
                }else{
                    onContinueBorrowListener.onSucess(false,msg);
                }
            }
        });

    }

    public interface OnChangePwdListener{
        public void onSucess(String msg);
    }

    private OnChangePwdListener onChangePwdListener;

    public void setOnChangePwdListener(OnChangePwdListener onChangePwdListener) {
        this.onChangePwdListener = onChangePwdListener;
    }

    public void changePwd(String oldPwd, String newPwd){
        RequestBody body = new FormBody.Builder()
                .add("old_passwd",oldPwd)
                .add("new_passwd",newPwd)
                .add("chk_passwd",newPwd)
                .add("submit1","确定")
                .build();

        Request request = new Request.Builder()
                .url(Config.BOOK_CHANGE_PWD_URL)
                .addHeader("Cookie",bookSp.getString(Config.BOOK_LOGIN_COOKIE,""))
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String reslut = response.body().string();
                Document doc = Jsoup.parse(reslut);
                String msg = doc.select("strong").get(1).text();
                if(msg.contains("密码修改成功")){
                //将数据清除
                    bookSp.edit().clear().commit();
                }
                Log.i("修改密码返回结果",msg);
                if (onChangePwdListener!=null)onChangePwdListener.onSucess(msg);
            }
        });
    }
}
