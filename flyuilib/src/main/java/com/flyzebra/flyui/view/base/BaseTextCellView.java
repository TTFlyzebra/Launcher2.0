package com.flyzebra.flyui.view.base;

import android.content.Context;
import android.view.ViewGroup;

import com.flyzebra.flyui.bean.CellBean;
import com.flyzebra.flyui.event.FlyEvent;
import com.flyzebra.flyui.event.IFlyEvent;
import com.flyzebra.flyui.utils.FlyLog;
import com.flyzebra.flyui.view.cellview.ICell;
import com.flyzebra.flyui.view.customview.FlyTextView;

public abstract class BaseTextCellView extends FlyTextView implements IFlyEvent, ICell {
    protected CellBean mCellBean;

    public BaseTextCellView(Context context) {
        super(context);
    }

    @Override
    protected void onAttachedToWindow() {
        FlyLog.v("onAttachedToWindow");
        super.onAttachedToWindow();
        FlyEvent.register(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        FlyLog.v("onDetachedFromWindow");
        FlyEvent.unregister(this);
        super.onDetachedFromWindow();
    }

    @Override
    public final void setCellBean(CellBean cellBean) {
        FlyLog.d("setCellBean");
        this.mCellBean = cellBean;
        if (verify(mCellBean)) {
            init(mCellBean);
            loadingRes(mCellBean);
            refresh(mCellBean);
        }
    }

    @Override
    public boolean verify(CellBean cellBean) {
        return false;
    }

    @Override
    public void init(CellBean cellBean) {

    }

    @Override
    public void loadingRes(CellBean cellBean) {

    }

    @Override
    public void refresh(CellBean cellBean) {

    }

    @Override
    public void onClick() {

    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        setSelectStyle(selected);
    }

    @Override
    public void setSelectStyle(boolean isSelect) {

    }

    @Override
    public void bindMirrorView(ViewGroup viewGroup, ViewGroup.LayoutParams lpMirror) {

    }

    @Override
    public boolean recvEvent(byte[] key) {
        return false;
    }

}
