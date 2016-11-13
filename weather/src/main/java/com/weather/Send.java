package com.weather;

import android.os.Bundle;
import android.app.Activity;

import java.util.Calendar;
import java.util.Locale;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

/**
 * Demo描述:
 * 手机定时发送短信的实现
 *
 * 参考资料:
 * 1 http://blog.csdn.net/pku_android/article/details/7548385
 * 2 http://blog.csdn.net/zjbpku/article/details/7577590
 *   Thank you very much
 *
 */
public class Send extends Activity {
    public int year;
    public int month;
    public int day;
    public int hour;
    public int minute;
    public int timedYear;
    public int timedMonth;
    public int timedDay;
    public int timedHour;
    public int timedMinute;
    private Context mContext;
    public Calendar calendar;
    private Button mDatePickerButton;
    private Button mTimePickerButton;
    private EditText mContentEditText;
    private Button mSendButton;
    private Button mCancelButton;
    public final static int DATE_PICKER = 9527;
    public final static int TIME_PICKER = 9528;
    public DatePickerDialog mDatePickerDialog;
    public TimePickerDialog mTimePickerDialog;
    private PendingIntent mTimedMessagePendingIntent=null;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private TimePickerDialog.OnTimeSetListener mTimeSetListener;
    public boolean isCreatePickerDialog = true;
    private AlarmManager mAlarmManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        Button button=(Button)findViewById(R.id.buttonReturn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Send.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        init();
    }

    private void init() {
        mContext=this;
        mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        mContentEditText = (EditText) findViewById(R.id.contentEditText);
        mSendButton = (Button) findViewById(R.id.sendButton);
        mSendButton.setOnClickListener(new OnClickListenerImpl());

        mDatePickerButton = (Button) findViewById(R.id.datePickButton);
        mDatePickerButton.setOnClickListener(new OnClickListenerImpl());

        mTimePickerButton = (Button) findViewById(R.id.timePickButton);
        mTimePickerButton.setOnClickListener(new OnClickListenerImpl());

        mCancelButton=(Button) findViewById(R.id.cancelButton);
        mCancelButton.setOnClickListener(new OnClickListenerImpl());

        // 监听DatePicker的set按钮
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) {
                //保存定时短信的年月日
                System.out.println("---> DatePicker设置后: year=" + year + ", month="+ monthOfYear + ",day=" + dayOfMonth);
                timedYear=year;
                timedMonth=monthOfYear;
                timedDay=dayOfMonth;
            }
        };

        // 监听TimePicker的set按钮
        mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                //保存定时短信的时分
                System.out.println("---> TimePicker设置后: hour=" + hourOfDay + ",minute="+ minute);
                timedHour=hourOfDay;
                timedMinute=minute;
            }
        };

    }

    private class OnClickListenerImpl implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.datePickButton:
                    showDialog(DATE_PICKER);
                    break;
                case R.id.timePickButton:
                    showDialog(TIME_PICKER);
                    break;
                case R.id.sendButton:
                    sendTimedMessage();
                    break;
                case R.id.cancelButton:
                    cancelTiimedMessage();
                    break;
                default:
                    break;
            }
        }
    }

    // 覆写Activty的onCreateDialog(int id)方法
    // 注意:
    // 1 第一次显示pickerDialog的时候会调用onCreateDialog()和onPrepareDialog()
    // 2 随后每次只会调用onPrepareDialog()方法
    // 所以需要在onPrepareDialog()方法中获取最新时间再设置
    @Override
    protected Dialog onCreateDialog(int id) {
        getCurrentTime();
        switch (id) {
            case DATE_PICKER:
                mDatePickerDialog = new DatePickerDialog(this, mDateSetListener,year, month, day);
                return mDatePickerDialog;
            case TIME_PICKER:
                mTimePickerDialog = new TimePickerDialog(this, mTimeSetListener,hour, minute, true);
                return mTimePickerDialog;
            default:
                break;
        }
        return super.onCreateDialog(id);
    }

    // 覆写Activty的onPrepareDialog(int id, Dialog dialog)方法
    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        super.onPrepareDialog(id, dialog);
        if (!isCreatePickerDialog) {
            getCurrentTime();
            switch (id) {
                case DATE_PICKER:
                    mDatePickerDialog.updateDate(year, month, day);
                    break;
                case TIME_PICKER:
                    mTimePickerDialog.updateTime(hour, minute);
                    break;
                default:
                    break;
            }
        }
        isCreatePickerDialog = false;
    }

    //发送定时短信
    private void sendTimedMessage() {
        String content = mContentEditText.getText().toString();
        if (!TextUtils.isEmpty(content)) {
            Intent intent=new Intent(this,AlarmBroadcastReceiver.class);
            intent.setAction("alarmBroadcastReceiverAction");
            intent.putExtra("content", content);
            mTimedMessagePendingIntent=PendingIntent.getBroadcast(this, 0, intent, 0);
            //设置定时时间
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, timedYear);
            calendar.set(Calendar.MONTH, timedMonth);
            calendar.set(Calendar.DAY_OF_MONTH, timedDay);
            calendar.set(Calendar.HOUR_OF_DAY, timedHour);
            calendar.set(Calendar.MINUTE, timedMinute);
            if (calendar.getTimeInMillis()<=System.currentTimeMillis()) {
                Toast.makeText(mContext, "请重新设置时间", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, "成功设置定时短信", Toast.LENGTH_SHORT).show();
                mAlarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), mTimedMessagePendingIntent);
            }
        }
    }

    //取消定时短信
    private void cancelTiimedMessage(){
        if(mTimedMessagePendingIntent!=null){
            mAlarmManager.cancel(mTimedMessagePendingIntent);
            Toast.makeText(mContext, "取消定时短信", Toast.LENGTH_SHORT).show();
        }
    }

    private void getCurrentTime() {
        // 设置时间为中国
        calendar = Calendar.getInstance(Locale.CHINA);
        // 获取日期
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
        System.out.println("-----> 获取当前时间 year=" + year + ",month=" + month+
                ",day=" + day + ",hour=" + hour + ",minute=" + minute);
    }
}

