package com.flyzebra.flyui.view.customview;


import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;


/**
 * Created by FlyZebra on 2016/3/25.
 */
public class CircleImageView extends ImageView {
    private int width;
    private int height;
    private int borderWidth = 2;
    private int borderColor = 0x1FFFFFFF;
    private Paint borderPaint;
    private Paint bitmapPaint;
    private Bitmap mBitmap;
    private BitmapShader mBitmapShader;
    private ObjectAnimator rotationAnimator;
    private boolean animatePlaying = false;

    public CircleImageView(Context context) {
        this(context, null);
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        borderPaint = new Paint();
        borderPaint.setAntiAlias(true);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(borderWidth);
        borderPaint.setColor(borderColor);
        bitmapPaint = new Paint();
        bitmapPaint.setAntiAlias(true);

        rotationAnimator = ObjectAnimator.ofFloat(this, "rotation", 0, 360);
        rotationAnimator.setDuration(10000);
        rotationAnimator.setInterpolator(new LinearInterpolator());
        rotationAnimator.setRepeatCount(ObjectAnimator.INFINITE);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        mBitmap = bm;
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        mBitmap = getBitmapFromDrawable(drawable);
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        mBitmap = getBitmapFromDrawable(getDrawable());
    }

    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        try {
            Bitmap bitmap;
            if (drawable instanceof ColorDrawable) {
                bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            }
            return bitmap;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mBitmap == null) {
            return;
        }
//        cropBitmap();
        mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mBitmapShader.setLocalMatrix(getMatrix(width, height, mBitmap.getWidth(), mBitmap.getHeight()));
        bitmapPaint.setShader(mBitmapShader);
        int circleRid = Math.min(width, height) / 2;
        canvas.drawCircle(width / 2, height / 2, circleRid - borderWidth, bitmapPaint);
        canvas.drawCircle(width / 2, height / 2, circleRid - borderWidth / 2, borderPaint);
    }

    private void cropBitmap() {
        width = mBitmap.getWidth();
        height = mBitmap.getHeight();
        int x = width < height ? 0 : (width - height) / 2;
        int y = width < height ? (height - width) / 2 : 0;
        int size = Math.min(width, height);
        Bitmap oldBitmap = mBitmap;
        mBitmap = Bitmap.createBitmap(mBitmap, x, y, size, size);
        oldBitmap.recycle();
    }

    private Matrix getMatrix(int width, int height, int b_width, int b_height) {
        float scale;
        float dx = 0;
        float dy = 0;
        Matrix matrix = new Matrix();
        int scalefactor;
        if (width > height) {
            scalefactor = height;
            dx = (width - height) / 2;
        } else {
            scalefactor = width;
            dy = (height - width) / 2;
        }
        if (b_width > b_height) {
            scale = (float) scalefactor / (float) b_width;
            dx = dx + (scalefactor - b_height * scale) / 2;
        } else {
            scale = (float) scalefactor / (float) b_height;
            dy = dy + (scalefactor - b_width * scale) / 2;
        }
        matrix.setScale(scale, scale);
        matrix.postTranslate(dx, dy);
        return matrix;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (animatePlaying) {
            rotationAnimator.resume();
        } else {
            rotationAnimator.pause();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        rotationAnimator.end();
    }

    public void setAnimatePlaying(boolean animatePlaying) {
        this.animatePlaying = animatePlaying;
        if (animatePlaying) {
            rotationAnimator.resume();
        } else {
            rotationAnimator.pause();
        }
    }
}
