package com.qxg.ihandsw.model.bean;

/**
 * Created by Qking on 2016/11/9.
 */

public class NowBorrow {
    public String id; //条码号
    public String name; //题名/责任者
    public String borrowData; //借阅日期
    public String returnData; //应还日期
    public String address;  //馆藏地
    public String check;

    public NowBorrow(String id, String name, String borrowData, String returnData, String address,String check) {
        this.id = id;
        this.name = name;
        this.borrowData = borrowData;
        this.returnData = returnData;
        this.address = address;
        this.check = check;
    }
}
