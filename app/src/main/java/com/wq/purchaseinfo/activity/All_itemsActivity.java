package com.wq.purchaseinfo.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;


import com.wq.purchaseinfo.R;
import com.wq.purchaseinfo.entity.Notice;
import com.wq.purchaseinfo.net.HttpConnect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class All_itemsActivity extends BaseActivity {
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
}
