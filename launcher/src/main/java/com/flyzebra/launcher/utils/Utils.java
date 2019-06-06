package com.flyzebra.launcher.utils;

import android.content.Context;
import android.text.TextUtils;

import com.bumptech.glide.load.DecodeFormat;
import com.flyzebra.launcher.R;
import com.flyzebra.launcher.constant.Constants;

import java.util.Locale;
import java.util.Map;

/**
 * 
 *
 */
public class Utils {

	/**
	 * 获取SDK版本
	 */
	public static int getSDKVersion() {
		int version = 0;
		try {
			version = Integer.valueOf(android.os.Build.VERSION.SDK);
		} catch (NumberFormatException e) {
		}
		return version;
	}

	public static String getName(Map<String,String> map){
		if(map==null) return null;
		String language = Locale.getDefault().getLanguage();
		String country = Locale.getDefault().getCountry();
		if(map.containsKey(country)){
			return map.get(country);
		}else if (map.containsKey(language)){
			return map.get(language);
		}
		return null;
	}

	public static void setBytesPerPixel(DecodeFormat config) {
		if (config == DecodeFormat.PREFER_ARGB_8888) {
			Constants.GLIDE_IMAGE_BYTES_PER_PIXEL = 4;
		} else if (config == DecodeFormat.PREFER_RGB_565) {
			Constants.GLIDE_IMAGE_BYTES_PER_PIXEL = 2;
		}
	}

	/**
	 * 获取ca卡号
	 * @param context
	 * @param defaultValue 默认值
	 * @return
     */
	public static String getCaId(Context context,String defaultValue){
		String ca = SystemPropertiesProxy.get(context, Constants.Property.YINHE_CA,null);
		if(ca == null || TextUtils.isEmpty(ca)){
			ca = SystemPropertiesProxy.get(context, Constants.Property.PPFUNS_CA,null);
		}
		if(ca == null || TextUtils.isEmpty(ca)){
			ca = SystemPropertiesProxy.get(context, Constants.Property.PPFUNS_CA_OTHER,defaultValue);
		}
		return ca;
	}

	/**
	 * 获取机顶盒序列号
	 * @param context
	 * @param defaultValue 默认值
     * @return
     */
	public static String getSn(Context context,String defaultValue){
		String sn = SystemPropertiesProxy.get(context, Constants.Property.PPFUNS_SN,null);
		if(sn == null || TextUtils.isEmpty(sn)){
			sn = SystemPropertiesProxy.get(context, Constants.Property.YINHE_SN,null);
		}
		if(sn == null || TextUtils.isEmpty(sn)){
			sn = SystemPropertiesProxy.get(context, Constants.Property.ANDROID_SN,defaultValue);
		}
		return sn;
	}

	/**

	 * @param context
	 * @param defaultValue
     * @return
     */
	public static String getUid(Context context,String defaultValue){
		return SystemPropertiesProxy.get(context,Constants.Property.UID,defaultValue);
	}

	/**
	 * 更新launcher更新时间
	 * @param context
     */
	public static void updateInterval(Context context){
		String time = SystemPropertiesProxy.get(context,Constants.Property.LAUNCHER_TIME,null);
		if(!TextUtils.isEmpty(time)){
			try {
				int internal = Integer.parseInt(time);
				Constants.UPDATE_IINTERVAL = internal * Constants.ONE_MINUTE;
			}catch (Exception e){

			}finally {
				FlyLog.d("update interval:"+Constants.UPDATE_IINTERVAL);
			}
		}
	}

	private static String getProduct(Context context){
		return context.getString(R.string.product);
	}

	/**
	 * 判断是否为湖南衡阳产品
	 * @param context
	 * @return
     */
	public static boolean isHengYangProduct(Context context){
		return Constants.Product.HENGYANG.equals(getProduct(context));
	}

	public static boolean isAllianceProduct(Context context){
		return Constants.Product.ALLIANCE.equals(getProduct(context));
	}

}
