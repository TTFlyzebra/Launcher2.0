package com.flyzebra.launcher.constant;

/**
 * Created by lenovo on 2016/6/22.
 */
public class Constants {

    /**
     * 本应用采用的默认分辨率
     */
    public static float BASE_SCREEN_PX = 1920;

    /**
     * 默认更新间隔(15分钟)
     */
    public static int UPDATE_IINTERVAL = 15 * 60 * 1000;//3 * 60 *

    /**
     * 一分钟
     */
    public static final int ONE_MINUTE = 60 * 1000;

    /**
     * 一小时
     */
    public static final int ONE_HOUR = 60 * ONE_MINUTE;

    /**
     * 进制转换基数(G->M->b)
     */
    public static final double FORMAT_MEMORY_BASE = 1024.0;

    /**
     * 加载页面内存调整因子
     */
    public static final double ADJUST_MEMORY_FACTOR = 3 / 4.0;

    /**
     * glide加载图片时图片格式所占的大小
     */
    public static int GLIDE_IMAGE_BYTES_PER_PIXEL = 2;

    /**
     * 前端可配置默认最小更新时间(10s)
     */
    public static int UPDATE_MIN_IINTERVAL = 10 * 1000;

    public static final String DEFAULT_WEB_DATA_PATH = "ppfuns/";
    /**
     * 默认asset根目录
     */
    public static final String DEFAULT_ASSET_PATH = "file:///android_asset/ppfuns/";

    /**
     * 3A鉴权相关
     */
    public static final String AAA_PACKAGE_NAME = "com.flyzebra.aaaservice";
    public static final String AAA_SERVICE = "com.flyzebra.aaaservice.service.AAAService";
    public static final String AAA_TOKEN_QUERY_KEY = "ppfuns_token";
    /**
     * 直播服务
     */
    public static final String PLAY_SV_ACTION = "com.flyzebra.action.PlayerService";
    public static final String PALY_SV_PKNAME = "com.flyzebra.playerservice";

    /**
     * 推送服务
     */
    public static final String MESSAGE_PACKAGE_NAME = "com.flyzebra.messageservice";
    public static final String MESSAGE_ACTIVITY_NAME = "com.flyzebra.messageservice.service.MessageService";


    public static final String TIME_DAY_START = "00:00";//凌晨零点
    public static final String API_STORE_KEY = "397f49a99f26ea5974580f5015bfca08";//api store key
    public static final String API_STORE_KEY_NAME = "apikey";
    public static final String API_STORE_TAG_AREA = "api_store_tag_area";
    public static final String API_STORE_TAG_TEMPERATURE = "api_store_tag_temperature";


    public static final String LOGO_PATH_DEFAULT = "/data/data/logo.png";


    /**
     * 系统属性相关常量
     */
    public interface Property {
        String YINHE_CA = "persist.sys.ca.card_id";
        String PPFUNS_CA = "sys.device.ca";//临时加persist,后期需去除
        String PPFUNS_CA_OTHER = "persist.sys.device.ca";
        String PPFUNS_MAC = "sys.device.mac";//
        String YINHE_MAC = "persist.sys.net.mac";
        String AREA_NAME = "persist.sys.areaname";
        String USER_ID = "sys.platform.userid";//用户ID
        String DEBUG = "persist.sys.debug.open";//debug模式
        String TEMPLATE_NAMES = "sys.launcher.template.names";
        String TEMPLATE_IDS = "sys.launcher.template.ids";
        String YINHE_SN = "persist.sys.hwconfig.stb_id";
        String PPFUNS_SN = "sys.device.sn";
        String ANDROID_SN = "ro.serialno";
        String UID = "sys.platform.userid";
        String URL_BASE = "persist.sys.launcher.base.url";
        String LOGO_PATH = "persist.sys.launcher.logo.path";
        String AREA_CODE = "persist.sys.osupdate.areacode";
        String LAUNCHER_VERSION = "persist.sys.launcher.version";
        String LAUNCHER_TIME = "persist.sys.launcher.time";
        String SWV = "ro.build.version.release";//软件版本号
        String HWV = "sys.device.hwv";//硬件版本号
        String DEVICE_CODE = "sys.device.name";
        String FRIENDLY_NAME = "sys.device.friendlyName";
        String SHOW_WEATHER = "persist.sys.show.weather";
        String BACKDOOR_SWITCH = "persist.sys.backdoor.open";
        String DIALOG_BLUR = "persist.sys.dialog.blur";
    }

    public static final String DEFAULT_AREACODE = "101010100";
    public static final String DEFAULT_LAUNCHER_VERSION = "v1.0.0";
    public static boolean IS_LAUNCHER_ON_TOP = true;//launcher是否在顶层

    public static final double MIN_TEXT_SIZE = 15;

    /**
     * Action相关常量
     */
    public interface Action {
        String BACK_DOOR = "com.flyzebra.launcher.BACK_DOOR";//后门action
        String STUTA_SET = "com.flyzebra.launcher.statu.setting";//状态栏action
        String VOD_SEARCH = "com.flyzebra.vod.action.ACTION_VIEW_SEARCH";//点播搜索action
        String MYAPP = "com.flyzebra.store.action.ACTION_VIEW_MYAPP";//我的应用action
        String HTML_DETAIL = "com.flyzebra.launcher.HTML_DETAIL";//网页action
        String PAY_DETAIL = "com.flyzebra.pay.action.ACTION_VIEW_PAY";//支付action
        String CHANGE_TEMPLATE = "com.flyzebra.launcher.change.template";//切换模板action
        String SPEECH_SWITCH_UI = "com.flyzebra.launcher.switchui";//语言切换模板
        String DLNA_SERVICE_STARTED = "com.flyzebra.dlnaservice.DLNA_SERVICE_STARTED";//DLNA服务
        String CHILD_CHANGE_UI = "com.flyzebra.child.ui.change";//儿童版内部切换UI
        String MESSAGE_DATA_CHANGE = "com.flyzebra.message.data.change";//消息中心数据变化
        String CHILD_UI_SET = "com.flyzebra.action.ui.set";//设置儿童版UI界面
        String WATCH_HISTORY = "com.flyzebra.vod.action.ACTION_VIEW_RECORD";//观看历史
        String PAY_HISTORY = "com.flyzebra.pay.action.RECORD_DETAIL";//消费记录
        String TIME_THEME = "com.flyzebra.launcher.my.time.theme";//时段主题
        String MY_INFO = "com.flyzebra.launcher.my.info";
        String OPEN_DEBUG = "com.flyzebra.opendebug";//打开debug
        String CLOSE_DEBUG = "com.flyzebra.dimissdebug";//关闭debug
        String RECENT_APP = "com.flyzebra.action.recent.app";
        String CHECK_VERSION = "com.flyzebra.debug.VERSION_CHECK";
    }

    public interface Product {
        String HENGYANG = "hengyang";
        String ALLIANCE = "alliance";
    }

    public static final String line = "\\|";

    /**
     * action对应的参数名称常量
     */
    public static final String KEY_TEMPLATE_ID = "template_id";//模板ID
    public static final String KEY_TEMPLATE_NAME = "template_name";//模板名称

    public static final String PACKAGE_LIVE = "com.flyzebra.live";
    public static final String PACKAGE_FILE_MANAGER = "com.flyzebra.filemanager";

    public static boolean IS_HOME_KEY = false;
    public static Integer RECENT_APP_INDEX = 0;

    /**
     * alpha值相关常量
     */
    public interface Alpha {
//        float cellFocus = 1.0f;
//        float cellNoFocus = 1.0f;

        float tabFocus = 1.0f;
        float tabNoFocus = 0.8f;
    }

    public static float cellFocus = 1.0f;
    public static float cellNoFocus = 1.0f;

    public static final String ADS_FILE_PATH = "/data/data/com.flyzebra.adservice/files/";
    public static final int DELAY_UPDATE_VERSION_TIME = 3 * ONE_MINUTE;

    public static final String UPDATE_PACKAGE = "com.yinhe.csguoan.update";//升级apk包名
    public static final String UPDATE_CLASS = "com.yinhe.csguoan.update.test.MainTest";//升级apk类名
    public static final String PACKAGE_PROPERTY_SET = "com.flyzebra.propertyset";//设置属性apk包名
}
