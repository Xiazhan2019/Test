package com.wq.purchaseinfo.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.wq.purchaseinfo.MainActivity;
import com.wq.purchaseinfo.R;
import com.wq.purchaseinfo.adapter.SearchListAdapter;
import com.wq.purchaseinfo.entity.Notice;
import com.wq.purchaseinfo.entity.Trace;
import com.wq.purchaseinfo.utils.HttpConnect;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


public class SearchActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView lvTrace;
    private List<Trace> traceList = new ArrayList<>();
    private SearchListAdapter adapter;
    private Button btn_clear;

    //编号文本框
    private EditText number;
    private Button search;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            switch (msg.what) {
                case 0x11:
                    adapter = new SearchListAdapter(SearchActivity.this, (List<Trace>) msg.obj);
                    lvTrace.setAdapter(adapter);
                    break;
                case 0x12:
                    Toast.makeText(SearchActivity.this, (String) msg.obj, Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        lvTrace = (ListView) findViewById(R.id.lvTrace);
        number = (EditText) findViewById(R.id.number);
        search = (Button) findViewById(R.id.search);
        btn_clear = (Button) findViewById(R.id.clear);
        search.setOnClickListener(this);
        btn_clear.setOnClickListener(this);
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SearchActivity.this, MainActivity.class);
        startActivity(intent);
        SearchActivity.this.finish();
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

    //判断字符串是否含有中文
    public boolean isChinese(String str){
        for (int i = 0; i < str.length(); i++) {  //遍历所有字符
            char ch = str.charAt(i);
            if(ch >= 0x4E00 && ch <= 0x9FA5){  //中文在unicode编码中所在的区间为0x4E00-0x9FA5
                return true;  //不在这个区间，说明不是中文字符，返回false
            }
        }
        return false;  //全部在中文区间，说明全部是中文字符，返回true
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //获得输入框内容
                        String inputText = number.getText().toString();
                        Log.d("文本",""+inputText);
                        Log.d("中文"," "+isChinese(""+inputText));
                        Message message = handler.obtainMessage();
                        HttpConnect con = new HttpConnect();
                        List<Notice> idlist = con.SendByHttpClient(inputText);
                        for (Notice item : idlist) {
                            traceList.add(new Trace("项目名称", "" + item.getTitle()));
                            traceList.add(new Trace("项目编码", "" + item.getId()));
                            traceList.add(new Trace("开始日期", "" + item.getpostTime()));
                            traceList.add(new Trace("结束日期", "" + item.getTime()));
                            traceList.add(new Trace("招标状态", "" + item.getType()));
                        }
                        if(traceList != null && !traceList.isEmpty()){
                            message.what = 0x11;
                            message.obj = traceList;
                        } else{
                            message.what = 0x12;
                            message.obj = "输入信息不存在，查询失败";
                        }
                        handler.sendMessage(message);
                    }
                }).start();
                break;
            case R.id.clear:
                lvTrace.setAdapter(null);
                traceList.clear();
                Toast.makeText(SearchActivity.this,"列表已清空",Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //加载菜单文件
        getMenuInflater().inflate(R.menu.title_menu, menu);
        setIconsVisible(menu,true);
        //默认显示三个点
        //new MenuInflater(this).inflate(R.menu.title_menu, menu);
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

    //在点击这个菜单的时候，会做对应的事，类似于侦听事件，这里我们也要重写它
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //这里是一个switch语句,主要通过menu文件中的id值来确定点了哪个菜单，然后做对应的操作，这里的menu是指你加载的那个菜单文件
        switch (item.getItemId()) {
            case R.id.calendar:
                Toast.makeText(this, "点击日历视图", Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent(this, MainActivity.class);
                startActivity(intent1);
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
                Toast.makeText(this, "点击日程管理", Toast.LENGTH_SHORT).show();
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

}
