package com.flyzebra.ppfunstv.view.TvView.CellView.CellAnim;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;

/**
 * Created by 李宗源 on 2016/8/18.
 * E-mail:lizy@ppfuns.com
 */
public class ScaleAnim implements IBaseAnim{

    private ScaleAnimation scaleAnimation;
    private int duration= 1000;

    @Override
    public IBaseAnim create(Context context) {
        scaleAnimation = new ScaleAnimation(1f,1.3f,1f,1.3f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setInterpolator(new LinearInterpolator());
        scaleAnimation.setFillAfter(true);
        return this;
    }

    @Override
    public void palyAnim(View view, int duration) {
//        view.animate().scaleX()
//        this.duration = duration;
//        scaleAnimation.setDuration(duration);
//        view.setAnimation(scaleAnimation);
//        scaleAnimation.start();
    }
}
