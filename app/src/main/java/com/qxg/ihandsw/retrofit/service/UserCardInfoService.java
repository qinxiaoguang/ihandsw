package com.qxg.ihandsw.retrofit.service;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * 获取用户的学号，卡号，和姓名
 * Created by Qking on 2016/11/6.
 */

public interface UserCardInfoService {
    @GET("/Account/UserInfo")
    public Call<ResponseBody> getUserInfo();
}
