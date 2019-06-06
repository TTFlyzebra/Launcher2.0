package com.flyzebra.launcher.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import java.io.FileOutputStream;

/**
 * Created by zpf on 2016/11/8.
 */

public class ScreenshotUtils {


    public static Bitmap takeScreenShot(Activity activity) {
        // View是你需要截图的View
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = null;
        try {
            b1 = view.getDrawingCache();
        } catch (OutOfMemoryError e) {
            // TODO: handle exception
        }

        // 获取状态栏高度
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        System.out.println(statusBarHeight);
        // 获取屏幕长和高
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        int height = activity.getWindowManager().getDefaultDisplay().getHeight();
        // 去掉标题栏
        // Bitmap mGaussBlurBitmap = Bitmap.createBitmap(b1, 0, 25, 320, 455);
        Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height);

        return b;
    }


    public static Bitmap getCurrentImage(Activity activity) {
        long startTime = System.currentTimeMillis();
        //1.构建Bitmap
        WindowManager windowManager = activity.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        int w = display.getWidth();
        int h = display.getHeight();

        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

        //2.获取屏幕
        View decorview = activity.getWindow().getDecorView();
        decorview.setDrawingCacheEnabled(false);
        decorview.setDrawingCacheEnabled(true);
        decorview.buildDrawingCache();
        bmp = decorview.getDrawingCache();
//        decorview.setDrawingCacheEnabled(false);
//        decorview.destroyDrawingCache();
        FlyLog.i("getScreenShot time " + (System.currentTimeMillis() - startTime) + " ms");
        FlyLog.i("bmp size = " + bmp.getByteCount());
        return bmp;
    }


    public static void saveScreenShot(Activity context) {
        FlyLog.i("start save and ScreenShot");
        long startTime = System.currentTimeMillis();
        String cachePath = context.getCacheDir().getPath();
        String fname = cachePath + "/launcher_screenshot.png";
        Bitmap bitmap = getCurrentImage(context);

        if (bitmap != null) {

            try {
                FileOutputStream
                        out = new FileOutputStream(fname);
                bitmap.compress(Bitmap.CompressFormat.PNG, 10, out);
                out.flush();
                out.close();
            } catch (Exception
                    e) {

                e.printStackTrace();

            }
        }

        FlyLog.i(" end and ScreenShot time = " + (System.currentTimeMillis() - startTime) + " ms");
    }
}
