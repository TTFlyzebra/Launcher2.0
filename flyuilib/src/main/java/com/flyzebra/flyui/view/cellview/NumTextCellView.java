package com.flyzebra.flyui.view.cellview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.flyzebra.flyui.bean.CellBean;
import com.flyzebra.flyui.bean.TextBean;
import com.flyzebra.flyui.chache.UpdataVersion;
import com.flyzebra.flyui.event.FlyEvent;
import com.flyzebra.flyui.utils.ByteUtil;
import com.flyzebra.flyui.utils.FlyLog;
import com.flyzebra.flyui.view.base.BaseView;


public class NumTextCellView extends BaseView {
    private Bitmap numBitmap;
    private String text = "87.5";
    private int bitmapWidth;
    private int bitmapHeigth;
    private int textWidth;
    private int textSumWidth;
    private Rect dstRect = new Rect();
    private Rect srcRect = new Rect();
    private Paint paint = new Paint();

    public NumTextCellView(Context context) {
        super(context);
    }

    @Override
    public boolean verify(CellBean cellBean) {
        return !(cellBean == null ||
                cellBean.texts == null ||
                cellBean.texts.isEmpty() ||
                cellBean.images == null ||
                cellBean.images.isEmpty());
    }


    private void initBitmap(Bitmap bitmap) {
        numBitmap = bitmap;
        bitmapWidth = numBitmap.getWidth();
        bitmapHeigth = numBitmap.getHeight();
        textWidth = bitmapWidth / 11;
        srcRect.top = 0;
        srcRect.bottom = bitmapHeigth;

        dstRect.top = mCellBean.height / 2 - bitmapHeigth / 2;
        dstRect.bottom = dstRect.top + bitmapHeigth;

        TextBean textBean = mCellBean.texts.get(0);
        if (textBean == null || textBean.recv == null || textBean.recv.recvId == null) {
            return;
        }
        Object obj = FlyEvent.getValue(textBean.recv.recvId);
        if (obj instanceof String) {
            FlyLog.d("Set recv text=" + obj);
            setText((String) obj);
        }
    }

    @Override
    public void loadingRes(CellBean cellBean) {
        String imageurl = UpdataVersion.getNativeFilePath(cellBean.images.get(0).url);
        Glide.with(getContext())
                .asBitmap()
                .load(imageurl)
                .override(cellBean.images.get(0).width, cellBean.images.get(0).height)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        initBitmap(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (numBitmap == null || textSumWidth == 0) return;
        int reduce = 0;
        int startx = (mCellBean.width - textSumWidth) / 2;
        FlyLog.d("startx = %d,textSumWidth=%d", startx, textSumWidth);
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            dstRect.left = i * textWidth - reduce + startx;
            dstRect.right = dstRect.left + textWidth;
            switch (c) {
                case '0':
                    srcRect.left = 9 * textWidth;
                    break;
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    srcRect.left = (c - '1') * textWidth;
                    break;
                case '.':
                default:
                    srcRect.left = 10 * textWidth;
                    reduce = textWidth / 2;
                    break;
            }
            srcRect.right = textWidth + srcRect.left;
            canvas.drawBitmap(numBitmap, srcRect, dstRect, paint);
        }

    }

    public void setText(String text) {
        this.text = text;
        refresh(mCellBean);
    }

    @Override
    public void refresh(CellBean cellBean) {
        if (TextUtils.isEmpty(text)) return;
        int count = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c >= '0' && c <= '9') {
                count += 2;
            } else {
                count++;
            }
        }
        textSumWidth = count * textWidth / 2;
        postInvalidate();
    }

    @Override
    public boolean recvEvent(byte[] key) {
        TextBean textBean = mCellBean.texts.get(0);
        if (textBean == null || textBean.recv == null || textBean.recv.recvId == null) {
            return false;
        }
        if (!textBean.recv.recvId.equals(ByteUtil.bytes2HexString(key))) {
            return false;
        }
        Object obj = FlyEvent.getValue(key);
        if (obj instanceof String) {
            FlyLog.d("Set recv text=" + obj);
            setText((String) obj);
        }
        return false;
    }
}
