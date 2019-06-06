// PlayerControl.aidl
package com.flyzebra.playerservice;

import com.flyzebra.playerservice.IPlayStatusListener;

// Declare any non-default types here with import statements

interface IPlayerControl {
    /**
     * 设置播放资源地址
     */
    void setUrl(String url);
    /**
    * 开始播放
    */
    void play();
    /**
     * 暂停播放
     */
    void pause();
    /**
    *设置播放窗口的位置
    */
    void setLocation(int left,int top,int right,int bottom);

    void regPlayStatusistener(IPlayStatusListener listener);
    void unregPlayStatusListener(IPlayStatusListener listener);

    void start();//未实现任何功能，空方法
    void stop();//未实现任何功能，空方法

}
