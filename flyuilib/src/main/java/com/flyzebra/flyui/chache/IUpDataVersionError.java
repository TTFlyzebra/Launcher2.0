package com.flyzebra.flyui.chache;

/**
 * 更新类常量接口
 * Created by  on 2016/6/21.
 */
public interface IUpDataVersionError {
    String FIND_NEW_VERSION = "有新的版本需要更新";
    String TAB_LIST_ERROR = "将请求数据解析成对像出错，请确认服务器后台提供的数据的正确性";
    String NETWORK_ERROR = "网络请求失败，请确认网络连接是否正常";
    String NETWORK_DATA_ERROR = "获取的网络数据异常，不能解析，请确认服务器后台提供的数据的正确性";
//    String NO_NEW_VERSION = "没有需要更新的版本";
    String UP_FILE_FAILD = "版本更新过程中因网络或其它原因有文件未能更新，请确认网络连接是否正常";
    String UP_TEMPLATE_NULL = "模板数据为空";
    String UP_ASSETS_ERROR = "读取缓存数据失败";
}
