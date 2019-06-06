package com.flyzebra.ppfunstv.view.TvView.CellView.CellClickAction;

import android.content.Context;

import com.flyzebra.ppfunstv.data.CellEntity;
import com.flyzebra.ppfunstv.view.TvView.CellView.CellType;

/**
 * Created by 李宗源 on 2016/9/6.
 * E-mail:lizy@ppfuns.com
 */
public class ActionFactory implements CellType{

    public static IClickEvent create(Context context, CellEntity cellEntity){
        IClickEvent iClickEvent = null;
        switch (cellEntity.getType()){
            case TYPE_VOD_CATALOG:
            case TYPE_VOD_DETAIL:
            case TYPE_VOD_SUBJECT:
            case TYPE_ALL_CATALOG:
                iClickEvent = new DefaultAction(context,cellEntity,MobclickConstants.TYPE_VOD);
                break;
            case TYPE_LIVE:
//                iClickEvent = new LivePlayAction(context,cellEntity);
//                break;
            case TYPE_ADS_VIDEO:
            case TYPE_APP:
                iClickEvent = new DefaultAction(context,cellEntity,MobclickConstants.TYPE_APP);
                break;
            case TYPE_WEB:
                iClickEvent = new DefaultAction(context,cellEntity,MobclickConstants.TYPE_WEB);
                break;
            case TYPE_SHOPPING:
                iClickEvent = new ShopAction(context,cellEntity,MobclickConstants.TYPE_SHOP);
                break;
            case TYPE_SHOPPING_BYL:
                iClickEvent = new BoYiLeAction(context,cellEntity,MobclickConstants.TYPE_SHOP);
                break;
            case TYPE_PAY:
                iClickEvent = new PayAction(context,cellEntity,MobclickConstants.TYPE_PAY);
                break;
            case TYPE_ADS_IMAGE:
                iClickEvent = new DefaultAction(context,cellEntity,MobclickConstants.TYPE_ADS);
                break;
            case TYPE_QRCODE:
                iClickEvent = new DefaultAction(context,cellEntity,MobclickConstants.TYPE_QRCODE);
                break;
            default:
                iClickEvent = new DefaultAction(context,cellEntity);
                break;
        }

        return iClickEvent;
    }

    public static IClickEvent create(Context context, String cmd,boolean needauth,int type){
        IClickEvent iClickEvent = null;
        switch (type){
            case TYPE_ADS_IMAGE:
                iClickEvent = new DefaultAction(context,cmd,needauth,MobclickConstants.TYPE_ADS);
                break;
            default:
                iClickEvent = new DefaultAction(context,cmd,needauth,MobclickConstants.TYPE_APP);
                break;
        }
        return iClickEvent;
    }
}
