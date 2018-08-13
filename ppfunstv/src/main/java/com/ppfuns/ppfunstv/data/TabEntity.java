package com.ppfuns.ppfunstv.data;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * Created by Administrator on 2016/6/13.
 */
public class TabEntity implements Comparable,Serializable{
    private int id;
    private String name;
    private String color1;
    private String color2;
    private int marging;
    private int sort;
    private String bgUrl;
    private ArrayList<StateStyleEntity> stateStyle;
    private ArrayList<AnimationEntity> animation;
    private int left;
    private int right;
    private int up;
    private int down;
    private String font;

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getRight() {
        return right;
    }

    public void setRight(int right) {
        this.right = right;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getColor1() {
        return color1;
    }

    public void setColor1(String color1) {
        this.color1 = color1;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor2() {
        return color2;
    }

    public void setColor2(String color2) {
        this.color2 = color2;
    }

    public int getMarging() {
        return marging;
    }

    public void setMarging(int marging) {
        this.marging = marging;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getBgUrl() {
        return bgUrl;
    }

    public void setBgUrl(String bgUrl) {
        this.bgUrl = bgUrl;
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

    @Override
    public int compareTo(@NonNull Object another) {
        TabEntity tab = (TabEntity)another;

        return sort - tab.getSort();
    }

    public String getFont() {
        return font;
    }

    public void setFont(String font) {
        this.font = font;
    }
}
