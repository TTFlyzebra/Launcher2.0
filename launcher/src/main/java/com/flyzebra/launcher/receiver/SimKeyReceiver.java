package com.flyzebra.launcher.receiver;
/*
 * 此类接收只能遥控器发送的键值通知，需要将下面的添加到 AndroidManifest.xml
 *      <!-- 向系统注册receiver -->
       <receiver android:name=".SimKeyReceiver">
            <intent-filter >
                <action android:name="com.scy.tv.assistant.KEYCODE"/>
            </intent-filter>
        </receiver>
 *
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.flyzebra.launcher.utils.FlyLog;

import java.io.IOException;

public class SimKeyReceiver extends BroadcastReceiver {

    private static final String TAG = "SimKeyReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("com.scy.tv.assistant.KEYCODE")){
            String keycode = intent.getExtras().getString("keycode");
            FlyLog.d("接收到:"+keycode);

            String cmd = "input keyevent " + keycode + "\n";
            try {
                Runtime.getRuntime().exec(cmd);
            } catch (IOException e) {
                e.printStackTrace();
                FlyLog.d(e.toString());
            }
        }
    }
}