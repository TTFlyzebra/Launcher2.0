package com.ppfuns.ppfunstv.view.MarqueeView;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;

import com.ppfuns.ppfunstv.utils.FlyLog;


/**
 *
 * Created by flyzebra on 17-3-28.
 */
public abstract class BaseMarqueeView extends RelativeLayout implements IMarquee {
    protected int mTextSize = 40;
    protected int mTextColor = 0xffffffff;
    protected int mDirection = MarqueeTextView.RIGHT_MOVE_LEFT;
    protected String mText = "";
    protected MarqueeTextView mMqrqueeTextView;
    protected Context mContext;
    protected float mScale = 1.0f;
    protected int mLeft;
    protected int mTop;
    protected int mHeight;
    protected int mWidth;
    protected long mDuration = 30;
    protected ObjectAnimator mAnim = null;


    public BaseMarqueeView(Context context) {
        this(context,null);
    }

    public BaseMarqueeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseMarqueeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        initContext(context);
    }

    protected void initContext(Context context) {
        mContext = context;
        DisplayMetrics dm = new DisplayMetrics();
        if(context instanceof Activity) {
            ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(dm);
            mScale = dm.widthPixels/1920.f;
        }else{
            mScale = 1280.f/1920.f;
        }
    }

    @Override
    public BaseMarqueeView setPoint(int x, int y, int width, int height){
        mWidth = (int) (width*mScale);
        mHeight = (int) (height*mScale);
        mLeft = (int) (x*mScale);
        mTop = (int) (y*mScale);
        return this;
    }

    @Override
    public BaseMarqueeView setText(String text) {
        mText = text;
        return this;
    }

    @Override
    public BaseMarqueeView setTextColor(String textColor) {
        try {
            mTextColor = Color.parseColor(textColor);
        }catch (Exception e){
            FlyLog.d("parseColor error set defualt color = %d",mTextColor);
            mTextColor = 0xffffffff;
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public BaseMarqueeView setTextColor(int textColor) {
        mTextColor = textColor;
        return this;
    }

    @Override
    public BaseMarqueeView setTextSize(int textSize) {
        if(textSize>0){
            mTextSize = (int) (textSize*mScale);
            FlyLog.d("mTextSize=%d,mScale=%f",mTextSize,mScale);
        }
        return this;
    }

    @Override
    public BaseMarqueeView setDirection(int direction) {
        mDirection = direction;
        return this;
    }

    @Override
    public BaseMarqueeView setDuration(long duration){
        if(duration>=1){
            mDuration = duration;
        }
        return this;
    }

    @Override
    public void init(){
        cancelAnimator();
        removeAllViews();
        initView();
        addMaqueeTextView();
    }

    private void addMaqueeTextView() {
        mMqrqueeTextView = new MarqueeTextView(mContext);
        mMqrqueeTextView.setText(mText)
                .setTextColor(mTextColor)
                .setTextSize(mTextSize)
                .setDirection(mDirection)
                .init();
        LayoutParams lp = new LayoutParams(mMqrqueeTextView.getmWidht(), mMqrqueeTextView.getmHeight());
        switch (mDirection){
            case MarqueeTextView.RIGHT_MOVE_LEFT:
                lp.leftMargin = mWidth;
                lp.topMargin = 0;
                mAnim = ObjectAnimator.ofFloat(mMqrqueeTextView,"translationX",0,-(mWidth + mMqrqueeTextView.getmWidht()));
                break;
            case MarqueeTextView.LEFT_MOVE_RIGHT:
                lp.leftMargin = 0- mMqrqueeTextView.getmWidht();
                lp.topMargin = 0;
                mAnim = ObjectAnimator.ofFloat(mMqrqueeTextView,"translationX",0,mWidth + mMqrqueeTextView.getmWidht());
                break;
            case MarqueeTextView.DOWN_MOVE_UP:
                lp.leftMargin =0;
                lp.topMargin = mHeight;
                mAnim = ObjectAnimator.ofFloat(mMqrqueeTextView,"translationY",0,-(mHeight+ mMqrqueeTextView.getmHeight()));
                break;
            case MarqueeTextView.UP_MOVE_DWON:
                lp.leftMargin = 0;
                lp.topMargin = 0 - mMqrqueeTextView.getmHeight();
                mAnim = ObjectAnimator.ofFloat(mMqrqueeTextView,"translationY",0,mHeight+ mMqrqueeTextView.getmHeight());
                break;
        }
        mMqrqueeTextView.setLayoutParams(lp);
        if(mAnim !=null){
            /**
             * 计算动画滚动时间
             */
            long animDuration = 30000;
            switch (mDirection){
                case MarqueeTextView.RIGHT_MOVE_LEFT:
                case MarqueeTextView.LEFT_MOVE_RIGHT:
                    animDuration = (mMqrqueeTextView.getTextLengthSize()+mWidth)/mDuration*1000;
                    break;
                case MarqueeTextView.DOWN_MOVE_UP:
                case MarqueeTextView.UP_MOVE_DWON:
                    animDuration = (mMqrqueeTextView.getTextLengthSize()+mHeight)/mDuration*1000;
                    break;
            }
            mAnim.setDuration(animDuration);
            mAnim.setInterpolator(new LinearInterpolator());
            mAnim.setRepeatCount(ValueAnimator.INFINITE);
            mAnim.setRepeatMode(ValueAnimator.INFINITE);
        }
        addView(mMqrqueeTextView);
    }

    @Override
    public void play() {
        setVisibility(VISIBLE);
        startAnimator();
    }

    @Override
    public void stop() {
        cancelAnimator();
        setVisibility(INVISIBLE);
    }

    @Override
    public abstract void release();

    @Override
    public abstract void bind(ViewGroup viewGroup);

    public abstract void initView();


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        FlyLog.d("onAttachedToWindow");
    }

    @Override
    protected void onDetachedFromWindow() {
        cancelAnimator();
        super.onDetachedFromWindow();
        FlyLog.d("onDetachedFromWindow");
    }


    protected void startAnimator(){
        if(mAnim !=null){
            mAnim.start();
        }
    }

    protected void cancelAnimator(){
        if(mAnim !=null){
            mAnim.cancel();
        }
    }
}
