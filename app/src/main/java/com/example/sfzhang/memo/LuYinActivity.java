package com.example.sfzhang.memo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class LuYinActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static final String SD_APP_DIR_NAME = "TestDir"; //存储程序在外部SD卡上的根目录的名字
    public static final String VOICE_DIR_NAME = "voice";    //存储音频在根目录下的文件夹名字
    public static final int VOICE_RESULT_CODE = 101;        //标志符，音频的结果码，判断是哪一个Intent
    private String mVoicePath;             //用于存储录音的最终目录，即根目录 / 录音的文件夹 / 录音
    private String mVoiceName;             //保存的录音的名字
    private File mVoiceFile;               //录音文件

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lu_yin);

        //录音按钮的点击事件
        Button button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(LuYinActivity.this, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, 201);
            }
        });
    }

    //返回用户是否允许权限的结果，并处理
    public void onRequestPermissionsResult(int requestCode,  String[] permissions,  int[] grantResult) {

        if (requestCode == 201) {
            //用户允许权限
            if (grantResult[0] == PackageManager.PERMISSION_GRANTED && grantResult[1] == PackageManager.PERMISSION_GRANTED) {
                //启动录音机
                startRecord();
            } else {
                Log.d(TAG, "用户已拒绝权限，程序终止。");
                Toast.makeText(this, "程序需要足够权限才能运行", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //启动录音机，创建文件
    private void startRecord() {
        Intent intent = new Intent();
        intent.setAction(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
        createVoiceFile();
        Log.d(TAG, "创建录音文件");
        //添加权限
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Log.d(TAG, "启动系统录音机，开始录音...");
        startActivityForResult(intent, VOICE_RESULT_CODE);
    }

    //创建音频目录
    private void createVoiceFile() {
        mVoiceName = getMyTime() + ".amr";
        Log.d(TAG, "录音文件名称：" + mVoiceName);
        mVoiceFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/" + SD_APP_DIR_NAME + "/" + VOICE_DIR_NAME + "/", mVoiceName);
        mVoicePath = mVoiceFile.getAbsolutePath();
        mVoiceFile.getParentFile().mkdirs();
        Log.d(TAG, "按设置的目录层级创建音频文件，路径：" + mVoicePath);//将录音文件存储在外部cd卡中
        mVoiceFile.setWritable(true);
    }

    //requestCode 请求码resultCode  结果码成功 -1 失败 0  data返回的数据
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "录音结束。");
        if (resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "返回成功。");
            Log.d(TAG, "请求码：" + requestCode + "  结果码：" + resultCode + "  data：" + data);
            switch (requestCode) {

                case VOICE_RESULT_CODE: {
                    try {
                        Uri uri = data.getData();
                        String filePath = getAudioFilePathFromUri(uri);
                        Log.d(TAG, "根据uri获取文件路径：" + filePath);
                        Log.d(TAG, "开始保存录音文件");
                        saveVoiceToSD(filePath);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    break;
                }
            }
        }
    }

    private String getMyTime() {
        //存储格式化后的时间
        String time;
        //存储上午下午
        String ampTime = "";
        //判断上午下午，am上午，值为 0 ； pm下午，值为 1
        int apm = Calendar.getInstance().get(Calendar.AM_PM);
        if (apm == 0) {
            ampTime = "上午";
        } else {
            ampTime = "下午";
        }
        //设置格式化格式
        SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd E " + ampTime + " kk:mm:ss");
        time = format.format(new Date());
        return time;
    }

    //保存音频到SD卡的指定位置  path 录音文件的路径
    private void saveVoiceToSD(String path) {
        //创建输入输出
        InputStream isFrom = null;
        OutputStream osTo = null;
        try {
            //设置输入输出流
            isFrom = new FileInputStream(path);
            osTo = new FileOutputStream(mVoicePath);
            byte bt[] = new byte[1024];
            int len;
            while ((len = isFrom.read(bt)) != -1) {
                Log.d(TAG, "len = " + len);
                osTo.write(bt, 0, len);
            }
            Log.d(TAG, "保存录音完成。");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (osTo != null) {
                try {
                    //不管是否出现异常，都要关闭流
                    osTo.close();
                    Log.d(TAG, "关闭输出流");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (isFrom != null) {
                try {
                    isFrom.close();
                    Log.d(TAG, "关闭输入流");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String getAudioFilePathFromUri(Uri uri) {
        Cursor cursor = getContentResolver()
                .query(uri, null, null, null, null);
        cursor.moveToFirst();
        int index = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA);
        String temp = cursor.getString(index);
        cursor.close();
        return temp;
    }

}