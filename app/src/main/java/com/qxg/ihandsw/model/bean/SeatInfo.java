package com.qxg.ihandsw.model.bean;

/**
 * Created by Qking on 2016/11/11.
 */

public class SeatInfo {
    public String floor;
    public String all;
    public String rest;
    public String nowSit;

    public SeatInfo(String floor, String all, String rest,String nowSit) {
        this.floor = floor;
        this.all = all;
        this.rest = rest;
        this.nowSit = nowSit;
    }
}
