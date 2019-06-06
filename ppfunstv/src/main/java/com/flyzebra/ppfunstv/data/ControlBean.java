package com.flyzebra.ppfunstv.data;

import java.io.Serializable;

/**
 * Created by lenovo on 2016/6/13.
 */
public class ControlBean implements Serializable{

    /**
     * msg : 下载成功
     * ret : 0
     * vesion : 10001
     * url : http://aaa.bbb
     * areaId : 220251
     * logo : {"imgUrl":"http://xxx","x":50,"y":50,"width":200,"height":100}
     */

    private String msg;
    private int ret;
    private int vesion;
    private String url;
    private int areaId;
    private LogoEntity logo;
    private MarqueeEntity marquee;

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public void setVesion(int vesion) {
        this.vesion = vesion;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setAreaId(int areaId) {
        this.areaId = areaId;
    }

    public void setLogo(LogoEntity logo) {
        this.logo = logo;
    }

    public String getMsg() {
        return msg;
    }

    public int getRet() {
        return ret;
    }

    public int getVesion() {
        return vesion;
    }

    public String getUrl() {
        return url;
    }

    public int getAreaId() {
        return areaId;
    }

    public LogoEntity getLogo() {
        return logo;
    }

    public MarqueeEntity getMarqueeEntity() {
        return marquee;
    }

    public void setMarqueeEntity(MarqueeEntity marqueeEntity) {
        this.marquee = marqueeEntity;
    }

    public boolean isValid(){
        if(ret ==0 && msg !=null){
            return true;
        }
        return  false;
    }

    @Override
    public String toString() {
        return "ControlBean{" +
                "msg='" + msg + '\'' +
                ", ret=" + ret +
                ", vesion=" + vesion +
                ", url='" + url + '\'' +
                ", areaId=" + areaId +
                ", logo=" + logo +
                ", marqueeEntity=" + marquee +
                '}';
    }
}
