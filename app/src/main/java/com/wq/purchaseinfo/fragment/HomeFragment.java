package com.wq.purchaseinfo.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wq.purchaseinfo.R;
import com.wq.purchaseinfo.adapter.HomeAdapt;
import com.wq.purchaseinfo.entity.Notice;
import com.wq.purchaseinfo.net.HttpConnect;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class HomeFragment extends Fragment {
    private TextView text1, text2;
    private RecyclerView rv1, rv2;
    private List<String> data1 = new ArrayList<>();
    List<Notice> focusList = new ArrayList<>();
    List<Notice> recommendList = new ArrayList<>();
    private List<String> data2 = new ArrayList<>();
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case 0:
              //      Log.e("测试==", "" + msg.obj.toString());
                    HomeAdapt adapt1 = new HomeAdapt(getContext(), data1, (List<Notice>) msg.obj);
                    rv1.setLayoutManager(new LinearLayoutManager(getContext()));
                    rv1.setAdapter(adapt1);
                    HomeAdapt adapt2 = new HomeAdapt(getContext(), data2, recommendList);
                    rv2.setLayoutManager(new LinearLayoutManager(getContext()));
                    rv2.setAdapter(adapt2);
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_home, container, false);
        text1 = v.findViewById(R.id.text1);
        text2 = v.findViewById(R.id.text2);
        rv1 = v.findViewById(R.id.rv1_content);
        rv2 = v.findViewById(R.id.rv2_content);
        initData();
        return  v;
    }
    public void initData(){
//        for(int i = 0; i < 20; i++){
//            data2.add("记录"+i);
//        }
        new Thread(new Runnable() {
            @Override
            public void run() {

                SharedPreferences sp = getContext().getSharedPreferences("login", Context.MODE_PRIVATE);
                HttpConnect con = new HttpConnect();
                focusList = con.SendFocus(sp.getString("username",null));
                recommendList = con.Recommend(sp.getString("username",null));
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");// HH:mm:ss
                Date date = new Date(System.currentTimeMillis());
                String cTime = df.format(date);
                if(focusList != null){
                    for(Notice notice : focusList){
                        int time = 0;
                        try{
                            time = (int)(df.parse(notice.getTime()).getTime() - df.parse(cTime).getTime())/(24*60*60*1000);
                        }catch(Exception e){
                            Log.e("日期转换错误", ""+ e.getMessage());
                        }
                        data1.add(notice.getTitle()+"--剩余--"+ time +"--天结束");
                    }

                }
                if(recommendList != null){
                    for(Notice notice : recommendList){
                        data2.add(notice.getTitle());
                    }
                }

                Message message = handler.obtainMessage();
                message.what = 0;
                message.obj = focusList;
                handler.sendMessage(message);
            }
        }).start();
    }
}
