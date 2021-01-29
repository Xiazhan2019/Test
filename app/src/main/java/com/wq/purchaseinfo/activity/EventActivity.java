package com.wq.purchaseinfo.activity;


import android.content.Intent;
import android.os.Bundle;

import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.wq.purchaseinfo.R;
import com.wq.purchaseinfo.adapter.EventAdapter;
import com.wq.purchaseinfo.entity.Event;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventActivity extends BaseActivity {

    private List<Map<String,String>> contentList = new ArrayList<Map<String, String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        FloatingActionButton addContent=(FloatingActionButton) findViewById(R.id.add_tickler);
        addContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(EventActivity.this, AddEventActivity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onStart() {//每次活动有不可见变可见时调用
        super.onStart();
        contentList.clear();//清空list子项数据，实现刷新list
        initContent();//初始化
        RecyclerView recyclerView=(RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        EventAdapter adapter=new EventAdapter(contentList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void initContent(){
        List<Event> events= DataSupport.order("id desc").find(Event.class);
        for(Event event:events){
            String content=event.getContent();
            String time=event.getTime();
            Map<String,String> map=new HashMap<String, String>();
            map.put("content",content);
            map.put("time",time);
            contentList.add(map);
        }
    }

}
