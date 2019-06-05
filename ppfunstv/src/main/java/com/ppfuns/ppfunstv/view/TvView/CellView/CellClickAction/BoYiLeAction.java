package com.ppfuns.ppfunstv.view.TvView.CellView.CellClickAction;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.ppfuns.ppfunstv.R;
import com.ppfuns.ppfunstv.data.ActionEntity;
import com.ppfuns.ppfunstv.data.CellEntity;
import com.ppfuns.ppfunstv.utils.DialogUtil;
import com.ppfuns.ppfunstv.utils.FlyLog;
import com.ppfuns.ppfunstv.utils.GsonUtil;
import com.ppfuns.ppfunstv.utils.IntentParamParseHelper;
import com.umeng.analytics.MobclickAgent;

import java.util.Map;

/**
 * 多乐播购物
 * 传给多乐购的参数是json格式,由于目前cell的Action也为json格式,为避免多层嵌套json格式,
 * 现将多乐购需要的参数配置成key-value模式,转成Map对象,然后在将map转为json传给多乐购
 * 文档:
 *      \\192.168.1.88\技术部\2-聚OS开发部\03.项目开发\聚OS\第三方业务\E购物类\多乐播-购好东西
 * 商品信息数据同步接口为http://tv.duolebo.com/qdghZhilinkTVService/data_interface.jsp?token=84CDB248D3436F92EFF66542C1C3C2CC644435DA3DC1D311B688727416CEF500&pageno=1&pagesize=50
 */
public class BoYiLeAction extends BaseAction {

    private static final String PARA = "data";
    protected Context mContext;
    protected boolean mNeedAuth;
    protected String mType;
    protected ActionEntity mEntity;
    private Map<String,Object> mPara;

    /**
     *
     * @param context
     * @param cellEntity
     */
    public BoYiLeAction(Context context, @NonNull CellEntity cellEntity, String type){
        mEntity = GsonUtil.json2Object(cellEntity.getIntent(), ActionEntity.class);
        if(mEntity!= null && !TextUtils.isEmpty(mEntity.getData())){
            mPara = IntentParamParseHelper.parseMap(mEntity.getData());
        }
        this.mNeedAuth = cellEntity.getNeedAuth();
        mContext = context;
        mType = type;
    }

    @Override
    protected void doActionImpl(int flag) {
        FlyLog.v("do aciton ...."+mEntity);
        if (mEntity != null && mPara != null) {
            if(!startActivity(mEntity.getPackageName(),mEntity.getClassName(), GsonUtil.mapToJson(mPara))){
                if(!TextUtils.isEmpty(mEntity.getDownIntent()) && CommondTool.execStartActivity(mContext,mEntity.getDownIntent(),mEntity.getDownPara(),mNeedAuth,flag)){

                }else{
                    DialogUtil.showDialog(mContext, mContext.getResources().getString(R.string.tv_data_err));
                }
            }else{
                mobPara.put(MobclickConstants.PARA_PACKAGE,mEntity.getPackageName());
                mobPara.put(MobclickConstants.PARA_CLASS,mEntity.getClassName());
                mobPara.put(MobclickConstants.PARA_DATAS,mEntity.getData());
                MobclickAgent.onEvent(mContext,MobclickConstants.TYPE_SHOP,mobPara);
            }
        }else if(mEntity != null){//执行shell指令
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
     * @return true:启动成功
     * false:启动失败
     */
    private boolean startActivity(String packageName, String className, String para) {
        if (TextUtils.isEmpty(packageName) || TextUtils.isEmpty(className)) {
            return false;
        }
        Intent intent = new Intent();
        ComponentName cn = new ComponentName(packageName, className);
        intent.putExtra(PARA, para);
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
