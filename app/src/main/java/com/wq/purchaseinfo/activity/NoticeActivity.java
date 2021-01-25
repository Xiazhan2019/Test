package com.wq.purchaseinfo.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.wq.purchaseinfo.MainActivity;
import com.wq.purchaseinfo.R;
import com.wq.purchaseinfo.entity.Notice;
import com.wq.purchaseinfo.fragment.NoticeFragment;
import com.wq.purchaseinfo.utils.HttpConnect;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NoticeActivity extends AppCompatActivity {

    public static final int SHOW_RESPONSE=1;
    public static final int xx12 = 0;
    private Handler handler=new Handler() {
        public void handleMessage(Message msg)
        {
            switch (msg.what){
                case SHOW_RESPONSE:
                    String response=(String)msg.obj;
                    Toast.makeText(NoticeActivity.this, response, Toast.LENGTH_SHORT).show();
                    break;
                case xx12:
                    Toast.makeText(NoticeActivity.this, "关注失败", Toast.LENGTH_SHORT).show();
                default:
                    break;
            }
        }
    };


    public static  void actionStart(Context context, String filterTitle, String filterContent){
        Intent intent = new Intent(context, NoticeActivity.class);
        intent.putExtra("filters_title", filterTitle);
        intent.putExtra("filters_content", filterContent);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notice_item_view);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){//显示系统返回按钮
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        String filtersTitle = getIntent().getStringExtra("filters_title");//获取出传入的新闻标题
        String filtersContent = getIntent().getStringExtra("filters_content");//获取出传入的新闻内容
        SharedPreferences sp = getSharedPreferences("notice", Context.MODE_PRIVATE);
        sp.edit().putString("item_title", filtersTitle)
                .apply();
        NoticeFragment noticeFragment = (NoticeFragment)
                getSupportFragmentManager().findFragmentById(R.id.filters_content_fragment);
        noticeFragment.refresh(filtersTitle, filtersContent);//刷新Filtered_item_view界面
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
              //关注招标按钮，保存关联的用户名和招标信息名
                SharedPreferences sp1 = getSharedPreferences("login", Context.MODE_PRIVATE);
           //     Log.d("保存登录信息",sp1.getString("username", null));
                SharedPreferences sp2 = getSharedPreferences("notice", Context.MODE_PRIVATE);
           //     Log.d("保存登录信息",sp2.getString("item_title", null));
                SendByHttpClient(sp1.getString("username", null),sp2.getString("item_title", null));
                setNotficationDemo(sp1.getString("username", null),sp2.getString("item_title", null));
                finish();
                break;

            case R.id.delete:
             //取消招标按钮，删除关联的用户名和招标信息名
                SharedPreferences sp3 = getSharedPreferences("login", Context.MODE_PRIVATE);
                SharedPreferences sp4 = getSharedPreferences("notice", Context.MODE_PRIVATE);
                SendByCancel(sp3.getString("username", null),sp4.getString("item_title", null));
                finish();
                break;

            case android.R.id.home://一定添加android还有下面这行代码
                onBackPressed();
                finish();
                return true;
            default:
        }
        return true;
    }
//向服务器发送关注请求
    public void SendByHttpClient(final String username, final String title){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpClient httpclient=new DefaultHttpClient();
                    HttpPost httpPost=new HttpPost("http://10.121.31.103:8080/HttpClientDemo/focus");
                    List<NameValuePair> params=new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("NAME", username));
                    params.add(new BasicNameValuePair("TITLE", title));
                    final UrlEncodedFormEntity entity=new UrlEncodedFormEntity(params,"utf-8");
                    httpPost.setEntity(entity);
                    HttpResponse httpResponse= httpclient.execute(httpPost);
                    if(httpResponse.getStatusLine().getStatusCode()==200)
                    {
                        HttpEntity entity1=httpResponse.getEntity();
                        String response= EntityUtils.toString(entity1, "utf-8");
                        Message message=new Message();
                        message.what=SHOW_RESPONSE;
                        message.obj=response;
                        handler.sendMessage(message);
                    }
                    else{
                        Message message=new Message();
                        message.what=xx12;
                        handler.sendMessage(message);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
//向服务器发送取消关注请求
    public void SendByCancel(final String username, final String title){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpClient httpclient=new DefaultHttpClient();
                    HttpPost httpPost=new HttpPost("http://10.121.31.103:8080/HttpClientDemo/Cancel");
                    List<NameValuePair> params=new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("NAME", username));
                    params.add(new BasicNameValuePair("TITLE", title));
                    final UrlEncodedFormEntity entity=new UrlEncodedFormEntity(params,"utf-8");
                    httpPost.setEntity(entity);
                    HttpResponse httpResponse= httpclient.execute(httpPost);
                    if(httpResponse.getStatusLine().getStatusCode()==200)
                    {
                        HttpEntity entity1=httpResponse.getEntity();
                        String response= EntityUtils.toString(entity1, "utf-8");
                        Message message=new Message();
                        message.what=SHOW_RESPONSE;
                        message.obj=response;
                        handler.sendMessage(message);
                    }
                    else{
                        Message message=new Message();
                        message.what=xx12;
                        handler.sendMessage(message);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 默认通知栏
     * 用不同手机会有不同效果显示，需注意
     */
    private void setNotficationDemo(final String name, final String title) {
        new Thread(new Runnable() {
            @Override
            public void run() {
               //创建通知栏管理工具
                NotificationManager notificationManager = (NotificationManager) getSystemService
                        (NOTIFICATION_SERVICE);

                //实例化通知栏构造器
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(NoticeActivity.this);
                SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                //接受关注的消息数和消息标题
                HttpConnect con = new HttpConnect();
                List<Notice> noticeList = con.SendFocus(name);
                /**设置Builder*/
                if (noticeList != null) {
                    for (Notice item : noticeList) {
                   //     int i = 1;
                        mBuilder.setContentTitle("政采消息通");
                        mBuilder.setContentText(title);
                        mBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
                        mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
                        if(item.getTitle() == title){
                            //转换成系统时间的格式
                            try {
                                mBuilder.setWhen((long)dateformat.parse(item.getTime()).getTime());
                                System.out.println((long)dateformat.parse(item.getTime()).getTime());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        mBuilder.setTicker("我是测试内容");
                        mBuilder.setDefaults(Notification.DEFAULT_SOUND);//设置内容
                        Intent intent = new Intent(NoticeActivity.this, FocusActivity.class);
                        PendingIntent pIntent = PendingIntent.getActivity(NoticeActivity.this, 0, intent, 0);
                        mBuilder.setContentIntent(pIntent);
                        notificationManager.notify(1, mBuilder.build());
                    //    i ++;
                    }
                }
            }
        }).start();

    }

 /*   //按返回键退回到主界面
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

  */
}
