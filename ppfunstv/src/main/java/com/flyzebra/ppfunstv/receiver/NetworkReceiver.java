package com.flyzebra.ppfunstv.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import com.flyzebra.ppfunstv.utils.FlyLog;

/**
 * Created by lizongyuan on 2016/11/18.
 * E-mail:lizy@ppfuns.com
 * 接收网络变化广播
 */

public class NetworkReceiver extends BroadcastReceiver {

    private static int lastNetType = -1;
    private static boolean lastIsAvaiable = false;


    private Context mContext;
    private EventListener mListener;

    public NetworkReceiver(Context context){
        mContext = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
//        String action = intent.getAction();
//    /**
//     * wifi联结状态
//     */
//        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
//            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
//            switch (wifiState) {
//                case WifiManager.WIFI_STATE_DISABLED:
//                    mListener.wifiDisconnected();
//                    break;
//                case WifiManager.WIFI_STATE_DISABLING:
//                    break;
//                case WifiManager.WIFI_STATE_ENABLED:
//                    break;
//                case WifiManager.WIFI_STATE_ENABLING:
//                    break;
//                case WifiManager.WIFI_STATE_UNKNOWN:
//                    break;
//            }
//        } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
//            Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
//            if (null != parcelableExtra) {
//                NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
//                NetworkInfo.State state = networkInfo.getState();
//                boolean isConnected = state == NetworkInfo.State.CONNECTED;
//                if (!isConnected) {
//                    FlyLog.d("newwork---->disconnect");
//                } else {
//                    FlyLog.d("newwork---->connect");
//                }
//            }
//        } else if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
//            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//            //获取联网状态的NetworkInfo对象
//            NetworkInfo info = manager.getActiveNetworkInfo();
//            if (info != null) {
//                //如果当前的网络连接成功并且网络连接可用
//                if (NetworkInfo.State.CONNECTED == info.getState() && info.isAvailable()) {
//                    switch (info.getType()) {
//                        //Wifi联结
//                        case ConnectivityManager.TYPE_WIFI:
//                            mListener.wifiConnected();
//                            FlyLog.d("TYPE_WIFI---->connect");
//                            break;
//                        //移动上网
//                        case ConnectivityManager.TYPE_MOBILE:
//                            FlyLog.d("TYPE_MOBILE---->connect");
//                            mListener.ethernetConnected();
//                            break;
//                        //有线网卡上网：
//                        case ConnectivityManager.TYPE_ETHERNET:
//                            FlyLog.d("TYPE_ETHERNET---->connect");
//                            mListener.ethernetConnected();
//                            break;
//                    }
//                } else {
//                    FlyLog.d("newwork---->disconnect");
//                    mListener.ethernetDisconnected();
//                }
//            }else{
//                FlyLog.d("newwork---->disconnect");
//                mListener.ethernetDisconnected();
//            }
//        }

        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = manager.getActiveNetworkInfo();
        if(activeInfo!=null&&(activeInfo.isAvailable()!=lastIsAvaiable||activeInfo.getType()!=lastNetType)){

            int netType = activeInfo.getType();
            if(netType==9){
                //以太网有线连接

                if(activeInfo.getState() == NetworkInfo.State.CONNECTED){
                    mListener.ethernetConnected();
                }else{
                    mListener.ethernetDisconnected();
                }
            }else if(netType==1){
                //wifi连接
                FlyLog.d("TYPE_WIFI---->connect");
                mListener.wifiConnected();
            }
            if(activeInfo.isAvailable()){
                //网络连上
                FlyLog.d("newwork---->connect");
            }else{
                //网络断开

                if(activeInfo.getType() == ConnectivityManager.TYPE_ETHERNET){
                    mListener.ethernetDisconnected();
                }else{
                    mListener.wifiDisconnected();
                }
            }
            lastIsAvaiable = activeInfo.isAvailable();
            lastNetType = activeInfo.getType();
        }
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
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
        void wifiConnected();

        void wifiDisconnected();

        void ethernetConnected();

        void ethernetDisconnected();
    }
}
