package com.wq.purchaseinfo.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.wq.purchaseinfo.MainActivity;
import com.wq.purchaseinfo.R;
import com.wq.purchaseinfo.entity.User;
import com.wq.purchaseinfo.utils.CustomDialog;
import com.wq.purchaseinfo.utils.HttpConnect;
import com.wq.purchaseinfo.utils.UtilTools;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class UserActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btn_exit_user;
    private TextView edit_user;

    private EditText et_username;
    private EditText et_sex;
    private EditText et_age;
    private EditText et_desc;

    private Button btn_update_ok;
    //圆形头像
    private CircleImageView profile_image;

    private CustomDialog dialog;

    private Button btn_camera;
    private Button btn_picture;
    private Button btn_cancel;
    //我的关注
    private TextView my_focus;

    public static final int SHOW_RESPONSE=1;
    public static final int xx12 = 0;
    private Handler handler1=new Handler() {
        public void handleMessage(Message msg)
        {
            switch (msg.what){
                case SHOW_RESPONSE:
                    String response=(String)msg.obj;
                    Toast.makeText(UserActivity.this, response, Toast.LENGTH_SHORT).show();
                    break;
                case xx12:
                    Toast.makeText(UserActivity.this, "修改失败", Toast.LENGTH_SHORT).show();
                default:
                    break;
            }
        }
    };
    private Handler handler2 = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            switch (msg.what) {
                case 0x11:
                    List<User> users = (List<User>) msg.obj;
                    for(User item: users){
                        et_username.setText(item.getUsername());
                        et_sex.setText(item.getGender());
                        et_age.setText(item.getAge());
                        et_desc.setText(item.getDesc());
                    }
                    break;
                case 0x12:
                    Toast.makeText(UserActivity.this, "false", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        initView();
        //显示返回按钮
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setTitle("个人中心");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    //初始化view
    private void initView(){
        btn_exit_user = (Button)findViewById(R.id.btn_exit_user);
        btn_exit_user.setOnClickListener(this);
        edit_user = (TextView)findViewById(R.id.edit_user);
        edit_user.setOnClickListener(this);

        et_username = (EditText)findViewById(R.id.et_username);
        et_sex = (EditText)findViewById(R.id.et_sex);
        et_age = (EditText)findViewById(R.id.et_age);
        et_desc = (EditText)findViewById(R.id.et_desc);

        btn_update_ok = (Button)findViewById(R.id.btn_update_ok);
        btn_update_ok.setOnClickListener(this);

        profile_image = (CircleImageView)findViewById(R.id.profile_image);
        profile_image.setOnClickListener(this);

        my_focus = (TextView)findViewById(R.id.my_focus);
        my_focus.setOnClickListener(this);

        UtilTools.getImageToShare(this,profile_image);
         //初始化dialog
        dialog = new CustomDialog(this,100,100,R.layout.dialog_photo,R.style.Theme_dialog, Gravity.BOTTOM,R.style.pop_anim_style);
        //屏幕外点击无效
        dialog.setCancelable(false);
        dialog.setCancelable(false);
        btn_camera = (Button)dialog.findViewById(R.id.btn_camera);
        btn_camera.setOnClickListener(this);
        btn_picture = (Button)dialog.findViewById(R.id.btn_picture);
        btn_picture.setOnClickListener(this);
        btn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(this);
        //默认是不可点击的
        setEnabled(false);
       //保存登录状态,设置具体的值
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
                Message message = handler2.obtainMessage();
                HttpConnect con = new HttpConnect();
                List<User> userlist = con.SendUser(sp.getString("username", null),sp.getString("password", null));
                if(userlist != null && ! userlist.isEmpty()){
                    message.what = 0x11;
                    message.obj = userlist;
                } else{
                    message.what = 0x12;
                    message.obj = "";
                }
                handler2.sendMessage(message);
            }
        }).start();

    }

    private void setEnabled(boolean is){
        et_username.setEnabled(is);
        et_sex.setEnabled(is);
        et_age.setEnabled(is);
        et_desc.setEnabled(is);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //退出登录
            case R.id.btn_exit_user:
                //清除缓存用户对象
                SharedPreferences sp1 = getSharedPreferences("login", Context.MODE_PRIVATE);
                sp1.edit().putString("username", null)
                        .putString("password", null)
                        .apply();
                startActivity(new Intent(this, LoginActivity.class));
                this.finish();
                break;
            case R.id.edit_user:
                setEnabled(true);
                btn_update_ok.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_update_ok:
                //1.拿到输入框的值
                String username = et_username.getText().toString();
                String sex = et_sex.getText().toString();
                String age = et_age.getText().toString();
                String desc = et_desc.getText().toString();
                //2.判断是否为空
                if(!TextUtils.isEmpty(username) & !TextUtils.isEmpty(sex) & !TextUtils.isEmpty(age)){
                    User user = new User();
                    user.setUsername(username);
                    user.setAge(age);
                    //性别
                    if(sex.equals("男")){
                        user.setGender("男");
                    }else{
                        user.setGender("女");
                    }
                    //简介
                    if(!TextUtils.isEmpty(desc)){
                        user.setDesc(desc);
                    }else{
                        user.setDesc("这个人很懒，什么都没留下");
                    }
                    SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
                    ModifyUser(user.getUsername(),user.getAge(),user.getGender(),user.getDesc(),sp.getString("password", null));
                    setEnabled(false);
                    btn_update_ok.setVisibility(View.GONE);
                }else{
                    Toast.makeText(this,"输入框不能为空",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.my_focus:
                Intent intent = new Intent(this, FocusActivity.class);
                startActivity(intent);
                break;
            case R.id.profile_image:
                dialog.show();
                break;
            case R.id.btn_camera:
                toCamera();
                break;
            case R.id.btn_picture:
                toPicture();
                break;
            case R.id.btn_cancel:
                dialog.dismiss();
                break;
        }
    }

    public static final String PHOTO_IMAGE_FILE_NAME = "fileImage_jpg";
    public static final int CAMERA_REQUEST_CODE = 100;
    public static final int IMAGE_REQUEST_CODE = 101;
    public static final int RESULT_REQUEST_CODE = 102;
    public File tempFile = null;
    //跳转相机
    private void toCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //判断内存卡是否可用，可用进行存储
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(),PHOTO_IMAGE_FILE_NAME)));
        startActivityForResult(intent,CAMERA_REQUEST_CODE);
        dialog.dismiss();
    }
//跳转相册
    private void toPicture() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != this.RESULT_CANCELED) {
            switch (requestCode) {
                //相册数据
                case IMAGE_REQUEST_CODE:
                    startPhotoZoom(data.getData());
                    break;
                //相机数据
                case CAMERA_REQUEST_CODE:
                    tempFile = new File(Environment.getExternalStorageDirectory(), PHOTO_IMAGE_FILE_NAME);
                    startPhotoZoom(Uri.fromFile(tempFile));
                    break;
                case RESULT_REQUEST_CODE:
                    //有可能点击舍弃
                    if (data != null) {
                        //拿到图片设置
                        setImageToView(data);
                        //既然已经设置了图片，我们原先的就应该删除
                        if (tempFile != null) {
                            tempFile.delete();
                        }
                    }
                    break;
            }
        }
    }
    private void startPhotoZoom(Uri uri){
         if(uri == null){
             Log.e("uri == null","");
             return;
         }
         Intent intent = new Intent("com.android.camera.action.CROP");
         intent.setDataAndType(uri,"image/*");
         //设置裁剪
        intent.putExtra("crop",true);
        //裁剪宽高比例
        intent.putExtra("aspectX",1);
        intent.putExtra("aspectY",1);
        //裁剪图片的质量
        intent.putExtra("outputX",320);
        intent.putExtra("outputY",320);
        //发送数据
        intent .putExtra("return-data",true);
        startActivityForResult(intent,RESULT_REQUEST_CODE);
    }
    //设置图片
    private void setImageToView(Intent data){
        Bundle bundle = data.getExtras();
        if(bundle != null){
            Bitmap bitmap = bundle.getParcelable("data");
            profile_image.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //保存
        UtilTools.putImageToShare(this,profile_image);
    }

    //向服务器发送修改信息
    public void ModifyUser(final String username, final String age, final String gender, final String desc, final String password){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpClient httpclient=new DefaultHttpClient();
                    HttpPost httpPost=new HttpPost("http://10.121.31.103:8080/HttpClientDemo/ModifyUser");
                    List<NameValuePair> params=new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("NAME", username));
                    params.add(new BasicNameValuePair("AGE", age));
                    params.add(new BasicNameValuePair("GENDER", gender));
                    params.add(new BasicNameValuePair("DESC", desc));
                    params.add(new BasicNameValuePair("PW", password));
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
                        handler1.sendMessage(message);
                    }
                    else{
                        Message message=new Message();
                        message.what=xx12;
                        handler1.sendMessage(message);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //按返回键退回到主界面
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home://一定添加android还有下面这行代码
                onBackPressed();
                return true;
            default:
        }
        return true;
    }
}
