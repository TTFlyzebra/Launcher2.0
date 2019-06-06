package com.flyzebra.ppfunstv.view.TvView.HeaderView;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.ImageView;

import com.flyzebra.ppfunstv.R;
import com.flyzebra.ppfunstv.receiver.NetworkReceiver;
import com.flyzebra.ppfunstv.utils.SPUtil;
import com.flyzebra.ppfunstv.view.TvView.CellView.CellClickAction.CommondTool;

/**
 * Created by miles on 2017/6/15 0015.
 */

public class HeaderNetView extends ImageView implements IHeaderImage,NetworkReceiver.EventListener{
    private Context mContext;
    private NetworkReceiver mNetworkReceiver;

    public HeaderNetView(Context context){
        this(context, null, 0);
    }

    public HeaderNetView(Context context, AttributeSet attrs){
        this(context, attrs, 0);
    }

    public HeaderNetView(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        this.mContext = context;
        int net_state = (int) SPUtil.get(mContext, "net_state", R.drawable.tv_header_net_wired);
        //        Glide.with(mContext).load(R.drawable.tv_header_net_wifi).into(this);
        setImageResource(net_state);
    }

    @Override
    public void setFocusImage(boolean isFocus){
    }
//
//    public void setAndSaveImage(){
//        if(isFocus){
////            Glide.with(mContext).load(ResImgFocus[state]).into(this);
//            setImageResource(ResImgFocus[state]);
//        }else{
////            Glide.with(mContext).load(ResImgUnfocus[state]).into(this);
//            setImageResource(ResImgUnfocus[state]);
//        }
//    }

    @Override
    public void wifiConnected(){
        setAndSaveImage(R.drawable.tv_header_net_wifi);
    }

    private void setAndSaveImage(int res){
        SPUtil.set(mContext,"net_state",res);
        setImageResource(res);
    }

    @Override
    public void wifiDisconnected(){
        setAndSaveImage(R.drawable.tv_header_nowifi);
    }

    @Override
    public void ethernetConnected(){
        setAndSaveImage(R.drawable.tv_header_net_wired);
    }

    @Override
    public void ethernetDisconnected(){
        setAndSaveImage(R.drawable.tv_header_nowired);

    }

    @Override
    protected void onAttachedToWindow(){
        super.onAttachedToWindow();
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mNetworkReceiver = new NetworkReceiver(mContext);
        mNetworkReceiver.register(this);
    }

    @Override
    protected void onDetachedFromWindow(){
        super.onDetachedFromWindow();
        mNetworkReceiver.unRegister();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        switch(keyCode){
            case KeyEvent.KEYCODE_DPAD_CENTER:
                CommondTool.execStartActivity(mContext, R.string.actionSetinngNetwork);
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
}
