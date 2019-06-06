package com.flyzebra.flyui.view.cellview;

public interface CellType {
    /**
     *通用控件
     */
    int TYPE_APP_NORMAL = 0;
    /**
     * 普通应用
     */
    int TYPE_IMAGE_TEXT = 1;
    /**
     * 收音机应用
     */
    int TYPE_NUM_TEXT = 2;
    /**
     * 时间
     */
    int TYPE_APP_DATE = 3;
    /**
     * 媒体小部件
     */
    int TYPE_APP_CONTROL = 4;
    /**
     * 可镜像的小部件
     */
    int TYPE_APP_MIRRORIMG = 5;
    /**
     * 导航条
     */
    int TYPE_APP_NAV = 6;
    /**
     * 可产生按键事件
     */
    int TYPE_SEEKBAR = 7;
    /**
     * 轮播控件
     */
    int TYPE_LOOPPLAY = 8;
    /**
     * 可根据条件变化的控件
     */
    int TYPE_LISTVIEW = 9;
    /**
     * 动画控件
     */
    int TYPE_ANIMTOR = 10;



    int TYPE_FRAGMENT= 11;



    int TYPE_PAGE = 12;



    int TYPE_FRAGMENT_NAV = 13 ;



    int TYPE_GROUP_LIST = 14 ;



    int TYPE_LRCVIEW = 15 ;



    int TYPE_IMAGE_RES = 99 ;
}
