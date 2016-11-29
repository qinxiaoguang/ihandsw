package com.qxg.ihandsw.model;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import com.qxg.ihandsw.utils.Log;

import com.qxg.ihandsw.Config;
import com.qxg.ihandsw.model.bean.LossCard;
import com.qxg.ihandsw.retrofit.service.LossCardService;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;

/**
 * Created by Qking on 2016/11/3.
 */

public class CardModel {

    private static final int PWOER_CONFIRM = 1;
    private static final int NET_CONFIRM = 2;
    private Activity activity;
    private SharedPreferences cardSp;
    private SharedPreferences userSp;
    private OkHttpClient client;

    public CardModel(Activity activity){
        this.activity = activity;
        cardSp = activity.getSharedPreferences(Config.CARD_INFO_FILE_NAME,Context.MODE_PRIVATE);
        userSp = activity.getSharedPreferences(Config.CARD_LOGIN_NAME,Context.MODE_PRIVATE);
        client = new OkHttpClient();
    }

    //用来查询一卡通的信息
    public interface OnGetCardUserInfo{
        public void onStart();
        public void onSucess(String result);
        public void onFaile();
    }

    public interface OnReadPwdTableListener{
        public void onSucess(Response response);
        public void onStart();
    }

    //缴费
    public interface OnTransCardFeeListener{
        public void onStart();
        public void onSucess(String msg);
        public void onFailed(String msg);
    }


    public interface OnGetNetFeeListener{
        public void onStart();
        public void onSucess(String balance);
    }

    public interface OnTransNetFeeListener{
        public void onStart();
        public void onSucess();
        public void onFailed(String msg);
    }

    private OnGetCardUserInfo onGetCardUserInfo;

    public void setOnGetCardUserInfo(OnGetCardUserInfo onGetCardUserInfo) {
        this.onGetCardUserInfo = onGetCardUserInfo;
    }

    private OnReadPwdTableListener onReadPwdTableListener;

    public void setOnReadPwdTableListener(OnReadPwdTableListener onReadPwdTableListener) {
        this.onReadPwdTableListener = onReadPwdTableListener;
    }

    private OnTransCardFeeListener onTransCardFeeListener;

    public void setOnTransCardFeeListener(OnTransCardFeeListener onTransCardFeeListener) {
        this.onTransCardFeeListener = onTransCardFeeListener;
    }

    private OnGetNetFeeListener onGetNetFeeListener;

    public void setOnGetNetFeeListener(OnGetNetFeeListener onGetNetFeeListener) {
        this.onGetNetFeeListener = onGetNetFeeListener;
    }

    private OnTransNetFeeListener onTransNetFeeListener;

    public void setOnTransNetFeeListener(OnTransNetFeeListener onTransNetFeeListener) {
        this.onTransNetFeeListener = onTransNetFeeListener;
    }


    public void getCardUserInfo(){
        RequestBody body = new FormBody.Builder()
                .add("needHeader","false")
                .build();


        Request request = new Request.Builder()
                .url(Config.CARD_INFO_ADDRESS)
                .post(body)
                .addHeader("Cookie",getNeedCookie())
                .build();

        if(onGetCardUserInfo!=null)onGetCardUserInfo.onStart();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //返回成功的数据，需要进行判断，如果返回的结果包含location，则表示setCookie失效，需要重新验证验证码
                String result = response.body().string();
                if(onGetCardUserInfo!=null)
                    onGetCardUserInfo.onSucess(result);
            }
        });
    }


    public void readPwdTable(){

        Request request = new Request.Builder()
                .url(Config.CARD_PWD_TABLE_ADDRESS)
                .addHeader("Cookie",getNeedCookie())
                .build();

        if(onReadPwdTableListener!=null)onReadPwdTableListener.onStart();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //成功
                if(onReadPwdTableListener != null)onReadPwdTableListener.onSucess(response);
            }
        });
    }



    public void transCardFee(String transNum, String queryNum){
        Log.i("--------->查询密码",queryNum);
        String check = userSp.getString(Config.CHECK_NUM,"");

        RequestBody body = new FormBody.Builder()
                .add("password",queryNum)
                .add("checkCode",check)
                .add("amt",transNum+".00")
                .add("fcard","bcard")
                .add("tocard","card")
                .add("bankno","")
                .add("bankpwd","")
                .build();


        Request request = new Request.Builder()
                .url(Config.CARD_FEE_ADDRESS)
                .post(body)
                .addHeader("Cookie",getNeedCookie())
                .build();

        if(onTransCardFeeListener!=null){
            onTransCardFeeListener.onStart();
        }
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                JSONObject jsonRes = null;
                Boolean ret = null;
                String msg = null;
                try {
                    jsonRes = new JSONObject(result);
                    ret = jsonRes.getBoolean("ret");
                    msg = jsonRes.getString("msg");
                    if(onTransCardFeeListener == null)return;
                    if(ret){
                        //成功
                        onTransCardFeeListener.onSucess(msg);
                    }else{
                        onTransCardFeeListener.onFailed(msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.i("---------------转账返回结果",result);
                Log.i("------------>JSON格式",ret + msg);
            }
        });
    }


    public void getNetFee(){

        RequestBody body = new FormBody.Builder()
                .add("needHeader","false")
                .build();

        Request request = new Request.Builder()
                .url(Config.GET_NET_FEE)
                .post(body)
                .addHeader("Cookie",getNeedCookie())
                .build();
        if(onGetNetFeeListener!=null)onGetNetFeeListener.onStart();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //对返回的结果进行解析
                String reslut = response.body().string();
                Document doc = Jsoup.parse(reslut);
                Element element = doc.select("strong").first();
                Log.i("---->",element.text());
                String tmps[] = element.text().split(":");
                String balance = "";
                if(tmps.length <= 1){
                    balance = "未知";
                }else{
                    balance = tmps[1];
                }
                if(onGetNetFeeListener !=null)onGetNetFeeListener.onSucess(balance);
            }
        });

    }


    public void transNetFee(final String num){
        //num表示充多少钱
        String account = userSp.getString(Config.USER_ID,null);
        Log.i("--->",account);
        RequestBody body = new FormBody.Builder()
                .add("PayTypeCodes","H3c")
                .add("radioAmount","3")
                .add("otherAmount","")
                .add("chkReadPaper","on")
                .add("xiaoquname","")
                .add("txtAmount",num)
                .add("account",account)
                .build();

        final Request request = new Request.Builder()
                .url(Config.NET_FEE_DOPAY_ADRESS)
                .post(body)
                .addHeader("Cookie",getNeedCookie())
                .build();

        if(onTransNetFeeListener!=null)onTransNetFeeListener.onStart();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("--->","失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String reslut = response.body().string();
                Document doc = Jsoup.parse(reslut);
                //doc 里面获取input，所有值都在input里面
                Elements ems = doc.select("input");

                confirmPay(NET_CONFIRM,ems,num);
            }
        });
    }

    private HashMap<HttpUrl,List<Cookie>> cookieStore = new HashMap<HttpUrl, List<Cookie>>();
    List<Cookie> cookies;
    private String confirmNetFeeCookie;
    //这个操作是确认支付，一旦执行成功，就会成功支付
    private void confirmPay(final int which, Elements ems, final String num){


        //设置cookieJar是为了获取cookie，因为重置界面会有新的cookie生成
        final OkHttpClient okHttpClient = getUnsafeOkHttpClient().cookieJar(new CookieJar() {
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

        final HashMap<String,String> hash = new HashMap<String,String>();
        FormBody.Builder builder = new FormBody.Builder();
        for(int i=0;i<ems.size()-1;i++){
            Element e = ems.get(i);
            hash.put(e.attr("name"),e.val());
            builder.add(e.attr("name"),e.val());
        }
        RequestBody body = builder.build();


        final Request request = new Request.Builder()
                .url(Config.NET_FEE_DOPAY_CONFIRM)
                .post(body)
                .addHeader("Cookie",getNeedCookie())
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //成功后，需要获取cookie
                List<Cookie> tmp;
                tmp = cookieStore.get(request.url());
                confirmNetFeeCookie = null;
                for(int i=0;i<tmp.size();i++){
                    String now = tmp.get(i).toString();
                    if(now.contains("JSESSIONID"))
                    {
                        confirmNetFeeCookie = now.split(";")[0];
                        break;
                    }
                }

                //开始新的一轮的访问
                forwardPayTool(which,okHttpClient,hash.get("trade_jnl"),num,confirmNetFeeCookie + "; " + getSetCookie());
            }
        });
    }

    private void forwardPayTool(final int which, final OkHttpClient client, final String jnl, String totalAmt, final String cookie){
        RequestBody body = new FormBody.Builder()
                .add("tranType","CardBank")
                .add("jnl",jnl)
                .add("app_id","NetFee")
                .add("rePay","")
                .add("totalAmt",totalAmt)
                .add("tradeType","pay")
                .add("isOpen","false")
                .add("inputPwdTime","0")
                .build();


        final Request request = new Request.Builder()
                .url(Config.FORWARD_PAY_TOOL)
                .post(body)
                .addHeader("Cookie",cookie)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("---------->","啊哦，失败了");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i("------------>",response.body().string());
                //成功后，进行下一步，下一步就是post eCardPay
                eCardPay(which,client,jnl,cookie);
            }
        });
    }

    private void eCardPay(final int which, OkHttpClient client, String jnl, String cookie){
        String pwd = userSp.getString(Config.CARD_PWD,"");
        Log.i("密码:",pwd);
        RequestBody body = new FormBody.Builder()
                .add("pwd",pwd)
                .add("jnl",jnl)
                .add("app_id","NetFee")
                .add("rePay","")
                .add("tranType","CardBank")
                .add("needBankPwd","false")
                .add("newPwd","")
                .build();

        Log.i("----->",jnl);

        Log.i("---->ecard",cookie);

        final Request request = new Request.Builder()
                .url(Config.E_CARD_PAY)
                .post(body)
                .addHeader("Cookie",cookie)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Log.i("转账结果",result);
                //结果包含付款金额就对了！！
                if(result.contains("付款金额")){
                    if(which == NET_CONFIRM && onTransNetFeeListener!=null){
                        onTransNetFeeListener.onSucess();
                    }else if(which == PWOER_CONFIRM && onTransElectricFeeListener!=null){
                        //待写
                        onTransElectricFeeListener.onSucess();
                    }
                }else if(onTransNetFeeListener!=null&& which==NET_CONFIRM){
                    onTransNetFeeListener.onFailed("转账失败，请稍后再试");
                }else if(which == PWOER_CONFIRM && onTransElectricFeeListener!=null){
                    //待写
                    onTransElectricFeeListener.onFailed("转账失败，请稍后再试");
                }
            }
        });
    }

    //获取忽略证书的OkHttpClient
    private static OkHttpClient.Builder getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final javax.net.ssl.SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            return builder;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



    public interface OnGetElectricCnt{
        public void onSucess(String result);
    }

    private OnGetElectricCnt onGetElectricCnt;

    public void setOnGetElectricCnt(OnGetElectricCnt onGetElectricCnt) {
        this.onGetElectricCnt = onGetElectricCnt;
    }

    public void getElectricCnt(int buildNum, String roomNum){
        RequestBody body = new FormBody.Builder()
                .add("payTypeCode","Luwei")
                .addEncoded("xiaoqu","主校区++++++++++++++++++++++++")
                .addEncoded("buildno",buildNum + "++++++++")
                .add("roomno",roomNum)
                .build();

        Log.i("---->",""+buildNum+","+roomNum);

        final Request request = new Request.Builder()
                .url(Config.GET_ELECTRIC_CNT)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                if(result.contains("该房间号不存在") && onGetElectricCnt!=null){
                    onGetElectricCnt.onSucess("房间号不存在");
                }else if(onGetElectricCnt!=null){
                    onGetElectricCnt.onSucess(result + " 度");
                }
            }
        });
    }

    public interface OnTransElectricFeeListener{
        public void onStart();
        public void onSucess();
        public void onFailed(String msg);
    }

    private OnTransElectricFeeListener onTransElectricFeeListener;

    public void setOnTransElectricFeeListener(OnTransElectricFeeListener onTransElectricFeeListener) {
        this.onTransElectricFeeListener = onTransElectricFeeListener;
    }

    public void transElectricFee(int buildNum, String roomNum, final String feeAccount){
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("payTypeCodes","Luwei")
                .addEncoded("xiaoqu","主校区++++++++++++++++++++++++")
                .addEncoded("buildno",buildNum + "++++++++")
                .add("roomno",roomNum)
                .add("txtPowerFeeAmount",feeAccount)
                .add("showBuild","True")
                .add("buildName",buildNum+"")
                .add("chkReadPowerFeePaper","on")
                .add("xiaoquname","")
                .build();

        Request request = new Request.Builder()
                .url(Config.POWER_FEE_DOPAY)
                .post(body)
                .addHeader("Cookie",getNeedCookie())
                .build();
        if(onTransElectricFeeListener != null)onTransElectricFeeListener.onStart();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String reslut = response.body().string();
                Document doc = Jsoup.parse(reslut);
                //doc 里面获取input，所有值都在input里面
                Elements ems = doc.select("input");

                confirmPay(PWOER_CONFIRM,ems,feeAccount);
            }
        });
    }

    private int lossInfoCnt = 0;
    private int pages = 0;
    private List<LossCard> lossCards;
    private List<String> times;

    public interface OnGetLossCardInfo{
        public void onSucess(List<LossCard> lossCards);
    }

    private OnGetLossCardInfo onGetLossCardInfo;

    public void setOnGetLossCardInfo(OnGetLossCardInfo onGetLossCardInfo) {
        this.onGetLossCardInfo = onGetLossCardInfo;
    }

    public void getLossCardInfo(){
        lossCards = new ArrayList<LossCard>();
        times = new ArrayList<String>();
        //首先获取总数量
        final Retrofit retrofit = getCookieRetrofit();


        Request request = new Request.Builder()
                .url(Config.GET_LOSS_CARD_INFO)
                .addHeader("Cookie",getNeedCookie())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //返回数据后，获取条数
                Document doc = Jsoup.parse(response.body().string());
                Element element = doc.select("#lblLostCount").first();
                lossInfoCnt = Integer.parseInt(element.text());
                pages = lossInfoCnt/10 + 1;

                Log.i("lossInfoCnt ",lossInfoCnt + "." + pages);
                //接着就开始取得数据,首先获取时间信息
                if(lossInfoCnt>0){
                    getLossCardTimeInfo(retrofit,1);
                }
            }
        });
    }


    private void getLossCardTimeInfo(final Retrofit retrofit, final int page){
        LossCardService service = retrofit.create(LossCardService.class);
        retrofit2.Call<ResponseBody> retrofitCall = service.getLossCardTime(page);
        retrofitCall.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                //成功返回数据后
                try {
                    String result = response.body().string();
                    //开始保存数据
                    Document doc = Jsoup.parse(result);
                    Elements ems = doc.select("td.widthover");
                    //然后将数据保存在times中
                    Log.i("ems大小",ems.size()+"");
                    for(int i=0;i<ems.size();i++){
                        Log.i("发布时间",ems.get(i).text());
                        times.add(ems.get(i).text());
                    }

                    //接着下一个数据
                    Log.i("page,pages",page + ":" +pages);
                    if(page == pages){
                        //所有数据读完，可以读取info了
                        for(int i=0;i<times.size();i++){
                            Log.i("---->times的时间",times.get(i));
                        }
                        getLossCardItemInfo(retrofit,lossInfoCnt);
                    }else{
                        //否则接着下一条数据读
                        getLossCardTimeInfo(retrofit,page+1);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void getLossCardItemInfo(final Retrofit retrofit, final int which){
        LossCardService service = retrofit.create(LossCardService.class);
        retrofit2.Call<ResponseBody> retrofitCall = service.getLossCardInfo(which);
        retrofitCall.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                //成功返回数据后
                try {
                    String result = response.body().string();
                    //Log.i("------->",result);
                    //开始保存数据
                    Document doc = Jsoup.parse(result);
                    Elements ems = doc.select("p");
                    List<String> infos = new ArrayList<String>();
                    int state = 0;  //卡的招领状态
                    for(int i=0;i<ems.size()-1;i++){
                        String tmp = ems.get(i).text();
                        //Log.i("--->",tmp);
                        if(tmp.equals("该卡已招领!")){
                            state = 1;
                            continue;
                        }
                        String tmps[] = tmp.split("：");
                        if(tmps.length == 1){
                            infos.add("");
                        }else{
                            //Log.i("------>",tmps[1]);
                            infos.add(tmps[1]);
                        }
                    }
                    //然后将未被招领的数据保存在lossCards中
                    if(state!=1)
                        lossCards.add(new LossCard(infos.get(0),infos.get(1),infos.get(2),infos.get(3),infos.get(4),infos.get(5),times.get(lossInfoCnt-which),which));
                    //接着下一个数据
                    if(which == 1){
                        //所有数据读完，可以显示了
                        if(onGetLossCardInfo!=null)onGetLossCardInfo.onSucess(lossCards);
                    }else{
                        //否则接着下一条数据读
                        getLossCardItemInfo(retrofit,which-1);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    public interface OnUpLossCardInfoListener{
        public void onSucess(String msg);
    }
    private OnUpLossCardInfoListener onUpLossCardInfoListener;

    public void setOnUpLossCardInfoListener(OnUpLossCardInfoListener onUpLossCardInfoListener) {
        this.onUpLossCardInfoListener = onUpLossCardInfoListener;
    }

    //上传丢卡信息
    public void upLossCardInfo(String address,String phone,String des){
        //首先获取自己的信息

        String name = cardSp.getString(Config.CARD_INFO_NAME,"");
        String sid = cardSp.getString(Config.CARD_INFO_SID,"");
        String id = cardSp.getString(Config.CARD_INFO_ID,"");
        //获取后，就可以进行提交了
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("CardNo",id)
                .add("Name",name)
                .add("Sno",sid)
                .add("Address",address)
                .add("Phone",phone)
                .add("Note",des)
                .add("Status","1")
                .build();

        Request request = new Request.Builder()
                .url(Config.UP_LOSS)
                .post(body)
                .addHeader("Cookie",getNeedCookie())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(onUpLossCardInfoListener!=null)onUpLossCardInfoListener.onSucess(response.body().string());
            }
        });
    }


    //后期再修改该代码
    private String getNeedCookie(){
        return userSp.getString(Config.COOKIE,"") + "; " + userSp.getString(Config.SET_COOKIE,"");
    }

    //取得自带cookie的retrofit
    private Retrofit getCookieRetrofit(){
        OkHttpClient retrofitClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request().newBuilder()
                                .addHeader("Cookie", getNeedCookie())
                                .build();
                        return chain.proceed(request);
                    }
                }).build();
         return new Retrofit.Builder()
                .baseUrl(Config.CARD_ADDRESS)
                .client(retrofitClient)
                .build();
    }

    public interface OnGetBackCardListener{
        public void onSucess(String msg);
    }
    OnGetBackCardListener onGetBackCardListener;

    public void setOnGetBackCardListener(OnGetBackCardListener onGetBackCardListener) {
        this.onGetBackCardListener = onGetBackCardListener;
    }

    //招领
    public void getBackCard(String id, String name, String sid, String lossAdress, String phone, String des, int cnt){
        RequestBody body = new FormBody.Builder()
                .add("CardNo",id)
                .add("Name",name)
                .add("Sno",sid)
                .add("Address",lossAdress)
                .add("Phone",phone)
                .add("Note",des)
                .add("Status","1")
                .build();

        Request request = new Request.Builder()
                .url(Config.GET_BACK_CARD + "/" + cnt)
                .post(body)
                .addHeader("Cookie",getNeedCookie())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Log.i("------>",response.body().string());
                if(onGetBackCardListener!=null)onGetBackCardListener.onSucess(result);
            }
        });
    }


    public interface OnReleaseCardListener{
        public void onSucess(String reslut);
    }

    private  OnReleaseCardListener onReleaseCardListener;

    public void setOnReleaseCardListener(OnReleaseCardListener onReleaseCardListener) {
        this.onReleaseCardListener = onReleaseCardListener;
    }

    //解挂
    public void releaseCard(String pwd){

        String cardId = cardSp.getString(Config.CARD_INFO_ID,"");
        String check = userSp.getString(Config.CHECK_NUM,"");

        if(cardId.length() == 5)cardId = "6" + cardId;

        RequestBody body = new FormBody.Builder()
                .add("checkCode",check)
                .addEncoded("cardno",cardId+"+")
                .add("password",pwd)
                .build();

        Request request = new Request.Builder()
                .url(Config.JIEGUA_CARD)
                .post(body)
                .addHeader("Cookie",getNeedCookie())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                if(onReleaseCardListener!=null)onReleaseCardListener.onSucess(result);
            }
        });

    }

    public interface OnSetCardLossListener{
        public void onSucess(String result);
    }

    private OnSetCardLossListener onSetCardLossListener;

    public void setOnSetCardLossListener(OnSetCardLossListener onSetCardLossListener) {
        this.onSetCardLossListener = onSetCardLossListener;
    }

    //挂失
    public void setCardLoss(String pwd){
        String check = userSp.getString(Config.CHECK_NUM,"");
        RequestBody body = new FormBody.Builder()
                .add("checkCode",check)
                .add("password",pwd)
                .build();

        Request request = new Request.Builder()
                .url(Config.GUASHI_CARD)
                .post(body)
                .addHeader("Cookie",getNeedCookie())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                if(onSetCardLossListener!=null)onSetCardLossListener.onSucess(result);
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
        String check = userSp.getString(Config.CHECK_NUM,"");
        RequestBody body = new FormBody.Builder()
                .add("OldPassword",oldPwd)
                .add("NewPassword",newPwd)
                .add("ConfirmPassword",newPwd)
                .add("ChgCheckCode",check)
                .build();

        Request request = new Request.Builder()
                .url(Config.CHANGE_CARD_PWD)
                .post(body)
                .addHeader("Cookie",getNeedCookie())
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                try {
                    JSONObject jsonRes = new JSONObject(result);
                    Boolean ret = jsonRes.getBoolean("ret");
                    String msg = jsonRes.getString("msg");
                    if(ret){
                        //更改密码成功，把密码保存
                        activity.getSharedPreferences(Config.CARD_LOGIN_NAME,Context.MODE_APPEND)
                                .edit()
                                .putString(Config.CARD_PWD,newPwd)
                                .commit();
                    }
                    if(onChangePwdListener!=null)onChangePwdListener.onSucess(ret,msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private String getJustCookie(){
        return userSp.getString(Config.COOKIE,"");
    }
    private String getSetCookie(){
        return userSp.getString(Config.SET_COOKIE,"");
    }
}
