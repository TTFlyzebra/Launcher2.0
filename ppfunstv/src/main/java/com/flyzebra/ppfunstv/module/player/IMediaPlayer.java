package com.flyzebra.ppfunstv.module.player;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.view.SurfaceHolder;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Map;

/**
 * Created by zpf on 2017/3/21.
 */

public interface IMediaPlayer {



    /* Do not change these values without updating their counterparts
     * in include/media/mediaplayer.h!
     */
    /** Unspecified media player info.
     * @see android.media.MediaPlayer.OnInfoListener
     */
     int MEDIA_INFO_UNKNOWN = 1;

    /** The player was started because it was used as the next player for another
     * player, which just completed playback.
     * @see android.media.MediaPlayer.OnInfoListener
     * @hide
     */
    int MEDIA_INFO_STARTED_AS_NEXT = 2;

    /** The player just pushed the very first video frame for rendering.
     * @see android.media.MediaPlayer.OnInfoListener
     */
    int MEDIA_INFO_VIDEO_RENDERING_START = 3;

    /** The video is too complex for the decoder: it can't decode frames fast
     *  enough. Possibly only the audio plays fine at this stage.
     * @see android.media.MediaPlayer.OnInfoListener
     */
    int MEDIA_INFO_VIDEO_TRACK_LAGGING = 700;

    /** MediaPlayer is temporarily pausing playback internally in order to
     * buffer more data.
     * @see android.media.MediaPlayer.OnInfoListener
     */
     int MEDIA_INFO_BUFFERING_START = 701;

    /** MediaPlayer is resuming playback after filling buffers.
     * @see android.media.MediaPlayer.OnInfoListener
     */
     int MEDIA_INFO_BUFFERING_END = 702;

    /** Estimated network bandwidth information (kbps) is available; currently this event fires
     * simultaneously as {@link #MEDIA_INFO_BUFFERING_START} and {@link #MEDIA_INFO_BUFFERING_END}
     * when playing network files.
     * @see android.media.MediaPlayer.OnInfoListener
     * @hide
     */
    int MEDIA_INFO_NETWORK_BANDWIDTH = 703;

    /** Bad interleaving means that a media has been improperly interleaved or
     * not interleaved at all, e.g has all the video samples first then all the
     * audio ones. Video is playing but a lot of disk seeks may be happening.
     * @see android.media.MediaPlayer.OnInfoListener
     */
     int MEDIA_INFO_BAD_INTERLEAVING = 800;

    /** The media cannot be seeked (e.g live stream)
     * @see android.media.MediaPlayer.OnInfoListener
     */
     int MEDIA_INFO_NOT_SEEKABLE = 801;

    /** A new set of metadata is available.
     * @see android.media.MediaPlayer.OnInfoListener
     */
     int MEDIA_INFO_METADATA_UPDATE = 802;

    /** A new set of external-only metadata is available.  Used by
     *  JAVA framework to avoid triggering track scanning.
     * @hide
     */
     int MEDIA_INFO_EXTERNAL_METADATA_UPDATE = 803;

    /** Failed to handle timed text track properly.
     * @see android.media.MediaPlayer.OnInfoListener
     *
     * {@hide}
     */
     int MEDIA_INFO_TIMED_TEXT_ERROR = 900;

    /** Subtitle track was not supported by the media framework.
     * @see android.media.MediaPlayer.OnInfoListener
     */
    int MEDIA_INFO_UNSUPPORTED_SUBTITLE = 901;

    /** Reading the subtitle track takes too long.
     * @see android.media.MediaPlayer.OnInfoListener
     */
     int MEDIA_INFO_SUBTITLE_TIMED_OUT = 902;




    /* Do not change these values without updating their counterparts
     * in include/media/mediaplayer.h!
     */
    /**
     * Unspecified media player error.
     *
     * @see android.media.MediaPlayer.OnErrorListener
     */
    int MEDIA_ERROR_UNKNOWN = 1;

    /**
     * Media server died. In this case, the application must release the
     * MediaPlayer object and instantiate a new one.
     *
     * @see android.media.MediaPlayer.OnErrorListener
     */
    int MEDIA_ERROR_SERVER_DIED = 100;

    /**
     * The video is streamed and its container is not valid for progressive
     * playback i.e the video's index (e.g moov atom) is not at the start of the
     * file.
     *
     * @see android.media.MediaPlayer.OnErrorListener
     */
    int MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK = 200;

    /**
     * File or network related operation errors.
     */
    int MEDIA_ERROR_IO = -1004;
    /**
     * Bitstream is not conforming to the related coding standard or file spec.
     */
    int MEDIA_ERROR_MALFORMED = -1007;
    /**
     * Bitstream is conforming to the related coding standard or file spec, but
     * the media framework does not support the feature.
     */
    int MEDIA_ERROR_UNSUPPORTED = -1010;
    /**
     * Some operation takes too long to complete, usually more than 3-5 seconds.
     */
    int MEDIA_ERROR_TIMED_OUT = -110;

    /**
     * Unspecified low-level system error. This value originated from UNKNOWN_ERROR in
     * system/core/include/utils/Errors.h
     *
     * @hide
     * @see android.media.MediaPlayer.OnErrorListener
     */
    int MEDIA_ERROR_SYSTEM = -2147483648;


    void seekTo(int msec);

    boolean isPlaying();


    void pause();

    void start();


    void stop();

    void reset();

    void release();

    void setAudioStreamType(int streamtype);

    void setScreenOnWhilePlaying(boolean screenOn);


    void setDisplay(SurfaceHolder sh);

    void setDataSource(Context context, Uri uri)
            throws IOException, IllegalArgumentException, SecurityException, IllegalStateException;

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    void setDataSource(Context context, Uri uri, Map<String, String> headers)
            throws IOException, IllegalArgumentException, SecurityException, IllegalStateException;

    void setDataSource(FileDescriptor fd)
            throws IOException, IllegalArgumentException, IllegalStateException;

    void setDataSource(String path)
            throws IOException, IllegalArgumentException, SecurityException, IllegalStateException;

    void setAudioSessionId(int audioSessionId);

    void prepareAsync();

    void stopPlayback();


    int getDuration();

    int getCurrentPosition();

    boolean isInPlaybackState();

    int getAudioSessionId();

    int getVideoWidth();

    int getVideoHeight();

    /*--------------------
     * Listeners
     */
    interface OnPreparedListener {
        void onPrepared(IMediaPlayer mp);
    }

    interface OnCompletionListener {
        void onCompletion(IMediaPlayer mp);
    }

    interface OnBufferingUpdateListener {
        void onBufferingUpdate(IMediaPlayer mp, int percent);
    }

    interface OnSeekCompleteListener {
        void onSeekComplete(IMediaPlayer mp);
    }

    interface OnVideoSizeChangedListener {
        void onVideoSizeChanged(IMediaPlayer mp, int width, int height,
                                int sar_num, int sar_den);
    }

    interface OnErrorListener {
        boolean onError(IMediaPlayer mp, int what, int extra);
    }

    interface OnInfoListener {
        boolean onInfo(IMediaPlayer mp, int what, int extra);
    }


    void setOnPreparedListener(OnPreparedListener listener);

    void setOnCompletionListener(OnCompletionListener listener);

    void setOnBufferingUpdateListener(
            OnBufferingUpdateListener listener);

    void setOnSeekCompleteListener(OnSeekCompleteListener listener);

    void setOnVideoSizeChangedListener(
            OnVideoSizeChangedListener listener);


    void setOnErrorListener(OnErrorListener listener);

    void setOnInfoListener(OnInfoListener listener);

    void setVolume(float leftVolume, float rightVolume);


}
