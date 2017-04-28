package com.qxg.ihandsw.model;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import com.qxg.ihandsw.utils.Log;

import com.qxg.ihandsw.Config;
import com.qxg.ihandsw.model.bean.HireInfo;
import com.qxg.ihandsw.model.bean.OfficeNotifyBean;
import com.qxg.ihandsw.model.bean.SchoolDate;
import com.qxg.ihandsw.model.bean.WorkArrange;
import com.qxg.ihandsw.widget.SmallToolWidget.SchoolHireDialog;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Qking on 2016/11/15.
 */

public class SmallToolModel {

    OkHttpClient client;
    Activity activity;
    Handler handler;
    public SmallToolModel(Activity activity){
        this.activity = activity;
        client = new OkHttpClient();
        handler = new Handler(activity.getMainLooper());
    }


    public interface OnLoadOfficeNotifyListener{
        public void onFailed();
        public void onSucess(List<OfficeNotifyBean> notifys);
    }

    private OnLoadOfficeNotifyListener onLoadOfficeNotifyListener;

    public void setOnLoadOfficeNotifyListener(OnLoadOfficeNotifyListener onLoadOfficeNotifyListener) {
        this.onLoadOfficeNotifyListener = onLoadOfficeNotifyListener;
    }

    public void loadOfficeNotify(){
        //更新通知
        Request request = new Request.Builder()
                .url(Config.OFFICE_NOTIFY_URL)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if(onLoadOfficeNotifyListener!=null){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            onLoadOfficeNotifyListener.onFailed();
                        }
                    });
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Document doc = Jsoup.parse(result);
                Elements articles = doc.select(".articleul").first().select("a");
                Elements times = doc.select(".timeul").first().select("li");
                final List<OfficeNotifyBean> notifys = new ArrayList<OfficeNotifyBean>();
                for(int i=0;i<articles.size();i++){
                    Element a = articles.get(i);
                    String url = a.attr("href".toString());
                    if(url.startsWith("/")){
                        url = "http://jwc.wh.sdu.edu.cn" + url;

                    }
                    notifys.add(new OfficeNotifyBean(a.text(),times.get(i).text(),url));
                }
                //读出结果后，开始回调显示
                if(onLoadOfficeNotifyListener!=null){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            onLoadOfficeNotifyListener.onSucess(notifys);
                        }
                    });

                }
            }
        });

    }

    public interface OnLoadSchoolDateListener{
        public void onFailed();
        public void onSucess(List<SchoolDate> dates);
    }

    private OnLoadSchoolDateListener onLoadSchoolDateListener;

    public void setOnLoadSchoolDateListener(OnLoadSchoolDateListener onLoadSchoolDateListener) {
        this.onLoadSchoolDateListener = onLoadSchoolDateListener;
    }

    public void loadSchoolDate(){
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        final String url = Config.TERM_DATE + year + "-1.html";
        System.out.println(url);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document doc = Jsoup.parse(new URL(url).openStream(),"gb2312",url);
                    Elements ems = doc.select("#Sleft").first().select("a");
                    final List<SchoolDate> dates = new ArrayList<SchoolDate>();
                    int size = ems.size();
                    for(int i=0; i< size -1;i++){
                        Element e = ems.get(i);
                        String title = new String(e.text());
                        String href = e.attr("href").split("\\.")[0];
                        dates.add(new SchoolDate(title,href));
                    }
                    if(onLoadSchoolDateListener!=null){
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                onLoadSchoolDateListener.onSucess(dates);
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    if(onLoadDateImgListener!=null){
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                onLoadSchoolDateListener.onFailed();
                            }
                        });
                    }
                }
            }
        }).start();
    }

    public interface OnLoadDateImgListener{
        public void onFailed();
        public void onSucess(Bitmap bitmap);
    }

    OnLoadDateImgListener onLoadDateImgListener;

    public void setOnLoadDateImgListener(OnLoadDateImgListener onLoadDateImgListener) {
        this.onLoadDateImgListener = onLoadDateImgListener;
    }

    public void loadDateImg(String url){
        //获取学期校历的图片
        Log.i("--->获取的校历照片URL",url);
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if(onLoadDateImgListener!=null){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            onLoadDateImgListener.onFailed();
                        }
                    });
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //返回成功后，读取图片数据
                final Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
                if(onLoadDateImgListener!=null){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("----->","获取成功");
                            onLoadDateImgListener.onSucess(bitmap);
                        }
                    });
                }
            }
        });
    }

    public interface OnLoadWorkArrangeListener{
        public void onFailed();
        public void onSucess(List<WorkArrange> arranges);
    }

    OnLoadWorkArrangeListener onLoadWorkArrangeListener;

    public void setOnLoadWorkArrangeListener(OnLoadWorkArrangeListener onLoadWorkArrangeListener) {
        this.onLoadWorkArrangeListener = onLoadWorkArrangeListener;
    }

    public void loadWorkArrange(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    final List<WorkArrange> arranges = new ArrayList<WorkArrange>();
                    String url = Config.WORK_ARRANGE_URL;
                    Document doc = Jsoup.parse(new URL(url),8000);

                    String secUrl = doc.select(".list_table").first().select("a").first().attr("href");
                    Log.i("--->secUrl",secUrl);
                    Document secDoc = Jsoup.parse(new URL(secUrl),8000);
                    Elements ems = secDoc.select(".view_content").first().select("tr");  //tr从第3项开始
                    int size = ems.size();
                    for(int i=2;i<size;i++){
                        Elements tdEms = ems.get(i).select("td");
                        String date = tdEms.get(0).text();
                        String name = tdEms.get(1).text();
                        String time = tdEms.get(2).text();
                        String address = tdEms.get(3).text();
                        String joinPeople = tdEms.get(4).text();
                        String responsible = tdEms.get(5).text();
                        arranges.add(new WorkArrange(date,name,time,address,joinPeople,responsible));
                    }
                    //完毕后回调
                    if(onLoadWorkArrangeListener!=null){
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                onLoadWorkArrangeListener.onSucess(arranges);
                            }
                        });
                    }
                }catch (IOException e){
                    e.printStackTrace();
                    if(onLoadWorkArrangeListener!=null){
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                onLoadWorkArrangeListener.onFailed();
                            }
                        });

                    }
                }

            }
        }).start();

    }


    public interface OnLoadSchoolHireInfoListener{
        public void onFailed();
        public void onSucess(List<HireInfo> hires);
    }

    OnLoadSchoolHireInfoListener onLoadSchoolHireInfoListener;

    public void setOnLoadSchoolHireInfoListener(OnLoadSchoolHireInfoListener onLoadSchoolHireInfoListener) {
        this.onLoadSchoolHireInfoListener = onLoadSchoolHireInfoListener;
    }

    public void loadSchoolHireInfo(final int flag){
        //加载校园招聘信息
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    String url = "";
                    switch (flag){
                        case SchoolHireDialog.SCHOOL_HIRE:
                            url = Config.SCHOOL_HIRE_URL;
                            break;
                        case SchoolHireDialog.ONLINE_HIRE:
                            url = Config.ONLINE_HIRE_URL;
                            break;
                    }
                    Document doc = Jsoup.parse(new URL(url),8000);
                    Elements ems = doc.select("table").get(2).select("tr");
                    final List<HireInfo> hires = new ArrayList<HireInfo>();
                    int size = ems.size();
                    for(int i=0;i < size;i++){
                        Elements tds = ems.get(i).select("td");
                        Element a = tds.get(1).select("a").first();
                        String href = "http://job.wh.sdu.edu.cn/" + a.attr("href");
                        String name = a.text();
                        String date = tds.get(2).text();

                        Log.i("获得的招聘信息为:--->",name + "," + href + "," + date);
                        hires.add(new HireInfo(name,href,date));
                    }
                    if(onLoadSchoolHireInfoListener!=null){
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                onLoadSchoolHireInfoListener.onSucess(hires);
                            }
                        });
                    }

                }catch (Exception e){
                    if(onLoadSchoolHireInfoListener!=null){
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                onLoadSchoolHireInfoListener.onFailed();
                            }
                        });
                    }
                }

            }
        }).start();
    }
}
