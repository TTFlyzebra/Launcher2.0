package com.flyzebra.ppfunstv.view.TvView.CellView.CellAnim;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;

/**
 * Created by 李宗源 on 2016/8/15.
 * E-mail:lizy@ppfuns.com
 */
public class LoadAnim implements IBaseAnim{
    private ScaleAnimation mLoadScaleAnimation;
    private int duration = 4000;

    @Override
    public IBaseAnim create(Context context) {
        mLoadScaleAnimation = new ScaleAnimation(1f,1.3f,1f,1.3f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mLoadScaleAnimation.setInterpolator(new LinearInterpolator());
        mLoadScaleAnimation.setFillAfter(true);
        return this;
    }

    @Override
    public void palyAnim(View view, int duration) {
        this.duration = duration;
        mLoadScaleAnimation.setDuration(duration);
        view.setAnimation(mLoadScaleAnimation);
        mLoadScaleAnimation.start();
    }
}
