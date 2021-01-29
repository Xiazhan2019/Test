package com.wq.purchaseinfo.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.wq.purchaseinfo.R;
import com.wq.purchaseinfo.activity.NoticeActivity;
import com.wq.purchaseinfo.entity.Notice;
import com.wq.purchaseinfo.net.HttpConnect;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DayFragment extends Fragment {

    private List<Notice> filtered_itemList = new ArrayList<>();
    private ListView listView;
    private TextView text;

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
                                Toast.makeText(getActivity(), "今日无信息", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    break;
                case 0x12:
                    listView.setAdapter(null);
                    Toast.makeText(getActivity(), (String) msg.obj, Toast.LENGTH_LONG).show();
                    break;
            }

        }
    };
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_day,null);

        text = (TextView)view.findViewById(R.id.tv_date);
        listView = (ListView)view.findViewById(R.id.today_view) ;
        //获取当前日期
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");// HH:mm:ss
        Date date = new Date(System.currentTimeMillis());
        String time = simpleDateFormat.format(date);
        text.setText(time);
        initData();
        return view;
    }


    public void initData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = handler.obtainMessage();
           //     Filter_item_Dao item_dao = new Filter_item_Dao();
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
            }
        }).start();

    }
}
