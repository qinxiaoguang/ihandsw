package com.qxg.ihandsw.model;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import com.qxg.ihandsw.utils.Log;

import com.qxg.ihandsw.Config;
import com.qxg.ihandsw.model.bean.OrderFloorInfo;
import com.qxg.ihandsw.model.bean.SeatInfo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

import static java.util.regex.Pattern.compile;

/**
 * Created by Qking on 2016/11/7.
 */

public class LibraryModel {

    private Activity activity;
    private OkHttpClient client;
    private SharedPreferences userSp;
    private SharedPreferences cardSp;
    private SharedPreferences librarySp;

    private OkHttpClient.Builder clientBuilder;

    public LibraryModel(Activity activity){
        this.activity = activity;
        userSp = activity.getSharedPreferences(Config.CARD_LOGIN_NAME, Context.MODE_PRIVATE);
        cardSp = activity.getSharedPreferences(Config.CARD_INFO_FILE_NAME,Context.MODE_PRIVATE);
        librarySp = activity.getSharedPreferences(Config.LIBRARY_INFO_FILE_NAME,Context.MODE_PRIVATE);

        clientBuilder = new OkHttpClient.Builder().cookieJar(new CookieJar() {
            @Override
            public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                cookieStore.put(url, cookies);
            }

            @Override
            public List<Cookie> loadForRequest(HttpUrl url) {
                cookies = cookieStore.get(url);
                return cookies != null ? cookies : new ArrayList<Cookie>();
            }
        });

        client = clientBuilder.build();
    }

    private HashMap<HttpUrl,List<Cookie>> cookieStore = new HashMap<HttpUrl, List<Cookie>>();
    List<Cookie> cookies;

    public interface OnGetSeatInfoListener{
        public void onSucess(Response res);
    }

    private OnGetSeatInfoListener onGetSeatInfoListener;

    public void setOnGetSeatInfoListener(OnGetSeatInfoListener onGetSeatInfoListener) {
        this.onGetSeatInfoListener = onGetSeatInfoListener;
    }

    public void getSeatInfo(){
        final Request request = new Request.Builder()
                .url(Config.GET_SEAT_INFO_ADDRESS)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //获取cookie
                String getImgCookie;
                List<Cookie> tmp;
                tmp = cookieStore.get(request.url());
                getImgCookie = tmp.get(tmp.size()-1).toString().split(";")[0];
                Log.i("----->cookie",getImgCookie);
                //获取到img的信息
                String result = response.body().string();
                Log.i("-------->",result);
                Document doc = Jsoup.parse(result);
                Element element = doc.select("#librarySeatUsedInfo").first();
                String imgSrc = element.attr("src");
                Log.i("---->",imgSrc);
                getImg(getImgCookie,imgSrc);
            }
        });
    }
    public void getImg(String cookie,String imgSrc){
        Request request = new Request.Builder()
                .url(Config.LIBRARY_ADDRESS + imgSrc)
                .addHeader("Cookie",cookie)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(onGetSeatInfoListener!=null)onGetSeatInfoListener.onSucess(response);
            }
        });
    }

    private String loginCookie;
    public void login(final String pwd){
        //获取userid;
        final String userId = userSp.getString(Config.USER_ID,"");
        //首先进入主页，获取一些Value值
        final Request request = new Request.Builder()
                .url(Config.LIBRARY_LOGIN_DEFAULT_ADDRESS)
                .build();
        client.newCall(request)
                .enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Document doc = Jsoup.parse(result);
                String viewState = doc.select("#__VIEWSTATE").first().val();
                String eventValidation = doc.select("#__EVENTVALIDATION").first().val();
                //获取cookie
                List<Cookie> tmps = cookieStore.get(request.url());
                loginCookie = tmps.get(tmps.size()-1).toString();
                Log.i("---->获取的loginCookie",loginCookie);
                //获取后就开始登录吧
                startLogin(userId,pwd,viewState,eventValidation);
            }
        });
    }
    public interface OnLoginListener{
        public void onSucess(boolean flag,String msg);
    }
    private OnLoginListener onLoginListener;

    public void setOnLoginListener(OnLoginListener onLoginListener) {
        this.onLoginListener = onLoginListener;
    }

    private void startLogin(String userId, final String pwd, String viewState, String eventValidation){
        RequestBody body = new FormBody.Builder()
                .add("__VIEWSTATE",viewState)
                .add("__EVENTVALIDATION",eventValidation)
                .add("txtUserName",userId)
                .add("txtPassword",pwd)
                .add("cmdOK.x","17")
                .add("cmdOk.y","6")
                .build();
        Request request = new Request.Builder()
                .url(Config.LIBRARY_LOGIN_DEFAULT_ADDRESS)
                .addHeader("Cookie",loginCookie)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Log.i("--->返回的登录结果",result);

                if(result.contains("基于")){
                    //登录成功，开始保存
                    SharedPreferences.Editor editor = librarySp.edit();
                    editor.putString(Config.LIBRARY_PWD,pwd);
                    editor.putString(Config.LIBRARY_LOGIN_COOKIE,loginCookie);
                    editor.commit();
                    if(onLoginListener!=null){
                        onLoginListener.onSucess(true,"验证成功,请重新操作~");
                        return;
                    }
                }

                if(onLoginListener==null)return;
                if(result.contains("用户或密码错误")){
                    onLoginListener.onSucess(false,"密码错误，请重新输入~");
                }else{
                    //未知错误
                    onLoginListener.onSucess(false,"未知错误，请稍后再试~");
                }

            }
        });
    }

    public interface OnGetOrderRecordListener{
        public void onGetSucess(String floor,String seat);
        public void onGetFailed(String msg);
        public void noRecord();
    }

    private OnGetOrderRecordListener onGetOrderRecordListener;

    public void setOnGetOrderRecord(OnGetOrderRecordListener onGetOrderRecordListener) {
        this.onGetOrderRecordListener = onGetOrderRecordListener;
    }

    //查询预约记录
    public void getOrderRecord(){
        Headers headers = new Headers.Builder()
                .add("Cookie",getLoginCookie())
                .add("Host","202.194.46.22")
                .add("Referer","http://202.194.46.22/Florms/FormSYS.aspx")
                .build();

        Request request = new Request.Builder()
                .url(Config.ORDER_RECORD_ADDRESS)
                .headers(headers)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Document doc = Jsoup.parse(result);
                    //如何获得预约记录呢？
                    Log.i("---->",result);
                    //当然是看是否包含代码：
                    if(result.contains("确定要取消该预约记录吗")){
                        getSeatInfo(result);
                }else{
                    //说明未预约
                    if(onGetOrderRecordListener!=null){
                        onGetOrderRecordListener.noRecord();
                    }
                }

            }
        });
    }


    private void getSeatInfo(String result){

        //获取页码
        //说明已预约
        //开始解析预约的信息
        //"240111", "三楼", "303", "等待确认"
        String floor = "";
        String seatNum = "";
        String reg ="\"\\d{6}\".\".{2,6}\".\"\\d{3}\".\"等待确认\"";
        Pattern p = compile(reg);
        Matcher m =p.matcher(result);
        String info = "";
        if(m.find()){
            Log.i("---->匹配内容",m.group(0));
            String tmp = m.group(0);
            //"240150","三楼","304","等待确认"
            String infos[] = tmp.split(",");
            floor = infos[1];
            floor = floor.substring(1,floor.length()-1);
            seatNum = infos[2];
            seatNum = seatNum.substring(1,seatNum.length()-1);
            Log.i("预约座位是:",floor + "," + seatNum);
            if(onGetOrderRecordListener != null)onGetOrderRecordListener.onGetSucess(floor,seatNum);
        }else{
            //读取信息出错
            if(onGetOrderRecordListener != null)onGetOrderRecordListener.onGetFailed("读取信息出错");
        }
    }

    public interface OnGetFloorInfoListener{
        public void onSucess(List<OrderFloorInfo> floorInfos);
    }

    private OnGetFloorInfoListener onGetFloorInfoListener;

    public void setOnGetFloorInfoListener(OnGetFloorInfoListener onGetFloorInfoListener) {
        this.onGetFloorInfoListener = onGetFloorInfoListener;
    }

    List<OrderFloorInfo> floorInfos;
    //获取座位信息
    public void getFloorInfo(){
        floorInfos = new ArrayList<>();
        Request request = new Request.Builder()
                .url(Config.LOAD_FLOOR_INFO_ADDRESS)
                .addHeader("Cookie",getLoginCookie())
                .addHeader("Referer","http://202.194.46.22/Florms/FormSYS.aspx")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //解析数据
                String date = "";
                String res = response.body().string();
                Log.i("--->获取Floor数据的结果是:",res);
                String reg ="\\[\"\\d{6}\".\".{2,6}\".\"图书馆\".+\\].";
                Pattern p = compile(reg);
                Matcher m = p.matcher(res);
                //获取date
                String dateReg = "date=\\d{18}";
                Matcher dateM = Pattern.compile(dateReg).matcher(res);
                if(dateM.find()){
                    date = dateM.group(0).substring(5);
                    Log.i("---date",date);
                }
                if(m.find()){
                    String tmp = m.group(0); //总数据
                    Log.i("---->匹配数据",tmp);
                    String tmps[] = tmp.split("],"); //前12个有用,也可能是11个，也可能是2个.....
                    int size = tmps.length;
                    for(int i=0;i<size;i++){
                        String splitTmps[] = tmps[i].split(",");
                        Log.i("--->长度",splitTmps.length + "");
                        if(splitTmps.length != 9)continue;

                        String roomId = splitTmps[0].substring(2,8);//roomId;
                        String floor = splitTmps[1].substring(1,splitTmps[1].length()-1);
                        String all = splitTmps[3].substring(1,splitTmps[3].length()-1);
                        String rest = splitTmps[4].substring(1,splitTmps[4].length()-1);
                        String param = splitTmps[5].split("\\?")[1];
                        param = param.substring(0,param.length() - 5);
                        Log.i("params -->" ,param);
                        floorInfos.add(new OrderFloorInfo(roomId,floor,date,all,rest,param));
                        Log.i("获取的item数据：",roomId + "," + floor + "," + all + "," + rest);
                    }
                }
                //解析完成后
                if(onGetFloorInfoListener!=null)onGetFloorInfoListener.onSucess(floorInfos);
            }
        });
    }

    public interface OnChangePwdListener{
        public void onSucess(boolean ret,String msg);
    }

    private OnChangePwdListener onChangePwdListener;

    public void setOnChangePwdListener(OnChangePwdListener onChangePwdListener) {
        this.onChangePwdListener = onChangePwdListener;
    }


    public void changePwd(String oldPwd, final String newPwd){

        String id = userSp.getString(Config.USER_ID,"");
        RequestBody body = new FormBody.Builder()
                .add("__EVENTTARGET","ctl03$ctl00$btnSubmit")
                .add("__EVENTARGUMENT","")
                .add("__VIEWSTATE","/wEPDwUJMzg3NjI1MTcyZBgBBR5fX0NvbnRyb2xzUmVxdWlyZVBvc3RCYWNrS2V5X18WBgULU2ltcGxlRm9ybTEFG1NpbXBsZUZvcm0xJHR4dFBhc3N3b3JkX29sZAUXU2ltcGxlRm9ybTEkdHh0UGFzc3dvcmQFGVNpbXBsZUZvcm0xJHR4dFBhc3N3b3JkX2QFBWN0bDAzBRVjdGwwMyRjdGwwMCRidG5TdWJtaXSm+rbSP13Fa/ePJRcO4XoOx5cizV0OVIg02MhRZ3udLg==")
                .add("SimpleForm1$txtPassword_old",oldPwd)
                .add("SimpleForm1$txtPassword",newPwd)
                .add("SimpleForm1$txtPassword_d",newPwd)
                .add("X_CHANGED","true")
                .add("X_TARGET","ctl03_ctl00_btnSubmit")
                .add("SimpleForm1_Collapsed","false")
                .add("ctl03_Collapsed","false")
                .add("X_STATE","e30=")
                .add("X_AJAX","true")
                .build();

        Request request = new Request.Builder()
                .url(Config.LIBRARY_CHANGE_PWD + "?id=" + id)
                .post(body)
                .addHeader("Cookie",getLoginCookie())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Log.i("--->密码更改返回结果",result);

                if(result.contains("密码更新成功")){
                    //保存密码
                    activity.getSharedPreferences(Config.LIBRARY_INFO_FILE_NAME,Context.MODE_APPEND)
                            .edit()
                            .putString(Config.LIBRARY_PWD,newPwd)
                            .commit();

                    if(onChangePwdListener!=null)onChangePwdListener.onSucess(true,"密码更新成功~");
                }else if(onChangePwdListener!=null){
                    onChangePwdListener.onSucess(false,"密码更新失败");
                }
            }
        });
    }



    public interface OnSeeSeatInfo{
        public void onFailed();
        public void onSucess(List<SeatInfo> seatInfos);
    }

    private OnSeeSeatInfo onSeeSeatInfo;

    public void setOnSeeSeatInfo(OnSeeSeatInfo onSeeSeatInfo) {
        this.onSeeSeatInfo = onSeeSeatInfo;
    }

    public interface OnReleaseSeatListener{
        public void onFailed();
        public void onNoSeat();//没有选座的时候的回调
        public void onSucess(String nowState,String viewState,String validation,String cookie); // 有选座的时候的回调
    }

    private OnReleaseSeatListener onReleaseSeatListener;

    public void setOnReleaseSeatListener(OnReleaseSeatListener onReleaseSeatListener) {
        this.onReleaseSeatListener = onReleaseSeatListener;
    }

    public final static int SEE_SEAT_INFO = 1;
    public final static int RELEASE = 2;

    public void seeSeatInfoOrRealease(final int flag){
        final Request request = new Request.Builder()
                .url(Config.JUNEBERRY_LOGIN_URL)
                .build();
        final OkHttpClient client = clientBuilder.connectTimeout(3, TimeUnit.SECONDS).readTimeout(3,TimeUnit.SECONDS).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //如果3s失败，就用另一个登录
                Log.i("---->","第一层失败");
                switch(flag){
                    case SEE_SEAT_INFO:
                        if(onSeeSeatInfo!=null)onSeeSeatInfo.onFailed();
                        break;
                    case RELEASE:
                        if(onReleaseSeatListener!=null)onReleaseSeatListener.onFailed();
                        break;
                    default:break;
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                //取得数据
                Document doc = Jsoup.parse(result);

                String viewState = doc.select("#__VIEWSTATE").first().val();
                String valigation = doc.select("#__EVENTVALIDATION").first().val();
                String schoolCode = "";
                Elements ems = doc.select("option");
                int size = ems.size();
                for(int i=0;i<size;i++){
                    Element element = ems.get(i);
                    if(element.text().contains("山东大学(威海)")){
                        schoolCode = element.val();
                        break;
                    }
                }

                //取得cookie
                List<Cookie> tmps = cookieStore.get(request.url());
                String cookie = tmps.get(tmps.size()-1).toString();

                //打印一下
                Log.i("JUN获取的数据",viewState + "," + valigation + "," + schoolCode + "," + cookie);
                //成功获取后，开始登录

                loginJun(client,cookie,viewState,valigation,schoolCode,flag);
                response.close();
            }
        });
    }

    private void loginJun(final OkHttpClient client, final String cookie, String viewState, String valigation, String schoolCode, final int flag){

        //使用本人帐号登录
        RequestBody body = new FormBody.Builder()
                .add("__VIEWSTATE",viewState)
                .add("__EVENTVALIDATION",valigation)
                .add("subCmd","Login")
                .add("txt_LoginID","201400820119")
                .add("txt_Password","201400820119")
                .add("selSchool",schoolCode)
                .build();

        Request request = new Request.Builder()
                .url(Config.JUN_TRUE_LOGIN_URL)
                .addHeader("Cookie",cookie.split(";")[0])
                .addHeader("Referer","http://yuyue.juneberry.cn/")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("---->","第二层失败");
                switch(flag){
                    case SEE_SEAT_INFO:
                        if(onSeeSeatInfo!=null)onSeeSeatInfo.onFailed();
                        break;
                    case RELEASE:
                        if(onReleaseSeatListener!=null)onReleaseSeatListener.onFailed();
                        break;
                    default:break;
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //如果成功,打印结果
                String result = response.body().string();
                Log.i("登录返回结果-->",result);
                if(result.contains("系统出现异常")){
                    switch(flag){
                        case SEE_SEAT_INFO:
                            if(onSeeSeatInfo!=null)onSeeSeatInfo.onFailed();
                            break;
                        case RELEASE:
                            if(onReleaseSeatListener!=null)onReleaseSeatListener.onFailed();
                            break;
                        default:break;
                    }
                }else if(result.contains("注销")){
                    //登录成功
                    //登录成功后，就拿着cookie去获取楼层信息吧
                    switch(flag){
                        case SEE_SEAT_INFO:
                            getFloorInfo(client,cookie);
                            break;
                        case RELEASE:
                            //登录成功后，读取数据吧！
                            //通过result获取到当前座位的信息
                            if(result.contains("你好,你当前没有座位")){
                                //说明当前没有选座，则让view显示没有选座
                                Log.i("当前没有选座","------------->当前没有选座");
                                if(onReleaseSeatListener!=null)onReleaseSeatListener.onNoSeat();
                            }else{
                                String nowState = Jsoup.parse(result).select("#SpanNowState").first().text();
                                //获取__VIEWSTATE ,__EVENTVALIDATION,并要传递当前cookie
                                Document doc = Jsoup.parse(result);
                                String viewState = doc.select("#__VIEWSTATE").first().val();
                                String validation = doc.select("#__EVENTVALIDATION").first().val();
                                if(onReleaseSeatListener!=null)onReleaseSeatListener.onSucess(nowState,viewState,validation,cookie);
                            }
                            break;
                        default:break;
                    }
                }else {
                    switch(flag){
                        case SEE_SEAT_INFO:
                            if(onSeeSeatInfo!=null)onSeeSeatInfo.onFailed();
                            break;
                        case RELEASE:
                            if(onReleaseSeatListener!=null)onReleaseSeatListener.onFailed();
                            break;
                        default:break;
                    }
                }
                response.close();
            }
        });
    }


    private void getFloorInfo(OkHttpClient client,String cookie){
        Request request = new Request.Builder()
                .url(Config.JUN_SEAT_INFO_URL)
                .addHeader("Cookie",cookie.split(";")[0])
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("---->","第三层失败");
                if(onSeeSeatInfo!=null)onSeeSeatInfo.onFailed();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();

                Log.i("座位信息--->",result);

                if(result.contains("系统出现异常")) {
                     if(onSeeSeatInfo!=null)onSeeSeatInfo.onFailed();
                }else{
                    //获取成功
                    //开始解析座位情况
                    List<SeatInfo> seatInfos = new ArrayList<SeatInfo>();
                    Document doc = Jsoup.parse(result);
                    Elements ems = doc.select("li[data-theme]");
                    int size = ems.size();
                    for(int i=0;i<size;i++){
                        Element element = ems.get(i);
                        Elements liEms = element.select("li");
                        int liEmsSize = liEms.size();
                        String floor = liEms.get(0).text().split(":")[0]; //楼层
                        String  all = liEms.get(1).text().split("：")[1]; //总座位
                        String nowSit = liEms.get(2).text().split("：")[1]; // 在座
                        String rest = liEms.get(4).text().split("：")[1]; //空闲
                        seatInfos.add(new SeatInfo(floor,all,rest,nowSit));
                    }
                    //数据个搞完后，就回调
                    if(onSeeSeatInfo!=null)onSeeSeatInfo.onSucess(seatInfos);
                }
                response.close();
            }
        });
    }


    public interface GoReleaseSeatListener{
        public void onFailed(); //释放失败
        public void onSucess();
    }

    private GoReleaseSeatListener goReleaseSeatListener;

    public void setGoReleaseSeatListener(GoReleaseSeatListener goReleaseSeatListener) {
        this.goReleaseSeatListener = goReleaseSeatListener;
    }

    public void releaseSeat(String viewState, String validation, String cookie){
        Log.i("准备取消信息,",viewState+"---" + validation + "---" + cookie);
        OkHttpClient client = clientBuilder.connectTimeout(10,TimeUnit.SECONDS).readTimeout(10,TimeUnit.SECONDS).build();
        RequestBody body = new FormBody.Builder()
                .add("__EVENTTARGET","")
                .add("__EVENTARGUMENT","")
                .add("__VIEWSTATE",viewState)
                .add("__EVENTVALIDATION",validation)
                .add("subCmd","leave")
                .build();

        Request request = new Request.Builder()
                .url(Config.RELEASE_SEAT_URL)
                .addHeader("Cookie",cookie)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //失败，失败读取
                if(goReleaseSeatListener!=null)goReleaseSeatListener.onFailed();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Log.i("--------->释放结果",result);
                //只要返回了结果，就是成功了呗
                if(goReleaseSeatListener!=null)goReleaseSeatListener.onSucess();
            }
        });

    }

    private String getLoginCookie(){
        return librarySp.getString(Config.LIBRARY_LOGIN_COOKIE,"");
    }
}
