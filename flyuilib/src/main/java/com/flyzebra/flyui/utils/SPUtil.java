package com.flyzebra.flyui.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

/**
 * 提供SharedPreferences本地化处理方法
 * Created by lzy on 2016/6/30.
 */
public class SPUtil {
    private static String TAG = SPUtil.class.getSimpleName();
    public static final String FILE_CONFIG = "config";
    public static final String TEMPLATE_ID = "template_id";
    public static final String CONFIG_SOUND_INDEX = "sound_id";
    public static final String CONFIG_SOUND_FLAG = "sound_flag";
    public static final String CONFIG_CIRCULATION_FLAG = "circulation_flag";
    public static final String CONFIG_UDN_NAME = "friendlyName";
    public static final String CONFIG_AGE_RANGE = "age_range";
    public static final String CONFIG_SEX = "sex";
    public static final String CONFIG_SHAKE = "shake";
    public static final String CONFIG_UPDATE_TIPS = "update_tip";
    public static final String CONFIG_ALPHA_CELL = "cell_no_focus";

    public static final String FILE_TIME_THEME = "time_theme";
    public static final String CHILD_TIME_CONTINUE = "child_time_continue";
    public static final String CHILD_TIME_HOUR_START = "child_time_hour_start";
    public static final String CHILD_TIME_HOUR_END = "child_time_hour_end";
    public static final String CHILD_TIME_MIN = "child_time_min";
    public static final String PUBLIC_TIME_HOUR = "public_time_hour";
    public static final String PUBLIC_TIME_MIN = "public_time_min";

    private static final String FILE_EVENT = "behavioral_event";
    public static final String EVENT_TEMPLATE_NAME = "template_name";
    public static final String EVENT_TEMPLATE_ID = "template_id";
    public static final String EVENT_TAB_NAME = "tab_name";
    public static final String EVENT_TAB_ID = "tab_id";
    public static final String EVENT_OUT = "out";//0 for in,1 for out

    public static final String FILE_RECENT_APP = "recent_app";
    public static final String KEY_RECENT = "app_";

    public interface Default{
        boolean tips = false;
    }

    /**
     * 默认使用文件config.xoml
     * @param context
     * @param key
     * @param defaultValue
     * @return
     */
    public static Object get(Context context, String key, @NonNull Object defaultValue){
        return get(context,FILE_CONFIG,key,defaultValue);
    }
    public static Object get(Context context, String fileName, String key, @NonNull Object defaultValue){
        SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        if (defaultValue instanceof String) {
            return sp.getString(key, (String) defaultValue);
        } else if (defaultValue instanceof Integer) {
            return sp.getInt(key, (Integer) defaultValue);
        } else if (defaultValue instanceof Boolean) {
            return sp.getBoolean(key, (Boolean) defaultValue);
        } else if (defaultValue instanceof Float) {
            return sp.getFloat(key, (Float) defaultValue);
        } else if (defaultValue instanceof Long) {
            return sp.getLong(key, (Long) defaultValue);
        }
        return null;
    }

    /**
     * 默认使用文件config.xoml
     * @param context
     * @param key
     * @param value
     */
    public static void set(Context context, String key, Object value){
        set(context,FILE_CONFIG,key,value);
    }
    public static void set(Context context, String fileName, String key, Object value){
        SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        } else {
            if(value != null){
                editor.putString(key, value.toString());
            }
        }
        editor.apply();
    }

    /**
     * 获取template相关信息
     * @param context
     * @param key
     * @param defaultValue
     * @return
     */
    public static int getTemplate(Context context, String key, @NonNull int defaultValue){
        return (int)get(context, FILE_CONFIG,key,defaultValue);
    }


    /**
     * 设置template相关信息
     * @param context
     * @param key
     * @param value
     */
    public static void setTemplate(Context context, String key, @NonNull int value){
        set(context, FILE_CONFIG,key,value);
    }

    /**
     * 设置行为数据相关属性
     * @param context
     * @param key
     * @param value
     */
    public static void setEvent(Context context, String key, @NonNull String value){
        set(context,FILE_EVENT,key,value);
    }

    /**
     * 获取行为数据相关属性
     * @param context
     * @param key
     * @param defaultValue
     * @return
     */
    public static String getEvent(Context context, String key, @NonNull String defaultValue){
        return (String) get(context,FILE_EVENT,key,defaultValue);
    }

    /**
     *获取第index个最近应用
     * @param context
     * @param index
     * @return
     */
    public static String getRecentApp(Context context, int index){
        return (String) get(context,FILE_RECENT_APP,KEY_RECENT+index,"");
    }

    /**
     * 设置第index个最近应用
     * @param context
     * @param index
     * @param defaultValue
     */
    public static void setRecentApp(Context context, int index, String defaultValue){
        set(context,FILE_RECENT_APP,KEY_RECENT+index,defaultValue);
    }

}
