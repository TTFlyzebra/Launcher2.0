package com.flyzebra.ppfunstv.view.TvView.CellView.CellAnim;

import android.content.Context;
import android.view.View;
import android.view.animation.DecelerateInterpolator;


/**
 *
 * Created by pc1 on 2016/7/19.
 */
public class Rotate3DAnim implements IBaseAnim{
    @Override
    public IBaseAnim create(Context context) {
        return null;
    }

    @Override
    public void palyAnim(View view,int duration) {
        Rotate3dAnimation animation = new Rotate3dAnimation();
        animation.init(view, 0, 360, 2);
        animation.setDuration(duration);
        animation.setFillAfter(true);
        animation.setInterpolator(new DecelerateInterpolator());
        view.startAnimation(animation);
    }
}
