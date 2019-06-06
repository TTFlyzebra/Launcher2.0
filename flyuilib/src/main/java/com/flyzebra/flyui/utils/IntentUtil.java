package com.flyzebra.flyui.utils;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by FlyZebra on 2016/6/22.
 */
public class IntentUtil {

    /**
     * 执行shell指令
     *
     * @param command
     */
    public static void execCommand(String command) {
        FlyLog.d("execCommand:" + command);
        if (TextUtils.isEmpty(command)) {
            FlyLog.d("command is null...");
            return;
        }
        try {
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec(command);
            InputStream inputstream = proc.getInputStream();
            InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
            BufferedReader bufferedreader = new BufferedReader(inputstreamreader);
            String line = "";
            StringBuilder sb = new StringBuilder(line);
            while ((line = bufferedreader.readLine()) != null) {
                sb.append(line);
                sb.append('\n');
            }
            try {
                if (proc.waitFor() != 0) {
                    FlyLog.d("exit value = " + proc.exitValue());
                }
            } catch (InterruptedException e) {
                FlyLog.e(e.toString());
            }
        } catch (Exception e) {
            FlyLog.e(e.toString());
        }

    }

    /**
     * 执行命令,发送广播
     *
     * @param context
     * @param action
     * @param map
     */
    public static void execSendBroadcast(Context context, String action, Map<String, Object> map) {
        FlyLog.d("sendBroadcast  action:" + action);
        Intent it = new Intent(action);
        if (map != null) {
            FlyLog.d("sendBroadcast  action:" + action + " map:" + map.toString());
            for (Object o : map.entrySet()) {
                Map.Entry<String, Object> entry = (Map.Entry) o;
                String key = entry.getKey();
                Object val = entry.getValue();
                if (val instanceof Integer) {
                    it.putExtra(key, Integer.parseInt((String) val));
                } else if (val instanceof Float) {
                    it.putExtra(key, Float.parseFloat((String) val));
                } else if (val != null) {
                    it.putExtra(key, (String) val);
                }
            }
        }
        context.sendBroadcast(it);
    }

    /**
     * 执行命令,发送广播
     *
     * @param context
     * @param action
     * @param cmd     格式遵循count=(int)1#name=xiaohei,详情参照IntentParamParseHelper类
     */
    public static void execSendBroadcast(Context context, String action, String cmd) {
        FlyLog.d("sendBroadcast  action:" + action + " cmd:" + cmd);
        Intent it = new Intent(action);
        Bundle bundle = ParamParseUtil.parseBundle(cmd);
        it.putExtras(bundle);
        context.sendBroadcast(it);
    }

    /**
     * 执行命令,启动activity
     *
     * @param context
     * @param action
     */
    public static boolean execStartActivity(Context context, String action) {
        return execStartActivity(context, action, Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    public static boolean execStartActivity(Context context, String action,int flag) {
        try {


            PackageManager packageManager = context.getPackageManager();
            final Intent intent = new Intent(action);
            List<ResolveInfo> resolveInfo = packageManager
                    .queryIntentActivities(intent,
                            PackageManager.MATCH_DEFAULT_ONLY);

            if (resolveInfo.isEmpty()) {
                FlyLog.d("no activity found to handle Intent :" + action);
                return false;
            }
            String activityName = resolveInfo.get(0).activityInfo.name;
            String packName = resolveInfo.get(0).activityInfo.packageName;

            Intent it = new Intent();
            it.addFlags(flag);
            if (!TextUtils.isEmpty(activityName) && !TextUtils.isEmpty(packName)) {
                FlyLog.d("action :" + action + " change to package " + packName + "and activity " + activityName);
                it.setPackage(packName);
                it.setClassName(packName, activityName);
                context.startActivity(it);
                return true;
            } else {
                FlyLog.d("no activity found to handle Intent :" + action);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 执行命令,启动activity
     *
     * @param context
     * @param it
     * @param info    启动失败时的提示信息
     */
    public static boolean execStartActivityAndShowTip(Context context, @NonNull Intent it, String info, boolean isNeedAuth) {
        return execStartActivityAndShowTip(context, it, info, isNeedAuth, Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    public static boolean execStartActivityAndShowTip(Context context, @NonNull Intent it, String info, boolean isNeedAuth, int flag) {
        try {
            FlyLog.d("intent :" + it.toString() + " cmd:" + info + " isNeedAuth:" + isNeedAuth);
            it.addFlags(flag);
            ComponentName componentName = it.resolveActivity(context.getPackageManager());
            if (componentName != null) {
                FlyLog.d("componentName:" + componentName);
                context.startActivity(it);
                return true;
            }
//            DialogUtil.showDialog(context, info);
        } catch (Exception e) {
            e.printStackTrace();
            FlyLog.e(e.toString());
        }
        return false;
    }


    /**
     * 执行命令,根据包名启动应用
     *
     * @param context     上下文
     * @param packageName 包名
     * @return 是否启动成功
     * true:启动成功
     * false:启动失败
     */
    public static boolean execStartPackage(Context context, String packageName) {
        return execStartPackage(context, packageName, Intent.FLAG_ACTIVITY_NEW_TASK);
    }


    public static boolean execStartPackage(Context context, String packageName, int flag) {
        return execStartPackage(context, packageName, null, flag);
    }


    public static boolean execStartPackage(Context context, String packageName, String data, int flag) {
        try {
            FlyLog.d("execStartPackage packageName = %s  ,data = %s.", packageName, data);
            if (TextUtils.isEmpty(packageName)) {
                return false;
            }
            if (isAppInstalled(context, packageName)) {
                final PackageManager packageManager = context.getPackageManager();
                packageName = packageName.replace(" ", "");
                Intent i = packageManager.getLaunchIntentForPackage(packageName);

                if (i != null) {//安装的apk可能没有配置android.intent.category.LAUNCHER
                    i.addFlags(flag);
                    if (!TextUtils.isEmpty(data)) {
                        Bundle bundle = ParamParseUtil.parseBundle(data);
                        i.putExtras(bundle);
                    }
                    context.startActivity(i);
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            FlyLog.e(e.toString());
        }
        return false;
    }


    public static boolean execStartPackage(Context context, String packageName, String className) {
        return execStartPackage(context, packageName, className, null);
    }


    public static boolean execStartPackage(Context context, String packageName, String className, String data) {
        return execStartPackage(context, packageName, className, data, Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    public static boolean execStartPackage(Context context, String packageName, String className, String data, int flag) {
        try {
            if (TextUtils.isEmpty(packageName) || TextUtils.isEmpty(className)) {
                FlyLog.d("execStartPackage packageName or className is null");
                return false;
            }
            FlyLog.d("execStartPackage packageName:" + packageName + " className:" + className);
            Intent intent = new Intent();
            intent.addFlags(flag);
            ComponentName cn = new ComponentName(packageName, className);
            intent.setComponent(cn);
            if (!TextUtils.isEmpty(data)) {
                Bundle bundle = ParamParseUtil.parseBundle(data);
                intent.putExtras(bundle);
            }
            @SuppressLint("WrongConstant")
            ActivityInfo info = intent.resolveActivityInfo(context.getPackageManager(), PackageManager.GET_ACTIVITIES);
            if (info != null) {
                FlyLog.d(info.toString());
                context.startActivity(intent);
                return true;
            } else {
                FlyLog.d("no activity found to handle Intent :" + packageName);
            }
        } catch (Exception e) {
            e.printStackTrace();
            FlyLog.e(e.toString());
        }
        return false;
    }

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

}

