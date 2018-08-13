package com.ppfuns.ppfunstv.constant;


/**
 * Created by lizongyuan on 2016/9/29.
 * 请查阅文档192.168.1.88\2-聚OS开发部\03.项目开发\聚OS\第三方业务\银河\湖南有线APK设置接口V2.1.pdf
 */
public class NetConstants {

    /**
     * 湖南有线网络相关常量
     */
    public static final String URL_BOAFRM = "http://192.168.88.254/boafrm/get_parameter";
    public static final String URL_BOAFRM_BACK_UP = "http://192.168.89.254/boafrm/get_parameter";

    public static final String URL_REGEXP = "http://192.168.*/boafrm/get_parameter";
    /**
     * 有线wan 连接状态
     * 0 : [WISP 模式] : 表示未连接
     *     [GW 模式DHCP] : 表示正在连接
     *     [GW 模式PPPOE] : 表示正在连接或连接已失败(伴随连接错误码)；
     * 1：连接成功，
     * 2：[wisp 模式] : 连接中
     */
    public static final String WAN_CONNECT_STATE = "wan_connect_state";

    /**
     * 连接命令(1 : 连接；2：断开）
     *
     */
    public static final String WAN_CONNECT_CMD = "wan_connect_cmd";

    /**
     * 操作模式(0:gateway ; 1 : bridge ; 2 : ap client)
     */
    public static final String OPERATION_MODE = "operation_mode";

    /**
     * PPPOE 连接错误码
     */
    public static final String WAN_PPPOE_ERROR_CODE = "wan_pppoe_error_code";

    /**
     * pppoe 用户名
     */
    public static final String WAN_PPPOE_USER = "wan_pppoe_user";

    /**
     * pppoe 密码
     */
    public static final String WAN_PPPOE_PASS = "wan_pppoe_pass";

    /**
     * WAN 口模式(DHCP | STATIC | PPPOE)
     */
    public static final String WAN_CONNECTION_MOD = "wan_connection_mode";

    public static final String WAN_IPADDR = "wan_ipaddr";
    public static final String WAN_NETMASK = "wan_netmask";
    public static final String WAN_GATEWAY = "wan_gateway";
    public static final String WAN_PRIMARY_DNS = "wan_primary_dns";
    public static final String WAN_SECONDARY_DNS = "wan_secondary_dns";
    public static final String WAN_MAC_ADDR = "wan_mac_addr";
    public static final String LAN_IPADDR = "lan_ipaddr";
    public static final String LAN_NETMASK = "lan_netmask";
    public static final String LAN_GATEWAY = "lan_gateway";
    public static final String LAN_MAC_ADDR = "lan_mac_addr";

    /**
     * wisp 模式下ssid 连接状态(0:ap client 未连接；1：ap client 已连接)
     */
    public static final String WISP_CONNECT_STATE = "wisp_connect_state";

    /**
     * AP 名称
     */
    public static final String WISP_AP_NAME = "wisp_ap_name";

    /**
     * AP 加密模式(OPEN | WPA | WPA2 | WPAWPA2)
     */
    public static final String WISP_AP_SECURITY_MODE = "wisp_ap_security_mode";

    /**
     * AP 加密方(AES | TKIP)
     */
    public static final String WISP_AP_WPA_CIPHER_SUITE = "wisp_ap_wpa_cipher_suite";

    /**
     * AP 的密码
     */
    public static final String WISP_AP_PASSWORD = "wisp_ap_password";

    /**
     * (ap 信号强度)
     */
    public static final String WISP_AP_SIGNAL_STRENGTH = "wisp_ap_signal_strength";

    /**
     * AP 的BSSID
     */
    public static final String AP_CLI_BSSID = "ApCliBssid";

    /**
     * ssid 运行状态(0:未开启；1：已开启)
     */
    public static final String WLAN_SSID_OPERATION_STATE = "wlan_ssid_operation_state";

    public static final String WLAN_SSID_NAME = "wlan_ssid_name";
    public static final String WLAN_SSID_PASSWORD = "wlan_ssid_password";

    /**
     *  (OPEN | WPA | WPA2 | WPAWPA2)
     */
    public static final String WLAN_SSID_SECURITY_MODE = "wlan_ssid_security_mode";

    /**
     *  (AES | TKIP)
     */
    public static final String WLAN_SSID_WPA_CLIPHER_SUITE = "wlan_ssid_wpa_cipher_suite";


    //===============以下为请求参数================================================================//

    public static final String PARA_DATA = "data";
    public static final String PARA_START = "[";
    public static final String PARA_END = "]";
    public static final String PARA_DIV = ",";

    //    operation_mode,
    //    wan_connection_mode，
    //    wisp_connect_state,
    //    wisp_ap_name,
    //    wisp_ap_password,
    //    wisp_ap_security_mode,
    //    wisp_ap_signal_strength,
    //    wan_connect_state，
    //    wan_ipaddr，
    //    wan_netmask，
    //    wan_gateway，
    //    wan_primary_dns，
    //    wan_secondary_dns,
    //    ApCliBssid,
    //    wisp_ap_wpa_cipher_suite
    public static final String PARA_WIFI_REQUEST = PARA_START + OPERATION_MODE+ PARA_DIV
            +WAN_CONNECTION_MOD + PARA_DIV
            +WISP_CONNECT_STATE+PARA_DIV
            +WISP_AP_NAME+PARA_DIV
            +WISP_AP_PASSWORD+PARA_DIV
            +WISP_AP_SECURITY_MODE+PARA_DIV
            +WISP_AP_SIGNAL_STRENGTH+PARA_DIV
            +WAN_CONNECT_STATE+PARA_DIV
            +WAN_IPADDR+PARA_DIV
            +WAN_NETMASK+PARA_DIV
            +WAN_GATEWAY+PARA_DIV
            +WAN_PRIMARY_DNS+PARA_DIV
            +WAN_SECONDARY_DNS+PARA_DIV
            +AP_CLI_BSSID + PARA_DIV
            + WISP_AP_WPA_CIPHER_SUITE
            + PARA_END;

    public static final String PARA_SSID_REQUEST = PARA_START + WLAN_SSID_SECURITY_MODE + PARA_DIV
            +WLAN_SSID_PASSWORD + PARA_DIV
            +WLAN_SSID_NAME + PARA_DIV
            +WLAN_SSID_WPA_CLIPHER_SUITE + PARA_DIV
            +WLAN_SSID_OPERATION_STATE
            + PARA_END;
}
