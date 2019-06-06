package com.flyzebra.flyui.view.pageanimtor;

import android.support.v4.view.ViewPager;
import android.view.View;


/**
 * Created by  Rex on 2016/9/28.
 * 给ViewPager加上3d旋转过度动画
 */
public class PageTransformerCube implements ViewPager.PageTransformer {
    @Override
    public void transformPage(View view, float position) {
//        FlyLog.d("%d:position="+position,view.getTag());
        float rotation = 45f * position;
        if (position >= 0) {
            view.setPivotX(0);
        } else {
            view.setPivotX(view.getWidth());
        }
        view.setPivotY(view.getHeight() * 0.5f);
        view.setRotationY(rotation);
        if (position > -1.0f && position < 1.0f) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setTranslationX(0);
            view.setRotation(0);
            view.setVisibility(View.INVISIBLE);
        }
    }
}