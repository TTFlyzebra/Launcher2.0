package com.flyzebra.ppfunstv.view.TvView.CellView.CellClickAction;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.flyzebra.ppfunstv.R;
import com.flyzebra.ppfunstv.data.ActionEntity;
import com.flyzebra.ppfunstv.data.CellEntity;
import com.flyzebra.ppfunstv.utils.DialogUtil;
import com.flyzebra.ppfunstv.utils.FlyLog;
import com.flyzebra.ppfunstv.utils.GsonUtil;
import com.flyzebra.ppfunstv.utils.IntentParamParseHelper;
import com.umeng.analytics.MobclickAgent;

import java.util.Map;

/**
 * Created by lizongyuan on 2016/12/29.
 * E-mail:lizy@ppfuns.com
 * 参数是json格式,由于目前cell的Action也为json格式,为避免多层嵌套json格式,
 * 现将需要的参数配置成key-value模式,转成Map对象,然后在将map转为json传给本控件
 */

public class ShopAction extends BaseAction {

    private static final String PARA = "data";
    private static final String ACTION = "action";
    private static final String DEFAULT_PACKAGE_NAME = "com.suning.tv.ebuy";
    private static final String DEFAULT_ACTIVITY = "com.suning.tv.ebuy.ui.home.ActivityHome";
    protected ActionEntity mEntity;
    protected Context mContext;
    protected boolean mNeedAuth;
    protected String mType;
    private Map<String,Object> mPara;

    /**
     *
     * @param context
     * @param cellEntity
     */
    public ShopAction(Context context, @NonNull CellEntity cellEntity,String type){
        mEntity = GsonUtil.json2Object(cellEntity.getIntent(), ActionEntity.class);
        if(mEntity!= null && !TextUtils.isEmpty(mEntity.getData())){
            mPara = IntentParamParseHelper.parseMap(mEntity.getData());
        }
        this.mNeedAuth = cellEntity.getNeedAuth();
        mContext = context;
        mType = type;
    }



    public void a(int ...a){

    }
    @Override
    protected void doActionImpl(int flag) {
        FlyLog.d( "do aciton ...."+mEntity);
        if(mEntity != null && TextUtils.isEmpty(mEntity.getPackageName()) && TextUtils.isEmpty(mEntity.getClassName())
                &&startActivity(DEFAULT_PACKAGE_NAME,DEFAULT_ACTIVITY,null,null)){
            //前端没有配置的话,默认进入主页
            mobPara.put(MobclickConstants.PARA_PACKAGE,DEFAULT_PACKAGE_NAME);
            mobPara.put(MobclickConstants.PARA_CLASS,DEFAULT_ACTIVITY);
            MobclickAgent.onEvent(mContext, mType,mobPara);
        }else if (mEntity != null && DEFAULT_PACKAGE_NAME.equals(mEntity.getPackageName()) && startActivity(mEntity.getPackageName(),mEntity.getClassName(),GsonUtil.mapToJson(mPara),mEntity.getAction())){
            mobPara.put(MobclickConstants.PARA_PACKAGE,DEFAULT_PACKAGE_NAME);
            mobPara.put(MobclickConstants.PARA_CLASS,DEFAULT_ACTIVITY);
            mobPara.put(MobclickConstants.PARA_ACTION,mEntity.getAction());
            mobPara.put(MobclickConstants.PARA_DATAS,mPara.toString());
            MobclickAgent.onEvent(mContext, mType,mobPara);
        }else if(CommondTool.execStartPackage(mContext,mEntity.getPackageName(),mEntity.getClassName(),mEntity.getData(),flag)){
            mobPara.put(MobclickConstants.PARA_PACKAGE,DEFAULT_PACKAGE_NAME);
            mobPara.put(MobclickConstants.PARA_CLASS,DEFAULT_ACTIVITY);
            mobPara.put(MobclickConstants.PARA_DATAS,mPara.toString());
            MobclickAgent.onEvent(mContext, mType,mobPara);
        }else if(!TextUtils.isEmpty(mEntity.getDownIntent()) && CommondTool.execStartActivity(mContext,mEntity.getDownIntent(),mEntity.getDownPara(),mNeedAuth,flag)){
            //去下载第三方应用
            mobPara.put(MobclickConstants.PARA_ACTION,mEntity.getDownIntent());
            mobPara.put(MobclickConstants.PARA_DATAS,mEntity.getDownPara());
            MobclickAgent.onEvent(mContext, MobclickConstants.TYPE_APP,mobPara);
        }else if(mEntity != null && !TextUtils.isEmpty(mEntity.getCmd())){//执行shell指令
            FlyLog.d("exec command...click:"+mEntity.getCmd());
            CommondTool.execCommand(mEntity.getCmd());
            MobclickAgent.onEvent(mContext,MobclickConstants.TYPE_SHOP,mEntity.getCmd());
        }else{
            DialogUtil.showDialog(mContext, mContext.getResources().getString(R.string.tv_data_err));
        }
    }
    /**
     * 根据包名和类名启动多乐购应用
     *
     * @param packageName 包名
     * @param className   类名
     * @param para        参数(json格式)
     * @param action      参数action
     * @return true:启动成功
     * false:启动失败
     */
    private boolean startActivity(String packageName, String className, String para, String action) {
        if (TextUtils.isEmpty(packageName) || TextUtils.isEmpty(className)) {
            return false;
        }
        Intent intent = new Intent();
        ComponentName cn = new ComponentName(packageName, className);
        if (!TextUtils.isEmpty(para)) {
            intent.putExtra(PARA, para);
        }
        if (!TextUtils.isEmpty(action)) {
            intent.putExtra(ACTION, action);
        }
        intent.setComponent(cn);
        ActivityInfo info = intent.resolveActivityInfo(mContext.getPackageManager(), PackageManager.GET_ACTIVITIES);
        if (info != null) {
            FlyLog.d(info.toString());
            mContext.startActivity(intent);
            return true;
        } else {
            FlyLog.d("no activity found to handle Intent :" + packageName);
        }
        return false;
    }

}
