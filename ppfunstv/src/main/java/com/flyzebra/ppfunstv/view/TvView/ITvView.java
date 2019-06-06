package com.flyzebra.ppfunstv.view.TvView;

import com.flyzebra.ppfunstv.data.CellBean;
import com.flyzebra.ppfunstv.data.LogoEntity;
import com.flyzebra.ppfunstv.data.MarqueeEntity;
import com.flyzebra.ppfunstv.data.TemplateEntity;
import com.flyzebra.ppfunstv.module.UpdataVersion.IDiskCache;

import java.util.List;

/**
 *
 * Created by flyzebra on 17-6-15.
 */
public interface ITvView {
    /**
     * 同步Activity生命周期触发此事件
     */
    void onStart();

    /**
     * 同步Activity生命周期触发此事件
     */
    void onResume();

    /**
     * 同步Activity生命周期触发此事件
     */
    void onPause();

    /**
     * 同步Activity生命周期触发此事件
     */
    void onStop();

    /**
     *
     */
    void createLogoView(LogoEntity logoEntity, IDiskCache iDiskCache);

    /**
     *
     */
    void createStatusbarView();

    /**
     *
     */
    void createPageView(TemplateEntity templateEntity, List<CellBean> list, IDiskCache iDiskCache);

    /**
     *
     */
    void createMaqueeView(MarqueeEntity marqueeEntity);

    /**
     * 传达消息给子控件，开始执行(播放视频，播放GiF)等操作
     */
    void startPlay();

    /**
     * 传达消息给子控件，开始执行(停止播放视频，停止播放GiF)等操作
     */
    void stopPlay();


}
