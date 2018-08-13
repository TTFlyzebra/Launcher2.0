package com.ppfuns.ppfunstv.view.MarqueeView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 *
 * Created by flyzebra on 17-3-27.
 */
public class MarqueeTextView extends View {
    public static final int LEFT_MOVE_RIGHT = 0;
    public static final int RIGHT_MOVE_LEFT = 1;
    public static final int UP_MOVE_DWON = 2;
    public static final int DOWN_MOVE_UP = 3;
    private int mDirection = LEFT_MOVE_RIGHT;

    private int mWidht;
    private int mHeight;


    private int mTextSize = 24;
    private int mTextColor = Color.WHITE;
    private String mText = "";
    private String mDrawText;

    private Paint mPaint;
    private Paint.FontMetrics mFontMetrics;
    private Rect mTextRect = new Rect();
    private int mBaseX = 20;



    public MarqueeTextView(Context context) {
        this(context,null);
    }

    public MarqueeTextView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MarqueeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.makeMeasureSpec(mWidht,MeasureSpec.EXACTLY),MeasureSpec.makeMeasureSpec(mHeight,MeasureSpec.EXACTLY));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        try {
            switch (mDirection){
                case RIGHT_MOVE_LEFT:
                case LEFT_MOVE_RIGHT:
                    canvas.drawText(mDrawText, 0, 0-mFontMetrics.top, mPaint);
                    break;
                case DOWN_MOVE_UP:
                case UP_MOVE_DWON:
                    StringBuffer sb = new StringBuffer(mDrawText);
                    for(int i=0;i<mDrawText.length();i++){
                        String text = sb.substring(i,i+1);
                        canvas.drawText(text, 0, (0-mFontMetrics.top)+(mTextRect.bottom-mTextRect.top)*i, mPaint);
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public MarqueeTextView setText(String text){
        mText = text;
        return this;
    }

    /**
     * 设置字体大小
     * @param size
     */
    public MarqueeTextView setTextSize(int size){
        mTextSize = size;
        return this;
    }

    /**
     * 设置字体颜色
     * @param color
     */
    public MarqueeTextView setTextColor(int color){
        mTextColor = color;
        return this;
    }

    /**
     * 设置文字排列顺序，根据文字排列顺序计算控件大小
     * @param order
     */
    public MarqueeTextView setDirection(int order){
        mDirection = order;
        return this;
    }

    public void init(){
        initDrawPaint();
    }


    /**
     * 根据设定值初始化画笔参数
     */
    private void initDrawPaint(){
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(mTextSize);
        mPaint.setColor(mTextColor);
//        mPaint.setFakeBoldText(true);
        mFontMetrics = mPaint.getFontMetrics();
        mPaint.getTextBounds(mText,0,mText.length(),mTextRect);
        mBaseX = (mTextRect.right-mTextRect.left)/mText.length();
        initDrawText();
    }


    public int getTextLengthSize(){
        int lengthSize = 0;
        switch (mDirection){
            case RIGHT_MOVE_LEFT:
            case LEFT_MOVE_RIGHT:
                lengthSize = mTextRect.right-mTextRect.left+ mBaseX;
                break;
            case DOWN_MOVE_UP:
            case UP_MOVE_DWON:
                lengthSize = (mTextRect.bottom-mTextRect.top)*mText.length()+((int)(mFontMetrics.bottom-mFontMetrics.top)-(mTextRect.bottom-mTextRect.top));
                break;
        }
        return lengthSize;
    }


    private void initDrawText(){
        switch (mDirection){
            case LEFT_MOVE_RIGHT:
            case UP_MOVE_DWON:
                mDrawText = new StringBuffer(mText).reverse().toString();
                break;
            case RIGHT_MOVE_LEFT:
            case DOWN_MOVE_UP:
                mDrawText = mText;
                break;
            default:
                mDrawText = mText;
                break;
        }

        switch (mDirection){
            case RIGHT_MOVE_LEFT:
            case LEFT_MOVE_RIGHT:
                mWidht = mTextRect.right-mTextRect.left+ mBaseX;
                mHeight = (int) (mFontMetrics.bottom-mFontMetrics.top);
                break;
            case DOWN_MOVE_UP:
            case UP_MOVE_DWON:
                mWidht = (mTextRect.right-mTextRect.left)/mText.length()+ mBaseX;
                mHeight = (mTextRect.bottom-mTextRect.top)*mText.length()+((int)(mFontMetrics.bottom-mFontMetrics.top)-(mTextRect.bottom-mTextRect.top));
                break;
        }
    }


    public int getmHeight() {
        return mHeight;
    }

    public int getmWidht() {
        return mWidht;
    }
}
