package com.flyzebra.flyui.view.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;

import com.flyzebra.flyui.utils.BitmapUtil;
import com.flyzebra.flyui.utils.FlyLog;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 倒影显示控件
 */
public class MirrorView extends ShapeImageView {
    public static final int MIRRORHIGHT = 60;
    private int mRefHeight = MIRRORHIGHT;
    private Handler mHander = new Handler(Looper.getMainLooper());
    private final static ExecutorService executors = Executors.newCachedThreadPool();
    private Bitmap mBitmap;

    private boolean isAttached = true;


    public MirrorView(Context context) {
        this(context, null);
    }

    public MirrorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MirrorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void setRefHeight(int refHeight) {
        mRefHeight = refHeight;
    }

    public void showImage(Bitmap bm) {
        mBitmap = bm;
        if (mBitmap != null) {
            try {
                showRefImage();
            } catch (OutOfMemoryError | Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        isAttached = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        isAttached = false;
        mHander.removeCallbacksAndMessages(null);
        super.onDetachedFromWindow();
    }

    /**
     * 异步显示倒影
     *
     */
    public void showRefImage() throws Exception {
        executors.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (mBitmap != null) {
                        mBitmap = BitmapUtil.createReflectedImage(mBitmap, mRefHeight);
                    }else{
                        FlyLog.d("mBitmap null for createReflectedImage");
                    }
                    if (mBitmap != null && isAttached) {
                        mHander.post(new Runnable() {
                            @Override
                            public void run() {
//                                FlyLog.d("SetBitmap Bitmap="+mBitmap);
                                setImageBitmap(mBitmap);
                            }
                        });
                    }else{
                        FlyLog.d("createReflectedImage failed");
                    }
                } catch (OutOfMemoryError | Exception error) {
                    FlyLog.e(error.toString());
                }
            }
        });
    }

}
