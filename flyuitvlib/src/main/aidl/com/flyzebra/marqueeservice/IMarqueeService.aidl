// IMarqueeService.aidl
package com.flyzebra.marqueeservice;

// Declare any non-default types here with import statements

interface IMarqueeService {

    void setPoint(int x, int y, int width, int height);

    void setText(String text);

    void setTextColor(String textColor);

    void setTextSize(int textSize);

    void setDuration(long duration);

    void setDirection(int direction);

    void init();

    void play();

    void stop();

    void release();

    void bind();
}
