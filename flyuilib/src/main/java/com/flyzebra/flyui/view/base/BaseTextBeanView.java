package com.flyzebra.flyui.view.base;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.TypedValue;

import com.flyzebra.flyui.bean.TextBean;
import com.flyzebra.flyui.event.FlyEvent;
import com.flyzebra.flyui.event.IFlyEvent;
import com.flyzebra.flyui.utils.ByteUtil;
import com.flyzebra.flyui.utils.FlyLog;
import com.flyzebra.flyui.view.customview.FlyTextView;

/**
 * Author FlyZebra
 * 2019/5/20 16:08
 * Describ:
 **/
public class BaseTextBeanView extends FlyTextView implements IFlyEvent {
    private TextBean mTextBean;

    public BaseTextBeanView(Context context) {
        super(context);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        FlyEvent.register(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        FlyEvent.unregister(this);
        super.onDetachedFromWindow();
    }

    public void setTextBean(TextBean textBean) {
        if (textBean == null) return;
        mTextBean = textBean;
        if (TextUtils.isEmpty(textBean.textColor)) {
            textBean.textColor = "#FFFFFF";
        }
        try{
            setTextColor(Color.parseColor(textBean.textColor));
        }catch (Exception e){
            FlyLog.e(e.toString());
        }
        setTextSize(TypedValue.COMPLEX_UNIT_PX, textBean.textSize);
        setGravity(textBean.getGravity());
        if (textBean.textLines <= 0) {
            setLines(1);
        } else {
            setLines(textBean.textLines);
        }
        if (textBean.text != null) {
            setText(textBean.text.getText());
        }
        if (textBean.recv != null) {
            if (!TextUtils.isEmpty(textBean.recv.recvId)) {
                recvEvent(ByteUtil.hexString2Bytes(textBean.recv.recvId));
            }

            if (!TextUtils.isEmpty(textBean.recv.keyId)) {
                try {
                    setId(Integer.valueOf(textBean.recv.keyId, 16));
                } catch (Exception e) {
                    FlyLog.e(e.toString());
                }
            }
        }

    }

    @Override
    public boolean recvEvent(byte[] key) {
        if (mTextBean == null || mTextBean.recv == null || mTextBean.recv.recvId == null) {
            return false;
        }
        String strKey = ByteUtil.bytes2HexString(key);
        if (!mTextBean.recv.recvId.equals(strKey)) {
            return false;
        }
        switch (strKey) {
            default:
                Object obj = FlyEvent.getValue(key);
                if (obj instanceof String) {
                    FlyLog.d("Set recv text=" + obj);
                    setText((String) obj);
                }
                break;
        }
        return false;
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if (mTextBean != null && !TextUtils.isEmpty(mTextBean.textFilter)) {
            try {
                setTextColor(Color.parseColor(selected ? mTextBean.textFilter : mTextBean.textColor));
            } catch (Exception e) {
                setTextColor(0xFFFFFFFF);
                FlyLog.e(e.toString());
            }
        }
    }
}
