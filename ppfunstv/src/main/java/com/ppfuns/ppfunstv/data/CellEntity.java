package com.ppfuns.ppfunstv.data;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * Created by FlyZebra on 2016/6/13.
 */
public class CellEntity implements Serializable, Cloneable {
    private int tabId;
    private int cellId;
    private int type;
    private int x;
    private int y;
    private int width;
    private int height;
    private String imgUrl;
    private String action;
    private boolean needAuth;
    private ArrayList<StateStyleEntity> stateStyle;
    private ArrayList<AnimationEntity> animation;
    private String font;
    private String text;
    private int textx;
    private int texty;
    private int textw;
    private int texth;
    private int size;
    private String color;
    private String adsId;
    private int sort;
    private List<SubScript> subScripts;
    private List<CellEntity> subCellList;
    private boolean canFocus = true;
    private int imageMarginTop;


    private int carouselPosition;//位置
    private int carouselTime;//轮播一次需要时间
    private int carouselType;//轮播方向
    private int focusType;//区别焦点框图片
    private int focusZorder;//指定焦点框Z轴顺序 0为在最顶层
    private int focusScale;//指定焦点框是否放大 0为放大 1为不放大
    private String imgUrlBg;//背景图片

    private int maxTextLine;//台词鉴赏控件，文字全部最多显示多少行
    private int minTextLine;//刚开始显示多少行
    private String textMaskColor;//遮罩颜色

    public String getTextMaskColor() {
        return textMaskColor;
    }

    public void setTextMaskColor(String textMaskColor) {
        this.textMaskColor = textMaskColor;
    }

    public int getMaxTextLine() {
        return maxTextLine;
    }

    public void setMaxTextLine(int maxTextLine) {
        this.maxTextLine = maxTextLine;
    }

    public int getMinTextLine() {
        return minTextLine;
    }

    public void setMinTextLine(int minTextLine) {
        this.minTextLine = minTextLine;
    }

    private int left;
    private int right;
    private int up;
    private int down;

    private Queue<Integer> lefts;
    private Queue<Integer> rights;
    private Queue<Integer> ups;
    private Queue<Integer> downs;

    private String extendData;

    private int showImageNum;
    private int showRows;

    public Integer getTabId() {
        return tabId;
    }

    public void setTabId(Integer tabId) {
        this.tabId = tabId;
    }

    public int getCellId() {
        return cellId;
    }

    public void setCellId(int cellId) {
        this.cellId = cellId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getIntent() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public ArrayList<StateStyleEntity> getStateStyle() {
        return stateStyle;
    }

    public void setStateStyle(ArrayList<StateStyleEntity> stateStyle) {
        this.stateStyle = stateStyle;
    }

    public ArrayList<AnimationEntity> getAnimation() {
        return animation;
    }

    public void setAnimation(ArrayList<AnimationEntity> animation) {
        this.animation = animation;
    }

    public int getRight() {
        return right;
    }

    public void setRight(int right) {
        this.right = right;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getUp() {
        return up;
    }

    public void setUp(int up) {
        this.up = up;
    }

    public int getDown() {
        return down;
    }

    public void setDown(int down) {
        this.down = down;
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

    public String getFont() {
        return font;
    }

    public void setFont(String font) {
        this.font = font;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getTextx() {
        return textx;
    }

    public void setTextx(int textx) {
        this.textx = textx;
    }

    public int getTexty() {
        return texty;
    }

    public void setTexty(int texty) {
        this.texty = texty;
    }

    public int getTextw() {
        return textw;
    }

    public void setTextw(int textw) {
        this.textw = textw;
    }

    public int getTexth() {
        return texth;
    }

    public void setTexth(int texth) {
        this.texth = texth;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getAdsId() {
        return adsId;
    }

    public void setAdsId(String adsId) {
        this.adsId = adsId;
    }

    public boolean getNeedAuth() {
        return needAuth;
    }

    public void setNeedAuth(boolean needAuth) {
        this.needAuth = needAuth;
    }

    public String getAction() {
        return action;
    }

    public boolean isNeedAuth() {
        return needAuth;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public List<SubScript> getSubScripts() {
        return subScripts;
    }

    public void setSubScripts(List<SubScript> subScripts) {
        this.subScripts = subScripts;
    }

    public boolean getCanFocus() {
        return canFocus;
    }

    public void setCanFocus(boolean canFocus) {
        this.canFocus = canFocus;
    }

    public List<CellEntity> getSubCellList() {
        return subCellList;
    }

    public void setSubCellList(List<CellEntity> subCellList) {
        this.subCellList = subCellList;
    }

    public Queue<Integer> getLefts() {
        return lefts;
    }

    public void setLefts(Queue<Integer> lefts) {
        this.lefts = lefts;
    }

    public Queue<Integer> getRights() {
        return rights;
    }

    public void setRights(Queue<Integer> rights) {
        this.rights = rights;
    }

    public Queue<Integer> getUps() {
        return ups;
    }

    public void setUps(Queue<Integer> ups) {
        this.ups = ups;
    }

    public Queue<Integer> getDowns() {
        return downs;
    }

    public void setDowns(Queue<Integer> downs) {
        this.downs = downs;
    }

    public int getImageMarginTop() {
        return imageMarginTop;
    }

    public void setImageMarginTop(int imageMarginTop) {
        this.imageMarginTop = imageMarginTop;
    }

    public int getCarouselPosition() {
        return carouselPosition;
    }

    public void setCarouselPosition(int carouselPosition) {
        this.carouselPosition = carouselPosition;
    }

    public int getCarouselTime() {
        return carouselTime;
    }

    public void setCarouselTime(int carouselTime) {
        //判断是否是毫秒，不是转成毫秒
        this.carouselTime = carouselTime > 1000 ? carouselTime : carouselTime * 1000;
    }

    public int getCarouselType() {
        return carouselType;
    }

    public void setCarouselType(int carouselType) {
        this.carouselType = carouselType;
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

    public String getExtendData() {
        return extendData;
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
        return "CellEntity{" +
                "tabId=" + tabId +
                ", cellId=" + cellId +
                ", type=" + type +
                ", x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                ", imgUrl='" + imgUrl + '\'' +
                ", action='" + action + '\'' +
                ", needAuth=" + needAuth +
                ", stateStyle=" + stateStyle +
                ", animation=" + animation +
                ", font='" + font + '\'' +
                ", text='" + text + '\'' +
                ", textx=" + textx +
                ", texty=" + texty +
                ", textw=" + textw +
                ", texth=" + texth +
                ", size=" + size +
                ", color='" + color + '\'' +
                ", adsId='" + adsId + '\'' +
                ", sort=" + sort +
                ", subScripts=" + subScripts +
                ", subCellList=" + subCellList +
                ", canFocus=" + canFocus +
                ", imageMarginTop=" + imageMarginTop +
                ", carouselPosition=" + carouselPosition +
                ", carouselTime=" + carouselTime +
                ", carouselType=" + carouselType +
                ", focusType=" + focusType +
                ", focusZorder=" + focusZorder +
                ", focusScale=" + focusScale +
                ", imgUrlBg='" + imgUrlBg + '\'' +
                ", maxTextLine=" + maxTextLine +
                ", minTextLine=" + minTextLine +
                ", textMaskColor='" + textMaskColor + '\'' +
                ", left=" + left +
                ", right=" + right +
                ", up=" + up +
                ", down=" + down +
                ", lefts=" + lefts +
                ", rights=" + rights +
                ", ups=" + ups +
                ", downs=" + downs +
                ", extendData='" + extendData + '\'' +
                ", showImageNum=" + showImageNum +
                ", showRows=" + showRows +
                '}';
    }

    public void setExtendData(String extendData) {
        this.extendData = extendData;
    }

    public String getImgUrlBg() {
        return imgUrlBg;
    }

    public void setImgUrlBg(String imgUrlBg) {
        this.imgUrlBg = imgUrlBg;
    }

    /**
     * 弹出小图片动画所需的参数
     * Created by FlyZebra on 2016/8/10.
     */
    static class PopupBitmapData implements Cloneable {
        private int x;
        private int y;
        private String imgUrl;
        //动画插值器
        private float animInsertor = 1.0f;

        public PopupBitmapData(int x, int y, String imgUrl, float animInsertor) {
            this.x = x;
            this.y = y;
            this.imgUrl = imgUrl;
            this.animInsertor = animInsertor;
        }

        public PopupBitmapData(PopupBitmapData childImgInfo) {
            this.x = childImgInfo.getX();
            this.y = childImgInfo.getY();
            this.imgUrl = childImgInfo.getImgUrl();
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

        public String getImgUrl() {
            return imgUrl;
        }

        public void setImgUrl(String imgUrl) {
            this.imgUrl = imgUrl;
        }

        public float getAnimInsertor() {
            return animInsertor;
        }

        public void setAnimInsertor(float animInsertor) {
            this.animInsertor = animInsertor;
        }

        @Override
        protected PopupBitmapData clone() throws CloneNotSupportedException {
            return new PopupBitmapData(this);
        }
    }

    @Override
    public boolean equals(@NonNull Object o) {
//        return TextUtils.equals(toString(),o.toString());
        return this == o;
    }

    @Override
    public CellEntity clone() {
        CellEntity cellEntity = null;
        try {
            cellEntity = (CellEntity) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return cellEntity;
    }
}
