package com.flyzebra.ppfunstv.view.MinVideoPlayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.flyzebra.ppfunstv.R;
import com.flyzebra.ppfunstv.utils.FlyLog;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;
import java.util.List;


/**
 *
 * Created by flyzebra on 17-2-16.
 */
public class MinVideoExoPlayer extends LinearLayout implements IMinVideoPlayer {
    private Context mContext;
    private SimpleExoPlayerView simpleExoPlayerView;
    private SimpleExoPlayer player;
    private List<String> videoUrls = new ArrayList<>();
    private Uri uri;

    ExoPlayer.EventListener mEventListener = new ExoPlayer.EventListener() {
        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest) {
            FlyLog.d("exoplay listerer onTimelineChanged");
        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
//            FlyLog.d("exoplay listerer onTracksChanged");
        }

        @Override
        public void onLoadingChanged(boolean isLoading) {
//            FlyLog.d("exoplay listerer onLoadingChanged isLoading=" + isLoading);
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            FlyLog.d("exoplay listerer onPlayerStateChanged isplay =" + playWhenReady + ",state=%d", playbackState);
            if(playbackState==ExoPlayer.STATE_ENDED){
                setPlayUrl(uri,true);
            }

        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            FlyLog.d("exoplay listerer onPlayerError %s", error.toString());
        }


        @Override
        public void onPositionDiscontinuity() {
            FlyLog.d("exoplay listerer onPositionDiscontinuity");
        }
    };


    public MinVideoExoPlayer(Context context) {
        this(context, null);
    }

    public MinVideoExoPlayer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MinVideoExoPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        simpleExoPlayerView = (SimpleExoPlayerView) LayoutInflater.from(mContext).inflate(R.layout.tv_simpleexoplayerview, null);
        addView(simpleExoPlayerView);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
//        initializePlayer();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        releasePlayer();
    }


    public void setAutoHideController(boolean flag) {
        simpleExoPlayerView.setUseController(flag);
    }

    public long getVideoPosition() {
        return player==null?0:player.getCurrentPosition();
    }

    @Override
    public void play() {
        if(player!=null){
            player.setPlayWhenReady(true);
        }
    }

    @Override
    public void pause() {
        if(player!=null){
            player.setPlayWhenReady(false);
        }
    }

    /**
     * 加载并开始播放视频
     */
    @Override
    public void playUri(Uri uri) {
        this.uri = uri;
        initializePlayer();
        setPlayUrl(uri, true);
    }

    @Override
    public void close() {
        releasePlayer();
    }

    /**
     * 初始化播放控件
     */
    private void initializePlayer() {
        if (player == null) {
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveVideoTrackSelection.Factory(bandwidthMeter);
            TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
            //播放缓存设置
            LoadControl loadControl = new DefaultLoadControl();
            player = ExoPlayerFactory.newSimpleInstance(mContext, trackSelector, loadControl);
            player.addListener(mEventListener);
            simpleExoPlayerView.setPlayer(player);
        }
    }

    /**
     * 释放播放资源
     */
    private void releasePlayer() {
        if (player != null) {
            player.stop();
            player.removeListener(mEventListener);
            player.release();
            player = null;

        }
        if(simpleExoPlayerView !=null ){
            simpleExoPlayerView.setPlayer(null);
            simpleExoPlayerView = null;
        }
    }

    /**
     * 设置视频播放列表，无缝循环播放，（测试发现对某些视频存在不兼容的情况）
     *
     * @param urls
     * @param isPlay
     */
    public void setPlayUrls(@NonNull List<String> urls, boolean isPlay) {
        FlyLog.d("paly urls= " + urls.toString());
        if (urls.size() == 0) {
            return;
        }
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(mContext, Util.getUserAgent(mContext, "ppfuns-launcher"));
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        MediaSource firstvideo = new ExtractorMediaSource(Uri.parse(urls.get(0)), dataSourceFactory, extractorsFactory, null, null);
        //循环播放视频
        ConcatenatingMediaSource concatenatedSource = new ConcatenatingMediaSource(firstvideo);
        //增加视频列表
        for (int i = 1; i < urls.size(); i++) {
            Uri videouri = Uri.parse(urls.get(i));
            MediaSource mediaSource = new ExtractorMediaSource(videouri, dataSourceFactory, extractorsFactory, null, null);
            concatenatedSource = new ConcatenatingMediaSource(concatenatedSource, mediaSource);
        }
        LoopingMediaSource loopingSource = new LoopingMediaSource(concatenatedSource);
        player.prepare(loopingSource);
        player.setPlayWhenReady(isPlay);
    }

    /**
     * 设置播放视频
     * @param uri
     * @param isPlay
     */
    public void setPlayUrl(Uri uri, boolean isPlay) {
        FlyLog.d("paly urls= " + uri);
        if(uri==null){
            FlyLog.d("play uri is null, set play url failed!");
            return;
        }
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(mContext, Util.getUserAgent(mContext, "ppfuns-launcher"));
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        MediaSource firstvideo = new ExtractorMediaSource(uri, dataSourceFactory, extractorsFactory, null, null);
        //循环播放视频
        player.prepare(firstvideo);
        player.setPlayWhenReady(isPlay);
    }

    public int getState(){
        return player==null?ExoPlayer.STATE_IDLE:player.getPlaybackState();
    }

    @Override
    public void setDefaultArtwork(Bitmap bitmap) {
        if(simpleExoPlayerView!=null){
            simpleExoPlayerView.setDefaultArtwork(bitmap);
        }
    }

}
