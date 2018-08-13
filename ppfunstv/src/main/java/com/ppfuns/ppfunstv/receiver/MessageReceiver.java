package com.ppfuns.ppfunstv.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.ppfuns.ppfunstv.utils.FlyLog;


/**
 * Created by lizongyuan on 2016/10/18.
 * E-mail:lizy@ppfuns.com
 * 接收消息改变广播
 */

public class MessageReceiver extends BroadcastReceiver {
    String MESSAGE_DATA_CHANGE = "com.ppfuns.message.data.change";
    private Context mContext;
    private EventListener mListener;

    public MessageReceiver(Context context){
        mContext = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        FlyLog.d("receiver message change broadcast...");
        mListener.messageChanged();
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(MESSAGE_DATA_CHANGE);
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

    public  void register(EventListener listener){
        registerReceiver();
        registerListener(listener);
    }

    public void unRegister(){
        unRegisterListener();
        unRegisterReceiver();
    }

    public interface EventListener {
        void messageChanged();
    }
}
