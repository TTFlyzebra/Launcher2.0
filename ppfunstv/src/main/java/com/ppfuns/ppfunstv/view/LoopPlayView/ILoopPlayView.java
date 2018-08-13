package com.ppfuns.ppfunstv.view.LoopPlayView;

import android.graphics.Rect;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by pc1 on 2016/8/3.
 */
public interface ILoopPlayView {
    /**
     * 设置图片的网络请求地址，用以显示图片
     *
     * @param urlList
     */
    ILoopPlayView setImageViewUrls(List<String> urlList);

    ILoopPlayView setImageViewUrls(String urls[]);

    ILoopPlayView setChildViewHeight(int mContentViewHeight);

    ILoopPlayView setChildViewWidth(int mContentViewWidth);

    ILoopPlayView setChildViewPadding(int mContentViewPadding);

    ILoopPlayView setMaxDuration(int mAnimatorDuration);

    /**
     * 设置图片的回调地址
     *
     * @param onChildViewDataChanged
     */
    ILoopPlayView notifyData(OnDataChanged onChildViewDataChanged);

    /**
     * 当前显示图片发生变化
     *
     * @param onImageChanger
     */
    ILoopPlayView setOnChildViewChanged(OnViewChangedListener onImageChanger);


    /**
     * 设置屏幕可见的显示的图片控件数量
     *
     * @param num
     */
    ILoopPlayView setShowImageNum(int num);

    /**
     * 设置第一张获取焦点的图片
     *
     * @param point
     */
    ILoopPlayView setFirstFocusItem(int point);

    int getCurrentItem();

    void initView(int width, int height);

    ILoopPlayView setTopTextHeight(int height);

    void notifyFocusChanged(boolean gainFocus);

    interface OnDataChanged {
        void setChildViewData(ImageView childView, int num, String url);
    }

    interface OnViewChangedListener {
        void onViewChanged(ImageView lostView,ImageView focusView, int currentItem);
    }

    Rect getFocusRect();
}
