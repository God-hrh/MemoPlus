package com.example.sfzhang.memo;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public class DBUtils {
    private static final String TAG = "DBUtils";

    private static Connection getConnection(String dbName) {
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver"); //加载驱动
            String ip = "rm-bp1s8wez9j4hw42oemo.mysql.rds.aliyuncs.com";
            conn = DriverManager.getConnection(
                    "jdbc:mysql://" + ip + ":3306/" + dbName,
                    "dizi02", "Dizi1234");
            Log.i("数据库连接成功", "数据库连接成功");
        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return conn;
    }

    public static HashMap<String, String> getUserInfoByName(String name) {
        HashMap<String, String> map = new HashMap<>();
        Connection conn = getConnection("dizi-02");
        try {
            Statement st = conn.createStatement();
            String sql = "select * from user1 where name = '" + name + "'";
            ResultSet res = st.executeQuery(sql);
            if (res == null) {
                return null;
            } else {
                int cnt = res.getMetaData().getColumnCount();
                res.next();
                for (int i = 1; i <= cnt; ++i) {
                    String field = res.getMetaData().getColumnName(i);
                    map.put(field, res.getString(field));
                }
                conn.close();
                st.close();
                res.close();
                return map;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, " 数据操作异常");
            return null;
        }
    }

    public static void setUserInfoByName(String name, String pwd) {

        try {
            Connection conn = getConnection("dizi-02");
            try {
                Statement st = conn.createStatement();
                String sql = "insert into user1 (name,pwd) values('"+name+"','"+pwd+"')";
                st.executeUpdate(sql);
                st.close();
                st.close();

            } catch (SQLException e) {
                e.printStackTrace();
                Log.d(TAG, "出错啦"+"错误信息："+e.toString()+"");
            }
        }catch (Exception e) {
           e.printStackTrace();
           Log.d(TAG, "出错啦，请联系管理员！"+"错误信息："+e.toString()+"");

        }
    }
}
