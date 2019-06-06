package com.flyzebra.flyui.view.customview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

@SuppressLint("AppCompatCustomView")
public class MarqueeTextView extends TextView {

    public MarqueeTextView(Context context) {
        this(context, null);
    }

    public MarqueeTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MarqueeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private boolean enableMarquee  = false;

    @Override
    public boolean isFocused() {
        return enableMarquee || super.isFocused();
    }


    public void enableMarquee(boolean enable) {
        setSelected(enable);
        enableMarquee = enable;
    }


}
