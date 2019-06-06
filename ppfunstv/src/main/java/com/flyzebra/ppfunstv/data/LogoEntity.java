package com.flyzebra.ppfunstv.data;

import java.io.Serializable;

/**
 * Created by lenovo on 2016/6/13.
 */
public class LogoEntity implements Serializable{
    /**
     * imgUrl : http://xxx
     * x : 50
     * y : 50
     * width : 200
     * height : 100
     */

    private String imgUrl;
    private int x;
    private int y;
    private int width;
    private int height;

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public String toString() {
        return "LogoEntity{" +
                "imgUrl='" + imgUrl + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
