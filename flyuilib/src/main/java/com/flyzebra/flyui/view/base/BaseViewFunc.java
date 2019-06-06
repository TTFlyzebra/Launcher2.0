package com.flyzebra.flyui.view.base;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.flyzebra.flyui.bean.RecvBean;
import com.flyzebra.flyui.bean.SendBean;
import com.flyzebra.flyui.event.FlyEvent;
import com.flyzebra.flyui.event.IFlyEvent;
import com.flyzebra.flyui.utils.ByteUtil;
import com.flyzebra.flyui.utils.FlyLog;
import com.flyzebra.flyui.utils.IntentUtil;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Author FlyZebra
 * 2019/5/22 14:26
 * Describ:
 **/
public class BaseViewFunc {
    public static void sendRecvEvent(RecvBean recv, IFlyEvent flyEvent) {
        if (recv != null && !TextUtils.isEmpty(recv.recvId)) {
            flyEvent.recvEvent(ByteUtil.hexString2Bytes(recv.recvId));
        }
    }

    public static void handVisible(View view, byte[] key, RecvBean recv) {
        if (recv != null
                && !TextUtils.isEmpty(recv.recvId)
                && (!TextUtils.isEmpty(recv.visibleContent) || !TextUtils.isEmpty(recv.disVisibleContent))
                && recv.recvId.equals(ByteUtil.bytes2HexString(key))) {
            FlyLog.d("handle event=" + recv.recvId);
            Object obj = FlyEvent.getValue(key);
            if (!TextUtils.isEmpty(recv.disVisibleContent)) {
                String str = null;
                if (obj instanceof byte[]) {
                    str = ByteUtil.bytes2HexString((byte[]) obj);
                } else if (obj instanceof String) {
                    str = (String) obj;
                }
                if (TextUtils.isEmpty(str)) {
                    view.setVisibility(VISIBLE);
                } else {
                    if (recv.disVisibleContent.contains(str)) {
                        view.setVisibility(GONE);
                    } else {
                        view.setVisibility(VISIBLE);
                    }
                }
            } else if (!TextUtils.isEmpty(recv.visibleContent)) {
                String str = null;
                if (obj instanceof byte[]) {
                    str = ByteUtil.bytes2HexString((byte[]) obj);
                } else if (obj instanceof String) {
                    str = (String) obj;
                }
                if (TextUtils.isEmpty(str)) {
                    view.setVisibility(GONE);
                } else {
                    if (recv.visibleContent.contains(str)) {
                        view.setVisibility(VISIBLE);
                    } else {
                        view.setVisibility(GONE);
                    }
                }
            }
            FlyLog.d("finish handle event=" + recv.recvId);
        }
    }

    public static void onClick(Context context,SendBean send) {
        if (send == null) {
            return;
        }
        if (!TextUtils.isEmpty(send.eventId)) {
            if(!TextUtils.isEmpty(send.eventContent)){
                FlyEvent.sendEvent(send.eventId,send.eventContent);
            }else{
                FlyEvent.sendEvent(send.eventId);
            }
        } else if (IntentUtil.execStartPackage(context, send.packName, send.className)) {
        } else if (IntentUtil.execStartPackage(context, send.packName)) {
        }
    }
}
