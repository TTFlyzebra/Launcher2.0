package com.flyzebra.flyui.view.pageview;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;

import com.flyzebra.flyui.bean.CellBean;
import com.flyzebra.flyui.bean.PageBean;
import com.flyzebra.flyui.utils.FlyLog;
import com.flyzebra.flyui.view.cellview.CellViewFactory;
import com.flyzebra.flyui.view.cellview.ICell;
import com.flyzebra.flyui.view.customview.MirrorView;

import java.util.List;


public class SimplePageView extends FrameLayout implements IPage {
    private PageBean pageBean;
    private int width;
    private int height;
    private boolean isMirror = false;

    public SimplePageView(Context context) {
        this(context, null);
    }

    public SimplePageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimplePageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;
        height = dm.heightPixels;
        FlyLog.d("width=%d,height=%d", width, height);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    @Override
    public void setPageBean(PageBean pageBean) {
        this.pageBean = pageBean;
        List<CellBean> appInfoList = pageBean.cellList;
        if (appInfoList == null || appInfoList.isEmpty()) return;
        addAllItemView(appInfoList);
    }

    private void addAllItemView(List<CellBean> appInfoList) {
        if (appInfoList == null || appInfoList.isEmpty()) return;
        int sx = 0;
        int sy = 0;
        FlyLog.d("sx=%d,sy=%d", sx, sy);
        for (int i = 0; i < appInfoList.size(); i++) {
            //多出的Cell不进行绘制
            CellBean cellBean = appInfoList.get(i);
            ICell iCellView = CellViewFactory.createView(getContext(), cellBean);
            LayoutParams lp;

            lp = new LayoutParams(cellBean.width, cellBean.height);
            lp.setMarginStart(cellBean.x);
            lp.topMargin = cellBean.y;

            //添加镜像
            if (isMirror) {
                LayoutParams lpMirror;
                lpMirror = new LayoutParams(cellBean.width, MirrorView.MIRRORHIGHT);
                lpMirror.setMarginStart(cellBean.x);
                lpMirror.topMargin = lp.topMargin + cellBean.height;
                iCellView.bindMirrorView(SimplePageView.this, lpMirror);
            }
            iCellView.setCellBean(cellBean);
            addView((View) iCellView, lp);
        }
    }

    @Override
    public void showMirror(boolean flag) {
        isMirror = flag;
    }
}
