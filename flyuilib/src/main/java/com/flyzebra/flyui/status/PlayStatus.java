package com.flyzebra.flyui.status;

public interface PlayStatus {
    /**
     * 播放出错
     */
    int STATUS_ERROR = -1;
    /**
     * 未初始化
     */
    int STATUS_IDLE = 0;

    /**
     * 开始加载播放
     */
    int STATUS_STARTPLAY = 1;
    /**
     * 加载缓存准备播放
     */
    int STATUS_LOADING = 2;
    /**
     * 正在播放
     */
    int STATUS_PLAYING = 3;
    /**
     * 暂停播放
     */
    int STATUS_PAUSE = 4;
    /**
     * 播放完成
     */
    int STATUS_COMPLETED = 5;
}
