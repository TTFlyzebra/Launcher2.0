package com.ppfuns.ppfunstv.view.TvView.CellView.AdsModule;

import android.content.Context;
import android.graphics.Bitmap;

import java.util.List;
import java.util.Set;

/**
 * Created by lenovo on 2016/6/29.
 */
public interface IAdsModule {

    /**
     * 根据广告id获取广告数据
     *
     * @return 返回对应intent字符串
     */
    String getIntentInfo(int adsType);

    List<String> getFileNames(int adsType);

    /**
     * 根据广告id获取广告图片
     *
     * @return 返回广告图片的bitmap对象
     */
    Bitmap getAdsImg(int adsType);

    void loadAdsData(Context context, Set<Integer> adsTypes);

    /**
     * 刷新广告数据，接收到广告更新广播时调用
     *
     * @param context
     * @param type    广告类型
     * @return
     */
    void updateAdsData(Context context, int type);

    /**
     * 获取广告图片的颜色
     *
     * @return 返回此广告图片的颜色
     */
    int getAdsImgColor();

    /**
     * 获取广告是否可获焦
     *
     * @param type 广告类型
     * @return 返回次广告是否可获焦的布尔值
     */
    boolean getAdsFocusable(int type);

    /**
     * 注册广播
     */
    void registerReceiver(Context context);

    /**
     * 注销广播
     */
    void unRegisterReceiver(Context context);

    /**
     * 增加新广告到来的监听
     *
     * @param onAdsUpdateListener
     */
    void addOnAdsUpdateListener(BaseAdsUpdateListener onAdsUpdateListener);

    /**
     * 移除新广告到来的监听
     *
     * @param onAdsUpdateListener
     */
    void removeOnUpdateListener(BaseAdsUpdateListener onAdsUpdateListener);

    /**
     * 移除所有监听
     */
    void removeAllListenter();
}
