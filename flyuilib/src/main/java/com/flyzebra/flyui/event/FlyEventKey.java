package com.flyzebra.flyui.event;

/**
 * Author FlyZebra
 * 2019/4/2 15:17
 * Describ:
 **/
public interface FlyEventKey {
    int REFRESH = 9000;
    int CHANGE_PAGER_WITH_RESID = 1018;
    int GROUP_TYPE =  9001;
    int IS_SELECT = 9002;
    int KEY_HIDE = 1001;//执行隐藏动画

    int KEY_PLAY = 1009;//播放暂停
    int KEY_NEXT = 1010;//播放下一首
    int KEY_PREV = 1011;//插放上一首
    int KEY_SEEK = 1013;//跳转到指定时间播放
    int KEY_MENU = 1014;//菜单按键
    int KEY_URL = 1015;//播放文件
    int KEY_LOOP = 1016;//切换播放状态
    int KEY_STORE = 1017;//选择存储器
    int KEY_ZOOM = 1018;//放大图片

    int MSG_PLAY_STATUS = 2001;//播放状态
    int MSG_MENU_STATUS = 2002;//菜单状态
    int MSG_LOOP_STATUS = 2004;//循环状态

    int MUSIC_NAME = 3007;//音乐歌名
    int MUSIC_ALBUM = 3008;//音乐专辑名
    int MUSIC_ARTIST = 3009;//音乐艺术家
    int MUSIC_TIME = 3010;//音乐播放时间
    int MUSIC_TEXT = 3013;//音乐歌词
    int MUSIC_URL = 3015;//音乐播放地址
    int MUSIC_LIST = 3014;//音乐播放列表
    int MUSIC_LIST_FOLDER = 3028;//音乐文件夹列表
    int MUSIC_LIST_ALBUM = 3029;//音乐专辑分类放列表
    int MUSIC_LIST_ARTIST = 3030;//音乐歌手分类列表

    int STORE_LIST = 3017;//存储器列表
    int STORE_URL = 3018;//存储器地址
    int STORE_NAME = 3020;//存储器名称
    int RES_URL = 3019;//存储器图片资源

    int VIDEO_NAME = 3021;//视频文件名称
    int VIDEO_URL = 3022;//视频文件地址
    int VIDEO_LIST = 3023;//视频播放列表

    int IMAGE_NAME = 3024;//图片文件名称
    int IMAGE_URL = 3025;//图片文件地址
    int IMAGE_LIST = 3026;//图片播放列表
    int IMAGE_LIST_FOLDER = 3027;//图片文件夹列表

    int FOLODER_NAME = 3034;//文件夹名
    int FOLODER_PATH = 3035;//文件夹路径
    int FOLODER_NUM = 3036;//子项目统计数

    int MUSIC_SUM_FOLDER = 3031;//音乐文件夹数量
    int MUSIC_SUM_ALBUM = 3032;//音乐专辑数量
    int MUSIC_SUM_ARTIST = 3033;//音乐歌手数量
    int SUM_STORE = 3037;//存储器数量
    int MUSIC_SUM = 3038;//音乐数量
}
