package com.ppfuns.ppfunstv.view.TvView.CellView;

import android.content.Context;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ppfuns.ppfunstv.R;
import com.ppfuns.ppfunstv.data.ActionEntity;
import com.ppfuns.ppfunstv.data.CellEntity;
import com.ppfuns.ppfunstv.receiver.NetworkReceiver;
import com.ppfuns.ppfunstv.utils.FlyLog;
import com.ppfuns.ppfunstv.utils.GsonUtils;
import com.ppfuns.ppfunstv.view.SuperVideoView.SuperVideoPlayer;
import com.ppfuns.ppfunstv.view.TvView.IPageChangeListener;


/**
 * 视频播放控件
 * Created by lenovo on 2016/6/15.
 */
public class MinVideoCellView extends SimpleCellView implements IPageChangeListener, NetworkReceiver.EventListener {

    private final static int DELAY_TIME = 100;
    private final static int MSG_DELAY_PLAY = 1;
    private SuperVideoPlayer mSuperVideoPlayer = null;
    private ActionEntity mEntity;
    private boolean isPageIn = false;
    //    private boolean bPlayFlag = false;
    private int mCurPlayPos = 0;//当前播放进度
    private Handler mHandler = new Handler(Looper.myLooper()) {
        @Override
        public void dispatchMessage(Message msg) {
            switch (msg.what) {
                case MSG_DELAY_PLAY: {
                    playVideo();
//                    bPlayFlag = true;
                    //更新海思底层播放器位置大小
                    updateHisiVideoSize();
                    break;
                }
                default:
                    break;
            }
            super.dispatchMessage(msg);
        }
    };

    private SuperVideoPlayer.VideoPlayCallbackImpl mVideoPlayCallback = new SuperVideoPlayer.VideoPlayCallbackImpl() {
        @Override
        public void onPlayStart() {
            mImageView.setVisibility(INVISIBLE);
        }

        @Override
        public void onCloseVideo() {
//            mImageView.setVisibility(VISIBLE);
        }

        @Override
        public void onSwitchPageType() {

        }

        @Override
        public void onPlayFinish() {
            mImageView.setVisibility(VISIBLE);
            mCurPlayPos = 0;
            playVideo();
        }

        @Override
        public void onError(int what, int extra) {
            mImageView.setVisibility(VISIBLE);
//            bPlayFlag = false;
        }

    };

    private NetworkReceiver mNetworkReceiver;

    public MinVideoCellView(Context context) {
        this(context, null);
    }

    public MinVideoCellView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public MinVideoCellView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mNetworkReceiver = new NetworkReceiver(context);
    }

    @Override
    public View setCellData(CellEntity cellEntity) {
        cellEntity.setFocusType(1); //0:圆角焦点框，1:矩形焦点框
        cellEntity.setFocusScale(1);
        return super.setCellData(cellEntity);
    }


    private SubScriptView subScriptView;

    @Override
    public void initView() {
        LayoutParams lp = new LayoutParams(mCell.getWidth(), mCell.getHeight());
        setLayoutParams(lp);
        inflate(mContext, R.layout.tv_live_play_item, this);
        mSuperVideoPlayer = (SuperVideoPlayer) findViewById(R.id.tv_iv_cell);
        mSuperVideoPlayer.setVideoPlayCallback(mVideoPlayCallback);
        mLlMask = (LinearLayout) findViewById(R.id.live_cover_rl);
        mLlMask.setVisibility(INVISIBLE);
        mTvInfo = (TextView) findViewById(R.id.live_cover_info);
        mImageView = (ImageView) findViewById(R.id.live_bg);
        subScriptView = (SubScriptView) findViewById(R.id.tv_iv_sub);
        subScriptView.setCell(mCell,mBitmapCache);
        mEntity = GsonUtils.json2Object(mCell.getIntent(), ActionEntity.class);
    }

    @Override
    public void pageIn() {
        FlyLog.d("page in....");
        if (!isPageIn) {
            isPageIn = true;
            if (mSuperVideoPlayer != null && !mSuperVideoPlayer.isPlaying()) {
                mHandler.removeCallbacksAndMessages(MSG_DELAY_PLAY);
                mHandler.sendEmptyMessageDelayed(MSG_DELAY_PLAY, DELAY_TIME);
            }
        }
    }

    @Override
    public void pageOut() {
        FlyLog.d("page out....");
        if (isPageIn) {
            isPageIn = false;
            mImageView.setVisibility(VISIBLE);
            mHandler.removeMessages(MSG_DELAY_PLAY);
            if (mSuperVideoPlayer != null) {
                mCurPlayPos = mSuperVideoPlayer.getVideoPosition();
//            mSuperVideoPlayer.pause();
                mSuperVideoPlayer.close();
            }
        }
    }


    private Rect mRect = new Rect();

    @Override
    public void pageScroll() {
        updateHisiVideoSize();
        if (mSuperVideoPlayer.getMediaPlayer() != null) {
            if (getLocalVisibleRect(mRect)) {
                mSuperVideoPlayer.getMediaPlayer().start();
            } else {
                mSuperVideoPlayer.getMediaPlayer().pause();
            }
        }
    }

    private void updateHisiVideoSize() {
        if (mSuperVideoPlayer != null) {
            int pos[] = new int[2];
            this.getLocationOnScreen(pos);
            FlyLog.d("updateHisiVideoSize:x/y:" + pos[0] + "/" + pos[1]);
            mSuperVideoPlayer.updateVideoPos(pos[0], pos[1]);
        }
    }

    private void playVideo() {
        try {
            if (mEntity != null && (!TextUtils.isEmpty(mEntity.getUrl()))) {
                FlyLog.d("play usr = %s", mEntity.getUrl());
                Uri uri = Uri.parse(mEntity.getUrl());
                if (mSuperVideoPlayer != null) {
                    mSuperVideoPlayer.loadAndPlay(uri, mCurPlayPos);
                }
                updateHisiVideoSize();
            }
        } catch (Exception e) {
            FlyLog.d(e.toString());
            mImageView.setVisibility(VISIBLE);
        }
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mNetworkReceiver.register(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        mNetworkReceiver.unRegister();
        super.onDetachedFromWindow();
    }

    @Override
    public void wifiConnected() {

    }

    @Override
    public void wifiDisconnected() {
    }

    @Override
    public void ethernetConnected() {
        if (isPageIn && mSuperVideoPlayer != null && !mSuperVideoPlayer.isPlaying()) {
            mHandler.removeCallbacksAndMessages(MSG_DELAY_PLAY);
            mHandler.sendEmptyMessageDelayed(MSG_DELAY_PLAY, DELAY_TIME);
        }
    }

    @Override
    public void ethernetDisconnected() {

    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        FlyLog.d("---------------------");
    }
}


