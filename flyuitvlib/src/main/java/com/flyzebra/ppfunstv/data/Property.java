package com.flyzebra.ppfunstv.data;

/**
 * Created by flyzebra on 17-4-10.
 */
public interface Property{
    String YINHE_CA = "persist.sys.ca.card_id";
    String PPFUNS_CA = "sys.device.ca";//临时加persist,后期需去除
    String PPFUNS_CA_OTHER = "persist.sys.device.ca";
    String PPFUNS_MAC  = "sys.device.mac";//
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
}