package com.ppfuns.launcher.module;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.module.GlideModule;

/**
 * Glide图片加载库Module
 * Created by FlyZebra on 2016/6/27.
 */
public class GlideConfiguration implements GlideModule {

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        // Apply options to the builder here.
        //设置图片显示格式
        builder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);
        //设置内存缓存大小
//        builder.setMemoryCache(new LruResourceCache(50*1024*1024));
        //设置对像池大小
//        builder.setBitmapPool(new LruBitmapPool(50*1024*1024));
    }

    @Override
    public void registerComponents(Context context, Glide glide) {
        // register ModelLoaders here.
    }
}