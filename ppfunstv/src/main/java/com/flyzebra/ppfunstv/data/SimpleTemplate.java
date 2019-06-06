package com.flyzebra.ppfunstv.data;

/**
 * Created by lizongyuan on 2016/11/17.
 * E-mail:lizy@ppfuns.com
 * 简单模板实体，用于同第三方应用交互
 */

public class SimpleTemplate {
    /**
     * 模板名称
     */
    public String name;

    /**
     * 是否为选中模板
     */
    public boolean bSelected;

    public SimpleTemplate(String name, boolean bSelected) {
        this.name = name;
        this.bSelected = bSelected;
    }

    public SimpleTemplate(){

    }

    @Override
    public String toString() {
        return "SimpleTemplate{" +
                "bSelected=" + bSelected +
                ", name='" + name + '\'' +
                '}';
    }
}
