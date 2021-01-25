package com.wq.purchaseinfo.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.wq.purchaseinfo.MainActivity;
import com.wq.purchaseinfo.R;
import com.wq.purchaseinfo.adapter.EventAdapter;
import com.wq.purchaseinfo.entity.Event;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.wq.purchaseinfo.MainActivity;
import com.wq.purchaseinfo.R;

import java.util.Calendar;
import java.util.TimeZone;

public class EventActivity extends AppCompatActivity {
    //Android2.2版本以后的URL，之前的就不写了
    private static String calanderURL = "content://com.android.calendar/calendars";
    private static String calanderEventURL = "content://com.android.calendar/events";
    private static String calanderRemiderURL = "content://com.android.calendar/reminders";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

    }

    public void onClick(View v) {
        if (v.getId() == R.id.readUserButton) {  //读取系统日历账户，如果为0的话先添加
            Cursor userCursor = getContentResolver().query(Uri.parse(calanderURL), null, null, null, null);

            Log.d("Count: " ,""+userCursor.getCount());
            Toast.makeText(this, "Count: " + userCursor.getCount(), Toast.LENGTH_LONG).show();

            for (userCursor.moveToFirst(); !userCursor.isAfterLast(); userCursor.moveToNext()) {
                System.out.println("name: " + userCursor.getString(userCursor.getColumnIndex("ACCOUNT_NAME")));


                String userName1 = userCursor.getString(userCursor.getColumnIndex("name"));
                String userName0 = userCursor.getString(userCursor.getColumnIndex("ACCOUNT_NAME"));
                Toast.makeText(this, "NAME: " + userName1 + " -- ACCOUNT_NAME: " + userName0, Toast.LENGTH_LONG).show();
            }
        } else if (v.getId() == R.id.inputaccount) {        //添加日历账户
            initCalendars();

        } else if (v.getId() == R.id.delEventButton) {  //删除事件

            int rownum = getContentResolver().delete(Uri.parse(calanderURL), "_id!=-1", null);  //注意：会全部删除所有账户，新添加的账户一般从id=1开始，
            //可以令_id=你添加账户的id，以此删除你添加的账户
            Toast.makeText(EventActivity.this, "删除了: " + rownum, Toast.LENGTH_LONG).show();

        } else if (v.getId() == R.id.readEventButton) {  //读取事件
            Cursor eventCursor = getContentResolver().query(Uri.parse(calanderEventURL), null, null, null, null);
            if (eventCursor.getCount() > 0) {
                eventCursor.moveToLast();             //注意：这里与添加事件时的账户相对应，都是向最后一个账户添加
                String eventTitle = eventCursor.getString(eventCursor.getColumnIndex("title"));
                Toast.makeText(EventActivity.this, eventTitle, Toast.LENGTH_LONG).show();
            }
        } else if (v.getId() == R.id.writeEventButton) {
            // 获取要出入的gmail账户的id
            String calId = "";
            Cursor userCursor = getContentResolver().query(Uri.parse(calanderURL), null, null, null, null);
            if (userCursor.getCount() > 0) {
                userCursor.moveToLast();  //注意：是向最后一个账户添加，开发者可以根据需要改变添加事件 的账户
                calId = userCursor.getString(userCursor.getColumnIndex("_id"));
            } else {
                Toast.makeText(this, "没有账户，请先添加账户", Toast.LENGTH_SHORT).show();
                return;
            }

            ContentValues event = new ContentValues();
            event.put("title", "提醒测试Title");
            event.put("description", "这是一条日历提醒测试内容.lol~");
            // 插入账户
            event.put("calendar_id", calId);
            System.out.println("calId: " + calId);
            event.put("eventLocation", "测试地点");

            Calendar mCalendar = Calendar.getInstance();
            mCalendar.set(Calendar.HOUR_OF_DAY, 14);
            mCalendar.set(Calendar.MINUTE, 53);
            long start = mCalendar.getTime().getTime();
            mCalendar.set(Calendar.HOUR_OF_DAY, 15);
            long end = mCalendar.getTime().getTime();
            event.put(CalendarContract.Events.DTSTART, start);//开始时间
            event.put(CalendarContract.Events.DTEND, end);//结束时间
            //设置一个全天事件的条目
            //event.put("allDay", 1); // 0 for false, 1 for true
            //事件状态暂定(0)，确认(1)或取消(2)
            //event.put("eventStatus", 1);
            //控制是否事件触发报警，提醒如下
            event.put(CalendarContract.Events.HAS_ALARM, 1); // 0 for false, 1 for true
            //设置时区,否则会报错
            event.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
            //添加事件
            Uri newEvent = getContentResolver().insert(Uri.parse(calanderEventURL), event);
            //事件提醒的设定
            long id = Long.parseLong(newEvent.getLastPathSegment());
            ContentValues values = new ContentValues();
            values.put(CalendarContract.Reminders.EVENT_ID, id);
            // 提前5分钟有提醒(提前0分钟时，值为0)
            values.put(CalendarContract.Reminders.MINUTES, 5);
            values.put(CalendarContract.Reminders.METHOD, 1);
            Uri uri = getContentResolver().insert(Uri.parse(calanderRemiderURL), values);
            if (uri == null) {
                // 添加闹钟提醒失败直接返回
                Toast.makeText(EventActivity.this, "插入事件失败!!!", Toast.LENGTH_LONG).show();
                return;
            }
            Toast.makeText(EventActivity.this, "插入事件成功!!!", Toast.LENGTH_LONG).show();
        }
    }

    //添加账户
    private void initCalendars() {

        TimeZone timeZone = TimeZone.getDefault();
        ContentValues value = new ContentValues();

        value.put(CalendarContract.Calendars.NAME, "yy");
        value.put(CalendarContract.Calendars.ACCOUNT_NAME, "mygmailaddress@gmail.com");
        value.put(CalendarContract.Calendars.ACCOUNT_TYPE, "com.android.exchange");
        value.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, "mytt");
        value.put(CalendarContract.Calendars.VISIBLE, 1);
        value.put(CalendarContract.Calendars.CALENDAR_COLOR, -9206951);
        value.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER);
        value.put(CalendarContract.Calendars.SYNC_EVENTS, 1);
        value.put(CalendarContract.Calendars.CALENDAR_TIME_ZONE, timeZone.getID());
        value.put(CalendarContract.Calendars.OWNER_ACCOUNT, "mygmailaddress@gmail.com");
        value.put(CalendarContract.Calendars.CAN_ORGANIZER_RESPOND, 0);

        Uri calendarUri = CalendarContract.Calendars.CONTENT_URI;
        calendarUri = calendarUri.buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, "mygmailaddress@gmail.com")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, "com.android.exchange")
                .build();

        getContentResolver().insert(calendarUri, value);
    }

}
*/
public class EventActivity extends AppCompatActivity {

    private List<Map<String,String>> contentList=new ArrayList<Map<String, String>>();

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
    //按返回键退回到主界面
    public void onBackPressed() {
        Intent intent = new Intent(EventActivity.this, MainActivity.class);
        startActivity(intent);
        EventActivity.this.finish();
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
                EventActivity.this.finish();
                break;

            case R.id.place:
                Toast.makeText(this, "点击地图视图", Toast.LENGTH_SHORT).show();
                Intent intent2 = new Intent(this, PlaceActivity.class);
                startActivity(intent2);
                EventActivity.this.finish();
                break;
            case R.id.more:
                Toast.makeText(this, "点击状态查询", Toast.LENGTH_SHORT).show();
                Intent intent3 = new Intent(this, SearchActivity.class);
                startActivity(intent3);
                EventActivity.this.finish();
                break;
            case R.id.event:
                Toast.makeText(this, "点击日程管理", Toast.LENGTH_SHORT).show();
                Intent intent4 = new Intent(this, EventActivity.class);
                startActivity(intent4);
                EventActivity.this.finish();
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
