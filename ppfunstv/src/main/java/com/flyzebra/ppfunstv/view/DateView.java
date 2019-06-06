package com.flyzebra.ppfunstv.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;

import com.flyzebra.ppfunstv.R;
import com.flyzebra.ppfunstv.utils.FlyLog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 湖南项目主界面右上角显示日期控件
 * Created by flyzebra on 17-4-19.
 */
public class DateView extends View{

    private Paint mTextPaint;
    private Paint mLinePaint;
    private int mTextSize = 24;
    private int mHeight;
    private int mWidth;
    private int mTextColor = 0xffffffff;
    private String[] weeks = new String[7];


    private Paint.FontMetrics mFontMetrics;
    private Rect mTextRect = new Rect();

    private String time = "00:00";
    private String date = "2017.01.01";
    private String week = "星期日";

    private final String TIME_FORMAT = "HH:mm";
    private final String TIME_DEFAULT = "00:00";
    private final String DATE_FORMAT = "yyyy.MM.dd";
    private final String DATE_DEFAULT = "0000.00.00";

    private Runnable task = new Runnable(){
        @Override
        public void run() {
            upView();
        }};

    private Handler mHander = new Handler(Looper.myLooper());
    private long mDelayMillis = 1000;

    public DateView(Context context) {
        this(context,null);
    }

    public DateView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public DateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        init(context);
    }

    public void setTextColor(int textColor){
        this.mTextColor = textColor;
    }

    private void init(Context context) {
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mTextColor);
//        mTextPaint.setFakeBoldText(true);
        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
//        mLinePaint.setStrokeWidth(2f);
        mLinePaint.setColor(mTextColor);
        weeks = new String[]{ context.getString(R.string.tv_str_sunday),
                context.getString(R.string.tv_str_monday),
                context.getString(R.string.tv_str_tuesday),
                context.getString(R.string.tv_str_wednesday),
                context.getString(R.string.tv_str_thursday),
                context.getString(R.string.tv_str_friday),
                context.getString(R.string.tv_str_saturday)};
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        mTextSize = (int) (mHeight*0.8f);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.getTextBounds(TIME_DEFAULT,0,TIME_DEFAULT.length(),mTextRect);
        mWidth = mTextRect.right;
        mWidth = mWidth + mHeight/2+4;//中间竖线所占用宽度

        mTextPaint.setTextSize(mTextSize/2);
        mTextPaint.getTextBounds(DATE_DEFAULT,0, DATE_DEFAULT.length(),mTextRect);
        mWidth = mWidth + mTextRect.right;

        setMeasuredDimension(MeasureSpec.makeMeasureSpec(mWidth,MeasureSpec.EXACTLY),MeasureSpec.makeMeasureSpec(mHeight,MeasureSpec.EXACTLY));
    }

    @Override
    protected void onAttachedToWindow() {
        FlyLog.d();
        upView();
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        FlyLog.d();
        mHander.removeCallbacks(task);
        super.onDetachedFromWindow();
    }

    private void upView() {
        String newTime = getCurrentDate(TIME_FORMAT);
        String newDate = getCurrentDate(DATE_FORMAT);
        String newWeek = getCurrentWeek();

        if(!(newTime.equals(time)&&newDate.equals(date)&&newWeek.equals(week))){
            time = newTime;
            date = newDate;
            week = newWeek;
            invalidate();
        }
        mHander.removeCallbacksAndMessages(null);
        mHander.postDelayed(task,mDelayMillis);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        FlyLog.d("refresh current time!");
        int X = 0;
        int Y = 0;
        //绘制时间
        mTextPaint.setTextSize(mTextSize);
        mFontMetrics = mTextPaint.getFontMetrics();
        mTextPaint.getTextBounds(TIME_DEFAULT,0,TIME_DEFAULT.length(),mTextRect);
        Y = (int) ((0 - mFontMetrics.top)+(mHeight-(mFontMetrics.bottom-mFontMetrics.top))/2);
        canvas.drawText(time,X,Y,mTextPaint);

        //绘制竖线
        X = mTextRect.right+mHeight/4+1;
//        canvas.drawLine(X,0+mHeight*0.15f,X,mHeight*0.85f,mLinePaint);

        //绘制年月日
        X = X+2+mHeight/4;
        mTextPaint.setTextSize(mTextSize/2);
        mFontMetrics = mTextPaint.getFontMetrics();
        mTextPaint.getTextBounds(DATE_DEFAULT,0, DATE_DEFAULT.length(),mTextRect);
        Y = (int) ((0 - mFontMetrics.top)+(mHeight/2-(mFontMetrics.bottom-mFontMetrics.top))/2+mHeight*0.05f);
        canvas.drawText(date,X,Y,mTextPaint);

        //绘制星期
        int lastRight = mTextRect.right;
        mTextPaint.setTextSize(mTextSize/2);
        mFontMetrics = mTextPaint.getFontMetrics();
        mTextPaint.getTextBounds(week,0, week.length(),mTextRect);
//        X = X+(lastRight -mTextRect.right)/2;
        Y = (int) ((0 - mFontMetrics.top)+(mHeight/2-(mFontMetrics.bottom-mFontMetrics.top))/2+mHeight/2-mHeight*0.05f);
        canvas.drawText(week,X,Y,mTextPaint);
    }

    private static String getCurrentDate(String dateFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat,	Locale.getDefault());
        Date date = new Date(System.currentTimeMillis());
        return sdf.format(date);
    }

    private String getCurrentWeek() {
        Date date = new Date(System.currentTimeMillis());
        final Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTime(date);
        return weeks[mCalendar.get(Calendar.DAY_OF_WEEK)-1];
    }
}
