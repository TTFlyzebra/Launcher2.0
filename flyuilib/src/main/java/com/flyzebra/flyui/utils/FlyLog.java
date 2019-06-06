package com.flyzebra.flyui.utils;

import android.util.Log;

/**
 *
 * Created by FlyZebra on 2016/3/24.
 */
public class FlyLog {
    public static final String TAG = "flyui";
    public static String[] filter = {
    };


    public static void i(String logString, Object... args) {
        for (String aFilter : filter) {
            if (logString.indexOf(aFilter) == 0) {
                return;
            }
        }
        Log.i(TAG, buildLogString(logString, args));
    }

    public static void d() {
        Log.d(TAG, buildLogString(""));
    }


    public static void d(String logString, Object... args) {
        for (String aFilter : filter) {
            if (logString.indexOf(aFilter) == 0) {
                return;
            }
        }
        Log.d(TAG, buildLogString(logString, args));
    }

    public static void v(String logString, Object... args) {
        for (String aFilter : filter) {
            if (logString.indexOf(aFilter) == 0) {
                return;
            }
        }
        Log.v(TAG, buildLogString(logString, args));
    }

    public static void e(String logString, Object... args) {
        for (String aFilter : filter) {
            if (logString.indexOf(aFilter) == 0) {
                return;
            }
        }
        Log.e(TAG, buildLogString(logString, args));
    }


    private static String buildLogString(String str, Object... args) {
        if (args.length > 0) {
            str = String.format(str, args);
        }
        //进程消息
        Thread thread = Thread.currentThread();

        //打印位置
        StackTraceElement caller = new Throwable().fillInStackTrace().getStackTrace()[2];
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder
                .append("[")
                .append(thread.getName())
                .append("][")
                .append(thread.getId())
                .append("](")
                .append(caller.getFileName())
                .append(":")
                .append(caller.getLineNumber())
                .append(")")
                .append(caller.getMethodName())
                .append("()")
                .append(">>>>")
                .append(str);
//        }
        return stringBuilder.toString();
    }

}