package com.ppfuns.ppfunstv.module.ads;

/**
 * 作者:lenovo on 2016/6/23.
 * 邮箱:554524787@qq.com
 */
public class ServiceNotExistException extends RuntimeException {
    public ServiceNotExistException() {
    }                //用来创建无参数对象

    public ServiceNotExistException(String message) {        //用来创建指定参数对象
        super(message);                             //调用超类构造器
    }
}
