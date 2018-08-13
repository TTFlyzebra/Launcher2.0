package com.ppfuns.ppfunstv.data;

import java.io.Serializable;

/**
 * Created by lenovo on 2016/6/13.
 */
public class MarqueeEntity implements Serializable{

    /**
     * msg : 下载成功
     * ret : 0
     * x:200
     * y:200
     * width : 500
     * height : 80
     * direction : 0
     * speed : 100
     * text : 欢迎使用.....欢迎你来了  ....
     * global:true
     */

    private String msg;
    private int ret;
    private int width;
    private int height;
    private int direction;
    private int speed;
    private String text;
    int x;
    int y;
    String font;
    String color;
    int size;
    private boolean global;



    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getMsg() {
        return msg;
    }

    public int getRet() {
        return ret;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getDirection() {
        return direction;
    }

    public int getSpeed() {
        return speed;
    }

    public String getText() {
        return text;
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

    public String getFont() {
        return font;
    }

    public void setFont(String font) {
        this.font = font;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public boolean isGlobal(){
        return global;
    }

    @Override
    public String toString() {
        return "MarqueeEntity{" +
                "color='" + color + '\'' +
                ", msg='" + msg + '\'' +
                ", ret=" + ret +
                ", width=" + width +
                ", height=" + height +
                ", direction=" + direction +
                ", speed=" + speed +
                ", text='" + text + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", font='" + font + '\'' +
                ", size=" + size +
                ", global=" + global +
                '}';
    }
}
