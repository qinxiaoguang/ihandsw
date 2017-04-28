package com.qxg.ihandsw.model.bean;

/**
 * Created by Qking on 2016/11/8.
 */

public class OrderFloorInfo {
    public String roomId;
    public String floor; //楼层
    public String date; //
    public String all;
    public String rest; //剩余座位
    public String param; // 2017.3.10 学校系统更新，新增Param参数

    public OrderFloorInfo(String roomId, String floor, String date, String all, String rest,String param) {
        this.roomId = roomId;
        this.floor = floor;
        this.date = date;
        this.all = all;
        this.rest = rest;
        this.param = param;
    }
}
