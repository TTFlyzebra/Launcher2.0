package com.flyzebra.flyui.view.cellview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.flyzebra.flyui.bean.CellBean;
import com.flyzebra.flyui.bean.ImageBean;
import com.flyzebra.flyui.bean.TextBean;
import com.flyzebra.flyui.chache.UpdataVersion;
import com.flyzebra.flyui.utils.FlyLog;
import com.flyzebra.flyui.view.base.BaseImageBeanView;
import com.flyzebra.flyui.view.base.BaseLayoutCellView;
import com.flyzebra.flyui.view.base.BaseTextBeanView;
import com.flyzebra.flyui.view.base.BaseViewFunc;
import com.flyzebra.flyui.view.customview.MirrorView;

import java.util.ArrayList;
import java.util.List;

public class SimpleCellView extends BaseLayoutCellView implements View.OnTouchListener, View.OnClickListener {
    protected List<ImageView> imageViewList = new ArrayList<>();
    private MirrorView mirrorView;
    protected List<TextView> textViewList = new ArrayList<>();
    protected Handler mHandler = new Handler();

    public SimpleCellView(Context context) {
        super(context);
    }

    @Override
    public boolean verify(CellBean cellBean) {
        return mCellBean != null;
    }

    @Override
    public void init(CellBean cellBean) {
        imageViewList.clear();
        textViewList.clear();
        removeAllViews();

        if (mCellBean.images != null) {
            for (ImageBean imageBean : mCellBean.images) {
                ImageView imageView = new BaseImageBeanView(getContext());
                ((BaseImageBeanView) imageView).setmImageBean(imageBean);
                int width = (mCellBean.width - imageBean.right) - imageBean.left;
                int height = (mCellBean.height - imageBean.bottom) - imageBean.top;
                LayoutParams lp = new LayoutParams(width == 0 ? mCellBean.width : width, height == 0 ? mCellBean.height : height);
                lp.leftMargin = imageBean.left;
                lp.topMargin = imageBean.top;
                addView(imageView, lp);
                imageViewList.add(imageView);
            }
        }

        if (mCellBean.texts != null) {
            for (TextBean textBean : mCellBean.texts) {
                TextView textView = new BaseTextBeanView(getContext());
                ((BaseTextBeanView) textView).setTextBean(textBean);
                int width = (mCellBean.width - textBean.right) - textBean.left;
                int height = (mCellBean.height - textBean.bottom) - textBean.top;
                LayoutParams lp = new LayoutParams(width == 0 ? mCellBean.width : width, height == 0 ? mCellBean.height : height);
                lp.leftMargin = textBean.left;
                lp.topMargin = textBean.top;
                addView(textView, lp);
                textViewList.add(textView);
            }
        }

        if (mCellBean.send != null) {
            setOnClickListener(this);
            setOnTouchListener(this);
        }

        try{
            if(!TextUtils.isEmpty(mCellBean.backColor)){
                setBackgroundColor(Color.parseColor(mCellBean.backColor));
            }
        }catch (Exception e){
            FlyLog.e(e.toString());
        }
    }

    @Override
    public void refresh(CellBean cellBean) {
        if (cellBean.images == null) return;
        for (int i = 0; i < cellBean.images.size(); i++) {
            String imageurl = UpdataVersion.getNativeFilePath(cellBean.images.get(i).url);
            final ImageView imageView = imageViewList.get(i);
            Glide.with(getContext())
                    .asBitmap()
                    .load(imageurl)
                    .override(cellBean.images.get(i).width, cellBean.images.get(i).height)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .centerInside()
                    .into(new BitmapImageViewTarget(imageView) {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            imageView.setImageBitmap(resource);
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
                    });
        }
    }

    /**
     * 启动优先级，包名+类名>Action>包名
     */
    @Override
    public void onClick() {
        BaseViewFunc.onClick(getContext(),mCellBean.send);
    }

    @Override
    public void bindMirrorView(ViewGroup viewGroup, ViewGroup.LayoutParams lpMirror) {
        MirrorView mirrorView = new MirrorView(getContext());
        mirrorView.setScaleType(ImageView.ScaleType.FIT_XY);
        mirrorView.setRefHeight(MirrorView.MIRRORHIGHT);
        viewGroup.addView(mirrorView, lpMirror);
        this.mirrorView = mirrorView;
    }

    private Runnable show = new Runnable() {
        @Override
        public void run() {
            focusChange(false);
        }
    };

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                focusChange(true);
                break;
            case MotionEvent.ACTION_MOVE:
                focusChange(isTouchPointInView(v, (int) event.getRawX(), (int) event.getRawY()));
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                focusChange(false);
                break;
        }
        return false;
    }

    private void focusChange(boolean flag) {
        if (flag) {
            for (ImageView imageView : imageViewList) {
                try {
                    imageView.setColorFilter(Color.parseColor(mCellBean.filterColor));
                } catch (Exception e) {
                    imageView.setColorFilter(0x3FFFFFFF);
                }
            }
            mHandler.removeCallbacks(show);
            mHandler.postDelayed(show, 300);
        } else {
            for (ImageView imageView : imageViewList) {
                imageView.clearColorFilter();
            }
        }
    }

    @Override
    public void onClick(View v) {
        onClick();
    }

    @Override
    protected void onDetachedFromWindow() {
        mHandler.removeCallbacksAndMessages(null);
        super.onDetachedFromWindow();
    }

    private boolean isTouchPointInView(View view, int x, int y) {
        if (view == null) {
            return false;
        }
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int left = location[0];
        int top = location[1];
        int right = left + view.getMeasuredWidth();
        int bottom = top + view.getMeasuredHeight();
        if (y >= top && y <= bottom && x >= left && x <= right) {
            return true;
        }
        return false;
    }
}
