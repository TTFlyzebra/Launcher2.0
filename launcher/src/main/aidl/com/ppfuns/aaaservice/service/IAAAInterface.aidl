package com.ppfuns.aaaservice.service;

import com.ppfuns.aaaservice.service.IPropertyListener;

interface IAAAInterface {
    /**
    *
    * DEVICE_UNAUTHORIZED = 0x01;         //设备未鉴权
    * DEVICE_ILLEGAL = 0x02;              //非法设备
    * USER_UNAVAILABLE = 0x03;            //用户不可用
    * USER_AVAILABLE = 0x04;              //用户可用
    * USER_ARREARS = 0x05;                //存在欠费的业务，但是用户可用
    *
    * */
    int getAuthStatus();


    /**
    *
    * 传入包名
    * true：该包可用，不在黑名单中；
    * false：黑名单应用，不可用；
    *
    * */
    boolean isAllowed (String packageName);


    /**
    *
    * 认证对各类业务进行认证
    * 数据以json格式传输:
    *
    * param: authInfo
    *   {
    *       "type":"1",
    *       "data":"data"
    *   }
    *
    *   type : 认证的业务类型
	*	    1 - 业务认证
    *
	*   data ： 对应认证类型的数据
    *
    * return:
    *   {
    *      "code":"1",
    *      "msg":"test"
    *   }
    *,result = null
    *   code: 认证结果
    *       1 - 认证通过
    *       2 - 认证不通过
    *       3 - 认证失败，没有网络或者本地缓存数据，无法进行认证
    *       4 - 无效认证，认证类型超出定义
    *
    *
	*   msg： 认证结果的附加信息
    *
    * */
    String auth(String authInfo);

    /*
    *
    * 获取由AAA存储的属性
    * params:
    *       pKey: 查询key值
    *
    * return: 返回json字符串,
    *       {
    *         code:1,//状态:
    *                   1:成功
    *                   2:服务未获取此属性
    *                   3:服务获取此属性失败
    *                   4:服务正在获取此属性
    *         result:"aaaaa" //不保证最新
    *       }
    * ppfuns_token
    *
    */
    String query(String pKey);

    /*
    * 注册属性监听
    *   params:
    *       pProp: 需监听的属性;
    *       pListner: 监听器
    */
    void registerPropertyListener(String pProp, IPropertyListener pListener);

    /*
    * 取消属性监听
    */
    void unregisterPropertyListener(String pProp);
}
