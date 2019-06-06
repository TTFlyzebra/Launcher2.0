package com.flyzebra.ppfunstv.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.flyzebra.ppfunstv.data.CellBean;
import com.flyzebra.ppfunstv.data.ControlBean;
import com.flyzebra.ppfunstv.data.TabEntity;
import com.flyzebra.ppfunstv.data.TemplateBean;
import com.flyzebra.ppfunstv.data.TemplateEntity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 加载本地数据
 */
public class LoadData {
    public static final String DEFAULT_WEB_DATA_PATH = "ppfuns/";

    private static final String TAG = LoadData.class.getSimpleName();
    private static LoadData mInstance = null;
    private static final Object lock = new Object();

    private LoadData(){

    }

    public static LoadData getInstance(){
        if(mInstance == null){
            synchronized (lock){
                mInstance = new LoadData();
            }

        }
        return mInstance;
    }

    public TemplateBean getAssetTemplate(Context context,String path){
        InputStream is = null;
        try {
            is = context.getAssets().open(path);
            String fileInfo = FileUtil.readFile(is);
            if(fileInfo != null){
                return  GsonUtil.json2Object(fileInfo,TemplateBean.class);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private TemplateBean getTemplate(Context context,String path){
        String fileInfo = FileUtil.readFile(path);
        if(fileInfo != null){
            return  GsonUtil.json2Object(fileInfo,TemplateBean.class);
        }
        return null;
    }

    public CellBean getAssetCells(Context context, String path){
        InputStream is = null;
        try {
            is = context.getAssets().open(path);
            String fileInfo = FileUtil.readFile(is);
            if(fileInfo != null){
                return  GsonUtil.json2Object(fileInfo,CellBean.class);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 获取每页内容数据
     * @param path 每页json文件路径
     * @return
     */
    public CellBean getCellsInfo(String path){
        String fileInfo = FileUtil.readFile(path);
        if(fileInfo != null){
            return  GsonUtil.json2Object(fileInfo, CellBean.class);
        }
        return null;
    }

    public ControlBean getAssetControlInfo(Context context, String path){
        InputStream is = null;
        try {
            is = context.getAssets().open(path);
            String fileInfo = FileUtil.readFile(is);
            if(fileInfo != null){
                return  GsonUtil.json2Object(fileInfo,ControlBean.class);
            }
        } catch (IOException e) {
            FlyLog.d(e.toString());
            e.printStackTrace();
        }
        return null;
    }

    public ControlBean getControlInfo(Context context,String path){
        String fileInfo = FileUtil.readFile(path);
        if(fileInfo != null){
            return  GsonUtil.json2Object(fileInfo,ControlBean.class);
        }
        return null;
    }

    public TemplateBean loadTemplateData(Context context,String URL_TempLate_DISK){
        String path = URL_TempLate_DISK;
        String templatePath = DEFAULT_WEB_DATA_PATH + EncodeUtil.md5(path)+".0";
        TemplateBean bean = getInstance().getAssetTemplate(context,templatePath);
        return bean;
    }

    public Map<Integer,ControlBean> loadControlData(Context context, TemplateBean bean,String URL_RESOURCE_DISK){
        List<TemplateEntity> templates = bean.getTemplate();
        Map<Integer,ControlBean> map = new HashMap<>();
        if(templates != null && templates.size() >0){
            for(TemplateEntity entity :templates){
                int key = entity.getTemplateId();
                String controlPath = DEFAULT_WEB_DATA_PATH + EncodeUtil.md5(URL_RESOURCE_DISK + key)+".0";
                ControlBean controlBean = getInstance().getAssetControlInfo(context,controlPath);
                map.put(key,controlBean);
            }
        }
        return map;
    }

    public TemplateEntity loadTemplateEntity(Context context,TemplateBean bean) {
        int templateId = SPUtil.getTemplate(context, SPUtil.TEMPLATE_ID, -1);
        List<TemplateEntity> templates = bean.getTemplate();
        TemplateEntity templateEntity = null;
        if (templates != null && templates.size() > 0) {
            templateEntity = bean.getTemplate().get(0);
            if (-1 == templateId) {//如果当前没有选定模板
                //查找默认模板
                for (TemplateEntity entity : templates) {
                    if("true".equals(entity.getIsdefault())){
                        templateEntity = entity;
                        break;
                    }
                }
            } else {//如果当前选定了特定模板,便加载选定的模板
                for (TemplateEntity entity : templates) {
                    if (entity.getTemplateId() == templateId) {
                        templateEntity = entity;
                        break;
                    }
                }
            }
        }
        return templateEntity;
    }

    public List<CellBean> loadCellList(Context context,List<TabEntity> tabs,String URL_CellBean_DISK){
        List<CellBean> cells = new ArrayList<>();
        for (TabEntity tab : tabs) {
//            FlyLog.d("tab.." + tab.getName());
//            String path = SystemPropertiesProxy.get(context,Constants.URL_BASE_DEFAULT,IUpdataVersion.URL_BASE) + IUpdataVersion.URL_CellBean_suffix;
            String path = URL_CellBean_DISK;
            String tabPath = DEFAULT_WEB_DATA_PATH + EncodeUtil.md5(path+tab.getId())+".0";
            CellBean cell = LoadData.getInstance().getAssetCells(context, tabPath);
            if (cell != null) {
                cells.add(cell);
            }
        }
        return cells;
    }


    public Bitmap loadAssetBitmap(Context context,String url){
        url = DEFAULT_WEB_DATA_PATH + EncodeUtil.md5(url) + ".0";
        Bitmap bitmap = null;
        AssetManager assets = context.getAssets();
        InputStream is = null;
        try {
            is = assets.open(url);
            BitmapFactory.Options options = new BitmapFactory.Options();
            bitmap = BitmapFactory.decodeStream(is, null, options);
        } catch (IOException e) {
            e.printStackTrace();

        }finally {
            if(is != null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    FlyLog.e(e.toString());
                }
            }
        }
        return bitmap;
    }


}
