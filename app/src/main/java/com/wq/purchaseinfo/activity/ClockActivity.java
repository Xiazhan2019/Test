package com.wq.purchaseinfo.activity;


import android.content.Context;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.appcompat.app.AlertDialog;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wq.purchaseinfo.R;
import com.wq.purchaseinfo.adapter.TimeAdapter;
import com.wq.purchaseinfo.entity.Clock;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class ClockActivity extends BaseActivity {
    public static List<Clock> list = new ArrayList<>();
    public static TimeAdapter timeAdapter;
    RecyclerView recyclerView;
    DrawerLayout drawerLayout;
    private ImageView add;
    private ImageView explain;
    TextView title;
    Context context = ClockActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock);
        title = findViewById(R.id.title);
        recyclerView = findViewById(R.id.clock_list);
        add = findViewById(R.id.add_clock);
        explain = findViewById(R.id.explain_clock);
        drawerLayout = findViewById(R.id.layout1);
        initTitle();
        LitePal.getDatabase();
        initRecyclerView();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initRecyclerView();
    }

    private void initTitle() {
        add.setImageResource(R.drawable.add_clock);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(context, AddClockActivity.class);
                startActivity(intent1);
            }
        });
        title.setText("你的闹钟");
        explain.setImageResource(R.drawable.explain_clock);
        explain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ClockActivity.this);
                builder.setTitle("使用说明");
                builder.setMessage("关于闹钟的设置,暂时一次只能运行一个闹钟,下一个版本继续改");
                builder.setCancelable(true);
                builder.setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });
    }

    private void initRecyclerView() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        timeAdapter = new TimeAdapter(list, context);
        recyclerView.setAdapter(timeAdapter);
        list.clear();
        List<Clock> list1 = DataSupport.findAll(Clock.class);
        for (Clock clock : list1) {
            list.add(clock);
        }
        timeAdapter.notifyDataSetChanged();
    }

//    //按返回键时返回主界面
//    public void onBackPressed() {
//        Intent intent = new Intent(ClockActivity.this, CalendarActivity.class);
//        startActivity(intent);
//        ClockActivity.this.finish();
//    }
}