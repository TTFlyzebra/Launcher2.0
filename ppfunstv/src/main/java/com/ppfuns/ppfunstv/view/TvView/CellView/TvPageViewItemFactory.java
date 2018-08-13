package com.ppfuns.ppfunstv.view.TvView.CellView;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.DrawableRes;
import android.text.TextUtils;

import com.ppfuns.ppfunstv.data.CellEntity;
import com.ppfuns.ppfunstv.data.FlyBean;
import com.ppfuns.ppfunstv.module.BitmapCache;
import com.ppfuns.ppfunstv.module.UpdataVersion.IDiskCache;
import com.ppfuns.ppfunstv.utils.DisplayUtils;
import com.ppfuns.ppfunstv.utils.GsonUtils;
import com.ppfuns.ppfunstv.view.TvView.CellView.PopupCellView.IPopupAnimatView;
import com.ppfuns.ppfunstv.view.TvView.CellView.PopupCellView.PopupAnimatView;

/**
 * 每页中cell控件生成类
 * Created by lzy on 2016/6/15.
 */
public class TvPageViewItemFactory {
    /**
     * 根据传入的cellEntity构建对应的自定义pageItemView
     *
     * @param context
     * @param cell
     * @return
     */
    public static ITvPageItemView createView(Context context, IDiskCache iDiskCache, BitmapCache bitmapCache, CellEntity cell, @DrawableRes int resID) {
        handleExtend(context, cell);
        ITvPageItemView base;
        switch (cell.getType()) {
            case CellType.TYPE_LIVE:
                base = new MinVideoCellView(context);
                break;
            case CellType.TYPE_WEB:
                base = new GifCellView(context);
                break;
            case CellType.TYPE_ADS_VIDEO:
                base = new AdsVideoCellView(context);
                break;
            case CellType.TYPE_TEXT:
                base = new TextCellView(context);
                break;
            case CellType.TYPE_IMAGE:
                base = new SaticImageCellView(context);
                break;
            case CellType.TYPE_ADS_IMAGE:
                base = new AdsImageCellView(context);
                break;
            case CellType.TYPE_QRCODE:
                base = new QrCodeCellView(context);
                break;
            case CellType.TYPE_RECENT_APP:
                base = new ReportCellView(context);
                break;
            case CellType.TYPE_REPEAT:
                base = new CarouselCellView(context);
                break;
            case CellType.TYPE_IRR:
                base = new IrregularCellView(context);
                break;
            case CellType.TYPE_ALLIANCE:
                IPopupAnimatView popupImageView = new PopupAnimatView(context);
                popupImageView.setDiskCache(iDiskCache);
                return (TvPageItemView) popupImageView;
            case CellType.TYPE_REPORT:
                base = new SimpleCellView(context);
                break;
            case CellType.TYPE_STATIC_IMAGE:
                base = new SaticImageCellView(context);
                break;
            case CellType.TYPE_BOTTOMTEXT:
                base = new NewImageCellView(context);
                break;
            case CellType.TYPE_ADS_VIDEO_OUTER:
                base = new AdsVideoCellView2(context);
                break;
            case CellType.TYPE_NEW_REPEAT:
                base = new CarouselCellView2(context);
                break;
//            case CellType.TYPE_LIST_IMAGE:
//                base = new HorizontalGridCellView(context);
//                break;
            case CellType.TYPE_CIRCLE_IMAGE:
                base = new CircleImageCellView(context);
                break;
            case CellType.TYPE_FORCE_TEXT:
                base = new ForceTextCellView(context);
                break;
            case CellType.TYPE_STAT_LIST:
                base = new StateListCellView(context);
                break;
            default:
                //默认返回基类
                base = new SimpleCellView(context);
                break;
        }
        base.setDiskCache(iDiskCache);
        base.setBitmapCache(bitmapCache);
        base.setLoadImageResId(resID);
        return base;
    }

    //解析备注中的值
    public static void handleExtend(Context context, CellEntity cell) {
        if (!TextUtils.isEmpty(cell.getExtendData())) {
            FlyBean flyBean = GsonUtils.json2Object(cell.getExtendData(), FlyBean.class);
            if (flyBean != null) {
                float scaleScreen = DisplayUtils.getMetrices((Activity) context).widthPixels / 1920f;
                if (flyBean.getType() != 0) {
                    cell.setType(flyBean.getType());
                }

//                if (flyBean.getWidth() != 0) {
//                    cell.setWidth((int) (flyBean.getWidth() * scaleScreen));
//                }
//
//                if (flyBean.getHeight() != 0) {
//                    cell.setHeight((int) (flyBean.getHeight()*scaleScreen));
//                }

                if (flyBean.getFocusScale() != 0) {
                    cell.setFocusScale(flyBean.getFocusScale());
                }

                if (flyBean.getFocusType() != 0) {
                    cell.setFocusType(flyBean.getFocusType());
                }

                if (flyBean.getShowImageNum() != 0) {
                    cell.setShowImageNum(flyBean.getShowImageNum());
                }

                if (flyBean.getShowRows() != 0) {
                    cell.setShowRows(flyBean.getShowRows());
                }

                if (flyBean.getMaxLine() != 0) {
                    cell.setMaxTextLine(flyBean.getMaxLine());
                }
                if (flyBean.getMinTextLine() != 0) {
                    cell.setMinTextLine(flyBean.getMinTextLine());
                }
                if (!TextUtils.isEmpty(flyBean.getMaskColor())) {
                    cell.setTextMaskColor(flyBean.getMaskColor());
                }

                if (!TextUtils.isEmpty(flyBean.getFont())) {
                    cell.setFont(flyBean.getFont());
                }

            }
        }
    }


}
