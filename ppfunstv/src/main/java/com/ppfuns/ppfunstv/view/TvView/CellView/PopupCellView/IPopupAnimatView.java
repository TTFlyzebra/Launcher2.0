package com.ppfuns.ppfunstv.view.TvView.CellView.PopupCellView;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ppfuns.ppfunstv.data.CellEntity;
import com.ppfuns.ppfunstv.module.UpdataVersion.IDiskCache;

import java.util.List;

/**
 * 类别标识，PopupTvViewLayout依赖此标识。用以表明控件类型
 * Created by FlyZebra on 2016/8/29.
 */
public interface IPopupAnimatView {

    View getCenterImageView();

    List<TextView> getTextViewList();

    List<ImageView> getImageViewList();

    int getFinalX();

    int getFinalY();

    int getFinalW();

    int getFinalH();

    void playZoomAnimat(int mAnimDuration, boolean b);

    void setOnSizeZoom(OnSizeZoom onSizeZoom);

    void setOnSelectListener(OnSelectListener onSelectListener);

    boolean isLostFocus();

    void setLostFocus(boolean lostFocus);

    CellEntity getCellData();

    int getVerticalPadding();

    int getTextViewHight();

    Object getTag();

    void setDiskCache(IDiskCache iDiskCache);

    View setCellData(CellEntity cell);

    /**
     * 初始化六张弹出图片的位置
     * @return
     */
    IPopupAnimatView initChildBitmapPoint();
    /**
     * 控件尺寸改变事件触发监听
     */
    interface OnSizeZoom {
        void onSizeZoom(IPopupAnimatView view, boolean isZoomIn);
    }

    /**
     * 控件选中事件触发
     */
    interface OnSelectListener {
        void onSelectListener(IPopupAnimatView parentView, View view, boolean hasFocus, boolean parentLostFocus);
    }


    IPopupAnimatView setCenterImage(@NonNull String url);

    IPopupAnimatView setChildTextViews(@NonNull String[] str);

    IPopupAnimatView setChildTextViews(@NonNull List<CellEntity> list);

    IPopupAnimatView setChildImageViews(@NonNull String[] str);

    IPopupAnimatView setChildImageViews(@NonNull List<CellEntity> list);

    IPopupAnimatView setPopupBitmapUrls(@NonNull String[] urls);

}
