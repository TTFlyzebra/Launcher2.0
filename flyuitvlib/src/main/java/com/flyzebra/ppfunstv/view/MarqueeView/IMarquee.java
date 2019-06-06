package com.flyzebra.ppfunstv.view.MarqueeView;


import android.view.ViewGroup;

/**
 * 调用方法
 * IMarquee.setPoint(x,y,width,height);
 * IMarquee.setText(text)
 * .setTextSize(size)
 * .setTextColor(color
 * .setDirection(direction)
 * .setDuration(duration)
 * .init();
 * Created by flyzebra on 17-3-29.
 */
public interface IMarquee {
    IMarquee setPoint(int x, int y, int width, int height);

    IMarquee setText(String text);

    IMarquee setTextColor(String textColor);

    IMarquee setTextColor(int textColor);

    IMarquee setTextSize(int textSize);

    IMarquee setDirection(int direction);

    IMarquee setDuration(long duration);

    void init();

    void play();

    void stop();

    void release();

    void bind(ViewGroup viewGroup);
}
