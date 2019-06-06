package com.flyzebra.flyui.chache;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.flyzebra.flyui.utils.EncodeUtil;
import com.flyzebra.flyui.utils.FlyLog;
import com.jakewharton.disklrucache.DiskLruCache;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * Author: FlyZebra
 * Time: 18-3-29 下午9:07.
 * Discription: This is BitmapMemoryCache
 */

public class BitmapDiskCache implements ICache<Bitmap> {
    private final int max_size = 100 * 1024 * 1024;
    private DiskLruCache mDiskLruCache;
    private Context mContext;

    public BitmapDiskCache(Context context) {
        mContext = context;
        init();
    }

    public boolean init() {
        try {
            mDiskLruCache = DiskLruCache.open(new File(getSavePath(mContext)), getAppVersion(mContext), 1, max_size);
            return mDiskLruCache != null;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Bitmap get(String key) {
        if (mDiskLruCache == null) {
            FlyLog.d("disklrucache is null!");
            return null;
        }
        Bitmap bitmap = null;
        DiskLruCache.Snapshot snapShot = null;
        InputStream is = null;
        try {
            snapShot = mDiskLruCache.get(EncodeUtil.md5(key));
            if (snapShot != null) {
                is = snapShot.getInputStream(0);
                bitmap = BitmapFactory.decodeStream(is);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (snapShot != null) {
                    snapShot.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FlyLog.d("get bitmap from file, bitmap = " + bitmap);
        return bitmap;
    }

    @Override
    public void put(String key, Bitmap bitmap) {
        if (mDiskLruCache == null) {
            FlyLog.d("disklrucache is null!");
            return;
        }
        boolean flag = false;
        OutputStream outputStream = null;
        try {
            DiskLruCache.Editor editor = mDiskLruCache.edit(EncodeUtil.md5(key));
            if (editor == null) return;
            outputStream = editor.newOutputStream(0);
            if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)) {
                editor.commit();
            } else {
                editor.abort();
            }
            mDiskLruCache.flush();
            flag = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        FlyLog.d("setCellBean bitmap url = %s, success=" + flag,key);
    }

    private int getAppVersion(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

    private String getSavePath(Context context) {
        File str = context.getCacheDir();
        String savePath = str.getAbsolutePath() + File.separator + "jancar" + File.separator + "video";
        return savePath;
    }
}
