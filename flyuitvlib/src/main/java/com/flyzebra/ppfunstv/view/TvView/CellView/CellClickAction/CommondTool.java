package com.flyzebra.ppfunstv.view.TvView.CellView.CellClickAction;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.text.TextUtils;

import com.flyzebra.ppfunstv.R;
import com.flyzebra.ppfunstv.utils.BehavioralUtil;
import com.flyzebra.ppfunstv.utils.DialogUtil;
import com.flyzebra.ppfunstv.utils.FlyLog;
import com.flyzebra.ppfunstv.utils.IntentParamParseHelper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by lenovo on 2016/6/22.
 */
public class CommondTool {

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
        Bundle bundle = IntentParamParseHelper.parseBundle(cmd);
        it.putExtras(bundle);
        context.sendBroadcast(it);
    }

    /**
     * 执行命令,启动activity
     *
     * @param context
     * @param action
     * @param data    格式遵循count=(int)1#name=xiaohei,详情参照IntentParamParseHelper类
     * @param url     URL Scheme
     */
    public static boolean execStartActivity(Context context, String action, String data, String url, boolean isNeedAuth) {
        return execStartActivity(context, action, data, url, isNeedAuth, Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    public static boolean execStartActivity(Context context, String action, String data, String url, boolean isNeedAuth, int flag) {
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

            FlyLog.d("action :" + action + " data:" + data + " isNeedAuth:" + isNeedAuth + " url:" + url);
            Intent it = new Intent();
            if (!TextUtils.isEmpty(data)) {
                Bundle bundle = IntentParamParseHelper.parseBundle(data);
                it.putExtras(bundle);
            }
            if (!TextUtils.isEmpty(url)) {
                it.setData(Uri.parse(url));
            }
            it.addFlags(flag);
            if (!TextUtils.isEmpty(activityName) && !TextUtils.isEmpty(packName)) {
                FlyLog.d("action :" + action + " change to package " + packName + "and activity " + activityName);
                it.setPackage(packName);
                it.setClassName(packName, activityName);
                context.startActivity(it);
                overridePendingTransition(context);
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
     * @param action
     * @param data    格式遵循count=(int)1#name=xiaohei,详情参照IntentParamParseHelper类
     */
    public static boolean execStartActivity(Context context, String action, String data, boolean isNeedAuth) {
        return execStartActivity(context, action, data, null, isNeedAuth, Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    public static boolean execStartActivity(Context context, String action, String data, boolean isNeedAuth, int flag) {
        return execStartActivity(context, action, data, null, isNeedAuth, flag);
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
                overridePendingTransition(context);
                return true;
            }
            DialogUtil.showDialog(context, info);
        } catch (Exception e) {
            e.printStackTrace();
            FlyLog.e(e.toString());
        }
        return false;
    }


    /**
     * 执行命令,启动activity,启动失败时给出相应提示
     *
     * @param context
     * @param action
     * @param cmd     格式遵循count=(int)1#name=xiaohei,详情参照IntentParamParseHelper类
     * @param info    启动失败时的提示信息
     */
    public static boolean execStartActivityAndShowTip(Context context, String action, String cmd, String info, boolean isNeedAuth) {
        if (!execStartActivity(context, action, cmd, isNeedAuth)) {
            DialogUtil.showDialog(context, info);
            return false;
        }
        return true;
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
                        Bundle bundle = IntentParamParseHelper.parseBundle(data);
                        i.putExtras(bundle);
                    }
                    context.startActivity(i);
                    overridePendingTransition(context);
                    BehavioralUtil.reportStartAppEvent(context, packageName);
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
                Bundle bundle = IntentParamParseHelper.parseBundle(data);
                intent.putExtras(bundle);
            }
            ActivityInfo info = intent.resolveActivityInfo(context.getPackageManager(), PackageManager.GET_ACTIVITIES);
            if (info != null) {
                FlyLog.d(info.toString());
                context.startActivity(intent);
                overridePendingTransition(context);
                BehavioralUtil.reportStartAppEvent(context, packageName);
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

    /**
     * @param context
     * @param resId   资源String
     *                以|线进行分割，第一个参数为action的话以action启动，为activity的话以activity启动，为package的话以package启动
     *                如果不以|进行分割的话，默认以action方式启动
     */
    public static void execStartActivity(Context context, @StringRes int resId) {
        try {
            String cmd = context.getString(resId);
            String[] cmds = cmd.split("\\|");
            if (cmds.length > 1) {
                if (Type.ACTION.equals(cmds[0])) {
                    execStartActivity(context, cmds[1], null, false);
                } else if (Type.ACTIVITY.equals(cmds[0]) && cmds.length == 3) {
                    execStartPackage(context, cmds[1], cmds[2]);
                } else {
                    execStartPackage(context, cmds[1]);
                }
            } else {
                execStartActivity(context, cmd, null, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            FlyLog.e(e.toString());
        }
    }

    public interface Type {
        String ACTIVITY = "activity";
        String ACTION = "action";
        String PACKAGE = "package";
    }


    public static void overridePendingTransition(Context context) {
        if (context instanceof Activity) {
            FlyLog.d();
            ((Activity) context).overridePendingTransition(R.anim.tv_anim_activity_enter, R.anim.tv_anim_activity_exit);
        }
    }
}

