package com.flyzebra.ppfunstv.module.UpdataVersion;


import com.flyzebra.ppfunstv.data.CellBean;
import com.flyzebra.ppfunstv.data.ControlBean;
import com.flyzebra.ppfunstv.data.TemplateBean;
import com.flyzebra.ppfunstv.data.TemplateEntity;

import java.util.List;

/**
 *
 * Created by FlyZebra on 2016/6/21.
 */
public interface IUpdataVersion {

    /**
     * 每次请求前调用此函数设置请求API地址
     */
    void initApi();

    /**
     * 设置使用的碰盘缓存
     *
     * @return
     */
    IUpdataVersion setDiskCache(IDiskCache iDiskCache);

    /**
     * 获取TemplateBean
     *
     * @return
     */
    TemplateBean getTemplateBean();

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
     * 设置默认加载模板
     * @param mode
     */
    void setDefualtTemplate(String mode);

    /**
     * 更换模板
     */
    void switchTemplate();

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
        void upVersionOK(TemplateEntity templateEntity, List<CellBean> cellBeanList, ControlBean controlBean);

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
        void getCacheDataOK(TemplateEntity templateEntity, List<CellBean> cellBeanList, ControlBean controlBean);

        /**
         * 读取缓存出错
         *
         * @param error
         */
        void getCacheDataFaile(String error);

    }


}
