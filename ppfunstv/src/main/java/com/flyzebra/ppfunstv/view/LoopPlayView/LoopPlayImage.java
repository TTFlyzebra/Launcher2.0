package com.flyzebra.ppfunstv.view.LoopPlayView;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by FlyZebra on 2016/8/3.
 */
public class LoopPlayImage extends BaseLoopPlayView {
    public LoopPlayImage(Context context) {
        super(context);
    }

    public LoopPlayImage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LoopPlayImage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void computeRects(int mShowImg, int width, int height, int mContentViewWidth, int mContentViewHeight) {
        mViewInfos = new LoopViewInfo[mShowImg + 2];
        for (int i = 0; i < mViewInfos.length; i++) {
            mViewInfos[i] = new LoopViewInfo();
            mViewInfos[i].left = getPaddingLeft() + (i - 1) * mContentViewWidth;
            mViewInfos[i].right = mViewInfos[i].left + mContentViewWidth;
            mViewInfos[i].top = (height - mContentViewHeight) / 2;
            mViewInfos[i].bottom = mViewInfos[i].top + mContentViewHeight;
            mViewInfos[i].scale = 1.0f;
        }
    }

    @Override
    public void ItemViewsbringToFront() {
    }

}
