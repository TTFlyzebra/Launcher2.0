package com.ppfuns.launcher.utils;

import android.text.TextUtils;

import java.io.UnsupportedEncodingException;

/**
 * Created by lizongyuan on 2017/1/4.
 * E-mail:lizy@ppfuns.com
 */

public class CutStringUtil {

    /**
     * 判断是否是一个中文汉字
     * @param c     字符
     * @return true表示是中文汉字，false表示是英文字母
     * @throws UnsupportedEncodingException
     *             使用了JAVA不支持的编码格式
     */
    public static boolean isChineseChar(char c) {
        // 如果字节数大于1，是汉字
        try {
            return String.valueOf(c).getBytes("GBK").length > 1;
        } catch (Exception e) {
            e.printStackTrace();
            FlyLog.e(e.toString());
        }
        return false;
    }

    public static int length(String text){
        try {
            return text.getBytes("GBK").length;
        }catch (Exception e){
            e.printStackTrace();
            FlyLog.e(e.toString());
        }
        if(!TextUtils.isEmpty(text)){
            return text.length();
        }
        return 0;
    }

    /**
     * 按字节截取字符串
     * @param orignal  原始字符串
     * @param count  截取位数,如果截取位数大于字符串位数，则不进行截取
     * @return 截取后的字符串
     * @throws UnsupportedEncodingException
     *             使用了JAVA不支持的编码格式
     */
    public static String substring(String orignal, int count){
        try {
            if(count >length(orignal)){
                return orignal;
            }
            // 原始字符不为null，也不是空字符串
            if (orignal != null && !"".equals(orignal)) {
                // 将原始字符串转换为UTF-8编码格式
                orignal = new String(orignal.getBytes(), "UTF-8");//
                // 要截取的字节数大于0，且小于原始字符串的字节数
                if (count > 0 && count < orignal.getBytes("UTF-8").length) {
                    StringBuffer buff = new StringBuffer();
                    char c;
                    for (int i = 0; i < count; i++) {
                        c = orignal.charAt(i);
                        FlyLog.d("TemplateUtil",c+" i:"+i+" count:"+count);
                        buff.append(c);
                        if (CutStringUtil.isChineseChar(c)) {
                            // 遇到中文汉字，截取字节总数减1
                            --count;
                        }
                    }
//                    return new String(buff.toString().getBytes("GBK"),"UTF-8");
                    return new String(buff.toString().getBytes(),"UTF-8");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            FlyLog.e(e.toString());
        }
        return orignal;
    }

}
