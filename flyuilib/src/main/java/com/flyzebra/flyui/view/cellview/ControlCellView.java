package com.flyzebra.flyui.view.cellview;

import android.content.Context;

import com.flyzebra.flyui.bean.CellBean;
import com.flyzebra.flyui.view.base.BaseViewFunc;


public class ControlCellView extends SimpleCellView {

    public ControlCellView(Context context) {
        super(context);
    }

    @Override
    public void init(CellBean cellBean) {
        super.init(cellBean);
        BaseViewFunc.sendRecvEvent(mCellBean.recv, this);
        for (int i = 0; i < imageViewList.size(); i++) {
            BaseViewFunc.sendRecvEvent(mCellBean.images.get(i).recv, this);
        }
    }

    @Override
    public boolean recvEvent(byte[] key) {
        if (mCellBean == null) {
            return false;
        }
        BaseViewFunc.handVisible(this, key, mCellBean.recv);
        for (int i = 0; i < imageViewList.size(); i++) {
            if (mCellBean.images.get(i) != null) {
                BaseViewFunc.handVisible(imageViewList.get(i), key, mCellBean.images.get(i).recv);
            }
        }
        return false;
    }

}
