package com.flyzebra.ppfunstv.data;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by lenovo on 2016/6/30.
 */
public class TemplateEntity implements Serializable{

    /**
     * templateId : 1
     * templateName :
     * isdefault;//是否是默认显示的主模板；true/false
     * resolution :
     * templateDetail :
     * backgroundImage :
     * backgroundColor :
     * x : 200
     * y : 200
     * tabList:
     */

    public int templateId;//模版Id
    public String templateName;//模版名称
    public String templateCode; //棋版编码
    public String isdefault;//是否是默认显示的主模板；true/false
    public String resolution;//模版分辨率
    public String templateDetail;//模板描述
    public String backgroundImage;//模版对应壁纸url
    public String backgroundColor;//模版对应背景颜色，格式:#FFFFFF
    public int x;//X坐标
    public int y;//Y坐标
    public ArrayList<TabEntity> tabList;
    public String type;
    public int defaultTabId;
    public int defaultCellId;

    public int getTemplateId() {
        return templateId;
    }

    public void setTemplateId(int templateId) {
        this.templateId = templateId;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }


    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getTemplateDetail() {
        return templateDetail;
    }

    public void setTemplateDetail(String templateDetail) {
        this.templateDetail = templateDetail;
    }

    public String getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public ArrayList<TabEntity> getTabList() {
        return tabList;
    }

    public void setTabList(ArrayList<TabEntity> tabList) {
        this.tabList = tabList;
    }

    public String getIsdefault() {
        return isdefault;
    }

    public void setIsdefault(String isdefault) {
        this.isdefault = isdefault;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getDefaultTabId() {
        return defaultTabId;
    }

    public void setDefaultTabId(int defaultTabId) {
        this.defaultTabId = defaultTabId;
    }

    public int getDefaultCellId() {
        return defaultCellId;
    }

    public void setDefaultCellId(int defaultCellId) {
        this.defaultCellId = defaultCellId;
    }


    @Override
    public String toString() {
        return "TemplateEntity{" +
                "templateId=" + templateId +
                ", templateName='" + templateName + '\'' +
                ", templateCode='" + templateCode + '\'' +
                ", isdefault='" + isdefault + '\'' +
                ", resolution='" + resolution + '\'' +
                ", templateDetail='" + templateDetail + '\'' +
                ", backgroundImage='" + backgroundImage + '\'' +
                ", backgroundColor='" + backgroundColor + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", tabList=" + tabList +
                ", type='" + type + '\'' +
                ", defaultTabId=" + defaultTabId +
                ", defaultCellId=" + defaultCellId +
                '}';
    }
}
