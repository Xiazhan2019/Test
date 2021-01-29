package com.wq.purchaseinfo.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.wq.purchaseinfo.R;
import com.wq.purchaseinfo.utils.ShareUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    //注册按钮
    private Button btn_registered;
    private EditText et_name;
    private EditText et_password;
    private Button btnLogin;
    private CheckBox keep_password;
    private TextView tv_forget;

    //登录状态
    public static final int SHOW_RESPONSE=1;
    public static final int xx12 = 0;
    private Handler handler=new Handler() {
        public void handleMessage(Message msg)
        {
            switch (msg.what){
                case SHOW_RESPONSE:
                    String response=(String)msg.obj;
                    Toast.makeText(LoginActivity.this, response, Toast.LENGTH_SHORT).show();
                    break;
                case xx12:
                    Toast.makeText(LoginActivity.this, "登陆失败", Toast.LENGTH_SHORT).show();
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }
    private void initView() {
        btn_registered = (Button) findViewById(R.id.btn_registered);
        btn_registered.setOnClickListener(this);
        et_name = (EditText) findViewById(R.id.et_name);
        et_password = (EditText)findViewById(R.id.et_password);
        btnLogin = (Button)findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);
        keep_password = (CheckBox) findViewById(R.id.keep_password);
        tv_forget = (TextView) findViewById(R.id.tv_forget);
        tv_forget.setOnClickListener(this);


        //默认不记住密码
        boolean isKeep = ShareUtils.getBoolean(this, "keeppass", false);
        keep_password.setChecked(isKeep);
        if(isKeep){
            String name = ShareUtils.getString(this, "name", "");
            String password = ShareUtils.getString(this, "password", "");
            et_name.setText(name);
            et_password.setText(password);
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btn_registered:
                startActivity(new Intent(this,RegisteredActivity.class));
                break;
            case R.id.btnLogin:
                //1.获取输入框的值
                String name = et_name.getText().toString().trim();
                String password = et_password.getText().toString().trim();
                //2.判断是否为空
                if (!TextUtils.isEmpty(name) & !TextUtils.isEmpty(password)) {
                    SendByHttpClient(name,password);
                    SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
                    sp.edit().putString("username", name)
                            .putString("password", password)
                            .apply();
                    Log.d("保存登录信息",sp.getString("username", null));
                } else {
                    Toast.makeText(this, "输入框不能为空", Toast.LENGTH_SHORT).show();
                }
                LoginActivity.this.finish();
                break;
            case R.id.tv_forget:
            //    startActivity(new Intent(this,ForgetPasswordActivity.class));
                break;
        }
    }
    //假设我现在输入用户名和密码，但是我不点击登录，而是直接退出了
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //保存状态
        ShareUtils.putBoolean(this, "keeppass", keep_password.isChecked());
        //是否记住密码
        if (keep_password.isChecked()) {
            //记住用户名和密码
            ShareUtils.putString(this, "name", et_name.getText().toString().trim());
            ShareUtils.putString(this, "password", et_password.getText().toString().trim());
        } else {
            ShareUtils.deleShare(this, "name");
            ShareUtils.deleShare(this, "password");
        }
    }

    public void SendByHttpClient(final String name, final String pw){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpClient httpclient=new DefaultHttpClient();
                    HttpPost httpPost=new HttpPost("http://192.168.254.151:8080/HttpClientDemo/Login");
                    List<NameValuePair> params=new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("NAME",name));
                    params.add(new BasicNameValuePair("PW",pw));
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

                        Intent intent = new Intent(LoginActivity.this, FirstActivity.class);
                        startActivity(intent);
                    }
                    else{
                        Message message=new Message();
                        message.what=xx12;
                        message.obj="登陆失败";
                        handler.sendMessage(message);

                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
