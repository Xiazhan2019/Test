package com.wq.purchaseinfo;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.wq.purchaseinfo.activity.ClockActivity;
import com.wq.purchaseinfo.activity.EventActivity;
import com.wq.purchaseinfo.activity.PlaceActivity;
import com.wq.purchaseinfo.activity.SearchActivity;
import com.wq.purchaseinfo.activity.UserActivity;
import com.wq.purchaseinfo.fragment.DayFragment;
import com.wq.purchaseinfo.fragment.MonthFragment;
import com.wq.purchaseinfo.fragment.WeekFragment;


import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //TabLayout
    private TabLayout mTabLayout;
    //ViewPager
    private ViewPager mViewPager;
    //title
    private List<String> mTitle;
    //Fragment
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
        //预加载
        mViewPager.setOffscreenPageLimit(mFragment.size());
        //mViewPager滑动监听
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
        //设置适配器
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            //选中的item
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return mFragment.get(position);
            }
            //返回item的个数
            @Override
            public int getCount() {
                return mFragment.size();
            }
            //设置标题
            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return mTitle.get(position);
            }
        });
        //绑定
          mTabLayout.setupWithViewPager(mViewPager);
    }

    //每天的日程数
    public static class DayFinish {
        int day;
        int count;
        int all;

        public DayFinish(int day, int count, int all) {
            this.day = day;
            this.count = count;
            this.all = all;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MainActivity.this.finish();
    }


    //加载菜单栏
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.title_menu, menu);
        //显示图标
        setIconsVisible(menu,true);
        //默认显示三个点
        //  new MenuInflater(this).inflate(R.menu.title_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    private void setIconsVisible(Menu menu, boolean flag) {
        //判断menu是否为空
        if(menu != null) {
            try {
                //如果不为空,就反射拿到menu的setOptionalIconsVisible方法
                Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                //暴力访问该方法
                method.setAccessible(true);
                //调用该方法显示icon
                method.invoke(menu, flag);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    //在点击这个菜单的时候，会做对应的事，类似于侦听事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //这里是一个switch语句,主要通过menu文件中的id值来确定点了哪个菜单，然后做对应的操作，这里的menu是指你加载的那个菜单文件
        switch (item.getItemId()) {
            case R.id.calendar:
                Toast.makeText(this, "点击日历视图", Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent(this, MainActivity.class);
                startActivity(intent1);
                MainActivity.this.finish();
                break;

            case R.id.place:
                Toast.makeText(this, "点击地图视图", Toast.LENGTH_SHORT).show();
                Intent intent2 = new Intent(this, PlaceActivity.class);
                startActivity(intent2);
                MainActivity.this.finish();
                break;
            case R.id.more:
                Toast.makeText(this, "点击查询", Toast.LENGTH_SHORT).show();
                Intent intent3 = new Intent(this, SearchActivity.class);
                startActivity(intent3);
                MainActivity.this.finish();
                break;
            case R.id.event:
                Toast.makeText(this, "日程管理", Toast.LENGTH_SHORT).show();
                Intent intent4 = new Intent(this, EventActivity.class);
                startActivity(intent4);
                MainActivity.this.finish();
                break;
            case R.id.clock:
                Toast.makeText(this, "设置闹钟", Toast.LENGTH_SHORT).show();
                Intent intent5 = new Intent(this, ClockActivity.class);
                startActivity(intent5);
                finish();
                break;
            case R.id.user:
                Toast.makeText(this, "个人中心", Toast.LENGTH_SHORT).show();
                Intent intent6 = new Intent(this, UserActivity.class);
                startActivity(intent6);
                finish();
                break;
            case R.id.quit:
               // exit(0);
                showdialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    //退出程序
    public void showdialog() {
        //定义一个新的对话框对象
        AlertDialog.Builder alertdialogbuilder = new AlertDialog.Builder(this);
        //设置对话框提示内容
        alertdialogbuilder.setMessage("确定要退出吗？");
        //定义对话框2个按钮标题及接受事件的函数
        alertdialogbuilder.setPositiveButton("确定", click1);
        alertdialogbuilder.setNegativeButton("取消", click2);
        //创建并显示对话框
        AlertDialog alertdialog1 = alertdialogbuilder.create();
        alertdialog1.show();
    }

    private DialogInterface.OnClickListener click1 = new DialogInterface.OnClickListener() {
        //使用该标记是为了增强程序在编译时候的检查，如果该方法并不是一个覆盖父类的方法，在编译时编译器就会报告错误。
        @Override
        public void onClick(DialogInterface arg0, int arg1) {
            //当按钮click1被按下时执行结束进程
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    };
    private DialogInterface.OnClickListener click2 = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface arg0, int arg1) {
            //当按钮click2被按下时则取消操作
            arg0.cancel();
        }
    };
}
