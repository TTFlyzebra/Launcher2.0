package com.flyzebra.ppfunstv.view.TvView.CellView;

/**
 * Created by 李宗源 on 2016/9/6.
 * E-mail:lizy@ppfuns.com
 */
public interface CellType {

    //1: 点播栏目 2: "点播" 点播详情 3:点播专题 4:直播
    //5:应用(打开应用含游戏) 6:网页(Url)新的窗口显示图片,如崔缴费、促销活动 活动图片 7: 购物
    //8: "资讯" 9.视频广告 10.跑马灯 11.普通文本，显示即可
    //12普通图片，显示即可 13:支付 14:图片广告 15:多乐播购物
    int TYPE_VOD_SUBJECT = 1;
    int TYPE_VOD_DETAIL = 2;
    int TYPE_VOD_CATALOG = 3;
    int TYPE_LIVE = 4;
    int TYPE_APP = 5;
    int TYPE_WEB = 6;
    int TYPE_SHOPPING = 7;
    int TYPE_ADS_VIDEO = 9;
    int TYPE_MESSAGE = 8;
    int TYPE_MARQUEE = 10;
    int TYPE_TEXT = 11;
    int TYPE_IMAGE = 12;
    int TYPE_PAY = 13;
    int TYPE_ADS_IMAGE = 14;
    int TYPE_SHOPPING_BYL = 15;//多乐播购物
    int TYPE_QRCODE = 16;//二维码控件
    int TYPE_ALLIANCE = 17;//阿里控件
    int TYPE_ALL_CATALOG = 18;//所有专辑
    int TYPE_LOGO = 19;//Logo
    int TYPE_RECENT_APP = 20;//最近应用
    int TYPE_REPEAT = 21;//轮播控件
    int TYPE_IRR = 22;//凸头控件
    int TYPE_REPORT = 23;//引用布局
    int TYPE_STATIC_IMAGE = 24;//静态图片(无文字焦点不放大)
    int TYPE_BOTTOMTEXT = 25;//栏目控件2(底部越界控件范围显示两行文字)
    int TYPE_ADS_VIDEO_OUTER = 26;//第三方广告视频控件
    int TYPE_NEW_REPEAT = 27;//竖版轮播控件
    int TYPE_LIST_IMAGE = 28;//竖版列表控件
    int TYPE_CIRCLE_IMAGE = 29;//圆形头像控件
    int TYPE_FORCE_TEXT = 30;//台词鉴赏控件
    int TYPE_STAT_LIST = 31;//图片按钮控件

}
