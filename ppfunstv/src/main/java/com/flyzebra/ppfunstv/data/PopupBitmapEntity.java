package com.flyzebra.ppfunstv.data;

/**
 *
 * Created by FlyZebra on 2016/8/30.
 */
public class PopupBitmapEntity{
    private int x;
    private int y;
    private String imgUrl;
    //动画插值器
    private float animInsertor = 1.0f;

    private float mDelayBefore = 0f;
    private float mDelayAfter = 0f;

    public PopupBitmapEntity(int x, int y, String imgUrl, float animInsertor) {
        this(x, y, imgUrl, animInsertor, 0, 0);
    }

    public PopupBitmapEntity(int pX, int pY, String pImgUrl, float pAnimInsertor, float pDelayBefore, float pDelayAfter) {
        x = pX;
        y = pY;
        imgUrl = pImgUrl;
        animInsertor = pAnimInsertor;
        mDelayBefore = pDelayBefore;
        mDelayAfter = pDelayAfter;
    }

    public PopupBitmapEntity(CellEntity cellEntity){
        this.x = cellEntity.getX();
        this.y = cellEntity.getY();
        this.imgUrl = cellEntity.getImgUrl();
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

    public float getDelayBefore() {
        return mDelayBefore;
    }

    public void setDelayBefore(float pDelayBefore) {
        mDelayBefore = pDelayBefore;
    }

    public float getDelayAfter() {
        return mDelayAfter;
    }

    public void setDelayAfter(float pDelayAfter) {
        mDelayAfter = pDelayAfter;
    }
}
