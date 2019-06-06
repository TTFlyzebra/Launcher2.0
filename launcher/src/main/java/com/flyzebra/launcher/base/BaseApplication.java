package com.flyzebra.launcher.base;

import android.app.Application;
import android.graphics.Bitmap;

/**
 * Created by lizongyuan on 2016/12/6.
 * E-mail:lizy@ppfuns.com
 */

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
//        MyOkHttp.getInstance().Init(getApplicationContext());
        //测试,开始严格模式,查找问题
//        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//                .detectDiskReads().detectDiskWrites().detectNetwork()
//                .detectCustomSlowCalls().detectAll()
//                .penaltyLog().build());
//        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
//                .detectActivityLeaks().detectAll()
//                .penaltyLog().penaltyDeath().build());
//        LeakCanary.install(this);
//        MobclickAgent.setDebugMode(true);
//        CrashHandler crashHandler = CrashHandler.getInstance();
//        crashHandler.init(getApplicationContext());

    }

    public Bitmap mGaussBlurBitmap;
}
