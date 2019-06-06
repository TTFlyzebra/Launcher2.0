package com.flyzebra.ppfunstv.data;

import java.io.Serializable;

/**
 * Created by lenovo on 2016/6/13.
 */
public class AnimationEntity implements Serializable{
    /**
     * state : 0
     * type : moveBy
     * delay : 0
     * duration : 100
     */

    private int state;
    private String type;
    private int delay;
    private int duration;

    public void setState(int state) {
        this.state = state;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getState() {
        return state;
    }

    public String getType() {
        return type;
    }

    public int getDelay() {
        return delay;
    }

    public int getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        return "AnimationEntity{" +
                "state=" + state +
                ", type='" + type + '\'' +
                ", delay=" + delay +
                ", duration=" + duration +
                '}';
    }
}
