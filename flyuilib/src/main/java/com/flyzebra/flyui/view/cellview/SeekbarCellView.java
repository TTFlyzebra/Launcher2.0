package com.flyzebra.flyui.view.cellview;

import android.content.Context;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.flyzebra.flyui.bean.CellBean;
import com.flyzebra.flyui.bean.TextBean;
import com.flyzebra.flyui.event.FlyEvent;
import com.flyzebra.flyui.utils.ByteUtil;
import com.flyzebra.flyui.view.base.BaseLayoutCellView;
import com.flyzebra.flyui.view.base.BaseTextBeanView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.graphics.drawable.ClipDrawable.HORIZONTAL;

/**
 * Author FlyZebra
 * 2019/4/11 13:32
 * Describ:
 **/
public class SeekbarCellView extends BaseLayoutCellView implements SeekBar.OnSeekBarChangeListener {
    private SeekBar seekBar;
    private Drawable draw1 = null;
    private Drawable draw2 = null;
    private Drawable draw3 = null;
    protected List<TextView> textViewList = new ArrayList<>();

    public SeekbarCellView(Context context) {
        super(context);
    }

    @Override
    public boolean verify(CellBean cellBean) {
        return mCellBean!=null&&mCellBean.images!=null&&mCellBean.images.size()>2;
    }

    @Override
    public void init(CellBean cellBean) {
//        setClipChildren(false);

        textViewList.clear();
        removeAllViews();

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

        seekBar = new SeekBar(getContext());
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, mCellBean.images.get(0).height);
        lp.topMargin = mCellBean.images.get(0).top;
        lp.bottomMargin = mCellBean.images.get(0).bottom;
        lp.setMarginStart(mCellBean.images.get(0).left);
        lp.setMarginEnd(mCellBean.images.get(0).right);
        addView(seekBar, lp);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            seekBar.setSplitTrack(false);
        }
        seekBar.setMinimumHeight(mCellBean.images.get(0).height);
        seekBar.setOnSeekBarChangeListener(this);
    }


    @Override
    public void loadingRes(CellBean cellBean) {
        Glide.with(getContext())
                .load(mCellBean.images.get(0).url)
                .override(mCellBean.images.get(0).width,mCellBean.images.get(0).height)
                .into(new CustomTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                draw1 = resource;
                loadBitmapFinish();
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }
        });
        Glide.with(getContext())
                .load(mCellBean.images.get(1).url)
                .override(mCellBean.images.get(1).width,mCellBean.images.get(1).height)
                .into(new CustomTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                draw2 = resource;
                loadBitmapFinish();
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }

        });
        Glide.with(getContext())
                .load(mCellBean.images.get(2).url)
                .override(mCellBean.images.get(2).width,mCellBean.images.get(2).height)
                .into(new CustomTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                draw3 = resource;
                loadBitmapFinish();
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }
        });

    }

    private void loadBitmapFinish() {
        if (draw1 != null && draw2 != null && draw3 != null) {
            Drawable[] drawables = new Drawable[3];
            drawables[0] = draw1;
            ClipDrawable clipDrawable = new ClipDrawable(draw1, Gravity.START, HORIZONTAL);
            drawables[1] = clipDrawable;
            clipDrawable = new ClipDrawable(draw2, Gravity.START, HORIZONTAL);
            drawables[2] = clipDrawable;
            LayerDrawable layerDrawable = new LayerDrawable(drawables);
            layerDrawable.setId(0, android.R.id.background);
            layerDrawable.setId(1, android.R.id.secondaryProgress);
            layerDrawable.setId(2, android.R.id.progress);
            seekBar.setProgressDrawable(layerDrawable);

            seekBar.setThumb(draw3);
        }
    }


    @Override
    public boolean recvEvent(byte[] key) {
        if (seekBar == null || mCellBean == null) return false;
        switch (ByteUtil.bytes2HexString(key)) {
            case "100226":
                Object obj = FlyEvent.getValue(key);
                if (obj instanceof byte[]) {
                    int c = ByteUtil.bytes2Int((byte[]) obj, 0);
                    int t = ByteUtil.bytes2Int((byte[]) obj, 4);
                    seekBar.setMax(t);
                    seekBar.setProgress(c);

                    if(textViewList.size()>1){
                        textViewList.get(0).setText(generateTime(c));
                        textViewList.get(1).setText(generateTime(t));
                    }
                }
                return false;
            default:
                return false;
        }
    }

    private String generateTime(int time) {
        time = Math.min(Math.max(time, 0), 359999000);
        int totalSeconds = time;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        return hours > 0 ?
                String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds) :
                String.format(Locale.US, "%02d:%02d", minutes, seconds);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int pos = seekBar.getProgress();
        FlyEvent.sendEvent("200306",pos);
    }
}
