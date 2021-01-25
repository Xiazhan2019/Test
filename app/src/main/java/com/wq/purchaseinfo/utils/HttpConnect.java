package com.wq.purchaseinfo.utils;

import android.util.Log;

import com.wq.purchaseinfo.entity.Notice;
import com.wq.purchaseinfo.entity.User;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HttpConnect {
    private String url = "http://10.121.31.103:8080/HttpClientDemo/";

    //根据访问路径返回信息
    public List<Notice> request(String s){
        List<Notice> noticeList = new ArrayList<>();
        HttpClient httpclient=new DefaultHttpClient();
        HttpPost httpPost=new HttpPost(url+s);
        try {
            HttpResponse httpResponse= httpclient.execute(httpPost);
            Log.d("访问状态",""+httpResponse.getStatusLine().getStatusCode());
            if(httpResponse.getStatusLine().getStatusCode()==200)
            {
                HttpEntity entity=httpResponse.getEntity();
                String response= EntityUtils.toString(entity,"utf-8");
                try{
                    JSONArray jsonArray=new JSONArray(response);
                    for(int i=0;i<jsonArray.length();i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String id = jsonObject.getString("item_id");
                        String title = jsonObject.getString("item_title");
                        String agent = jsonObject.getString("item_agent");
                        String postTime = jsonObject.getString("item_postTime");
                        String content = jsonObject.getString("filter_content");
                        String type = jsonObject.getString("result_type");
                        String time = jsonObject.getString("log_time");
                        Notice notices = new Notice(id, title, agent,postTime,content,type,time);
                        noticeList.add(notices);
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
            else{
                Log.d("W：","访问失败,请重新查看");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return noticeList;
    }
   //根据特定文本信息请求并返回消息信息
    public List<Notice> SendByHttpClient(final String text){
        List<Notice> noticeList = new ArrayList<>();
        HttpClient httpclient=new DefaultHttpClient();
        HttpPost httpPost=new HttpPost("http://10.121.31.103:8080/HttpClientDemo/FindID");
        List<NameValuePair> params=new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("ID",text));
        try {
          //  HttpResponse httpResponse= httpclient.execute(httpPost);
          //  Log.d("访问状态",""+httpResponse.getStatusLine().getStatusCode());
            final UrlEncodedFormEntity entity=new UrlEncodedFormEntity(params,"utf-8");
            httpPost.setEntity(entity);
            HttpResponse httpResponse= httpclient.execute(httpPost);
            if(httpResponse.getStatusLine().getStatusCode()==200)
            {
                HttpEntity entity1=httpResponse.getEntity();
                String response= EntityUtils.toString(entity1,"utf-8");
                try{
                    JSONArray jsonArray=new JSONArray(response);
                    for(int i=0;i<jsonArray.length();i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String id = jsonObject.getString("item_id");
                        String title = jsonObject.getString("item_title");
                        String agent = jsonObject.getString("item_agent");
                        String postTime = jsonObject.getString("item_postTime");
                        String content = jsonObject.getString("filter_content");
                        String type = jsonObject.getString("result_type");
                        String time = jsonObject.getString("log_time");
                        Notice notices = new Notice(id, title, agent,postTime,content,type,time);
                        noticeList.add(notices);
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
            else{
                Log.d("W：","访问失败,请重新查看");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return noticeList;
    }

    public List<Integer> count(final String text){
        List<Integer> result = new ArrayList<>();
        HttpClient httpclient=new DefaultHttpClient();
        HttpPost httpPost=new HttpPost("http://10.121.31.103:8080/HttpClientDemo/FindCount");
        List<NameValuePair> params=new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("ID",text));
        try {
            //  HttpResponse httpResponse= httpclient.execute(httpPost);
            //  Log.d("访问状态",""+httpResponse.getStatusLine().getStatusCode());
            final UrlEncodedFormEntity entity=new UrlEncodedFormEntity(params,"utf-8");
            httpPost.setEntity(entity);
            HttpResponse httpResponse= httpclient.execute(httpPost);
            if(httpResponse.getStatusLine().getStatusCode()==200)
            {
                HttpEntity entity1=httpResponse.getEntity();
                String response= EntityUtils.toString(entity1,"utf-8");
                Log.d("返回的count列表",""+response);
                //接收字符串并转化为列表
                List<String> lis = Arrays.asList(response.split(","));
                if(lis.isEmpty()){
                    result.add(0);
                }
                else{
                    for (String  s: lis) {
                        int i =Integer.parseInt(s);
                        result.add(i);
                    }
                }
            }
            else{
                Log.d("W：","访问失败,请重新查看");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    //请求并返回用户信息
    public List<User> SendUser(final String text1, final String text2){
        List<User> userList = new ArrayList<>();
        HttpClient httpclient=new DefaultHttpClient();
        HttpPost httpPost=new HttpPost("http://10.121.31.103:8080/HttpClientDemo/User");
        List<NameValuePair> params=new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("Username",text1));
        params.add(new BasicNameValuePair("Password",text2));
        try {
            //  HttpResponse httpResponse= httpclient.execute(httpPost);
            //  Log.d("访问状态",""+httpResponse.getStatusLine().getStatusCode());
            final UrlEncodedFormEntity entity=new UrlEncodedFormEntity(params,"utf-8");
            httpPost.setEntity(entity);
            HttpResponse httpResponse= httpclient.execute(httpPost);
            if(httpResponse.getStatusLine().getStatusCode()==200)
            {
                HttpEntity entity1=httpResponse.getEntity();
                String response= EntityUtils.toString(entity1,"utf-8");
                try{
                    JSONArray jsonArray=new JSONArray(response);
                    for(int i=0;i<jsonArray.length();i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Integer userid = jsonObject.getInt("userid");
                        String username = jsonObject.getString("username");
                        String password = jsonObject.getString("password");
                        String gender = jsonObject.getString("gender");
                        String age = jsonObject.getString("age");
                        String desc = jsonObject.getString("desc");
                        User users = new User(userid, username, password,gender,age,desc);
                        userList.add(users);
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
            else{
                Log.d("W：","访问失败,请重新查看");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return userList;
    }

    //返回用户关注的信息
    public List<Notice> SendFocus(final String text){
        List<Notice> noticeList = new ArrayList<>();
        HttpClient httpclient=new DefaultHttpClient();
        HttpPost httpPost=new HttpPost("http://10.121.31.103:8080/HttpClientDemo/MyFocus");
        List<NameValuePair> params=new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("NAME",text));
        try {
            final UrlEncodedFormEntity entity=new UrlEncodedFormEntity(params,"utf-8");
            httpPost.setEntity(entity);
            HttpResponse httpResponse= httpclient.execute(httpPost);
            if(httpResponse.getStatusLine().getStatusCode()==200)
            {
                HttpEntity entity1=httpResponse.getEntity();
                String response= EntityUtils.toString(entity1,"utf-8");
                try{
                    JSONArray jsonArray=new JSONArray(response);
                    for(int i=0;i<jsonArray.length();i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String id = jsonObject.getString("item_id");
                        String title = jsonObject.getString("item_title");
                        String agent = jsonObject.getString("item_agent");
                        String postTime = jsonObject.getString("item_postTime");
                        String content = jsonObject.getString("filter_content");
                        String type = jsonObject.getString("result_type");
                        String time = jsonObject.getString("log_time");
                        Notice notices = new Notice(id, title, agent,postTime,content,type,time);
                        noticeList.add(notices);
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
            else{
                Log.d("W：","访问失败,请重新查看");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return noticeList;
    }
}
