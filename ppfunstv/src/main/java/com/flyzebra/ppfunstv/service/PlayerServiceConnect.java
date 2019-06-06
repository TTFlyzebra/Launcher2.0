package com.flyzebra.ppfunstv.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.NonNull;

import com.flyzebra.playerservice.IPlayControl;
import com.flyzebra.ppfunstv.constant.Constants;
import com.flyzebra.ppfunstv.utils.FlyLog;
import com.flyzebra.ppfunstv.view.TvView.CellView.CellClickAction.CommondTool;

/**
 * 控制小视屏播放
 */
public class PlayerServiceConnect {
    private final String PACKAGE = "com.flyzebra.live";
    private final String CLSNAME = "com.flyzebra.live.service.VodService";
    IPlayControl iPlayControl;
    private Context mContext;
    private int playAction = -1;//0 for play,1 for stop
    private String playPara = null;
    private int left;
    private int top;
    private int width;
    private int height;

    private ServiceConnection serviceConn;
    public PlayerServiceConnect(final Context context){
        mContext = context;

        serviceConn =  new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                FlyLog.d("play service onServiceConnected...playAction(0 for play,1 for stop):"+playAction);
                iPlayControl = IPlayControl.Stub.asInterface(service);
                if(0 == playAction){
                    setBound(left,top,width,height);
                    play(playPara);
                }else if(1 == playAction){
                    stop();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                FlyLog.d("play service onServiceDisconnected...");
                reBind();
            }
        };
        bindServiceCls(context,PACKAGE,CLSNAME,serviceConn);
    }

    private void bindServiceCls(Context context, String pkg, String cls, @NonNull ServiceConnection conn) {
        final Intent intent = new Intent();
        ComponentName componentName = new ComponentName(pkg,cls);
        intent.setComponent(componentName);
        context.bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }

    public void unbindService(Context context){
        FlyLog.d("unbindService...");
        context.unbindService(serviceConn);
    }

    public void setBound(int l,int t,int w,int h){
        left = l;
        top = t;
        width = w;
        height = h;
        try {
            if(iPlayControl != null){
                iPlayControl.setBound(l,t,w,h);
            }else{
                FlyLog.e("play control is null...");
                if (CommondTool.isAppInstalled(mContext, Constants.PACKAGE_LIVE)) {
                    reBind();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            FlyLog.e(e.toString());
            reBind();
        }
    }

    public void play(String playUrl){
        FlyLog.i(playUrl);
        playAction = 0;
        playPara = playUrl;
        try {
            if(iPlayControl != null){
                iPlayControl.play(playUrl);
            }else{
                FlyLog.e("play control is null...");
                if (CommondTool.isAppInstalled(mContext, Constants.PACKAGE_LIVE)) {
                    reBind();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            FlyLog.e(e.toString());
            reBind();
        }
    }

    public void stop(){
        playAction = 1;
        try {
            if(iPlayControl != null){
                iPlayControl.stop();
            }else{
                FlyLog.e("play control is null...");
                if (CommondTool.isAppInstalled(mContext, Constants.PACKAGE_LIVE)) {
                    reBind();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            FlyLog.e(e.toString());
            reBind();
        }
    }

    public void playLast(String playUrl){
        try {
            if(iPlayControl != null){
                iPlayControl.playLast(playUrl);
            }
        } catch (Exception e) {
            e.printStackTrace();
            FlyLog.e(e.toString());
            reBind();
        }
    }

    private void reBind(){
        bindServiceCls(mContext,PACKAGE,CLSNAME,serviceConn);
    }

}
