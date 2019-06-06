package com.flyzebra.flyui;

import android.app.Activity;
import android.view.ViewGroup;

import com.flyzebra.flyui.bean.ThemeBean;
import com.flyzebra.flyui.chache.DiskCache;
import com.flyzebra.flyui.chache.IDiskCache;
import com.flyzebra.flyui.chache.IUpdataVersion;
import com.flyzebra.flyui.chache.UpdataVersion;
import com.flyzebra.flyui.event.FlyEvent;
import com.flyzebra.flyui.event.IFlyEvent;
import com.flyzebra.flyui.utils.AppUtil;
import com.flyzebra.flyui.utils.FlyLog;
import com.flyzebra.flyui.view.themeview.ThemeView;

/**
 * Author FlyZebra
 * 2019/4/4 11:31
 * Describ:
 **/
public class Flyui implements IUpdataVersion.CheckCacheResult, IUpdataVersion.UpResult {
    private Activity activity;
    private ThemeView mThemeView;
    private IUpdataVersion iUpDataVersion;
    private IDiskCache iDiskCache;

    public Flyui(Activity activity) {
        this.activity = activity;
    }

    public void onCreate() {
        FlyLog.d("setCellBean");
        mThemeView = new ThemeView(activity);
        activity.addContentView(mThemeView, new ViewGroup.LayoutParams(-1, -1));
        mThemeView.onCreate(activity);
        if (activity instanceof IFlyEvent) {
            FlyEvent.register((IFlyEvent) activity);
        }
        iDiskCache = new DiskCache().init(activity);
        iUpDataVersion = new UpdataVersion(activity.getApplicationContext(), iDiskCache);
        String token = "1234567890";
        String ApiUrl = "http://192.168.1.119:801/uiweb";
        String ApiTheme = "/api/app?type=%s&themeName=%s&version=%s";
        String type=AppUtil.getApplicationName(activity);
        String themeName="";
        String version = AppUtil.getVersionName(activity);
        iUpDataVersion.initApi(ApiUrl, ApiTheme,type,themeName,version, token);
        iUpDataVersion.forceUpVersion(this);
    }

    public void onDestroy() {
        FlyLog.d("onDestroy");
        if (activity instanceof IFlyEvent) {
            FlyEvent.unregister((IFlyEvent) activity);
        }
        mThemeView.onDestory();
        iUpDataVersion.cancelAllTasks();
        FlyEvent.clear();
    }


    @Override
    public void upVersionOK(ThemeBean themeBean) {
        upView(themeBean);
    }

    @Override
    public void upVesionProgress(String msg, int sum, int progress) {
    }

    @Override
    public void upVersionFaile(String error) {
        iUpDataVersion.getCacheData(this);
    }

    @Override
    public void getCacheDataOK(ThemeBean themeBean) {
        upView(themeBean);
    }

    @Override
    public void getCacheDataFaile(String error) {
        iUpDataVersion.forceUpVersion(this);
    }

    private void upView(ThemeBean themeBean) {
        mThemeView.upData(themeBean);
    }

}
