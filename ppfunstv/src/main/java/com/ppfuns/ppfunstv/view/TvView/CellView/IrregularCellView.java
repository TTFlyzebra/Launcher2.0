package com.ppfuns.ppfunstv.view.TvView.CellView;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.ppfuns.ppfunstv.data.CellEntity;

/**
 *凸头控件
 * Created by fagro on 17-5-3.
 */

public class IrregularCellView extends SimpleCellView {

    public IrregularCellView(Context context) {
        this(context,null);
    }

    public IrregularCellView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public IrregularCellView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setClipChildren(false);
        setClipToPadding(false);
    }

    @Override
    public View setCellData(CellEntity cellEntity) {
        cellEntity.setFocusZorder(1);
        return super.setCellData(cellEntity);
    }

    @Override
    public void showImage(final String imgUrl) {
        ImageView imageView = getMyImageView();
        if (imageView == null) return;


        if(imageView instanceof SubScriptView){
            ((SubScriptView)imageView).setPlaceholder(mLoadImageResId);
        }

        String filePath = iDiskCache.getBitmapPath(imgUrl);
        Glide.with(mContext)
                .load(filePath)
                .override(mCell.getWidth(), mCell.getHeight())
//                .placeholder(loadImageResId)
//                .transform(new GlideRoundTransform(mContext, 40))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(imageView);

        /**
         * 显示镜像图片，用Glide拉伸图片完美显示比例
         */
        if (reflectImageView == null) return;
        Glide.with(mContext)
                .load(filePath)
                .asBitmap()
                .override(mCell.getWidth(), mCell.getHeight())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        showReflectView(resource);
                    }
                });
    }

}
