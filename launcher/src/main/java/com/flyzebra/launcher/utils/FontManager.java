package com.flyzebra.launcher.utils;

import android.content.Context;
import android.graphics.Typeface;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 字体管理,限定每个FontFamily只拿到同一个Typeface实例
 * Created by lenovo on 2016/6/21.
 */
public class FontManager {
    private static final String FONT_SYSTEM_PATH = "/system/fonts/";
    private static HashMap<String, Typeface> TypefaceMap = new HashMap<String, Typeface>();
    private static List<String> fonts = new ArrayList<>();//

    public static Typeface getTypefaceByFontName(Context context, String name){
        if (TypefaceMap.containsKey(name)) {
            return TypefaceMap.get(name);
        } else if(isExistFont(name)){
            Typeface tf = Typeface.createFromFile(FONT_SYSTEM_PATH + name);
            TypefaceMap.put(name, tf);
            return tf;
        }else{//返回默认字体
            return Typeface.DEFAULT;
        }
    }


    static {
        fonts.clear();
        File baseFile = new File(FONT_SYSTEM_PATH);
        FlyLog.d("init fonts name....");
        if(baseFile.isDirectory()){
            File[] files = baseFile.listFiles();
            for(File file : files){
                FlyLog.d("list fonts name:"+file.getName());
                fonts.add(file.getName());
            }
        }
    }

    /**
     * 判断该字体在系统中是否存在
     * @param name 字体名(以.ttf结尾)
     * @return true :系统中存在该字体
     *          false:系统中不存在该字体
     */
    private static boolean isExistFont(String name){
        for(String item :fonts){
            if(item.equals(name)){
               return true;
            }
        }
        return false;
    }
}
