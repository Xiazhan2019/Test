package com.wq.purchaseinfo.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.wq.purchaseinfo.R;
import com.wq.purchaseinfo.adapter.SearchListAdapter;
import com.wq.purchaseinfo.entity.Notice;
import com.wq.purchaseinfo.entity.Trace;
import com.wq.purchaseinfo.net.HttpConnect;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


public class SearchActivity extends BaseActivity implements View.OnClickListener {

    private ListView lvTrace;
    private List<Trace> traceList = new ArrayList<>();
    private SearchListAdapter adapter;
    private Button btn_clear;

    //编号文本框
    private EditText number;
    private Button search;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            switch (msg.what) {
                case 0x11:
                    adapter = new SearchListAdapter(SearchActivity.this, (List<Trace>) msg.obj);
                    lvTrace.setAdapter(adapter);
                    break;
                case 0x12:
                    Toast.makeText(SearchActivity.this, (String) msg.obj, Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        lvTrace = (ListView) findViewById(R.id.lvTrace);
        number = (EditText) findViewById(R.id.number);
        search = (Button) findViewById(R.id.search);
        btn_clear = (Button) findViewById(R.id.clear);
        search.setOnClickListener(this);
        btn_clear.setOnClickListener(this);
    }


    //判断字符串是否含有中文
    public boolean isChinese(String str){
        for (int i = 0; i < str.length(); i++) {  //遍历所有字符
            char ch = str.charAt(i);
            if(ch >= 0x4E00 && ch <= 0x9FA5){  //中文在unicode编码中所在的区间为0x4E00-0x9FA5
                return true;  //不在这个区间，说明不是中文字符，返回false
            }
        }
        return false;  //全部在中文区间，说明全部是中文字符，返回true
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //获得输入框内容
                        String inputText = number.getText().toString();
                        Log.d("文本",""+inputText);
                        Log.d("中文"," "+isChinese(""+inputText));
                        Message message = handler.obtainMessage();
                        HttpConnect con = new HttpConnect();
                        List<Notice> idlist = con.SendByHttpClient(inputText);
                        for (Notice item : idlist) {
                            traceList.add(new Trace("项目名称", "" + item.getTitle()));
                            traceList.add(new Trace("项目编码", "" + item.getId()));
                            traceList.add(new Trace("开始日期", "" + item.getpostTime()));
                            traceList.add(new Trace("结束日期", "" + item.getTime()));
                            traceList.add(new Trace("招标状态", "" + item.getType()));
                        }
                        if(traceList != null && !traceList.isEmpty()){
                            message.what = 0x11;
                            message.obj = traceList;
                        } else{
                            message.what = 0x12;
                            message.obj = "输入信息不存在，查询失败";
                        }
                        handler.sendMessage(message);
                    }
                }).start();
                break;
            case R.id.clear:
                lvTrace.setAdapter(null);
                traceList.clear();
                Toast.makeText(SearchActivity.this,"列表已清空",Toast.LENGTH_LONG).show();
                break;
        }
    }
}
