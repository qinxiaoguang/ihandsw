package com.qxg.ihandsw.retrofit.service;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Qking on 2016/11/6.
 */

public interface LossCardService {
    @GET("/CardManage/CardLoss/Detail/{id}")
    public Call<ResponseBody> getLossCardInfo(@Path("id") int id);

    @GET("/CardManage/CardLoss/ManageList")
    public Call<ResponseBody> getLossCardTime(@Query("pageindex") int page);
}
