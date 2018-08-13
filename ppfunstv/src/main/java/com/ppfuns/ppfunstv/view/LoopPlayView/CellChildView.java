package com.ppfuns.ppfunstv.view.LoopPlayView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 *
 * Created by flyzebra on 17-8-17.
 */

public class CellChildView extends ImageView {
    private String mText;
    private Paint mTextPaint;
    private int topPadding = 0;
    private int bottomPadding = 0;
    private int mTextColor;


    public CellChildView(Context context) {
        this(context, null);
    }

    public CellChildView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CellChildView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTextPaint = new TextPaint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(24);
        mTextPaint.setColor(0x3fffffff);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!TextUtils.isEmpty(mText)) {
            Drawable compass = getDrawable();
            if (compass != null) {
                compass.setBounds(0, 0, getWidth() - topPadding, getHeight() - bottomPadding);
                setImageDrawable(compass);
            }
        }
        super.onDraw(canvas);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (!TextUtils.isEmpty(mText)) {
            canvas.drawText(mText, 0, getMeasuredHeight() - 10, mTextPaint);
        }
    }

    public void setText(String text) {
        mText = text;
        postInvalidate();
    }

    public void setTopPadding(int topPadding) {
        this.topPadding = topPadding;
    }

    public void setBottomPadding(int bottomPadding) {
        this.bottomPadding = bottomPadding;
    }

    public void setTextColor(@ColorInt int color) {
        mTextColor = color;
        mTextPaint.setColor(mTextColor);
        postInvalidate();
    }
}
