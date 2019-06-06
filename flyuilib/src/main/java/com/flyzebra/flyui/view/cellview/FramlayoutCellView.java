package com.flyzebra.flyui.view.cellview;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.MotionEvent;

import com.flyzebra.flyui.bean.CellBean;
import com.flyzebra.flyui.bean.RecvBean;
import com.flyzebra.flyui.utils.ByteUtil;
import com.flyzebra.flyui.utils.FlyLog;
import com.flyzebra.flyui.utils.RtlUtil;
import com.flyzebra.flyui.view.base.BaseLayoutCellView;
import com.flyzebra.flyui.view.pageview.SimplePageView;

/**
 * Author FlyZebra
 * 2019/4/2 16:15
 * Describ:
 **/
public class FramlayoutCellView extends BaseLayoutCellView {
    private boolean show = true;
    private Handler sHander = new Handler(Looper.getMainLooper());
    private Runnable hideMenuTask = new Runnable() {
        @Override
        public void run() {
            FlyLog.d("hide Menu Task run");
            if (show) {
                goAnimtor(false, 300);
            }
        }
    };


    public FramlayoutCellView(Context context) {
        super(context);
    }

    @Override
    public boolean verify(CellBean cellBean) {
        return mCellBean != null && mCellBean.pages != null && !mCellBean.pages.isEmpty();
    }

    @Override
    public void init(CellBean cellBean) {
        SimplePageView simplePageView = new SimplePageView(getContext());
        addView(simplePageView);
        simplePageView.setPageBean(mCellBean.pages.get(0));

        try {
            if (!TextUtils.isEmpty(mCellBean.backColor)) {
                setBackgroundColor(Color.parseColor(mCellBean.backColor));
            }
        } catch (Exception e) {
            FlyLog.d("error! parseColor exception!" + e.toString());
        }

        try {
            if (mCellBean.recv.recvId.equals("400201")) {
                goAnimtor(false, 0);
            }
        } catch (Exception e) {
            FlyLog.e(e.toString());
        }
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (show && mCellBean.recv.recvId.equals("400201")) {
            sHander.removeCallbacks(hideMenuTask);
            sHander.postDelayed(hideMenuTask, 5000);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onDetachedFromWindow() {
        sHander.removeCallbacksAndMessages(null);
        super.onDetachedFromWindow();
    }

    @Override
    public boolean recvEvent(byte[] key) {
        if (mCellBean == null) return false;
        RecvBean recvBean = mCellBean.recv;
        if (recvBean != null && !TextUtils.isEmpty(recvBean.recvId)) {
            if (recvBean.recvId.equals(ByteUtil.bytes2HexString(key))) {
                switch (recvBean.recvId) {
                    case "400201":
                        goAnimtor(!show, 300);
                        break;
                }
            }
        }
        return false;
    }


    private void goAnimtor(final boolean isShow, long during) {
        show = isShow;
        FlyLog.d("isShow=" + isShow);
        if (isShow) {
            sHander.removeCallbacks(hideMenuTask);
            sHander.postDelayed(hideMenuTask, 5000);
        } else {
            sHander.removeCallbacks(hideMenuTask);
        }
        boolean isRtl = RtlUtil.isRtl();
        int adjust = 0;
        if (mCellBean.texts != null && mCellBean.texts.size() > 0) {
            adjust = mCellBean.texts.get(0).left;
        }
        animate().translationX(isShow ? 0 : (isRtl ? -(mCellBean.width + adjust) : (mCellBean.width + adjust))
        ).setDuration(during).start();
    }
}
