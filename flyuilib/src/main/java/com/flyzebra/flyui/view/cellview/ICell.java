package com.flyzebra.flyui.view.cellview;

import android.view.ViewGroup;

import com.flyzebra.flyui.bean.CellBean;


public interface ICell {

    /**
     * 设置数据
     * @param cellBean
     */
    void setCellBean(CellBean cellBean);

    /**
     * 校验CellBean数据是否完整
     * @param cellBean
     */
    boolean verify(CellBean cellBean);

    /**
     * 加载所需资源到内存
     * @param cellBean
     */
    void loadingRes(CellBean cellBean);

    /**
     * 创建View
     * @param cellBean
     */
    void init(CellBean cellBean);

    /**
     * 更新View
     */
    void refresh(CellBean cellBean);

    /**
     * 执行点击事件
     */
    void onClick();

    /**
     * 执行点击事件
     */
    void setSelectStyle(boolean isSelect);

    /**
     * 绑定设置镜像图片
     * @param viewGroup
     */
    void bindMirrorView(ViewGroup viewGroup,ViewGroup.LayoutParams lpMirror);
}
