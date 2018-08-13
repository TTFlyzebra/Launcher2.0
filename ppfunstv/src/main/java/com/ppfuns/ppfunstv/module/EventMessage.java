package com.ppfuns.ppfunstv.module;

import android.graphics.Bitmap;

/**
 * Created by 李宗源 on 2016/7/25.
 * E-mail:lizy@ppfuns.com
 */
public class EventMessage {

    public static final int MSG_UPDATE_VERSION = 1000;//版本更新消息
    public static final int MSG_CHANGE_TEMPLATE = 1001;//更换模板
    public static final int MSG_UPDATE_DATE = 1100;//日期更新消息

    public static final int MSG_UPDATE_TEMPERATURE = 2001;//更新温度信息
    public static final int MSG_UPDATE_WEATHER = 2002;//更新天气信息
    public static final int MSG_UPDATE_RECENT_APP = 2003;//更新最近应用
    public static final int MSG_UPDATE_REFLECT = 3001;//更新倒影控件

    public static final int MSG_UPDATE_SOUND_INDEX = 4000;//更新声音index

    public static final int MSG_UPDATE_TIME_THEME = 5000;//更新时段主题
    public static final int MSG_UPDATE_QRCODE = 5100;//更新二维码

    public int index;
    public String msg;
    public Object obj;
    public Bitmap bitmap;

    public EventMessage(int index) {
        this.index = index;
    }

    public EventMessage(int index, String msg) {
        this.index = index;
        this.msg = msg;
    }

    public EventMessage(int index, String msg, Object obj) {
        this.index = index;
        this.msg = msg;
        this.obj = obj;
    }

    public EventMessage(int index, Bitmap bitmap, Object obj) {
        this.index = index;
        this.bitmap = bitmap;
        this.obj = obj;
    }

}
