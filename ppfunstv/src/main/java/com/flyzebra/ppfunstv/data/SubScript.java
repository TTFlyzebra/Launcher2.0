package com.flyzebra.ppfunstv.data;

/**
 * Created by lizongyuan on 2016/11/16.
 * E-mail:lizy@ppfuns.com
 * 角标
 */

public class SubScript {

    /**
     * 角标显示位置
     *      0：左上角
     *      1：右上角
     *      2：左下角
     *      3：右下角
     */
    public int pos;

    /**
     * 角标图片url
     */
    public String url;

    /**
     * 角标宽度
     */
    public int width;

    /**
     * 角标高度
     */
    public int height;

    public String name;

    @Override
    public String toString() {
        return "SubScript{" +
                "height=" + height +
                ", pos=" + pos +
                ", url='" + url + '\'' +
                ", width=" + width +
                '}';
    }
}
