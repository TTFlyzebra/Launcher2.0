package com.flyzebra.ppfunstv.view.TvView.HeaderView;

import android.view.KeyEvent;

/**
 * Created by miles on 2017/6/15 0015.
 */

public interface IHeaderImage{
    void setFocusImage(boolean isFocus);

    boolean onKeyDown(int keyCode, KeyEvent event);
}
