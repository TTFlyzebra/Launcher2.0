package com.flyzebra.flyui.view.cellview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.flyzebra.flyui.bean.CellBean;
import com.flyzebra.flyui.chache.UpdataVersion;
import com.flyzebra.flyui.event.FlyEvent;
import com.flyzebra.flyui.utils.ByteUtil;
import com.flyzebra.flyui.utils.FlyLog;
import com.flyzebra.flyui.view.base.BaseImageCellView;

import java.util.Hashtable;
import java.util.Map;

public class ImageTextCellView extends BaseImageCellView {
    private String[] contents;
    private boolean loadFinish = false;
    private Map<String, Drawable> drawables = new Hashtable<>();
    private String setContentKey;

    public ImageTextCellView(Context context) {
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
        try {
            if (cellBean.recv != null && !TextUtils.isEmpty(cellBean.recv.keyId)) {
                setId(Integer.valueOf(cellBean.recv.keyId, 16));
            }
        } catch (Exception e) {
            FlyLog.e(e.toString());
        }

        try {
            if (cellBean.recv != null && !TextUtils.isEmpty(cellBean.recv.visibleContent)) {
                contents = cellBean.recv.visibleContent.split("#");
                setContentKey = contents[0];
            }
        } catch (Exception e) {
            FlyLog.e(e.toString());
        }

        try {
            if (mCellBean.recv != null && !TextUtils.isEmpty(mCellBean.recv.recvId)) {
                recvEvent(ByteUtil.hexString2Bytes(mCellBean.recv.recvId));
            }
        } catch (Exception e) {
            FlyLog.e(e.toString());
        }
    }

    @Override
    public void loadingRes(CellBean cellBean) {
        drawables.clear();
        loadFinish = false;
        if(contents!=null) {
            for (int i = 0; i < contents.length; i++) {
                loadDrawable(i);
            }
        }
    }

    private void loadDrawable(int i) {
        if (i < 0 || i >= mCellBean.images.size()) return;
        String imageurl = UpdataVersion.getNativeFilePath(mCellBean.images.get(i).url);
        final String key = new String(contents[i]);
        Glide.with(getContext())
                .load(imageurl)
                .override(mCellBean.images.get(0).width, mCellBean.images.get(i).height)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .centerInside()
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        drawables.put(key, resource);
                        if (drawables.size() == contents.length) {
                            loadFinish = true;
                            FlyLog.d("loadFinish");
                            setContentDrawable(setContentKey);
                            if (mCellBean.recv != null && !TextUtils.isEmpty(mCellBean.recv.recvId)) {
                                recvEvent(ByteUtil.hexString2Bytes(mCellBean.recv.recvId));
                            }
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

    @Override
    public void refresh(CellBean cellBean) {
    }

    @Override
    public boolean recvEvent(byte[] key) {
        if (mCellBean == null || mCellBean.recv == null || mCellBean.recv.recvId == null) {
            return false;
        }
        if (!mCellBean.recv.recvId.equals(ByteUtil.bytes2HexString(key))) {
            return false;
        }
        try {
            Object obj = FlyEvent.getValue(key);
            if(loadFinish) {
                if (obj instanceof String) {
                    for (String str : contents) {
                        if (((String) obj).contains(str)) {
                            setContentDrawable(str);
                            break;
                        }
                    }
                }else if(obj instanceof byte[]){
                    for (String str : contents) {
                        if (ByteUtil.bytes2HexString((byte[]) obj).contains(str)) {
                            setContentDrawable(str);
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            FlyLog.e(e.toString());
        }
        FlyLog.d("handle event=" + ByteUtil.bytes2HexString(key));
        return false;
    }

    public void setContentDrawable(String mapKey) {
        FlyLog.d("setContentDrawable mapkey=%s",mapKey);
        try {
            if (!TextUtils.isEmpty(mapKey)&&drawables.get(mapKey)==null) {
                for (String str : contents) {
                    if (mapKey.contains(str)) {
                        mapKey = str;
                        break;
                    }
                }
            }
            setContentKey = mapKey;
            setImageDrawable(drawables.get(setContentKey));
        } catch (Exception e) {
            FlyLog.e(e.toString());
        }
    }


    @Override
    public void setSelectStyle(boolean isSelect) {
        if (mCellBean != null) {
            if (isSelect) {
                setColorFilter(Color.parseColor(mCellBean.filterColor));
            } else {
                clearColorFilter();
            }
        }
    }
}
