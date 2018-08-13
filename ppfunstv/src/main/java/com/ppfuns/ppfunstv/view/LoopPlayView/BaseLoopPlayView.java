package com.ppfuns.ppfunstv.view.LoopPlayView;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ppfuns.ppfunstv.utils.FlyLog;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * Created by FlyZebra on 2016/8/3.
 */
public abstract class BaseLoopPlayView extends RelativeLayout implements ILoopPlayView {
    protected int mTopTextHeight;
    //    protected int width;
//    protected int height;
    protected Context context;
    protected String[] mImgUrls;//图片网络请求地址
    protected AnimatorSet mAnimatorSet;
    protected int mMaxDuration = 300;//屏幕滚动延续时长
    protected int mMinDuration = 100;//屏幕滚动延续时长
    protected int mChildViewWidth = 200;//最大图片的宽度
    protected int mChildViewHeight = 150;//最大图片的高度
    protected int mChildViewPadding = 0;//图片填充内容
    protected AtomicInteger mCurrentItem = new AtomicInteger(0);
    protected long currentTime;
    protected ImageView[] mChildViews;//用来显示图片的控件
    protected LoopViewInfo[] mViewInfos;

    /**
     * 控件显示图片的数量
     */
    protected int mShowViewNum = 4;//显示的图片数

    protected AtomicInteger mCurrentRect = new AtomicInteger(1);


    protected int mFirstFocusItem = 1;//第一张获取焦点的图片

    private Handler mHandle = new Handler();


    private long systemTime = 0;

    private static final ExecutorService executors = Executors.newFixedThreadPool(1);


    protected Rect mFocusRect = new Rect();

    public BaseLoopPlayView(Context context) {
        this(context, null);
    }

    public BaseLoopPlayView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseLoopPlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(Context context) {
        this.context = context;
        systemTime = System.currentTimeMillis();
        init(context);
    }

    /**
     * 根据设置参数（未设置的取默认参数）初始化控件
     *
     * @param context
     */
    public void init(Context context) {
        this.context = context;
    }

    /**
     * 创建播放轮播动画所需的控件，
     * NOTE:轮播动画所需的控件数并不等于mMaxImg，个数由具体动画决定。
     */
    @Override
    public void initView(int width, int height) {
        computeRects(mShowViewNum, width, height, mChildViewWidth, mChildViewHeight);
        mChildViews = new ImageView[mShowViewNum + 2];
        for (int i = 0; i <= mShowViewNum + 1; i++) {
            //图像
            final ImageView childView = new ImageView(context);
            RelativeLayout.LayoutParams lp1 = new LayoutParams(mViewInfos[i].right - mViewInfos[i].left, mViewInfos[i].bottom - mViewInfos[i].top);
            lp1.leftMargin = mViewInfos[i].left;
            lp1.topMargin = mViewInfos[i].top;
            childView.setLayoutParams(lp1);
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

                //图片数量不足显示数量时隐藏控件
//                if (i > mImgUrls.length) {
//                    childView.setVisibility(GONE);
//                }
            }

        }
        ItemViewsbringToFront();
    }

    /**
     * 计算每个图片的放置位置
     */
    protected abstract void computeRects(int mMaxImg, int width, int height, int mContentViewWidth, int mContentViewHeight);

    private boolean isRunAnim = false;

    Runnable setAnimState = new Runnable() {
        @Override
        public void run() {
            isRunAnim = false;
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        currentTime = System.currentTimeMillis();
        int mDuration = (int) Math.min((currentTime - systemTime), mMaxDuration);
        mDuration = Math.max(mMinDuration, mDuration);
        int finalMDuration = mDuration;
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_DOWN:
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                goLeft(mCurrentItem.get(), mCurrentRect.get(), finalMDuration);
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                goRight(mCurrentItem.get(), mCurrentRect.get(), finalMDuration);
                break;
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_DPAD_CENTER:
                Toast.makeText(context, "选中子项-->" + mCurrentItem.get(), Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    public synchronized void goLeft(final int currentItem, final int currentRect, final int finalMDuration) {
        if (!isRunAnim) {
            isRunAnim = true;
//            final int currentItem = mCurrentItem.get();
            final ImageView lostView = mChildViews[mCurrentRect.get()];
            final ImageView focusView = mChildViews[mCurrentRect.get() - 1];
            mCurrentItem.set((currentItem + mImgUrls.length - 1) % mImgUrls.length);
            notifyChildViewChanged(lostView, focusView, mCurrentItem.get());
//            executors.execute(new Runnable() {
//                @Override
//                public void run() {
//                    mHandle.post(new Runnable() {
//                        @Override
//                        public void run() {
            if (context == null || ((Activity) context).isFinishing()) {
                FlyLog.d("Activity finish ,no need to call goLeftItem");
                return;
            }
            goLeftItem(mChildViews, currentItem, currentRect, finalMDuration);
//                        }
//                    });
//                    try {
//                        Thread.sleep(finalMDuration);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
            startAnimta(true);
            mHandle.postDelayed(setAnimState, mMinDuration);
            systemTime = currentTime;
        }
    }

    public synchronized void goRight(final int currentItem, final int currentRect, final int finalMDuration) {
        if (!isRunAnim) {
            isRunAnim = true;
            final ImageView lostView = mChildViews[mCurrentRect.get()];
            final ImageView focusView = mChildViews[mCurrentRect.get() + 1];
            mCurrentItem.set((currentItem + 1) % mImgUrls.length);
            notifyChildViewChanged(lostView, focusView, mCurrentItem.get());
//            executors.execute(new Runnable() {
//                @Override
//                public void run() {
//                    mHandle.post(new Runnable() {
//                        @Override
//                        public void run() {
            if (context == null || ((Activity) context).isFinishing()) {
                FlyLog.d("Activity finish ,no need to call goRightItem");
                return;
            }
            goRightItem(mChildViews, currentItem, currentRect, finalMDuration);
//                        }
//                    });
//                    try {
//                        Thread.sleep(finalMDuration);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//            });
            startAnimta(false);
            mHandle.postDelayed(setAnimState, mMinDuration);
            systemTime = currentTime;
        }
    }

    protected void notifyChildViewChanged(ImageView lostView, ImageView focusView, int mCurrentItem) {
        if (onChildViewChanged != null) {
            try {
                onChildViewChanged.onViewChanged(lostView, focusView, mCurrentItem);
            } catch (Exception e) {
                e.printStackTrace();
                FlyLog.d(e.toString());
            }

        }
    }

    public void goRightItem(ImageView[] mChildViews, int mCurrentItem, int mCurrentRect, int mAnimatorDuration) {
        Set<Animator> mAnimSet = new HashSet<>();
        //向左移动，第一张图片要特殊处理，更换显示图像
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(mChildViews[0], "translationX", mViewInfos[mViewInfos.length - 1].left - mViewInfos[0].left, mViewInfos[mViewInfos.length - 1].left - mViewInfos[0].left);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mChildViews[0], "scaleX", mViewInfos[mViewInfos.length - 1].scale, mViewInfos[mViewInfos.length - 1].scale);
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(mChildViews[0], "scaleY", mViewInfos[mViewInfos.length - 1].scale, mViewInfos[mViewInfos.length - 1].scale);
        ObjectAnimator animator4 = ObjectAnimator.ofInt(new WrapperView(mChildViews[0]), "width", mViewInfos[mViewInfos.length - 1].right - mViewInfos[mViewInfos.length - 1].left, mViewInfos[mViewInfos.length - 1].right - mViewInfos[mViewInfos.length - 1].left);
        ObjectAnimator animator5 = ObjectAnimator.ofInt(new WrapperView(mChildViews[0]), "height", mViewInfos[mViewInfos.length - 1].bottom - mViewInfos[mViewInfos.length - 1].top, mViewInfos[mViewInfos.length - 1].bottom - mViewInfos[mViewInfos.length - 1].top);
        mAnimSet.add(animator1);
        mAnimSet.add(animator2);
        mAnimSet.add(animator3);
        mAnimSet.add(animator4);
        mAnimSet.add(animator5);

        if (mImgUrls != null) {
            final int num = (mCurrentItem + mChildViews.length - mFirstFocusItem + mImgUrls.length * 1000 - mCurrentRect + 1) % mImgUrls.length;
            final ImageView imageView = mChildViews[0];
            final String url = mImgUrls[num];
            if (onChildViewDataChanged != null) {
                onChildViewDataChanged.setChildViewData(imageView, num, url);
            } else {
                Glide.with(context)
                        .load(url)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(imageView);
            }

        }

        for (int i = 1; i < mChildViews.length; i++) {
            ObjectAnimator anim1 = ObjectAnimator.ofFloat(mChildViews[i], "translationX", mViewInfos[i].left - mViewInfos[i - 1].left, 0);
            ObjectAnimator anim2 = ObjectAnimator.ofFloat(mChildViews[i], "scaleX", mViewInfos[i].scale, mViewInfos[i - 1].scale);
            ObjectAnimator anim3 = ObjectAnimator.ofFloat(mChildViews[i], "scaleY", mViewInfos[i].scale, mViewInfos[i - 1].scale);
            ObjectAnimator anim4 = ObjectAnimator.ofInt(new WrapperView(mChildViews[i]), "width", mViewInfos[i].right - mViewInfos[i].left, mViewInfos[i - 1].right - mViewInfos[i - 1].left);
            ObjectAnimator anim5 = ObjectAnimator.ofInt(new WrapperView(mChildViews[i]), "height", mViewInfos[i].bottom - mViewInfos[i].top, mViewInfos[i - 1].bottom - mViewInfos[i - 1].top);

            mAnimSet.add(anim1);
            mAnimSet.add(anim2);
            mAnimSet.add(anim3);
            mAnimSet.add(anim4);
            mAnimSet.add(anim5);
        }
        if (mAnimatorSet != null) {
            if (mAnimatorSet.isRunning()) {
                mAnimatorSet.end();
            }
        }
        mAnimatorSet = new AnimatorSet();
        //将需要移动的图片置前
        //考虑一张图片的情况
//        if (mChildViews.length > 1) {
//            mChildViews[mFirstFocusItem + 1].bringToFront();
//        }

        mAnimatorSet.playTogether(mAnimSet);
        mAnimatorSet.setInterpolator(new LinearInterpolator());
        mAnimatorSet.setDuration(mAnimatorDuration);
        mAnimatorSet.start();
    }

    public void goLeftItem(ImageView[] mChildViews, int mCurrentItem, int mCurrentRect, int mAnimatorDuration) {
        Set<Animator> mAnimSet = new HashSet<>();
        //向右移动，最后一张图片要特殊处理
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(mChildViews[mViewInfos.length - 1], "translationX", mViewInfos[0].left - mViewInfos[mViewInfos.length - 1].left, mViewInfos[0].left - mViewInfos[mViewInfos.length - 1].left);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mChildViews[mViewInfos.length - 1], "scaleX", mViewInfos[0].scale, mViewInfos[0].scale);
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(mChildViews[mViewInfos.length - 1], "scaleY", mViewInfos[0].scale, mViewInfos[0].scale);
        ObjectAnimator animator4 = ObjectAnimator.ofInt(new WrapperView(mChildViews[mViewInfos.length - 1]), "width", mViewInfos[0].right - mViewInfos[0].left, mViewInfos[0].right - mViewInfos[0].left);
        ObjectAnimator animator5 = ObjectAnimator.ofInt(new WrapperView(mChildViews[mViewInfos.length - 1]), "height", mViewInfos[0].bottom - mViewInfos[0].top, mViewInfos[0].bottom - mViewInfos[0].top);
        mAnimSet.add(animator1);
        mAnimSet.add(animator2);
        mAnimSet.add(animator3);
        mAnimSet.add(animator4);
        mAnimSet.add(animator5);

        if (mImgUrls != null) {
            final int num = (mCurrentItem + mImgUrls.length * 1000 - mFirstFocusItem - mCurrentRect) % mImgUrls.length;
            final ImageView imageView = mChildViews[mChildViews.length - 1];
            final String url = mImgUrls[num];
            if (onChildViewDataChanged != null) {
                onChildViewDataChanged.setChildViewData(imageView, num, url);
            } else {
                Glide.with(context)
                        .load(url)
                        .override(imageView.getWidth(), imageView.getHeight())
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(imageView);
            }

        }

        for (int i = 0; i < mChildViews.length - 1; i++) {
            ObjectAnimator anim1 = ObjectAnimator.ofFloat(mChildViews[i], "translationX", mViewInfos[i].left - mViewInfos[i + 1].left, 0);
            ObjectAnimator anim2 = ObjectAnimator.ofFloat(mChildViews[i], "scaleX", mViewInfos[i].scale, mViewInfos[i + 1].scale);
            ObjectAnimator anim3 = ObjectAnimator.ofFloat(mChildViews[i], "scaleY", mViewInfos[i].scale, mViewInfos[i + 1].scale);
            ObjectAnimator anim4 = ObjectAnimator.ofInt(new WrapperView(mChildViews[i]), "width", mViewInfos[i].right - mViewInfos[i].left, mViewInfos[i + 1].right - mViewInfos[i + 1].left);
            ObjectAnimator anim5 = ObjectAnimator.ofInt(new WrapperView(mChildViews[i]), "height", mViewInfos[i].bottom - mViewInfos[i].top, mViewInfos[i + 1].bottom - mViewInfos[i + 1].top);

            mAnimSet.add(anim1);
            mAnimSet.add(anim2);
            mAnimSet.add(anim3);
            mAnimSet.add(anim4);
            mAnimSet.add(anim5);
        }

        if (mAnimatorSet != null) {
            if (mAnimatorSet.isRunning()) {
                mAnimatorSet.end();
            }
        }

        mAnimatorSet = new AnimatorSet();
        //将需要移动的图片置前
        //考虑一张图片的情况
//        if (mChildViews.length > 1) {
//            mChildViews[mFirstFocusItem - 1].bringToFront();
//        }

        mAnimatorSet.playTogether(mAnimSet);
        mAnimatorSet.setInterpolator(new LinearInterpolator());
        mAnimatorSet.setDuration(mAnimatorDuration);
        mAnimatorSet.start();
    }

    private void startAnimta(boolean isLeft) {

        if (mChildViews != null) {
            if (isLeft) {
                ImageView tempView = mChildViews[mChildViews.length - 1];
                System.arraycopy(mChildViews, 0, mChildViews, 1, mChildViews.length - 1);
                mChildViews[0] = tempView;
            } else {
                ImageView tempView = mChildViews[0];
                System.arraycopy(mChildViews, 1, mChildViews, 0, mChildViews.length - 1);
                mChildViews[mChildViews.length - 1] = tempView;
            }

            for (int i = 0; i < mChildViews.length; i++) {
                mChildViews[i].setTranslationX(0);
                mChildViews[i].setScaleY(1);
                mChildViews[i].setScaleX(1);
                mChildViews[i].layout(mViewInfos[i].left, mViewInfos[i].top, mViewInfos[i].right, mViewInfos[i].bottom);
            }
        }
        ItemViewsbringToFront();
    }

    public abstract void ItemViewsbringToFront();


    @Override
    public BaseLoopPlayView setImageViewUrls(@NonNull List<String> urlList) {
        String[] urls = (String[]) urlList.toArray();
        setImageViewUrls(urls);
        return this;
    }

    @Override
    public BaseLoopPlayView setImageViewUrls(@NonNull String[] urls) {
        mImgUrls = new String[urls.length];
        System.arraycopy(urls, 0, mImgUrls, 0, urls.length);
        return this;
    }

    protected OnDataChanged onChildViewDataChanged;

    @Override
    public BaseLoopPlayView notifyData(OnDataChanged onDataChanged) {
        this.onChildViewDataChanged = onDataChanged;
        return this;
    }


    protected OnViewChangedListener onChildViewChanged;

    @Override
    public BaseLoopPlayView setOnChildViewChanged(OnViewChangedListener onImageChanger) {
        this.onChildViewChanged = onImageChanger;
        return this;
    }

    @Override
    protected void onDetachedFromWindow() {
        mHandle.removeCallbacksAndMessages(null);
        if (mAnimatorSet != null) {
            if (mAnimatorSet.isRunning()) {
                mAnimatorSet.end();
            }
        }
        super.onDetachedFromWindow();
    }

    @Override
    public BaseLoopPlayView setChildViewHeight(int mContentViewHeight) {
        this.mChildViewHeight = mContentViewHeight;
        return this;
    }

    @Override
    public BaseLoopPlayView setChildViewWidth(int mContentViewWidth) {
        this.mChildViewWidth = mContentViewWidth;
        return this;
    }

    @Override
    public BaseLoopPlayView setChildViewPadding(int mContentViewPadding) {
        this.mChildViewPadding = mContentViewPadding;
        return this;
    }

    public BaseLoopPlayView setMaxDuration(int mMaxDuration) {
        this.mMaxDuration = mMaxDuration;
        return this;
    }

    @Override
    public BaseLoopPlayView setShowImageNum(int num) {
        mShowViewNum = num;
        return this;
    }


    @Override
    public BaseLoopPlayView setFirstFocusItem(int point) {
        mFirstFocusItem = point;
        return this;
    }

    public int getContentWidth() {
        return mChildViewWidth;
    }

    public int getContentHeight() {
        return mChildViewHeight;
    }

    @Override
    public int getCurrentItem() {
        return mCurrentItem.get();
    }

    @Override
    public BaseLoopPlayView setTopTextHeight(int height) {
        mTopTextHeight = height;
        return this;
    }

    @Override
    public Rect getFocusRect() {
        return mFocusRect;
    }

    public void setFocusRect(Rect mFocusRect) {
        this.mFocusRect = mFocusRect;
    }

    private class WrapperView {
        private View mTarget;


        public WrapperView(View target) {
            mTarget = target;
        }

        public int getWidth() {
            return mTarget.getLayoutParams().width;
        }

        public void setWidth(int width) {
            mTarget.getLayoutParams().width = width;
            mTarget.layout(mTarget.getLeft(), mTarget.getTop(), mTarget.getLeft() + width, mTarget.getBottom());

        }

        public int getHeight() {
            return mTarget.getLayoutParams().height;
        }

        public void setHeight(int height) {

            int top = mTarget.getTop();
            int left = mTarget.getLeft();
            int bottom = mTarget.getBottom();
            int right = mTarget.getRight();

            top = BaseLoopPlayView.this.mTopTextHeight+BaseLoopPlayView.this.mChildViewHeight/2-height/2;
            bottom = top+height;

            mTarget.getLayoutParams().height = height;
            mTarget.layout(left,top,right,bottom);
        }
    }

    @Override
    public void notifyFocusChanged(boolean gainFocus) {
        if (gainFocus) {
            notifyChildViewChanged(null, mChildViews[mCurrentRect.get()], mCurrentItem.get());
        } else {
            notifyChildViewChanged(mChildViews[mCurrentRect.get()], null, mCurrentItem.get());
        }
    }
}
