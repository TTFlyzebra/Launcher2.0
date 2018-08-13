package com.ppfuns.ppfunstv.view.TvView.CellView.CellClickAction;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.ppfuns.ppfunstv.R;
import com.ppfuns.ppfunstv.constant.Constants;
import com.ppfuns.ppfunstv.data.ActionEntity;
import com.ppfuns.ppfunstv.data.CellEntity;
import com.ppfuns.ppfunstv.utils.DialogUtil;
import com.ppfuns.ppfunstv.utils.FlyLog;
import com.ppfuns.ppfunstv.utils.GsonUtils;
import com.ppfuns.ppfunstv.utils.IntentParamParseHelper;
import com.umeng.analytics.MobclickAgent;

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
        mEntity = GsonUtils.json2Object(cellEntity.getIntent(), ActionEntity.class);
        if(mEntity != null && !TextUtils.isEmpty(mEntity.getAppendAttr())){
            mAppendAttr = GsonUtils.json2HashMap(mEntity.getAppendAttr());
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
                MobclickAgent.onEvent(mContext, MobclickConstants.TYPE_PAY,mobPara);
            }
        }else{
            DialogUtil.showDialog(mContext, mContext.getString(R.string.tv_data_is_null));
        }
    }


}
