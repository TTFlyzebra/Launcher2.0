package com.flyzebra.ppfunstv.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.support.annotation.NonNull;

/**
 * Created by pc1 on 2016/7/28.
 */
public class ServiceUtils {
    public static void bindServiceCls(Context activity, String pkg, String cls, @NonNull ServiceConnection conn) {
        final Intent intent = new Intent();
        ComponentName componentName = new ComponentName(pkg,cls);
        intent.setComponent(componentName);
        activity.bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }

    public static void bindServiceAction(Context activity, String action, String pkg, @NonNull ServiceConnection conn) {
        Intent intent = new Intent(action);
        intent.setPackage(pkg);
        activity.bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }

}
