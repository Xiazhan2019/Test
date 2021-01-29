package com.wq.purchaseinfo.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.wq.purchaseinfo.R;
import com.wq.purchaseinfo.fragment.DayFragment;
import com.wq.purchaseinfo.fragment.MonthFragment;
import com.wq.purchaseinfo.fragment.WeekFragment;


import java.util.ArrayList;
import java.util.List;

public class CalendarActivity extends BaseActivity {


    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private List<String> mTitle;
    private List<Fragment> mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpager_first);
        //去掉阴影
        getSupportActionBar().setElevation(0);
        initData();
        initView();
    }

    //初始化数据
    private void initData() {
        mTitle = new ArrayList<>();
        mTitle.add("日");
        mTitle.add("月");
        mTitle.add("周");

        mFragment = new ArrayList<>();
        mFragment.add(new DayFragment());
        mFragment.add(new MonthFragment());
        mFragment.add(new WeekFragment());

    }

    //初始化view
    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.mViewPager);
        mTabLayout = (TabLayout)findViewById(R.id.mTabLayout);
        mViewPager.setOffscreenPageLimit(mFragment.size());
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @SuppressLint("RestrictedApi")
            @Override
            public void onPageSelected(int position) {
                Log.i("TAG", "position:" + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return mFragment.get(position);
            }
            @Override
            public int getCount() {
                return mFragment.size();
            }
            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return mTitle.get(position);
            }
        });
          mTabLayout.setupWithViewPager(mViewPager);
    }

    //每天的日程数
    public static class DayFinish {
        public int day;
        public int count;
        public int all;

        public DayFinish(int day, int count, int all) {
            this.day = day;
            this.count = count;
            this.all = all;
        }
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        CalendarActivity.this.finish();
//    }
}
