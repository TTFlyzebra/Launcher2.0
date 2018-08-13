package com.ppfuns.launcher.view;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.widget.TextView;

import com.ppfuns.launcher.R;
import com.ppfuns.launcher.utils.FlyLog;
import com.ppfuns.launcher.utils.GsonUtils;
import com.ppfuns.launcher.utils.SystemPropertiesProxy;
import com.ppfuns.ppfunstv.constant.Constants;
import com.ppfuns.ppfunstv.data.AreaInfoEntity;
import com.ppfuns.ppfunstv.http.IHttp;
import com.ppfuns.ppfunstv.http.MyOkHttp;
import com.ppfuns.ppfunstv.module.EventMessage;

import org.greenrobot.eventbus.EventBus;


/**
 * Created by 李宗源 on 2016/8/1.
 * E-mail:lizy@ppfuns.com
 * 根据IP获取当前区域信息
 * 备注(废弃):由于IP地址获取的是本地IP,无法获取到区域信息,需重新设计
 *      或者考虑通过某种方法(NAT),将本地IP转换为外部IP进行访问
 * 目前采用方案:通过设置系统属性(persist.sys.areaname)获取地区信息
 * 对接地址:http://apistore.baidu.com/apiworks/servicedetail/114.html
 */
public class AreaTextView extends TextView{
    private static final String TAG = AreaTextView.class.getSimpleName();
    private final int DELAY_TIME = 300;
    private Context mContext;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private Runnable task = new Runnable() {
        @Override
        public void run() {
            updateTextView();
        }
    };

    public AreaTextView(Context context) {
        this(context,null);
    }

    public AreaTextView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }
    public AreaTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
//        updateTextView();
        String area = SystemPropertiesProxy.get(mContext,Constants.Property.AREA_NAME,mContext.getString(R.string.default_area_name));
        setText(area);
    }

    @Override
    protected void onDetachedFromWindow() {
        mHandler.removeCallbacksAndMessages(null);
        super.onDetachedFromWindow();
    }

    @Deprecated
    public void updateTextView() {
        String url = "http://apis.baidu.com/apistore/iplookupservice/iplookup?ip=";
//        String ip = IpAddressUtil.getLocalHostIp();
//        Log.d(TAG,"ip:"+ip);
        url += "58.60.3.130";
        MyOkHttp.getInstance().getString(url, Constants.API_STORE_KEY_NAME, Constants.API_STORE_KEY, Constants.API_STORE_TAG_AREA, new IHttp.HttpResult() {
            @Override
            public void succeed(Object object) {
                if(object != null){
                    FlyLog.d( "onSuccess msg:"+object.toString());
                    AreaInfoEntity entity = GsonUtils.json2Object(object.toString(),AreaInfoEntity.class);
                    if(entity != null && entity.getRetData() != null) {
                        setText(entity.getRetData().getCity());
                        EventBus.getDefault().post(new EventMessage(EventMessage.MSG_UPDATE_TEMPERATURE, entity.getRetData().getCity()));
                        mHandler.removeCallbacks(task);
                    }else{
                        mHandler.postDelayed(task,DELAY_TIME);
                    }
                }
            }

            @Override
            public void failed(Object object) {
                FlyLog.d( "errMsg: " + (object == null ? "" : object.toString()));
                mHandler.postDelayed(task,DELAY_TIME);
            }
        });
    }



}
