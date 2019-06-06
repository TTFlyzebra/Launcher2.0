package com.flyzebra.flyui.view.cellview;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.flyzebra.flyui.bean.CellBean;
import com.flyzebra.flyui.bean.PageBean;
import com.flyzebra.flyui.bean.RecvBean;
import com.flyzebra.flyui.event.FlyEvent;
import com.flyzebra.flyui.utils.ByteUtil;
import com.flyzebra.flyui.utils.FlyLog;
import com.flyzebra.flyui.view.base.BaseLayoutCellView;
import com.flyzebra.flyui.view.pageview.SimplePageView;

/**
 * Author FlyZebra
 * 2019/4/16 14:41
 * Describ:
 **/
public class FragmentCellView extends BaseLayoutCellView {

    public FragmentCellView(Context context) {
        super(context);
    }

    @Override
    public boolean verify(CellBean cellBean) {
        return mCellBean != null && mCellBean.pages != null && !mCellBean.pages.isEmpty();
    }

    @Override
    public void init(CellBean cellBean) {
        try {
            if (mCellBean.recv != null && !TextUtils.isEmpty(mCellBean.recv.keyId)) {
                setId(Integer.valueOf(mCellBean.recv.keyId));
            }
        } catch (Exception e) {
            FlyLog.e(e.toString());
        }
        try {
            if (mCellBean.recv != null && !TextUtils.isEmpty(mCellBean.recv.recvId)) {
                recvEvent(ByteUtil.hexString2Bytes(mCellBean.recv.recvId));
            }
        } catch (Exception e) {
            FlyLog.e(e.toString());
        }
        try {
            if (!TextUtils.isEmpty(mCellBean.backColor)) {
                setBackgroundColor(Color.parseColor(mCellBean.backColor));
            }
        } catch (Exception e) {
            FlyLog.e("error! parseColor exception!" + e.toString());
        }
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public boolean recvEvent(byte[] key) {
        if (mCellBean == null) return false;
        RecvBean recvBean = mCellBean.recv;
        if (recvBean != null && !TextUtils.isEmpty(recvBean.recvId)) {
            if (recvBean.recvId.equals(ByteUtil.bytes2HexString(key))) {
                switch (recvBean.recvId) {
                    case "400302":
                        Object obj = FlyEvent.getValue(recvBean.recvId);
                        if (obj instanceof byte[]) {
                            int page = ((byte[]) obj)[0];
                            if (page < 0 || page > mCellBean.pages.size()) {
                                page = 0;
                            }
                            replaceFragment(page);
                        } else {
                            replaceFragment(0);
                        }
                        break;
                }
            }
        }
        return false;
    }


    private Handler mHandler = new Handler(Looper.getMainLooper());

    public void replaceFragment(int i) {
        if (i < 0 || i > mCellBean.pages.size()) return;
        final PageBean pageBean = mCellBean.pages.get(i);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                removeAllViews();
                SimplePageView view = new SimplePageView(getContext());
                view.setPageBean(pageBean);
                addView(view);
            }
        });
//        try {
//            FragmentTransaction ft = ((Activity) getContext()).getFragmentManager().beginTransaction();
//            Fragment fragment = CellFragment.newInstance(cellBean);
//            ft.replace(resID, fragment).commit();
//        } catch (Exception e) {
//            FlyLog.e(e.toString());
//        }
    }

}
