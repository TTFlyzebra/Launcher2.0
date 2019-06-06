package com.flyzebra.flyui.view.base;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.flyzebra.flyui.bean.CellBean;
import com.flyzebra.flyui.event.FlyEvent;
import com.flyzebra.flyui.event.IFlyEvent;
import com.flyzebra.flyui.utils.FlyLog;
import com.flyzebra.flyui.view.cellview.ICell;
import com.flyzebra.flyui.view.customview.FlyImageView;

public abstract class BaseImageCellView extends FlyImageView implements IFlyEvent, ICell,View.OnClickListener,View.OnTouchListener {
    protected CellBean mCellBean;
    protected Handler mHandler = new Handler();
    public BaseImageCellView(Context context) {
        super(context);
    }
    private Runnable show = new Runnable() {
        @Override
        public void run() {
            focusChange(false);
        }
    };

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
        if (mCellBean.send != null) {
            setOnClickListener(this);
            setOnTouchListener(this);
        }
    }


    @Override
    public boolean verify(CellBean cellBean) {
        return mCellBean!=null&&mCellBean.images!=null&&mCellBean.images.size()>0;
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
        BaseViewFunc.onClick(getContext(),mCellBean.send);
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
            try {
                setColorFilter(Color.parseColor(mCellBean.filterColor));
            } catch (Exception e) {
                setColorFilter(0x3FFFFFFF);
            }
            mHandler.removeCallbacks(show);
            mHandler.postDelayed(show, 300);
        } else {
            clearColorFilter();
        }
    }

    @Override
    public void onClick(View v) {
        onClick();
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
