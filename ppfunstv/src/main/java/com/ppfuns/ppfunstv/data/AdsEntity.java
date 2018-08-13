package com.ppfuns.ppfunstv.data;

import android.graphics.Bitmap;

import com.ppfuns.ppfunstv.utils.FlyLog;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by lenovo on 2016/6/28.
 */
public class AdsEntity {
    public ArrayList<AdsBean> mAdsBeanArrayList;
    public ArrayList<AdsFile> mAdsFileArrayList;
    public ArrayList<String> mFileNames;


    public AdsEntity(ArrayList<AdsBean> adsBeanArrayList, ArrayList<AdsFile> adsFileArrayList,ArrayList<String> fileNames) {
        mAdsBeanArrayList = adsBeanArrayList;
        mAdsFileArrayList = adsFileArrayList;
        mFileNames = fileNames;
    }

    /**
     * 广告数据的封装
     */
    public static class AdsBean implements Serializable {
        public int adId;
        public String strIntent;
        public boolean focusable;
    }

    /**
     * 广告图片的封装
     */
    public static class AdsFile {
        public String cacheKey;
        public Bitmap adsImg;
    }

    /**
     * 根据id获取广告数据的封装
     *
     * @param adId 广告id
     * @return 对应id的广告数据的封装
     */
    public AdsBean getAdsBeanById(int adId) {
        if (mAdsBeanArrayList == null || mAdsBeanArrayList.isEmpty()) {
            FlyLog.d("<entity.AdsEntity>" + ":广告数据为空");
            return null;
        }

        for (AdsBean bean : mAdsBeanArrayList) {
            if (bean.adId == adId) {
                return bean;
            }
        }
        FlyLog.d("<entity.AdsEntity>" + ":广告中没有对应id的数据");
        return null;
    }

    /**
     * 根据广告id获取广告文件的封装
     *
     * @param adId 广告id
     * @return 对应id的广告文件的封装
     */
    public AdsFile getAdsFileById(int adId) {
        if (mAdsFileArrayList == null || mAdsFileArrayList.isEmpty()) {
            FlyLog.d("<entity.AdsEntity>" + ":广告图片为空");
            return null;
        }

        for (AdsBean bean : mAdsBeanArrayList) {
            if (bean.adId == adId) {
                return mAdsFileArrayList.get(mAdsBeanArrayList.indexOf(bean));
            }
        }
        FlyLog.d("<entity.AdsEntity>" + ":广告中没有对应id的图片");
        return null;
    }

    public void clear() {
        if (mAdsBeanArrayList != null) {
            mAdsBeanArrayList.clear();
        }

        if (mAdsFileArrayList != null) {
            for (AdsFile file : mAdsFileArrayList) {
                file.adsImg.recycle();
            }
            mAdsFileArrayList.clear();
        }
        if(mFileNames != null){
            mFileNames.clear();
        }
    }

    public boolean isAvaliable() {
        boolean result = true;

        if (mAdsBeanArrayList == null || mAdsBeanArrayList.isEmpty()) {
            result = false;
        }
        if (mAdsFileArrayList == null || mAdsFileArrayList.isEmpty()) {
            result = false;
        }

        return result;
    }
}

