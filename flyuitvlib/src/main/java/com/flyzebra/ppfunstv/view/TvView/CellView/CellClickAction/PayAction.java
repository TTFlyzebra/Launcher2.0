package com.flyzebra.ppfunstv.view.TvView.CellView.CellClickAction;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.flyzebra.ppfunstv.R;
import com.flyzebra.ppfunstv.constant.Constants;
import com.flyzebra.ppfunstv.data.ActionEntity;
import com.flyzebra.ppfunstv.data.CellEntity;
import com.flyzebra.ppfunstv.utils.DialogUtil;
import com.flyzebra.ppfunstv.utils.FlyLog;
import com.flyzebra.ppfunstv.utils.GsonUtil;
import com.flyzebra.ppfunstv.utils.IntentParamParseHelper;

import java.util.HashMap;

/**
 * Created by lizongyuan on 2017/2/7.
 * E-mail:lizy@ppfuns.com
 */

public class PayAction  extends BaseAction {

    private final String KEY_MAP = "appendAttr";
    protected ActionEntity mEntity;
    protected Context mContext;
    protected boolean mNeedAuth;
    protected String mType;
    private HashMap<String, String> mAppendAttr = null;

    public PayAction(Context context, @NonNull CellEntity cellEntity, String type){
        mEntity = GsonUtil.json2Object(cellEntity.getIntent(), ActionEntity.class);
        if(mEntity != null && !TextUtils.isEmpty(mEntity.getAppendAttr())){
            mAppendAttr = GsonUtil.json2HashMap(mEntity.getAppendAttr());
        }
        this.mNeedAuth = cellEntity.getNeedAuth();
        mContext = context;
        mType = type;
    }

    @Override
    protected void doActionImpl(int flag) {
        FlyLog.v("do aciton ...."+mEntity);
        if(mEntity != null){
            Intent it = new Intent();
            Bundle budle = new Bundle();

            if(!TextUtils.isEmpty(mEntity.getIntent())){
                it.setAction(mEntity.getIntent());
                mobPara.put(MobclickConstants.PARA_ACTION,mEntity.getIntent());
            }else {
                it.setAction(Constants.Action.PAY_DETAIL);
                mobPara.put(MobclickConstants.PARA_ACTION,Constants.Action.PAY_DETAIL);
            }
            if(!TextUtils.isEmpty(mEntity.getData())){
                budle.putAll(IntentParamParseHelper.parseBundle(mEntity.getData()));
                mobPara.put(MobclickConstants.PARA_DATAS,mEntity.getData());
            }
            if(mAppendAttr != null){
                budle.putSerializable(KEY_MAP,mAppendAttr);
                it.putExtras(budle);
            }
            if (CommondTool.execStartActivityAndShowTip(mContext, it, mContext.getString(R.string.tv_app_not_install), mNeedAuth,flag)) {
            }
        }else{
            DialogUtil.showDialog(mContext, mContext.getString(R.string.tv_data_is_null));
        }
    }


}
