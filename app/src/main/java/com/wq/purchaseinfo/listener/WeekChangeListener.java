package com.wq.purchaseinfo.listener;

import org.joda.time.DateTime;


public interface WeekChangeListener {
    void onWeekChanged(DateTime firstDayOfWeek);
}
