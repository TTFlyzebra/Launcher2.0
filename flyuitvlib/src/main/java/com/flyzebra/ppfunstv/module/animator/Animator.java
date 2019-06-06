package com.flyzebra.ppfunstv.module.animator;

import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.view.View;

/**
 * Created by flyzebra on 17-9-8.
 */

public class Animator {

    /**
     * 左右抖动动画
     *
     * @param view
     * @param px
     * @return
     */
    public static ObjectAnimator nopeLR(View view, int px) {
        int delta = 50;
        PropertyValuesHolder pvhTranslateX = PropertyValuesHolder.ofKeyframe(View.TRANSLATION_X,
                Keyframe.ofFloat(0f, 0),
                Keyframe.ofFloat(.25f, px),
                Keyframe.ofFloat(.50f, 0),
                Keyframe.ofFloat(.75f, px),
                Keyframe.ofFloat(1f, 0f)
        );

        return ObjectAnimator.ofPropertyValuesHolder(view, pvhTranslateX).
                setDuration(500);
    }

    /**
     * 上下抖动动画
     *
     * @param view
     * @param px
     * @return
     */
    public static ObjectAnimator nopeTB(View view, int px) {
        int delta = 50;
        PropertyValuesHolder pvhTranslateX = PropertyValuesHolder.ofKeyframe(View.TRANSLATION_Y,
                Keyframe.ofFloat(0f, 0),
                Keyframe.ofFloat(.25f, px),
                Keyframe.ofFloat(.50f, 0),
                Keyframe.ofFloat(.75f, px),
                Keyframe.ofFloat(1f, 0f)
        );

        return ObjectAnimator.ofPropertyValuesHolder(view, pvhTranslateX).
                setDuration(500);
    }
}
