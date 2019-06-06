package com.ppfuns.launcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.ppfuns.launcher.base.BaseApplication;
import com.ppfuns.ppfunstv.http.FlyOkHttp;
import com.ppfuns.ppfunstv.utils.AppUtil;
import com.ppfuns.ppfunstv.utils.FlyLog;
import com.ppfuns.ppfunstv.utils.wallpaper.FastWallpaper;
import com.umeng.analytics.MobclickAgent;

/**
 *
 * Created by pc1 on 2016/6/13.
 */
public class MyApp extends BaseApplication {
    @Override
    public void onCreate() {
        super.onCreate();

        registerActivityChange();
        FlyOkHttp.getInstance().Init(getApplicationContext());
        //测试,开始严格模式,查找问题
//        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//                .detectDiskReads().detectDiskWrites().detectNetwork()
//                .detectCustomSlowCalls().detectAll()
//                .penaltyLog().build());
//        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
//                .detectActivityLeaks().detectAll()
//                .penaltyLog().penaltyDeath().build());
//        LeakCanary.install(this);
        MobclickAgent.setDebugMode(true);
//        CrashHandler crashHandler = CrashHandler.getInstance();
//        crashHandler.init(getApplicationContext());
    }

    private static final String LAUNCHER_NAME = "com.ppfuns.launcher.ui.LauncherActivity";
    private static String lastPackageName = LAUNCHER_NAME;
    private void registerActivityChange(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.ppfuns.activitychange");  //添加要收到的广播
        registerReceiver(new BroadcastReceiver(){
                @Override
                public void onReceive(Context context, Intent intent){
                    String newActivityName = intent.getStringExtra("activityName");
                    FlyLog.d("onReceive:com.ppfuns.activitychange, "
                            + "activityName" + newActivityName
                            + ", lastPackageName:" + lastPackageName);

                    if (newActivityName == null) return;

                    if (newActivityName.contains(LAUNCHER_NAME)){
                        //从其它应用返回到launcher时，显示fastwallpaper
                        FlyLog.d("FastWallpaper check show");
                        FastWallpaper.getInstance().show();
												
                    }else{
                        //launcher切换到其它应用时，隐藏fastwallpaper
                        //此时需要判断luancher是否已经是top，避免dialog提示框触发隐藏fastwallpaper
                        boolean bLauncherIsTop = AppUtil.isActivityTop(context);
                        FlyLog.d("launcher is top:" + bLauncherIsTop);
                        if (!bLauncherIsTop) {
                            FlyLog.d("FastWallpaper check hide");
                            FastWallpaper.getInstance().hide();
                        }
                    }

                    lastPackageName = newActivityName;
                }
            }, intentFilter);
    }
}
