package com.ppfuns.ppfunstv.data;

import java.io.Serializable;

/**
 *
 * Created by lenovo on 2016/6/13.
 */
public class StateStyleEntity implements Serializable{
    /**
     * state : 1
     * size : 30
     * color : #ffff0000
     * alpah : 0-255
     */

    private int state;
    private int size;
    private String color;
    private int alpha;
    public String font;

    public void setState(int state) {
        this.state = state;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    public int getState() {
        return state;
    }

    public int getSize() {
        return size;
    }

    public String getColor() {
        return color;
    }

    public int getAlpha() {
        return alpha;
    }

    public String getFont() {
        return font;
    }

    public void setFont(String font) {
        this.font = font;
    }

    @Override
    public String toString() {
        return "StateStyleEntity{" +
                "alpha=" + alpha +
                ", state=" + state +
                ", size=" + size +
                ", color='" + color + '\'' +
                ", font='" + font + '\'' +
                '}';
    }
}
