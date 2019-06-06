package com.flyzebra.flyui.chache;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * 图片磁盘缓存定义接口
 * Created by FlyZebra on 2016/6/21.
 */
public interface IDiskCache {

    /**
     * 初始化磁盘缓存，默认缓存大小为50M
     *
     * @param context
     */
    IDiskCache init(Context context);

    /**
     * 初始化磁盘缓存
     *
     * @param context
     * @param size    缓存大小
     */
    IDiskCache init(Context context, int size);

    /**
     * 初始化磁盘缓存
     *
     * @param context
     * @param size    缓存大小
     * @param cachePath 缓存路径
     */
    IDiskCache init(Context context, int size, String cachePath);

    /**
     * 读取缓存的图片
     *
     * @param imgUrl
     * @return
     */
    Bitmap getBitmap(String imgUrl);

    /**
     * 将Bitmap以文件的形式存入磁盘
     *
     * @param url
     * @param bitmap
     * @return
     */
    boolean saveBitmapFromBitmap(String url, Bitmap bitmap);

    /**
     * NOTE:有网络请求，要求在线程中执行
     * 从网络地址imgUrl下载图片并存入磁盘
     *
     * @param imgUrl 图片地址
     * @return
     */
    boolean saveBitmapFromImgurl( String imgUrl);


    /**
     * 删除本地保存的文件
     *
     * @param url
     * @return
     */
    boolean delFileUrl(String url);


    /**
     * 删除本地保存的文件
     *
     * @param key
     * @return
     */
    boolean delFileKey(String key);

    /**
     * 获取网络地址对应的本地文件URL路径
     *
     * @param imgUrl
     * @return
     */
    String getBitmapPath(String imgUrl);

    /**
     * 从磁盘文件读取字符串
     *
     * @param key
     * @return
     */
    String getString(String key);

    /**
     * 将字符串保存进磁盘缓存
     *
     * @param str
     * @param key
     * @return
     */
    boolean saveString(String str, String key);

    /**
     * 将序列化的对象,保存进磁盘
     *
     * @param object
     * @param key
     * @return
     */
    boolean saveObj(Object object, String key);

    /**
     * 从磁盘中获取序列化保存的对象
     *
     * @param key
     * @return
     */
    Object getObj(String key);

    /**
     * 检测该网络地址的文件是否已经保存到本地磁盘
     *
     * @param imgUrl
     * @return
     */
    boolean checkFileExist(String imgUrl);

    /**
     * 获取缓存的存放路径
     *
     * @return
     */
    String getSavePath();

    /**
     * 释放资源
     */
    void release();

}
