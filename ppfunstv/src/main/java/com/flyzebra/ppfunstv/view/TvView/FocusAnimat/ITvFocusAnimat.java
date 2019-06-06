package com.flyzebra.ppfunstv.view.TvView.FocusAnimat;

import android.graphics.Rect;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 焦点动画实现接口
 * Created by pc1 on 2016/6/15.
 */
public interface ITvFocusAnimat {
    /**
     * 实现TvPageLayout中两个子控件的动画切换效果
     * 参数会传入空值，空值表示不需要实现这个控件的动画，必须判断并完善处理传入空值的情况
     *
     * @param srcView 失去选中焦点的控件
     * @param objView 获得选中焦点的控件
     * @param
     */

    void startAnim(IAnimatView srcView, IAnimatView objView);

    void startAnim(IAnimatView objView,boolean isMoverCenter);

    void startAnim(IAnimatView srcView, IAnimatView objView, boolean isMoveCenter);

    void flyWhiteBorder(final Rect rect1, Rect rect2, boolean isMoveCenter);

    void flyWhiteBorder(IAnimatView objView, final Rect rect1, Rect rect2, boolean isMoveCenter);

    /**
     * 删除所有所建立的为实现动画效果显示在窗口上的控件
     */
    void removeAllSelect();


    void setShadowAmend(int px);

    int getShadowAmend();

    void setFocusResIDs(@DrawableRes int[] resIDs);

    void setListenerAnimateState(ListenerAnimtaState listenerAnimtaState);

    void setAnimduartion(int animDuration);

    @DrawableRes int getShadowResID(int type);

    interface ListenerAnimtaState {
        void finishAnimate();
    }


    @IntDef({TV_PAGE_MOVE_ANIM, TV_PAGE_NOT_MOVE_ANIM})
    @Retention(RetentionPolicy.SOURCE)
    @interface AnimStyle {
    }


    int TV_PAGE_MOVE_ANIM = 0x001;
    int TV_PAGE_NOT_MOVE_ANIM = 0x002;
}
