package com.ppfuns.ppfunstv.view.MarqueeView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RelativeLayout;



/**
 * 局部跑马灯，只在当前Activity有效
 *
 * Created by flyzebra on 17-3-28.
 */
public class LocalMarqueeView extends BaseMarqueeView {


    public LocalMarqueeView(Context context) {
        this(context,null);
    }

    public LocalMarqueeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LocalMarqueeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void initView() {
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(mWidth,mHeight);
        lp.leftMargin = mLeft;
        lp.topMargin = mTop;
        setLayoutParams(lp);
    }

    @Override
    public void release() {
        setVisibility(GONE);
    }

    @Override
    public void bind(ViewGroup viewGroup) {
        viewGroup.addView(this);
    }


}
