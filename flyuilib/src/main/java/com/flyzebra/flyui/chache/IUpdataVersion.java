package com.flyzebra.flyui.chache;


import com.flyzebra.flyui.bean.ThemeBean;

/**
 *
 * Created by FlyZebra on 2016/6/21.
 */
public interface IUpdataVersion {

    /**
     * 每次请求前调用此函数设置请求API地址
     */
    void initApi(String apiUrl, String apiTheme, String type, String themeName, String version, String token);


    /**
     * 设置使用的碰盘缓存
     *
     * @return
     */
    IUpdataVersion setDiskCache(IDiskCache iDiskCache);

    /**
     * 启动更线线程
     *
     * @param upResult 更新结果的回调通知
     */
    void startUpVersion(UpResult upResult);

    /**
     * 强制更新，不用检测版本
     */
    void forceUpVersion(UpResult upResult);

    /**
     * 返回更新状态
     *
     * @return true:正在更新
     * false:不是处于更新状态
     */
    boolean isUPVeriosnRunning();


    /**
     * 检测本地是否有存在的已更新的版本数据
     *
     * @return
     */
    void getCacheData(CheckCacheResult cacheResult);

    /**
     * 取消所有网络请求线程
     */
    void cancelAllTasks();


    interface UpResult {
        /**
         * 更新成功
         */
        void upVersionOK(ThemeBean themeBean);

        /**
         * 更新进度
         *
         * @param msg
         * @param sum
         * @param progress
         */
        void upVesionProgress(String msg, int sum, int progress);

        /**
         * 更新失几信息
         *
         * @param error
         */
        void upVersionFaile(String error);
    }

    /**
     * 检测缓存
     */
    interface CheckCacheResult {
        /**
         * 缓存数据已成功读出
         */
        void getCacheDataOK(ThemeBean themeBean);

        /**
         * 读取缓存出错
         *
         * @param error
         */
        void getCacheDataFaile(String error);

    }


}
