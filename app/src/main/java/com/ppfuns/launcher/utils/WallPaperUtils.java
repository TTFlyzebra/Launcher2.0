package com.ppfuns.launcher.utils;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.IntDef;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.ppfuns.ppfunstv.utils.wallpaper.FastWallpaper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.URI;

/**
 * Created by FlyZebra on 2016/8/30.
 */
public class WallPaperUtils {

    private static final String TAG = WallPaperUtils.class.getSimpleName();

    //设置壁纸
    public static void setWallPaper(Context context, final Bitmap bitmap) {
        if (bitmap == null) {
            FlyLog.d("Bitmap is null, no set wallpager!");
            return;
        }
        final WallpaperManager wallManager = WallpaperManager.getInstance(context);
        wallManager.suggestDesiredDimensions(ScreenUtils.getScreenWidth(context), ScreenUtils.getScreenHeight(context));

        new Thread(new Runnable() {
            @Override
            public void run() {
                FlyLog.d(TAG + " set wall paper start");
                try {
                    wallManager.setWallpaperOffsetSteps(0, 0);
                    wallManager.setBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                FlyLog.d(TAG + " set wall paper end");
            }
        }).start();
    }


    //设置壁纸
    public static void setWallPaper(final Context context, final String filePath) {
        FlyLog.d("set wall paper start");
        new Thread(new Runnable() {
            @Override
            public void run() {
                FlyLog.d("set wall paper start");
                try {
                    WallpaperManager wallManager = WallpaperManager.getInstance(context);
                    wallManager.suggestDesiredDimensions(ScreenUtils.getScreenWidth(context), ScreenUtils.getScreenHeight(context));
                    InputStream data = null;
                    if (filePath.startsWith("file:///android_asset/")) {
                        data = context.getAssets().open(filePath.substring("file:///android_asset/".length()));
                    } else {
                        data = new FileInputStream(new File(URI.create(filePath)));
                    }
                    if (data != null) {
                        wallManager.setWallpaperOffsetSteps(0, 0);
                        wallManager.setStream(data);
                    }
                } catch (IOException e) {
                    FlyLog.d("set wall Failed! error is %s", e.toString());
                    e.printStackTrace();
                }
                FlyLog.d("set wall paper end");
            }
        }).start();
    }


    //设置壁纸
    public static void setWallPaper(final Context context, final String filePath, final int width, final int height) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                FlyLog.d("set wall paper start");
                try {
                    WallpaperManager wallManager = WallpaperManager.getInstance(context);
                    wallManager.suggestDesiredDimensions(ScreenUtils.getScreenWidth(context), ScreenUtils.getScreenHeight(context));
                    InputStream data = null;
                    if (filePath.startsWith("file:///android_asset/")) {
                        data = context.getAssets().open(filePath.substring("file:///android_asset/".length()));
                    } else {
                        data = new FileInputStream(new File(URI.create(filePath)));
                    }
                    if (data != null) {
                        wallManager.setWallpaperOffsetSteps(0, 0);
                        //wallManager.setStream(data);
                        Bitmap bitmap = BitmapFactory.decodeStream(data);
                        bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);

                        //系统壁纸耗时较长，优先设置fastwallpaer
                        FastWallpaper.getInstance().setBitmap(bitmap);
                        FlyLog.d("set fast wall paper end");

                        wallManager.setBitmap(bitmap);
                    }
                } catch (Exception e) {
                    FlyLog.d("set wall Failed! error is %s", e.toString());
                    e.printStackTrace();
                }
                FlyLog.d("set wall paper end");
            }
        }).start();
    }

    //设置壁纸显示宽,高
    public static void setWallPaperDimensios(Context context) {
        try {
            WallpaperManager wallManager = WallpaperManager.getInstance(context);
            wallManager.suggestDesiredDimensions(ScreenUtils.getScreenWidth(context), ScreenUtils.getScreenHeight(context));
        } catch (Exception e) {
            e.printStackTrace();
            FlyLog.e(e.toString());
        }
    }

    public static void setWallPaper(final Context context, final String url, final WallPaperCallback callback) {
        FlyLog.d(TAG + " set wall paper url:" + url);
        if (TextUtils.isEmpty(url)) {
            callback.onCall(WallPaperCallback.ERROR);
            return;
        }
        //读取最后一次设置的壁纸，如果最后一次设置的壁纸的url跟现在要设置的相同则不设置，直接返回
        String lastUrl = (String) SPUtil.get(context, "wallpagerurl", "url", "");
        if (lastUrl.equals(url)) {
            FlyLog.d(TAG + " the same url address, no set!");
            callback.onCall(WallPaperCallback.ERROR);
            return;
        }
        callback.onCall(WallPaperCallback.SETTING);
        Glide.with(context.getApplicationContext())
                .load(url)
                .asBitmap()
                .fitCenter()
                .override(DisplayUtils.getMetrices((Activity) context).widthPixels, DisplayUtils.getMetrices((Activity) context).heightPixels)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .listener(new RequestListener<String, Bitmap>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                        callback.onCall(WallPaperCallback.ERROR);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        if (resource != null) {
                            //调整壁纸大小
                            FlyLog.d("width = " + resource.getWidth() + "\t height = " + resource.getHeight());
                            setWallPaper(context, resource);
                            SPUtil.set(context, "wallpagerurl", "url", url);
                            FlyLog.d("set wallpaper file ok...");
                        }
                        callback.onCall(WallPaperCallback.SUCCESS);
                    }
                });
    }


    public interface WallPaperCallback {
        int SUCCESS = 0x001;
        int ERROR = 0x002;
        int SETTING = 0x003;

        @IntDef({SUCCESS, ERROR, SETTING})
        @Retention(RetentionPolicy.SOURCE)
        @interface setWallPaperStat {
        }

        void onCall(@setWallPaperStat int code);
    }
}
