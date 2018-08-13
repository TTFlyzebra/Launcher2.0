package com.ppfuns.ppfunstv.view.TvView.CellView;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.support.annotation.DrawableRes;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.ppfuns.ppfunstv.data.CellEntity;
import com.ppfuns.ppfunstv.utils.FlyLog;

/**
 * 图片按钮控件
 * Created with Android Studio.
 * User: Fargo
 * Date: 2017/8/17
 * Time: 下午4:14
 */

public class StateListCellView extends SimpleCellView {


    private Drawable normalDrawable;
    private Drawable focusDrawable;

    public StateListCellView(Context context) {
        super(context);
    }

    public StateListCellView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StateListCellView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public View setCellData(CellEntity cellEntity) {
        cellEntity.setFocusType(1);
        cellEntity.setFocusScale(1);
        return super.setCellData(cellEntity);
    }

    @Override
    public void showImage(String imgUrl) {
        super.showImage(imgUrl);
        loadImageSrc(mCell);

    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        getMyImageView().setSelected(gainFocus);
        FlyLog.d("view is select ?" + getMyImageView().isSelected());
        FlyLog.d("view is fouce ?" + getMyImageView().isFocused());
    }

    /**
     * 加载图片
     *
     * @param entity
     */
    protected void loadImageSrc(CellEntity entity) {
        String imgUrl = entity.getImgUrl();
        String nomalFilePath = iDiskCache != null ? imgUrl : iDiskCache.getBitmapPath(imgUrl);
        final String focusFilePath = iDiskCache != null ? entity.getImgUrlBg() : iDiskCache.getBitmapPath(entity.getImgUrlBg());
        Glide.with(mContext)
                .load(nomalFilePath)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(new SimpleTarget<GlideDrawable>() {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        normalDrawable = resource;
                        if (normalDrawable != null && !TextUtils.isEmpty(focusFilePath)) {
                            if (focusDrawable != null) {

                                mImageView.setImageDrawable(getSelectorDrawable());
                            } else if (TextUtils.isEmpty(focusFilePath)) {
                                mImageView.setImageDrawable(resource);
                            }
                        }
                    }
                });
        if (TextUtils.isEmpty(focusFilePath)) return;
        Glide.with(mContext)
                .load(focusFilePath)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(new SimpleTarget<GlideDrawable>() {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        focusDrawable = resource;
                        if (normalDrawable != null && focusDrawable != null) {
                            mImageView.setImageDrawable(getSelectorDrawable());
                        }
                    }
                });


    }

    private Drawable getSelectorDrawable() {
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{-android.R.attr.state_selected}, normalDrawable);
        stateListDrawable.addState(new int[]{android.R.attr.state_selected}, focusDrawable);
        return stateListDrawable;
    }
}
