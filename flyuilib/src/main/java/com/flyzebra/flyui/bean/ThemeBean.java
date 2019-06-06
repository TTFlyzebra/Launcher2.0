package com.flyzebra.flyui.bean;

import android.text.TextUtils;

import java.util.List;

/**
 * Author FlyZebra
 * 2018/12/25 15:01
 * Describ:
 **/
public class ThemeBean {
    public static int filterColor = 0xFF0370E5;
    public static int normalColor = 0xFFFFFFFF;
    public String themeName;
    public String themeType;
    public String imageurl = "";
    public String exampleurl = "";
    public List<PageBean> pageList;
    public PageBean topPage;
    public int left = 0;
    public int top = 0;
    public int right = 0;
    public int bottom = 0;
    public int screenWidth = 1024;
    public int screenHeight = 600;
    public int animType = 0;
    public int isMirror = 0;

    @Override
    public String toString() {
        return "ThemeBean{" +
                "themeName='" + themeName + '\'' +
                ", themeType='" + themeType + '\'' +
                ", imageurl='" + imageurl + '\'' +
                ", pageList=" + pageList +
                ", topPage=" + topPage +
                ", left=" + left +
                ", top=" + top +
                ", right=" + right +
                ", bottom=" + bottom +
                ", screenWidth=" + screenWidth +
                ", screenHeight=" + screenHeight +
                ", animType=" + animType +
                ", isMirror=" + isMirror +
                '}';
    }

    public boolean isValid() {
        return pageList != null && !TextUtils.isEmpty(themeName);
    }
}
