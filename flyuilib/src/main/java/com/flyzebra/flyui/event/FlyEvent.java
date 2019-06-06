package com.flyzebra.flyui.event;

import com.flyzebra.flyui.utils.ByteUtil;
import com.flyzebra.flyui.utils.FlyLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author FlyZebra
 * 2019/3/25 14:36
 * Describ:
 **/
public class FlyEvent {
    private static final List<IFlyEvent> flyuiEvents = new ArrayList<>();
    private static final Map<String, Object> saveKey = new HashMap<>();

    public static FlyEvent getInstance() {
        return FlyEvent.FlyActionHolder.sInstance;
    }

    public static Object saveValue(byte[] key, Object obj) {
        return saveKey.put(ByteUtil.bytes2HexString(key), obj);
    }

    public static Object getValue(byte[] key) {
        return saveKey.get(ByteUtil.bytes2HexString(key));
    }

    public static Object saveValue(String key, Object obj) {
        return saveKey.put(key, obj);
    }

    public static Object getValue(String key) {
        return saveKey.get(key);
    }


    public static void register(IFlyEvent flyuiAction) {
        getInstance().registerMe(flyuiAction);
    }

    public static void register(IFlyEvent flyuiAction, byte[] key) {
        getInstance().registerMe(flyuiAction);
    }

    public static void unregisterAll() {
        flyuiEvents.clear();
    }

    private void registerMe(IFlyEvent flyuiAction) {
        if (flyuiAction != null) {
            synchronized (flyuiEvents) {
                flyuiEvents.add(flyuiAction);
            }
            FlyLog.v("size=%d, add action=" + flyuiAction, flyuiEvents.size());
        }
    }

    public static void unregister(IFlyEvent flyuiAction) {
        getInstance().unregisterMe(flyuiAction);
    }

    private void unregisterMe(IFlyEvent flyuiAction) {
        if (flyuiAction != null) {
            synchronized (flyuiEvents) {
                flyuiEvents.remove(flyuiAction);
            }
            FlyLog.v("size=%d, remove action=" + flyuiAction, flyuiEvents.size());
        }
    }

    private static class FlyActionHolder {
        static final FlyEvent sInstance = new FlyEvent();
    }

    public static void sendEvent(String key) {
        getInstance().sendEventToClient(ByteUtil.hexString2Bytes(key));
    }

    public static void sendEvent(byte[] key) {
        getInstance().sendEventToClient(key);
    }

    public static void sendEvent(String key, Object obj) {
        saveValue(key, obj);
        getInstance().sendEventToClient(ByteUtil.hexString2Bytes(key));
    }

    public static void sendEvent(byte[] key, Object obj) {
        saveValue(key, obj);
        getInstance().sendEventToClient(key);
    }


    private void sendEventToClient(byte[] key) {
        FlyLog.d("send event=%s", ByteUtil.bytes2HexString(key));
        synchronized (flyuiEvents) {
            for (IFlyEvent flyuiAction : flyuiEvents) {
                if (flyuiAction.recvEvent(key)) {
                    break;
                }
            }
        }
    }

    public static void clear() {
        getInstance().clearMap();
    }

    private void clearMap() {
        flyuiEvents.clear();
        saveKey.clear();
    }

}
