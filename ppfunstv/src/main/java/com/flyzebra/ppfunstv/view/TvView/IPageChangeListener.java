package com.flyzebra.ppfunstv.view.TvView;

/**
 * 翻页事件
 * Created by lzy on 2016/6/21.
 */
public interface IPageChangeListener {
    /**
     * 进入当前page
     */
    void pageIn();

    /**
     * 离开当前page
     */
    void pageOut();

    /**
     * 页面滚动事件
     */
    void pageScroll();
}
