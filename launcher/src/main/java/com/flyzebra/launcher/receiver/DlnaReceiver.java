package com.flyzebra.launcher.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.flyzebra.launcher.utils.SPUtil;


/**
 * Created by lenovo on 2016/11/23.
 */
public class DlnaReceiver extends BroadcastReceiver{
    String DLNA_SERVICE_STARTED = "com.flyzebra.dlnaservice.DLNA_SERVICE_STARTED";//DLNA服务

    @Override
    public void onReceive(Context context, Intent intent) {
        if(DLNA_SERVICE_STARTED.equals(intent.getAction())){
            String friendlyName = intent.getStringExtra("friendlyName");
            SPUtil.set(context,SPUtil.FILE_CONFIG,SPUtil.CONFIG_UDN_NAME,friendlyName);
        }
    }
}
