package com.flyzebra.ppfunstv.view.TvView.CellView;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.flyzebra.ppfunstv.data.RecentTag;
import com.flyzebra.ppfunstv.module.EventMessage;
import com.flyzebra.ppfunstv.utils.AppUtil;
import com.flyzebra.ppfunstv.utils.FlyLog;
import com.flyzebra.ppfunstv.utils.SPUtil;
import com.flyzebra.ppfunstv.view.TvView.CellView.CellClickAction.CommondTool;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by lizongyuan on 2016/11/23.
 * E-mail:lizy@ppfuns.com
 */

public class RecentAppCellView extends SimpleCellView {

    private Context mContext;
    private int index = 0;
    RecentTag mTag;
    private boolean showText;

    public RecentAppCellView(Context context) {
        this(context, null);
    }

    public RecentAppCellView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecentAppCellView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
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

    //
    @Subscribe
    public void onEvent(EventMessage msg) {
        if (EventMessage.MSG_UPDATE_RECENT_APP == msg.index) {
            update();
        }
    }

    public void initView() {

//        synchronized (Constants.RECENT_APP_INDEX) {
//            index = Constants.RECENT_APP_INDEX;
//            Constants.RECENT_APP_INDEX++;
//        }
        update();
    }


    public void setIndex(int index) {
        this.index = index;
        update();
    }


    public void showText(boolean showText) {
        this.showText = showText;
    }


    /**
     * 更新
     */
    public void update() {
        FlyLog.d("update recent app,mCell:" + mCell);
        RecentTag tag = AppUtil.getRecentApp(mContext, index, null, true);
        if (tag == null) {
            String name = SPUtil.getRecentApp(mContext, index);
            if (!TextUtils.isEmpty(name)) {
                PackageInfo packageInfo;
                try {
                    packageInfo = mContext.getPackageManager().getPackageInfo(name, 0);
                    tag = new RecentTag();
                    tag.packageName = name;
                    PackageManager pm = mContext.getPackageManager();
                    tag.icon = packageInfo.applicationInfo.loadIcon(pm);
                    tag.name = packageInfo.applicationInfo.loadLabel(pm).toString();
                } catch (PackageManager.NameNotFoundException e) {
                    FlyLog.e(e.toString());
                    e.printStackTrace();
                }
            }
        }

        if (tag != null) {
            SPUtil.setRecentApp(mContext, index, tag.packageName);
            mTag = tag;
            //更新界面
            updateView();
        }

    }

    private void updateView() {
        if (mTag != null) {
            if (mSecodeText != null) {
                mSecodeText.setVisibility(GONE);
            }
            if (showText) {
                mTvInfo.setText(mTag.name);
                setTextEffect(mTvInfo, firstLineState);
            }


            getMyImageView().setImageDrawable(mTag.icon);
//            Glide.with(mContext)
//                    .load(mTag.icon)
//                    .diskCacheStrategy(DiskCacheStrategy.NONE)
//                    .into(getMyImageView());
//            Bitmap bitmap = BitmapUtils.toBitmap(mTag.icon);
//            EventBus.getDefault().post(new EventMessage(EventMessage.MSG_UPDATE_REFLECT, bitmap, mCell));
        } else {
            showImage(mCell.getImgUrl());
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

    @Override
    public void showImage(String imgUrl) {
        Glide.with(mContext).load(imgUrl)
//               .transform(new GlideRoundTransform(context, (int) (4 * screenScale)))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        update();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        update();
                        return false;
                    }
                })
                .into(getMyImageView());
    }


}
