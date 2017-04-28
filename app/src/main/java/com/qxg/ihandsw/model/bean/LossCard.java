package com.qxg.ihandsw.model.bean;

/**
 * Created by Qking on 2016/11/6.
 */

public class LossCard {
    String name;
    String id; //卡号
    String sid; //学工号
    String lossAdress; //丢失地点
    String phone;//联系电话
    String des; //说明
    String date; //发布时间
    int cnt; //信息中的第几个

    public LossCard(String name, String id, String sid, String lossAdress, String phone, String des,String date,int cnt) {
        this.name = name;
        this.id = id;
        this.sid = sid;
        this.lossAdress = lossAdress;
        this.phone = phone;
        this.des = des;
        this.date = date;
        this.cnt = cnt;
    }

    public int getCnt(){
        return cnt;
    }
    public String getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getLossAdress() {
        return lossAdress;
    }

    public void setLossAdress(String lossAdress) {
        this.lossAdress = lossAdress;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }
}
