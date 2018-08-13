package com.ppfuns.ppfunstv.view.TvView.FocusAnimat;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.DrawableRes;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;

import com.ppfuns.ppfunstv.R;
import com.ppfuns.ppfunstv.utils.DisplayUtils;
import com.ppfuns.ppfunstv.view.TvView.CellView.GifCellView;
import com.ppfuns.ppfunstv.view.TvView.CellView.PopupCellView.IPopupAnimatView;
import com.ppfuns.ppfunstv.view.TvView.IPageChangeListener;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created FlyZebra on 2016/6/15.
 */
public class PopupTvNotMoveAnimat implements ITvFocusAnimat {
    private FrameLayout shadowView;
    private int shadowRid = 0;
    private IAnimatView lastView;
    private Rect lastRect;
    private int shadowAmend = 6;//阴影修正值单位DP
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private ViewGroup rootView;
    private final static ExecutorService executors = Executors.newFixedThreadPool(1);
    /**
     * 线程同步操作原子数,记录线程是否运行中
     */
    private AtomicBoolean isAnimRunning = new AtomicBoolean(false);

    /**
     * 线程同步操作原子数,记录线程是否取消状态
     */
    private AtomicBoolean isAtomicCancel = new AtomicBoolean(false);

    private float screenScale = 1.0f;

    private float MAX_ANIM_SCALE = 1.08f;

    private AnimTask mAnimTask;
    private int animDuration = 300;
    int resIDs[] = {R.drawable.tv_focus0, R.drawable.tv_focus1, R.drawable.tv_focus2,R.drawable.tv_focus4,R.drawable.tv_focus4};


    public PopupTvNotMoveAnimat(ViewGroup viewGroup) {
        rootView = viewGroup;
        shadowView = new FocusShadowView(viewGroup.getContext());
        viewGroup.addView(shadowView);
        shadowView.setBackgroundResource(R.drawable.tv_bg_focus);
//        shadowAmend = DisplayUtils.dip2px(viewGroup.getContext(), shadowAmend);
        DisplayMetrics dm = DisplayUtils.getMetrices((Activity) viewGroup.getContext());
        screenScale = dm.widthPixels / 1920f;
        shadowAmend = Math.round(0.5f + (shadowAmend * screenScale));
    }

    public void setShadowAmend(int shadowAmend) {
        this.shadowAmend = Math.round(0.5f + shadowAmend * screenScale);
    }

    @Override
    public void startAnim(IAnimatView srcView, IAnimatView objView) {

    }

    @Override
    public void startAnim(IAnimatView objView, boolean isMoverCenter) {

    }

    @Override
    public void startAnim(IAnimatView srcView, IAnimatView objView, boolean isMoveCenter) {

    }

    @Override
    public void flyWhiteBorder(Rect rect1, Rect rect2, boolean isMoveCenter) {
        if (rect1 == null) {
            rect1 = new Rect(rect2.left, rect2.top, rect2.right, rect2.bottom);
        }
        if (rect1.left == 0 && rect1.right == 0 && rect1.top == 0 && rect1.bottom == 0) {
            rect1 = new Rect(rect2.left, rect2.top, rect2.right, rect2.bottom);
        }

        if (isAnimRunning.get()) {
            mAnimTask.setToViewInfo(rect2, isMoveCenter);
        } else {
            mAnimTask = new AnimTask(shadowView, rect1, rect2, animDuration, isMoveCenter);
            executors.execute(mAnimTask);
        }
    }

    @Override
    public void flyWhiteBorder(IAnimatView view, Rect rect1, Rect rect2, boolean isMoveCenter) {
        if (lastView != null) {
            ((View)lastView).animate().scaleX(1).scaleY(1).setInterpolator(new AccelerateInterpolator()).setDuration(animDuration).start();
        }
        int left = rect2.left;
        int top = rect2.top;
        int right = rect2.left + rect2.width();
        int bottom = rect2.top + rect2.height();
        if (shadowView != null) {
            shadowView.setVisibility(View.VISIBLE);
            shadowView.animate().scaleX(1).scaleY(1).setInterpolator(new AccelerateInterpolator()).setDuration(0).start();
            shadowView.bringToFront();
            shadowView.layout(left - shadowAmend, top - shadowAmend, right + shadowAmend, bottom + shadowAmend);
        }
        lastView = view;
        if (lastView != null) {
            if (!(lastView instanceof IPageChangeListener) || lastView instanceof GifCellView) {
                shadowView.animate().scaleX(MAX_ANIM_SCALE).scaleY(MAX_ANIM_SCALE).setDuration(animDuration).start();
                ((View)lastView).animate().scaleX(MAX_ANIM_SCALE).scaleY(MAX_ANIM_SCALE).setInterpolator(new AccelerateInterpolator()).setDuration(animDuration).start();
            }
            if (!(lastView instanceof IPopupAnimatView)) {
                ((View)lastView).bringToFront();
            }
        }

        flyWhiteBorder(rect1, rect2, isMoveCenter);
    }

    private ListenerAnimtaState listenerAnimtaState;

    @Override
    public void setListenerAnimateState(ListenerAnimtaState listenerAnimtaState) {
        this.listenerAnimtaState = listenerAnimtaState;
    }

    @Override
    public void setAnimduartion(int animDuration) {
        this.animDuration = animDuration;

    }

    @Override
    public void removeAllSelect() {
        if (lastView != null) {
            ((View)lastView).animate().scaleX(1).scaleY(1).setInterpolator(new AccelerateInterpolator()).setDuration(300).start();
        }
        lastView = null;
        isAtomicCancel.set(true);
        if (shadowView != null) {
            shadowView.animate().scaleX(1).scaleY(1).setDuration(0).start();
            shadowView.setVisibility(View.GONE);
        }
        mHandler.removeCallbacksAndMessages(null);
    }

    public class AnimTask implements Runnable {
        private static final int ONE_TIME = 40;
        private View view;
        private int from_x, from_y, to_x, to_y;
        private int from_width, from_height, to_width, to_height;
        private int duration;
        private AtomicInteger mAnimCount = new AtomicInteger();
        private boolean isMoveCenter = true;//是否将焦点一直定位到中间

        public AnimTask(View view, int from_x, int from_y, int to_x, int to_y, int from_width, int from_height, int to_width, int to_height, int duration) {
            this.view = view;
            this.from_x = from_x;
            this.from_y = from_y;
            this.to_x = to_x;
            this.to_y = to_y;
            this.from_width = from_width;
            this.from_height = from_height;
            this.to_width = to_width;
            this.to_height = to_height;
            this.duration = duration;
        }

        public AnimTask(View view, Rect rect1, Rect rect2, int duration, boolean isMoveCenter) {
            this.view = view;
            this.from_x = rect1.left;
            this.from_y = rect1.top;
            this.from_width = rect1.width();
            this.from_height = rect1.height();
            this.to_x = rect2.left;
            this.to_y = rect2.top;
            this.to_width = rect2.width();
            this.to_height = rect2.height();
            this.duration = duration;
            this.isMoveCenter = isMoveCenter;
        }

        private void setToViewInfo(int to_x, int to_y, int to_width, int to_height) {
            this.to_x = to_x;
            this.to_y = to_y;
            this.to_width = to_width;
            this.to_height = to_height;
            mAnimCount.set(0);
        }

        public void setToViewInfo(Rect rect2, boolean isMoveCenter) {
            this.isMoveCenter = isMoveCenter;
            this.to_x = rect2.left;
            this.to_y = rect2.top;
            this.to_width = rect2.width();
            this.to_height = rect2.height();
            mAnimCount.set(0);
        }

        private void move(int left, int top, int right, int bottom, int shadowAmend, View view, ViewGroup rootView) {
            if (isMoveCenter) {
                int mScrollX = rootView.getScrollX();
                int mCurrentScrollX = rootView.getScrollX();
                int leftScrollX = (int) (left - 100 * screenScale);
                int rightScrollX = (int) (right + 100 * screenScale - 1920 * screenScale);
                if (mCurrentScrollX > leftScrollX) {
                    mScrollX = leftScrollX;
                }
                if (mCurrentScrollX < rightScrollX) {
                    mScrollX = rightScrollX;
                }
                mScrollX = (int) Math.min(mScrollX, rootView.getMeasuredWidth() - 1920 * screenScale);
                mScrollX = Math.max(mScrollX, 0);
                rootView.scrollTo(mScrollX, 0);
            }
        }

        @Override
        public void run() {
            isAtomicCancel.set(false);
            isAnimRunning.set(true);
            final int num = duration / ONE_TIME;
            mAnimCount.set(0);
            while (mAnimCount.getAndAdd(1) < num) {
                if (isAtomicCancel.get()) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            view.setVisibility(View.GONE);
                            move(to_x, to_y, to_x + to_width, to_y + to_height, shadowAmend, shadowView, rootView);
                        }
                    });
                    finishAnimta();
                    return;
                }
                from_x = from_x + mAnimCount.get() * (to_x - from_x) / num;
                from_y = from_y + mAnimCount.get() * (to_y - from_y) / num;
                from_width = from_width + mAnimCount.get() * (to_width - from_width) / num;
                from_height = from_height + mAnimCount.get() * (to_height - from_height) / num;

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        move(from_x, from_y, from_x + from_width, from_y + from_height, shadowAmend, shadowView, rootView);
                        if (view.getVisibility() != View.VISIBLE) {
                            view.setVisibility(View.VISIBLE);
                        }
                    }
                });
                if (to_x == from_x && to_y == from_y) {
                    finishAnimta();
                    return;
                }
                try {
                    Thread.sleep(ONE_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            finishAnimta();
        }

        private void finishAnimta() {
            if (listenerAnimtaState != null) {
                listenerAnimtaState.finishAnimate();
            }
            isAnimRunning.set(false);
        }
    }

    @Override
    public int getShadowAmend() {
        return shadowAmend;
    }


    @Override
    public void setFocusResIDs(@DrawableRes int[] resIDs) {
        int length = Math.min(this.resIDs.length,resIDs.length);
        System.arraycopy(resIDs,0,this.resIDs,0,length);
    }

    @Override
    public int getShadowResID(int type) {
        type = Math.max(0,type);
        return resIDs[Math.min(type,resIDs.length-1)];
    }


}
