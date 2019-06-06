package com.flyzebra.ppfunstv.view.TvView.CellView.CellClickAction;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.flyzebra.ppfunstv.R;
import com.flyzebra.ppfunstv.data.ActionEntity;
import com.flyzebra.ppfunstv.data.CellEntity;
import com.flyzebra.ppfunstv.utils.DialogUtil;
import com.flyzebra.ppfunstv.utils.FlyLog;
import com.flyzebra.ppfunstv.utils.GsonUtil;

/**
 * Created by 李宗源 on 2016/9/6.
 * E-mail:lizy@ppfuns.com
 * 默认动作执行类
 */
public class DefaultAction extends BaseAction{

    protected ActionEntity mEntity;
    protected Context mContext;
    protected boolean mNeedAuth;
    protected String mType;

    public DefaultAction(Context context, String cmd,boolean needAuth,@NonNull String type){
        mEntity = GsonUtil.json2Object(cmd, ActionEntity.class);
        this.mNeedAuth = needAuth;
        mContext = context;
        mType = type;
    }

    /**
     * 默认为MobclickConstants.TYPE_APP类型
     * @param context
     * @param cellEntity
     */
    public DefaultAction(Context context,@NonNull CellEntity cellEntity){
        this(context,cellEntity,MobclickConstants.TYPE_APP);
    }

    protected DefaultAction(Context context,boolean needAuth){
        this.mNeedAuth = needAuth;
        this.mContext = context;
    }

    /**
     *
     * @param context
     * @param cmd  执行命令
     * @param needAuth 是否需要授权
     */
    public DefaultAction(Context context,String cmd,boolean needAuth){
        this(context,needAuth);
        mEntity = GsonUtil.json2Object(cmd, ActionEntity.class);
    }

    /**
     *
     * @param context
     * @param cellEntity
     * @param type  上次友盟的自定义类型
     */
    public DefaultAction(Context context,@NonNull CellEntity cellEntity,String type){
        this(context,cellEntity.getIntent(),cellEntity.getNeedAuth(),type);
    }

    @Override
    protected void doActionImpl(int flag) {
        FlyLog.v("do aciton ...." + mEntity);

        if (mEntity != null && CommondTool.execStartPackage(mContext, mEntity.getPackageName(), mEntity.getClassName(), mEntity.getData(),flag)) {
            mobPara.put(MobclickConstants.PARA_PACKAGE, mEntity.getPackageName());
            mobPara.put(MobclickConstants.PARA_CLASS, mEntity.getClassName());
            mobPara.put(MobclickConstants.PARA_DATAS, mEntity.getData());
        } else if (mEntity != null && CommondTool.execStartPackage(mContext, mEntity.getPackageName(),mEntity.getData(),flag)) {
            mobPara.put(MobclickConstants.PARA_PACKAGE, mEntity.getPackageName());
        } else if (mEntity != null && !TextUtils.isEmpty(mEntity.getIntent()) && CommondTool.execStartActivity(mContext, mEntity.getIntent(), mEntity.getData(), mEntity.getUrl(), mNeedAuth,flag)) {//启动第三方应用
            mobPara.put(MobclickConstants.PARA_ACTION, mEntity.getIntent());
        } else if (mEntity != null && !TextUtils.isEmpty(mEntity.getDownIntent()) && CommondTool.execStartActivity(mContext, mEntity.getDownIntent(), mEntity.getDownPara(), mNeedAuth,flag)) {//去下载第三方应用
            mobPara.put(MobclickConstants.PARA_ACTION, mEntity.getDownIntent());
            mobPara.put(MobclickConstants.PARA_DATAS, mEntity.getDownPara());
        } else if (mEntity != null && !TextUtils.isEmpty(mEntity.getCmd())) {
            FlyLog.d("exec command...click:" + mEntity.getCmd());
            CommondTool.execCommand(mEntity.getCmd());
        } else {
            if (!TextUtils.equals(mType, MobclickConstants.TYPE_ADS)) {
                DialogUtil.showDialog(mContext, mContext.getResources().getString(R.string.tv_data_err));
            }
        }
    }

    @Override
    public String toString() {
        return "DefaultAction{" +
                "mEntity=" + mEntity +
                ", mContext=" + mContext +
                ", mNeedAuth=" + mNeedAuth +
                ", mType='" + mType + '\'' +
                '}';
    }
}
