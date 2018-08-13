package com.ppfuns.ppfunstv.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 *
 * Created by flyzebra on 17-8-14.
 */

public class ClearTopRectView extends RelativeLayout{
    private int mHeight = 0;
    private Paint clearPaint;
    public ClearTopRectView(Context context) {
        this(context,null);
    }

    public ClearTopRectView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClearTopRectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setWillNotDraw(false);
        setClipToPadding(false);
        setClipChildren(false);
        clearPaint = new Paint();
        clearPaint.setColor(Color.TRANSPARENT);
        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    public void setClearHeight(int height) {
        this.mHeight = height;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if(mHeight>0){
            canvas.drawRect(0,0,getMeasuredWidth(),mHeight,clearPaint);
        }
    }
}
