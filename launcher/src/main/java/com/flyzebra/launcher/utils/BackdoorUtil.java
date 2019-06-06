package com.flyzebra.launcher.utils;

import android.view.KeyEvent;

/**
 * Created by lizongyuan on 2016/11/1.
 * E-mail:lizy@ppfuns.com
 */

public class BackdoorUtil {

    /**
     * 后门为8次0键(进入后门页面)
     */
    private static int[] mBackDoorKey = new int[]{KeyEvent.KEYCODE_0, KeyEvent.KEYCODE_0, KeyEvent.KEYCODE_0, KeyEvent.KEYCODE_0,
            KeyEvent.KEYCODE_0, KeyEvent.KEYCODE_0, KeyEvent.KEYCODE_0, KeyEvent.KEYCODE_0};
    private static int backDoorKeyIndex = 0;

    /**
     * 检测是否为后门
     */
    public static  boolean checkBackDoorEntry(KeyEvent event) {
        boolean bRet = false;
        if (event.getKeyCode() == mBackDoorKey[backDoorKeyIndex] ) {
            backDoorKeyIndex++;
            if (backDoorKeyIndex == mBackDoorKey.length) {
                backDoorKeyIndex = 0;
                bRet = true;
            }
        } else {
            backDoorKeyIndex = 0;
        }
        return bRet;
    }


    /**
     * 更新后门为8次1键
     */
    private static int[] mUpdateKey = new int[]{KeyEvent.KEYCODE_1, KeyEvent.KEYCODE_1, KeyEvent.KEYCODE_1, KeyEvent.KEYCODE_1,
            KeyEvent.KEYCODE_1, KeyEvent.KEYCODE_1, KeyEvent.KEYCODE_1, KeyEvent.KEYCODE_1};
    private static int updateKeyIndex = 0;
    /**
     * 检测是否为更新后门(8个1)
     */
    public static boolean checkUpdateBackDoor(KeyEvent event){
        boolean bRet = false;
        if (event.getKeyCode() == mUpdateKey[updateKeyIndex]) {
            updateKeyIndex++;
            if (updateKeyIndex == mUpdateKey.length) {
                updateKeyIndex = 0;
                bRet = true;
            }
        } else {
            updateKeyIndex = 0;
        }
//        FlyLog.i(" backdoor: index=" + updateKeyIndex + " isBackDoor:" + bRet + " keyCode:" + event.getKeyCode());
        return bRet;
    }

    public static boolean checkSetPropertyBackDoor(KeyEvent event){
        return checkUpdateBackDoor(event);
    }

    /**
     * 跑马灯为83276600
     */
    private static int[] mMarqueeKey = new int[]{KeyEvent.KEYCODE_8, KeyEvent.KEYCODE_3, KeyEvent.KEYCODE_2, KeyEvent.KEYCODE_7,
            KeyEvent.KEYCODE_6, KeyEvent.KEYCODE_6, KeyEvent.KEYCODE_0, KeyEvent.KEYCODE_0};

    private static int marqueeKeyIndex = 0;
    /**
     * 检测是否为显示跑马灯后门(83276600)
     */
    public static boolean checkMarqueeBackDoor(KeyEvent event){
        boolean bRet = false;
        if (event.getKeyCode() == mMarqueeKey[marqueeKeyIndex]) {
            marqueeKeyIndex++;
            if (marqueeKeyIndex == mMarqueeKey.length) {
                marqueeKeyIndex = 0;
                bRet = true;
            }
        } else {
            marqueeKeyIndex = 0;
        }
        return bRet;
    }

}
