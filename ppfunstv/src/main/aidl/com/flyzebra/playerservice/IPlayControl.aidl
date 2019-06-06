// PlayerControl.yinhe.hunan.aidl
package com.flyzebra.playerservice;

// Declare any non-default types here with import statements

interface IPlayControl {
    void setBound(int l,int t,int w,int h);
    void play(String url);
    void stop();
    void playLast(String playUrl);
}
