package com.flyzebra.flyui.utils;

import android.os.Bundle;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 将字符串参数的解析类
 * 约定格式:  "count=(int)1#name=xiaohei"
 * "#":不同参数的分隔符
 * ":":键/值的分隔符
 * <p/>
 * (int)/(float)/(map)/(array):值的类型标记
 * 如:
 * (int)1 => 整型数值1,
 * (float)1.0 => 浮点数值1.0
 * (map)[count:(int)1#name:xiaohei] => map数值
 * <p/>
 * Created by lenovo on 2016/6/24.
 */
public class ParamParseUtil {
    private final static String TAG = ParamParseUtil.class.getSimpleName();
    private final static String DEFAULT_PARAM_DIVIDER = "#";
    private final static String DEFAULT_VALUE_DIVIDER = "=";

    /**
     * A:1#B:1
     *
     * @param src
     * @return
     */
    public static Map<String, Object> parseMap(String src, String paramDivider, String valueDivider) {
        HashMap<String, Object> map = new HashMap<>();
        try {
            if (TextUtils.isEmpty(src)) {
                return map;
            }
            for (String str : src.split(paramDivider)) {
                String[] split = str.split(valueDivider);
                String key = null;
                String strValue = null;
                if(split.length > 2){
                    key = split[0].trim();
                    if(str.length() > split[0].length()+1){
                        strValue = str.substring(split[0].length()+1);
                    }
                }
                if (split.length == 2) {
                    key = split[0].trim();
                    strValue = split[1].trim();
                }

                if (strValue != null && isInt(strValue)) {
                    map.put(key,parseToInt(strValue));
                } else if (strValue != null &&isFloat(strValue)) {
                    map.put(key,parseToFloat(strValue));
                } else if (strValue != null && isMap(strValue)) {
                    map.put(key,parseToMap(strValue));
                }else if(strValue != null){
                    map.put(key, strValue);
                }

            }
        }catch (Exception e){
            FlyLog.e(e.toString());
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 默认以#分割参数,以:分割值
     * @param src
     * @return
     */
    public static Map<String, Object> parseMap(String src){
        return parseMap(src,DEFAULT_PARAM_DIVIDER,DEFAULT_VALUE_DIVIDER);
    }

    /**
     * 默认以#分割参数,以:分割值
     * @param src
     * @return
     */
    public static Bundle parseBundle(String src){
        return parseBundle(src,DEFAULT_PARAM_DIVIDER,DEFAULT_VALUE_DIVIDER);
    }

    /**
     * A:1#B:1
     *
     * @param src
     * @return
     */
    public static Bundle parseBundle(String src, String paramDivider, String valueDivider) {
        Bundle bundle = new Bundle();
        try {
            if (TextUtils.isEmpty(src)) {
                return null;
            }
            for (String str : src.split(paramDivider)) {
                String[] split = str.split(valueDivider);
                String key = null;
                String strValue = null;
                if(split.length > 2){
                    key = split[0].trim();
                    if(str.length() > split[0].length()+1){
                        strValue = str.substring(split[0].length()+1);
                    }
                }
                if (split.length == 2) {
                    key = split[0].trim();
                    strValue = split[1].trim();
                }
                if (strValue != null && isInt(strValue)) {
                    bundle.putInt(key, parseToInt(strValue));
                    continue;
                } else if (strValue != null &&isFloat(strValue)) {
                    bundle.putFloat(key, parseToFloat(strValue));
                    continue;
                } else if (isBoolean(strValue)) {
                    bundle.putBoolean(key, parseToBoolean(strValue));
                }else if (strValue != null &&isMap(strValue)) {
                    bundle.putBundle(key, parseToBundle(strValue));
                } else if(strValue != null){
                    bundle.putString(key, strValue);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            FlyLog.e(e.toString());
        }

        return bundle;
    }

    private static boolean isInt(String s) {
        return s.startsWith("(int)");
    }


    private static boolean isFloat(String s) {
        return s.startsWith("(float)");
    }

    private static boolean isMap(String s) {
        return s.startsWith("(map)");
    }

    private static boolean isBoolean(String s) {
        return !TextUtils.isEmpty(s) && s.startsWith("(boolean)");
    }


    private static Boolean parseToBoolean(String s) {
        if (s.startsWith("(boolean)")) {
            try {

                String replace = s.replace("(boolean)", "").trim();
                return Boolean.parseBoolean(replace);
            } catch (Exception e) {
                FlyLog.d(" 不是boolean类型的值");
                return false;
            }
        } else {
//            throw new RuntimeException(s + " 不是float类型的值");
            FlyLog.d(" 不是boolean类型的值");
            return false;
        }
    }

    private static int parseToInt(String s) {
        if (s.startsWith("(int)")) {
            try {
                String replace = s.replace("(int)", "");
                return Integer.parseInt(replace);
            }catch (Exception e){
                FlyLog.d(" 不是int类型的值");
                return 0;
            }
        } else {
//            throw new RuntimeException(s + " 不是int类型的值");
            FlyLog.d(" 不是int类型的值");
            return 0;
        }
    }

    private static float parseToFloat(String s) {
        if (s.startsWith("(float)")) {
            try {
                String replace = s.replace("(float)", "").trim();
                return Float.parseFloat(replace);
            }catch (Exception e){
                FlyLog.d(" 不是float类型的值");
                return 0;
            }
        } else {
//            throw new RuntimeException(s + " 不是float类型的值");
            FlyLog.d(" 不是float类型的值");
            return 0;
        }
    }

    private static Map parseToMap(String s) {
        if (s.startsWith("(map)")) {
            try{
                String replace = s.replace("(map)", "").trim();
                if (replace.startsWith("[") && replace.endsWith("]")) {
                    replace = replace.substring(1, replace.length() - 1).trim();
                    return parseMap(replace, "@", ">");
                } else {
//                throw new RuntimeException(s + " 值的部分的格式书写错误");
                    FlyLog.e(" 值的部分的格式书写错误\"");
                }
            }catch (Exception e){
                FlyLog.e(" 值的部分的格式书写错误\"");
            }

        } else {
//            throw new RuntimeException(s + " 不是float类型的值");
            FlyLog.d(" 不是float类型的值");
        }
        return null;
    }

    private static Bundle parseToBundle(String s) {
        if (s.startsWith("(map)")) {
            try{
                String replace = s.replace("(map)", "").trim();
                if (replace.startsWith("[") && replace.endsWith("]")) {
                    replace = replace.substring(1, replace.length()).trim();
                    return parseBundle(replace, "@", ">");
                } else {
//                throw new RuntimeException(s + " 值的部分的格式书写错误");
                    FlyLog.d(" 值的部分的格式书写错误");
                }
            }catch (Exception e){
                FlyLog.d(" 值的部分的格式书写错误");
            }
        } else {
//            throw new RuntimeException(s + " 不是float类型的值");
            FlyLog.d(" 不是float类型的值");
        }
        return null;
    }


}
