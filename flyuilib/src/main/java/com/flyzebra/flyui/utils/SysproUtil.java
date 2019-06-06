package com.flyzebra.flyui.utils;

import android.content.Context;

import java.lang.reflect.Method;

/**
 */
public class SysproUtil {

    /**
     * 系统属性相关常量
     */
    public interface Property{
        String URL_BASE = "persist.sys.launcher.upurl";
        String AREA_CODE = "persist.sys.osupdate.areacode";
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
        }

    }

}
