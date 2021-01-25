package com.wq.purchaseinfo.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import com.wq.purchaseinfo.MainActivity;
import com.wq.purchaseinfo.R;
import com.wq.purchaseinfo.entity.Notice;
import com.wq.purchaseinfo.utils.HttpConnect;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class All_itemsActivity extends AppCompatActivity {
    private ListView listView;
    private List<Notice> itemList = new ArrayList<>();
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            switch (msg.what) {
                case 0x11:
                    //创建SimpleAdapter适配器将数据绑定到item显示控件上
                    SimpleAdapter adapter = new SimpleAdapter(All_itemsActivity.this, (List<? extends Map<String, ?>>) msg.obj, R.layout.allitem_title_view,
                            new String[]{"title"}, new int[]{R.id.title});
                    //实现列表的显示
                    listView.setAdapter(adapter);
                    //实现列表点击事件
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            TextView tv = (TextView)view.findViewById(R.id.title);
                            String text = tv.getText().toString();
                            Toast.makeText(All_itemsActivity.this, text, Toast.LENGTH_LONG).show();
                            String content;
                            if(itemList != null && !itemList.isEmpty()){
                                //点击标题显示内容
                                for(int i =0;i<itemList.size();i++){
                                    String element = itemList.get(i).getTitle();
                                    if(element == text){
                                        content = itemList.get(i).getContent();
                                        NoticeActivity.actionStart(All_itemsActivity.this,text,content);
                                    }
                                }
                            }else {
                                Toast.makeText(All_itemsActivity.this, "结果为空", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    break;
                case 0x12:
                    Toast.makeText(All_itemsActivity.this, (String) msg.obj, Toast.LENGTH_LONG).show();
                    break;
            }

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.allitems_view);
        listView = (ListView) findViewById(R.id.all_data_view);
        select();
    }

    public void select(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = handler.obtainMessage();
            //    Filter_item_Dao item_dao = new Filter_item_Dao();
            //    List<Filtered_item> list = item_dao.find();
                HttpConnect con = new HttpConnect();
                itemList = con.request("FindAll");
                if (itemList != null) {
                    List<HashMap<String, Object>> data = new ArrayList<>();
                    for (Notice item : itemList) {
                        HashMap<String, Object> it = new HashMap<String, Object>();
                        it.put("title", item.getTitle());
                        it.put("content",item.getContent());
                        data.add(it);
                    }
                    message.what = 0x11;
                    message.obj = data;
                } else {
                    message.what = 0x12;
                    message.obj = "查询结果为空";
                }
                // 发消息通知主线程更新UI
                handler.sendMessage(message);
            }
        }).start();
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
            case R.id.user:
                Toast.makeText(this, "个人中心", Toast.LENGTH_SHORT).show();
                Intent intent6 = new Intent(this, ClockActivity.class);
                startActivity(intent6);
                finish();
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
