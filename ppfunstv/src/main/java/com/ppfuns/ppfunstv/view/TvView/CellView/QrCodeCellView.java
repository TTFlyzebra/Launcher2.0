package com.ppfuns.ppfunstv.view.TvView.CellView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.Toast;

import com.ppfuns.ppfunstv.R;
import com.ppfuns.ppfunstv.constant.Constants;
import com.ppfuns.ppfunstv.constant.NetConstants;
import com.ppfuns.ppfunstv.http.FlyOkHttp;
import com.ppfuns.ppfunstv.http.IHttp;
import com.ppfuns.ppfunstv.module.EventMessage;
import com.ppfuns.ppfunstv.receiver.CaReceiver;
import com.ppfuns.ppfunstv.utils.FlyLog;
import com.ppfuns.ppfunstv.utils.IntentParamParseHelper;
import com.ppfuns.ppfunstv.utils.QRCodeUtil;
import com.ppfuns.ppfunstv.utils.SPUtil;
import com.ppfuns.ppfunstv.view.TvView.CellView.CellClickAction.MobclickConstants;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.Map;


/**
 * 二维码控件
 * Created by 李宗源 on 2016/8/30.
 * E-mail:lizy@ppfuns.com
 * 如果生成二维码失败的话,在(失败次数的平方)秒后进行更换url重试生成二维码
 */
public class QrCodeCellView extends SimpleCellView implements CaReceiver.EventListener {

    private static final int MSG_UPDATE_AP = 2;
    private Bitmap bitmap;
    private String mUrl = NetConstants.URL_BOAFRM;

    private int err_count = 0;
    private int one_second = 1000;
    private String apInfo;//记录二维码ap部分内容
    private Object mLockQr = new Object();
//    private CaReceiver caReceiver;
    private DLANREceiver mReceiver = new DLANREceiver();
    private Handler mHandler = new Handler(Looper.myLooper()) {
        @Override
        public void dispatchMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATE_AP: {
                    updateApInfo();
                    break;
                }
                default:
                    break;
            }
            super.dispatchMessage(msg);
        }
    };

    public QrCodeCellView(Context context) {
        this(context,null);
    }

    public QrCodeCellView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public QrCodeCellView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void createQrcode(){
        new SyncThread(0).start();
        new SyncThread(1).start();
    }

    @Override
    public void showImage(String imgUrl) {
        getMyImageView().setImageResource(mLoadImageResId);
        createQrcode();
    }

    /**
     * 更新热点信息
     */
    private void updateApInfo(){
        final Map<String ,String > para = new HashMap<>();
        para.put(NetConstants.PARA_DATA,NetConstants.PARA_SSID_REQUEST);
        FlyOkHttp.getInstance().postString(mUrl,para,"create qr code",new IHttp.HttpResult(){
            @Override
            public void succeed(Object object) {
                FlyLog.d("succeed,return object:"+object);
                Map<String ,Object> map = IntentParamParseHelper.parseMap(object.toString(),",","=");
                Object nameObj =  map.get(NetConstants.WLAN_SSID_NAME);
                Object passwordObj = map.get(NetConstants.WLAN_SSID_PASSWORD);
                if(nameObj != null && passwordObj != null){
                    String info = "|"+nameObj.toString()+"|"+passwordObj.toString();
                    FlyLog.d("apInfo:"+apInfo+"  new info:"+info);
                    apInfo = info;
                    new SyncThread(0).start();
                }else {
                    FlyLog.d(" name or password is null,no need to update qrcode view.");
                }
            }

            @Override
            public void failed(Object object) {
                FlyLog.d(" failed,return object:"+object+" curUrl:"+mUrl);
                if(NetConstants.URL_BOAFRM.equals(mUrl)){
                    mUrl = NetConstants.URL_BOAFRM_BACK_UP;
                }else{
                    mUrl = NetConstants.URL_BOAFRM;
                }
                err_count++;
                int delayTime = err_count * err_count * one_second / 2;
                FlyLog.d(" delay time:"+delayTime+" nextUrl:"+mUrl);
                mHandler.sendEmptyMessageDelayed(MSG_UPDATE_AP,delayTime);
            }
        });
    }

    public void updateView(Bitmap bitmap){
        FlyLog.d();
        getMyImageView().setImageBitmap(bitmap);
        showReflectView(bitmap);
    }

    @Override
    public void doAction() {
        Toast.makeText(mContext,"请用聚视界客户端扫描关联机顶盒",Toast.LENGTH_SHORT).show();
        MobclickAgent.onEvent(mContext, MobclickConstants.TYPE_QRCODE);
    }

    public Bitmap getBitmap(){
        return bitmap;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        IntentFilter filter = new IntentFilter(Constants.Action.DLNA_SERVICE_STARTED);
        mContext.registerReceiver(mReceiver,filter);
//        caReceiver = new CaReceiver(mContext);
//        caReceiver.register(this);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        mContext.unregisterReceiver(mReceiver);
        EventBus.getDefault().unregister(this);
//        caReceiver.unRegister();
        super.onDetachedFromWindow();
    }

    @Override
    public void cardIn() {
//        new SyncThread(0).start();
    }

    @Override
    public void cardOut() {
//        new SyncThread(0).start();
    }

    @Subscribe
    public void onEvent(EventMessage msg) {
        FlyLog.d("receiver update qrcode message...");
        if (EventMessage.MSG_UPDATE_QRCODE == msg.index) {
            new SyncThread(1).start();
        }
    }

    class DLANREceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(Constants.Action.DLNA_SERVICE_STARTED.equals(intent.getAction())){
                String friendlyName = intent.getStringExtra("friendlyName");
                if(TextUtils.isEmpty(friendlyName)){
                    SPUtil.set(context, SPUtil.FILE_CONFIG, SPUtil.CONFIG_UDN_NAME, friendlyName);
                }
                new SyncThread(0).start();
            }
        }
    }

    public class SyncThread extends Thread{
        int mType = 0;//0:update QR  1:update apinfo
        public SyncThread(int type){
            mType = type;
        }
        @Override
        public void run() {
            FlyLog.d("type(0:update QR,1 update ap):"+mType);
            if(0 == mType){
                synchronized (mLockQr) {
                    Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.tv_qrcode_smi);
                    bitmap = QRCodeUtil.createQRImage(QRCodeUtil.createContent(mContext,apInfo),mCell.getWidth(),mCell.getHeight(),logo);
                    if(null !=bitmap) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                updateView(bitmap);
                            }
                        });
                    }else{
                        FlyLog.d("createQRImage fail");
                    }
                }
            }else if(1 == mType){
                synchronized (mLockQr) {
                    updateApInfo();
                }
            }
        }
    }

}
