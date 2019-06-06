package com.flyzebra.ppfunstv.view.TvView.CellView.AdsModule;

/**
 * Created by lizongyuan on 2017/3/8.
 * E-mail:lizy@ppfuns.com
 */

public abstract class BaseAdsUpdateListener implements IAdsUpdateListener {

    public int type = -1;

    public BaseAdsUpdateListener(int type) {
        this.type = type;
    }

    @Override
    public abstract void onAdsUpdate();
}
