package com.flyzebra.ppfunstv.view.LoopPlayView;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by FlyZebra on 2016/8/3.
 */
public class LoopPlayImage2 extends BaseLoopPlayView {
    public LoopPlayImage2(Context context) {
        super(context);
    }

    public LoopPlayImage2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LoopPlayImage2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void computeRects(int mShowImg, int width, int height, int mContentViewWidth, int mContentViewHeight) {
        mViewInfos = new LoopViewInfo[mShowImg + 2];
        int stepWidth = (int) ((width - mContentViewWidth - getPaddingLeft() - getPaddingRight()) / (float) (mShowImg - 1));//使用Math.ceil修正1相素值
        for (int i = 0; i < mViewInfos.length; i++) {
            mViewInfos[i] = new LoopViewInfo();
            int num = i - mFirstFocusItem;
            float scale = (float) Math.pow(0.9f, Math.abs(num));
            if (num < 0) {
                mViewInfos[i].left = getPaddingLeft() + (i - 1) * stepWidth;
                mViewInfos[i].right = (int) (mViewInfos[i].left + mContentViewWidth * scale);
                mViewInfos[i].top = (int) ((1-scale)*mContentViewHeight/2);
                mViewInfos[i].bottom = mViewInfos[i].top+(int) (mContentViewHeight * scale);
            } else if (num > 0) {
                mViewInfos[i].right = getPaddingLeft() + (i - 1) * stepWidth + mContentViewWidth;
                mViewInfos[i].left = (int) (mViewInfos[i].right - mContentViewWidth * scale);
                mViewInfos[i].top = (int) ((1-scale)*mContentViewHeight/2);
                mViewInfos[i].bottom = mViewInfos[i].top+(int) (mContentViewHeight * scale);
            } else {
                mViewInfos[i].left = getPaddingLeft() + (i - 1) * stepWidth;
                mViewInfos[i].right = mViewInfos[i].left + mContentViewWidth;
                mViewInfos[i].top = (int) ((1-scale)*mContentViewHeight/2);
                mViewInfos[i].bottom = mViewInfos[i].top+(int) (mContentViewHeight * scale);
            }
            mViewInfos[i].scale = 1.0f;
        }
    }

    @Override
    public void ItemViewsbringToFront() {
        try {
            for (int i = 0; i < mFirstFocusItem; i++) {
                mChildViews[i + 1].bringToFront();
                mChildViews[mFirstFocusItem + (mFirstFocusItem - i - 1)].bringToFront();
            }
            mChildViews[mFirstFocusItem].bringToFront();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
