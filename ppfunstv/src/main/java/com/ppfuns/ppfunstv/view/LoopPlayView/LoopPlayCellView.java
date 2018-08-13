package com.ppfuns.ppfunstv.view.LoopPlayView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

/**
 * Created by FlyZebra on 2016/8/3.
 */
public class LoopPlayCellView extends BaseLoopPlayView {
    public LoopPlayCellView(Context context) {
        super(context);
    }

    public LoopPlayCellView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LoopPlayCellView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void initView(int width, int height) {
        computeRects(mShowViewNum, width, height, mChildViewWidth, mChildViewHeight);
        mChildViews = new CellChildView[mShowViewNum + 2];
        for (int i = 0; i <= mShowViewNum + 1; i++) {
            //图像
            final CellChildView childView = new CellChildView(context);
            RelativeLayout.LayoutParams lp1 = new LayoutParams(mViewInfos[i].right - mViewInfos[i].left, mViewInfos[i].bottom - mViewInfos[i].top);
            lp1.leftMargin = mViewInfos[i].left;
            lp1.topMargin = mViewInfos[i].top;
            childView.setLayoutParams(lp1);
            childView.setBottomPadding(40);
            mChildViews[i] = childView;
            childView.setScaleType(ImageView.ScaleType.FIT_XY);
            this.addView(childView);
            if (mImgUrls != null) {
                final int num = ((i - mFirstFocusItem) + 1000 * mImgUrls.length) % mImgUrls.length;
                final String url = mImgUrls[num];
                if (onChildViewDataChanged != null) {
                    onChildViewDataChanged.setChildViewData(childView, num, url);
                } else {
                    Glide.with(context)
                            .load(url)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .into(childView);
                }
                if (i > mImgUrls.length) {
                    childView.setVisibility(GONE);
                }
            }

        }
        ItemViewsbringToFront();
    }

    @Override
    public synchronized void goLeft(final int currentItem, final int currentRect, final int durtion) {
        final ImageView lostView = mChildViews[mCurrentRect.get()];
        final ImageView focusView = mChildViews[mCurrentRect.get()-1];
        if (mCurrentRect.get() > 2) {
            if (mCurrentItem.get() <= 0) {
                mCurrentItem.set(0);
                return;
            }
            mCurrentRect.decrementAndGet();
            mCurrentItem.decrementAndGet();
            setFocusRect();
            if (onChildViewChanged != null) {
                onChildViewChanged.onViewChanged(lostView,focusView,mCurrentItem.get());
            }
        } else if (mCurrentRect.get() == 2) {
            if (mCurrentItem.get() == 1) {
                mCurrentItem.decrementAndGet();
                mCurrentRect.decrementAndGet();
                setFocusRect();
                if (onChildViewChanged != null) {
                    onChildViewChanged.onViewChanged(lostView,focusView,mCurrentItem.get());
                }
            } else {
                super.goLeft(currentItem, currentRect, durtion);
            }
        }
    }


    @Override
    public synchronized void goRight(final int currentItem, final int currentRect, final int durtion) {
        final ImageView lostView = mChildViews[mCurrentRect.get()];
        final ImageView focusView = mChildViews[mCurrentRect.get()+1];
        if (mCurrentRect.get() < mShowViewNum - 1) {
            if (mCurrentItem.get() >= mImgUrls.length - 1) {
                mCurrentItem.set(mImgUrls.length - 1);
                return;
            }
            mCurrentRect.incrementAndGet();
            mCurrentItem.incrementAndGet();
            setFocusRect();
            if (onChildViewChanged != null) {
                onChildViewChanged.onViewChanged(lostView,focusView,mCurrentItem.get());
            }
        } else if (mCurrentRect.get() == mShowViewNum - 1) {
            if (mCurrentItem.get() == mImgUrls.length - 2) {
                mCurrentItem.incrementAndGet();
                mCurrentRect.incrementAndGet();
                setFocusRect();
                if (onChildViewChanged != null) {
                    onChildViewChanged.onViewChanged(lostView,focusView,mCurrentItem.get());
                }
            } else  {
                super.goRight(currentItem, currentRect, durtion);
            }
        }else{

        }
    }


    @Override
    protected void computeRects(int mShowViewNum, int width, int height, int mContentViewWidth, int mContentViewHeight) {
        mViewInfos = new LoopViewInfo[mShowViewNum + 2];
        int w = (width - (mShowViewNum - 1) * (mChildViewPadding * 2)) / mShowViewNum;
        for (int i = 0; i < mViewInfos.length; i++) {
            mViewInfos[i] = new LoopViewInfo();
            mViewInfos[i].left = mChildViewPadding * 2 * (i - 1) + (i - 1) * w;
            mViewInfos[i].right = mViewInfos[i].left + w;
            mViewInfos[i].top = mTopTextHeight;
            mViewInfos[i].bottom = mViewInfos[i].top + mContentViewHeight;
            mViewInfos[i].scale = 1.0f;
        }
        setFocusRect();
    }

    public void setFocusRect() {
        mFocusRect.top = mViewInfos[mCurrentRect.get()].top;
        mFocusRect.left = mViewInfos[mCurrentRect.get()].left;
        mFocusRect.right = mViewInfos[mCurrentRect.get()].right;
        mFocusRect.bottom = mViewInfos[mCurrentRect.get()].bottom - 40;
    }

    @Override
    public void ItemViewsbringToFront() {
    }

}
