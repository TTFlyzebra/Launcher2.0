package com.flyzebra.ppfunstv.data;

/**
 *
 * Created by flyzebra on 17-8-7.
 */

public class FlyBean {
    int type;
    int width;
    int height;
    private int focusType;//区别焦点框图片类型
    private int focusZorder;//指定焦点框Z轴顺序 0为在最顶层,-1为不显示,1为在底层.
    private int focusScale;//指定焦点框是否放大 0为放大 1为不放大
    private int showImageNum;//竖版轮播和竖版列表控件显示几张图片
    private int showRows;//竖版控件显示几列

    private int maxTextLine;//台词鉴赏控件，文字全部最多显示多少行
    private int minTextLine;//刚开始显示多少行
    private String maskColor;//遮罩颜色
    private String focusImgUrlBg;//获得焦点之后背景图片链接
    private String font;

    public String getFont() {
        return font;
    }

    public void setFont(String font) {
        this.font = font;
    }

    public String getFocusImgUrlBg() {
        return focusImgUrlBg;
    }

    public void setFocusImgUrlBg(String focusImgUrlBg) {
        this.focusImgUrlBg = focusImgUrlBg;
    }

    public int getMaxLine() {
        return maxTextLine;
    }

    public void setMaxLine(int maxLine) {
        this.maxTextLine = maxLine;
    }

    public String getMaskColor() {
        return maskColor;
    }

    public void setMaskColor(String maskColor) {
        this.maskColor = maskColor;
    }

    public int getMinTextLine() {
        return minTextLine;
    }

    public void setMinTextLine(int minTextLine) {
        this.minTextLine = minTextLine;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getFocusType() {
        return focusType;
    }

    public void setFocusType(int focusType) {
        this.focusType = focusType;
    }

    public int getFocusZorder() {
        return focusZorder;
    }

    public void setFocusZorder(int focusZorder) {
        this.focusZorder = focusZorder;
    }

    public int getFocusScale() {
        return focusScale;
    }

    public void setFocusScale(int focusScale) {
        this.focusScale = focusScale;
    }

    public int getShowImageNum() {
        return showImageNum;
    }

    public void setShowImageNum(int showImageNum) {
        this.showImageNum = showImageNum;
    }

    public int getShowRows() {
        return showRows;
    }

    public void setShowRows(int showRows) {
        this.showRows = showRows;
    }

    @Override
    public String toString() {
        return "FlyBean{" +
                "type=" + type +
                ", width=" + width +
                ", height=" + height +
                ", focusType=" + focusType +
                ", focusZorder=" + focusZorder +
                ", focusScale=" + focusScale +
                ", showImageNum=" + showImageNum +
                ", showRows=" + showRows +
                ", maxTextLine=" + maxTextLine +
                ", minTextLine=" + minTextLine +
                ", maskColor='" + maskColor + '\'' +
                ", focusImgUrlBg='" + focusImgUrlBg + '\'' +
                ", font='" + font + '\'' +
                '}';
    }
}
