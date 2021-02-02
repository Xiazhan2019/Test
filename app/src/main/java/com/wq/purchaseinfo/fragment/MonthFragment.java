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

import com.wq.purchaseinfo.entity.DayNum;
import com.wq.purchaseinfo.view.Month_Calendar;
import com.wq.purchaseinfo.activity.CalendarActivity;
import com.wq.purchaseinfo.R;
import com.wq.purchaseinfo.activity.NoticeActivity;
import com.wq.purchaseinfo.entity.Notice;
import com.wq.purchaseinfo.net.HttpConnect;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MonthFragment extends Fragment{

    private Month_Calendar cal;
    private ListView listView;
    private List<Notice> filtered_itemList = new ArrayList<Notice>();
    private List<DayNum> dayNumList = new ArrayList<>();
    private Calendar curDtate = Calendar.getInstance();
    List<CalendarActivity.DayFinish> list = new ArrayList<>();

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            switch (msg.what) {
                case 0x11:
                    //创建SimpleAdapter适配器将数据绑定到item显示控件上
                    SimpleAdapter adapter = new SimpleAdapter(getActivity(), (List<? extends Map<String, ?>>) msg.obj, R.layout.allitem_title_view,
                            new String[]{"title"}, new int[]{R.id.title});
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
                                for(int i = 0; i<filtered_itemList.size(); i++){
                                    String element = filtered_itemList.get(i).getTitle();
                                    if(element == text){
                                        content = filtered_itemList.get(i).getContent();
                                        NoticeActivity.actionStart(getActivity(),text,content);
                                    }
                                }
                                filtered_itemList.clear();
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
                    Log.e("wq==", ""+dayNumList.get(0).getDate().substring(8,10));
                    int count = Integer.parseInt(dayNumList.get(0).getDate().substring(8,10));
                    int j = 0;
                    int size = Integer.parseInt(dayNumList.get(dayNumList.size()-1).getDate().substring(8,10));
                    for (int i = count ; i <= size; i++) {
                        if(i == Integer.parseInt(dayNumList.get(j).getDate().substring(8,10))){
                            list.add(j, new CalendarActivity.DayFinish(i, dayNumList.get(j).count, 0));
                        }else{
                            i = Integer.parseInt(dayNumList.get(j).getDate().substring(8,10));
                            list.add(j, new CalendarActivity.DayFinish(i, 0, 0));
                        }
                        j = j+1;
                    }
                    Log.d("message" , " " + list);
                    int year = curDtate.get(Calendar.YEAR);//当前年份
                    int month = curDtate.get(Calendar.MONTH)+1;//当前月份
                    cal.setRenwu(year+"年"+month+"月", list);
                    break;
            }

        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_month,null);
        findView(view);
        return view;
    }
    //初始化view
    private void findView(View view){
        cal = (Month_Calendar)view.findViewById(R.id.cal);
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
 //               Toast.makeText(getActivity() , "点击了标题："+monthStr, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onWeekClick(int weekIndex, String weekStr) {
  //              Toast.makeText(getActivity(), "点击了星期："+weekStr, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDayClick(int day, final String dayStr, CalendarActivity.DayFinish finish) {
 //               Toast.makeText(getActivity(), "点击了日期："+dayStr, Toast.LENGTH_SHORT).show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        filtered_itemList.clear();
                        Message message = handler.obtainMessage();
                        HttpConnect con = new HttpConnect();
                        List<Notice> all_list = con.request("FindAll");
                        String date;
                        for(int i = 0;i < all_list.size(); i++){
                            String select = all_list.get(i).getpostTime().substring(0,10);
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
                                    filtered_itemList.add(all_list.get(i));
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
                                    filtered_itemList.add(all_list.get(i));
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
    //查询某月的数据并返回每天的记录数
    public void init_data(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = handler.obtainMessage();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");// HH:mm:ss
                Date date = new Date(System.currentTimeMillis());
                String time = format.format(date);
                Log.d("事件数对应日期",""+time.substring(0,7));
                HttpConnect con = new HttpConnect();
                List<DayNum> count = con.count(time.substring(0,7));
                dayNumList = count;
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
