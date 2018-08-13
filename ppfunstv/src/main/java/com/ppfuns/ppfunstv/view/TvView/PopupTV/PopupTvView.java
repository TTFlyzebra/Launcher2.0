package com.ppfuns.ppfunstv.view.TvView.PopupTV;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;

import com.ppfuns.ppfunstv.data.CellBean;
import com.ppfuns.ppfunstv.data.CellEntity;
import com.ppfuns.ppfunstv.utils.BehavioralUtil;
import com.ppfuns.ppfunstv.utils.DisplayUtils;
import com.ppfuns.ppfunstv.utils.FlyLog;
import com.ppfuns.ppfunstv.utils.GsonUtils;
import com.ppfuns.ppfunstv.utils.SPUtil;
import com.ppfuns.ppfunstv.utils.Utils;
import com.ppfuns.ppfunstv.view.ClearTopRectView;
import com.ppfuns.ppfunstv.view.TvView.BaseTvView;
import com.ppfuns.ppfunstv.view.TvView.CellView.ITvPageItemView;
import com.ppfuns.ppfunstv.view.TvView.HeaderView.HeaderLayout;
import com.ppfuns.ppfunstv.view.TvView.IOnKeyDownOutEnvent;

import java.util.List;


/**
 * Created by FlyZebra on 2016/9/1.
 */
public class PopupTvView extends BaseTvView {
    private Context context;
    private HeaderLayout mHeaderLayout;
    private PopupTvViewLayout popupTvViewLayout;
    private float screenScale = 1.0f;
//    private int toFront;

    public PopupTvView(Context context) {
        this(context, null);
    }

    public PopupTvView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PopupTvView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        screenScale = DisplayUtils.getMetrices((Activity) this.context).widthPixels / 1920f;
    }

    @Override
    public void createView() {

        if (popupTvViewLayout != null) {
            removeView(popupTvViewLayout);
        }
        if (mCellBeanList != null && mCellBeanList.size() > 0) {

            int age_range = (int) SPUtil.get(context, SPUtil.FILE_CONFIG, SPUtil.CONFIG_AGE_RANGE, 0);
            if (mCellBeanList.size() <= age_range) {
                age_range = 0;
            }

            CellBean cellBean = mCellBeanList.get(age_range);

            ClearTopRectView clearTopRectView = new ClearTopRectView(mContext);
            LayoutParams lp1 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            clearTopRectView.setLayoutParams(lp1);

            List<CellEntity> list = cellBean.getCellList();
            int top = Integer.MAX_VALUE;
            int bottom = 0;

            try {
                top = mTemplateEntity.getY() + 60;
            } catch (Exception e) {
                top = Integer.MAX_VALUE;
            }

            if (list != null) {
                for (CellEntity cell : list) {
                    top = Math.min(cell.getY()-20, top);
                    bottom = Math.max(cell.getY() + cell.getHeight(), bottom);
                }
            } else {
                top = 0;
            }
            if (top > 0 && bottom > 1080) {
                clearTopRectView.setClearHeight((int) (top * screenScale));
            }

            popupTvViewLayout = new PopupTvViewLayout.Builder()
                    .context(context)
                    .shadowBringToFront(this.toFront)
                    .shadowAmend(this.shadowAmend)
                    .isUseWallPager(isUseWallPager)
                    .context(this.context)
                    .isShowReflect(this.isShowReflect)
                    .setLoadImageResID(loadImageResId)
                    .animDuration(animDuration)
                    .animStyle(animStyle)
                    .iDiskCache(iDiskCache)
                    .setBitmapCache(mBitmapCache)
                    .setFocusResIDs(mFocusResIDs)
                    .onCellItemClick(new OnCellItemClick() {
                        @Override
                        public void onCellItemClick(ITvPageItemView view) {
                            if (onCellItemClick != null) {
                                onCellItemClick.onCellItemClick(view);
                            }
                        }
                    })
                    .onKeyDownOutEnvent(new IOnKeyDownOutEnvent() {
                        @Override
                        public boolean onKeyDownGoLeft(View view) {
                            startShake(popupTvViewLayout);
                            if (onKeyDownOutEnvent != null) {
                                onKeyDownOutEnvent.onKeyDownGoLeft(view);
                            }
                            return false;
                        }

                        @Override
                        public boolean onKeyDownGoRight(View view) {
                            startShake(popupTvViewLayout);
                            if (onKeyDownOutEnvent != null) {
                                onKeyDownOutEnvent.onKeyDownGoRight(view);
                            }
                            return false;
                        }

                        @Override
                        public boolean onKeyDownGoUp(View view) {
                            if (onKeyDownOutEnvent != null) {
                                onKeyDownOutEnvent.onKeyDownGoUp(view);
                            }
                            return false;
                        }

                        @Override
                        public boolean onKeyDownGoDown(View view) {
                            if (onKeyDownOutEnvent != null) {
                                onKeyDownOutEnvent.onKeyDownGoDown(view);
                            }
                            return false;
                        }
                    }).createView();

            LayoutParams lp2 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            popupTvViewLayout.setLayoutParams(lp2);
            //上报行为数据
            if (mTabEntityList != null && mTabEntityList.size() > age_range) {
                String name = mTabEntityList.get(age_range).getName();
                try {
                    String tabName = Utils.getLocalLanguageString(GsonUtils.json2Map(name));
                    if (!TextUtils.isEmpty(tabName)) {
                        String id = mTabEntityList.get(age_range).getId() + "";
                        BehavioralUtil.reportPageEvent(mContext, tabName, id);
                        SPUtil.setEvent(context, SPUtil.EVENT_OUT, "0");
                    }
                } catch (Exception e) {
                    FlyLog.e(e.toString());
                    e.printStackTrace();
                }

            }
            popupTvViewLayout.setData(cellBean);

            clearTopRectView.addView(popupTvViewLayout);
            addView(clearTopRectView);
        }

        if (isCreateHeaderLayout) {
            addHeaderView(context, mHeaderLayout, screenScale);
//            mHeaderLayout.setChildFocusListenr();
        }

        if (mControlBean != null && mControlBean.getMarqueeEntity() != null) {
            createMaqueeView(mControlBean.getMarqueeEntity());
        }

    }

    /**
     * 通知页面改变
     *
     * @param type 0:进入页面 1:退出页面
     */
    @Override
    public void notifyPageChange(int type) {
        if (popupTvViewLayout != null) {
            popupTvViewLayout.notifyPageChange(type);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        FlyLog.d("<PopupTvVIew>dispatchKeyEvent event=" + event);
        return super.dispatchKeyEvent(event);
    }

    public void setTvPageLoseFocus() {
        try {
            popupTvViewLayout.loseFocus();
        } catch (Exception e) {
            FlyLog.e(e.getMessage());
        }
    }

    @Override
    public HeaderLayout getHeaderLayout() {
        return this.mHeaderLayout;
    }
}
