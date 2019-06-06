package com.flyzebra.launcher.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.lang.reflect.Field;

/**
 *内存泄漏处理
 * Created by FlyZebra on 2016/6/26.
 */
public class MemoryManager {

    /**
     * 处理InputMethodManage内存泄漏
     * @param context
     */

    public static void fixInputMethodManagerLeak(Context context) {
        if (context == null) {
            return;
        }

        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) {
            return;
        }
        String [] arr = new String[]{"mCurRootView", "mServedView", "mNextServedView"};
        Field f = null;
        Object obj_get = null;
        for (int i = 0;i < arr.length;i ++) {
            String param = arr[i];
            try{
                f = imm.getClass().getDeclaredField(param);
                if (f.isAccessible() == false) {
                    f.setAccessible(true);
                }
                obj_get = f.get(imm);
                if (obj_get != null && obj_get instanceof View) {
                    View v_get = (View) obj_get;
                    if (v_get.getContext() == context) {
                        // 被InputMethodManager持有引用的context是想要目标销毁的 置空，破坏掉path to gc节点
                        f.set(imm, null);
                    } else {
                        // 不是想要目标销毁的，即为又进了另一层界面了，不要处理，避免影响原逻辑,也就不用继续for循环了
                        break;
                    }
                }
            }catch(Throwable t){
                t.printStackTrace();
            }
        }
    }
}
