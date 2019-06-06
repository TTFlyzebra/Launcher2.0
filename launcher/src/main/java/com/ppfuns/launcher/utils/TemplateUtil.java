package com.ppfuns.launcher.utils;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import com.ppfuns.launcher.R;
import com.ppfuns.launcher.constant.Constants;
import com.ppfuns.ppfunstv.data.SimpleTemplate;
import com.ppfuns.ppfunstv.data.TemplateBean;
import com.ppfuns.ppfunstv.data.TemplateEntity;
import com.ppfuns.ppfunstv.module.UpdataVersion.IUpdataVersion;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lizongyuan on 2016/11/1.
 * E-mail:lizy@ppfuns.com
 */

public class TemplateUtil {
    private static final String TIME_ZERO = "0";
    private static final String line = "|";
    private static int MAX_ITEM_LENGTH = 16;
    private static int MAX_LENGTH = 89;
    /**
     * 切换模板
     * @param templateName 模板名称
     */
    public static  String changeTemplate(Context context, IUpdataVersion iUpDataVersion, String templateName, boolean bShowTip){
        int curTemplate = SPUtil.getTemplate(context, SPUtil.TEMPLATE_ID, -1);
//        String ret = null;
        TemplateBean bean = iUpDataVersion.getTemplateBean();
        if (bean != null && bean.getTemplate() != null) {
            for (int i = 0; i < bean.getTemplate().size(); i++) {
                TemplateEntity entity = bean.getTemplate().get(i);
                if (entity !=null && entity.getTabList() != null && entity.getTabList().size() > 0) {
                    int id = entity.getTemplateId();
                    if (templateName.equals(entity.getTemplateName())) {
                        if(curTemplate != id){
                            FlyLog.d(" switch ui by name :"+templateName);
                            SPUtil.setTemplate(context, SPUtil.TEMPLATE_ID, id);
                            iUpDataVersion.switchTemplate();
                            return context.getString(R.string.tip_switch_ui);
                        }else if(bShowTip){
                            FlyLog.d("no need to switch template...by name:"+templateName);
                            return context.getString(R.string.template_is_the_same);
                        }else {
                            FlyLog.d("no need to switch template...by name:"+templateName);
                        }
                        return null;
                    }
                }
            }
            if(bShowTip){
                FlyLog.d("template not found...by name:"+templateName);
                return context.getString(R.string.template_not_found);
            }
        }
        return null;
    }
    /**
     * 切换模板(手机三屏指令,语言指令)
     *
     * @param templateId   模板id
     * @param templateName 模板名称
     *                     说明:
     *                     1:如果templateId和templateName都没传的话,自动切换到下一个模板
     *                     2:如果只传了templateName,那么根据templateName进行切换
     *                     3:只要传递了templateId,那就就根据templateId进行切换
     */
    public static String changeTemplate(Context context, IUpdataVersion iUpDataVersion,int templateId, String templateName) {
        FlyLog.i(" templateId:" + templateId + " templateName:" + templateName);
        TemplateBean bean = iUpDataVersion.getTemplateBean();
        int curTemplate = SPUtil.getTemplate(context, SPUtil.TEMPLATE_ID, -1);
        if (templateId == -1 && TextUtils.isEmpty(templateName)) {//什么参数都没传,自动切换到下一个模板
            if (bean != null && bean.getTemplate() != null) {
                for (int i = 0; i < bean.getTemplate().size(); i++) {
                    TemplateEntity entity = bean.getTemplate().get(i);
                    if (entity.getTabList() != null && entity.getTabList().size() > 0) {
                        int id = entity.getTemplateId();
                        if (id == curTemplate) {
                            int index = (i + 1 == bean.getTemplate().size() ? 0 : i + 1);
                            TemplateEntity toTemplate = bean.getTemplate().get(index);
                            SPUtil.setTemplate(context, SPUtil.TEMPLATE_ID, toTemplate.getTemplateId());
                            iUpDataVersion.switchTemplate();
                            return context.getString(R.string.tip_switch_ui);
                        }
                    }
                }
                return context.getString(R.string.template_not_found);
            } else {
                return context.getString(R.string.template_is_null);
            }
        } else if (templateId == -1 && !TextUtils.isEmpty(templateName)) {//name不为空,根据name切换模板
            return changeTemplate(context,iUpDataVersion,templateName,true);
        }
        //根据传入的templateId切换模板
        if (curTemplate == templateId) {
            FlyLog.d("curTemplate id == select template id, no need to change template");
            return context.getString(R.string.template_is_the_same);
        }
        if (bean != null && bean.getTemplate() != null) {
            for (final TemplateEntity entity : bean.getTemplate()) {
                if (entity.getTabList() != null && entity.getTabList().size() > 0) {
                    if (templateId == entity.getTemplateId()) {
                        FlyLog.d("curTemplate id != select template id,need to change template");
                        SPUtil.setTemplate(context, SPUtil.TEMPLATE_ID, entity.getTemplateId());
                        iUpDataVersion.switchTemplate();
                        return context.getString(R.string.tip_switch_ui);
                    }
                }
            }
            return context.getString(R.string.template_not_found);
        } else {
            return context.getString(R.string.template_is_null);
        }
    }


    public static String updateTimeTheme(Context context, IUpdataVersion iUpDataVersion, Handler handler,int msgId){
        if(iUpDataVersion != null && !iUpDataVersion.isUPVeriosnRunning()){
            String time = TimeUtils.getCurrentTime(TimeUtils.format_HHmm);
            String childHourStart = (String) SPUtil.get(context,SPUtil.FILE_TIME_THEME,SPUtil.CHILD_TIME_HOUR_START,TIME_ZERO);
            String childMin = (String) SPUtil.get(context,SPUtil.FILE_TIME_THEME,SPUtil.CHILD_TIME_MIN,TIME_ZERO);
            String childHourEnd = (String) SPUtil.get(context,SPUtil.FILE_TIME_THEME,SPUtil.CHILD_TIME_HOUR_END,TIME_ZERO);
            String childContinue = (String) SPUtil.get(context,SPUtil.FILE_TIME_THEME,SPUtil.CHILD_TIME_CONTINUE,TIME_ZERO);
            FlyLog.d(" nowTime=%s;childHourStart=%s;childHourEnd=%s;childMin=%s",time,childHourStart,childHourEnd,childMin);
            String childStart = childHourStart+":"+childMin;
            String childEnd = childHourEnd+":"+childMin;
            int iStart = TimeUtils.String2time(childHourStart,childMin);
            int iEnd = TimeUtils.String2time(childHourEnd,childMin);
            String[] nowTime = time.split(":");
            int iNow = TimeUtils.String2time(nowTime[0],nowTime[1]);
            int delayTime = 0;
            if(iNow == iStart || iNow == iEnd){
                handler.removeMessages(msgId);
                handler.sendEmptyMessageDelayed(msgId, Constants.ONE_MINUTE);
            }
            String retInfo = null;
            if((childStart.compareTo(childEnd)<0&& childStart.compareTo(time) <= 0 && childEnd.compareTo(time) > 0)
                    ||"24".equals(childContinue)
                    ||(childStart.compareTo(childEnd)>0&&
                    (childStart.compareTo(time) <= 0 || childEnd.compareTo(time) > 0))){
                //切换到儿童版
                retInfo = TemplateUtil.changeTemplate(context,iUpDataVersion,context.getString(R.string.ui_version_child),false);
            }else{
                //切换到大众版
                retInfo = TemplateUtil.changeTemplate(context,iUpDataVersion,context.getString(R.string.ui_version_public),false);
            }
            //下次时间
            if(childHourStart.compareTo(childHourEnd)!=0){
                if(iNow<iStart){
                    delayTime = iStart - iNow;
                }else if(iNow < iEnd){
                    delayTime = iEnd - iNow;
                }else{
                    delayTime = 24 * Constants.ONE_HOUR - iNow + iStart;
                }
                handler.removeMessages(msgId);
                handler.sendEmptyMessageDelayed(msgId,delayTime);
            }
            return retInfo;
        }else{
            FlyLog.d(" is in update vesion,change time theme later...");
            handler.removeMessages(msgId);
            handler.sendEmptyMessageDelayed(msgId,Constants.ONE_MINUTE);
        }
        return null;
    }


    /**
     * 设置模板信息到系统属性中
     * @param bean
     */
    public static void setSimpleTemplateInfo(Context context,TemplateBean bean){
        int templateId =  SPUtil.getTemplate(context, SPUtil.TEMPLATE_ID, -1);
        StringBuilder builder = new StringBuilder();
        StringBuilder idBuilder = new StringBuilder();
        if(bean != null && bean.getTemplate() != null){
            List<TemplateEntity> entities = bean.getTemplate();
            if(entities.size() >0){
                for(TemplateEntity entity :entities){
                    String name = entity.getTemplateName();
                    if(!TextUtils.isEmpty(name) && CutStringUtil.length(name)> MAX_ITEM_LENGTH){
                        FlyLog.d("name:"+name+" length:"+CutStringUtil.length(name));
                        name= CutStringUtil.substring(name,MAX_ITEM_LENGTH);
                    }
                    FlyLog.d("name:"+name+" length:"+CutStringUtil.length(name));
                    if(templateId == entity.getTemplateId()){
                        builder.insert(0,line).insert(0,name);
                        idBuilder.insert(0,line).insert(0,entity.getTemplateId());
                    }else{
                        builder.append(name).append(line);
                        idBuilder.append(entity.getTemplateId()).append(line);
                    }

                }
                builder.deleteCharAt(builder.length()-1);
                idBuilder.deleteCharAt(idBuilder.length()-1);
                String names = builder.toString();
                if(!TextUtils.isEmpty(names) && CutStringUtil.length(names)> MAX_LENGTH){
                    FlyLog.d("names:"+names+" length:"+CutStringUtil.length(names));
                    names= CutStringUtil.substring(names,MAX_LENGTH);
                }
                FlyLog.d("names:"+names+" length:"+CutStringUtil.length(names));
                SystemPropertiesProxy.set(context,Constants.Property.TEMPLATE_NAMES,names);
                SystemPropertiesProxy.set(context,Constants.Property.TEMPLATE_IDS,idBuilder.toString());
            }else {
                FlyLog.e("template list size is 0");
            }
        }
    }

    public static List<SimpleTemplate> getSimpleTemplateInfo(Context context){
        String names = SystemPropertiesProxy.get(context,Constants.Property.TEMPLATE_NAMES,null);
        List<SimpleTemplate> templates = null;
        if(names!=null){
            templates = new ArrayList<>();
            String[] items = names.split(line);
            for(int i=0;i<items.length;i++){
                SimpleTemplate template = new SimpleTemplate(items[i],false);
                if(i==0){
                    template.bSelected = true;
                }
                templates.add(template);
            }
        }
        return templates;
    }
}
