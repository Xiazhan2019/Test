package com.wq.purchaseinfo.net;

import android.util.Log;

import com.wq.purchaseinfo.entity.DayNum;
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
import java.util.List;

public class HttpConnect {
//    private String url = "http://192.168.254.151:8080/HttpClientDemo/";
    private String url = "http://192.168.254.151:8080/HttpClientDemo/";
    private HttpClient httpclient = new DefaultHttpClient();
    List<NameValuePair> params = new ArrayList<NameValuePair>();
    //根据访问路径返回信息
    public List<Notice> request(String s){
        List<Notice> noticeList = new ArrayList<>();
        HttpPost httpPost = new HttpPost(url+s);
        try {
            HttpResponse httpResponse = httpclient.execute(httpPost);
            Log.d("访问状态",""+httpResponse.getStatusLine().getStatusCode());
            if(httpResponse.getStatusLine().getStatusCode()==200)
            {
                HttpEntity entity=httpResponse.getEntity();
                String response= EntityUtils.toString(entity,"utf-8");
                noticeList = common(response);
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
        HttpPost httpPost=new HttpPost(url+"FindID");
        params.add(new BasicNameValuePair("ID",text));
        try {
            final UrlEncodedFormEntity entity=new UrlEncodedFormEntity(params,"utf-8");
            httpPost.setEntity(entity);
            HttpResponse httpResponse= httpclient.execute(httpPost);
            if(httpResponse.getStatusLine().getStatusCode()==200)
            {
                HttpEntity entity1=httpResponse.getEntity();
                String response= EntityUtils.toString(entity1,"utf-8");
                noticeList = common(response);
            }
            else{
                Log.d("W：","访问失败,请重新查看");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return noticeList;
    }

//请求本月每天的数量
    public List<DayNum> count(final String text){
        List<DayNum> result = new ArrayList<>();
        HttpPost httpPost = new HttpPost(url+"FindCount");
        params.add(new BasicNameValuePair("ID",text));
        try {
            final UrlEncodedFormEntity entity=new UrlEncodedFormEntity(params,"utf-8");
            httpPost.setEntity(entity);
            HttpResponse httpResponse= httpclient.execute(httpPost);
            if(httpResponse.getStatusLine().getStatusCode()==200)
            {
                HttpEntity entity1 = httpResponse.getEntity();
                String response= EntityUtils.toString(entity1,"utf-8");
//                Log.e("CJY==返回的count列表",""+response.isEmpty());
//                Log.e("CJY==返回的count列表",""+response.split(",").length);
                //接收日期数量
                try{
                    JSONArray jsonArray=new JSONArray(response);
                    for(int i=0;i < jsonArray.length();i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int count = Integer.parseInt(jsonObject.getString("number"));
                        String date = jsonObject.getString("item_postTime");
                        DayNum dayNum = new DayNum(count, date);
                        result.add(dayNum);
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
        return result;
    }

    //请求并返回个人中心信息
    public List<User> SendUser(final String text1, final String text2){
        List<User> userList = new ArrayList<>();
        HttpPost httpPost=new HttpPost(url+"User");
        params.add(new BasicNameValuePair("Username",text1));
        params.add(new BasicNameValuePair("Password",text2));
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
        HttpPost httpPost = new HttpPost(url+"MyFocus");
        params.add(new BasicNameValuePair("NAME",text));
        try {
            final UrlEncodedFormEntity entity=new UrlEncodedFormEntity(params,"utf-8");
            httpPost.setEntity(entity);
            HttpResponse httpResponse= httpclient.execute(httpPost);
            if(httpResponse.getStatusLine().getStatusCode()==200)
            {
                HttpEntity entity1=httpResponse.getEntity();
                String response= EntityUtils.toString(entity1,"utf-8");
                noticeList = common(response);
            }
            else{
                Log.d("W：","访问失败,请重新查看");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return noticeList;
    }

    public List<Notice> common(String response ){
        List<Notice> noticeList = new ArrayList<>();
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
        return noticeList;
    }

    //修改 用户信息
    public String EditUser(final String username, final String age, final String gender, final String desc, final String password){
        try {
            HttpPost httpPost=new HttpPost(url+"ModifyUser");
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
                return response;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //取消关注
    public String SendByCancel(final String username, final String title){
        try {
            HttpPost httpPost = new HttpPost(url+"Cancel");
            params.add(new BasicNameValuePair("NAME", username));
            params.add(new BasicNameValuePair("TITLE", title));
            final UrlEncodedFormEntity entity=new UrlEncodedFormEntity(params,"utf-8");
            httpPost.setEntity(entity);
            HttpResponse httpResponse= httpclient.execute(httpPost);
            if(httpResponse.getStatusLine().getStatusCode()==200)
            {
                HttpEntity entity1=httpResponse.getEntity();
                String response= EntityUtils.toString(entity1, "utf-8");
                return response;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //添加关注
    public String SendByFocus(final String username, final String title){
        try {
            HttpPost httpPost = new HttpPost(url+"focus");
            params.add(new BasicNameValuePair("NAME", username));
            params.add(new BasicNameValuePair("TITLE", title));
            final UrlEncodedFormEntity entity=new UrlEncodedFormEntity(params,"utf-8");
            httpPost.setEntity(entity);
            HttpResponse httpResponse= httpclient.execute(httpPost);
            if(httpResponse.getStatusLine().getStatusCode()==200)
            {
                HttpEntity entity1=httpResponse.getEntity();
                String response= EntityUtils.toString(entity1, "utf-8");
                return response;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //返回用户关注的信息
    public List<Notice> Recommend(final String text){
        List<Notice> noticeList = new ArrayList<>();
        HttpPost httpPost = new HttpPost(url+"Recommend");
        params.add(new BasicNameValuePair("NAME",text));
        try {
            final UrlEncodedFormEntity entity=new UrlEncodedFormEntity(params,"utf-8");
            httpPost.setEntity(entity);
            HttpResponse httpResponse= httpclient.execute(httpPost);
            if(httpResponse.getStatusLine().getStatusCode()==200)
            {
                HttpEntity entity1=httpResponse.getEntity();
                String response= EntityUtils.toString(entity1,"utf-8");
                noticeList = common(response);
            }
            else{
                Log.d("W：","访问失败,请重新查看");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return noticeList;
    }

    //添加浏览的信息
    public String BrowseInfo(final String username, final String title, final long time){
        try {
            HttpPost httpPost = new HttpPost(url+"Browse");
            params.add(new BasicNameValuePair("NAME", username));
            params.add(new BasicNameValuePair("TITLE", title));
            params.add(new BasicNameValuePair("TIME",  ""+ time));
            final UrlEncodedFormEntity entity=new UrlEncodedFormEntity(params,"utf-8");
            httpPost.setEntity(entity);
            HttpResponse httpResponse = httpclient.execute(httpPost);
            if(httpResponse.getStatusLine().getStatusCode()==200)
            {
                HttpEntity entity1=httpResponse.getEntity();
                String response= EntityUtils.toString(entity1, "utf-8");
                return response;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
