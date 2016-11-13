package com.weather;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import java.util.ArrayList;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.widget.Toast;

public class AlarmBroadcastReceiver extends BroadcastReceiver{
    private final String TELPHONE_NUMBER="10086";
    @Override
    public void onReceive(Context context, Intent intent) {
        SmsManager manager = SmsManager.getDefault();
        String content=intent.getStringExtra("content");
        System.out.println("-----> 收到广播 content="+content);
        ArrayList<String> messages = manager.divideMessage(content);
        for (String everyMessage : messages) {
            manager.sendTextMessage(TELPHONE_NUMBER, null, everyMessage, null,null);
        }
        Toast.makeText(context, "已经发送定时短信", Toast.LENGTH_SHORT).show();
    }

}

