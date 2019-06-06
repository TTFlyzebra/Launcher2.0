package com.flyzebra.flyui.bean;

import java.util.List;

public class PageBean {
    public int pageId;
    public String imageurl;
    public int rows = 0;
    public int x = 0;
    public int y= 0;
    public List<CellBean> cellList;
    public int width;
    public int height;
    public String backColor;
    public SendBean send;
    public RecvBean recv;

    @Override
    public String toString() {
        return "PageBean{" +
                "imageurl='" + imageurl + '\'' +
                ", rows=" + rows +
                ", x=" + x +
                ", y=" + y +
                ", cellList=" + cellList +
                ", width=" + width +
                ", height=" + height +
                ", backColor='" + backColor + '\'' +
                ", send=" + send +
                ", recv=" + recv +
                '}';
    }
}
