package com.flyzebra.flyui.view.customview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

@SuppressLint("AppCompatCustomView")
public class FlyImageView extends ImageView{
    public FlyImageView(Context context) {
        super(context);
    }

    public FlyImageView(Context context,  AttributeSet attrs) {
        super(context, attrs);
    }

    public FlyImageView(Context context,  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
