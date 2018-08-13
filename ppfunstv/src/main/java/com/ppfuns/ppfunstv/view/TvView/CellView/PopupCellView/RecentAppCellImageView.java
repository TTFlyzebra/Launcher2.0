package com.ppfuns.ppfunstv.view.TvView.CellView.PopupCellView;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.ppfuns.ppfunstv.data.RecentTag;
import com.ppfuns.ppfunstv.module.EventMessage;
import com.ppfuns.ppfunstv.utils.AppUtil;
import com.ppfuns.ppfunstv.utils.FlyLog;
import com.ppfuns.ppfunstv.view.TvView.CellView.CellClickAction.CommondTool;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 *
 * Created by fagro on 17-7-5.
 */

public class RecentAppCellImageView extends ChildImageView {
    RecentTag mTag;

    public RecentAppCellImageView(Context context) {
        this(context, null);
    }

    public RecentAppCellImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecentAppCellImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        num = -1;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEvent(EventMessage msg) {
        if (EventMessage.MSG_UPDATE_RECENT_APP == msg.index) {
            if(num!=-1){
                update();
            }
        }
    }

    @Override
    public void setNum(int num) {
        super.setNum(num);
        update();
    }

    /**
     * 更新
     */
    public void update() {
        FlyLog.d("update recent app");
        RecentTag tag = AppUtil.getRecentApp(mContext, num, AppUtil.mFliter, false);
//        if (tag == null) {
//            String name = SPUtil.getRecentApp(mContext, num);
//            if (!TextUtils.isEmpty(name)) {
//                PackageInfo packageInfo;
//                try {
//                    packageInfo = mContext.getPackageManager().getPackageInfo(name, 0);
//                    tag = new RecentTag();
//                    tag.packageName = name;
//                    PackageManager pm = mContext.getPackageManager();
//                    tag.icon = packageInfo.applicationInfo.loadIcon(pm);
//                    tag.name = packageInfo.applicationInfo.loadLabel(pm).toString();
//                } catch (PackageManager.NameNotFoundException e) {
//                    FlyLog.e(e.toString());
//                    e.printStackTrace();
//                }
//            }
//        }

        if (tag != null) {
//            SPUtil.setRecentApp(mContext, num, tag.packageName);
            mTag = tag;
            //更新界面
            showImage();
        }

    }

    @Override
    public void showImage() {
        if (mTag != null) {
            setImageDrawable(mTag.icon);
        } else {
            super.showImage();
        }
    }

    @Override
    public void doAction() {
        if (mTag != null) {
            CommondTool.execStartPackage(mContext, mTag.packageName);
        } else {
            super.doAction();
        }
    }


}
