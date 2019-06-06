package com.flyzebra.ppfunstv.view.TvView.CellView.CellClickAction;


import android.content.Intent;

import com.flyzebra.ppfunstv.utils.FlyLog;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lizongyuan on 2017/2/24.
 * E-mail:lizy@ppfuns.com
 */

public abstract class BaseAction implements IClickEvent {

    Map<String, String> mobPara = new HashMap<>();
    /**
     *  子类重写doActionImpl即可，默认对其添加try异常处理，避免前端配置导致应用异常崩溃
     */
    @Override
    public final void doAction() {
        doAction(Intent.FLAG_ACTIVITY_NEW_TASK
        );
    }

    @Override
    public final void doAction(int flag) {
        try {
            mobPara.clear();
            doActionImpl(flag);
        }catch (Exception e){
            e.printStackTrace();
            FlyLog.e(e.toString());
        }
    }

    /**
     *  doAction实现方式，实现各种
     */
    protected abstract void doActionImpl(int flag);



}
