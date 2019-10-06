package com.example.sfzhang.memo;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class Login extends AppCompatActivity {
    private static final String TAG = "Login";
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
//            ((TextView)findViewById(R.id.tv_result)).setText((String)message.obj);
            String str = "密码错误";
            if(message.what == 1) {str = "登陆成功";
                Intent intent = new Intent(Login.this,MainActivity.class);
                startActivity(intent);
            }
            Toast.makeText(Login.this, str, Toast.LENGTH_SHORT).show();
            return false;
}
    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final EditText et_name = (EditText) findViewById(R.id.et_name);
        (findViewById(R.id.btn_01)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = et_name.getText().toString().trim();
                Log.e(TAG, name);
                if(name == null || name.equals("")) {
                    Toast.makeText(Login.this,"输入不能为空",Toast.LENGTH_SHORT).show();
                }
                else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
//                            TextView tv_result = (TextView) findViewById(R.id.tv_result);
                            HashMap<String, String> mp =
                                    DBUtils.getUserInfoByName(name);
                            Message msg = new Message();
                            if(mp == null) {
//                                msg.what = 0;
//                                msg.obj =  "密码错误，请重新输入！";
//                                Toast.makeText(Login.this,"密码错误，请重新输入！",Toast.LENGTH_SHORT).show();
                                //非UI线程不要试着去操作界面
                            }
                            else {
                                String ss = new String();
//                                for (String key : mp.keySet()) {
//                                    ss = ss + key + ":" + mp.get(key) + ";";
//                                }
                                msg.what = 1;
                                msg.obj = ss;
                            }
                            handler.sendMessage(msg);
                        }
                    }).start();
                }
            }
        });
        (findViewById(R.id.login_btn_register)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(Login.this,"注册",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Login.this,Register.class);
                startActivity(intent);
            }
        });
    }
}