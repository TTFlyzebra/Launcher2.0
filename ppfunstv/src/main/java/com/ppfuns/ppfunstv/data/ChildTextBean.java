package com.ppfuns.ppfunstv.data;

/**
 *
 * Created by FlyZebra on 2016/8/26.
 */
public class ChildTextBean {
    private String text;
    private int width;
    private int height;
    private int focusColor;
    private int unfocusColor;
    private int textSize;
    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getFocusColor() {
        return focusColor;
    }

    public void setFocusColor(int focusColor) {
        this.focusColor = focusColor;
    }

    public int getUnfocusColor() {
        return unfocusColor;
    }

    public void setUnfocusColor(int unfocusColor) {
        this.unfocusColor = unfocusColor;
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }
}
