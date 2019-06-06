package com.flyzebra.ppfunstv.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lizongyuan on 2016/12/28.
 * E-mail:lizy@ppfuns.com
 * 正则表达式相关工具类
 */

public class RegexpUtil {

    /**
     * 判断文本是否符合正则表达式
     * @param text 待匹配文本
     * @param regexp 待匹配正则表达式
     * @return true:匹配
     * 			false:不匹配
     */
    public static boolean isAccept(String text,String regexp){
        try {
            Pattern pattern = Pattern.compile(regexp);
            Matcher match = pattern.matcher(text);
            return match.matches();
        }catch (Exception e){
            e.printStackTrace();
            FlyLog.d(e.toString());
        }
        return false;
    }
}
