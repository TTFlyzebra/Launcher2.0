package com.flyzebra.ppfunstv.data;

import android.app.ActivityManager;
import android.content.Intent;
import android.graphics.drawable.Drawable;

/**
 * Created by lizongyuan on 2016/11/22.
 * E-mail:lizy@ppfuns.com
 */

public class RecentTag {
    public ActivityManager.RecentTaskInfo recentTaskInfo;
    public Intent intent;
    public String name;
    public Drawable icon;
    public String packageName;

    @Override
    public String toString() {
        return "RecentTag{" +
                "icon=" + icon +
                ", recentTaskInfo=" + recentTaskInfo +
                ", intent=" + intent +
                ", name='" + name + '\'' +
                '}';
    }
}
