package com.wq.purchaseinfo.entity;

//记录每天的招标数量和对应日期
public class DayNum {

    public DayNum(int count, String date) {
        this.count = count;
        this.date = date;
    }
    public DayNum() {
    }
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int count;
    public String date;


}
