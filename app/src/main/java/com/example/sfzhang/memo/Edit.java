package com.example.sfzhang.memo;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class Edit extends Activity
        implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, View.OnLongClickListener, RadioGroup.OnCheckedChangeListener{

    LinearLayout myLayout;
    TextView date_text;
    TextView time_text;
    ImageButton alarm_button;
    EditText edt;
    TextView av;
    RadioGroup tagRadio;
    RadioButton rdButton;
    String pic="";
    String audio="";

    int tag;
    String textDate;
    String textTime;
    String mainText;

    int num=0; //for requestcode
    int BIG_NUM_FOR_ALARM=100;
    String alarm="";
    int alarm_hour=0;
    int alarm_minute=0;
    int alarm_year=0;
    int alarm_month=0;
    int alarm_day=0;
    Button button1;
    Button button2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//将界面全屏无Title显示
        setContentView(R.layout.activity_edit);
        Intent it=getIntent();
        getInformationFromMain(it);//从main_activity中读取意图信息

        myLayout = (LinearLayout) findViewById(R.id.whole);
        //myLayout.setBackgroundColor(color[tag]);
        //myLayout.setBackgroundResource(R.drawable.edit_bg_yellow);
        myLayout.setBackgroundResource(R.drawable.beijing001);

        date_text=(TextView) findViewById(R.id.dateText);
        time_text=(TextView) findViewById(R.id.timeText);
        alarm_button=(ImageButton) findViewById((R.id.alarmButton));
        edt=(EditText) findViewById(R.id.editText);
        av=(TextView) findViewById(R.id.alarmView);//闹钟图标

        date_text.setText(textDate);//主界面左上角的日期
        time_text.setText(textTime);//主界面左上角的时间
        edt.setText(mainText);

        av.setOnLongClickListener(this);
        if(alarm.length()>1) av.setText("Alert at "+alarm+"!");
        else av.setVisibility(View.GONE);

        tagRadio=(RadioGroup) findViewById(R.id.tagRadio);
        tagRadio.setOnCheckedChangeListener(this);

        setRadioButtonCheckedAccordingToTag(tag);
        rdButton.setChecked(true);
        button1 = (Button)findViewById(R.id.bt1) ;

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(Edit.this,ChoosePicture.class);
                startActivity(intent1);
            }
        });
        button2 = (Button)findViewById(R.id.bt2) ;

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(Edit.this,LuYinActivity.class);
                startActivity(intent1);
            }
        });
    }

    private void setRadioButtonCheckedAccordingToTag(int tag) {
        switch (tag) {
            case 0:
                rdButton=(RadioButton) findViewById(R.id.beijing001);
                break;
            case 1:
                rdButton=(RadioButton) findViewById(R.id.beijing002);
                break;
            case 2:
                rdButton=(RadioButton) findViewById(R.id.beijing003);
                break;
            case 3:
                rdButton=(RadioButton) findViewById(R.id.beijing004);
                break;
            case 4:
                rdButton=(RadioButton) findViewById(R.id.beijing005);
                break;
            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (tagRadio.getCheckedRadioButtonId()) {
            case R.id.beijing001:
                tag=0;
                myLayout.setBackgroundResource(R.drawable.beijing001);
                break;
            case R.id.beijing002:
                tag=1;
                myLayout.setBackgroundResource(R.drawable.beijing002);
                break;
            case R.id.beijing003:
                tag=2;
                myLayout.setBackgroundResource(R.drawable.beijing003);
                break;
            case R.id.beijing004:
                tag=3;
                myLayout.setBackgroundResource(R.drawable.beijing004);
                break;
            case R.id.beijing005:
                tag=4;
                myLayout.setBackgroundResource(R.drawable.beijing005);
                break;
            default:
                break;
        }
    }

    //设置闹钟
    public void setAlarm(View v) {
        if(alarm.length()<=1) {
            //if no alarm clock has been set up before
            //show the current time
            Calendar c=Calendar.getInstance();//获取指定时间点
            alarm_hour=c.get(Calendar.HOUR_OF_DAY);//当前时间
            alarm_minute=c.get(Calendar.MINUTE);
            alarm_year=c.get(Calendar.YEAR);
            alarm_month=c.get(Calendar.MONTH)+1;
            alarm_day=c.get(Calendar.DAY_OF_MONTH);
        }
        else {
            //显示以前设置的闹钟时间
            int i=0, k=0;
            while(i<alarm.length()&&alarm.charAt(i)!='/') i++;
            alarm_year=Integer.parseInt(alarm.substring(k,i));
            k=i+1;i++;
            while(i<alarm.length()&&alarm.charAt(i)!='/') i++;
            alarm_month=Integer.parseInt(alarm.substring(k,i));
            k=i+1;i++;
            while(i<alarm.length()&&alarm.charAt(i)!=' ') i++;
            alarm_day=Integer.parseInt(alarm.substring(k,i));
            k=i+1;i++;
            while(i<alarm.length()&&alarm.charAt(i)!=':') i++;
            alarm_hour=Integer.parseInt(alarm.substring(k,i));
            k=i+1;i++;
            alarm_minute=Integer.parseInt(alarm.substring(k));
        }

        new TimePickerDialog(this,this,alarm_hour,alarm_minute,true).show();
        new DatePickerDialog(this,this,alarm_year,alarm_month-1,alarm_day).show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        alarm_year=year;
        alarm_month=monthOfYear+1;
        alarm_day=dayOfMonth;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        alarm_hour=hourOfDay;
        alarm_minute=minute;

        alarm=alarm_year+"/"+alarm_month+"/"+alarm_day+" "+alarm_hour+":"+alarm_minute;
        av.setText("Alert at"+alarm+"!");
        av.setVisibility(View.VISIBLE);
        Toast.makeText(this,"闹钟将在"+alarm+" 响!",Toast.LENGTH_LONG).show();
    }

    //按“保存”按钮
    public void onSave(View v) {
        returnResult();
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        //if the Back Button is pressed
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            returnResult();
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //按back或save后，返回当前状态
    private void returnResult() {
        Intent it=new Intent();
        it.putExtra("tag",tag);
        //no need for date and time
        it.putExtra("alarm",alarm);
        it.putExtra("mainText",edt.getText().toString());
        setResult(RESULT_OK,it);
    }

    //从main_activity中读取意图信息
    private void getInformationFromMain(Intent it) {
        num=it.getIntExtra("num",0);

        tag=it.getIntExtra("tag",0);
        textDate=it.getStringExtra("textDate");
        textTime=it.getStringExtra("textTime");

        alarm=it.getStringExtra("alarm");
        mainText=it.getStringExtra("mainText");
    }

    @Override
    public boolean onLongClick(View v) {
        if(v.getId()==R.id.alarmView||v.getId()==R.id.alarmButton) {
            //delete the alarm information
            alarm="";
            //hide textView
            av.setVisibility(View.GONE);//设置为GONE的View不会占用布局空间，但是会进行类的初始化
        }
        return true;
    }
}
