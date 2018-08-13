package com.ppfuns.ppfunstv.view.TvView.CellView;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.ppfuns.ppfunstv.data.CellEntity;
import com.ppfuns.ppfunstv.data.SubScript;
import com.ppfuns.ppfunstv.module.BitmapCache;
import com.ppfuns.ppfunstv.utils.DisplayUtils;
import com.ppfuns.ppfunstv.utils.FlyLog;

import java.util.List;


/**
 * 有绘制角标功能的Image
 * Created by lizongyuan on 2016/11/16.
 * E-mail:lizy@ppfuns.com
 */

public class SubScriptView extends ImageView {

    private List<SubScript> subScripts;
    public CellEntity mCell;
    private float textSize;
    private float pading;
    private Paint paint;
    private Matrix matrix;
    private float screenScale = 1.0f;
    private BitmapCache mBitmapCache;

    private int resID;
    private Rect mTextRect = new Rect();
    private Paint.FontMetrics mFontMetrics;

    public SubScriptView(Context context) {
        this(context, null);
    }

    public SubScriptView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SubScriptView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        screenScale = DisplayUtils.getMetrices((Activity) context).widthPixels / 1920.0f;
        textSize = 36 * screenScale;
        pading = 4 * screenScale;
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setColor(Color.WHITE);
        paint.setTextSize(textSize);
        matrix = new Matrix();
    }

    public void setCell(CellEntity cell, BitmapCache bitmapCache) {
        mCell = cell;
        mBitmapCache = bitmapCache;
        if (mCell != null && mBitmapCache != null) {
            subScripts = mCell.getSubScripts();
            if(subScripts!=null){
                threadInitBitmapCache(subScripts);
            }
        }
    }

    /**
     * 在线程中将角标图标放入共用图片缓存
     * @param subScripts
     */
    private void threadInitBitmapCache(final List<SubScript> subScripts) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mBitmapCache == null) {
                    FlyLog.d("threadInitBitmapCache get subscript bitmap mBitmapCache = null");
                    return;
                }
                boolean flag = false;
                if (subScripts != null && subScripts.size() > 0) {
                    flag = true;
                    for (SubScript script : subScripts) {
                        if (!TextUtils.isEmpty(script.url)) {
                            Bitmap bitmap = mBitmapCache.getBitmap(script.url);
                            if(bitmap!= null){
                                FlyLog.d("threadInitBitmapCache get subscript bitmap true");
                            }
                        }
                    }
                }
                if (flag) {
                    FlyLog.d("threadInitBitmapCache postInvalidate");
                    postInvalidate();
                }
            }
        }).start();
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        if (mCell != null && mCell.getImageMarginTop() != 0) {
            Drawable compass = getDrawable();
            if (compass == null) {
                if (resID != 0) {
                    compass = getResources().getDrawable(resID);
                }
                if (compass != null) {
                    compass.setBounds(0, 0, getWidth(), getHeight());
                    canvas.save();
                    compass.draw(canvas);
                    canvas.restore();
                }
            } else {
                compass.setBounds(0, 0 - mCell.getImageMarginTop(), getWidth(), getHeight());
                canvas.save();
                compass.draw(canvas);
                canvas.restore();
            }
        } else {
            super.onDraw(canvas);
        }
        try {
            if (subScripts != null && subScripts.size() > 0) {
                for (SubScript subScript : subScripts) {
                    if (!TextUtils.isEmpty(subScript.name)) {
//                        FlyLog.d(subScript.name);
                        mFontMetrics = paint.getFontMetrics();
                        paint.getTextBounds(subScript.name, 0, subScript.name.length(), mTextRect);

                        float textLength = paint.measureText(subScript.name) + pading;
                        //绘画
                        switch (subScript.pos) {
                            case 1: {//右上角
                                canvas.drawText(subScript.name, getWidth() - mTextRect.right - pading, 0 - mFontMetrics.top + pading, paint);
                                break;
                            }
                            case 2: {//左下角
                                canvas.drawText(subScript.name, pading, getHeight() - mFontMetrics.bottom - pading, paint);
                                break;
                            }
                            case 3: {//右下角
                                canvas.drawText(subScript.name, getWidth() - mTextRect.right - pading, getHeight() - mFontMetrics.bottom - pading, paint);
                                break;
                            }
                            default: {//默认左上角
                                canvas.drawText(subScript.name, pading, 0 - mFontMetrics.top + pading, paint);
                                break;
                            }
                        }
                    } else if (!TextUtils.isEmpty(subScript.url)) {
//                        FlyLog.i(subScript.url);
                        float width = subScript.width * screenScale;
                        float height = subScript.height * screenScale;
                        Bitmap bitmap = mBitmapCache.getBitmap(subScript.url);
                        if (bitmap != null) {
                            matrix.reset();
                            matrix.setScale((float) subScript.width / (float) mBitmapCache.getBitmap(subScript.url).getWidth() * (float) screenScale,
                                    (float) subScript.height / (float) mBitmapCache.getBitmap(subScript.url).getHeight() * (float) screenScale);
                            //绘画
                            switch (subScript.pos) {
                                case 1: {//右上角
                                    matrix.postTranslate(getWidth() - width, 0);
                                    break;
                                }
                                case 2: {//左下角
                                    matrix.postTranslate(0, getHeight() - height);
                                    break;
                                }
                                case 3: {//右下角
                                    matrix.postTranslate(getWidth() - width, getHeight() - height);
                                    break;
                                }
                                default: {//默认左上角
                                    break;
                                }
                            }
                            canvas.drawBitmap(bitmap, matrix, paint);
                        }
                    } else {
                        FlyLog.e("name and url are null...");
                    }
                }
            }
        } catch (Exception e) {
            FlyLog.e(e.toString());
            e.printStackTrace();
        }
    }

    public void setPlaceholder(int resID) {
        this.resID = resID;
        invalidate();
    }

}
