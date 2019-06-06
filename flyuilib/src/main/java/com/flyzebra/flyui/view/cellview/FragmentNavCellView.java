package com.flyzebra.flyui.view.cellview;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import com.flyzebra.flyui.bean.CellBean;
import com.flyzebra.flyui.event.FlyEvent;
import com.flyzebra.flyui.utils.ByteUtil;
import com.flyzebra.flyui.utils.FlyLog;
import com.flyzebra.flyui.view.base.BaseLayoutCellView;
import com.flyzebra.flyui.view.customview.FlyTabView;

/**
 * Author FlyZebra
 * 2019/4/16 15:01
 * Describ:
 **/
public class FragmentNavCellView extends BaseLayoutCellView {
    private FlyTabView flyTabView;

    public FragmentNavCellView(Context context) {
        super(context);
    }

    @Override
    public boolean verify(CellBean cellBean) {
        return mCellBean != null && mCellBean.texts != null && !mCellBean.texts.isEmpty();
    }

    @Override
    public void init(CellBean cellBean) {
        this.mCellBean = cellBean;
        flyTabView = new FlyTabView(getContext());
        flyTabView.setOrientation(mCellBean.width > mCellBean.height ? LinearLayout.HORIZONTAL : LinearLayout.VERTICAL);
        LayoutParams lp = new LayoutParams(mCellBean.width, mCellBean.height);
        addView(flyTabView, lp);
        flyTabView.setTitles(mCellBean.texts);
        flyTabView.setOnItemClickListener(new FlyTabView.OnItemClickListener() {
            @Override
            public void onItemClick(View v) {
                int n = (int) v.getTag();
                try {
                    FlyEvent.sendEvent(mCellBean.texts.get(n).send.eventId, ByteUtil.hexString2Bytes(mCellBean.texts.get(n).send.eventContent));
                } catch (Exception e) {
                    FlyLog.e(e.toString());
                }
            }
        });

        for (int i = 0; i < mCellBean.texts.size(); i++) {
            try {
                if (mCellBean.texts.get(i).recv != null && !TextUtils.isEmpty(mCellBean.texts.get(i).recv.recvId)) {
                    recvEvent(ByteUtil.hexString2Bytes(mCellBean.texts.get(i).recv.recvId));
                }
            } catch (Exception e) {
                FlyLog.e(e.toString());
            }
        }

        try {
            setBackgroundColor(Color.parseColor(mCellBean.backColor));
        } catch (Exception e) {
            FlyLog.d("error! parseColor exception!" + e.toString());
        }
    }

    @Override
    public boolean recvEvent(byte[] key) {
        if (mCellBean == null)
            return false;
        try {
            if (mCellBean.texts != null && !mCellBean.texts.isEmpty() && flyTabView != null) {
                String strKey = ByteUtil.bytes2HexString(key);
                for (int i = 0; i < mCellBean.texts.size(); i++) {
                    if (mCellBean.texts.get(i).recv != null && strKey.equals(mCellBean.texts.get(i).recv.recvId)) {
                        Object obj = FlyEvent.getValue(key);
                        if (obj instanceof String) {
                            flyTabView.setTextTitle(i, (String) obj);
                        }
                    }
                }
            }
        } catch (Exception e) {
            FlyLog.e(e.toString());
        }

        return false;
    }

}
