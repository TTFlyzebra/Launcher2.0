package com.ppfuns.ppfunstv.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.ppfuns.ppfunstv.utils.FlyLog;


/**
 * Created by lizongyuan on 2016/10/18.
 * E-mail:lizy@ppfuns.com
 * 接收CA卡插拔消息
 */

public class CaReceiver extends BroadcastReceiver {
    private static final String YINHE_CARD_OUT = "com.ppfuns.ngb.action.SMARTCARDPULLOUT";
    private static final String YINHE_CARD_IN = "com.ppfuns.ngb.action.SMARTCARDINSERT";
    private static final String PPFUNS_CARD_CHANGED = "com.ppfuns.ca.changed";
    private Context mContext;
    private EventListener mListener;

    public CaReceiver(Context context){
        mContext = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(mListener != null){
            String action = intent.getAction();
            FlyLog.d("action:"+action);
            if(YINHE_CARD_OUT.equals(action)){
                mListener.cardOut();
            }else if(YINHE_CARD_IN.equals(action)){
                mListener.cardIn();
            }
        }else{
            FlyLog.e("ca listener is null...");
        }
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(YINHE_CARD_OUT);
        filter.addAction(YINHE_CARD_IN);
        filter.addAction(PPFUNS_CARD_CHANGED);
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
        void cardIn();

        void cardOut();
    }
}
