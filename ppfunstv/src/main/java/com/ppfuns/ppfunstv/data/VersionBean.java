package com.ppfuns.ppfunstv.data;

/**
 * Created by 李宗源 on 2016/8/22.
 * E-mail:lizy@ppfuns.com
 */
public class VersionBean {


    /**
     * msg : 成功
     * ret : 0
     * version : 1471836135279
     * versionInterval : 10000000 版本更新间隔
     */

    private String msg;
    private int ret;
    private String version;
    private int versionInterval;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getVersionInterval() {
        return versionInterval;
    }

    public void setVersionInterval(int versionInterval) {
        this.versionInterval = versionInterval;
    }
    public boolean isValid(){
        if(ret ==0 && msg !=null){
            return true;
        }
        return  false;
    }
}
