package com.flyzebra.flyui.utils;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by FlyZebra on 2016/4/23.
 */
public class GsonUtil {
    private static Gson gson = null;
    static {
        if(gson == null){
            gson = new Gson();
        }
    }

    public static <T> T json2Object(String jsonStr, Class<T> cls){
        if(TextUtils.isEmpty(jsonStr)){
            return null;
        }
        try {
            return gson.fromJson(jsonStr,cls);
        } catch (JsonSyntaxException e) {
            FlyLog.e(e.toString());
            return null;
        }
    }

    public static <T> List<T> json2ListObject(String jsonStr, Class<T> cls){
        if(TextUtils.isEmpty(jsonStr)){
            return null;
        }
        try {
            return gson.fromJson(jsonStr, new ListOfJson(cls));
        } catch (Exception e) {
            FlyLog.e(e.toString());
            return null;
        }
    }

    public static class ListOfJson<T> implements ParameterizedType {
        private Class<?> wrapped;
        public ListOfJson(Class<T> wrapper) {
            this.wrapped = wrapper;
        }
        @Override
        public Type[] getActualTypeArguments() {
            return new Type[]{wrapped};
        }
        @Override
        public Type getRawType() {
            return List.class;
        }
        @Override
        public Type getOwnerType() {
            return null;
        }
    }

    public static Map json2Map(String json){
        return json2Object(json,Map.class);
    }

    public static HashMap json2HashMap(String json){
        return json2Object(json,HashMap.class);
    }

    public static <T> String mapToJson(Map<String, T> map) {
        try{
            return gson.toJson(map);
        }catch (JsonSyntaxException e){
            FlyLog.d(e.toString());
            return null;
        }
    }

}
