package com.flyzebra.ppfunstv.view.TvView.FocusAnimat;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.DrawableRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.flyzebra.ppfunstv.R;
import com.flyzebra.ppfunstv.utils.DisplayUtils;
import com.flyzebra.ppfunstv.view.TvView.CellView.GifCellView;
import com.flyzebra.ppfunstv.view.TvView.IPageChangeListener;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by fagro on 17-5-4.
 */

public class TvPageNotMoveAnimat implements ITvFocusAnimat {
    private float screenScale = 1.0f;

    private FrameLayout shadowView;
    private View lastView;
    private float scale = 1.05f;//动画放大倍数
    private float scaleDefault = 1f;
    private int shadowAmend = 4;//阴影修正值单位DP

    private int animDuration = 200;
    private Handler mHandler = new Handler(Looper.getMainLooper());


    private int shadowRid;

    private static ExecutorService executors = Executors.newFixedThreadPool(1);

    /**
     * 线程同步操作原子数
     */
    private AtomicBoolean isCancel = new AtomicBoolean(false);

    public TvPageNotMoveAnimat(ViewGroup viewGroup) {
        shadowView = new FocusShadowView(viewGroup.getContext());
        viewGroup.addView(shadowView);
        shadowView.setBackgroundResource(R.drawable.tv_shadow_bg);
        screenScale = DisplayUtils.getMetrices((Activity) viewGroup.getContext()).widthPixels / 1920f;
    }

    @Override
    public void startAnim(final IAnimatView view1, final IAnimatView view2) {
        if (view1 != null) {
            ((View)view1).animate().scaleX(scaleDefault).scaleY(scaleDefault).setDuration(animDuration).start();
        }

        if (view2 != null) {
            flyWhiteBorder((View)view1, (View)view2, animDuration);
            shadowView.animate().scaleX(scaleDefault / scale).scaleY(scaleDefault / scale).setDuration(0).start();
            if (view2 instanceof IPageChangeListener &&(!(view2 instanceof GifCellView))) {
//                view2.animate().scaleX(scaleDefault).scaleY(scaleDefault).setDuration(animDuration).start();
//                shadowView.animate().scaleX(scaleDefault / scale).scaleY(scaleDefault / scale).setDuration(0).start();
            } else {
//                shadowView.animate().scaleX(scaleDefault / scale).scaleY(scaleDefault / scale).setDuration(0).start();
                shadowView.animate().scaleX(scaleDefault).scaleY(scaleDefault).setDuration(animDuration).start();
                ((View)view2).animate().scaleX(scale).scaleY(scale).setDuration(animDuration).start();
//                shadowView.animate().scaleX(scale).scaleY(scale).setDuration(animDuration).start();
            }
        }


        if (view2 != null) {
            shadowView.bringToFront();
            ((View)view2).bringToFront();
            lastView = (View)view2;
        }

        if (shadowView.getVisibility() != View.VISIBLE) {
            shadowView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void startAnim(IAnimatView objView, boolean isMoverCenter) {

    }

    @Override
    public void startAnim(IAnimatView srcView, IAnimatView objView, boolean isMoveCenter) {

    }

    @Override
    public void removeAllSelect() {
        isCancel.set(true);
        if (lastView != null) {
            lastView.animate().scaleX(1).scaleY(1).setDuration(animDuration).start();
            lastView.setSelected(false);
        }
        if (shadowView != null) {
            shadowView.animate().scaleX(scaleDefault / scale).scaleY(scaleDefault / scale).setDuration(animDuration).start();
            shadowView.setVisibility(View.GONE);
        }
        mHandler.removeCallbacksAndMessages(null);
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

    public void flyWhiteBorder(final View view1, View view2, final int duration) {
        isCancel.set(true);
        final int to_x = view2.getLeft();
        final int to_y = view2.getTop();

        final int to_width = view2.getWidth();
        final int to_height = view2.getHeight();

        int tmpW = Math.round(0.5f + to_width * scale - to_width) / 2;
        int tmpH = Math.round(0.5f + to_height * scale - to_height) / 2;
        shadowView.layout(to_x - tmpW - shadowAmend, to_y - tmpH - shadowAmend, to_width + to_x + tmpW + shadowAmend, to_y + to_height + tmpH + shadowAmend);
    }


    public void setShadowAmend(int shadowAmend) {
        this.shadowAmend = Math.round(0.5f + shadowAmend * screenScale);
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
