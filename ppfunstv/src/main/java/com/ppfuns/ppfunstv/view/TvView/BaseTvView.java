package com.ppfuns.ppfunstv.view.TvView;

import android.content.Context;
import android.os.RemoteException;
import android.support.annotation.DrawableRes;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.ppfuns.marqueeservice.IMarqueeService;
import com.ppfuns.ppfunstv.R;
import com.ppfuns.ppfunstv.data.CellBean;
import com.ppfuns.ppfunstv.data.CellEntity;
import com.ppfuns.ppfunstv.data.ControlBean;
import com.ppfuns.ppfunstv.data.MarqueeEntity;
import com.ppfuns.ppfunstv.data.TabEntity;
import com.ppfuns.ppfunstv.data.TemplateEntity;
import com.ppfuns.ppfunstv.module.BitmapCache;
import com.ppfuns.ppfunstv.module.UpdataVersion.IDiskCache;
import com.ppfuns.ppfunstv.utils.FlyLog;
import com.ppfuns.ppfunstv.utils.GsonUtils;
import com.ppfuns.ppfunstv.utils.SPUtil;
import com.ppfuns.ppfunstv.utils.Utils;
import com.ppfuns.ppfunstv.view.TvView.CellView.ITvPageItemView;
import com.ppfuns.ppfunstv.view.TvView.FocusAnimat.ITvFocusAnimat;
import com.ppfuns.ppfunstv.view.TvView.HeaderView.HeaderLayout;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by FlyZebra on 2016/8/31.
 */
public abstract class BaseTvView extends RelativeLayout {
    protected static final String TAG = BaseTvView.class.getSimpleName();
    protected List<TabEntity> mTabEntityList = new ArrayList<>();
    protected List<CellBean> mCellBeanList = new ArrayList<>();
    protected ControlBean mControlBean;
    private int mPageSize;
    protected static boolean bBgShowing = true;

    protected Animation shake;
    protected Context mContext;
    protected TemplateEntity mTemplateEntity;
    protected IDiskCache iDiskCache;
    protected BitmapCache mBitmapCache;

    protected OnCellItemClick onCellItemClick;

    protected boolean isCreateHeaderLayout = false;
    protected boolean isCreateNavLayout = false;

    protected IOnKeyDownOutEnvent onKeyDownOutEnvent;


    protected boolean isShowReflect;
    protected
    @ITvFocusAnimat.AnimStyle
    int animStyle;

    protected int loadImageResId = R.drawable.tv_default;
    protected boolean toFront;
    public int shadowAmend = 0; //
    protected int animDuration = 200; //ms
    private HeaderLayout.HeadEntity headEntity;
    private IMarqueeService marqueeService;
    private MarqueeEntity mMarqueeEntity;
    protected int[] mFocusResIDs;
    protected boolean isUseWallPager;


    public BaseTvView(Context context) {
        this(context, null);
    }

    public BaseTvView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseTvView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Glide.get(context).clearMemory();
        releaseGlide();
        mContext = context;
        shake = AnimationUtils.loadAnimation(context, R.anim.tv_shake);
        setClipChildren(false);
        setClipToPadding(false);
        mBitmapCache = new BitmapCache(mContext);
    }

    private void releaseGlide() {
        try {
            Class clazz = Class.forName("com.bumptech.glide.Glide");
            Method m = clazz.getDeclaredMethod("tearDown",new Class[]{});
            m.setAccessible(true);// 调用private方法的关键一句话
            FlyLog.d("call Glide tearDown" );
            m.invoke(clazz,new Object[]{});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public BaseTvView setDiskCache(IDiskCache iDiskCache) {
        this.iDiskCache = iDiskCache;
        return this;
    }

    /**
     * 设置占位图
     *
     * @param resId
     */
    public BaseTvView setLoadImageResId(@DrawableRes int resId) {
        this.loadImageResId = resId;
        return this;
    }


    public void setOnKeyDownOutEnvent(IOnKeyDownOutEnvent iOnKeyDownOutEnvent) {
        this.onKeyDownOutEnvent = iOnKeyDownOutEnvent;
    }

    /**
     * 控制最后一排是否显示倒影镜像
     *
     * @param showReflect
     * @return
     */
    public BaseTvView setShowReflect(boolean showReflect) {
        isShowReflect = showReflect;
        return this;
    }


    public BaseTvView setAnimStyle(
            @ITvFocusAnimat.AnimStyle int style) {
        animStyle = style;
        return this;
    }

    public BaseTvView setAnimDuration(int animDuration) {
        this.animDuration = animDuration;
        return this;
    }


    public BaseTvView setShadowAmend(int shadowAmend) {
        this.shadowAmend = shadowAmend;
        return this;
    }


    public BaseTvView setHeadEntity(HeaderLayout.HeadEntity headEntity) {
        this.headEntity = headEntity;
        return this;
    }


    /**
     * 设置导航菜单显示数据，动态生成导航菜单界面所需数据
     * 备注:如果需要根据页面图片数量多少调整页面数量,需将本方法放置在@setTvPageData方法后
     *
     * @param templateEntity
     */
    public BaseTvView setNavData(TemplateEntity templateEntity) {
        mTemplateEntity = templateEntity;
        List<TabEntity> tabEntityList = templateEntity.getTabList();
        if (tabEntityList != null && tabEntityList.size() > 0) {
            this.mTabEntityList.clear();
            if (mPageSize > 0) {
                tabEntityList = adjustTabSize(tabEntityList, mPageSize);
            }
            this.mTabEntityList.addAll(tabEntityList);
        }
        return this;
    }

    public BaseTvView setCreateHeaderLayout(boolean createHeaderLayout) {
        isCreateHeaderLayout = createHeaderLayout;
        return this;
    }

    public BaseTvView setCreateNavLayout(boolean createNavLayout) {
        isCreateNavLayout = createNavLayout;
        return this;
    }


    /**
     * 动态生成页面
     * 备注:如果需要根据页面图片数量多少调整页面数量,需将本方法放置在@setNavData方法前
     *
     * @param cellBeanList
     * @return
     */
    /**
     * 加载页面内存调整因子
     */
    public static final double ADJUST_MEMORY_FACTOR = 0.75f;

    public BaseTvView setTvPageData(List<CellBean> cellBeanList) {
        if (cellBeanList != null && cellBeanList.size() > 0) {
            this.mCellBeanList.clear();
            if (cellBeanList.size() > 1) {
                mPageSize = adjustMemory(cellBeanList, ADJUST_MEMORY_FACTOR);
                cellBeanList = adjustCellSize(cellBeanList, mPageSize);
            }
            this.mCellBeanList.addAll(cellBeanList);
        }
        return this;
    }

    public BaseTvView setControlData(ControlBean controlBean) {
        this.mControlBean = controlBean;
        return this;
    }


    /**
     * 添加跑马灯
     */
    protected void createMaqueeView(MarqueeEntity marqueeEntity) {
        FlyLog.d(marqueeEntity != null ? "not null" : "null" );

        mMarqueeEntity = marqueeEntity;
        /**
         * 加载跑马灯
         */
        if(marqueeEntity!=null){
            try {
                if(marqueeService != null){
                    String text = "";
                    float textScale = 0.56f;

                    if(mMarqueeEntity.getText()!=null){
                        Map<String, String> str = GsonUtils.json2Map(mMarqueeEntity.getText());
                        text = Utils.getLocalLanguageString(str);
                    }

                    if(TextUtils.isEmpty(text)){
                        return;
                    }

                    marqueeService.setPoint(mMarqueeEntity.getX(),mMarqueeEntity.getY(),mMarqueeEntity.getWidth(),mMarqueeEntity.getHeight());
                    marqueeService.setText(text);
                    marqueeService.setTextSize((int) (mMarqueeEntity.getSize()*textScale));
                    marqueeService.setTextColor(mMarqueeEntity.getColor());
                    marqueeService.setDirection(mMarqueeEntity.getDirection());
                    marqueeService.setDuration(mMarqueeEntity.getSpeed());
                    marqueeService.init();
                    marqueeService.bind();
                    marqueeService.play();

                    if (bBgShowing) {
                        marqueeService.stop();
                    } else {
                        marqueeService.play();
                    }
                }

            }catch (RemoteException e){
                e.printStackTrace();

            }
        }
    }

    /**
     * 根据所要加载的页面计算所需的内存,为避免出现OOM,限制加载页面的内存为总内存的adjustFactor(默认3/4)
     * 如果超出总内存的adjustFactor的话,就自动调整所要加载的页面数量
     *
     * @param adjustFactor 调整因子
     * @return 返回调整后的页面数量
     * 备注: 这里采用的计算图片所占内存大小是理论上的最大值,但是由于采用Glide第三方控件,在加载图片时对图片
     * 进行了处理,导致计算出来的结果比实际占用的结果大,所以默认的调整因子设置的比较大(3/4).这个算法
     * 有待优化.
     */
    /**
     * glide加载图片时图片格式所占的大小
     */
    public static int GLIDE_IMAGE_BYTES_PER_PIXEL = 2;

    protected int adjustMemory(List<CellBean> cellBeanList, double adjustFactor) {
        int length = cellBeanList.size();
        if (cellBeanList != null) {
            int oldSize = cellBeanList.size();
            int memory = 0;//所需要的内存
            long maxMemory = Runtime.getRuntime().maxMemory();//最大内存
            FlyLog.i(TAG + " maxMemory:" + maxMemory / 1024 / 1024);
            double allowMemory = maxMemory * adjustFactor;
            for (int i = 0; i < oldSize; i++) {
                List<CellEntity> cells = cellBeanList.get(i).getCellList();
                if (cells == null || cells.size() == 0) {
                    continue;
                }
                for (CellEntity entity : cells) {
                    if (entity.getImgUrl() != null) {
                        double x = entity.getX();
                        double y = entity.getY();
                        memory += x * y * GLIDE_IMAGE_BYTES_PER_PIXEL;
                    }
                }
                FlyLog.i(TAG + " current i:" + i + " need memory:" + memory / 1024 / 1024);
                if (memory > allowMemory) {
                    length = i;
                    break;
                }
            }
        }
        return length;
    }

    /**
     * 根据调整后的页面大小返回对应大小的页面数据
     *
     * @param cellBeanList 待调整大小的页面数据
     * @param length       调整的页面大小
     * @return 返回调整后的页面数据
     */
    protected List<CellBean> adjustCellSize(List<CellBean> cellBeanList, int length) {
        if (cellBeanList != null && cellBeanList.size() > length) {
            for (int i = cellBeanList.size() - 1; i >= length; i--) {
                cellBeanList.remove(i);
            }
        }
        return cellBeanList;
    }

    /**
     * 根据调整后的页面大小返回对应大小的一级导航栏数据
     *
     * @param tabs   待调整大小的一级导航栏数据
     * @param length 调整的页面大小
     * @return 返回调整后的一级导航栏数据
     */
    protected List<TabEntity> adjustTabSize(List<TabEntity> tabs, int length) {
        if (tabs != null && tabs.size() > length) {
            for (int i = tabs.size() - 1; i >= length; i--) {
                tabs.remove(i);
            }
        }
        return tabs;
    }

    /**
     * 创建控件
     */
    public abstract void createView();

    public abstract void notifyPageChange(int i);

    @Override
    protected void onAttachedToWindow() {
        FlyLog.d("onAttachedToWindow....");
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        FlyLog.d("onDetachedFromWindow....");
        /**
         * 如果已使用跑马灯，需释放跑马灯资源
         */

        try{
            if (marqueeService != null) {
                marqueeService.stop();
                marqueeService = null;
            }


        }catch (Exception e){
            e.printStackTrace();
        }

//        SubScriptView.mBitmapCache = null;

        super.onDetachedFromWindow();
    }

    protected void setHomePage() {

    }

//    /**
//     * 设置导航栏margin
//     *
//     * @param top
//     * @param left
//     */
//    protected BaseTvView setNavMargin(int top, int left) {
//        if (top > 50 && left > 50) {//大于50才设置,否则使用默认值
//            mNavTopMargin = top;
//            mNavLeftMargin = left;
//        }
//        return this;
//    }

    protected void startShake(View view) {
        boolean bShake = (boolean) SPUtil.get(mContext, SPUtil.FILE_CONFIG, SPUtil.CONFIG_SHAKE, false);
        if (bShake) {
            view.startAnimation(shake);
        }
    }

    /**
     * 加载页面消失时需要做的处理
     */
    public void loadingDismiss() {
        setLoadShowing(false);
        startWork();
    }

    public void setLoadShowing(boolean bShow) {
        bBgShowing = bShow;
    }

    public void startWork() {
        if(marqueeService != null){
            try {
                marqueeService.play();
            }catch (RemoteException e){
                e.printStackTrace();
            }

        }
    }

    public void stopWork() {

        if(marqueeService != null){
            try {
                marqueeService.stop();
            }catch (RemoteException e){
                e.printStackTrace();
            }

        }
    }

    public void setOnCellItemClick(OnCellItemClick onCellItemClick) {
        this.onCellItemClick = onCellItemClick;
    }

    public BaseTvView setFocusResIDs(int[] mFocusResIDs) {
        this.mFocusResIDs = mFocusResIDs;
        return this;
    }

    public interface OnCellItemClick {
        void onCellItemClick(ITvPageItemView view);
    }

    protected HeaderLayout addHeaderView(Context context, HeaderLayout mHeaderLayout, float screenScale) {
        if (mHeaderLayout != null) {
            removeView(mHeaderLayout);
        }
        mHeaderLayout = new HeaderLayout(context);
        mHeaderLayout.setHeadEntity(this.headEntity);
        if (mControlBean != null) {
            mHeaderLayout.setData(mControlBean.getLogo(), iDiskCache, screenScale);
        }
        addView(mHeaderLayout);
        return mHeaderLayout;
    }


    /**
     * 设置中间动态UI控件失去焦点
     */
    public abstract void setTvPageLoseFocus();


    public HeaderLayout getHeaderLayout() {
        return null;
    }

    public void onPause(){
        try{
            FlyLog.d("marquee is global "+mMarqueeEntity.isGlobal());
            if(!mMarqueeEntity.isGlobal() && marqueeService != null) {
                marqueeService.stop();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void onResume(){
        try{
            if(!mMarqueeEntity.isGlobal() && marqueeService != null) {
                marqueeService.play();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setIMarqueeService(IMarqueeService iMarqueeService){
        marqueeService = iMarqueeService;
    }


    public BaseTvView isUseWallPager(boolean isUseWallPager){
        this.isUseWallPager= isUseWallPager;
        return this;
    }

}
