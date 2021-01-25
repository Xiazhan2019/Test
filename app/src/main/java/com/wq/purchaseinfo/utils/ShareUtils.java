package com.wq.purchaseinfo.utils;

import android.content.Context;
import android.content.SharedPreferences;

//SharedPreferences封装
public class ShareUtils {
    public static final String NAME="config";
    //键 值
    public static void putString(Context mContext,String key,String value){
        SharedPreferences sp = mContext.getSharedPreferences(NAME,Context.MODE_PRIVATE);
        sp.edit().putString(key,value).commit();
    }
    //键 默认值
    public static String getString(Context mContext, String key, String defValue){
        SharedPreferences sp = mContext.getSharedPreferences(NAME,Context.MODE_PRIVATE);
        return sp.getString(key,defValue);
    }
    //键 值
    public static void putInt(Context mContext,String key,Integer value){
        SharedPreferences sp = mContext.getSharedPreferences(NAME,Context.MODE_PRIVATE);
        sp.edit().putInt(key,value).commit();
    }
    //键 默认值
    public static Integer getInt(Context mContext, String key, Integer defValue){
        SharedPreferences sp = mContext.getSharedPreferences(NAME,Context.MODE_PRIVATE);
        return sp.getInt(key,defValue);
    }
    //键 值
    public static void putBoolean(Context mContext,String key,Boolean value){
        SharedPreferences sp = mContext.getSharedPreferences(NAME,Context.MODE_PRIVATE);
        sp.edit().putBoolean(key,value).commit();
    }
    //键 默认值
    public static Boolean getBoolean(Context mContext, String key, Boolean defValue){
        SharedPreferences sp = mContext.getSharedPreferences(NAME,Context.MODE_PRIVATE);
        return sp.getBoolean(key,defValue);
    }
    //删除 单个
    public static void deleShare(Context mContext, String key){
        SharedPreferences sp = mContext.getSharedPreferences(NAME,Context.MODE_PRIVATE);
        sp.edit().remove(key).commit();
    }
    //删除 全部
    public static void deleAll(Context mContext){
        SharedPreferences sp = mContext.getSharedPreferences(NAME,Context.MODE_PRIVATE);
        sp.edit().clear().commit();
    }


}