package com.flyzebra.ppfunstv.utils;

import android.content.Context;
import android.content.Intent;

/**
 * 
 * Created by lenovo on 2016/6/21.
 */
public class LivePlayHelper {

    private static final String TAG = LivePlayHelper.class.getSimpleName();

    public static final String ACTION_STOP = "com.flyzebra.liveplay.ACTION_STOP";//停止播放
    public static final String ACTION_PLAY = "com.flyzebra.liveplay.ACTION_PLAY";//開始播放
    public static final String ACTION_BOUNDS = "com.flyzebra.liveplay.ACTION_SET_BOUNDS";//设置显示位置

    private final String PARAM = "playElem";//参数名称
    private final String PARAM_X = "x";
    private final String PARAM_Y = "y";
    private final String PARAM_WIDTH = "width";
    private final String PARAM_HEIGTH = "height";
    public final String ACTION_RESPONSE = "com.flyzebra.liveplay.ACTION_RESPONSE";
    public final String ACTION_CA = "com.flyzebra.liveplay.ACTION_CA_RESPONSE";//

    private static final int CODE_NO_SINGER = 1001;//无信号
    public static final int CODE_RESUME_SINGER = 0x1002;//信号恢复

    /**
     * 播放指定节目/分组(小视屏播放)
     * @param context
     * @param cmd
     */
    @Deprecated
    public void play(Context context,String cmd){
        sendBroadcast(context,ACTION_PLAY,cmd);
    }

    /**
     * 发送播放默认频道命令(小视屏播放)
     * @param context
     */
    @Deprecated
    public void play(Context context){
        sendBroadcast(context,ACTION_PLAY);
    }

    /**
     * 进入直播apk进行播放
     * @param context
     * @param action 启动直播action
     * @param data 直播相关参数
     */
    @Deprecated
    public void play(Context context,String action,String data){
        sendBroadcast(context,action,data);
    }

    /**
     * 发送停止播放命令
     * @param context
     */
    @Deprecated
    public void stop(Context context){
        sendBroadcast(context,ACTION_STOP);
    }

    private void sendBroadcast(Context context,String action,String cmd){
        FlyLog.i(TAG+" sendBroadcast action:"+action+" cmd:"+cmd);
        Intent it = new Intent(action);
        it.putExtra(PARAM,cmd);
        context.sendBroadcast(it);
    }

    private void sendBroadcast(Context context, String action){
        FlyLog.i(TAG+" sendBroadcast action:"+action);
        Intent it = new Intent(action);
        context.sendBroadcast(it);
    }

    /**
     * 设置视频播放窗口位置
     * @param context
     * @param x
     * @param y
     * @param width
     * @param height
     */
    @Deprecated
    private void setBounds(Context context,int x,int y,int width,int height){
        FlyLog.i(TAG+" sendBroadcast action:"+ACTION_BOUNDS+" x:"+x+" y:"+y+" width:"+width+" height:"+height);
        Intent it = new Intent(ACTION_BOUNDS);
        it.putExtra(PARAM_X,x);
        it.putExtra(PARAM_Y,y);
        it.putExtra(PARAM_WIDTH,width);
        it.putExtra(PARAM_HEIGTH,height);
        context.sendBroadcast(it);
    }

}
