package com.wq.purchaseinfo.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.wq.purchaseinfo.R;
import com.wq.purchaseinfo.fragment.CalendarFragment;
import com.wq.purchaseinfo.fragment.HomeFragment;
import com.wq.purchaseinfo.fragment.UserFragment;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class FirstActivity extends BaseActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private String[] tabs = {"首页","日历 ","个人中心"};
    private List<Fragment> tabList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        tabLayout = findViewById(R.id.tab);
        viewPager = findViewById(R.id.v_pager);
        tabList.add(0, new HomeFragment());
        tabList.add(1, new CalendarFragment());
        tabList.add(2, new UserFragment());

        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return tabList.get(position);
            }
            @Override
            public int getCount() {
                return tabList.size();
            }
            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return tabs[position];
            }
        });

        tabLayout.setupWithViewPager(viewPager);
    }
}
