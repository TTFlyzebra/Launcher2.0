package com.flyzebra.launcher.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.flyzebra.launcher.utils.FlyLog;

/**
 * Created by lizongyuan on 2016/10/19.
 * E-mail:lizy@ppfuns.com
 * 接收用户Id改变消息
 */

public class UserIdReceiver extends BroadcastReceiver {
    private Context mContext;
    private EventListener mListener;
    public static final String ACTION_USER_ID_CHANGE = "com.aaaservice.ACTION.USERID_CHANGE";//用户ID发生改变

    public UserIdReceiver(Context context){
        mContext = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(mListener != null){
            FlyLog.d("user id changed...");
            mListener.userIdChange();
        }else{
            FlyLog.e("user id listener is null...");
        }
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USER_ID_CHANGE);
        mContext.registerReceiver(this, filter);
    }

    private void unRegisterReceiver() {
        mContext.unregisterReceiver(this);
    }

    private void registerListener(EventListener listener){
        this.mListener = listener;
    }

    private void unRegisterListener(){
        mListener = null;
    }

    public interface EventListener{
        void userIdChange();
    }

    public  void register(UserIdReceiver.EventListener listener){
        registerReceiver();
        registerListener(listener);
    }

    public void unRegister(){
        unRegisterListener();
        unRegisterReceiver();
    }
}
