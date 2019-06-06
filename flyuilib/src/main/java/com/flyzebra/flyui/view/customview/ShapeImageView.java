package com.flyzebra.flyui.view.customview;


import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;


/**
 * Created by FlyZebra on 2016/3/25.
 */
public class ShapeImageView extends FlyImageView {
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
    private int shapeType = 0;
    private Path mHexagonPath;

    public ShapeImageView(Context context) {
        this(context, null);
    }

    public ShapeImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShapeImageView(Context context, AttributeSet attrs, int defStyleAttr) {
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
        if (shapeType > 0) {
            mBitmap = bm;
        }
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        if (shapeType > 0) {
            mBitmap = getBitmapFromDrawable(drawable);
        }
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        if (shapeType > 0) {
            mBitmap = getBitmapFromDrawable(getDrawable());
        }
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
        if (mBitmap != null && shapeType > 0) {
            switch (shapeType) {
                case 1:
                    mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                    mBitmapShader.setLocalMatrix(getMatrix(width, height, mBitmap.getWidth(), mBitmap.getHeight()));
                    bitmapPaint.setShader(mBitmapShader);
                    int circleRid = Math.min(width, height) / 2;
                    canvas.drawCircle(width / 2, height / 2, circleRid - borderWidth, bitmapPaint);
                    canvas.drawCircle(width / 2, height / 2, circleRid - borderWidth / 2, borderPaint);
                    break;
                case 2:
                    mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                    mBitmapShader.setLocalMatrix(getMatrix(width, height, mBitmap.getWidth(), mBitmap.getHeight()));
                    bitmapPaint.setShader(mBitmapShader);
                    initHexagonPath();
                    canvas.drawPath(mHexagonPath, bitmapPaint);
                    canvas.drawPath(mHexagonPath, borderPaint);
                    break;
                default:
                    super.onDraw(canvas);
                    break;
            }
        } else {
            super.onDraw(canvas);
        }
    }

    private void initHexagonPath() {
        if (mHexagonPath == null) {
            mHexagonPath = new Path();
            int c = width / 2;
            int a = (int) (c * Math.sin(Math.toRadians(60)));
            int b = (int) (c * Math.cos(Math.toRadians(60)));
            int x = 0;
            int y = height / 2;
            mHexagonPath.moveTo(x, y);
            x = x + b;
            y = y - a;
            mHexagonPath.lineTo(x, y);
            x = x + c;
            mHexagonPath.lineTo(x, y);
            x = width;
            y = height / 2;
            mHexagonPath.lineTo(x, y);
            x = x - b;
            y = y + a;
            mHexagonPath.lineTo(x, y);
            x = x - c;
            mHexagonPath.lineTo(x, y);
            x = 0;
            y = height / 2;
            mHexagonPath.lineTo(x, y);
        }
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
        rotationAnimator.start();
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

    public void setShapeType(int shapeType) {
        this.shapeType = shapeType;
    }
}
