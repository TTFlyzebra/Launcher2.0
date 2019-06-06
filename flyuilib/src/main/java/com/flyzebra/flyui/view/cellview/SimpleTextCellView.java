package com.flyzebra.flyui.view.cellview;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;

import com.flyzebra.flyui.bean.CellBean;
import com.flyzebra.flyui.bean.TextBean;
import com.flyzebra.flyui.event.FlyEvent;
import com.flyzebra.flyui.utils.ByteUtil;
import com.flyzebra.flyui.utils.FlyLog;
import com.flyzebra.flyui.view.base.BaseTextCellView;

/**
 * Author FlyZebra
 * 2019/4/3 16:22
 * Describ:
 **/
public class SimpleTextCellView extends BaseTextCellView {

    public SimpleTextCellView(Context context) {
        super(context);
    }

    @Override
    public boolean verify(CellBean cellBean) {
        return !(cellBean == null ||
                cellBean.texts == null ||
                cellBean.texts.isEmpty());
    }

    @Override
    public void init(CellBean cellBean) {
        TextBean textBean = mCellBean.texts.get(0);
        try {
            setTextColor(Color.parseColor(textBean.textColor));
        } catch (Exception e) {
            setTextColor(0xffffffff);
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

        if (textBean == null || textBean.recv == null || textBean.recv.recvId == null) {
            return ;
        }
        Object obj = FlyEvent.getValue(textBean.recv.recvId);
        if(obj instanceof String){
            FlyLog.d("Set recv text="+obj);
            setText((String) obj);
        }
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
        if(obj instanceof String){
            FlyLog.d("Set recv text="+obj);
            setText((String) obj);
        }
        return false;
    }
}
