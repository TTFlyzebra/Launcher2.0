package com.ppfuns.ppfunstv.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.ppfuns.ppfunstv.utils.FlyLog;
import com.ppfuns.ppfunstv.utils.UsbUtil;


/**
 * Created by lizongyuan on 2016/10/12.
 * E-mail:lizy@ppfuns.com
 */

public class UsbStateReceiver extends BroadcastReceiver{

    private final static int MSG_DELAY_CHECK_USB = 0x1;
    private final static int DELAY_CHECK_TIME = 1000;
    private Context mContext;
    private EventListener mListener;
    private Handler mHandler = new Handler(Looper.myLooper()) {
        @Override
        public void dispatchMessage(Message msg) {
            FlyLog.i("receiver message:" + msg.what);
            switch (msg.what) {
                case MSG_DELAY_CHECK_USB:
                    checkUsb();
                    break;
                default:
                    break;
            }
            super.dispatchMessage(msg);
        }
    };

    public UsbStateReceiver(Context context) {
        mContext = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        FlyLog.i("usb action = "+action);

        if(Intent.ACTION_MEDIA_MOUNTED.equals(action)){//插入
            if(mListener != null){
                mListener.mounted();
            }
        }else if(Intent.ACTION_MEDIA_UNMOUNTED.equals(action)
                ||Intent.ACTION_MEDIA_REMOVED.equals(action)
                ||Intent.ACTION_MEDIA_EJECT.equals(action)){//拔出
            //刚拔出U盘便立即去检测是否存在U盘会出现偏差
            mHandler.sendEmptyMessageDelayed(MSG_DELAY_CHECK_USB,DELAY_CHECK_TIME);
        }
    }

    public void register(EventListener listener){
        registerReceiver();
        registerListener(listener);
    }

    public void unRegister(){
        unRegisterListener();
        unRegisterReceiver();
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_CHECKING);
        filter.addAction(Intent.ACTION_MEDIA_EJECT);
        filter.addAction(Intent.ACTION_MEDIA_REMOVED);
        filter.addDataScheme("file");
        mContext.registerReceiver(this, filter);
    }

    private void unRegisterReceiver() {
        mContext.unregisterReceiver(this);
    }

    private void registerListener(EventListener listener){
        this.mListener = listener;
        checkUsb();
    }

    private void unRegisterListener(){
        mListener = null;
    }

    /**
     * 检测是否存在U盘
     * 避免在启动运用前插入U盘而导致识别不到
     */
    public void checkUsb(){
        if(UsbUtil.isExistUsb()){
            FlyLog.i(" exist usb,do mount action...");
            if (mListener != null){
                mListener.mounted();
            }
        }else{
            FlyLog.i(" not exist usb,do unmount action...");
            if (mListener != null){
                mListener.unMounted();
            }
        }
    }

    public interface EventListener {
        void mounted();

        void unMounted();
    }
}
