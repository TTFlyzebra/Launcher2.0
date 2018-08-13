package com.ppfuns.ppfunstv.view.MarqueeView;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.ppfuns.ppfunstv.utils.FlyLog;


/**
 * 全局跑马灯，退出当前应用也可以显示
 * <p/>
 * Created by flyzebra on 17-3-29.
 */
public class GlobalMarqueeView extends BaseMarqueeView {
    public static GlobalMarqueeView INSTANCE = null;
    public static boolean isBind = false;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mWMLP;


    public GlobalMarqueeView(Context context) {
        this(context, null);
    }

    public GlobalMarqueeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GlobalMarqueeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public BaseMarqueeView setPoint(int x, int y, int width, int height) {
        return super.setPoint(x, y, width, height);
    }

    @Override
    public void initView() {
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(mWidth, mHeight);
        setLayoutParams(lp);
        mWindowManager = (WindowManager) (mContext.getApplicationContext()).getSystemService(Context.WINDOW_SERVICE);
        mWMLP = new WindowManager.LayoutParams();
        mWMLP.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
        mWMLP.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mWMLP.format = PixelFormat.TRANSPARENT;
        mWMLP.gravity = Gravity.TOP | Gravity.LEFT;
        mWMLP.windowAnimations = android.R.anim.fade_in;
        mWMLP.x = mLeft;
        mWMLP.y = mTop;
        mWMLP.width = mWidth;
        mWMLP.height = mHeight;
    }


    @Override
    public void init() {
        super.init();
    }

    @Override
    public void release() {
        try {
            if (INSTANCE != null) {
                cancelAnimator();
                if(mWindowManager != null) {
                    mWindowManager.removeView(INSTANCE);
                    mWindowManager =null;
                }
                isBind = false;
                INSTANCE = null;
                mContext = null;
            }
        } catch (Exception e) {
            FlyLog.d();
            e.printStackTrace();
        }

    }

    @Override
    public void bind(ViewGroup viewGroup) {
        if (!isBind) {
            if (mWindowManager != null) {
                mWindowManager.addView(this, mWMLP);
            }
            isBind = true;
        } else {
            if (mWindowManager != null) {
                mWindowManager.removeView(this);
                mWindowManager.addView(this, mWMLP);
            }
        }
    }


    public static GlobalMarqueeView getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (GlobalMarqueeView.class) {
                if (INSTANCE == null) {
                    INSTANCE = new GlobalMarqueeView(context);
                }
            }
        }else{
            INSTANCE.cancelAnimator();
            INSTANCE.removeAllViews();
        }
        return INSTANCE;
    }
}
