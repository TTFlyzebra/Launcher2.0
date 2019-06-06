package com.flyzebra.flyui.chache;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * Author: FlyZebra
 * Time: 18-3-29 下午9:07.
 * Discription: This is BitmapMemoryCache
 */

public class BitmapMemoryCache implements ICache<Bitmap> {
    LruCache<String, Bitmap> lruCache;

    public BitmapMemoryCache(Context context) {
        lruCache = new LruCache<String, Bitmap>(Math.min(30 * 1024, (int) (Runtime.getRuntime().maxMemory() / 1024 / 4))) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight() / 1024;
            }
        };
    }

    @Override
    public Bitmap get(String key) {
        return lruCache.get(key);
    }

    @Override
    public void put(String key, Bitmap bitmap) {
        lruCache.put(key, bitmap);
    }
}
