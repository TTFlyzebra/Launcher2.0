package com.flyzebra.ppfunstv.view.TvView.CellView.CellAnim;

import android.content.Context;
import android.view.View;

/**
 *
 * Created by FlyZebra on 2016/7/19.
 */
public interface IBaseAnim {
    IBaseAnim create(Context context);

    void palyAnim(View view, int duration);

}
