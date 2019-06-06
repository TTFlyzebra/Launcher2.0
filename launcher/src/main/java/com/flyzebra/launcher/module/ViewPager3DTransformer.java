package com.flyzebra.launcher.module;

import android.support.v4.view.ViewPager;
import android.view.View;

import com.flyzebra.ppfunstv.view.TvView.CellView.CellAnim.Rotate3dAnimation;


public class ViewPager3DTransformer implements ViewPager.PageTransformer {
    private static float MIN_SCALE = 0.85f;

    private static float MIN_ALPHA = 0.5f;
    private Rotate3dAnimation animation;

    public void transformPage(View view, float position) {
        if (position < -1) { // [-Infinity,-1)
            view.setAlpha(0);
            // This page is way off-screen to the left.
//            animation = new Rotate3dAnimation();
//            animation.init(view, 0, -60, 6);
//            animation.setDuration(1000);
//            animation.setFillAfter(true);
//            view.startAnimation(animation);
        } else if (position <= 1) { // [-1,1]
            // Modify the default slide transition to
            view.setAlpha(1);
//            animation = new Rotate3dAnimation();
//            animation.init(view, 0, 360, 4);
//            animation.setDuration(3000);
//            animation.setFillAfter(true);
//            animation.setRepeatCount(Animation.INFINITE);
//            view.startAnimation(animation);
        } else { // (1,+Infinity]
            view.setAlpha(0);
            // This page is way off-screen to the right.
//            animation = new Rotate3dAnimation();
//            animation.init(view, 0, 60, 6);
//            animation.setDuration(1000);
//            animation.setFillAfter(true);
//            view.startAnimation(animation);
        }
    }
}