package com.flyzebra.ppfunstv.utils;

import android.content.Context;
import android.text.TextUtils;

import com.flyzebra.ppfunstv.data.Property;
import com.flyzebra.ppfunstv.module.UpdataVersion.UpdataVersion;

import java.util.Locale;
import java.util.Map;

/**
 * 
 * @author hailongqiu 356752238@qq.com
 *
 */
public class Utils implements Property{

	/**
	 * 默认更新间隔(15分钟)
	 */
//	public static int UPDATE_IINTERVAL = 15 * 60 * 1000;//3 * 60 *

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

	public static String getLocalLanguageString(Map<String,String> map){
		if(map==null) return null;
		String str = null;
		String language = Locale.getDefault().getLanguage();
		String country = Locale.getDefault().getCountry();
		if(map.containsKey(country)){
			str = map.get(country);
		}else if (map.containsKey(language)){
			str = map.get(language);
		}

		if(!TextUtils.isEmpty(str)){
			str =  str.replace(" ","");
		}
		return str;
	}

	/**
	 * 获取ca卡号
	 * @param context
	 * @param defaultValue 默认值
	 * @return
     */
	public static String getCaId(Context context,String defaultValue){
		String ca = SystemPropertiesProxy.get(context, Property.YINHE_CA,null);
		if(ca == null || TextUtils.isEmpty(ca)){
			ca = SystemPropertiesProxy.get(context, PPFUNS_CA,null);
		}
		if(ca == null || TextUtils.isEmpty(ca)){
			ca = SystemPropertiesProxy.get(context, PPFUNS_CA_OTHER,defaultValue);
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
		String sn = SystemPropertiesProxy.get(context, PPFUNS_SN,null);
		if(sn == null || TextUtils.isEmpty(sn)){
			sn = SystemPropertiesProxy.get(context, YINHE_SN,null);
		}
		if(sn == null || TextUtils.isEmpty(sn)){
			sn = SystemPropertiesProxy.get(context, ANDROID_SN,defaultValue);
		}
		return sn;
	}

	/**
	 * 更新launcher更新时间
	 * @param context
	 */

	/**

	 * @param context
	 * @param defaultValue
     * @return
     */
	public static String getUid(Context context,String defaultValue){
		return SystemPropertiesProxy.get(context,UID,defaultValue);
	}

	public static void updateInterval(Context context){
		String time = SystemPropertiesProxy.get(context,LAUNCHER_TIME,null);
		if(!TextUtils.isEmpty(time)){
			try {
				int internal = Integer.parseInt(time);
				UpdataVersion.UPDATE_IINTERVAL = internal * 60000;
			}catch (Exception e){

			}finally {
				FlyLog.d("update interval:"+UpdataVersion.UPDATE_IINTERVAL);
			}
		}
	}

}
