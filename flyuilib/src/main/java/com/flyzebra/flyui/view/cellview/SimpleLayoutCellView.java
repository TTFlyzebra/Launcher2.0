package com.flyzebra.flyui.view.cellview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.flyzebra.flyui.bean.CellBean;
import com.flyzebra.flyui.view.base.BaseLayoutCellView;

public class SimpleLayoutCellView extends BaseLayoutCellView implements ICell, View.OnClickListener {

    public SimpleLayoutCellView(Context context) {
        super(context);
    }

    @Override
    public boolean verify(CellBean cellBean) {
       return mCellBean!=null;
    }

    @Override
    public void loadingRes(CellBean cellBean) {
    }

    @Override
    public void init(CellBean cellBean) {
    }

    @Override
    public void refresh(CellBean cellBean) {
    }

    @Override
    public void onClick() {
    }

    @Override
    public void bindMirrorView(ViewGroup viewGroup, ViewGroup.LayoutParams lpMirror) {
    }


    @Override
    public void onClick(View v) {
        onClick();
    }

}
