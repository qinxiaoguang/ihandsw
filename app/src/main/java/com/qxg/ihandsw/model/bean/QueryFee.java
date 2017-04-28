package com.qxg.ihandsw.model.bean;

/**
 * Created by qxg on 17-2-20.
 */

public class QueryFee {
    String date; //交易时间
    String name; //商户名称
    String tName; //交易名称
    String moneyCount; //交易金额
    String remain;// 交易余额

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String gettName() {
        return tName;
    }

    public void settName(String tName) {
        this.tName = tName;
    }

    public String getMoneyCount() {
        return moneyCount;
    }

    public void setMoneyCount(String moneyCount) {
        this.moneyCount = moneyCount;
    }

    public String getRemain() {
        return remain;
    }

    public void setRemain(String remain) {
        this.remain = remain;
    }

    @Override
    public String toString() {
        return date + " " + name + " " + tName + " 消费：" + moneyCount + " 余额:" + remain;
    }
}
