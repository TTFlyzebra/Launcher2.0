package com.flyzebra.flyui.chache;

import android.content.Context;
import android.graphics.Bitmap;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Author: FlyZebra
 * Time: 18-3-29 下午8:58.
 * Discription: This is BitmapCache
 */

public class DoubleBitmapCache implements ICache<Bitmap> {
    private BitmapMemoryCache bitmapMemoryCache;
    private BitmapDiskCache bitmapDiskCache;
    private final static Executor executor = Executors.newFixedThreadPool(1);
    private static DoubleBitmapCache doubleBitmapCache = null;

    public static synchronized DoubleBitmapCache getInstance(Context context) {
        if (doubleBitmapCache == null) {
            doubleBitmapCache = new DoubleBitmapCache(context);
        }
        return doubleBitmapCache;
    }

    private DoubleBitmapCache(Context context) {
        bitmapDiskCache = new BitmapDiskCache(context);
        bitmapMemoryCache = new BitmapMemoryCache(context);
    }

    public Bitmap get(String url) {
        Bitmap bitmap = bitmapMemoryCache.get(url);
        if (bitmap == null) {
            bitmap = bitmapDiskCache.get(url);
            if (bitmap != null) {
                bitmapMemoryCache.put(url, bitmap);
            }
        }
        return bitmap;
    }

    @Override
    public void put(final String url, final Bitmap bitmap) {
        if (get(url) == null) {
            bitmapMemoryCache.put(url, bitmap);
        }
        executor.execute(new Runnable() {
            @Override
            public void run() {
                bitmapDiskCache.put(url, bitmap);
            }
        });
    }

}
