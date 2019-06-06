package com.flyzebra.ppfunstv.utils;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lizongyuan on 2016/11/17.
 * E-mail:lizy@ppfuns.com
 * 行为数据相关
 * 参考文档\\192.168.1.88\技术部\2-聚OS开发部\03.项目开发\聚OS\功能开发\行为采集\行为采集服务对外功能接口定义V1.0.doc
 *      \\192.168.1.88\技术部\2-聚OS开发部\03.项目开发\聚OS\功能开发\行为采集\终端用户行为采集数据模型V1.0.doc
 */

public class BehavioralUtil {

    private static int id = 0;
    private static final String ACTION = "com.flyzebra.action.msg";
    private static final String PARA_ID = "id";
    private static final String PARA_TYPE = "type";
    private static final String PARA_TIME = "time";
    private static final String PARA_SN = "sn";
    private static final String PARA_SMC_ID = "smcId";
    private static final String PARA_UID = "uid";
    private static final String PARA_ACTIONS = "actions";

    private static final String PARA_ACTION ="action";
    private static final String PARA_NAME = "name";
    private static final String PARA_PACKAGE_NAME = "app";
    private static final int TYPE_PAGE_CHANGE = 4;//页面变化
    private static final int TYPE_TEMPLATE_CHANGE = 27;//模板变化
    private static final int TYPE_APP = 15;//启动app

    public static String[] filter = {"com.flyzebra."};
    /**
     * 通用部分	 id	  Unsigned Int 	消息编号，自动增长
     *           type		行为事件类型，具体值参见 5行为事件定义
     *           time	Long	当前事件产生的时间，采用UTC时间描述，单位毫秒，如1970年1月1日 00:00:00用UTC时间应表示为0。
     *           sn	String	机顶盒序列号
     *           smcId	String	智能卡号
     *           uid	String	用户唯一标识，设备激活后运营商后台统一分配。
     * 扩展部分	actions	Object	详细格式参见 6数据格式定义
     */

    /**
     * 生成通用部分
     * @return
     */
    private static Intent createGeneralPart(Context context,int type){
        id++;
        Intent intent = new Intent(ACTION);
        intent.putExtra(PARA_ID,id);
        intent.putExtra(PARA_TYPE,type);
        intent.putExtra(PARA_TIME,new Date().getTime());
        intent.putExtra(PARA_SN,Utils.getSn(context,null));
        intent.putExtra(PARA_SMC_ID,Utils.getCaId(context,null));
        intent.putExtra(PARA_UID,Utils.getUid(context,null));
        return intent;
    }


    /**
     * 生成信息
     * @param intent 包含通用部分信息的intent（用createGeneralPart生成）
     * @param items 扩展部分信息
     * @return
     */
    private static Intent createInfo(Intent intent,Map<String, Object> items){
        JSONObject object = new JSONObject();
        if(items != null){
            for (Map.Entry<String, Object> entry : items.entrySet()) {
                try {
                    object.put(entry.getKey(),entry.getValue());
                } catch (JSONException e) {
                    e.printStackTrace();
                    FlyLog.e(e.toString());
                }
            }
            intent.putExtra(PARA_ACTIONS,object.toString());
        }
        return intent;
    }


    /**
     * 上报页面切换事件
     * @param context
     * @param tabName tab名称
     * @param tabId tabID
     * @param action （0进入,1离开）
     */
    private static void reportPageChangeEvent(Context context,String tabName,String tabId,int action){
        Map<String, Object> map = new HashMap<>();
        map.put(PARA_NAME,tabName);
        map.put(PARA_ID,tabId);
        map.put(PARA_ACTION,action);
        Intent intent = createGeneralPart(context,TYPE_PAGE_CHANGE);
        intent = createInfo(intent,map);
        context.sendBroadcast(intent);
        FlyLog.d("report page event,tabName:"+tabName+" action:"+action+" tabId:"+tabId);
    }

    /**
     * 上报切换模板事件
     * @param context
     * @param templateName 模板名称
     * @param templateId 模板ID
     * @param action （0进入,1离开）
     */
    private static void reportTemplateChangeEvent(Context context,String templateName,String templateId,int action){
        Intent intent = createGeneralPart(context,TYPE_TEMPLATE_CHANGE);
        Map<String, Object> map = new HashMap<>();
        map.put(PARA_NAME,templateName);
        map.put(PARA_ID,templateId);
        map.put(PARA_ACTION,action);
        intent = createInfo(intent,map);
        context.sendBroadcast(intent);
        FlyLog.d("report template event,templateName:"+templateName+" action:"+action+" templateId:"+templateId);
    }

    /**
     * 上报所有事件（包括模板和页面进入/离开事件）
     * @param context
     * @param action （0进入,1离开）
     */
    private static void reportEvent(Context context, int action){
        String templateName = SPUtil.getEvent(context,SPUtil.EVENT_TEMPLATE_NAME,"");
        String templateId = SPUtil.getEvent(context,SPUtil.EVENT_TEMPLATE_ID,"0");
        String tabName = SPUtil.getEvent(context,SPUtil.EVENT_TAB_NAME,"");
        String tabId = SPUtil.getEvent(context,SPUtil.EVENT_TAB_ID,"0");
        if(!TextUtils.isEmpty(templateName)){
            reportTemplateChangeEvent(context,templateName,templateId,action);
        }
        if(!TextUtils.isEmpty(tabName)){
            reportPageChangeEvent(context,tabName,tabId,action);
        }
//        //标记进入或者退出
//        SPUtil.setEvent(context,SPUtil.EVENT_OUT,action+"");
    }


    /**
     * 上报进入事件（包括模板和页面进入事件）
     * @param context
     */
    public static void reportInEvent(Context context){
        reportEvent(context,0);
    }

    /**
     * 上报离开事件（包括模板和页面离开事件）
     * @param context
     */
    public static void reportOutEvent(Context context){
        reportEvent(context,1);
    }


    /**
     * 上报模板事件（先上报上一个模板退出，然后上报本模板进入事件）
     * @param context
     * @param curName
     * @param curId
     */
    public static void reportTemplateEvent(Context context,String curName,String curId){
        String oldName =  SPUtil.getEvent(context,SPUtil.EVENT_TEMPLATE_NAME,"");
        String oldId = SPUtil.getEvent(context,SPUtil.EVENT_TEMPLATE_ID,"0");
//        if(!TextUtils.isEmpty(curName)&&!TextUtils.equals(curName,oldName)&&!TextUtils.equals(oldId,curId)){
        //上报前一个
        if(!TextUtils.isEmpty(oldName)&&!TextUtils.equals(oldId,curId)){
            reportTemplateChangeEvent(context,oldName,oldId,1);
        }
        //设置属性
        SPUtil.setEvent(context,SPUtil.EVENT_TEMPLATE_NAME,curName);
        SPUtil.setEvent(context,SPUtil.EVENT_TEMPLATE_ID,curId);
        //上报本次进入事件
        if(!TextUtils.isEmpty(curName)){
            reportTemplateChangeEvent(context,curName,curId,0);
        }
//        }
    }

    /**
     * 上报翻页事件（先上报上一个页面退出，然后上报本页面进入事件）
     * @param context
     * @param curName
     * @param curId
     */
    public static void reportPageEvent(Context context,String curName,String curId){
        String oldName =  SPUtil.getEvent(context,SPUtil.EVENT_TAB_NAME,"");
        String oldId = SPUtil.getEvent(context,SPUtil.EVENT_TAB_ID,"0");
//        if(!TextUtils.isEmpty(curName)&&!TextUtils.equals(curName,oldName)&&!TextUtils.equals(oldId,curId)){
            //上报前一个
            if(!TextUtils.isEmpty(oldName)&&!TextUtils.equals(oldId,curId)){
                reportPageChangeEvent(context,oldName,oldId,1);
            }
            //设置属性
            SPUtil.setEvent(context,SPUtil.EVENT_TAB_NAME,curName);
            SPUtil.setEvent(context,SPUtil.EVENT_TAB_ID,curId);
            //上报本次进入事件
            if(!TextUtils.isEmpty(curName)){
                reportPageChangeEvent(context,curName,curId,0);
            }
//        }
    }

    /**
     * 上报启动应用事件
     * @param context
     * @param packageName 应用包名
     */
    public static void reportStartAppEvent(Context context,String packageName){
        for (String item : filter) {
            if (packageName.contains(item)) {
                FlyLog.d("no need to report app start,packName:"+packageName);
                return;
            }
        }
        Intent intent = createGeneralPart(context,TYPE_APP);
        Map<String, Object> map = new HashMap<>();
        map.put(PARA_PACKAGE_NAME,packageName);
        intent = createInfo(intent,map);
        context.sendBroadcast(intent);
        FlyLog.d("report start app event,packageName:"+packageName);
    }
}
