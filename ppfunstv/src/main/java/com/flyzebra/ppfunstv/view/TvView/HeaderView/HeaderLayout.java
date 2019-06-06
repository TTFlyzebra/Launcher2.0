package com.flyzebra.ppfunstv.view.TvView.HeaderView;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.flyzebra.ppfunstv.R;
import com.flyzebra.ppfunstv.data.ActionEntity;
import com.flyzebra.ppfunstv.data.LogoEntity;
import com.flyzebra.ppfunstv.module.UpdataVersion.IDiskCache;
import com.flyzebra.ppfunstv.receiver.UsbStateReceiver;
import com.flyzebra.ppfunstv.utils.FlyLog;
import com.flyzebra.ppfunstv.utils.SPUtil;
import com.flyzebra.ppfunstv.utils.SystemPropertiesProxy;
import com.flyzebra.ppfunstv.view.TvView.CellView.CellClickAction.CommondTool;
import com.flyzebra.ppfunstv.view.TvView.IOnKeyDownOutEnvent;
import com.flyzebra.ppfunstv.view.WeatherTemView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2016/6/14.
 * 顶部控件,用于显示logo以及状态栏(通过addView进行添加)
 */
public class HeaderLayout extends RelativeLayout {
    /**
     * 默认asset根目录
     */
    public static final String DEFAULT_ASSET_PATH = "file:///android_asset/ppfuns/";
    public static final String LOGO_PATH_DEFAULT = "/data/data/logo.png";
    public static final String LOGO_PATH = "persist.sys.launcher.logo.path";
    private ImageView mLogo;
    private LogoEntity mLogoEntity;
    private List<IHeaderImage> viewList = new ArrayList();
    private HeaderNetView networkImg;
    private HeaderSearchView searchIv;
    private HeaderSetView setIv;
    private Context mContext;


    public ImageView iv_search;
    public ImageView iv_filter;


    private WeatherTemView weatherTemView;

    private float mScale = 1.0f;
    private UsbStateReceiver usbReceiver;
    private ListenerLostOnKeyDown listenerLostOnKeyDown;
    private IDiskCache iDiskCache;
    private IOnKeyDownOutEnvent onKeyDownOutEnvent;
    private TextView titleView;


    private HeadEntity headEntity;

    public HeadEntity getHeadEntity() {
        return headEntity;
    }

    public void setHeadEntity(final HeadEntity headEntity) {
        this.headEntity = headEntity;
        if (headEntity == null) return;

        if (!TextUtils.isEmpty(headEntity.headTitle)) {
            if(headEntity.showTitle) {
                titleView.setVisibility(VISIBLE);
                titleView.setText(headEntity.headTitle);
            }else{
                titleView.setVisibility(GONE);
            }
        } else {
            titleView.setVisibility(GONE);
        }


        if (headEntity.showWeather) {
            weatherTemView.setVisibility(VISIBLE);
            weatherTemView.sendMsgToUpdate();
        } else {
            weatherTemView.setVisibility(GONE);
            weatherTemView.cancelStateInfo();
        }


        if (headEntity.showSearch) {
            iv_search.setVisibility(VISIBLE);
            iv_search.setFocusable(true);
        } else {
            iv_search.setVisibility(GONE);
        }

        if (headEntity.showFilter) {
            iv_filter.setVisibility(VISIBLE);
            iv_filter.setFocusable(true);
            if (!headEntity.showSearch) {
                RelativeLayout.LayoutParams layoutParams = (LayoutParams) iv_filter.getLayoutParams();
                layoutParams.setMargins(78, 58, 0, 0);
                iv_filter.setLayoutParams(layoutParams);
            }
        } else {
            iv_filter.setVisibility(GONE);
        }


        iv_search.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (headEntity.searchAction != null) {
                    CommondTool.execStartActivity(mContext, headEntity.searchAction.getIntent(), null, null, false);
                    FlyLog.d();
                }
            }
        });


        iv_filter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                FlyLog.d();
            }
        });
    }

    //    public boolean isShowLogo() {
//        return showLogo;
//    }
//
//    public void setShowLogo(boolean showLogo) {
//        this.showLogo = showLogo;
//    }

    public HeaderLayout(Context context) {
        this(context, null);
    }

    public HeaderLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeaderLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setFocusable(false);
        init(context);
        FlyLog.d(" onCreate...");
    }

    private void init(Context context) {

            this.mContext = context;
            View iconView = LayoutInflater.from(context).inflate(R.layout.tv_header_layout, null);
            this.addView(iconView);
            mLogo = (ImageView) iconView.findViewById(R.id.tv_header_logo);
            titleView = (TextView) iconView.findViewById(R.id.tv_head_title);

            iv_filter = (ImageView) iconView.findViewById(R.id.tv_head_filter);
            iv_search = (ImageView) iconView.findViewById(R.id.tv_head_serarch);
            //        Glide.with(context).load(R.drawable.tv_header_logo).diskCacheStrategy(DiskCacheStrategy.NONE).into(mLogo);

            mLogo.setImageResource(R.drawable.tv_header_logo);
            //        searchIv = (HeaderSearchView) iconView.findViewById(R.id.tv_header_search);
            //        viewList.add(searchIv);
            //        setIv = (HeaderSetView) iconView.findViewById(R.id.tv_header_setting);
            //        viewList.add(setIv);
            networkImg = (HeaderNetView) iconView.findViewById(R.id.tv_header_wifi);
            viewList.add(networkImg);
        try{
            weatherTemView = (WeatherTemView) iconView.findViewById(R.id.weather_temperature);
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void addView(View child, int index, android.view.ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
    }

    public ImageView getLogo() {
        return mLogo;
    }

    public void setLogo(ImageView mLogo) {
        this.mLogo = mLogo;
    }

    public void setScale(int scale) {
        mScale = scale;
    }

    public void setListenerLostOnKeyDown(ListenerLostOnKeyDown listenerLostOnKeyDown) {
        this.listenerLostOnKeyDown = listenerLostOnKeyDown;
    }

    /**
     * 根据配置文件调整logo图片位置
     */

    public void setData(LogoEntity logoEntity, IDiskCache iDiskCache, float scale) {
        this.iDiskCache = iDiskCache;
        mLogoEntity = logoEntity;
        mScale = scale;
        setLogoInfo();
    }

    private void setLogoInfo() {
        if (headEntity == null) {
            mLogo.setVisibility(INVISIBLE);
            return;
        } else {
            if (headEntity.showLogo) {
                mLogo.setVisibility(VISIBLE);
            } else {
                mLogo.setVisibility(INVISIBLE);
            }


        }
        if (mLogoEntity != null && mLogoEntity.getHeight() != 0 && 0 != mLogoEntity.getWidth()) {
            LayoutParams lp = (LayoutParams) mLogo.getLayoutParams();
            lp.width = (int) (mLogoEntity.getWidth() * mScale);
            lp.height = (int) (mLogoEntity.getHeight() * mScale);
            if (0 != mLogoEntity.getX()) {
                lp.leftMargin = (int) (mLogoEntity.getX() * mScale);
            }
            if (0 != mLogoEntity.getY()) {
                lp.topMargin = (int) (mLogoEntity.getY() * mScale);
            }
            mLogo.setLayoutParams(lp);
        }
        if (mLogoEntity != null) {
            String imgUrl = mLogoEntity.getImgUrl();
            if (!TextUtils.isEmpty(imgUrl)) {
                String filePath = iDiskCache.getBitmapPath(imgUrl);
                FlyLog.d("set log imgurl = %s", filePath);
                Glide.with(mContext).load(filePath).diskCacheStrategy(DiskCacheStrategy.NONE).into(mLogo);
            }
        } else {
            //如果没有配置logo信息,根据系统属性进行配置
            String logoPath = SystemPropertiesProxy.get(mContext, LOGO_PATH, LOGO_PATH_DEFAULT);
            if (logoPath != null) {
                File file = new File(logoPath);
                if (file != null && file.exists()) {
                    Glide.with(mContext).load(file).diskCacheStrategy(DiskCacheStrategy.NONE).into(mLogo);
                }
            }
        }
    }
    //    @Override
    //    public void onClick(View v){
    //        if(v.getId() == R.id.tv_header_search){
    //            String data = "searchType=all";
    //            CommondTool.execStartActivityAndShowTip(mContext, Constants.Action.VOD_SEARCH,
    // data,
    //                    "", false);
    //        }else if(v.getId() == R.id.tv_header_wifi){
    //            CommondTool.execStartActivity(mContext, R.string.actionSetinngNetwork);
    //        }else if(v.getId() == R.id.tv_header_setting){
    //            CommondTool.execStartActivity(mContext, R.string.actionSettings);
    //        }
    //    }

    public interface OnItemClick {
        void onItemClick(View view);
    }

    public interface ListenerLostOnKeyDown {
        void onKeyDownGoFore();

        void onKeyDownGoNext();
    }

    public void setOnKeyDownOutEnvent(IOnKeyDownOutEnvent onKeyDownOutEnvent) {
        this.onKeyDownOutEnvent = onKeyDownOutEnvent;
    }

    private int selectItem = 0;

    private void select(int currentItem) {
        setSelectItem(currentItem, true);
    }

    public void setSelectItem(int selectItem, boolean flag) {

        FlyLog.d("set select item....");
        int count = viewList.size();
        if (this.selectItem < count && this.selectItem > -1) {
            viewList.get(this.selectItem).setFocusImage(false);
        }
        if (flag) {
            viewList.get(selectItem).setFocusImage(true);
        } else {
            viewList.get(selectItem).setFocusImage(false);
        }
        this.selectItem = selectItem;
    }

    public int getSelectItem() {
        return selectItem;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (viewList.size() < 1) {
            return super.onKeyDown(keyCode, event);
        }
        int left = ((View) viewList.get(selectItem)).getLeft();
        int right = ((View) viewList.get(selectItem)).getRight();
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (onKeyDownOutEnvent != null) {
                    onKeyDownOutEnvent.onKeyDownGoDown(this);
                }
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                if (onKeyDownOutEnvent != null) {
                    onKeyDownOutEnvent.onKeyDownGoUp(this);
                }
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (left >= 0) {
                    int cur = selectItem == -1 ? 0 : selectItem;
                    boolean tvpage_loop = (boolean) SPUtil.get(mContext, SPUtil.FILE_CONFIG,
                            SPUtil.CONFIG_CIRCULATION_FLAG, false);
                    int next = cur - 1 >= 0 ? cur - 1 : (tvpage_loop ? viewList.size() - 1 : 0);
                    //listView.size()-1  不让循环
                    select(next);
                } else {
                    if (onKeyDownOutEnvent != null) {
                        onKeyDownOutEnvent.onKeyDownGoLeft(this);
                    }
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (right >= 0) {
                    int cur = selectItem == -1 ? 0 : selectItem;
                    boolean tvpage_loop = (boolean) SPUtil.get(mContext, SPUtil.FILE_CONFIG,
                            SPUtil.CONFIG_CIRCULATION_FLAG, false);
                    int next = cur + 1 == viewList.size() ? (tvpage_loop ? 0 : viewList.size() -
                            1) : cur + 1;//listView.size() 不让循环
                    select(next);
                } else {
                    if (onKeyDownOutEnvent != null) {
                        onKeyDownOutEnvent.onKeyDownGoRight(this);
                    }
                }
                break;
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_DPAD_CENTER:
                try {
                    viewList.get(getSelectItem()).onKeyDown(keyCode, event);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void setChildFocusListenr() {
        if (viewList != null) {
            for (IHeaderImage iHeaderImage : viewList) {
                View view = (View) iHeaderImage;
                view.setFocusable(true);
                view.setFocusableInTouchMode(true);
                view.setOnFocusChangeListener(new OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        ((IHeaderImage) v).setFocusImage(hasFocus);
                    }
                });
            }
        }
    }

    public static class HeadEntity {
        public boolean showTitle;
        public String headTitle;
        public boolean showLogo;
        public boolean showWeather;
        public boolean showSearch;
        public boolean showFilter;


        public ActionEntity searchAction;
        public ActionEntity filterAction;

        public HeadEntity(boolean showTitle, String headTitle, boolean showLogo) {
            this.showTitle = showTitle;
            this.headTitle = headTitle;
            this.showLogo = showLogo;
        }

        List<LogoEntity> list;
    }
}
