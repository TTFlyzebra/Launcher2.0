package com.ppfuns.launcher.utils;

import android.support.annotation.NonNull;

import com.ppfuns.launcher.constant.Constants;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lenovo on 2016/6/20.
 */
public class TimeUtils {
    public final static String TAG = TimeUtils.class.getSimpleName();

    /**
     * 时间格式为yyyy-MM-dd
     */
    public static SimpleDateFormat format_yyyyMMdd=new SimpleDateFormat("yyyy-MM-dd");

    /**
     * 时间格式为yyyy-MM-dd HH:mm:ss
     */
    public static SimpleDateFormat format_all = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 时间格式为HH:mm
     */
    public static SimpleDateFormat format_HHmm=new SimpleDateFormat("HH:mm");


    public static String getCurrentTime(SimpleDateFormat format) {
        Date date = new Date(System.currentTimeMillis());
        return format.format(date);
    }

    /**
     * 将所对应的小时和分钟转变成秒数
     * @param hour
     * @param min
     * @return
     */
    public static int String2time(@NonNull String hour,@NonNull  String min){
        int time = 0;
        try{
            time = Integer.parseInt(hour)* Constants.ONE_HOUR + Integer.parseInt(min)*Constants.ONE_MINUTE;
        }catch (Exception e){

        }
        return time;
    }
}
