package com.wq.purchaseinfo.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.wq.purchaseinfo.Month_Calendar;
import com.wq.purchaseinfo.MainActivity;
import com.wq.purchaseinfo.R;
import com.wq.purchaseinfo.activity.All_itemsActivity;
import com.wq.purchaseinfo.activity.NoticeActivity;
import com.wq.purchaseinfo.entity.Notice;
import com.wq.purchaseinfo.utils.HttpConnect;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MonthFragment extends Fragment implements View.OnClickListener {

    private Month_Calendar cal;
    private ListView listView;
    private Button btn_current;
    private Button btn_cancel;
    private Button btn_all;
    private List<Notice> filtered_itemList = new ArrayList<Notice>();
    private Calendar curDtate = Calendar.getInstance();
    List<MainActivity.DayFinish> list = new ArrayList<>();

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            switch (msg.what) {
                case 0x11:
                    //创建SimpleAdapter适配器将数据绑定到item显示控件上
                    SimpleAdapter adapter = new SimpleAdapter(getActivity(), (List<? extends Map<String, ?>>) msg.obj, R.layout.allitem_title_view,
                            new String[]{"title"}, new int[]{R.id.title});
                    //实现列表的显示
                    listView.setAdapter(adapter);
                    //实现列表点击事件
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            TextView tv = (TextView)view.findViewById(R.id.title);
                            String text = tv.getText().toString();
                            //     Toast.makeText(MainActivity.this, text, Toast.LENGTH_LONG).show();
                            String content;
                            if(filtered_itemList != null && !filtered_itemList.isEmpty()){
                                //点击标题显示内容
                                for(int i =0;i<filtered_itemList.size();i++){
                                    String element = filtered_itemList.get(i).getTitle();
                                    if(element == text){
                                        content = filtered_itemList.get(i).getContent();
                                        NoticeActivity.actionStart(getActivity(),text,content);
                                    }
                                }

                            }else {
                                Toast.makeText(getActivity(), "结果为空", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    break;
                case 0x12:
                    listView.setAdapter(null);
                    Toast.makeText(getActivity(), (String) msg.obj, Toast.LENGTH_LONG).show();
                    break;
                case 0x13:
                    List nlist = new ArrayList<>();
                    nlist = (List) msg.obj;
                    for (int i = 1; i <= 31; i++) {
                        //  Log.d("count" + i, " " + monthlist.get(i-1));
                        list.add(new MainActivity.DayFinish(i, (Integer) nlist.get(i-1), 0));
                    }
                    Log.d("message" , " " +nlist);
                    int year = curDtate.get(Calendar.YEAR);//当前年份
                    int month = curDtate.get(Calendar.MONTH)+1;//当前月份
                    cal.setRenwu(year+"年"+month+"月", list);

                    break;
            }

        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.month_frag,null);
        findView(view);
        return view;
    }
    //初始化view
    private void findView(View view){
        cal = (Month_Calendar)view.findViewById(R.id.cal);
        btn_current = (Button)view.findViewById(R.id.btn_current);
        btn_current.setOnClickListener(this);
        btn_cancel = (Button)view.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(this);
        btn_all = (Button)view.findViewById(R.id.btn_all);
        btn_all.setOnClickListener(this);
        listView = (ListView) view.findViewById(R.id.data_view);

        init_data();

        cal.setOnClickListener(new Month_Calendar.onClickListener() {
            @Override
            public void onLeftRowClick() {
                //    Toast.makeText(MainActivity.this, "点击减箭头", Toast.LENGTH_SHORT).show();
                cal.monthChange(-1);
                new Thread(){
                    @Override
                    public void run() {
                        try{
                            Thread.sleep(1000);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    cal.setRenwu(list);
                                }
                            });
                        }catch (Exception e){
                        }
                    }
                }.start();
            }

            @Override
            public void onRightRowClick() {
                //    Toast.makeText(MainActivity.this, "点击加箭头", Toast.LENGTH_SHORT).show();
                cal.monthChange(1);
                new Thread(){
                    @Override
                    public void run() {
                        try{
                            Thread.sleep(100);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    cal.setRenwu(list);
                                }
                            });
                        }catch (Exception e){
                        }
                    }
                }.start();
            }

            @Override
            public void onTitleClick(String monthStr, Date month) {
                Toast.makeText(getActivity() , "点击了标题："+monthStr, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onWeekClick(int weekIndex, String weekStr) {
                Toast.makeText(getActivity(), "点击了星期："+weekStr, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDayClick(int day, final String dayStr, MainActivity.DayFinish finish) {
                Toast.makeText(getActivity(), "点击了日期："+dayStr, Toast.LENGTH_SHORT).show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message message = handler.obtainMessage();
                    //    Filter_item_Dao item_dao = new Filter_item_Dao();
                    //    List<Filtered_item> allist = item_dao.find();
                    //    Log.d("Tag","数据 "+allist);
                        HttpConnect con = new HttpConnect();
                        List<Notice> allist = con.request("FindAll");
                        String date;
                        for(int i=0;i <allist.size();i++){
                            String select = allist.get(i).getpostTime().substring(0,10);
                       //     Log.d("数据库日期",""+allist.get(i).getpostTime().substring(0,10));
                       //     Log.d("日历日期",""+dayStr);
                            //判断当前日期与数据库日期一致，则把数据加入filtered中
                            if(dayStr.length() < 11){
                                String str1=dayStr.substring(0,4);
                                String str2=dayStr.substring(5);
                                String str3=str2.substring(0,2);
                                String str4=dayStr.substring(8);
                                String str5=str4.substring(0,str4.length()-1);
                                String str6 = str1+"-"+str3+"-0"+str5;
                                date = str6;
                                if(select.equals(date)){
                                    filtered_itemList.add(allist.get(i));
                                }

                            }else{
                                String str1=dayStr.substring(0,4);
                                String str2=dayStr.substring(5);
                                String str3=str2.substring(0,2);
                                String str4=dayStr.substring(8);
                                String str5=str4.substring(0,str4.length()-1);
                                String str6 = str1+"-"+str3+"-"+str5;
                                date = str6;
                                if(select.equals(date)){
                                    filtered_itemList.add(allist.get(i));
                                }
                            }
                        }
                        if(filtered_itemList != null && !filtered_itemList.isEmpty()){
                            List<HashMap<String, Object>> data = new ArrayList<>();
                            for (Notice item : filtered_itemList) {
                                HashMap<String, Object> it = new HashMap<String, Object>();
                                it.put("title", item.getTitle());
                                it.put("content",item.getContent());
                                data.add(it);
                            }
                            message.what = 0x11;
                            message.obj = data;
                        }else{
                            message.what = 0x12;
                            message.obj = "查询结果为空";
                        }

                        handler.sendMessage(message);
                    }
                }).start();
            }
        });
    }

    //设置点击事件
    public void onClick(View v) {
        switch (v.getId()) {
            //显示所有日程标题列表
            case R.id.btn_all:
                Intent intent = new Intent(getActivity(), All_itemsActivity.class);
                startActivity(intent);
                break;

            case R.id.btn_current:
                //获取当前日期
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");// HH:mm:ss
                final Date date = new Date(System.currentTimeMillis());
                Log.d("当前日期",""+simpleDateFormat.format(date));
              //  Toast.makeText(getActivity(), simpleDateFormat.format(date), Toast.LENGTH_LONG).show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message message = handler.obtainMessage();
                  //      Filter_item_Dao item_dao = new Filter_item_Dao();
                  //      List<Filtered_item> clist = item_dao.findCurrent();
                        HttpConnect con = new HttpConnect();
                        List<Notice> clist = con.request("FindToday");
                        if (clist != null && ! clist.isEmpty()) {
                            List<HashMap<String, Object>> data = new ArrayList<>();
                            for (Notice item : clist) {
                                HashMap<String, Object> it = new HashMap<String, Object>();
                                it.put("title", item.getTitle());
                                it.put("content",item.getContent());
                                data.add(it);
                            }
                            message.what = 0x11;
                            message.obj = data;
                            filtered_itemList = clist;
                        } else {
                            message.what = 0x12;
                            message.obj = "查询结果为空";
                        }
                        handler.sendMessage(message);
                     //   clist.clear_notice();
                    }
                }).start();
                break;


            case R.id.btn_cancel:
                listView.setAdapter(null);
                filtered_itemList.clear();
                Toast.makeText(getActivity(),"列表已清空",Toast.LENGTH_LONG).show();
                break;
        }

    }
    //查询某月的数据并返回每天的记录数
    public void init_data(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = handler.obtainMessage();
             //   Filter_item_Dao filter = new Filter_item_Dao();
             //   List<Integer> monthlist = filter.findMonth();
                // Log.d("", "2019-08 " + monthlist);
                SimpleDateFormat sformat = new SimpleDateFormat("yyyy-MM-dd");// HH:mm:ss
                Date date = new Date(System.currentTimeMillis());
                String time = sformat.format(date);
                Log.d("事件数对应日期",""+time.substring(0,7));
                HttpConnect con = new HttpConnect();
                List count = con.count(time.substring(0,7));
                if (count != null && ! count.isEmpty()){
                    message.what = 0x13;
                    message.obj = count;
                }else {
                    message.what = 0x12;
                    message.obj = "查询结果为空";
                }
                handler.sendMessage(message);
            }
        }).start();
    }
}
