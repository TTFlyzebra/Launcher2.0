package com.flyzebra.ppfunstv.view.TvView.FocusAnimat;

import android.graphics.Rect;
import android.view.View;

/**
 *
 * Created by flyzebra on 17-8-24.
 */

public interface IAnimatView {

    void setFocusAnimate(ITvFocusAnimat iTvFocusAnimat);
    /**
     * 获取控件位置
     * @return
     */
    Rect getFocusRect();

    Rect getOldRect();

    int getFocusZorder();

    int getFocusScale();

    int getFocusType();

    View getReflectImageView();
}
