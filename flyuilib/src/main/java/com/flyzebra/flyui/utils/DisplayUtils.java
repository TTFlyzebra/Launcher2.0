package com.flyzebra.flyui.utils;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 *
 * Created by Administrator on 2016/3/25.
 */
public class DisplayUtils {
    private static TypedValue mTmpValue = new TypedValue();

    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static int getXmlDef(Context context, int ResId) {
        synchronized (mTmpValue) {
            TypedValue value = mTmpValue;
            context.getResources().getValue(ResId, value, true);
            return (int) TypedValue.complexToFloat(value.data);
        }
    }

    public static float getDimension(Context context, int ResId) {
        return context.getResources().getDimension(ResId);
    }

//    public static ColorStateList getColorStateList(Context context, int ResId) {
//        return ContextCompat.getColorStateList(context, ResId);
//    }
//
//    public static int getColor(Context context, int ResId) {
//        return ContextCompat.getColor(context, ResId);
//    }

    public static String getString(Context context, int ResId) {
        return context.getResources().getString(ResId);
    }

    public static String[] getStringArray(Context context, int ResID) {
        return context.getResources().getStringArray(ResID);
    }

    public static DisplayMetrics getMetrices(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm;
    }

}
