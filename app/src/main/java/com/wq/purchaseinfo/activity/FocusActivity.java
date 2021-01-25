package com.wq.purchaseinfo.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.wq.purchaseinfo.R;
import com.wq.purchaseinfo.entity.Notice;
import com.wq.purchaseinfo.utils.HttpConnect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FocusActivity extends AppCompatActivity {
    private ListView listView;
    private List<Notice> itemList = new ArrayList<>();
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            switch (msg.what) {
                case 0x11:
                    //创建SimpleAdapter适配器将数据绑定到item显示控件上
                    SimpleAdapter adapter = new SimpleAdapter(FocusActivity.this, (List<? extends Map<String, ?>>) msg.obj, R.layout.allitem_title_view,
                            new String[]{"title"}, new int[]{R.id.title});
                    //实现列表的显示
                    listView.setAdapter(adapter);
                    //实现列表点击事件
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            TextView tv = (TextView)view.findViewById(R.id.title);
                            String text = tv.getText().toString();
                            Toast.makeText(FocusActivity.this, text, Toast.LENGTH_LONG).show();
                            String content;
                            if(itemList != null && !itemList.isEmpty()){
                                //点击标题显示内容
                                for(int i =0;i<itemList.size();i++){
                                    String element = itemList.get(i).getTitle();
                                    if(element == text){
                                        content = itemList.get(i).getContent();
                                        NoticeActivity.actionStart(FocusActivity.this,text,content);
                                    }
                                }
                            }else {
                                Toast.makeText(FocusActivity.this, "结果为空", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    break;
                case 0x12:
                    Toast.makeText(FocusActivity.this, (String) msg.obj, Toast.LENGTH_LONG).show();
                    break;
            }

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus);
        listView = (ListView) findViewById(R.id.focus_view);
        //显示返回按钮
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setTitle("我的关注");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        my_focus();
    }

    public void my_focus(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = handler.obtainMessage();
                //获取用户姓名
                SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
                HttpConnect con = new HttpConnect();
                itemList = con.SendFocus(sp.getString("username",null));
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home://一定添加android还有下面这行代码
                onBackPressed();
                finish();
                return true;
            default:
        }
        return true;
    }
    /*   //按返回键退回到用户界面
    public void onBackPressed() {
        Intent intent = new Intent(FocusActivity.this, UserActivity.class);
        startActivity(intent);
        finish();
    }*/
}
