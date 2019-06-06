package com.flyzebra.ppfunstv.constant;

/**
 * Created by 李宗源 on 2016/9/20.
 * E-mail:lizy@ppfuns.com
 * 请查阅文档192.168.1.88\2-聚OS开发部\03.项目开发\聚OS\第三方业务\银河\银河小窗口播放接口.pdf
 */
public class YinheConstants {

    /**
     *播放器的错误状态
     * 0 正常播放
     * 1 无信号
     * 2 未插卡
     * 3 无授权
     * 4 本地播放失败
     * 5 信号已恢复
     * 6 卡插入
     * 7 已授权
     * 8 本地无节目请搜索
     * 9 无指定节目
     */
    public static final int STATUS_OK = 0;
    public static final int STATUS_NO_SIGNLE = 1;
    public static final int STATUS_NO_CARD = 2;
    public static final int STATUS_ONT_AUTH = 3;
    public static final int STATUS_PLAY_FAIL = 4;
    public static final int STATUS_SIGNLE_OK = 5;
    public static final int STATUS_CARD_IN = 6;
    public static final int STATUS_AUTH = 7;
    public static final int STATUS_NO_PROGRAM = 8;
    public static final int STATUS_NO_ASSIGN_PROGRAM = 9;


    public static final int TYPE_DEFAULT_FIRST = 1;//首先播放默认,若无默认播放指定频道
    public static final int TYPE_ASSIGN_FIRST = 2;//首先播放指定频道,若无指定频道则播放默认频道
    public static final int TYPE_DEFAULT_ONLY = 3;//只播放默认
    public static final int TYPE_ASSIGN_ONLY = 4;//只播放指定


    public static final String PARA_SID = "serviceId";
    public static final String PARA_TSID = "tsId";
    public static final String PARA_ONID = "onId";

}
