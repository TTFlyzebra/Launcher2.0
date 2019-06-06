package com.flyzebra.ppfunstv.view.TvView.FocusAnimat;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.DrawableRes;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;

import com.flyzebra.ppfunstv.R;
import com.flyzebra.ppfunstv.utils.DisplayUtils;
import com.flyzebra.ppfunstv.utils.FlyLog;
import com.flyzebra.ppfunstv.view.TvView.CellView.TvPageItemView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created FlyZebra on 2016/6/15.
 */
public class PopupTvAnimat implements ITvFocusAnimat {
    private FrameLayout shadowView;
    private int animDuration = 200;
    private int shadowAmend = 26;//阴影修正值单位DP
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
    private AtomicBoolean isCancel = new AtomicBoolean(false);

    private float screenScale = 1.0f;

    private IAnimatView srcView;
    private IAnimatView objView;

    private float SRC_SCALE = 1.0f;
    private float OBJ_SCALE = 1.05f;

    int resIDs[] = {R.drawable.tv_focus0, R.drawable.tv_focus1, R.drawable.tv_focus2,R.drawable.tv_focus0};
    int currentID = 0;

    private ListenerAnimtaState listenerAnimtaState;

    private AtomicInteger count = new AtomicInteger(0);

    private AnimTask mAnimTask;

    public PopupTvAnimat(ViewGroup viewGroup) {
        rootView = viewGroup;
        shadowView = new FocusShadowView(viewGroup.getContext());
        viewGroup.addView(shadowView);
        shadowView.setBackgroundResource(R.drawable.tv_focus0);
        DisplayMetrics dm = DisplayUtils.getMetrices((Activity) viewGroup.getContext());
        screenScale = dm.widthPixels / 1920f;
        shadowAmend = Math.round(0.5f + (shadowAmend * screenScale));
    }

    @Override
    public void setListenerAnimateState(ListenerAnimtaState listenerAnimtaState) {
        this.listenerAnimtaState = listenerAnimtaState;
    }


    @Override
    public void startAnim(IAnimatView srcView, IAnimatView objView) {
        startAnim(srcView,objView,false);
    }

    @Override
    public void startAnim(IAnimatView objView, boolean isMoverCenter) {
        startAnim(srcView,objView,isMoverCenter);
    }

    @Override
    public void startAnim(IAnimatView srcView, IAnimatView objView, boolean isMoveCenter) {
        Rect srcRect = null;
        Rect objRect = null;

        if(srcView!=null){
            srcRect = srcView.getOldRect();
        }

        if(objView!=null){
            objRect = objView.getFocusRect();
        }

        flyWhiteBorder(objView,srcRect,objRect,isMoveCenter);
    }


    @Override
    public void flyWhiteBorder(Rect rect1, Rect rect2, boolean isMoveCenter) {
        if (rect1 == null) {
            rect1 = new Rect(rect2.left, rect2.top, rect2.right, rect2.bottom);
        }
        if (rect1.left == 0 && rect1.right == 0 && rect1.top == 0 && rect1.bottom == 0) {
            rect1 = new Rect(rect2.left, rect2.top, rect2.right, rect2.bottom);
        }
        final IAnimatView view = objView;
        count.incrementAndGet();
        int time = Math.max(animDuration/ Math.max(count.get(), 1), 100);
        FlyLog.d("fly anim time = %d ,count =%d", time, count.get());
        mAnimTask = new AnimTask(view, rect1, rect2, time, isMoveCenter);
        executors.execute(mAnimTask);
    }

    @Override
    public void flyWhiteBorder(IAnimatView view, Rect rect1, Rect rect2, boolean isMoveCenter) {
        if(view==null||(view.getFocusType()==-1)){
            shadowView.setVisibility(View.INVISIBLE);
        }else{
            shadowView.setVisibility(View.VISIBLE);
        }

        if(view==null){
            srcView = null;
            return;
        }

        objView = view;

        if (objView.getFocusZorder() == 1) {
            shadowView.bringToFront();
            ((View)objView).bringToFront();
        }else {
            ((View)objView).bringToFront();
            shadowView.bringToFront();
        }

        if (srcView != null) {
            ((View)srcView).animate().scaleX(SRC_SCALE).scaleY(SRC_SCALE).setDuration(animDuration).start();
            if (srcView.getReflectImageView() != null) {
                srcView.getReflectImageView().animate().scaleX(SRC_SCALE).scaleY(SRC_SCALE).setDuration(animDuration).start();
            }
        }

        if ((srcView != null && (srcView.getFocusZorder() == 1))) {
            ((View)srcView).bringToFront();
        }


        if (objView.getFocusScale() == 0) {
            ((View)objView).animate().scaleX(OBJ_SCALE).scaleY(OBJ_SCALE).setDuration(animDuration).start();
            shadowView.animate().scaleX(OBJ_SCALE).scaleY(OBJ_SCALE).setDuration(animDuration).start();
            if (objView.getReflectImageView() != null) {
                objView.getReflectImageView().animate().scaleX(OBJ_SCALE).scaleY(OBJ_SCALE).setDuration(animDuration).start();
            }
        } else {
            ((View)objView).animate().scaleX(SRC_SCALE).scaleY(SRC_SCALE).setDuration(animDuration).start();
            shadowView.animate().scaleX(SRC_SCALE).scaleY(SRC_SCALE).setDuration(animDuration).start();
            if (objView.getReflectImageView() != null) {
                objView.getReflectImageView().animate().scaleX(SRC_SCALE).scaleY(SRC_SCALE).setDuration(animDuration).start();
            }
        }

        flyWhiteBorder(rect1, rect2, isMoveCenter);
        srcView = objView;

    }

    @Override
    public void removeAllSelect() {
        isCancel.set(true);
        if (shadowView != null) {
            shadowView.animate().scaleX(SRC_SCALE).scaleY(SRC_SCALE).setDuration(0).start();
            shadowView.setVisibility(View.GONE);
        }
        if (srcView != null) {
            ((View)srcView).animate().scaleX(SRC_SCALE).scaleY(SRC_SCALE).setDuration(0).start();
            srcView = null;
        }
        mHandler.removeCallbacksAndMessages(null);
    }

    public class AnimTask implements Runnable {
        private static final float ONE_TIME = 20;
        private int from_x, from_y, to_x, to_y;
        private int from_width, from_height, to_width, to_height;
        private int duration;
        private AtomicInteger mAnimCount = new AtomicInteger();
        private boolean isMoveCenter = true;//是否将焦点一直定位到中间
        private Interpolator interpolator = new DecelerateInterpolator(1f);

        public AnimTask(IAnimatView toView, Rect rect1, Rect rect2, int duration, boolean isMoveCenter) {
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


        private void move(int left, int top, int right, int bottom, int shadowAmend, View shadowView, ViewGroup rootView) {
            shadowView.layout(left - shadowAmend, top - shadowAmend, right + shadowAmend, bottom + shadowAmend);
            if (isMoveCenter) {
                //横版焦点保持在中点
                int mScrollX = (int) (left + (right - left) / 2 - 960 * screenScale);
                mScrollX = (int) Math.min(mScrollX, rootView.getMeasuredWidth() - 1920 * screenScale);
                mScrollX = Math.max(mScrollX, 0);
//                int startX = rootView.getScrollX();
//                FlyLog.d("PopupTvView move startX=%d, mScrollX = %d, distance=%d",startX,mScrollX,mScrollX - startX);

                int mScrollY = (int) (top + (bottom - top) / 2 - 540 * screenScale);
                mScrollY = (int) Math.min(mScrollY, rootView.getMeasuredHeight() - 1080 * screenScale);
                mScrollY = Math.max(mScrollY, 0);

                //竖版底部预留100相素
//                int mScrollY = rootView.getScrollY();
//                int mCurrentScrollY = rootView.getScrollY();
//                int topScrollY = top - 220;
//                int bottomScrollY = (int) (bottom + 100 * screenScale - 1080 * screenScale);
//                if (mCurrentScrollY > topScrollY) {
//                    mScrollY = topScrollY;
//                }
//                if (mCurrentScrollY < bottomScrollY) {
//                    mScrollY = bottomScrollY;
//                }
//                mScrollY = (int) Math.min(mScrollY, rootView.getMeasuredHeight() - 1080 * screenScale);
//                mScrollY = Math.max(mScrollY, 0);

                rootView.scrollTo(mScrollX, mScrollY);
//                FlyLog.d("move mScrollX=%d, mScrollY = %d", mScrollX, mScrollY);
            }
        }

        @Override
        public void run() {
            isCancel.set(false);
            isAnimRunning.set(true);
            final int num = (int) (duration / ONE_TIME);
//            final int x = from_x;
//            final int y = from_y;
//            final int width = from_width;
//            final int height = from_height;
            mAnimCount.set(0);
            while (mAnimCount.getAndIncrement() <= num) {
                if (isCancel.get()) {
                    FlyLog.d("PopupTvView move Cancel move x=%d,y=%d,width=%d,height=%d", to_x, to_y, to_width, to_height);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
//                            shadowView.setVisibility(View.GONE);
                            move(to_x, to_y, to_x + to_width, to_y + to_height, shadowAmend, shadowView, rootView);
                        }
                    });
                    finishAnimate();
                    return;
                }

////                //减速动画
                from_x = from_x + mAnimCount.get() * (to_x - from_x) / num;
                from_y = from_y + mAnimCount.get() * (to_y - from_y) / num;
                from_width = from_width + mAnimCount.get() * (to_width - from_width) / num;
                from_height = from_height + mAnimCount.get() * (to_height - from_height) / num;


                //插值器动画
//                float input = mAnimCount.get()/(float)num;
//                from_x = (int) (x + (to_x - x)* interpolator.getInterpolation(input));
//                from_y = (int) (y + (to_y - y)*interpolator.getInterpolation(input));
//                from_width = (int) (width + (to_width - width)*interpolator.getInterpolation(input));
//                from_height = (int) (height + (to_height - height)*interpolator.getInterpolation(input));

                if (Math.abs(from_x - to_x) < 2) {
                    from_x = to_x;
                }
                if (Math.abs(from_y - to_y) < 2) {
                    from_y = to_y;
                }
                if (Math.abs(from_width - to_width) < 2) {
                    from_width = to_width;
                }
                if (Math.abs(from_height - to_height) < 2) {
                    from_height = to_height;
                }

                final int left = from_x;
                final int top = from_y;
                final int right = from_x + from_width;
                final int bottom = from_y + from_height;

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        move(left, top, right, bottom, shadowAmend, shadowView, rootView);
                    }
                });
                if (to_x == from_x && to_y == from_y && to_width == from_width && to_height == from_height) {
                    finishAnimate();
                    return;
                }
                try {
                    Thread.sleep((long) ONE_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            finishAnimate();
        }

        private void finishAnimate() {
            count.decrementAndGet();
            if (objView.getFocusType() < 0) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        shadowView.setVisibility(View.GONE);
                    }
                });
            }
            if (listenerAnimtaState != null) {
                listenerAnimtaState.finishAnimate();
            }
            isAnimRunning.set(false);

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    PopupTvAnimat.this.shadowView.bringToFront();
                    if (objView.getFocusZorder() == 1) {
                        ((View)objView).bringToFront();
                    }
                }
            });
        }

        public Rect getRect() {
            return new Rect(from_x, from_y, from_x + from_width, from_y + from_height);
        }
    }

    private void flyWhiteBorder(final View view1, View view2) {
        isCancel.set(true);
        int to_x = view2.getLeft();
        int from_x = view1 == null ? to_x : view1.getLeft();
        int to_y = view2.getTop();
        int from_y = view1 == null ? to_y : view1.getTop();

        int to_width = view2.getWidth();
        int from_width = view1 == null ? to_width : view1.getWidth();
        int to_height = view2.getHeight();
        int from_height = view1 == null ? to_height : view1.getHeight();

        Rect rect1 = new Rect(from_x, from_y, from_x + from_width, from_y + from_height);
        Rect rect2 = new Rect(to_x, to_y, to_x + to_width, to_y + to_height);
        shadowView.layout(from_x - shadowAmend, from_y - shadowAmend, from_width + from_x + shadowAmend, from_y + from_height + shadowAmend);
        executors.execute(new AnimTask((TvPageItemView) view2, rect1, rect2, animDuration, true));
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
    public void setAnimduartion(int animDuration) {
        this.animDuration = animDuration;
    }

    @Override
    public int getShadowResID(int type) {
        type = Math.max(0,type);
        return resIDs[Math.min(type,resIDs.length-1)];
    }

    public void setShadowAmend(int shadowAmend) {
        this.shadowAmend = shadowAmend;
    }

}
