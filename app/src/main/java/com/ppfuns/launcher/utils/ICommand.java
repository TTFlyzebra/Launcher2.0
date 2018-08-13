package com.ppfuns.launcher.utils;

/**
 * 动作处理模块
 * Created by lzy on 2016/6/21.
 */
public interface ICommand {

    /**
     * 点击事件
     */
    void clickAction();

    /**
     * 静默事件,当前view显示在页面时自动执行
     */
    void silenceAction();
}
