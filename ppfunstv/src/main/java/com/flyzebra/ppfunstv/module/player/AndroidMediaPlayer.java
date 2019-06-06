package com.flyzebra.ppfunstv.module.player;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.SurfaceHolder;

import java.io.FileDescriptor;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Map;

/**
 * Created by zpf on 2017/3/21.
 */

public class AndroidMediaPlayer extends AbstractMediaPlayer {


    private MediaPlayer mMediaPlayer;
    private AndroidMediaPlayerListenerHolder mAndroidMediaPlayerListenerHolder;

    public AndroidMediaPlayer() {
        mMediaPlayer = new MediaPlayer();
        mAndroidMediaPlayerListenerHolder = new AndroidMediaPlayerListenerHolder(this);
        attachInternalListeners();
    }

    @Override
    public void seekTo(int msec) {
        mMediaPlayer.seekTo(msec);
    }

    @Override
    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }



    @Override
    public void pause() {
        mMediaPlayer.pause();
    }

    @Override
    public void start() {
        mMediaPlayer.start();
    }

    @Override
    public void stop() {
        mMediaPlayer.stop();
    }

    @Override
    public void reset() {
        mMediaPlayer.reset();
    }

    @Override
    public void release() {
        mMediaPlayer.release();
    }








    @Override
    public void setAudioStreamType(int streamtype) {
        mMediaPlayer.setAudioStreamType(streamtype);
    }

    @Override
    public void setScreenOnWhilePlaying(boolean screenOn) {
        mMediaPlayer.setScreenOnWhilePlaying(screenOn);
    }

    @Override
    public void setDisplay(SurfaceHolder sh) {
        mMediaPlayer.setDisplay(sh);
    }

    @Override
    public void setDataSource(Context context, Uri uri) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        mMediaPlayer.setDataSource(context, uri);
    }

    @Override
    public void setDataSource(Context context, Uri uri, Map<String, String> headers) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        mMediaPlayer.setDataSource(context, uri, headers);
    }

    @Override
    public void setDataSource(FileDescriptor fd) throws IOException, IllegalArgumentException, IllegalStateException {
        mMediaPlayer.setDataSource(fd);
    }

    @Override
    public void setDataSource(String path) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        mMediaPlayer.setDataSource(path);
    }

    @Override
    public void setAudioSessionId(int audioSessionId) {
        mMediaPlayer.setAudioSessionId(audioSessionId);
    }

    @Override
    public void prepareAsync() {
        mMediaPlayer.prepareAsync();
    }

    @Override
    public void stopPlayback() {

    }



    @Override
    public int getDuration() {
        return mMediaPlayer.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    @Override
    public boolean isInPlaybackState() {
        return false;
    }

    @Override
    public int getAudioSessionId() {
        return mMediaPlayer.getAudioSessionId();
    }

    @Override
    public int getVideoWidth() {
        return mMediaPlayer.getVideoWidth();
    }

    @Override
    public int getVideoHeight() {
        return mMediaPlayer.getVideoHeight();
    }

    @Override
    public void setVolume(float leftVolume, float rightVolume) {
        mMediaPlayer.setVolume(leftVolume,rightVolume);
    }

    private class AndroidMediaPlayerListenerHolder implements
            MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener,
            MediaPlayer.OnBufferingUpdateListener,
            MediaPlayer.OnSeekCompleteListener,
            MediaPlayer.OnVideoSizeChangedListener,
            MediaPlayer.OnErrorListener, MediaPlayer.OnInfoListener {
        public final WeakReference<AndroidMediaPlayer> mWeakMediaPlayer;

        public AndroidMediaPlayerListenerHolder(AndroidMediaPlayer mp) {
            mWeakMediaPlayer = new WeakReference<AndroidMediaPlayer>(mp);
        }

        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            AndroidMediaPlayer self = mWeakMediaPlayer.get();
            return self != null && notifyOnInfo(what, extra);

        }

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            AndroidMediaPlayer self = mWeakMediaPlayer.get();
            return self != null && notifyOnError(what, extra);

        }

        @Override
        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
            AndroidMediaPlayer self = mWeakMediaPlayer.get();
            if (self == null)
                return;

            notifyOnVideoSizeChanged(width, height, 1, 1);
        }

        @Override
        public void onSeekComplete(MediaPlayer mp) {
            AndroidMediaPlayer self = mWeakMediaPlayer.get();
            if (self == null)
                return;

            notifyOnSeekComplete();
        }

        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            AndroidMediaPlayer self = mWeakMediaPlayer.get();
            if (self == null)
                return;

            notifyOnBufferingUpdate(percent);
        }

        @Override
        public void onCompletion(MediaPlayer mp) {
            AndroidMediaPlayer self = mWeakMediaPlayer.get();
            if (self == null)
                return;

            notifyOnCompletion();
        }

        @Override
        public void onPrepared(MediaPlayer mp) {
            AndroidMediaPlayer self = mWeakMediaPlayer.get();
            if (self == null)
                return;

            notifyOnPrepared();
        }
    }


    /*--------------------
     * Listeners adapter
     */
    private void attachInternalListeners() {
        mMediaPlayer.setOnPreparedListener(mAndroidMediaPlayerListenerHolder);
        mMediaPlayer.setOnBufferingUpdateListener(mAndroidMediaPlayerListenerHolder);
        mMediaPlayer.setOnCompletionListener(mAndroidMediaPlayerListenerHolder);
        mMediaPlayer.setOnSeekCompleteListener(mAndroidMediaPlayerListenerHolder);
        mMediaPlayer.setOnVideoSizeChangedListener(mAndroidMediaPlayerListenerHolder);
        mMediaPlayer.setOnErrorListener(mAndroidMediaPlayerListenerHolder);
        mMediaPlayer.setOnInfoListener(mAndroidMediaPlayerListenerHolder);
    }
}
