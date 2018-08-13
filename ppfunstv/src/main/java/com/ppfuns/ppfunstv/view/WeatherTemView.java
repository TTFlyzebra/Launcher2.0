package com.ppfuns.ppfunstv.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ppfuns.ppfunstv.R;
import com.ppfuns.ppfunstv.constant.Constants;
import com.ppfuns.ppfunstv.data.TemperatureEntity;
import com.ppfuns.ppfunstv.http.IHttp;
import com.ppfuns.ppfunstv.http.MyOkHttp;
import com.ppfuns.ppfunstv.utils.FlyLog;
import com.ppfuns.ppfunstv.utils.GsonUtils;
import com.ppfuns.ppfunstv.utils.SystemPropertiesProxy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by miles on 2017/6/17 0017.
 * 天气温度显示view
 */

public class WeatherTemView extends RelativeLayout {
    private Context mContext;
    private ImageView mIvWeather;
    private TextView mTvTemperature;
    private String city;
    private String mTemperature;
    private static final String TAG = "WeatherTemView";

    private static final int MSG_GET_STATE_INFO = 3;
    private static final int MSG_CANCEL_STATE_INFO = 4;


    private static final long ONE_HOUR = 60 * 60 * 1000;

    private String type;

    public WeatherTemView(Context context) {
        this(context, null, 0);
    }

    public WeatherTemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeatherTemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
    }

    private void init() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.tv_header_weather_tem, this);
        mIvWeather = (ImageView) view.findViewById(R.id.tv_header_weather);
        mIvWeather.setImageResource(R.drawable.tv_header_weather3);
        mTvTemperature = (TextView) view.findViewById(R.id.tv_header_temperature);
//        sendMsgToUpdate();
        initMap();
    }

    public void getStateInfo() {
        if (getVisibility() != VISIBLE) {
            FlyLog.v("not visible,no need to update");
            return;
        }
        String url = "http://wthrcdn.etouch.cn/weather_mini?city=";
        url += city;
        MyOkHttp.getInstance().getString(url, TAG, new IHttp.HttpResult() {
            @Override
            public void succeed(Object object) {
                if (object != null) {
                    FlyLog.d("onSuccess msg:" + " city: " + city + object);
                    TemperatureEntity entity = GsonUtils.json2Object(object.toString(), TemperatureEntity.class);
                    if (entity != null && entity.getData() != null) {
                        List<TemperatureEntity.DataBean.ForecastBean> forecast = entity.getData()
                                .getForecast();
                        mTemperature = getTemper(forecast.get(0).getLow()) + "-" + getTemper(forecast.get(0).getHigh()) + "℃";
                        mTvTemperature.setText(mTemperature);
                        type = forecast.get(0).getType();
                        updateImageView(forecast.get(0).getType());
                    }
                }
            }

            @Override
            public void failed(Object object) {
                FlyLog.d("errMsg: " + (object == null ? "" : object.toString()));
            }
        });
    }


    public void cancelStateInfo(){
        FlyLog.d();
        mHandler.removeMessages(MSG_GET_STATE_INFO);
//        mHandler.removeMessages();

//        mHandler.sendEmptyMessage(MSG_GET_STATE_INFO);
    }
    public void sendMsgToUpdate(){
        FlyLog.d();
        mHandler.sendEmptyMessage(MSG_GET_STATE_INFO);
    }
    private String getTemper(String str) {
        String str2 = "";
        if (str != null && !"".equals(str.trim())) {
            for (int i = 0; i < str.length(); i++) {
                if (str.charAt(i) >= 48 && str.charAt(i) <= 57) {
                    str2 += str.charAt(i);
                }
            }
        }
        return str2;
    }

    @Override
    protected void onAttachedToWindow() {
        city = SystemPropertiesProxy.get(mContext, Constants.Property.AREA_NAME, "长沙");
        if (city.length() > 5) {
            city = city.substring(0, 2);
        }
        init();
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        FlyLog.d();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        super.onDetachedFromWindow();
    }

    private Map<String, Integer> mWeather;

    private void initMap() {
        mWeather = new HashMap<>();
        mWeather.put("晴", R.drawable.tv_header_weather26);
        mWeather.put("多云", R.drawable.tv_header_weather3);
        mWeather.put("阴", R.drawable.tv_header_weather2);
        mWeather.put("阵雨", R.drawable.tv_header_weather18);
        mWeather.put("雷阵雨", R.drawable.tv_header_weather12);
        mWeather.put("雷阵雨伴有冰雹", R.drawable.tv_header_weather15);
        mWeather.put("小雨", R.drawable.tv_header_weather8);
        mWeather.put("小到中雨", R.drawable.tv_header_weather8);
        mWeather.put("中雨", R.drawable.tv_header_weather8);
        mWeather.put("中到大雨", R.drawable.tv_header_weather8);
        mWeather.put("大雨", R.drawable.tv_header_weather17);
        mWeather.put("大到暴雨", R.drawable.tv_header_weather17);
        mWeather.put("暴雨", R.drawable.tv_header_weather17);
        mWeather.put("大暴雨", R.drawable.tv_header_weather17);
        mWeather.put("特大暴雨", R.drawable.tv_header_weather17);
        mWeather.put("阵雪", R.drawable.tv_header_weather25);
        mWeather.put("小雪", R.drawable.tv_header_weather23);
        mWeather.put("中雪", R.drawable.tv_header_weather23);
        mWeather.put("大雪", R.drawable.tv_header_weather23);
    }

    public void updateImageView(String msg) {
        if (!TextUtils.isEmpty(msg) && mWeather.get(msg) != -1) {
//            Glide.with(mContext).load(mWeather.get(msg)).into(mIvWeather);
            mIvWeather.setImageResource(mWeather.get(msg));
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_GET_STATE_INFO:
                    mHandler.sendEmptyMessageDelayed(MSG_GET_STATE_INFO, ONE_HOUR);
                    getStateInfo();
                    break;
            }
        }
    };

}
