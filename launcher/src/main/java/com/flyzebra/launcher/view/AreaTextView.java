package com.flyzebra.launcher.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.widget.TextView;

import com.flyzebra.launcher.R;
import com.flyzebra.launcher.utils.FlyLog;
import com.flyzebra.launcher.utils.SystemPropertiesProxy;
import com.flyzebra.ppfunstv.constant.Constants;
import com.flyzebra.ppfunstv.http.FlyOkHttp;
import com.flyzebra.ppfunstv.http.IHttp;



/**
 * Created by 李宗源 on 2016/8/1.
 * E-mail:lizy@ppfuns.com
 * 根据IP获取当前区域信息
 * 备注(废弃):由于IP地址获取的是本地IP,无法获取到区域信息,需重新设计
 *      或者考虑通过某种方法(NAT),将本地IP转换为外部IP进行访问
 * 目前采用方案:通过设置系统属性(persist.sys.areaname)获取地区信息
 * 对接地址:http://apistore.baidu.com/apiworks/servicedetail/114.html
 */
@SuppressLint("AppCompatCustomView")
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
        FlyOkHttp.getInstance().getString(url, Constants.API_STORE_KEY_NAME, Constants.API_STORE_KEY, Constants.API_STORE_TAG_AREA, new IHttp.HttpResult() {
            @Override
            public void succeed(Object object) {
            }

            @Override
            public void failed(Object object) {
                FlyLog.d( "errMsg: " + (object == null ? "" : object.toString()));
                mHandler.postDelayed(task,DELAY_TIME);
            }
        });
    }



}
