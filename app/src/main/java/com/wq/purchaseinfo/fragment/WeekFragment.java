package com.wq.purchaseinfo.fragment;


import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.wq.purchaseinfo.R;
import com.wq.purchaseinfo.view.WeekCalendar;
import com.wq.purchaseinfo.activity.NoticeActivity;
import com.wq.purchaseinfo.entity.Notice;
import com.wq.purchaseinfo.listener.DateSelectListener;
import com.wq.purchaseinfo.listener.GetViewHelper;
import com.wq.purchaseinfo.listener.WeekChangeListener;
import com.wq.purchaseinfo.utils.CalendarUtil;
import com.wq.purchaseinfo.net.HttpConnect;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class WeekFragment extends Fragment {
    private TextView tvSelectDate;
    private TextView tvWeekChange;
    private GridView gridView;
    private List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();;
    Map<String, Object> map = new HashMap<String, Object>();
    private SimpleAdapter adapter;
    private ListView listView;
    private List<Notice> filtered_itemList = new ArrayList<Notice>();
    private List<DateTime> eventDates;
    private WeekCalendar weekCalendar;
    private TextView currentFirstDay;
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
                                listView.setAdapter(null);
                            }
                        }
                    });
                    break;
                case 0x12:
                    listView.setAdapter(null);
                    Toast.makeText(getActivity(), (String) msg.obj, Toast.LENGTH_LONG).show();
                    break;
                case 0x13:
//                    dataList.add((Map<String, Object>) msg.obj);
//                    String[] from={"text","text"};
//                    int[] to={R.id.text, R.id.text};
//                    adapter=new SimpleAdapter(getActivity(), dataList, R.layout.gridview_item, from, to);
//                    gridView.setAdapter(adapter);
//                    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                        @Override
//                        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//                            AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());
//                            builder.setTitle("提示").setMessage(dataList.get(arg2).get("text").toString()).create().show();
//                        }
//                    });
                    break;
            }

        }
    };
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_week, null);
        findView(view);
        return view;
    }
    public void findView(View view){
        tvSelectDate = (TextView) view.findViewById(R.id.tv_select_date);
        tvWeekChange = (TextView) view.findViewById(R.id.tv_week_change);
        listView = (ListView) view.findViewById(R.id.data_view);
        currentFirstDay = (TextView)view.findViewById(R.id.tv_current_firstday);
        eventDates = new ArrayList<>();
        gridView = (GridView) view.findViewById(R.id.grid_view);
//        //初始化数据
//        initData();
//        String[] from={"text","text"};
//        int[] to={R.id.text,R.id.text};
//        adapter=new SimpleAdapter(getActivity(), dataList, R.layout.gridview_item, from, to);
//        gridView.setAdapter(adapter);
//        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//                AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());
//                builder.setTitle("提示").setMessage(dataList.get(arg2).get("text").toString()).create().show();
//            }
//        });

        weekCalendar = (WeekCalendar) view.findViewById(R.id.week_calendar);
        weekCalendar.setGetViewHelper(new GetViewHelper() {
            @Override
            public View getDayView(int position, View convertView, ViewGroup parent, DateTime dateTime, boolean select) {
                if(convertView == null){
                    convertView = LayoutInflater.from(getActivity()).inflate(R.layout.item_day, parent, false);
                }
                TextView tvDay = (TextView) convertView.findViewById(R.id.tv_day);
                tvDay.setText(dateTime.toString("d"));
                if(CalendarUtil.isToday(dateTime) && select){
                    tvDay.setTextColor(Color.WHITE);
                    tvDay.setBackgroundResource(R.drawable.circular_blue);
                } else if(CalendarUtil.isToday(dateTime)){
                    tvDay.setTextColor(getResources().getColor(R.color.colorTodayText));
                    tvDay.setBackgroundColor(Color.TRANSPARENT);
                } else if(select){
                    tvDay.setTextColor(Color.WHITE);
                    tvDay.setBackgroundResource(R.drawable.circular_blue);
                } else {
                    tvDay.setTextColor(Color.BLACK);
                    tvDay.setBackgroundColor(Color.TRANSPARENT);
                }

                ImageView ivPoint = (ImageView) convertView.findViewById(R.id.iv_point);
                ivPoint.setVisibility(View.GONE);
                for (DateTime d : eventDates) {
                    if(CalendarUtil.isSameDay(d, dateTime)){
                        ivPoint.setVisibility(View.VISIBLE);
                        break;
                    }
                }
                return convertView;
            }

            @Override
            public View getWeekView(int position, View convertView, ViewGroup parent, String week) {
                if(convertView == null){
                    convertView = LayoutInflater.from(getActivity()).inflate(R.layout.item_week, parent, false);
                }
                TextView tvWeek = (TextView) convertView.findViewById(R.id.tv_week);
                tvWeek.setText(week);
                if(position == 0 || position == 6){
                    tvWeek.setTextColor(getResources().getColor(R.color.colorAccent));
                }
                return convertView;
            }
        });

        weekCalendar.setDateSelectListener(new DateSelectListener() {
            @Override
            public void onDateSelect(DateTime selectDate) {
                String text = "你选择的日期是：" + selectDate.toString("yyyy-MM-dd");
                tvSelectDate.setText(text);
                final String dayStr = selectDate.toString("yyyy-MM-dd");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        List<Notice> itemList = new ArrayList<Notice>();
                        Message message = handler.obtainMessage();
                        HttpConnect con = new HttpConnect();
                        List<Notice> allist = con.request("FindAll");
                        for(int i=0;i <allist.size();i++){
                            String select = allist.get(i).getpostTime().substring(0,10);
                            //判断当前日期与数据库日期一致，则把数据加入filtered中
                           if(select.equals(dayStr)){
                                itemList.add(allist.get(i));
                           }
                        }

                        if(itemList != null && !itemList.isEmpty()){
                            List<HashMap<String, Object>> data = new ArrayList<>();
                            filtered_itemList = itemList;
                            for (Notice item : itemList) {
                                HashMap<String, Object> it = new HashMap<String, Object>();
                                it.put("title", item.getTitle());
                                it.put("content",item.getContent());
                                data.add(it);
                            }
                            message.what = 0x11;
                            message.obj = data;
                        }else{
                            filtered_itemList = null;
                            message.what = 0x12;
                            message.obj = "查询结果为空";
                        }
                        handler.sendMessage(message);
                    }
                }).start();
            }
        });

        weekCalendar.setWeekChangedListener(new WeekChangeListener() {
            @Override
            public void onWeekChanged(DateTime firstDayOfWeek) {
                String text = "本周第一天:" + firstDayOfWeek.toString("yyyy年M月d日")
                        + ",本周最后一天:" + new DateTime(firstDayOfWeek).plusDays(6).toString("yyyy年M月d日");
                tvWeekChange.setText(text);
//                dataList.clear();
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        HttpConnect con = new HttpConnect();
//                        Log.e("测试==", ""+weekCalendar.getCurrentFirstDay().toString("yyyy-MM-dd"));
//                        List<DayNum> dayNumList = con.count(weekCalendar.getCurrentFirstDay().toString("yyyy-MM-dd"));
//                        for(int i = 0; i < 7; i++){
//                            map.put("text", dayNumList.get(i).getCount());
//                            dataList.add(map);
//                        }
//                        Message message = handler.obtainMessage();
//                        message.what = 0x13;
//                        message.obj = dataList;
//                        handler.sendMessage(message);
//                    }
//                }).start();
            }
        });
    }

//    void initData() {
//        //日期下的文字
//        String name[]={"0","0","0","0","0","0","0"};
//        for (int i = 0; i <name.length; i++) {
//            map.put("text",name[i]);
//            dataList.add(map);
//        }
//        map.clear();
//    }
}

