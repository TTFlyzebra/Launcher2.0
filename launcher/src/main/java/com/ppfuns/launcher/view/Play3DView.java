package com.ppfuns.launcher.view;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.ppfuns.launcher.R;
import com.ppfuns.ppfunstv.view.TvView.CellView.CellAnim.Rotate3dAnimation;

import java.util.List;


/**
 *
 * Created by FlyZebra on 2016/7/14.
 */
public class Play3DView extends FrameLayout {
    private Context context;
    private String urlArray[];
    private long durationMillis = 1000;
    private long showMillis = 0;
    private Rotate3dAnimation animation[];
    private float degreesArr[];
    private View viewArr[];
    private Interpolator interplatro = new LinearInterpolator();
    private float imageAlpha = 1.0f;
    private int imagePadding = 0;
    private boolean isClockwise = false;
    private boolean isPlay = true;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private Runnable playTask = new Runnable() {
        @Override
        public void run() {
            if (isPlay) {
                if (!isClockwise) {
                    playToNextImage(durationMillis);
                } else {
                    playToFroeImage(durationMillis);
                }
                mHandler.postDelayed(this, durationMillis + showMillis);
            }
        }
    };

    //滑动
    private GestureDetectorCompat mGestureDetector;
    private OnGestureListener mGestureListener;
    private int mTouchSlop;
    private boolean isFling;


    public Play3DView(Context context) {
        super(context);
        initContext(context);
    }

    public Play3DView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initContext(context);
    }

    public Play3DView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initContext(context);
    }

    private void initContext(Context context) {
        this.context = context;
        ViewConfiguration config = ViewConfiguration.get(context);
        mTouchSlop = config.getScaledTouchSlop();
        mGestureListener = new SimpleOnGestureListener() {

            @Override
            public boolean onDown(MotionEvent e) {
                return super.onDown(e);
            }

            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (Math.abs(velocityX) > Math.abs(velocityY)) {
                    isFling = true;
                    if (velocityX < 0) {
                        playToFroeImage(300);
                        if (isPlay) {
                            mHandler.removeCallbacks(playTask);
                            playAnimition(durationMillis + showMillis);
                        }
                    } else {
                        playToNextImage(300);
                        if (isPlay) {
                            mHandler.removeCallbacks(playTask);
                            playAnimition(durationMillis + showMillis);
                        }
                    }
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        };
        mGestureDetector = new GestureDetectorCompat(context, mGestureListener);
    }

    /**
     * 当前播放状态
     *
     * @return
     */
    public boolean isPlay() {
        return isPlay;
    }

    public Play3DView setPasue(boolean isPlay) {
        this.isPlay = isPlay;
        return this;
    }

    /**
     * 当关播放方向
     *
     * @return
     */
    public boolean isClockwise() {
        return isClockwise;
    }

    /**
     * 设置播放的图片组的网络地址
     *
     * @param urlArray
     * @return
     */


    public Play3DView setImageUrlArray(String urlArray[]) {
        this.urlArray = new String[urlArray.length];
        System.arraycopy(urlArray, 0, this.urlArray, 0, urlArray.length);
        viewArr = new View[urlArray.length];
        for (int i = 0; i < urlArray.length; i++) {
            ImageView iv = new ImageView(context);
            LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            iv.setLayoutParams(lp);
            iv.setPadding(imagePadding, imagePadding, imagePadding, imagePadding);
            iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
            iv.setTag(i);
            iv.setAlpha((int) (255 * imageAlpha));
            iv.setImageResource(R.mipmap.back);
            viewArr[i] = iv;
        }
        return this;
    }

    public Play3DView setImageUrlList(List<String> list) {
        this.urlArray = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            this.urlArray[i] = list.get(i);
        }
        viewArr = new View[urlArray.length];
        for (int i = 0; i < urlArray.length; i++) {
            ImageView iv = new ImageView(context);
            LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            iv.setLayoutParams(lp);
            iv.setPadding(imagePadding, imagePadding, imagePadding, imagePadding);
            iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
            iv.setTag(i);
            iv.setAlpha((int) (255 * imageAlpha));
            iv.setImageResource(R.mipmap.back);
            viewArr[i] = iv;
        }
        return this;
    }


    public Play3DView setViewArr(List<View> list) {
        this.viewArr = new View[list.size()];
        for (int i = 0; i < list.size(); i++) {
            this.viewArr[i] = list.get(i);
        }
        return this;
    }

    /**
     * 设置图片间的间隔
     *
     * @param padding
     * @return
     */
    public Play3DView setImagePadding(int padding) {
        imagePadding = padding;
        return this;
    }

    /**
     * 设置显示图片的透明度
     *
     * @param imageAlpha
     * @return
     */
    public Play3DView setImageAlpha(float imageAlpha) {
        this.imageAlpha = imageAlpha;
        return this;
    }

    /**
     * 完成控件的初始化
     */
    public void Init() {
        initViews();
        initAnimatios();
        initDegrees();
    }

    private void initViews() {
        this.removeAllViews();
        for (int i = 0; i < viewArr.length; i++) {
            this.addView(viewArr[i]);
        }
    }

    private void initAnimatios() {
        animation = new Rotate3dAnimation[viewArr.length];
        for (int i = 0; i < viewArr.length; i++) {
            animation[i] = new Rotate3dAnimation();
        }
    }

    private void initDegrees() {
        degreesArr = new float[viewArr.length];
        for (int i = 0; i < degreesArr.length; i++) {
            degreesArr[i] = (i * 360f / degreesArr.length) % 360;
        }
        playAnimition(0);
    }

    /**
     * 播放下一张图片
     *
     * @param durationMillis
     */
    public void playToNextImage(long durationMillis) {
        if (viewArr == null) return;
        for (int i = 0; i < viewArr.length; i++) {
            degreesArr[i] = (degreesArr[i] + 360f / viewArr.length) % 360;
            isClockwise = false;
            animation[i].init(viewArr[i], degreesArr[i] - 360f / viewArr.length, degreesArr[i], viewArr.length);
            animation[i].setDuration(durationMillis);
            animation[i].setFillAfter(true);
            animation[i].setInterpolator(interplatro);
            viewArr[i].startAnimation(animation[i]);
        }
    }

    /**
     * 播放上一张图片
     *
     * @param durationMillis
     */
    public void playToFroeImage(long durationMillis) {
        if (viewArr == null) return;
        for (int i = 0; i < viewArr.length; i++) {
            degreesArr[i] = (degreesArr[i] - 360f / viewArr.length) % 360;
            isClockwise = true;
            animation[i].init(viewArr[i], degreesArr[i] + 360f / viewArr.length, degreesArr[i], viewArr.length);
            animation[i].setDuration(durationMillis);
            animation[i].setFillAfter(true);
            animation[i].setInterpolator(interplatro);
            viewArr[i].startAnimation(animation[i]);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHandler.removeCallbacksAndMessages(null);
    }

    /**
     * 设置转动一张图片所要的时间
     *
     * @param durationMillis
     * @return
     */
    public Play3DView setDuration(long durationMillis) {
        this.durationMillis = durationMillis;
        return this;
    }

    /**
     * 设置图片播放停止显示图片的时间
     *
     * @param showMillis
     * @return
     */
    public Play3DView setShowMillis(long showMillis) {
        this.showMillis = showMillis;
        return this;
    }

    /**
     * 开始轮播图片
     *
     * @return
     */
    public Play3DView playAnimition(long durationMillis) {
        isPlay = true;
        mHandler.postDelayed(playTask, durationMillis);
        return this;
    }

    /**
     * 在指定时间内取消当前播放，延播放轨迹返回
     *
     * @param durationMillis
     */
    public Play3DView cancleAnimition(long durationMillis) {
        this.isPlay = false;
        mHandler.removeCallbacks(playTask);
        for (int i = 0; i < viewArr.length; i++) {
            if (isClockwise) {
                degreesArr[i] = (degreesArr[i] + 360f / viewArr.length) % 360;
            } else {
                degreesArr[i] = (degreesArr[i] - 360f / viewArr.length) % 360;
            }
            animation[i].init(viewArr[i], degreesArr[i], viewArr.length);
            animation[i].setDuration(durationMillis);
            animation[i].setFillAfter(true);
            animation[i].setInterpolator(interplatro);
            viewArr[i].startAnimation(animation[i]);
        }
        return this;
    }

    /**
     * 在指定时间内完成当前播放
     *
     * @param durationMillis
     */
    public Play3DView finishAnimition(long durationMillis) {
        this.isPlay = false;
        mHandler.removeCallbacks(playTask);
        for (int i = 0; i < viewArr.length; i++) {
            animation[i].init(viewArr[i], degreesArr[i], viewArr.length);
            animation[i].setDuration(durationMillis);
            animation[i].setFillAfter(true);
            animation[i].setInterpolator(interplatro);
            viewArr[i].startAnimation(animation[i]);
        }
        return this;
    }

    /**
     * 停止播放并显示上一张图像
     *
     * @param delayMillis
     * @return
     */
    public Play3DView pauseShowForeImage(long delayMillis) {
        if (isPlay) {
            if (isClockwise()) {
                playToFroeImage(delayMillis);
            } else {
                cancleAnimition(delayMillis);
            }
        } else {
            playToFroeImage(delayMillis);
        }
        isPlay = false;
        isClockwise = true;
        return this;
    }

    /**
     * 停止播放并显示下一张图像
     *
     * @param delayMillis
     * @return
     */
    public Play3DView pauseShowNextImage(long delayMillis) {
        if (isPlay) {
            if (!isClockwise()) {
                playToNextImage(delayMillis);
            } else {
                cancleAnimition(delayMillis);
            }
        } else {
            playToNextImage(delayMillis);
        }
        isPlay = false;
        isClockwise = false;
        return this;
    }

    private OnItemClick mOnItemClick;

    public interface OnItemClick {
        void onItemClick(int position);
    }

    public void setOnItemClick(OnItemClick onItemClick) {
        if (this.mOnItemClick == null) {
            for (View v : viewArr) {
//                iv.setOnClickListener(new OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        mOnItemClick.onItemClick((Integer) v.getTag());
//                        FlyLog.i("<Play3DImages> setOnItemClick position=" + v.getTag());
//                    }
//                });
                v.setOnTouchListener(new OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        mGestureDetector.onTouchEvent(event);
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                isFling = false;
                                break;
                            case MotionEvent.ACTION_UP:
                                if(!isFling){
                                    mOnItemClick.onItemClick((Integer) v.getTag());
                                }
                        }
                        return true;
                    }
                });
            }
        }
        this.mOnItemClick = onItemClick;
    }


}
