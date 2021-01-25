package com.wq.purchaseinfo.utils;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

import org.litepal.LitePalApplication;

public class BDApplication extends LitePalApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        // 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
        SDKInitializer.initialize(this);
    }

}