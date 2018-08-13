package com.ppfuns.ppfunstv.module;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;

import com.ppfuns.ppfunstv.utils.EncodeHelper;
import com.ppfuns.ppfunstv.utils.FlyLog;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 *
 * Created by flyzebra on 17-7-3.
 */
public class BitmapCache {
    private Context mContext;
    private LruCache<String, Bitmap> mCache;

    public BitmapCache(Context context) {
        this(context,(int) (Runtime.getRuntime().maxMemory() / 4));
    }

    public BitmapCache(Context context,int maxSize) {
        mContext = context;
        mCache = new LruCache<String, Bitmap>(maxSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }
        };
    }

    public Bitmap getBitmap(String url) {
        Bitmap bitmap = mCache.get(url);
        if (bitmap == null) {
            bitmap = createBitmapFromLocal(mContext, url);
            if (bitmap != null) {
                mCache.put(url, bitmap);
            }else{
                FlyLog.d("don't find local bitmap file!");
            }
        }
        return bitmap;
    }

    public synchronized void putBitmap(String url, Bitmap bitmap) {
        try {
            mCache.put(url, bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 加载本地图片（包括默认asset目录下的图片和下载后存在本地的图片）
     * NODE:此方法不具有通用性，不应这样麉装
     * @param context
     * @param url 网络请求地址的URL
     * @return
     */
    public static Bitmap createBitmapFromLocal(Context context, String url) {
        Bitmap bitmap = null;

        if (url == null || "".equals(url)){
            return null;
        }

        try {
            String filename;
            if(url.startsWith("http://")){
                filename = "file://" + context.getFilesDir().getAbsolutePath() + File.separator + "ppfuns/" + EncodeHelper.md5(url) + ".0";
            }else{
                filename = url;
            }
            File file = new File(URI.create(filename));
            if (file.exists()) {
                bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            } else {
                AssetManager am = context.getResources().getAssets();
                InputStream is = am.open("ppfuns/" + EncodeHelper.md5(url) + ".0");
                bitmap = BitmapFactory.decodeStream(is);
                is.close();
            }
        } catch (IOException e) {
            FlyLog.d(e.toString());
            e.printStackTrace();
        }
        return bitmap;
    }

}