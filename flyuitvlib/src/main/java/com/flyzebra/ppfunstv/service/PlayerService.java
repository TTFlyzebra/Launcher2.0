package com.flyzebra.ppfunstv.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.flyzebra.playerservice.IPlayStatusListener;
import com.flyzebra.playerservice.IPlayerControl;
import com.flyzebra.ppfunstv.R;
import com.flyzebra.ppfunstv.utils.FlyLog;
import com.flyzebra.ppfunstv.view.SuperVideoView.SuperVideoPlayer;

/**
 * 海思特有进程中控制底层视频小窗口位置播放服务
 * Created by pc1 on 2016/7/25.
 */
public class PlayerService extends Service{
    private SuperVideoPlayer mSuperVideoPlayer;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mWMLP;
    private View mPlayView;
    private String DEFULT_VIDEO_URL = "http://10.10.30.220/video/tsy12.mp4";
    private Handler mHandler = new Handler();

    private boolean firstPlay = true;
    private RemoteCallbackList<IPlayStatusListener> mListenerList = new RemoteCallbackList<>();

    private Binder mBinder = new IPlayerControl.Stub(){

        @Override
        public void setUrl(final String url) throws RemoteException {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mSuperVideoPlayer.close();
                    Uri uri = Uri.parse(url);
                    mSuperVideoPlayer.loadAndPlay(uri,0);
                }
            });
        }

        @Override
        public void play() throws RemoteException {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(firstPlay){
                        Uri uri = Uri.parse(DEFULT_VIDEO_URL);
                        mSuperVideoPlayer.loadAndPlay(uri,0);
                        firstPlay = false;
                    }else{
                        mSuperVideoPlayer.start();
                    }
                }
            });

        }

        @Override
        public void pause() throws RemoteException {
            mSuperVideoPlayer.pause();
        }

        @Override
        public void setLocation(int left, int top, int width, int height) throws RemoteException {
            try {
                if(left<0){
                    width=width+left;
                    left=0;
                }
                if((left+width)>1280){
                    width = width-(left+width-1280);
                }
                if(left==1280){
                    left = 1270;
                    height = 1;
                    width = 1;
                }
                mWMLP.x = left;
                mWMLP.y = top;
                mWMLP.width = width;
                mWMLP.height = height;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mWindowManager.updateViewLayout(mPlayView, mWMLP);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public void start() throws RemoteException {
            play();
        }

        @Override
        public void stop() throws RemoteException {
            firstPlay = true;
            mSuperVideoPlayer.close();
        }

        @Override
        public void regPlayStatusistener(IPlayStatusListener listener) throws RemoteException {
            mListenerList.register(listener);
            final int N = mListenerList.beginBroadcast();
            mListenerList.finishBroadcast();
            FlyLog.d("<PlayerService>regPlayStatusistener, current size:" + N);
        }

        @Override
        public void unregPlayStatusListener(IPlayStatusListener listener) throws RemoteException {
            boolean success = mListenerList.unregister(listener);

            if (success) {
                FlyLog.d("<PlayerService>unregPlayStatusListener->unregister success.");
            } else {
                FlyLog.d("<PlayerService>unregPlayStatusListener->not found, can not unregister.");
            }
            final int N = mListenerList.beginBroadcast();
            mListenerList.finishBroadcast();
            FlyLog.d("<PlayerService>unregPlayStatusListener->unregisterListener, current size:" + N);
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FlyLog.d("<PlayerService>onCreate");
        mPlayView = LayoutInflater.from(this).inflate(R.layout.tv_video,null);
        mSuperVideoPlayer = (SuperVideoPlayer) mPlayView.findViewById(R.id.tv_player);
        mSuperVideoPlayer.setVideoPlayCallback(new SuperVideoPlayer.VideoPlayCallbackImpl() {
            @Override
            public void onPlayStart() {

            }

            @Override
            public void onCloseVideo() {
            }

            @Override
            public void onSwitchPageType() {
            }

            @Override
            public void onPlayFinish() {
                final int N = mListenerList.beginBroadcast();
                for (int i = 0; i < N; i++) {
                    IPlayStatusListener l = mListenerList.getBroadcastItem(i);
                    if (l != null) {
                        try {
                            l.onPlayFinish();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }
                mListenerList.finishBroadcast();
            }

            @Override
            public void onError(int what, int extra) {

            }
        });

        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mWMLP = new WindowManager.LayoutParams();
        mWMLP.type = WindowManager.LayoutParams.TYPE_PHONE;
        mWMLP.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mWMLP.x = 1;
        mWMLP.y = 1;
        mWMLP.width = 1;
        mWMLP.height = 1;
        mWMLP.gravity = Gravity.LEFT | Gravity.TOP;
        mWMLP.windowAnimations = android.R.anim.fade_in;
        mWindowManager.addView(mPlayView, mWMLP);
    }

    @Override
    public void onDestroy() {
        FlyLog.d("<PlayerService>onDestroy");
        mSuperVideoPlayer.close();
        mWindowManager.removeView(mPlayView);
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
