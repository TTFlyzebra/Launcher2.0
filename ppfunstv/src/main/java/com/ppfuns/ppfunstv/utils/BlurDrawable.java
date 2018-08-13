package com.ppfuns.ppfunstv.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;

/**
 * Created by lijinan on 2017/6/28.
 * Blur实时渲染
 */

public class BlurDrawable extends ColorDrawable {

    private int mDownsampleFactor;
    private View mBlurredBgView;
    private int mBlurredViewWidth, mBlurredViewHeight;
    private boolean mDownsampleFactorChanged;
    private Bitmap mBitmapToBlur, mBlurredBitmap;
    private Canvas mBlurringCanvas;
    private RenderScript mRenderScript;
    private ScriptIntrinsicBlur mBlurScript;
    private Allocation mBlurInput, mBlurOutput;

    private float offsetX;
    private float offsetY;

    private boolean enabled;

    private int mOverlayColor;

    public BlurDrawable(@NonNull View mBlurredBgView) {
        this.mBlurredBgView = mBlurredBgView;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            enabled = false;
        } else {
            enabled = true;
            initializeRenderScript(mBlurredBgView.getContext());
        }
        setOverlayColor(Color.argb(175, 0xff, 0xff, 0xff));
    }

    /**
     * used for dialog/fragment/popWindow
     *
     * @param activity the blurredView attached
     * @see #setDrawOffset
     */
    public BlurDrawable(Activity activity) {
        this(activity.getWindow().getDecorView());
    }

    /**
     * Set for window
     *
     * @param blurredWindow another window,void draw self(may throw stackoverflow)
     */
    public BlurDrawable(Window blurredWindow) {
        this(blurredWindow.getDecorView());
    }

    @TargetApi(17)
    public void setBlurRadius(@IntRange(from = 0, to = 25) int radius) {
        if (!enabled) {
            return;
        }
        mBlurScript.setRadius(radius);
    }

    @TargetApi(17)
    public void setDownsampleFactor(int factor) {
        if (factor <= 0) {
            throw new IllegalArgumentException("Downsample factor must be greater than 0.");
        }
        if (!enabled) {
            return;
        }
        if (mDownsampleFactor != factor) {
            mDownsampleFactor = factor;
            mDownsampleFactorChanged = true;
        }
    }

    /**
     * set both for blur and non-blur
     */
    public void setOverlayColor(@ColorInt int color) {
        mOverlayColor = color;
        setColor(color);
    }

    @TargetApi(17)
    private void initializeRenderScript(Context context) {
        mRenderScript = RenderScript.create(context);
        mBlurScript = ScriptIntrinsicBlur.create(mRenderScript, Element.U8_4(mRenderScript));
        //设置blur半径, iOS中默认为12px
        setBlurRadius(15);
        //图片缩放等级，缩放越大越节约性能，理论要在100px^2以内
        setDownsampleFactor(8);
    }

    /**
     * 相当于一个单例的初始化
     */
    protected boolean prepare() {
        //assume a 1080 x 1920 RecyclerView
        final int width = mBlurredBgView.getWidth();
        final int height = mBlurredBgView.getHeight();
        if (mBlurringCanvas == null
                || mDownsampleFactorChanged
                || mBlurredViewWidth != width
                || mBlurredViewHeight != height) {
            mDownsampleFactorChanged = false;

            mBlurredViewWidth = width;
            mBlurredViewHeight = height;

            int scaledWidth = width / mDownsampleFactor;
            int scaledHeight = height / mDownsampleFactor;

            // The following manipulation is to avoid some RenderScript artifacts at the edge.
            // 136 x 244
            scaledWidth = scaledWidth - scaledWidth % 4 + 4;
            scaledHeight = scaledHeight - scaledHeight % 4 + 4;

            if (mBlurredBitmap == null
                    || mBlurredBitmap.getWidth() != scaledWidth
                    || mBlurredBitmap.getHeight() != scaledHeight) {
                mBitmapToBlur = Bitmap.createBitmap(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888);
                if (mBitmapToBlur == null) {
                    return false;
                }

                mBlurredBitmap = Bitmap.createBitmap(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888);
                if (mBlurredBitmap == null) {
                    return false;
                }
            }
            //创建了一个136 x 244的画板
            //当画板调用draw是，将画到mBitmapToBlur上
            mBlurringCanvas = new Canvas(mBitmapToBlur);

            mBlurringCanvas.scale(1f / mDownsampleFactor, 1f / mDownsampleFactor);
            mBlurInput = Allocation.createFromBitmap(mRenderScript, mBitmapToBlur,
                    Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
            mBlurOutput = Allocation.createTyped(mRenderScript, mBlurInput.getType());
        }
        return true;
    }

    /**
     * 渲染任务，可以在16ms完成，可以调用多核
     * 将mBitmapToBlur渲染为mBlurredBitmap输出
     */
    @TargetApi(17)
    protected void blur(Bitmap mBitmapToBlur, Bitmap mBlurredBitmap) {
        if (!enabled) {
            return;
        }
        //类似于c中的alloc，这里是栈内存，这样就把bitmap放入了c的栈中
        mBlurInput.copyFrom(mBitmapToBlur);
        //滤镜加入输入源
        mBlurScript.setInput(mBlurInput);
        //滤镜进行渲染并输出到output，类似于DSP
        mBlurScript.forEach(mBlurOutput);
        //将栈内存复制到bitmap
        mBlurOutput.copyTo(mBlurredBitmap);
    }

    /**
     * force enable blur, however it will only works on API 17 or higher
     * if your want to support more, use Support RenderScript Pack
     */
    public void setEnabled(boolean enabled) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            enabled = false;
        }
        this.enabled = enabled;
    }

    @TargetApi(17)
    public void onDestroy() {
        if (!enabled) {
            return;
        }
        if (mRenderScript != null) {
            mRenderScript.destroy();
        }
    }

    @Override
    public void draw(Canvas canvas) {
        if (!enabled) {
            //draw overlay color
            super.draw(canvas);
        } else {
            drawBlur(canvas);
        }
    }

    @TargetApi(17)
    private void drawBlur(Canvas canvas) {
        if (prepare()) {
            // If the background of the blurred view is a color drawable, we use it to clear
            // the blurring canvas, which ensures that edges of the child views are blurred
            // as well; otherwise we clear the blurring canvas with a transparent color.
            if (mBlurredBgView.getBackground() != null
                    && mBlurredBgView.getBackground() instanceof ColorDrawable) {
                mBitmapToBlur.eraseColor(((ColorDrawable) mBlurredBgView.getBackground()).getColor());
            } else {
                mBitmapToBlur.eraseColor(Color.TRANSPARENT);
            }
            //在1920x1080中，只画一个大小为 136 x 244 的RecyclerView，这个View绘制了两次
            //类似于开发者选项中的多显示输出
            //将bitmaptoblur进行赋值
            mBlurredBgView.draw(mBlurringCanvas);
            //进行模糊渲染，生成mBlurredBitmap
            blur(mBitmapToBlur, mBlurredBitmap);
            //
            canvas.save();
            //这里的是dx，正好与坐标是相反的
            canvas.translate(mBlurredBgView.getX() - offsetX, mBlurredBgView.getY() - offsetY);
            //实际输出的只有 136 x 244的像素，缩放后就和当前view一样大了
            canvas.scale(mDownsampleFactor, mDownsampleFactor);
            canvas.drawBitmap(mBlurredBitmap, 0, 0, null);
            canvas.restore();
        }
        canvas.drawColor(mOverlayColor);
    }

    /**
     * set the offset between top view and blurred view
     */
    @TargetApi(17)
    public void setDrawOffset(float x, float y) {
        this.offsetX = x;
        this.offsetY = y;
    }

}