package com.wq.purchaseinfo.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/*
* 工具同一类
* */
public class UtilTools {

    //设置字体
    public static void setFont(Context mContext, TextView textview) {
        Typeface fontType = Typeface.createFromAsset(mContext.getAssets(), "fonts/FONT.TTF");
        textview.setTypeface(fontType);
    }

    public static void putImageToShare(Context mContext, ImageView imageView){
        //保存
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        //第一步：将Bitmap压缩成字节数组输出流
        ByteArrayOutputStream byStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,80,byStream);
        //第二步：利用Base64将我们的字节数组输出流转换成String
        byte [] byteArry = byStream.toByteArray();
        String imgString = new  String(Base64.encodeToString(byteArry,Base64.DEFAULT));
        //第三步，将String保存shareUtils
        ShareUtils.putString(mContext,"image_title",imgString);
    }

    //拿到图片
    public static void getImageToShare(Context mContext, ImageView imageView){
        //1.拿到string
        String imgString = ShareUtils.getString(mContext,"image_title","");
        if(!imgString.equals("")){
            //2.利用Base64将我们String转换
            byte []byteArray = Base64.decode(imgString,Base64.DEFAULT);
            ByteArrayInputStream byStream = new ByteArrayInputStream(byteArray);
            //3.生成bitmap
            Bitmap bitmap = BitmapFactory.decodeStream(byStream);
            imageView.setImageBitmap(bitmap);

        }
    }
}
