package com.flyzebra.flyui.view.cellview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.flyzebra.flyui.bean.CellBean;
import com.flyzebra.flyui.bean.ImageBean;
import com.flyzebra.flyui.chache.UpdataVersion;
import com.flyzebra.flyui.event.FlyEvent;
import com.flyzebra.flyui.utils.ByteUtil;
import com.flyzebra.flyui.utils.FlyLog;
import com.flyzebra.flyui.view.base.BaseImageCellView;
import com.flyzebra.flyui.view.customview.MirrorView;

public class SimpleImageCellView extends BaseImageCellView  {
    private MirrorView mirrorView;
    private Bitmap mBitmap;

    public SimpleImageCellView(Context context) {
        super(context);
    }


    @Override
    public boolean verify(CellBean cellBean) {
        return !(cellBean == null ||
                cellBean.images == null ||
                cellBean.images.isEmpty());
    }

    @Override
    public void init(CellBean cellBean) {
        setScaleType(mCellBean.images.get(0).getScaleType());
    }

    @Override
    public void refresh(CellBean cellBean) {
        String imageurl = UpdataVersion.getNativeFilePath(cellBean.images.get(0).url);
        Glide.with(getContext())
                .asBitmap()
                .load(imageurl)
                .override(cellBean.images.get(0).width, cellBean.images.get(0).height)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .centerInside()
                .into(new BitmapImageViewTarget(this) {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        if (mBitmap == null){
                            setImageBitmap(resource);
                        }
                        if (mirrorView != null) {
                            setDrawingCacheEnabled(true);
                            Bitmap bmp = getDrawingCache();
                            if (bmp == null) {
                                measure(MeasureSpec.makeMeasureSpec(mCellBean.width, MeasureSpec.EXACTLY),
                                        MeasureSpec.makeMeasureSpec(mCellBean.height, MeasureSpec.EXACTLY));
                                layout(0, 0, getMeasuredWidth(), getMeasuredHeight());
                                buildDrawingCache();
                                bmp = getDrawingCache();
                            }
                            mirrorView.showImage(bmp);
                        }
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                    }
                });
    }

    @Override
    public void bindMirrorView(ViewGroup viewGroup, ViewGroup.LayoutParams lpMirror) {
        MirrorView mirrorView = new MirrorView(getContext());
        mirrorView.setScaleType(ImageView.ScaleType.FIT_XY);
        mirrorView.setRefHeight(MirrorView.MIRRORHIGHT);
        viewGroup.addView(mirrorView, lpMirror);
        this.mirrorView = mirrorView;
    }



    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mCellBean == null || mCellBean.images.isEmpty()) return;
        ImageBean imageBean = mCellBean.images.get(0);
        if (imageBean == null || imageBean.recv == null || imageBean.recv.recvId == null) return ;
        recvEvent(ByteUtil.hexString2Bytes(imageBean.recv.recvId));
    }

    @Override
    protected void onDetachedFromWindow() {
        mHandler.removeCallbacksAndMessages(null);
        super.onDetachedFromWindow();
    }


    @Override
    public boolean recvEvent(byte[] key) {
        if (mCellBean == null || mCellBean.images.isEmpty()) return false;
        ImageBean imageBean = mCellBean.images.get(0);
        if (imageBean == null || imageBean.recv == null || imageBean.recv.recvId == null) {
            return false;
        }
        if (!imageBean.recv.recvId.equals(ByteUtil.bytes2HexString(key))) {
            return false;
        }
        switch (imageBean.recv.recvId) {
            case "100227":
                final byte[] imageBytes = (byte[]) FlyEvent.getValue(imageBean.recv.recvId);
                FlyLog.d("handle 100227 imageBytes=" + imageBytes);
                if (imageBytes == null) {
                    mBitmap = null;
                    refresh(mCellBean);
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                            } catch (Exception e) {
                                FlyLog.e(e.toString());
                                return;
                            }
                            if (mBitmap == null) return;
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (mBitmap != null) {
                                        setImageBitmap(mBitmap);
                                    }
                                    FlyLog.d("handle 100227 finish; bitmap=" + mBitmap);
                                }
                            });
                        }
                    }).start();
                }
                break;

        }
        return false;
    }
}
