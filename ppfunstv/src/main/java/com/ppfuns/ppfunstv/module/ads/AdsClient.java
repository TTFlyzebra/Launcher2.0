package com.ppfuns.ppfunstv.module.ads;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * 作者:zhoubl on 2016/6/21.
 * 邮箱:554524787@qq.com
 * <p/>
 * AD广告服务工具类
 */
public class AdsClient {
    private static final Uri URI = Uri.parse("content://com.ppfuns.adservice.ADprovider");
    private static final Uri URITable = Uri.parse("content://com.ppfuns.adservice.ADprovider/ad_info_table");


    private AdsClient() {
    }

    /**
     * 通过广告类型，获取所有该类型的广告。
     *
     * @param adType 广告类型（  0开机背景广告、4开机动画广告、1PF图片广告、2频道导航广告、5回看收藏广告、6时移列表广告）
     */
    public static List<AdsInfo> getADinfo(Context mContext, int adType) throws ServiceNotExistException, DataEmptyException {
        List<AdsInfo> list = new ArrayList<>();
        ContentResolver resolver = mContext.getContentResolver();
        Cursor cursor = resolver.query(URITable, null, "adtype=?", new String[]{adType + ""}, null);
        if (cursor == null) {
            throw new ServiceNotExistException("服务不存在");
        }
        while (cursor.moveToNext()) {
            AdsInfo adInfo = new AdsInfo();
            int ad_id = cursor.getInt(cursor.getColumnIndex("ad_id"));
            int adtype = cursor.getInt(cursor.getColumnIndex("adtype"));
            int adfiletype = cursor.getInt(cursor.getColumnIndex("adfiletype"));
            int adplaytype = cursor.getInt(cursor.getColumnIndex("adplaytype"));
            int adplaytime = cursor.getInt(cursor.getColumnIndex("adplaytime"));
            int stop_flag = cursor.getInt(cursor.getColumnIndex("stop_flag"));
            int priority = cursor.getInt(cursor.getColumnIndex("priority"));
            String adfileurl = cursor.getString(cursor.getColumnIndex("adfileurl"));
            String thirdurl = cursor.getString(cursor.getColumnIndex("thirdurl"));
            String tvno_string = cursor.getString(cursor.getColumnIndex("tvno_string"));
            int adschedules = cursor.getInt(cursor.getColumnIndex("adschedules"));
            String focusable = cursor.getString(cursor.getColumnIndex("focusable"));

            adInfo.setAdId(ad_id);
            adInfo.setAdtype(adtype);
            adInfo.setAdfiletype(adfiletype);
            adInfo.setAdplaytype(adplaytype);
            adInfo.setAdplaytime(adplaytime);
            adInfo.setStopFlag(stop_flag);
            adInfo.setPriority(priority);
            adInfo.setAdfileurl(adfileurl);
            adInfo.setThirdurl(thirdurl);
            adInfo.setTvnoString(tvno_string);
            adInfo.setAdschedules(adschedules);
            adInfo.setFocusable(focusable);
            list.add(adInfo);
        }
        cursor.close();
        if (list.size() == 0) {
            throw new DataEmptyException("数据为空");
        }
        return list;
    }

    /**
     * 通过广告id获取广告资源
     *
     * @param
     * @param
     */
    public static InputStream getADfile(Context mContext, String adurl) throws IOException {

        String[] split = adurl.split("/");
        Uri uri = Uri.parse("content://com.ppfuns.adservice.ADprovider/" + split[split.length-1]);

        InputStream is = null;
        ContentResolver resolver = mContext.getContentResolver();
        is = resolver.openInputStream(uri);
        if (is == null) {
            throw new IOException("流为空");
        }
        return is;
    }

}
