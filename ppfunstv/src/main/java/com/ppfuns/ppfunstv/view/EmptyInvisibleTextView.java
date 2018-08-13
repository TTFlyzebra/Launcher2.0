package com.ppfuns.ppfunstv.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 字符串为空将不显示的TextView
 * Created by flyzebra on 17-8-8.
 */

public class EmptyInvisibleTextView extends TextView{
    public EmptyInvisibleTextView(Context context) {
        super(context);
    }

    public EmptyInvisibleTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public EmptyInvisibleTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if(TextUtils.isEmpty(text)){
            setVisibility(GONE);
        }else{
            setVisibility(VISIBLE);
        }
        super.setText(text, type);
    }


}
