package com.wq.purchaseinfo.listener;

import android.view.View;
import android.view.ViewGroup;

import org.joda.time.DateTime;


public interface GetViewHelper {

    View getDayView(int position, View convertView, ViewGroup parent, DateTime dateTime, boolean select);
    View getWeekView(int position, View convertView, ViewGroup parent, String week);

}
