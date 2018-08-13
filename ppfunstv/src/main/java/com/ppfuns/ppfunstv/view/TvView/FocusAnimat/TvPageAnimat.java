package com.ppfuns.ppfunstv.view.TvView.FocusAnimat;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.DrawableRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.ppfuns.ppfunstv.R;
import com.ppfuns.ppfunstv.utils.DisplayUtils;
import com.ppfuns.ppfunstv.view.TvView.CellView.GifCellView;
import com.ppfuns.ppfunstv.view.TvView.CellView.IrregularCellView;
import com.ppfuns.ppfunstv.view.TvView.CellView.TvPageItemView;
import com.ppfuns.ppfunstv.view.TvView.IPageChangeListener;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created FlyZebra on 2016/6/15.
 */
public class TvPageAnimat implements ITvFocusAnimat {
    private float screenScale = 1.0f;

    private FrameLayout shadowView;
    private View lastView;
    private float scale = 1.08f;//动画放大倍数
    private float scaleDefault = 1f;
    private int shadowAmend = 16;//阴影修正值单位DP
    private int animDuration = 200;
    private int shadowRid;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private final static ExecutorService executors = Executors.newFixedThreadPool(1);

    /**
     * 线程同步操作原子数
     */
    private AtomicBoolean isAnimRunning = new AtomicBoolean(false);
    private AtomicBoolean isCancel = new AtomicBoolean(false);

    public TvPageAnimat(ViewGroup viewGroup) {
        shadowView = new FocusShadowView(viewGroup.getContext());
        viewGroup.addView(shadowView);
        shadowView.setBackgroundResource(R.drawable.tv_focus);
        screenScale = DisplayUtils.getMetrices((Activity) viewGroup.getContext()).widthPixels / 1920f;
        shadowAmend = DisplayUtils.dip2px(viewGroup.getContext(), shadowAmend);
        shadowAmend = Math.round(0.5f + shadowAmend * screenScale);
    }


    public void setShadowAmend(int shadowAmend) {
        this.shadowAmend = Math.round(0.5f + shadowAmend * screenScale);
    }


    @Override
    public void startAnim(IAnimatView view1, final IAnimatView view2) {
        //设置外发光颜色
        if (view1 != null) {
            doScaleAnim((View)view1, null, scaleDefault, animDuration);
        }

        if (view2 != null) {
            if (view2 instanceof IPageChangeListener && (!(view2 instanceof GifCellView))) {
                doScaleAnim((View)view2, shadowView, scaleDefault, animDuration);
            } else {
                doScaleAnim((View)view2, shadowView, scale, animDuration);
            }
        }

        if (view2 != null) {
            ((View)view2).bringToFront();
            if (!(view2 instanceof IrregularCellView)) {
                shadowView.bringToFront();
            }
            flyWhiteBorder((View)view1, (View)view2);
            lastView = (View)view2;
        }

        if (shadowView.getVisibility() != View.VISIBLE) {
            shadowView.setVisibility(View.VISIBLE);
        }
    }

    private void doScaleAnim(View targetView, View shadowView, float toScale, int duration) {
        if (null != targetView) {
            targetView.animate().scaleX(toScale).scaleY(toScale).setDuration(duration).start();
            if (targetView instanceof TvPageItemView) {
                TvPageItemView tpView = (TvPageItemView) targetView;
                View reflectView = tpView.getReflectImageView();
                if (reflectView != null) {
                    reflectView.animate().scaleX(toScale).scaleY(toScale).setDuration(duration).start();
                }
            }
        }
        if (null != shadowView) {
            shadowView.animate().scaleX(toScale).scaleY(toScale).setDuration(duration).start();
        }

        return;
    }

    @Override
    public void removeAllSelect() {
        isCancel.set(true);
        doScaleAnim(lastView,shadowView,scaleDefault,animDuration);
        if (lastView != null) {
            lastView.setSelected(false);
        }
        if (shadowView != null) {
            shadowView.setVisibility(View.GONE);
        }
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void startAnim(IAnimatView objView, boolean isMoverCenter) {

    }

    @Override
    public void startAnim(IAnimatView srcView, IAnimatView objView, boolean isMoveCenter) {

    }

    @Override
    public void flyWhiteBorder(Rect rect1, Rect rect2, boolean isMoveCenter) {

    }

    @Override
    public void flyWhiteBorder(IAnimatView objView, Rect rect1, Rect rect2, boolean isMoveCenter) {

    }


    @Override
    public int getShadowAmend() {
        return shadowAmend;
    }

    @Override
    public void setListenerAnimateState(ListenerAnimtaState listenerAnimtaState) {

    }

    @Override
    public void setAnimduartion(int animDuration) {
        this.animDuration = animDuration;
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
        shadowView.layout(from_x - shadowAmend, from_y - shadowAmend, from_width + from_x + shadowAmend, from_y + from_height + shadowAmend);
        executors.execute(new AnimTask(shadowView, from_x, from_y, to_x, to_y, from_width, from_height, to_width, to_height, animDuration));
    }

    public class AnimTask implements Runnable {
        private final int ONE_TIME = 40;
        private View view;
        private int from_x, from_y, to_x, to_y;
        private int from_width, from_height, to_width, to_height;
        private int duration;

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

        @Override
        public void run() {
            isCancel.set(false);
            isAnimRunning.set(true);
            final int num = duration / ONE_TIME;
            final int startLeft = from_x;
            final int startTop = from_y;
            final int startWidth = from_width;
            final int startHeight = from_height;
            for (int i = 0; i < num; i++) {
                if (isCancel.get()) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            view.setVisibility(View.GONE);
                            view.layout(to_x - shadowAmend, to_y - shadowAmend, to_x + to_width + shadowAmend, to_y + to_height + shadowAmend);
                        }
                    });
                    isAnimRunning.set(false);
                    return;
                }
                final int left = startLeft + i * (to_x - from_x) / num;
                final int top = startTop + i * (to_y - from_y) / num;
                final int right = left + startWidth + i * (to_width - from_width) / num;
                final int bottom = top + startHeight + i * (to_height - from_height) / num;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        view.layout(left - shadowAmend, top - shadowAmend, right + shadowAmend, bottom + shadowAmend);
                        if (view.getVisibility() != View.VISIBLE) {
                            view.setVisibility(View.VISIBLE);
                        }
                    }
                });
                if (to_x == from_x && to_y == from_y) {
                    return;
                }
                try {
                    Thread.sleep(ONE_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    view.layout(to_x - shadowAmend, to_y - shadowAmend, to_x + to_width + shadowAmend, to_y + to_height + shadowAmend);
                }
            });
            isAnimRunning.set(false);
        }
    }

    int resIDs[] = {R.drawable.tv_focus0, R.drawable.tv_focus1, R.drawable.tv_focus2,R.drawable.tv_focus4,R.drawable.tv_focus4};

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
