package com.flyzebra.ppfunstv.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;

import com.flyzebra.ppfunstv.data.RecentTag;

import java.util.ArrayList;
import java.util.List;

/**
 */

public class AppUtil {

    public static String[] mFliter = {
            "com.flyzebra.launcher",
            "com.flyzebra."
//            "com.alliance.homeshell"
    };
    private static int MAX_RECENT_TASKS = 10;

    /**
     * 检测APP是否安装
     *
     * @param context     上下文
     * @param packagename 包名
     * @return true:已安装该应用
     * false:未安装该应用
     */
    public static boolean isAppInstalled(Context context, String packagename) {
        if (packagename != null) {
            packagename = packagename.trim();
        }
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packagename, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
//            e.printStackTrace();
        }
        if (packageInfo == null) {
            FlyLog.d(packagename + "app not installed....");
            return false;
        } else {
            FlyLog.d(packagename + "app  installed....");
            return true;
        }
    }

    /**
     * 判断是否为系统应用
     *
     * @param context
     * @param packageName 应用包名
     * @return true：系统应用   false：非系统应用
     */
    public static boolean isSystemApp(Context context, String packageName) {
        boolean isSystemApp = false;
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                isSystemApp = true;
            }
        } catch (PackageManager.NameNotFoundException e) {
            FlyLog.e(e.toString());
            e.printStackTrace();
        }
        return isSystemApp;
    }


    /**
     * 获取所有最近app
     * @param context
     * @param maxNum
     * @return
     */
    private static List<RecentTag> getRecetApp(Context context, int maxNum) {
        final PackageManager pm = context.getPackageManager();
        final ActivityManager am = (ActivityManager)
                context.getSystemService(Context.ACTIVITY_SERVICE);

        //拿到最近使用的应用的信息列表
        final List<ActivityManager.RecentTaskInfo> recentTasks =
                am.getRecentTasks(maxNum, ActivityManager.RECENT_IGNORE_UNAVAILABLE);

        //自制一个home activity recentTaskInfo，用来区分
        ActivityInfo homeInfo =
                new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
                        .resolveActivityInfo(pm, 0);


        int numTasks = recentTasks.size();

        List<RecentTag> tags = new ArrayList<>();
        //开始初始化每个任务的信息
        for (int i = 0; i < numTasks; ++i) {
            final ActivityManager.RecentTaskInfo info = recentTasks.get(i);

            //复制一个任务的原始Intent
            Intent intent = new Intent(info.baseIntent);
            if (info.origActivity != null) {
                intent.setComponent(info.origActivity);
            }

            //跳过home activity
            if (homeInfo != null) {
                if (homeInfo.packageName.equals(
                        intent.getComponent().getPackageName())
                        && homeInfo.name.equals(
                        intent.getComponent().getClassName())) {
                    continue;
                }
            }

//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(intent.getFlags() & ~Intent.FLAG_ACTIVITY_TASK_ON_HOME & ~Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
            final ResolveInfo resolveInfo = pm.resolveActivity(intent, 0);
            if (resolveInfo != null) {
                final ActivityInfo activityInfo = resolveInfo.activityInfo;
                RecentTag tag = new RecentTag();
                tag.name = activityInfo.loadLabel(pm).toString();
                tag.recentTaskInfo = info;
                tag.intent = intent;
                tag.icon = activityInfo.loadIcon(pm);
                tag.packageName = intent.getComponent().getPackageName();
                tags.add(tag);
            }
        }
        return tags;
    }


    /**
     * 获取所有最近app
     *
     * @param context
     * @return
     */
    private static List<RecentTag> getAllRecentApp(Context context) {
        return getRecetApp(context, MAX_RECENT_TASKS);
    }

    /**
     * 获取第index个最近应用
     *
     * @param context
     * @param index
     * @param filter           过滤条件
     * @param includeSystemApp 是否包含系统应用（true：包含，false不包含）
     * @return
     */
    public static RecentTag getRecentApp(Context context, int index, String[] filter, boolean includeSystemApp) {
        RecentTag recentTag = null;
        List<RecentTag> recentTags = getExcludeApp(context, filter, includeSystemApp);
        if (recentTags != null && recentTags.size() > index) {
            recentTag = recentTags.get(index);
        }

        return recentTag;
    }








    /**
     * 获取除过滤外的所有最近应用
     *
     * @param context
     * @param filter
     * @param includeSystemApp
     * @return212
     */
    public static List<RecentTag> getExcludeApp(Context context, String[] filter, boolean includeSystemApp) {
        List<RecentTag> recentTags = null;
        List<RecentTag> tags = getAllRecentApp(context);
        if (tags != null) {//过滤
            recentTags = new ArrayList<>(tags);
            for (RecentTag tag : tags) {
                if (!includeSystemApp && AppUtil.isSystemApp(context, tag.packageName)) {
                    recentTags.remove(tag);
//                    FlyLog.d("remove system app:" + tag.packageName);
                    continue;
                }

                if (filter == null || filter.length == 0) {
                    filter = mFliter;
                }
                for (String item : filter) {
                    if (!TextUtils.isEmpty(tag.packageName) && tag.packageName.contains(item)) {
//                        FlyLog.d("remove app:" + tag.packageName);
                        recentTags.remove(tag);
                        break;
                    }
                }
            }
        }
        return recentTags;
    }

    /**
     * 获取versionCode
     *
     * @param context
     * @return
     */
    public static String getVersionCode(Context context) {
        try {
            String pkName = context.getPackageName();
            return context.getPackageManager()
                    .getPackageInfo(pkName, 0).versionCode + "";
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 获取versionName
     *
     * @param context
     * @return
     */
    public static String getVersionName(Context context) {
        try {
            String pkName = context.getPackageName();
            return context.getPackageManager().getPackageInfo(
                    pkName, 0).versionName;
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * @param context 上下文
     * @param intent  intent携带activity
     * @return boolean true为在最顶层，false为否
     * @Description: 判断activity是否在应用的最顶层
     */
    public static boolean isTop(Context context, Intent intent) {
        ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> appTask = am.getRunningTasks(1);
        FlyLog.d(appTask.get(0).topActivity.toString() + " intent:" + intent.getComponent());
        if (appTask.size() > 0 && appTask.get(0).topActivity.equals(intent.getComponent())) {
            FlyLog.d(" is in top");
            return true;
        } else {
            FlyLog.d(" not in top");
            return false;
        }
    }

    /**
     * 判断主应用是否在顶层
     *
     * @param context
     * @return
     */
    public static boolean isAppTop(Context context) {
        try {
            String className = "com.flyzebra.launcher.ui.LauncherActivity";
            ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
            ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
            return cn.getClassName().contains(className);
        } catch (Exception e) {
            e.printStackTrace();
            FlyLog.e(e.toString());
        }
        return true;
    }

    /**
     * 判断主应用首界面是否在顶层
     *
     * @param context
     * @return
     */
    public static boolean isActivityTop(Context context) {
        try {
            String className = "com.flyzebra.launcher.ui.LauncherActivity";
            ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
            ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
            return cn.getClassName().equals(className);
        } catch (Exception e) {
            e.printStackTrace();
            FlyLog.e(e.toString());
        }
        return true;
    }
}
