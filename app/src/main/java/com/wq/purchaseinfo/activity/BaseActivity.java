package com.wq.purchaseinfo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.wq.purchaseinfo.R;
import com.wq.purchaseinfo.utils.UtilTools;

import java.lang.reflect.Method;

public class BaseActivity extends AppCompatActivity {
    /*主要做的事情
    1.统一的属性
    2.统一的方法
    3.统一的接口
    */

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //显示返回键
        getSupportActionBar().setDisplayShowHomeEnabled(true);
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
    //菜单栏操作
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                break;

            case R.id.place:
                Toast.makeText(this, "点击地图视图", Toast.LENGTH_SHORT).show();
                Intent intent2 = new Intent(this, PlaceActivity.class);
                startActivity(intent2);
                finish();
                break;
            case R.id.more:
                Toast.makeText(this, "点击状态查询", Toast.LENGTH_SHORT).show();
                Intent intent3 = new Intent(this, SearchActivity.class);
                startActivity(intent3);
                finish();
                break;
            case R.id.event:
                Toast.makeText(this, "点击事件查询", Toast.LENGTH_SHORT).show();
                Intent intent4 = new Intent(this, EventActivity.class);
                startActivity(intent4);
                finish();
                break;
            case R.id.clock:
                Toast.makeText(this, "设置闹钟", Toast.LENGTH_SHORT).show();
                Intent intent5 = new Intent(this, ClockActivity.class);
                startActivity(intent5);
                finish();
                break;
            case R.id.quit:
                UtilTools.showdialog(this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, FirstActivity.class);
        startActivity(intent);
        this.finish();
    }
}
