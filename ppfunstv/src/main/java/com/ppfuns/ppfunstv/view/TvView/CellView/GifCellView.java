package com.ppfuns.ppfunstv.view.TvView.CellView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.ppfuns.ppfunstv.data.CellEntity;
import com.ppfuns.ppfunstv.view.TvView.IPageChangeListener;

/**
 * Gif图片显示控件
 * Created by flyzebra on 17-6-6.
 */
public class GifCellView extends SimpleCellView implements IPageChangeListener {
    private Rect rect = new Rect();
    private Handler mHander = new Handler(Looper.getMainLooper());

    public GifCellView(Context context) {
        super(context);
    }

    public GifCellView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GifCellView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public View setCellData(CellEntity cellEntity) {
        cellEntity.setFocusType(1); //0:圆角焦点框，1:矩形焦点框
        cellEntity.setFocusScale(1);
        return super.setCellData(cellEntity);
    }

    @Override
    public void showImage(String imgUrl) {
        mImageView.setVisibility(INVISIBLE);
        ImageView imageView = getMyImageView();
        if (imageView == null) return;

        String filePath = iDiskCache.getBitmapPath(imgUrl);
        Glide.with(mContext)
                .load(filePath)
                .override(mCell.getWidth(), mCell.getHeight())
                .placeholder(mLoadImageResId)
//                .transform(new GlideRoundTransform(mContext, 12))
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
//                .transform(new GlideRoundTransform(mContext, 12))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        showReflectView(resource);
                    }
                });
    }

    private int tryNum = 0;

    private Runnable task = new Runnable() {
        @Override
        public void run() {
            if (getLocalVisibleRect(rect)) {
                showGif();
            }
            tryNum++;
            if (tryNum < 5) {
                mHander.postDelayed(task, 100);
            }
        }
    };

    @Override
    public void pageIn() {
        tryNum = 0;
        mHander.removeCallbacks(task);
        mHander.post(task);
    }

    @Override
    public void pageOut() {
        mHander.removeCallbacks(task);
        hideGif();
    }

    @Override
    protected void onDetachedFromWindow() {
        mHander.removeCallbacks(task);
        super.onDetachedFromWindow();
    }

    @Override
    public void pageScroll() {
        if (getLocalVisibleRect(rect)) {
            showGif();
        } else {
            hideGif();
        }
    }

    private void showGif() {
        mImageView.setVisibility(VISIBLE);
    }

    private void hideGif() {
        mImageView.setVisibility(INVISIBLE);
    }

}
