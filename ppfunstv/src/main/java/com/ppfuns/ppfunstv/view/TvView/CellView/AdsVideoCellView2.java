package com.ppfuns.ppfunstv.view.TvView.CellView;

import android.content.Context;
import android.graphics.Rect;
import android.net.Uri;
import android.os.FileObserver;
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
import com.ppfuns.ppfunstv.utils.FlyLog;
import com.ppfuns.ppfunstv.utils.GsonUtils;
import com.ppfuns.ppfunstv.view.SuperVideoView.SuperVideoPlayer;
import com.ppfuns.ppfunstv.view.TvView.CellView.AdsModule.BaseAdsUpdateListener;
import com.ppfuns.ppfunstv.view.TvView.IPageChangeListener;

import java.io.File;


/**
 * 视频广告控件
 * Created by lenovo on 2016/6/15.
 */
public class AdsVideoCellView2 extends SimpleCellView implements IPageChangeListener {

    private final static int DELAY_TIME = 100;
    private final static int MSG_DELAY_PLAY = 1;
    private final static int MSG_DELAY_UPDAT_VIDEOPOS = 2;
    //    private final IAdsModule mAdsModule;
    private SuperVideoPlayer mSuperVideoPlayer = null;
    private ActionEntity mEntity;
    private BaseAdsUpdateListener mOnAdsUpdateListener;

    //播放器状态管理
    private final static int STATUS_CLOSED = 0;
    private final static int STATUS_PLAYING = 1;
    private final static int STATUS_PAUSED = 2;
    private int bPlayerStatus = STATUS_CLOSED;

    private String defaultVideoUrl = "/system/ppfconf/live_default_ad.mp4";


    private static final String OBSERVER_DIR = "/data/hncatvads";
    private int mCurPlayPos = 0;//当前播放进度
    private SubScriptView mSubScriptView;
    private Handler mHandler = new Handler(Looper.myLooper()) {
        @Override
        public void dispatchMessage(Message msg) {
            switch (msg.what) {
                case MSG_DELAY_PLAY: {
                    FlyLog.i("MSG_DELAY_PLAY:");
                    if (getLocalVisibleRect(mRect)) {
                        FlyLog.i("MSG_DELAY_PLAY: getLocalVisibleRect");
                        safePlay();
                    }
                    break;
                }
                case MSG_DELAY_UPDAT_VIDEOPOS:
                    updateHisiVideoSize();
                    break;
                default:
                    break;
            }
            super.dispatchMessage(msg);
        }
    };


    public SDCardListener sdCardListener;

    private void safePlay() {
        if (bPlayerStatus == STATUS_CLOSED) { //避免重复播放
            FlyLog.d("safePlay: first play");

            String videoUrl;
            videoUrl = getAdsFile(OBSERVER_DIR);
            FlyLog.d(" video url:" + videoUrl);
            if (TextUtils.isEmpty(videoUrl)) {//判断广告是否存在
                FlyLog.d(videoUrl + " is not exist,play default video:" + defaultVideoUrl);
                showBg();
                return;
            }

            Uri uri = Uri.parse(videoUrl);
            if (mSuperVideoPlayer != null) {
                mSuperVideoPlayer.loadAndPlay(uri, mCurPlayPos);

                mSuperVideoPlayer.setVisibility(View.VISIBLE);
            }
            //更新海思底层播放器位置大小
            updateHisiVideoSize();

            bPlayerStatus = STATUS_PLAYING;
        } else if (bPlayerStatus == STATUS_PAUSED) {
            FlyLog.d("safePlay: no need init, start direct");
            if (mSuperVideoPlayer.getMediaPlayer() != null) {
                mSuperVideoPlayer.getMediaPlayer().start();
            }
            bPlayerStatus = STATUS_PLAYING;
        }
    }

    private void safeRestPlay() {
        mCurPlayPos = 0;

        FlyLog.d("safePlay: first play");

        String videoUrl;
        videoUrl = getAdsFile("/data/hncatvads");

        FlyLog.d(" video url:" + videoUrl);
        if (TextUtils.isEmpty(videoUrl)) {//判断广告是否存在
            FlyLog.d(videoUrl + " is not exist,play default video:" + defaultVideoUrl);
            showBg();
            return;
        }

        Uri uri = Uri.parse(videoUrl);
        if (mSuperVideoPlayer != null) {
            mSuperVideoPlayer.loadAndPlay(uri, mCurPlayPos);
            mSuperVideoPlayer.setVisibility(View.VISIBLE);
        }
        //更新海思底层播放器位置大小
        updateHisiVideoSize();

        bPlayerStatus = STATUS_PLAYING;
    }

    private void safePause() {
        FlyLog.d("safePause:");
        if (bPlayerStatus == STATUS_PLAYING) {
            if (mSuperVideoPlayer.getMediaPlayer() != null) {
                mSuperVideoPlayer.getMediaPlayer().pause();
            }
            bPlayerStatus = STATUS_PAUSED;
        }
    }

    private void safeClose() {
        FlyLog.d("safeClose: bPlayerStatus=" + bPlayerStatus);
        if (bPlayerStatus != STATUS_CLOSED) {
            if (mSuperVideoPlayer != null) {
                mCurPlayPos = mSuperVideoPlayer.getVideoPosition();
                mSuperVideoPlayer.close();
            }
            bPlayerStatus = STATUS_CLOSED;
        }
    }

    private SuperVideoPlayer.VideoPlayCallbackImpl mVideoPlayCallback = new SuperVideoPlayer.VideoPlayCallbackImpl() {
        @Override
        public void onPlayStart() {
            showTv();
        }

        @Override
        public void onCloseVideo() {
            showBg();
        }

        @Override
        public void onSwitchPageType() {

        }

        @Override
        public void onPlayFinish() {
            FlyLog.d("play again...");
//            showBg();
//            playIndex++;
//            String playUrl = getPlayUrl(false);
//            if (playUrl != null) {
//                mCurrentVideoUrl = playUrl;
//            }

            safeRestPlay();
        }

        @Override
        public void onError(int what, int extra) {
            FlyLog.d("what:" + what + " extra:" + extra);
//            showPlayInfo(mContext.getString(R.string.tv_live_play_fail));
            showBg();
        }

    };

    public AdsVideoCellView2(Context context) {
        this(context, null);
    }

    public AdsVideoCellView2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public AdsVideoCellView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        mAdsModule = AdsModule.getInstance();
    }


    @Override
    public View setCellData(CellEntity cellEntity) {
        cellEntity.setFocusType(1); //0:圆角焦点框，1:矩形焦点框
        cellEntity.setFocusScale(1);
        return super.setCellData(cellEntity);
    }

    @Override
    public void initView() {
        FlyLog.d("init cell:" + mCell.toString());
        LayoutParams lp = new LayoutParams(mCell.getWidth(), mCell.getHeight());
        setLayoutParams(lp);
        inflate(mContext, R.layout.tv_live_play_item, this);
        mSuperVideoPlayer = (SuperVideoPlayer) findViewById(R.id.tv_iv_cell);
        mSuperVideoPlayer.setVideoPlayCallback(mVideoPlayCallback);
        mLlMask = (LinearLayout) findViewById(R.id.live_cover_rl);
        mTvInfo = (TextView) findViewById(R.id.live_cover_info);
        mImageView = (ImageView) findViewById(R.id.live_bg);
        mSubScriptView = (SubScriptView) findViewById(R.id.tv_iv_sub);
        mSubScriptView.setCell(mCell, mBitmapCache);
        setTextInfo(mTvInfo, mLlMask);
        setTextEffect(mTvInfo, firstLineState);
        sdCardListener = new SDCardListener(OBSERVER_DIR);
    }

    @Override
    public void setActionEntity() {
        FlyLog.d("setActionEntity....");
        super.setActionEntity();
        mEntity = GsonUtils.json2Object(mCell.getIntent(), ActionEntity.class);
    }

    @Override
    public void pageIn() {
        FlyLog.d("pageIn....");

        mHandler.sendEmptyMessageDelayed(MSG_DELAY_PLAY, DELAY_TIME);
    }

    @Override
    public void pageOut() {
        FlyLog.d("pageOut....");
        showBg();
        mHandler.removeMessages(MSG_DELAY_PLAY);
        safeClose();
    }

    private Rect mRect = new Rect();

    private void updateVieoPos() {
        FlyLog.d("updateVieoPos...");

        //延時操作，確保動畫和視頻位置同步
        mHandler.sendEmptyMessageDelayed(MSG_DELAY_UPDAT_VIDEOPOS, 10);

        if (getLocalVisibleRect(mRect)) {
            safePlay();
        } else {
            safePause();
        }
    }

    @Override
    public void pageScroll() {
        FlyLog.d("pageScroll...");

        updateVieoPos();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        FlyLog.d("onLayout...");
        updateVieoPos();
    }

    private void updateHisiVideoSize() {
        if (mSuperVideoPlayer != null) {
            int pos[] = new int[2];
            this.getLocationOnScreen(pos);
            FlyLog.d("updateHisiVideoSize:x/y:" + pos[0] + "/" + pos[1]);
            mSuperVideoPlayer.updateVideoPos(pos[0], pos[1]);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        sdCardListener.startWatching();
    }

    /**
     * @param reset 是否重置play
     */
    private String getPlayUrl(boolean reset) {
//        try {
//            List<String> names = mAdsModule.getFileNames(Integer.parseInt(getCellData().getAdsId()));
//            if (reset) {
//                playIndex = 0;
//                mCurPlayPos = 0;
//            }
//            if (names != null && names.size() > 0) {
//                if (names.size() <= playIndex) {
//                    playIndex = 0;
//                }
//                return names.get(playIndex);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            FlyLog.e(e.toString());
//        }
        return null;
    }

    @Override
    protected void onDetachedFromWindow() {
        FlyLog.v("");
        super.onDetachedFromWindow();
        sdCardListener.stopWatching();
        safeClose();
//        mAdsModule.removeOnUpdateListener(mOnAdsUpdateListener);
    }

    /**
     * 显示小视屏窗口播放视频
     */
    private void showTv() {
        if (mSuperVideoPlayer != null) {
            mSuperVideoPlayer.setVisibility(View.VISIBLE);
        }
        mImageView.setVisibility(INVISIBLE);
        mLlMask.setVisibility(INVISIBLE);
    }

    /**
     * 显示背景图片(当播放失败时显示)
     */
    private void showBg() {
        mImageView.setVisibility(VISIBLE);
        mLlMask.setVisibility(INVISIBLE);
        mImageView.bringToFront();
        mSuperVideoPlayer.setVisibility(View.INVISIBLE);
    }

//    /**
//     * 显示播放相关信息
//     *
//     * @param info 提示信息内容
//     */
//    private void showPlayInfo(String info) {
//        mSuperVideoPlayer.setVisibility(View.VISIBLE);
//        mImageView.setVisibility(INVISIBLE);
//        mLlMask.setVisibility(VISIBLE);
//        mTvInfo.setText(info);
//        mLlMask.bringToFront();
//    }
//
//    private void play(String videoUrl, boolean showProgress) {
//        FlyLog.d(" video url:" + videoUrl);
//        if (TextUtils.isEmpty(videoUrl) || !FileUtil.isAdsExists(videoUrl)) {//判断广告是否存在
//            FlyLog.d(videoUrl + " is not exist,play default video:" + defaultVideoUrl);
//            videoUrl = defaultVideoUrl;
//        }
//
//        Uri uri = Uri.parse(videoUrl);
//        if (mSuperVideoPlayer != null) {
//            mSuperVideoPlayer.loadAndPlay(uri, mCurPlayPos);
//            mCurrentVideoUrl = videoUrl;
//            mSuperVideoPlayer.setVisibility(View.VISIBLE);
//        }
//        updateHisiVideoSize();
//    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }

    //监听文件夹变化
    public class SDCardListener extends FileObserver {

        public SDCardListener(String path) {
              /*
               * 这种构造方法是默认监听所有事件的,如果使用 super(String,int)这种构造方法，
               * 则int参数是要监听的事件类型.
               */
            super(path);
        }

        @Override
        public void onEvent(int event, String path) {
            switch (event) {
                case FileObserver.CREATE:

                    break;
                case FileObserver.DELETE:
                    FlyLog.d("DELETE " + "path:" + path);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            safeClose();
                            showBg();
                            mCurPlayPos = 0;
                        }
                    });
                    break;
                case FileObserver.CLOSE_WRITE:
                    FlyLog.d("CLOSE_WRITE " + "path:" + path);
                    FlyLog.d("Create " + "path:" + path);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            safePlay();
                            showTv();
                        }
                    });
                    break;
                case FileObserver.ACCESS:
                    FlyLog.d("ACCESS " + "path:" + path);
                    break;
                case FileObserver.ATTRIB:
                    FlyLog.d("ATTRIB " + "path:" + path);
                    break;
                case FileObserver.CLOSE_NOWRITE:
                    FlyLog.d("CLOSE_NOWRITE " + "path:" + path);
                    break;
                case FileObserver.DELETE_SELF:
                    FlyLog.d("DELETE_SELF " + "path:" + path);
                    break;
                case FileObserver.MODIFY:
                    FlyLog.d("MODIFY " + "path:" + path);
                    break;
                case FileObserver.MOVE_SELF:
                    FlyLog.d("MOVE_SELF " + "path:" + path);
                    break;
                case FileObserver.MOVED_FROM:
                    FlyLog.d("MOVED_FROM " + "path:" + path);
                    break;
                case FileObserver.OPEN:

                    FlyLog.d("all " + "path:" + path);
                    break;

            }
        }
    }


    public static String getAdsFile(String path) {
        if (!TextUtils.isEmpty(path)) {
            File file = new File(path);
            if (file.exists() && file.isDirectory()) {
                File[] fileLists = file.listFiles();
                if (fileLists.length > 0) {
                    return fileLists[0].getAbsolutePath();
                }
            }
        }
        return null;
    }


}


