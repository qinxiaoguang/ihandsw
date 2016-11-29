package com.qxg.ihandsw.model.bean;

/**
 * Created by Qking on 2016/11/15.
 */

public class WorkArrange {
    public String date;
    public String name;
    public String time;
    public String address;
    public String joinPeople;
    public String responsible;//负责部门

    public WorkArrange(String date, String name, String time, String address, String joinPeople, String responsible) {
        this.date = date;
        this.name = name;
        this.time = time;
        this.address = address;
        this.joinPeople = joinPeople;
        this.responsible = responsible;
    }
}
