package com.flyzebra.ppfunstv.module.ads;

/**
 * 作者:lenovo on 2016/6/23.
 * 邮箱:554524787@qq.com
 */
public class DataEmptyException extends Exception {
    public DataEmptyException() {
    }                //用来创建无参数对象

    public DataEmptyException(String message) {        //用来创建指定参数对象
        super(message);
    }
}