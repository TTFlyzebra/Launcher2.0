package com.ppfuns.ppfunstv.view;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import com.ppfuns.ppfunstv.constant.Constants;
import com.ppfuns.ppfunstv.data.TemperatureEntity;
import com.ppfuns.ppfunstv.http.FlyOkHttp;
import com.ppfuns.ppfunstv.http.IHttp;
import com.ppfuns.ppfunstv.module.EventMessage;
import com.ppfuns.ppfunstv.utils.FlyLog;
import com.ppfuns.ppfunstv.utils.GsonUtil;
import com.ppfuns.ppfunstv.utils.SystemPropertiesProxy;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Date;
import java.util.List;

/**
 * Created by 李宗源 on 2016/8/1.
 * E-mail:lizy@ppfuns.com
 * 根据区域信息获取温度信息,并通知天气控件进行更新
 * http://apistore.baidu.com/apiworks/servicedetail/112.html
 */
public class TemperatureTextView extends TextView{
    private static final String TAG = TemperatureTextView.class.getSimpleName();
    private static final long ONE_HOUR = 1 * 60 * 60 * 1000;
    private static String mTemperature;
    private static String mWeather;
    private static long initTime;//初始化数据的时间
    private final int DELAY_TIME = 1000;
    private Context mContext;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private String city = null;
    private int mErrorCount = 0;

    private Runnable task = new Runnable(){
        @Override
        public void run() {
            updateTextView();
        }};

    public TemperatureTextView(Context context) {
        this(context,null);
    }

    public TemperatureTextView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TemperatureTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        EventBus.getDefault().register(this);
        mHandler.post(task);
        city = SystemPropertiesProxy.get(mContext,Constants.Property.AREA_NAME,"长沙");
        initData();
    }

    @Override
    protected void onDetachedFromWindow() {
        FlyLog.d("onDetachedFromWindow..");
        EventBus.getDefault().unregister(this);
        mHandler.removeCallbacks(task);
        super.onDetachedFromWindow();
    }

    public void updateTextView() {
        if(getVisibility() != VISIBLE){
            FlyLog.v("not visible,no need to update");
            return;
        }
        String url = "http://wthrcdn.etouch.cn/weather_mini?city=";
        url += city;
        FlyOkHttp.getInstance().getString(url, TAG, new IHttp.HttpResult() {
            @Override
            public void succeed(Object object) {
                if(object != null){
                    FlyLog.d( "onSuccess msg:"+object);
                    TemperatureEntity entity = GsonUtil.json2Object(object.toString(),TemperatureEntity.class);
                    mErrorCount = 0;
                    if(entity != null && entity.getData() != null){
                        List<TemperatureEntity.DataBean.ForecastBean> forecast = entity.getData()
                                .getForecast();
                        mTemperature = getTemper(forecast.get(0).getLow())+"-"+getTemper(forecast.get(0).getHigh())+"℃";
                        setText(mTemperature);
                        mWeather = forecast.get(0).getType();
                        EventBus.getDefault().post(new EventMessage(EventMessage.MSG_UPDATE_WEATHER,mWeather));
                        initTime = new Date().getTime();
                    }
                }
            }

            @Override
            public void failed(Object object) {
                FlyLog.d( "errMsg: " + (object == null ? "" : object.toString()));
                mErrorCount++;
                mHandler.postDelayed(task,DELAY_TIME *mErrorCount *mErrorCount);
            }
        });
    }

    private String getTemper(String str){
        String str2="";
        if(str != null && !"".equals(str.trim())){
            for(int i = 0; i < str.length(); i++){
                if(str.charAt(i) >= 48 && str.charAt(i) <= 57){
                    str2 += str.charAt(i);
                }
            }
        }
        return str2;
    }

    @Subscribe
    public void onEvent(EventMessage msg){
        if(EventMessage.MSG_UPDATE_TEMPERATURE == msg.index){
            FlyLog.d(TAG+" update temperature..."+msg.msg);
            city = msg.msg;
            initData();
        }
    }

    /**
     * 初始化数据
     * 1:如果获取过数据且同当前时间不超过一小时的话,就直接用上一次的数据
     * 2:如果之前没有获取过数据,或者获取数据超过一小时,便重新从网络获取数据
     */
    private void initData(){
        if(!TextUtils.isEmpty(mTemperature) && !TextUtils.isEmpty(mWeather)
                && (new Date().getTime() - initTime < ONE_HOUR)){
            FlyLog.d(" use early info,temperature=%s,weather=%s",mTemperature,mWeather);
            setText(mTemperature);
            EventBus.getDefault().post(new EventMessage(EventMessage.MSG_UPDATE_WEATHER,mWeather));
        }else {
            FlyLog.d(" get data form network..");
            updateTextView();
        }
    }

}
