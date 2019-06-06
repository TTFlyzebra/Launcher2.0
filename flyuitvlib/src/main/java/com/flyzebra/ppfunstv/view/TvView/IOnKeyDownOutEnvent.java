package com.flyzebra.ppfunstv.view.TvView;

import android.view.View;

/**
 * 为统一规范，定义统一传出按键消息的接口。
 * 各自定义控件用以传出按键消息给父控件处理。
 * Created by FlyZebra on 2016/8/31.
 */
public interface IOnKeyDownOutEnvent {
    boolean onKeyDownGoLeft(View view);

    boolean onKeyDownGoRight(View view);

    boolean onKeyDownGoUp(View view);

    boolean onKeyDownGoDown(View view);
}
