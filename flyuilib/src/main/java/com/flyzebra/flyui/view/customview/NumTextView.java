package com.flyzebra.flyui.view.customview;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.flyzebra.flyui.R;


public class NumTextView extends View {
    private Bitmap numBitmap;
    private String text = "98.50";
    private int bitmapWidth;
    private int bitmapHeigth;
    private int textWidth;
    private int textSumWidth;
    private Rect dstRect = new Rect();
    private Rect srcRect = new Rect();
    private Paint paint = new Paint();

    public NumTextView(Context context) {
        this(context, null);
    }

    public NumTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context);
    }

    private void init(Context context) {
        numBitmap = getBitmapFromResources(context, R.drawable.numtext);
        bitmapWidth = numBitmap.getWidth();
        bitmapHeigth = numBitmap.getHeight();
        textWidth = bitmapWidth / 11;
        dstRect.top = 0;
        dstRect.bottom = bitmapHeigth;
        srcRect.top = 0;
        srcRect.bottom = bitmapHeigth;
    }

    //从资源文件中获取Bitmap
    public static Bitmap getBitmapFromResources(Context context, int resId) {
        Resources res = context.getResources();
        return BitmapFactory.decodeResource(res, resId);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int reduce = 0;
        int startx = (getMeasuredWidth()-textSumWidth)/2;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            dstRect.left = i * textWidth - reduce +startx;
            dstRect.right = dstRect.left + textWidth;
            switch (c) {
                case '0':
                    srcRect.left = 9 * textWidth;
                    break;
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    srcRect.left = (c - '1') * textWidth;
                    break;
                case '.':
                default:
                    srcRect.left = 10 * textWidth;
                    reduce = textWidth/2;
                    break;
            }
            srcRect.right = textWidth + srcRect.left;
            canvas.drawBitmap(numBitmap, srcRect, dstRect, paint);
        }

    }

    public void setText(String text) {
        this.text = text;
        int count = 0;
        for(int i=0;i<text.length();i++){
            char c = text.charAt(i);
            if(c>='0'&&c<='9'){
                count+=2;
            }else{
                count++;
            }
        }
        textSumWidth = count*textWidth/2;
        postInvalidate();
    }
}
