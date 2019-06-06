package com.flyzebra.ppfunstv.view.TvView.CellView;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;

import com.flyzebra.ppfunstv.utils.FlyLog;
import com.flyzebra.ppfunstv.view.TvView.CellView.AdsModule.AdsModule;
import com.flyzebra.ppfunstv.view.TvView.CellView.AdsModule.BaseAdsUpdateListener;
import com.flyzebra.ppfunstv.view.TvView.CellView.AdsModule.IAdsModule;
import com.flyzebra.ppfunstv.view.TvView.CellView.CellClickAction.ActionFactory;


/**
 *图片广告控件
 * Created by 李冰锋 on 2016/6/27.
 */
public class AdsImageCellView extends SimpleCellView {
    private final IAdsModule mAdsModule;

    private BaseAdsUpdateListener mOnAdsUpdateListener;

    public AdsImageCellView(Context context) {
        this(context, null);
    }

    public AdsImageCellView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AdsImageCellView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mAdsModule = AdsModule.getInstance();
    }


    /**
     * 确认后执行的行为
     */
    @Override
    public void doAction() {
        try {
            String intentInfo = mAdsModule.getIntentInfo(Integer.parseInt(getCellData().getAdsId()));
            if (mAdsModule.getAdsImg(Integer.parseInt(getCellData().getAdsId())) == null) {
                clickEvent = ActionFactory.create(mContext, mCell);
            } else {
                clickEvent = ActionFactory.create(mContext, intentInfo, mCell.getNeedAuth(), mCell.getType());
            }
            if (clickEvent != null) {
                clickEvent.doAction();
            }
        } catch (Exception e) {
            e.printStackTrace();
            FlyLog.e(e.toString());
        }
    }

    @Override
    public void showImage(String imgUrl) {
        try {
            Bitmap adImg = mAdsModule.getAdsImg(Integer.parseInt(getCellData().getAdsId()));
            FlyLog.d("<view.AdsCellView>" + "adImg:" + adImg + " id:" + getCellData().getAdsId() + " " + mCell);
            if (adImg != null) {
                getMyImageView().setImageBitmap(adImg);
                showReflectView(adImg);
            } else {
               super.showImage(imgUrl);
            }
        } catch (Exception e) {
            e.printStackTrace();
            FlyLog.d(e.toString());
        }
    }

    /**
     * 注册广播
     */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        int adstype = -1;
        try {
            adstype = Integer.parseInt(getCellData().getAdsId());
        } catch (Exception e) {
            e.printStackTrace();
            FlyLog.e(e.toString());
        }
        mOnAdsUpdateListener = new BaseAdsUpdateListener(adstype) {
            @Override
            public void onAdsUpdate() {
                FlyLog.d("receiver ads update, adsId :" + type);
                showImage(mCell.getImgUrl());
            }
        };
        mAdsModule.addOnAdsUpdateListener(mOnAdsUpdateListener);
    }

    /**
     * 注销广播
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mAdsModule.removeOnUpdateListener(mOnAdsUpdateListener);
    }
}
