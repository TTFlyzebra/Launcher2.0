package com.flyzebra.ppfunstv.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.flyzebra.ppfunstv.R;
import com.flyzebra.ppfunstv.module.EventMessage;
import com.flyzebra.ppfunstv.utils.FlyLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.Map;


public class WeatherView extends ImageView {
	private static final String TAG = WeatherView.class.getSimpleName();

	private Context mContext;
	private Map<String,Integer> mWeather;

	public WeatherView(Context context) {
		this(context,null);
	}

	public WeatherView(Context context, AttributeSet attrs) {
		this(context, attrs,0);
	}

	public WeatherView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		initMap();
	}
	
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		EventBus.getDefault().register(this);
	}
	
	@Override
	protected void onDetachedFromWindow() {
		EventBus.getDefault().unregister(this);
		super.onDetachedFromWindow();
	}
	
	public void updateView(String msg) {
		if(!TextUtils.isEmpty(msg) && mWeather.get(msg) != -1){
//			setImageResource(mWeather.get(msg));
//			Glide.with(mContext).load(mWeather.get(msg)).diskCacheStrategy(DiskCacheStrategy.NONE).into(this);
			setImageResource(mWeather.get(msg));
		}
	}

	private void initMap(){
		mWeather = new HashMap<String, Integer>();
		mWeather.put("晴", R.drawable.tv_header_weather26);
		mWeather.put("多云", R.drawable.tv_header_weather3);
		mWeather.put("阴", R.drawable.tv_header_weather2);
		mWeather.put("阵雨", R.drawable.tv_header_weather18);
		mWeather.put("雷阵雨", R.drawable.tv_header_weather12);
		mWeather.put("雷阵雨伴有冰雹", R.drawable.tv_header_weather15);
		mWeather.put("小雨", R.drawable.tv_header_weather8);
		mWeather.put("小到中雨",R.drawable.tv_header_weather8);
		mWeather.put("中雨", R.drawable.tv_header_weather8);
		mWeather.put("中到大雨",R.drawable.tv_header_weather8);
		mWeather.put("大雨",R.drawable.tv_header_weather17);
		mWeather.put("大到暴雨",R.drawable.tv_header_weather17);
		mWeather.put("暴雨", R.drawable.tv_header_weather17);
		mWeather.put("大暴雨", R.drawable.tv_header_weather17);
		mWeather.put("特大暴雨", R.drawable.tv_header_weather17);
		mWeather.put("阵雪", R.drawable.tv_header_weather25);
		mWeather.put("小雪", R.drawable.tv_header_weather23);
		mWeather.put("中雪", R.drawable.tv_header_weather23);
		mWeather.put("大雪", R.drawable.tv_header_weather23);
	}

	@Subscribe
	public void onEvent(EventMessage msg){
		if(EventMessage.MSG_UPDATE_WEATHER == msg.index && msg.msg != null){
			FlyLog.d("update weather..."+msg.msg);
			updateView(msg.msg);
		}
	}

}
