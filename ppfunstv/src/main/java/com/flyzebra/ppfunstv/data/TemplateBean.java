package com.flyzebra.ppfunstv.data;

import java.util.List;

/**
 *
 * Created by lenovo on 2016/6/30.
 */
public class TemplateBean {
    int ret;
    String msg;
    String version;
    List<TemplateEntity> template;

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<TemplateEntity> getTemplate() {
        return template;
    }

    public void setTemplate(List<TemplateEntity> template) {
        this.template = template;
    }

    public boolean isValid(){
        if((ret ==0)
                && (msg != null)
                && (template != null)
                && (template.size() > 0)){
            return true;
        }
        return  false;
    }
}
