package com.ppfuns.ppfunstv.view.MinVideoPlayer;

import android.graphics.Bitmap;
import android.net.Uri;

/**
 *
 * Created by flyzebra on 17-2-16.
 */
public interface IMinVideoPlayer {

    /**
     * 播放视频
     */
    void play();

    /**
     * 暂停视频
     */
    void pause();

    /**
     * 加载并播放指定视频
     * @param uri
     */
    void playUri(Uri uri);

    /**
     * 停止播放，释放所有资源
     */
    void close();


    void setDefaultArtwork(Bitmap bitmap);

}
