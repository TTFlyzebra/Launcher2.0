package com.ppfuns.ppfunstv.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.ppfuns.ppfunstv.data.CellEntity;
import com.ppfuns.ppfunstv.utils.BitmapUtils;
import com.ppfuns.ppfunstv.utils.FlyLog;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by 李宗源 on 2016/8/12.
 * E-mail:lizy@ppfuns.com
 * 倒影显示控件
 */
public class ReflectImageView extends ImageView {
    private CellEntity mCell;
    private int mRefHeight = 80;
    private Handler mHander = new Handler(Looper.getMainLooper());
    private final static ExecutorService executors = Executors.newCachedThreadPool();
    private Bitmap mBitmap;

    private boolean isAttached = true;


    public ReflectImageView(Context context) {
        this(context, null);
    }

    public ReflectImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ReflectImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setCellData(CellEntity cellEntity) {
        mCell = cellEntity;
    }

    public CellEntity getCellData() {
        return mCell;
    }

    public int getRefHeight() {
        return mRefHeight;
    }


    public void setRefHeight(int refHeight) {
        mRefHeight = refHeight;
    }

    public void showRefImage(Bitmap bm) {
        if (bm != null) {
            try {
                showRefImage(bm, mRefHeight);
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
     * @param bitmap
     */
    public void showRefImage(final Bitmap bitmap, final int refHight) throws Exception {
        mRefHeight = refHight;
        executors.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //以下代码使镜像能够按显示尽寸调整比例缩放
                    if (mCell != null) {
                        Matrix matrix = new Matrix();
                        matrix.postScale((float) mCell.getWidth() / (float) bitmap.getWidth(), (float) mCell.getHeight() / (float) bitmap.getHeight());
                        mBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
                    }
                    if (mBitmap != null) {
                        mBitmap = BitmapUtils.createReflectedImage(mBitmap, mRefHeight);
                    }
                    if (mBitmap != null && isAttached) {
                        mHander.post(new Runnable() {
                            @Override
                            public void run() {
                                setImageBitmap(mBitmap);
                            }
                        });
                    }
                } catch (OutOfMemoryError | Exception error) {
//                    System.gc();
                    error.printStackTrace();
                    FlyLog.d(error.toString());
                }
            }
        });
    }

}
