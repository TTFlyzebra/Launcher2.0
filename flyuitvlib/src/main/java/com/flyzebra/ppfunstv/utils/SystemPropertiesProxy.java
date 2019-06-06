package com.flyzebra.ppfunstv.utils;

import android.content.Context;

import java.lang.reflect.Method;

/**
 */
public class SystemPropertiesProxy {

    /**
     * 系统属性相关常量
     */
    public interface Property{
        String YINHE_CA = "persist.sys.ca.card_id";
        String PPFUNS_CA = "sys.device.ca";//临时加persist,后期需去除
        String PPFUNS_CA_OTHER = "persist.sys.device.ca";
        String PPFUNS_MAC  = "sys.device.mac";//
        String YINHE_MAC = "persist.sys.net.mac";
        String AREA_NAME = "persist.sys.areaname";
        String USER_ID = "sys.platform.userid";//用户ID
        String DEBUG = "persist.sys.debug.open";//debug模式
        String TEMPLATE_NAMES = "sys.launcher.template.names";
        String TEMPLATE_IDS = "sys.launcher.template.ids";
        String YINHE_SN = "persist.sys.hwconfig.stb_id";
        String PPFUNS_SN = "sys.device.sn";
        String ANDROID_SN = "ro.serialno";
        String UID = "sys.platform.userid";
        String URL_BASE = "persist.sys.launcher.base.url";
        String LOGO_PATH = "persist.sys.launcher.logo.path";
        String AREA_CODE = "persist.sys.osupdate.areacode";
        String LAUNCHER_VERSION = "persist.sys.launcher.version";
        String LAUNCHER_TIME = "persist.sys.launcher.time";
        String SWV = "ro.build.version.release";//软件版本号
        String HWV = "sys.device.hwv";//硬件版本号
        String DEVICE_CODE = "sys.device.name";
        String FRIENDLY_NAME = "sys.device.friendlyName";
        String SHOW_WEATHER = "persist.sys.show.weather";
        String SHOW_SHADER_ANIM = "persist.sys.show.shader.anim";
    }

    /**
     * 根据Key获取值.
     * @return 如果key不存在, 并且如果def不为空则返回def否则返回空字符串
     */
    public static String get(Context context, String key, String def){
        String ret= def;
        try{
            ClassLoader cl = context.getClassLoader();
            @SuppressWarnings("rawtypes")
            Class SystemProperties = cl.loadClass("android.os.SystemProperties");
            //参数类型
            @SuppressWarnings("rawtypes")
            Class[] paramTypes= new Class[2];
            paramTypes[0]= String.class;
            paramTypes[1]= String.class;

            Method get = SystemProperties.getMethod("get", paramTypes);

            //参数
            Object[] params= new Object[2];
            params[0]= new String(key);
            if(def == null){
                params[1]= new String();
            }else{
                params[1]= new String(def);
            }
            ret= (String) get.invoke(SystemProperties, params);
        }catch( Exception e ){
            ret= def;
            FlyLog.d(e.toString());
        }
        return ret;
    }

    /**
     * 根据给定的key和值设置属性, 该方法需要特定的权限才能操作.
     */
    public static void set(Context context, String key, String val){

        try{
            @SuppressWarnings("rawtypes")
            Class SystemProperties = Class.forName("android.os.SystemProperties");

            //参数类型
            @SuppressWarnings("rawtypes")
            Class[] paramTypes= new Class[2];
            paramTypes[0]= String.class;
            paramTypes[1]= String.class;

            Method set = SystemProperties.getMethod("set", paramTypes);

            //参数
            Object[] params= new Object[2];
            params[0]= new String(key);
            params[1]= new String(val);

            set.invoke(SystemProperties, params);

        }catch( Exception e ){
            FlyLog.e(e.toString());
            //TODO
        }

    }

}
