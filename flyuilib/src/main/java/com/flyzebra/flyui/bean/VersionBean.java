package com.flyzebra.flyui.bean;

/**
 * Author FlyZebra
 * 2018/12/25 15:01
 * Describ:
 **/
public class VersionBean {


    /**
     * msg : 成功
     * ret : 0
     * version : 1471836135279
     * versionInterval : 10000000 版本更新间隔
     */

    public String msg;
    public int ret;
    public String version;
    public int versionInterval;

    public boolean isValid() {
        if (ret == 0 && msg != null) {
            return true;
        }
        return false;
    }
}
