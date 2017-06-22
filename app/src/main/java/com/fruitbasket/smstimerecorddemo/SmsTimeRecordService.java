package com.fruitbasket.smstimerecorddemo;

import android.accessibilityservice.AccessibilityService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Author: FruitBasket
 * Time: 2017/6/22
 * Email: FruitBasket@qq.com
 * Source code: github.com/DevelopersAssociation
 */
public class SmsTimeRecordService extends AccessibilityService {
    private static final String TAG=".SmsTimeRecordService";

    private static final String APP_FILE_DIR= Environment.getExternalStorageDirectory()+File.separator+"SmsTimeRecord";
    private BroadcastReceiver smsReceiver;
    @Override
    public void onCreate(){
        super.onCreate();
        Log.i(TAG,"onCreate()");

        //创建程序的根目录
        boolean state=false;
        File appDir=new File(APP_FILE_DIR);
        if(appDir.exists()==false){
            state=appDir.mkdirs();
        }
        if(state==false){
            Log.e(TAG,"false to create the mian directory of the app");
        }

        //记录时间
        recordName("boot");

        smsReceiver=new SmsReceiver();
        registerReceiver(
                smsReceiver,
                new IntentFilter("android.provider.Telephony.SMS_RECEIVED")
        );

    }

    @Override
    public void onDestroy() {
        Log.i(TAG,"onDestroy()");
        unregisterReceiver(smsReceiver);
        super.onDestroy();
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.i(TAG,"onServiceConnected()");
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
    }

    @Override
    public void onInterrupt() {
    }

    private class SmsReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG,"onReceive()");
            //记录时间
            recordName("received");
        }
    }

    private void recordName(String tag){
        Log.d(TAG, "current time: "+SystemClock.elapsedRealtimeNanos());
        try {
            DataOutputStream outputStream=new DataOutputStream(
                    new FileOutputStream(APP_FILE_DIR+File.separator+"log.txt",true)
            );
            outputStream.writeBytes(tag+":  "+SystemClock.elapsedRealtimeNanos()+'\n');
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
