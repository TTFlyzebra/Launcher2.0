package com.flyzebra.flyui.view.cellview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.flyzebra.flyui.bean.CellBean;
import com.flyzebra.flyui.chache.UpdataVersion;
import com.flyzebra.flyui.event.FlyEvent;
import com.flyzebra.flyui.utils.ByteUtil;
import com.flyzebra.flyui.utils.FlyLog;
import com.flyzebra.flyui.view.base.BaseView;

import java.util.concurrent.atomic.AtomicInteger;


/**
 * Author FlyZebra
 * 2019/3/25 12:08
 * Describ:
 **/
public class SimpleNavCellView extends BaseView implements ICell {
    private Paint paint;
    private int sumItem = 0;
    private int currentItem = 1;
    private Bitmap nav_on;
    private Bitmap nav_off;
    private AtomicInteger count = new AtomicInteger(0);

    public SimpleNavCellView(Context context) {
        super(context);
    }

    @Override
    public boolean verify(CellBean mCellBean) {
        return (mCellBean != null && mCellBean.images != null && mCellBean.images.size() > 1);
    }

    @Override
    public void loadingRes(CellBean cellBean) {
        count.incrementAndGet();
        count.incrementAndGet();
        Glide.with(getContext())
                .asBitmap()
                .load(UpdataVersion.getNativeFilePath(cellBean.images.get(0).url))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        nav_on = resource;
                        if (count.decrementAndGet() <= 0) {
                            postInvalidate();
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }

                });

        Glide.with(getContext())
                .asBitmap()
                .load(UpdataVersion.getNativeFilePath(cellBean.images.get(1).url))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        nav_off = resource;
                        if (count.decrementAndGet() <= 0) {
                            postInvalidate();
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }


    @Override
    public boolean recvEvent(byte[] key) {
        if (mCellBean == null || mCellBean.recv == null || mCellBean.recv.recvId == null) {
            return false;
        }
        if (!mCellBean.recv.recvId.equals(ByteUtil.bytes2HexString(key))) {
            return false;
        }
        if ("400301".equals(mCellBean.recv.recvId)) {
            Object obj = FlyEvent.getValue(key);
            if (obj instanceof byte[]) {
                byte[] data = (byte[]) obj;
                if (data.length > 1) {
                    setCurrentItem(data[0]);
                    setSumItem(data[1]);
                    postInvalidate();
                }
            }
            FlyLog.d("handle event=400301");
            return false;
        }
        return false;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mCellBean != null && sumItem > 0 && nav_on != null && nav_off != null && mCellBean.width > 0 && mCellBean.height > 0) {
            float x = mCellBean.width / 2 - (sumItem * mCellBean.height) + mCellBean.height / 2;
            if (paint == null) {
                initPaint();
            }
            for (int i = 1; i <= sumItem; i++) {
                if (i == currentItem) {
                    canvas.drawBitmap(nav_on, x + (i - 1) * mCellBean.height * 2, 0, paint);
                } else {
                    canvas.drawBitmap(nav_off, x + (i - 1) * mCellBean.height * 2, 0, paint);
                }
            }
        }

    }

    private void initPaint() {
        if (paint == null) {
            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.FILL);
        }
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

}
