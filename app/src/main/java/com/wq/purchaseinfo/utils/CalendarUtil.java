package com.wq.purchaseinfo.utils;


import org.joda.time.DateTime;

public class CalendarUtil {
    public static boolean isSameDay(DateTime t1, DateTime t2){
        return t1.toString("yyyyMMdd").equals(t2.toString("yyyyMMdd"));
    }

    public static boolean isToday(DateTime t){
        return isSameDay(t, new DateTime());
    }
}
