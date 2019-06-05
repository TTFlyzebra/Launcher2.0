package com.ppfuns.ppfunstv.view.TvView.CellView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.ppfuns.ppfunstv.R;
import com.ppfuns.ppfunstv.constant.Constants;
import com.ppfuns.ppfunstv.constant.YinheConstants;
import com.ppfuns.ppfunstv.data.ActionEntity;
import com.ppfuns.ppfunstv.service.PlayerServiceConnect;
import com.ppfuns.ppfunstv.utils.FlyLog;
import com.ppfuns.ppfunstv.utils.GsonUtil;
import com.ppfuns.ppfunstv.utils.LivePlayHelper;
import com.ppfuns.ppfunstv.view.TvView.CellView.CellClickAction.CommondTool;
import com.ppfuns.ppfunstv.view.TvView.IPageChangeListener;


/**
 *
 * Created by lenovo on 2016/6/15.
 */
public class LiveCellView extends SimpleCellView implements SurfaceHolder.Callback, IPageChangeListener {

    private static final String TAG = LiveCellView.class.getSimpleName();
    private final String PARAM_CODE = "code";
    private final String PARAM_INFO = "info";
    private final int CODE_ERR = 0;
    //播放服务
    public PlayerServiceConnect playerServiceConnect;//ppfuns播放服务
    private VideoView mVideoView = null;
    private SurfaceHolder mSurfaceHolder;
    private LivePlayHelper mHelper = null;
    private ActionEntity mEntity = null;
    private RelativeLayout mRlCover = null;//提示框层
    private TextView mTvTipInfo = null;//提示信息
    private boolean bPlayFlag = false;
    /**
     * 接收直播发送过来的反馈消息
     */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (mHelper.ACTION_RESPONSE.equals(action)) {
                int code = intent.getIntExtra(PARAM_CODE, CODE_ERR);
                String info = intent.getStringExtra(PARAM_INFO);
                FlyLog.d(TAG, "receiver response :code-" + code + "  info:" + info);
                if (LivePlayHelper.CODE_RESUME_SINGER == code) {
                    showTv();
                } else {
                    showCaInfo(info);
                }
            } else if (mHelper.ACTION_CA.equals(action)) {
                FlyLog.d(TAG, "receiver ca info :");
                String cmd = intent.getStringExtra("cmd");
                if ("showTv".equals(cmd)) {
                    showTv();
                } else if ("showBg".equals(cmd)) {
                    showBg();
                } else {
                    showCaInfo("default error!");
                }
            }
        }
    };
    private int lastStatus = YinheConstants.STATUS_OK;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void dispatchMessage(Message msg) {
            FlyLog.d("msg.what:" + msg.what);
            switch (msg.what) {
                case YinheConstants.STATUS_OK:
//                    showCaInfo("正常播放");
                    showTv();
                    break;
                case YinheConstants.STATUS_NO_SIGNLE:
                    showCaInfo(mContext.getString(R.string.tv_live_no_signal));
                    break;
                case YinheConstants.STATUS_NO_CARD:
                    showCaInfo(mContext.getString(R.string.tv_live_no_card));
                    break;
                case YinheConstants.STATUS_ONT_AUTH:
                    showCaInfo(mContext.getString(R.string.tv_live_no_auth));
                    break;
                case YinheConstants.STATUS_PLAY_FAIL:
                    showCaInfo(mContext.getString(R.string.tv_live_play_fail));
                    break;
                case YinheConstants.STATUS_SIGNLE_OK:
                    if (lastStatus == YinheConstants.STATUS_NO_SIGNLE) {
                        showTv();
                    }
                    break;
                case YinheConstants.STATUS_CARD_IN:
//                    showCaInfo("卡插入");
                    break;
                case YinheConstants.STATUS_AUTH:
//                    showCaInfo("已授权");
                    showTv();
                    break;
                case YinheConstants.STATUS_NO_PROGRAM:
                    showCaInfo(mContext.getString(R.string.tv_live_no_program));
                    break;
                case YinheConstants.STATUS_NO_ASSIGN_PROGRAM:
                    showCaInfo(mContext.getString(R.string.tv_live_no_assign_program));
                    break;
                default:
                    break;
            }
            lastStatus = msg.what;
            super.dispatchMessage(msg);
        }
    };

    public LiveCellView(Context context) {
        this(context, null);
    }

    public LiveCellView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LiveCellView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mHelper = new LivePlayHelper();
    }

    public void initView() {
        LayoutParams lp = new LayoutParams(mCell.getWidth(), mCell.getHeight());
        setLayoutParams(lp);

        inflate(mContext, R.layout.tv_live_cell_item, this);
        mVideoView = (VideoView) findViewById(R.id.iv_cell);
        mSurfaceHolder = mVideoView.getHolder();
        mSurfaceHolder.addCallback(this);
        mRlCover = (RelativeLayout) findViewById(R.id.live_cover_rl);
        mTvTipInfo = (TextView) findViewById(R.id.live_cover_info);
        mImageView = (SubScriptView) findViewById(R.id.live_bg);
        mTvInfo = (TextView) findViewById(R.id.tv_cell);
    }

    @Override
    public void setActionEntity() {
        FlyLog.d("setActionEntity....");
        mEntity = GsonUtil.json2Object(mCell.getIntent(), ActionEntity.class);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        FlyLog.d("surfaceCreated: ");
//        mSurfaceHolder = holder;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        FlyLog.d("surfaceChanged: ");
//        mSurfaceHolder = holder;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        FlyLog.d("surfaceDestroyed: ");
//        mSurfaceHolder = null;
    }

    @Override
    public void pageIn() {
        FlyLog.d(TAG + "page in....");
        if (!bPlayFlag) {
//            showBg();
            showTv();
            setBounds();
            play();
        } else {
            FlyLog.d(" in play statu,no need to play again...");
        }
    }

    @Override
    public void pageOut() {
        FlyLog.d(TAG + "page out....");
        stop();
    }

    @Override
    public void pageScroll() {

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setBounds();
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        FlyLog.d("onAttachedToWindow");
        registerReceiver();
        playerServiceConnect = new PlayerServiceConnect(mContext);
    }

    @Override
    protected void onDetachedFromWindow() {
        FlyLog.d(TAG, "onDetachedFromWindow");
        stop();
        unregisterReceiver();
        if (playerServiceConnect != null) {
            playerServiceConnect.unbindService(mContext);
        }
        super.onDetachedFromWindow();
    }

    private void registerReceiver() {
        IntentFilter mResponseFilter = new IntentFilter();
        mResponseFilter.addAction(mHelper.ACTION_RESPONSE);
        mResponseFilter.addAction(mHelper.ACTION_CA);
        mContext.registerReceiver(mReceiver, mResponseFilter);
    }

    private void unregisterReceiver() {
        mContext.unregisterReceiver(mReceiver);
    }

    /**
     * 显示小视屏窗口播放视频
     */
    private void showTv() {
        mVideoView.setVisibility(View.VISIBLE);
        mImageView.setVisibility(INVISIBLE);
        mRlCover.setVisibility(INVISIBLE);
    }

    /**
     * 显示背景图片(当播放失败时显示)
     */
    private void showBg() {
        mVideoView.setVisibility(View.INVISIBLE);
        mImageView.setVisibility(VISIBLE);
        mImageView.bringToFront();
        mRlCover.setVisibility(INVISIBLE);
    }

    /**
     * 显示CA相关信息
     *
     * @param info CA提示信息内容
     */
    private void showCaInfo(String info) {
        mVideoView.setVisibility(View.VISIBLE);
        mImageView.setVisibility(INVISIBLE);
        mRlCover.setVisibility(VISIBLE);
        mTvTipInfo.setText(info);
        mRlCover.bringToFront();
    }

    private void setBounds() {
        int padding = 0;
        int x = mCell.getX();
        int y = mCell.getY();
        int width = mCell.getWidth();
        int height = mCell.getHeight();
        FlyLog.d("x:" + x + " y:" + y + " width:" + width + " height:" + height + " padding:" + padding);
        if (CommondTool.isAppInstalled(mContext, Constants.PACKAGE_LIVE)) {
            if (playerServiceConnect != null) {
                playerServiceConnect.setBound(x, y, width, height);
            }
        }

    }

    /**
     * 小视屏窗口播放
     */
    private void play() {
        String playPara = null;
        if (mEntity != null) {
            playPara = mEntity.getPlay();
        }
        if (CommondTool.isAppInstalled(mContext, Constants.PACKAGE_LIVE)) {
            if (playerServiceConnect != null) {
                playerServiceConnect.play(playPara);
            }
        }
    }

    /**
     * 停止播放（小视屏窗口）
     */
    private void stop() {
        bPlayFlag = false;
        if (CommondTool.isAppInstalled(mContext, Constants.PACKAGE_LIVE)) {
            if (playerServiceConnect != null) {
                playerServiceConnect.setBound(mCell.getX(), mCell.getY(), 1, 1);
                playerServiceConnect.stop();
            }
        }
    }

}


