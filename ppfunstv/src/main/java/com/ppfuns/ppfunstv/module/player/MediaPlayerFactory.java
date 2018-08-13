package com.ppfuns.ppfunstv.module.player;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by zpf on 2017/3/22.
 */

public class MediaPlayerFactory {



    public static final int ANDROID_MEDIAPLAYER = 0;

    @IntDef({ANDROID_MEDIAPLAYER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface MediaPlayerType {
    }


    private MediaPlayerFactory() {
    }

    public static MediaPlayerFactory getInstance() {
        return MediaPlayerFactoryHolder.sInstance;
    }

    private static class MediaPlayerFactoryHolder {
        public static final MediaPlayerFactory sInstance = new MediaPlayerFactory();
    }

    public IMediaPlayer getMediaPlayer(@MediaPlayerType int type) {
        IMediaPlayer mediaPlayer = null;
        switch (type) {
            case ANDROID_MEDIAPLAYER:
                mediaPlayer = new AndroidMediaPlayer();
                break;
            default:
                mediaPlayer = new AndroidMediaPlayer();
                break;
        }
        return mediaPlayer;

    }


}
