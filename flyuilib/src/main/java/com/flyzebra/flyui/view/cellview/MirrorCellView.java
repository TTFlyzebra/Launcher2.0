package com.flyzebra.flyui.view.cellview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.flyzebra.flyui.bean.CellBean;
import com.flyzebra.flyui.bean.ImageBean;
import com.flyzebra.flyui.chache.UpdataVersion;
import com.flyzebra.flyui.view.customview.MirrorView;

public class MirrorCellView extends SimpleCellView {
    private MirrorView mirrorImageView;

    public MirrorCellView(Context context) {
        super(context);
    }

    @Override
    public boolean verify(CellBean cellBean) {
        return mCellBean!=null&&mCellBean.images!=null&&mCellBean.images.size()>0;
    }

    @Override
    public void init(CellBean cellBean) {
        super.init(cellBean);
        ImageBean imageBean = mCellBean.images.get(0);
        mirrorImageView = new MirrorView(getContext());
        mirrorImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        mirrorImageView.setAlpha(0.4f);
        int width = (mCellBean.width - imageBean.right) - imageBean.left;
        int height = (mCellBean.height - imageBean.bottom) - imageBean.top;
        LayoutParams lp = new LayoutParams(width == 0 ? mCellBean.width : width, (int) ((height == 0 ? mCellBean.height : height) / 2.5));
        lp.topMargin = height;
        addView(mirrorImageView, lp);
    }

    public void refresh(CellBean cellBean) {
        super.refresh(cellBean);
        String imageurl = UpdataVersion.getNativeFilePath(mCellBean.images.get(0).url);
        Glide.with(getContext())
                .asBitmap()
                .load(imageurl)
                .override(mCellBean.width, mCellBean.height)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        mirrorImageView.showImage(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

}
