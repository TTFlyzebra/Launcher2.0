package com.flyzebra.ppfunstv.utils.wallpaper;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.view.Surface;
import android.view.SurfaceControl;
import android.view.SurfaceSession;

import com.flyzebra.ppfunstv.utils.FlyLog;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by huhuatao on 2017/7/13.
 */
public class FastWallpaper {
    //surface相关变量，用于绘制图像
    private Surface surface;
    private SurfaceControl surCtrl;
    private IBinder displayToken;

    //动画指令
    private final int MSG_NORMAL = 1;
    private final int MSG_ALPHA_MODE1 = 2;
    private final int MSG_ALPHA_MODE2 = 3;
    private final int MSG_ZOOMIN = 4;
    private final int MSG_ZOOMOUT = 5;
    private final int MSG_ROTATE = 6;
    private final int MSG_TRANSLATE = 7;

    //用户控制立即取消之前的动画
    private boolean bCancelled = false;
    private Handler mHandler;

    private static FastWallpaper fastWallpaper;

    private static Bitmap curBitmap;

    //第1种透明度动画模式
    private static final int INIT_1 = 1; //初始值
    private static final int STEP_1 = 15;   //步长
    private static final int MAX_1 = 255;  //最大值255

    //第2种透明度动画模式
    private static final int INIT_2 = 1; //初始值
    private static final int STEP_2 = 1;   //步长
    private static final int MAX_2 = 50;  //最大值255
    private static final int SLEEP_2 = 10;  //动画间隔，决定过渡快慢，数字越小越快

    private void initHandler() {
        HandlerThread mHandlerThread = new HandlerThread("fastwallper");
        mHandlerThread.start();

        Looper looper = mHandlerThread.getLooper();
        if (looper != null) {
            mHandler = new Handler(looper) {
                @Override
                public void dispatchMessage(Message msg) {
                    Bitmap bitmap = (Bitmap) msg.obj;
                    FlyLog.d("dispatchMessage:msg:" + msg.what + ", msgString:" + getMsgString(msg.what));

                    //状态重置，确保最新的动画指令可以执行
                    bCancelled = false;

                    switch (msg.what) {
                        case MSG_NORMAL:
                            doSetBitmap(bitmap);
                            break;
                        case MSG_ALPHA_MODE1:
                            doAlphaAnamationMode1(bitmap);
                            break;
                        case MSG_ALPHA_MODE2:
                            doAlphaAnamationMode2(bitmap);
                            break;
                        default:
                            break;
                    }

                    super.dispatchMessage(msg);
                }
            };
        }
    }

    public static synchronized FastWallpaper getInstance() {
        if (fastWallpaper == null) {
            fastWallpaper = new FastWallpaper();
        }
        return fastWallpaper;
    }

    public FastWallpaper() {
        initHandler();
        initSurfaceControl();
    }

    public void initSurfaceControl() {
        try {
            surCtrl = new SurfaceControl(new SurfaceSession(), "fastwallpaper",
                    1280, 720, PixelFormat.RGBA_8888, SurfaceControl.HIDDEN);

            displayToken = SurfaceControl.getBuiltInDisplay(
                    SurfaceControl.BUILT_IN_DISPLAY_ID_MAIN);

            Class clazz = Class.forName("android.view.Surface");
            surface = (Surface) clazz.newInstance();
            SurfaceControl.setDisplaySurface(displayToken, surface);

            Class[] cArg = new Class[1];
            cArg[0] = SurfaceControl.class;
            Method method = clazz.getDeclaredMethod("copyFrom", cArg);
            method.invoke(surface, surCtrl);

            SurfaceControl.openTransaction();
            surCtrl.setLayerStack(0);
            //surCtrl.setLayer(21000);
            surCtrl.setWindowCrop(new Rect(0, 0, 1280, 720));
            surCtrl.show();
            SurfaceControl.closeTransaction();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doSetBitmap(Bitmap bitmap) {
        try {
            if (surface != null && bitmap != null) {
                Canvas canvas = surface.lockCanvas(null);
                canvas.drawBitmap(bitmap, 0, 0, null);
                surface.unlockCanvasAndPost(canvas);

                curBitmap = bitmap;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //取值范围0-255， 0为不透明，255全透明
    private void setAlpha(float alpha){
        SurfaceControl.openTransaction();
        surCtrl.setAlpha(alpha);
        SurfaceControl.closeTransaction();
    }

    private void doAlphaAnamationMode1(final Bitmap bitmap) {
        try {
            int alpha = INIT_1;
            Paint paint = new Paint();
            paint.setAntiAlias(true);

            while (!isCancelled()) {
                if (surface != null && bitmap != null) {
                    Canvas canvas = surface.lockCanvas(null);

                    canvas.drawRGB(0, 0, 0);
                    //paint.setAlpha(MAX - alpha);
                    canvas.drawBitmap(curBitmap, 0, 0, null);
                    paint.setAlpha(alpha);
                    canvas.drawBitmap(bitmap, 0, 0, paint);

                    surface.unlockCanvasAndPost(canvas);

                    //Thread.sleep(10);
                    if (alpha >= MAX_1) break;

                    alpha += STEP_1;
                    if (alpha > MAX_1) alpha = MAX_1;
                }
            }

            curBitmap = bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void doAlphaAnamationMode2(final Bitmap bitmap) {
        try {
            int alpha;

            //从不透明往透明过渡
            alpha = INIT_2;
            while (!isCancelled()) {
                setAlpha(alpha);

                Thread.sleep(SLEEP_2);
                if (alpha >= MAX_2) break;
                alpha += STEP_2;
                if (alpha > MAX_2) alpha = MAX_2;
            }

            doSetBitmap(bitmap);

            //从透明往不透明过渡
            alpha = MAX_2;
            while (!isCancelled()) {
                setAlpha(alpha);

                Thread.sleep(SLEEP_2);
                if (alpha <= INIT_2) break;
                alpha -= STEP_2;
                if (alpha < INIT_2) alpha = INIT_2;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public synchronized void setBitmap(Bitmap bitmap) {
        cancelAll();
        mHandler.removeCallbacksAndMessages(null);

        Message msg = new Message();
        msg.what = MSG_NORMAL;
        msg.obj = bitmap;
        mHandler.sendMessage(msg);
    }

    //mode取值：1,2，默认为1
    public synchronized void setBitmapAlphaAnimation(final Bitmap bitmap, int mode) {
        //cancelAll();//不中断前一次动画，否则会造成视觉效果不自然
        mHandler.removeCallbacksAndMessages(null);

        Message msg = new Message();
        if (mode == 1){
            msg.what = MSG_ALPHA_MODE1;
        }else if(mode == 2) {
            msg.what = MSG_ALPHA_MODE2;
        }else{
            msg.what = MSG_ALPHA_MODE1;
        }
        msg.obj = bitmap;
        mHandler.sendMessage(msg);
    }


    private AtomicBoolean isShow = new AtomicBoolean(true);

    private Runnable task = new Runnable() {
        @Override
        public void run() {
            try {
                if (isShow.get()) {
                    isShow.set(false);
                    SurfaceControl.openTransaction();
                    FlyLog.d("real hide");
                    surCtrl.hide();
                    SurfaceControl.closeTransaction();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public void hide() {
        mHandler.removeCallbacks(task);
        if (isShow.get()) {
            mHandler.postDelayed(task, 1000);
        }
    }

    public void show() {
        mHandler.removeCallbacks(task);
        try {
            if (!isShow.get()) {
                isShow.set(true);
                SurfaceControl.openTransaction();
                FlyLog.d("real show");
                surCtrl.show();
                SurfaceControl.closeTransaction();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isCancelled() {
        return bCancelled;
    }

    private void cancelAll() {
        bCancelled = true;
    }

    private String getMsgString(int msg) {
        String msgString = "";
        switch (msg) {
            case MSG_NORMAL:
                msgString = "MSG_NORMAL";
                break;
            case MSG_ALPHA_MODE1:
                msgString = "MSG_ALPHA_MODE1";
                break;
            case MSG_ALPHA_MODE2:
                msgString = "MSG_ALPHA_MODE2";
                break;
            case MSG_ROTATE:
                msgString = "MSG_ROTATE";
                break;
            case MSG_TRANSLATE:
                msgString = "MSG_TRANSLATE";
                break;
            case MSG_ZOOMIN:
                msgString = "MSG_ZOOMIN";
                break;
            case MSG_ZOOMOUT:
                msgString = "MSG_ZOOMOUT";
                break;
            default:
                break;
        }

        return msgString;
    }
}