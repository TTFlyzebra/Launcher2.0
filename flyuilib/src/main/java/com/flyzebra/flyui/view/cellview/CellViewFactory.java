package com.flyzebra.flyui.view.cellview;

import android.content.Context;

import com.flyzebra.flyui.bean.CellBean;


/**
 * 每页中cell控件生成类
 * Created by FlyZebra on 2016/6/15.
 */
public class CellViewFactory {
    /**
     * 根据传入的AppInfo构建对应的自定义pageItemView
     *
     * @param context
     */
    public static ICell createView(Context context, CellBean cellBean) {
        ICell iCellView;
        switch (cellBean.celltype) {
            case CellType.TYPE_APP_NAV:
                iCellView = new SimpleNavCellView(context);
                break;
            case CellType.TYPE_IMAGE_TEXT:
                iCellView = new ImageTextCellView(context);
                return iCellView;
            case CellType.TYPE_NUM_TEXT:
                iCellView = new NumTextCellView(context);
                return iCellView;
            case CellType.TYPE_APP_MIRRORIMG:
                iCellView = new MirrorCellView(context);
                break;
            case CellType.TYPE_APP_DATE:
                iCellView = new DateCellView(context);
                break;
            case CellType.TYPE_APP_CONTROL:
                iCellView = new ControlCellView(context);
                return iCellView;
            case CellType.TYPE_SEEKBAR:
                iCellView = new SeekbarCellView(context);
                return iCellView;
            case CellType.TYPE_LOOPPLAY:
                iCellView = new LoopPlayCellView(context);
                return iCellView;
            case CellType.TYPE_LISTVIEW:
                iCellView = new RecyclerCellView(context);
                return iCellView;
            case CellType.TYPE_ANIMTOR:
                iCellView = new AnimtorCellView(context);
                return iCellView;
            case CellType.TYPE_FRAGMENT:
                iCellView = new FragmentCellView(context);
                return iCellView;
            case CellType.TYPE_PAGE:
                iCellView = new FramlayoutCellView(context);
                return iCellView;
            case CellType.TYPE_FRAGMENT_NAV:
                iCellView = new FragmentNavCellView(context);
                return iCellView;
            case CellType.TYPE_GROUP_LIST:
                iCellView = new GrouplistCellView(context);
                return iCellView;

            case CellType.TYPE_LRCVIEW:
                iCellView = new LrcViewCellView(context);
                return iCellView;
            case CellType.TYPE_APP_NORMAL:
            default:
//                boolean isText = cellBean.texts != null && cellBean.texts.size() == 1;
//                boolean isImage = cellBean.images != null && cellBean.images.size() == 1;
//                iCellView = isImage && isText ? new SimpleCellView(context)
//                        : isText ? new SimpleTextCellView(context)
//                        : isImage ? new SimpleImageCellView(context)
//                        : new SimpleCellView(context);
                iCellView = new SimpleCellView(context);
                break;
        }
        return iCellView;
    }

}
