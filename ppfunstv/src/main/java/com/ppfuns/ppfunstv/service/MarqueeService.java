package com.ppfuns.ppfunstv.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.ppfuns.marqueeservice.IMarqueeService;
import com.ppfuns.ppfunstv.utils.FlyLog;
import com.ppfuns.ppfunstv.view.MarqueeView.GlobalMarqueeView;
import com.ppfuns.ppfunstv.view.MarqueeView.IMarquee;

/**
 *
 * Created by flyzebra on 17-6-26.
 */
public class MarqueeService extends Service {
    private IMarquee marquee;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        FlyLog.d(""+intent);
        return mBinder;
    }


    @Override
    public void onCreate() {
        FlyLog.d("create GlobalMarqueeView");
        super.onCreate();
        marquee  = GlobalMarqueeView.getInstance(this);

    }

    @Override
    public boolean onUnbind(Intent intent) {
        FlyLog.d();

        return true;
    }

    @Override
    public void onDestroy() {
        FlyLog.d();
        marquee.release();
        marquee = null;
        handler.removeCallbacksAndMessages(null);
        handler = null;
        mBinder = null;
        super.onDestroy();
    }

    Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    private Binder mBinder =  new IMarqueeService.Stub() {

        @Override
        public void setPoint(int x, int y, int width, int height) throws RemoteException {
            FlyLog.d();
            marquee.setPoint(x,y,width,height);
        }

        @Override
        public void setText(String text) throws RemoteException {
            FlyLog.d();
            marquee.setText(text);
        }

        @Override
        public void setTextColor(String textColor) throws RemoteException {
            FlyLog.d();
            marquee.setTextColor(textColor);
        }

        @Override
        public void setTextSize(int textSize) throws RemoteException {
            FlyLog.d();
            marquee.setTextSize(textSize);
        }

        @Override
        public void setDuration(long duration) throws RemoteException {
            FlyLog.d();
            marquee.setDuration(duration);
        }

        @Override
        public void init() throws RemoteException {
            FlyLog.d();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    marquee.init();
                }
            });

        }

        @Override
        public void play() throws RemoteException {
            FlyLog.d();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    marquee.play();
                }
            });
        }

        @Override
        public void stop() throws RemoteException {
            FlyLog.d();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    marquee.stop();
                }
            });
        }

        @Override
        public void release() throws RemoteException {
            FlyLog.d();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    marquee.release();

                }
            });
        }

        @Override
        public void bind() throws RemoteException {
            FlyLog.d();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    marquee.bind(null);
                }
            });
        }

        @Override
        public void setDirection(final int direction) throws RemoteException {
            FlyLog.d();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    marquee.setDirection(direction);
                }
            });
        }
    };

}
