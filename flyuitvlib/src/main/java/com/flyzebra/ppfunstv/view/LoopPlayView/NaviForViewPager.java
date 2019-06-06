package com.flyzebra.ppfunstv.view.LoopPlayView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.flyzebra.ppfunstv.R;


/**
 * ViewPager轮播用的导航条用来指示ViewPager当前显示的页面ViewPager
 * Created by FlyZebra on 2016/3/1.
 */
public class NaviForViewPager extends View {
    public static final int LEFT = 1;
    public static final int MID = 2;
    public static final int RIGHT = 3;

    private int gravity = MID;

    private Paint select_paint;
    private Paint un_select_paint;
    private int width;
    private int height;
    //总页数
    private int sumItem = 10;
    //当前页
    private int currentItem = 0;

    private int circleWidth = 10;
    private int circleColor1 = 0x70000000;
    private int circleColor2 = 0xDFFF1100;

    public NaviForViewPager(Context context) {
        this(context, null);
    }

    public NaviForViewPager(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NaviForViewPager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TV_NaviForViewPager, defStyleAttr, 0);
        circleWidth = typedArray.getDimensionPixelSize(R.styleable.TV_NaviForViewPager_TvCircelWidth, 10);
        circleColor1 = typedArray.getColor(R.styleable.TV_NaviForViewPager_TvCircelColor1, 0x7F000000);
        circleColor2 = typedArray.getColor(R.styleable.TV_NaviForViewPager_TvCircelColor2, 0xCF00FF00);
        typedArray.recycle();
    }

    public void setGravity(int gravity) {
        this.gravity = gravity;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (width > 0 && height > 0) {
            this.width = width;
            this.height = height;
        }
//        Log.i(TAG, "ViewPagerCountImage->onMeasure->width=" + width);
    }

    public void setSumItem(int sumItem) {
        if (sumItem > 1) {
            setVisibility(VISIBLE);
        } else {
            setVisibility(GONE);
        }
        this.sumItem = sumItem;
        postInvalidate();
    }

    public void setCurrentItem(int currentItem) {
        this.currentItem = currentItem;
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initPaint();
        if (sumItem > 0) {
            float x = 0;
            switch (gravity){
                case LEFT:
                    x = circleWidth;
                    break;
                case MID:
                    x = width / 2 - (sumItem * circleWidth * 2 - circleWidth) / 2 + circleWidth / 2;
                    break;
                case RIGHT:
                    x = width - sumItem * circleWidth * 2+circleWidth;
                    break;
                default:
                    x = width / 2 - (sumItem * circleWidth * 2 - circleWidth) / 2 + circleWidth / 2;
                    break;
            }
            for (int i = 0; i < sumItem; i++) {
                if (i == currentItem) {
                    canvas.drawCircle(x + i * circleWidth * 2, circleWidth, circleWidth / 2, select_paint);
                } else {
                    canvas.drawCircle(x + i * circleWidth * 2, circleWidth, circleWidth / 2, un_select_paint);
                }
            }
        }

    }

    private void initPaint() {
        if (un_select_paint == null) {
            un_select_paint = new Paint();// 实例化Paint
            un_select_paint.setAntiAlias(true);
            un_select_paint.setColor(circleColor1);// 设置颜色
            un_select_paint.setStyle(Paint.Style.FILL);// 设置样式
        }
        if (select_paint == null) {
            select_paint = new Paint();
            select_paint.setAntiAlias(true);
            select_paint.setColor(circleColor2);
            select_paint.setStyle(Paint.Style.FILL);
        }
    }
}
