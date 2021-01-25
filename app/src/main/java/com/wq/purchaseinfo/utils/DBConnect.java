package com.wq.purchaseinfo.utils;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/*开始实现的直连数据库mysql的方法*/
public class DBConnect {
    private String driver ="com.mysql.jdbc.Driver";
    private String url = "jdbc:mysql://192.168.1.34:3306/test01?useUnicode=true&characterEncoding=utf8&autoReconnect=true&failOverReadOnly=false";
    private String name = "local";
    private String pwd = "123456";
    Connection conn=null;

    protected  Connection connect(){
        conn=null;
        try{
            Class.forName(driver);
            conn = DriverManager.getConnection(url,name,pwd);
            Log.d("数据库","连接成功 ");
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return conn;
    }

     //关闭数据库连接
    protected void closeAll(Connection conn , PreparedStatement ps, ResultSet rs){
        if(rs!=null)
            try {
                if(rs!=null)
                    rs.close();
                if(ps!=null)
                    ps.close();
                if(conn!=null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
    }

    //查询
    protected PreparedStatement prepareStatement(Connection conn,String sql){
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql);
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        return ps;
    }

}
