package com.flyzebra.flyui.view.cellview;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.flyzebra.flyui.bean.CellBean;
import com.flyzebra.flyui.chache.UpdataVersion;
import com.flyzebra.flyui.utils.FlyLog;
import com.flyzebra.flyui.view.base.BaseImageCellView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Author FlyZebra
 * 2019/4/13 14:35
 * Describ:
 **/
public class AnimtorCellView extends BaseImageCellView {
    private List<Drawable> drawables = new ArrayList<>();
    private AtomicInteger count = new AtomicInteger(0);

    public AnimtorCellView(Context context) {
        super(context);
        setVisibility(GONE);
    }

    @Override
    public void init(CellBean cellBean) {
        try{
            setId(Integer.valueOf(cellBean.recv.keyId,16));
        }catch (Exception e){
            FlyLog.e(e.toString());
        }
    }


    @Override
    public void loadingRes(CellBean cellBean) {
        if (cellBean.images == null || cellBean.images.isEmpty()) return;
        drawables.clear();
        for (int i = 0; i < cellBean.images.size(); i++) {
            final int num = i;
            count.incrementAndGet();
            String url = UpdataVersion.getNativeFilePath(cellBean.images.get(i).url);
            Glide.with(getContext())
                    .load(url)
                    .override(cellBean.images.get(i).width, cellBean.images.get(i).height)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(new CustomTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            drawables.add(resource);
                            if (count.decrementAndGet() <= 0) {
                                runLoadingFinish();
                            }
                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            if (count.decrementAndGet() <= 0) {
                                runLoadingFinish();
                            }
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }
                    });
        }
    }

    private void runLoadingFinish() {
        AnimationDrawable frameAnim = new AnimationDrawable();
        for (Drawable draw : drawables) {
            frameAnim.addFrame(draw, 150);
        }
        setImageDrawable(frameAnim);
        frameAnim.start();
    }

    @Override
    public void setSelectStyle(boolean isSelect) {
        setVisibility(isSelect ? VISIBLE : GONE);
    }
}
