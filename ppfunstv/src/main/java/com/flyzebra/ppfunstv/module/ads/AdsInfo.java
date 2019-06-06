package com.flyzebra.ppfunstv.module.ads;

/**
 * 作者:lenovo on 2016/6/22.
 * 邮箱:554524787@qq.com
 * AD广告实体类
 */
public class AdsInfo {
    /**
     * adId:广告ID
     * adtype:广告类型（  0开机背景广告、4开机动画广告、1PF图片广告、2频道导航广告、5回看收藏广告、6时移列表广告）
     * adfiletype:广告内容类型 (图片，视频)
     * adplaytype:投放方式
     * adfileurl:广告文件
     * adplaytime:播放次数
     * thirdurl:跳转地址
     * stopFlag:暂停标志
     * tvno_string:直播频道号格式[110,111,120,100,1110,1111,.....,2222]
     * priority:优先级
     */
    int adId;
    int adtype;
    int adfiletype;
    int adplaytype;
    int adplaytime;
    int stopFlag;
    int priority;
    String adfileurl;
    String thirdurl;
    String tvnoString;//所有频道字符串
    int adschedules;
    String focusable;

    public int getAdplaytime() {
        return adplaytime;
    }

    public void setAdplaytime(int adplaytime) {
        this.adplaytime = adplaytime;
    }


    public int getAdplaytype() {
        return adplaytype;
    }

    public void setAdplaytype(int adplaytype) {
        this.adplaytype = adplaytype;
    }

    public int getAdtype() {
        return adtype;
    }

    public void setAdtype(int adtype) {
        this.adtype = adtype;
    }

    public int getAdfiletype() {
        return adfiletype;
    }

    public void setAdfiletype(int adfiletype) {
        this.adfiletype = adfiletype;
    }

    public int getAdId() {
        return adId;
    }

    public void setAdId(int adId) {
        this.adId = adId;
    }

    public int getStopFlag() {
        return stopFlag;
    }

    public void setStopFlag(int stopFlag) {
        this.stopFlag = stopFlag;
    }

    public String getTvnoString() {
        return tvnoString;
    }

    public void setTvnoString(String tvnoString) {
        this.tvnoString = tvnoString;
    }

    public String isFocusable() {
        return focusable;
    }

    public void setFocusable(String focusable) {
        this.focusable = focusable;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getAdfileurl() {
        return adfileurl;
    }

    public void setAdfileurl(String adfileurl) {
        this.adfileurl = adfileurl;
    }

    public String getThirdurl() {
        return thirdurl;
    }

    public void setThirdurl(String thirdurl) {
        this.thirdurl = thirdurl;
    }


    public int getAdschedules() {
        return adschedules;
    }

    public void setAdschedules(int adschedules) {
        this.adschedules = adschedules;
    }
}
