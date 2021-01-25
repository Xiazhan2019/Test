package com.wq.purchaseinfo.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wq.purchaseinfo.R;
import com.wq.purchaseinfo.entity.Event;

import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AddEventActivity extends AppCompatActivity{

    public static final String CONTENT="content";
    public static final String TIME="time";
    private String time;
    private EditText content;
    private TextView showTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        content=(EditText) findViewById(R.id.add_content);
        showTime=(TextView) findViewById(R.id.time_show);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){//显示系统返回按钮
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //取得内容和时间
        Intent intent=getIntent();
        time=intent.getStringExtra(TIME);
        String showContent=intent.getStringExtra(CONTENT);
        showTime.setText(time);
        content.setText(showContent);
        if(showContent!=null) {
            content.setSelection(showContent.length());//将光标移动到文本最后
        }
    }

    //配置菜单项设置点击事件
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.save_content:
                //判断当前操作是新增还是修改
                if(time!=null){
                    String inputText=content.getText().toString();
                    Event event=new Event();
                    event.setContent(inputText);
                    event.updateAll("time=?",time);
                    finish();
                    break;
                }else {
                    //取得新增记录时的系统时间
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                Date date=new Date(System.currentTimeMillis());
                String inputText=content.getText().toString();
                Event event=new Event();
                event.setContent(inputText);
                event.setTime(simpleDateFormat.format(date));
                event.save();
                Toast.makeText(this,"保存成功",Toast.LENGTH_SHORT).show();
                finish();//操作完成结束当前活动
                break;}

            case R.id.delete:

                //删除操作
                String deleteContent=content.getText().toString();
                DataSupport.deleteAll(Event.class,"content=?",deleteContent);
                finish();
                break;

            case android.R.id.home://一定添加android还有下面这行代码，我当时为这搞了半天
                onBackPressed();
                return true;
                default:
        }
        return true;
    }
}
