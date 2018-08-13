/*
 *
 * Copyright 2015 TedXiong xiong-wei@hotmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ppfuns.ppfunstv.view.SuperVideoView;

import android.content.Context;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.View;
import android.view.animation.Animation;
import android.widget.RelativeLayout;

import com.ppfuns.ppfunstv.R;
import com.ppfuns.ppfunstv.module.player.IMediaPlayer;
import com.ppfuns.ppfunstv.utils.FlyLog;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Timer;

/**
 * Created by Ted on 2015/8/6.
 * SuperVideoPlayer
 */
public class SuperVideoPlayer extends RelativeLayout {

    private final int MSG_HIDE_CONTROLLER = 10;//隐藏控制器
    private final int MSG_UPDATE_PLAY_TIME = 11;//更新播放时间

    private Context mContext;
    private SuperVideoView mSuperVideoView;//播放器
    private Timer mUpdateTimer;
    private VideoPlayCallbackImpl mVideoPlayCallback;//回调函数
    //    private View mProgressBarView;//加载中按钮
    private Uri mUri;//网络视频路径
    private boolean isFullScreen = false;

    //是否自动隐藏控制栏
    private boolean mAutoHideController = true;
    private int defaultWidth = 0;
    private int defaultHeight = 0;

    private AudioManager mAudioManager;

//    private Handler mHandler = new Handler(new Handler.Callback() {
//        @Override
//        public boolean handleMessage(Message msg) {
//            if (msg.what == MSG_UPDATE_PLAY_TIME) {
//                updatePlayTime();
//                updatePlayProgress();
//            } else if (msg.what == MSG_HIDE_CONTROLLER) {
//                showOrHideController(false);
//            }
//            return false;
//        }
//    });


    /**
     * 初始化View
     *
     * @param context
     */
    private void initView(Context context) {
        mContext = context;
        View.inflate(context, R.layout.tv_super_video_player_layout, this);//TODO 假如只是将java和Layout结合起来，可以直接这么写。
        mSuperVideoView = (SuperVideoView) findViewById(R.id.my_video_view);
//        mProgressBarView = findViewById(R.id.progressbar);//加载中的那个圆圈

//        mVideoView.setOnTouchListener(mOnTouchVideoListener);
        setOnClickListener(mOnClickListener);
//        showProgressView(false);
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
//        mProgressBarView.setOnClickListener(mOnClickListener);
    }


    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
//            int id = view.getId();
//            if (id == R.id.video_inner_container) {
//                changeMode();
//            }else if (id == R.id.video_close_view){
//                mVideoPlayCallback.onCloseVideo();//回调函数的关闭方法
//            }
        }
    };

    public void changeMode() {
        if (defaultWidth == 0) {
            defaultWidth = getWidth();
            defaultWidth = defaultWidth > 0 ? defaultWidth : 320;
        }
        if (defaultHeight == 0) {
            defaultHeight = getHeight();
            defaultHeight = defaultHeight > 0 ? defaultHeight : 240;
        }
        if (!isFullScreen) {
            LayoutParams layoutParams = new LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            this.setLayoutParams(layoutParams);
            isFullScreen = true;//改变全屏/窗口的标记
        } else {
            LayoutParams lp = new LayoutParams(defaultWidth, defaultHeight);
            lp.addRule(RelativeLayout.CENTER_IN_PARENT);
            this.setLayoutParams(lp);
            isFullScreen = false;//改变全屏/窗口的标记
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (!isFullScreen) {
            return super.onKeyDown(keyCode, event);
        }
     /*   if (keyCode == KeyEvent.KEYCODE_ENTER && !isFullScreen){
            changeMode();
            return true;
        } else */
      /*  if (keyCode == KeyEvent.KEYCODE_BACK && isFullScreen){
            changeMode();
            return true;
        } else*/
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            showOrHideController(true);
            backOff();
            return false;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            showOrHideController(true);
            fastForward();
            return false;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            showOrHideController(true);
            //   adjustVolume(1);
            return false;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            showOrHideController(true);
            //    adjustVolume(0);
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    private void adjustVolume(int flag) {
        if (!isFullScreen || mAudioManager == null) {
            return;
        }
        //降低音量，调出系统音量控制
        if (flag == 0) {
            mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER,
                    AudioManager.FX_FOCUS_NAVIGATION_UP);
        } else if (flag == 1) {//增加音量，调出系统音量控制
            mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE,
                    AudioManager.FX_FOCUS_NAVIGATION_UP);
        }
    }

    private void backOff() {
        if (!isFullScreen) {
            return;
        }
        int allTime = mSuperVideoView.getDuration();
        int playTime = mSuperVideoView.getCurrentPosition() - 5 * 1000;
        int backOffPosition = playTime < 0 ? 0 : playTime;
        int loadProgress = mSuperVideoView.getBufferPercentage();
        if (allTime == 0) {
            return;
        }
        int progress = backOffPosition * 100 / allTime;
        mSuperVideoView.seekTo(backOffPosition);
    }

    private void fastForward() {
        if (!isFullScreen) {
            return;
        }
        int allTime = mSuperVideoView.getDuration();
        int playTime = mSuperVideoView.getCurrentPosition() + 5 * 1000;
        int forwardPosition = playTime > allTime ? 0 : playTime;
        int loadProgress = mSuperVideoView.getBufferPercentage();
        if (allTime == 0) {
            return;
        }
        int progress = forwardPosition * 100 / allTime;
        mSuperVideoView.seekTo(forwardPosition);
    }


    /**
     * 判断播放器是否正在播放
     */

    public boolean isPlaying() {
        return mSuperVideoView.isPlaying();
    }

    /**
     * 获取进度条当前位置
     */

    public int getVideoPosition() {
        return mSuperVideoView.getCurrentPosition();
    }




/*
    final Handler handler = new Handler(Looper.getMainLooper());
    Runnable runnable = new Runnable() {
        public void run() {
            int duration = videoView.getCurrentPosition();
            if (old_duration == duration && videoView.isPlaying()) {
                videoMessage.setVisibility(View.VISIBLE);
            } else {
                videoMessage.setVisibility(View.GONE);
            }
            old_duration = duration;
            handler.postDelayed(runnable, 1000);
        }

    };
    handler.postDelayed(runnable, 0);
*/


    /**
     * 设置进度条跳转到具体的位置
     */
    public void gotoPosition(int playTime) {
        int allTime = mSuperVideoView.getDuration();
        //    int forwardPosition = playTime > allTime ? 0 : playTime;
        int forwardPosition = playTime;
        int loadProgress = mSuperVideoView.getBufferPercentage();
        int progress = forwardPosition * 100 / allTime;
        mSuperVideoView.seekTo(forwardPosition);
    }

    // 当MediaPlayer准备好后触发该回调
    private IMediaPlayer.OnPreparedListener mOnPreparedListener = new IMediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(IMediaPlayer mediaPlayer) {
            mediaPlayer.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(IMediaPlayer mp, int what, int extra) {
//                    FlyLog.d("onInfo what:"+what+" extra:"+extra);
                    if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
//                        mProgressBarView.setVisibility(View.GONE);
                        if (mVideoPlayCallback != null) {
                            mVideoPlayCallback.onPlayStart();
                        }
                        return true;
                    }
                    return false;
                }
            });

        }
    };


    private IMediaPlayer.OnErrorListener mOnErrorListener = new IMediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(IMediaPlayer mp, int what, int extra) {
            FlyLog.e("what:" + what + " extra:" + extra);
//            mProgressBarView.setVisibility(GONE);
            mVideoPlayCallback.onError(what, extra);
            return true;
        }
    };

    private IMediaPlayer.OnInfoListener mOnInfoListener = new IMediaPlayer.OnInfoListener() {
        public boolean onInfo(IMediaPlayer mp, int what, int extra) {
            if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                mVideoPlayCallback.onPlayStart();
            }
            return true;
        }
    };

    // 当MediaPlayer播放完成后触发该回调
    private IMediaPlayer.OnCompletionListener mOnCompletionListener = new IMediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(IMediaPlayer mediaPlayer) {
            stopUpdateTimer();
            stopHideTimer(true);
            if (mVideoPlayCallback != null) {
                mVideoPlayCallback.onPlayFinish();
            }
        }
    };


    /**
     * 加载并开始播放视频
     */
    public void loadAndPlay(Uri uri, int seekTime) {
        mUri = uri;
        mSuperVideoView.setOnPreparedListener(mOnPreparedListener);
        mSuperVideoView.setOnInfoListener(mOnInfoListener);
        mSuperVideoView.setOnErrorListener(mOnErrorListener);

        try {
            mSuperVideoView.setVideoURI(uri);
        } catch (Exception e) {
            e.printStackTrace();
            FlyLog.e(e.toString());
        }
        mSuperVideoView.setVisibility(VISIBLE);
        startPlayVideo(seekTime);
    }

    public void start() {
        mSuperVideoView.start();
    }


    /**
     * 暂停播放
     */
    public void pause() {
        mSuperVideoView.pause();
    }


    /**
     * 关闭视频
     */
    public void close() {
        stopHideTimer(true);
        stopUpdateTimer();
        mSuperVideoView.pause();
//        mProgressBarView.setVisibility(GONE);
//        mVideoPlayCallback.onCloseVideo();
        mSuperVideoView.susPend();
        mSuperVideoView.setVisibility(GONE);
    }

    /**
     * 判断是否自动隐藏控制器
     */
    public boolean isAutoHideController() {
        return mAutoHideController;
    }

    public void setAutoHideController(boolean autoHideController) {
        mAutoHideController = autoHideController;
    }

    public SuperVideoPlayer(Context context) {
        super(context);
        initView(context);
    }

    public SuperVideoPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public SuperVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }



    /**
     * 播放视频
     * should called after setVideoPath()
     */
    private void startPlayVideo(int seekTime) {
        if (null == mUpdateTimer) resetUpdateTimer();
        resetHideTimer();

        mSuperVideoView.setOnCompletionListener(mOnCompletionListener);
        mSuperVideoView.start();

        if (seekTime > 0) {
            try {
                mSuperVideoView.seekTo(seekTime);
            } catch (Exception e) {
                FlyLog.e(e.toString());
            }
        }
    }

    /**
     * 更新播放的进度时间
     */
    private void updatePlayTime() {
        int allTime = mSuperVideoView.getDuration();
        int playTime = mSuperVideoView.getCurrentPosition();
    }

    /**
     * 更新播放进度条
     */
    private void updatePlayProgress() {
        int allTime = mSuperVideoView.getDuration();
        int playTime = mSuperVideoView.getCurrentPosition();
        int loadProgress = mSuperVideoView.getBufferPercentage();
        if (allTime == 0 || playTime <= 0 || loadProgress <= 0) {
            return;
        }
        int progress = playTime * 100 / allTime;
    }

//    /**
//     * 显示loading圈
//     *
//     * @param isTransparentBg isTransparentBg
//     */
//    private void showProgressView(Boolean isTransparentBg) {
////        mProgressBarView.setVisibility(VISIBLE);
//        if (!isTransparentBg) {
////            mProgressBarView.setBackgroundResource(android.R.color.black);
//        } else {
////            mProgressBarView.setBackgroundResource(android.R.color.transparent);
//        }
//    }

    public void hideProgressView() {
//        mProgressBarView.setVisibility(INVISIBLE);
    }

    /**
     * 控制器的显示与隐藏
     */
    private void showOrHideController(boolean isKey) {
        if (!isFullScreen) {
            return;
        }
    }

    /**
     * 始终显示控制器
     */
    public void alwaysShowController() {
//        mHandler.removeMessages(MSG_HIDE_CONTROLLER);
    }

    private void resetHideTimer() {
//        if (!isAutoHideController()) return;
//        mHandler.removeMessages(MSG_HIDE_CONTROLLER);
//        int TIME_SHOW_CONTROLLER = 4000;
//        mHandler.sendEmptyMessageDelayed(MSG_HIDE_CONTROLLER, TIME_SHOW_CONTROLLER);
    }

    private void stopHideTimer(boolean isShowController) {
//        mHandler.removeMessages(MSG_HIDE_CONTROLLER);
    }

    private void resetUpdateTimer() {
//        mUpdateTimer = new Timer();
//        int TIME_UPDATE_PLAY_TIME = 1000;
//        mUpdateTimer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                mHandler.sendEmptyMessage(MSG_UPDATE_PLAY_TIME);
//            }
//        }, 0, TIME_UPDATE_PLAY_TIME);
    }

    private void stopUpdateTimer() {
        if (mUpdateTimer != null) {
            mUpdateTimer.cancel();
            mUpdateTimer = null;
        }
    }


    //set回调方法，实现回调在本类中的实例化
    public void setVideoPlayCallback(VideoPlayCallbackImpl videoPlayCallback) {
        mVideoPlayCallback = videoPlayCallback;
    }

    private class AnimationImp implements Animation.AnimationListener {

        @Override
        public void onAnimationEnd(Animation animation) {

        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationStart(Animation animation) {
        }
    }

    public interface VideoPlayCallbackImpl {
        void onPlayStart();

        void onCloseVideo();

        void onSwitchPageType();

        void onPlayFinish();

        void onError(int what, int extra);
    }

    private Rect rect = new Rect();

    public void updateVideoPos(int x, int y) {
        //判断窗口是否在屏幕范围内，不在屏幕范围内不执行任何操作
        if (!getLocalVisibleRect(rect)) {
            FlyLog.d("SuperVideoPlayer is invisible");
            return;
        }
        try {
            Class clazz = Class.forName("android.view.Surface");

            Field field = clazz.getDeclaredField("mPlayer");

            Surface surface = mSuperVideoView.getHolder().getSurface();
            Object player = field.get(surface);

            Class clsPlayer = Class.forName("android.media.MediaPlayer");
            Method m = clsPlayer.getDeclaredMethod("setParameter", new Class[]{int.class, String.class});
            StringBuilder builder = new StringBuilder();

            builder.append(".left=" + x);
            builder.append(".top=" + y);
            builder.append(".right=" + (this.getMeasuredWidth() + x+2));
            builder.append(".bottom=" + (this.getMeasuredHeight() + y+6));

            FlyLog.d("updateVideoPos：builder.toString()" + builder.toString());
            m.invoke(player, 6009, builder.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public IMediaPlayer getMediaPlayer() {
        if (mSuperVideoView != null) {
            return mSuperVideoView.getMediaPlayer();
        } else {
            return null;
        }
    }


}