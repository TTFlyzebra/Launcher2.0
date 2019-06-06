package com.flyzebra.ppfunstv.view.TvView.FocusAnimat;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 *
 *用来区分特定的动画焦点框，实现焦点移动的特殊处理
 * Created by FlyZebra on 2016/8/4.
 */
public class FocusShadowView extends FrameLayout{
    public FocusShadowView(Context context) {
        super(context);
    }

    public FocusShadowView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FocusShadowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
