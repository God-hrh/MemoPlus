package com.example.sfzhang.memo;

import android.content.Intent;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;

public class Register extends AppCompatActivity {
    private static final String TAG = "Regist";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        final EditText et_name = (EditText) findViewById(R.id.resetpwd_edit_name);
        final EditText et_pwd = (EditText) findViewById(R.id.resetpwd_edit_pwd);
        (findViewById(R.id.register_btn_cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Register.this,Login.class);
                startActivity(intent);
            }
        });
        (findViewById(R.id.register_btn_sure)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = et_name.getText().toString().trim();
                Log.e(TAG, name);
                if(name.equals("")) {
                    Toast.makeText(Register.this,"输入不能为空",Toast.LENGTH_SHORT).show();
                }else{
                    final String pwd = et_pwd.getText().toString().trim();
                    new Thread(new Runnable(){
                        @Override
                        public void run() {
                            DBUtils.setUserInfoByName(name,pwd);
                        }
                    }).start();
                    Intent intent = new Intent(Register.this,Login.class);
                    startActivity(intent);
                    Toast.makeText(Register.this,"注册成功",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
