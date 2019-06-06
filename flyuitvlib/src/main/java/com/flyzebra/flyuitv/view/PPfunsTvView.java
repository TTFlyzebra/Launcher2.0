package com.flyzebra.flyuitv.view;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

import com.flyzebra.ppfunstv.R;
import com.flyzebra.ppfunstv.utils.BehavioralUtil;
import com.flyzebra.ppfunstv.utils.DialogUtil;
import com.flyzebra.ppfunstv.utils.DisplayUtils;
import com.flyzebra.ppfunstv.utils.FlyLog;
import com.flyzebra.ppfunstv.utils.SPUtil;
import com.flyzebra.ppfunstv.view.TvView.BaseTvView;
import com.flyzebra.ppfunstv.view.TvView.CellView.ITvPageItemView;
import com.flyzebra.ppfunstv.view.TvView.HeaderView.HeaderLayout;
import com.flyzebra.ppfunstv.view.TvView.HomeWatcher;
import com.flyzebra.ppfunstv.view.TvView.IOnKeyDownOutEnvent;
import com.flyzebra.ppfunstv.view.TvView.NavView.NavItemView;
import com.flyzebra.ppfunstv.view.TvView.NavView.NavLayout;
import com.flyzebra.ppfunstv.view.TvView.PPfunsTV.TvPageLayout;

import java.util.List;

/**
 * 主应用动态布局控件，实现功能
 * 1.各种按键消息传递响应
 * 2.动态生成加载导航菜单
 * 3.动态生成加载分页界面内容
 * Created by FlyZebra on 2016/8/31.
 */
public class PPfunsTvView extends BaseTvView {
    public static final int FOCUS_IN_HEADER = 0;
    public static final int FOCUS_IN_NAV = 1;
    public static final int FOCUS_IN_TVPAGE = 2;
    public int mCurrentFoucs = FOCUS_IN_NAV;
    public ViewPager mViewPager;
    private boolean isSetDefaultFocus = false;
    private Context context;
    private HeaderLayout mHeaderLayout;
    private NavLayout mNavLayout;
    private float screenScale;
    private boolean IS_PLAY_VIEWPAGER_ANIM = false;//控制打开关闭ViewPage默认切换动画
    private MyPgaeAdapter mAdapter;
    //    private TvPageLayout mTvPageArrays[];
    private int[] mTabCountList;
    private TvPageLayout mCurrentTvPageLayout;
    //    private int lastViewPagerPoingt;
    private boolean bChangeTvPage;
    private boolean isViewPageScolling;
    private boolean isDelayPlayViewPager = false; //按锓延迟开关
    private HomeWatcher homeWatcher;

    public PPfunsTvView(Context context) {
        this(context, null);
    }

    /*PPfunsTvView 的动画时间要比POPUPTvView的短体验才会好*/
    @Override
    public BaseTvView setAnimDuration(int animDuration) {
        if(animDuration>300){
            animDuration = animDuration-200;
        }
        return super.setAnimDuration(animDuration);
    }

    public PPfunsTvView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PPfunsTvView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        screenScale = DisplayUtils.getMetrices((Activity) this.context).widthPixels / 1920f;
        homeWatcher = new HomeWatcher(context);
    }

    @Override
    public void createView() {
        try {
        /*
        根据后排模板配置的默认焦点的tab页，优先加载默认焦点Tab页面的图片，
        加载顺序为：
        start-------focusTab--------end
        1.先加载focusTab页的图片；
        2.加载focusTab+i图片；
        3.加载focusTab-i图片；
        4.递归调用2-3步
        5.最后加载剩余部分的图片
        * */
        if (mTemplateEntity!=null && !mTabEntityList.isEmpty()){
            mTabCountList = new int[mTabEntityList.size()];
            int focusTab = 0;
            for (int i = 0; i < mTabEntityList.size(); i++) {
                if (mTabEntityList.get(i).getId() == mTemplateEntity.getDefaultTabId()) {
                    focusTab = i;
                    break;
                }
            }
            int count = 0;
            int cellBeanCunt = mCellBeanList.size();
            /*for (int i = 0; (focusTab+i) < mCellBeanList.size(); i++){
                mTabCountList[focusTab] = count;
                count = count+mCellBeanList.get(i).getCellList().size();
                int fore = -(i-focusTab);
            }*/
            int fisrtloop = Math.min(focusTab,cellBeanCunt-focusTab);
            int lastloop = cellBeanCunt-2*fisrtloop;
            mTabCountList[focusTab] = 0;
            count = count +mCellBeanList.get(focusTab).getCellList().size();
            for (int i=1;i<=fisrtloop;i++){
                mTabCountList[focusTab+i] = count;
                count = count+mCellBeanList.get(focusTab+i).getCellList().size();
                mTabCountList[focusTab-i] = count;
                count = count+mCellBeanList.get(focusTab-i).getCellList().size();
            }
            if(lastloop > 0) {
                for (int i = 1; i < lastloop; i++) {
                    mTabCountList[focusTab + fisrtloop+ i] = count;
                    count = count + mCellBeanList.get(focusTab + fisrtloop+ i).getCellList().size();
                }
            }else if (lastloop < 0){
                for (int i = 0; i < -lastloop; i++) {
                    mTabCountList[i] = count;
                    count = count + mCellBeanList.get(i).getCellList().size();
                }
            }else{
                //do nothing
            }
        }

        }catch (Exception e){
            FlyLog.d(e.toString());
        }

        //创建添加顶部状态栏菜单
        if (isCreateHeaderLayout) {
            addHeaderView(context, mHeaderLayout, screenScale);
        }
        //创建导航菜单
        if (mTabEntityList != null && isCreateNavLayout) {
            if (mNavLayout != null) {
                removeView(mNavLayout);
            }
            mNavLayout = new NavLayout(context);
            LayoutParams lp2 = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp2.leftMargin = (int) (mTemplateEntity.getX() * screenScale);
            lp2.topMargin = (int) (mTemplateEntity.getY() * screenScale);
            lp2.rightMargin = (int) (mTemplateEntity.getX() * screenScale);
            mNavLayout.setLayoutParams(lp2);
            mNavLayout.setScreenScale(screenScale);
            mNavLayout.setTabData(mTabEntityList);

            mNavLayout.setOnItemClick(new NavLayout.OnItemClick() {
                @Override
                public void onItemClick(View view) {
                    int current = (int) view.getTag();
//                    notifyPageChange(1);//通知离开本页面
                    mViewPager.setCurrentItem(current, IS_PLAY_VIEWPAGER_ANIM);
                    if (mCurrentFoucs == FOCUS_IN_NAV) {
                        if (mCurrentTvPageLayout != null) {
                            mCurrentTvPageLayout.setFocusEffect(0, false);
                        }
                    }
                    if (mCurrentTvPageLayout != null) {
                        mCurrentTvPageLayout.setFocusState(false);
                    }
                    mCurrentFoucs = FOCUS_IN_NAV;
                }
            });

            /**
             * 菜单栏传出焦点事件
             */
            mNavLayout.setOnKeyDownOutEnvent(new IOnKeyDownOutEnvent() {
                @Override
                public boolean onKeyDownGoLeft(View view) {
                    return true;
                }

                @Override
                public boolean onKeyDownGoRight(View view) {
                    return true;
                }

                @Override
                public boolean onKeyDownGoUp(View view) {
                    if (onKeyDownOutEnvent != null) {
                        onKeyDownOutEnvent.onKeyDownGoUp(view);
                    }
                    return true;
                }

                @Override
                public boolean onKeyDownGoDown(View view) {
                    if (mCurrentTvPageLayout != null && mCurrentTvPageLayout.hasData()) {
                        mCurrentFoucs = FOCUS_IN_TVPAGE;
                        if (bChangeTvPage) {
                            mCurrentTvPageLayout.setFocusEffect(0, true);
                        } else {
                            mCurrentTvPageLayout.setFocusState(true);
                        }
                        mNavLayout.getListView().get(mNavLayout.getSelectItem()).setFocusEffect(1);
                    } else {
                        DialogUtil.showDialog(context, context.getString(R.string.tv_tvpage_no_data));
                    }
                    return true;
                }

            });
            this.addView(mNavLayout);
        }

        //创建动态布局页面
        if (mCellBeanList != null) {
            if (mViewPager != null) {
                removeView(mViewPager);
            }
            mViewPager = new ViewPager(context);
            LayoutParams lp3 = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mViewPager.setLayoutParams(lp3);
            mViewPager.setOffscreenPageLimit(20);
            mAdapter = new MyPgaeAdapter();
            mViewPager.setAdapter(mAdapter);
//            mViewPager.setPageTransformer(true,new ViewPager3DTransformer());
//            mHandler.sendEmptyMessageDelayed(MSG_UPDATE_PAGE_LIMIT, 1500);
            this.addView(mViewPager);
        }

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (positionOffset == 0 && positionOffsetPixels == 0) {
//                    mCurrentTvPageLayout.notifyPageChange(0);
                } else {
                    if (mCurrentFoucs == FOCUS_IN_NAV) {
                        mCurrentTvPageLayout.setFocusEffect(0, false);
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
                FlyLog.d("<PPfunsTvView>onPageSelected-->position=" + position);
                bChangeTvPage = true;
                //上报翻页事件
                if (mNavLayout != null) {
                    List<NavItemView> navItemViews = mNavLayout.getListView();
                    if (navItemViews != null && position < navItemViews.size()) {
                        try {
                            NavItemView navItemView = navItemViews.get(position);
                            String tabName = navItemView.getTextView().getText().toString();
                            if (tabName != null) {
                                BehavioralUtil.reportPageEvent(mContext, tabName, navItemView.mTab.getId() + "");
                            }
                        } catch (Exception e) {
                            FlyLog.e(e.toString());
                        }
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                switch (state) {
                    case ViewPager.SCROLL_STATE_DRAGGING:
                    case ViewPager.SCROLL_STATE_SETTLING:
                        isViewPageScolling = true;
                        break;
                    case ViewPager.SCROLL_STATE_IDLE:
                        isViewPageScolling = false;
                        if (mCurrentFoucs == FOCUS_IN_TVPAGE) {
                            if (!mCurrentTvPageLayout.getFocusState()) {
                                mCurrentTvPageLayout.setFocusState(true);
                            }
                        }
                        break;
                }
            }
        });

        //设置焦点状态
        setDefaultFocusAndShowPager();

        //如果ViewPager第一页有视频控件通知视频控件播放视频
        if (!isSetDefaultFocus) {
            notifyPageChange(1);
        }

        if (mControlBean != null && mControlBean.getMarqueeEntity() != null) {
            createMaqueeView(mControlBean.getMarqueeEntity());
        }


        //
        //上报翻页事件
        if (mNavLayout != null) {
            List<NavItemView> navItemViews = mNavLayout.getListView();
            if (navItemViews != null && 0 < navItemViews.size()) {
                try {
                    NavItemView navItemView = navItemViews.get(0);
                    String tabName = navItemView.getTextView().getText().toString();
                    if (tabName != null) {
                        BehavioralUtil.reportPageEvent(mContext, tabName, navItemView.mTab.getId() + "");
                    }
                } catch (Exception e) {
                    FlyLog.e(e.toString());
                }
            }
        }
    }

    /**
     * 设定默认焦点并跳转到指定页
     */
    private void setDefaultFocusAndShowPager() {
        try {
            //ViewPager滚动到指定焦点页
            for (int i = 0; i < mTabEntityList.size(); i++) {
                if (mTabEntityList.get(i).getId() == mTemplateEntity.getDefaultTabId()) {
                    mCurrentFoucs = mTemplateEntity.getDefaultCellId() == -1 ? FOCUS_IN_NAV : FOCUS_IN_TVPAGE;
                    if (mNavLayout != null) {
                        mNavLayout.getListView().get(i).requestFocus();
                        if (FOCUS_IN_NAV == mCurrentFoucs) {
                            mNavLayout.getListView().get(i).setFocusEffect(0);
                        } else {
                            mNavLayout.getListView().get(i).setFocusEffect(1);
                        }
                        mViewPager.setCurrentItem(i, false);
                    }
                    return;
                }
            }

            //没有指定获取默认焦点的情况，指定第一个菜单项获取焦点
            if (mNavLayout != null && mNavLayout.getListView().size() != 0) {
                int selectItem = 0;
                if (mNavLayout.getSelectItem() < mNavLayout.getListView().size()) {
                    selectItem = mNavLayout.getSelectItem();
                }
                mNavLayout.getListView().get(selectItem).requestFocus();
                mNavLayout.getListView().get(selectItem).setFocusEffect(0);
                mCurrentFoucs = FOCUS_IN_NAV;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void notifyPageChange(int i) {
        if (mCurrentTvPageLayout != null) {
            mCurrentTvPageLayout.notifyPageChange(i);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean bRet = false;
        if (isViewPageScolling && isDelayPlayViewPager) {
            return true;
        }
        switch (mCurrentFoucs) {
            case FOCUS_IN_HEADER:
                if (mHeaderLayout != null) {
                    bRet = mHeaderLayout.onKeyDown(keyCode, event);
                }
                break;
            case FOCUS_IN_NAV:
                if (mNavLayout != null) {
                    bRet = mNavLayout.onKeyDown(keyCode, event);
                }
                break;
            case FOCUS_IN_TVPAGE:
                bChangeTvPage = false;
                if (mCurrentTvPageLayout != null) {
                    bRet = mCurrentTvPageLayout.onKeyDown(keyCode, event);
                }
                break;
        }

        //TODO 声音处理
//        boolean sound_flag = (boolean) SPUtil.get(context,SPUtil.FILE_CONFIG,SPUtil.CONFIG_SOUND_FLAG,false);
//        if(sound_flag){
//            soundPlay.playSound(soundIndex, 0);
//        }
        if (!bRet) {
            bRet = super.onKeyDown(keyCode, event);
        }
        return bRet;
    }

    protected void setHomePage() {
        if (mViewPager != null && mAdapter.getCount() > 0) {
            mViewPager.setCurrentItem(0, IS_PLAY_VIEWPAGER_ANIM);
            mCurrentFoucs = FOCUS_IN_NAV;
            mCurrentTvPageLayout.setFocusState(false);
            if (mNavLayout != null) {
                mNavLayout.getListView().get(0).requestFocus();
                mNavLayout.getListView().get(0).setFocusEffect(0);
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        mCurrentTvPageLayout = null;
        mCellBeanList.clear();
        if(mAdapter!=null){
            mAdapter.notifyDataSetChanged();
        }
        FlyLog.d("<PPfunsTvView>onDetachedFromWindow");
        super.onDetachedFromWindow();
    }

    @Override
    public void loadingDismiss() {
        super.loadingDismiss();
        if (mCurrentTvPageLayout != null) {
            mCurrentTvPageLayout.notifyPageChange(0);
        }
    }

    public class MyPgaeAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mCellBeanList == null ? 0 : mCellBeanList.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            final TvPageLayout tvPageLayout = new TvPageLayout.Builder()
                    .context(context)
                    //设置是否显示倒影
                    .isShowReflect(isShowReflect)
                    .shadowAmend(shadowAmend)
                    //设置动画类型
                    .animDuration(animDuration)
                    .animStyle(animStyle)
                    //统一设置缓存所使用的实例
                    .iDiskCache(iDiskCache)
                    .bitmapCache(mBitmapCache)
                    //设置数据，最终会动态产生成控件
                    .cellDate(mCellBeanList.get(position).getCellList(),mTabCountList[position])
                    //设置占位图
                    .loadImageResId(loadImageResId)
                    .setFocusResIDs(mFocusResIDs)

                    /**
                     * 子控件被点击时设置焦点切换到控制上
                     */
                    .onCellItemClick(new TvPageLayout.OnCellItemClick() {
                        @Override
                        public void onItemClick(ITvPageItemView view) {
                            mCurrentFoucs = FOCUS_IN_TVPAGE;
                            if (mNavLayout != null) {
                                mNavLayout.getListView().get(mNavLayout.getSelectItem()).setFocusEffect(1);
                            }
                            if (onCellItemClick != null) {
                                onCellItemClick.onCellItemClick(view);
                            }
                        }
                    })
                    .createView();

            container.addView(tvPageLayout);

            /*设置默认获取焦点的控件，考虑后台的数据给的可能不够严谨（从后台所得数据能获取焦点的控件不是唯一的，产生多个控件可以获得焦点的冲突情况）
             添加条件判断，按数据读取先后顺序，只设置第一个为获取焦点状态，
            */
            if (!isSetDefaultFocus) {
                try {
                    isSetDefaultFocus = tvPageLayout.setDefaultFocus(mTemplateEntity.getDefaultCellId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            /**
             * Viewpager页面传出焦点
             */
            tvPageLayout.setOnKeyDownOutEnvent(new IOnKeyDownOutEnvent() {
                @Override
                public boolean onKeyDownGoLeft(View view) {
                    int current = mViewPager.getCurrentItem();
                    if (mNavLayout != null) {
                        mNavLayout.getListView().get(mNavLayout.getSelectItem()).setFocusEffect(1);
                    }
                    if (current == 0) {
                        boolean tvpage_loop = (boolean) SPUtil.get(mContext, SPUtil.FILE_CONFIG, SPUtil.CONFIG_CIRCULATION_FLAG, false);
                        if (tvpage_loop) {
                            mViewPager.setCurrentItem(getCount() - 1, IS_PLAY_VIEWPAGER_ANIM);
                        } else {
                            startShake(mViewPager);
                            return false;
                        }
                    } else {
                        tvPageLayout.setFocusState(false);
                        mViewPager.setCurrentItem(current - 1, IS_PLAY_VIEWPAGER_ANIM);
                    }
                    mCurrentTvPageLayout.setFocusEffect(1, true);
                    return true;
                }

                @Override
                public boolean onKeyDownGoRight(View view) {
                    int current = mViewPager.getCurrentItem();
                    if (mNavLayout != null) {
                        mNavLayout.getListView().get(mNavLayout.getSelectItem()).setFocusEffect(1);
                    }
                    if (current == (getCount() - 1)) {
                        boolean tvpage_loop = (boolean) SPUtil.get(mContext, SPUtil.FILE_CONFIG, SPUtil.CONFIG_CIRCULATION_FLAG, false);
                        if (tvpage_loop) {
                            mViewPager.setCurrentItem(0, IS_PLAY_VIEWPAGER_ANIM);
                        } else {
                            startShake(mViewPager);
                            return false;
                        }
                    } else {
                        tvPageLayout.setFocusState(false);
                        mViewPager.setCurrentItem(current + 1, IS_PLAY_VIEWPAGER_ANIM);
                    }
                    mCurrentTvPageLayout.setFocusEffect(0, true);
                    return true;
                }

                @Override
                public boolean onKeyDownGoUp(View view) {
                    mCurrentFoucs = FOCUS_IN_NAV;
                    tvPageLayout.setFocusState(false);
                    if (mNavLayout != null) {
                        mNavLayout.getListView().get(mNavLayout.getSelectItem()).requestFocus();
                        mNavLayout.getListView().get(mNavLayout.getSelectItem()).setFocusEffect(0);
                    }
                    return true;
                }

                @Override
                public boolean onKeyDownGoDown(View view) {
                    if (onKeyDownOutEnvent != null) {
                        onKeyDownOutEnvent.onKeyDownGoDown(view);
                    }
                    return true;
                }
            });

            return tvPageLayout;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
//            FlyLog.d("<PPfunsTvView>setPrimaryItem-->ViewPageChange bBgShowing:" + bBgShowing);
            if ((mNavLayout != null) && (position != mNavLayout.getSelectItem())) {
                mNavLayout.setSelectItem(position, mCurrentFoucs == FOCUS_IN_NAV);
            }
            if (mCurrentTvPageLayout != null && mCurrentTvPageLayout != object) {
                mCurrentTvPageLayout.notifyPageChange(1);
            }
            if (mCurrentTvPageLayout != object && !bBgShowing) {
                ((TvPageLayout) object).notifyPageChange(0);
            }
            if (mCurrentTvPageLayout != object) {
                mCurrentTvPageLayout = (TvPageLayout) object;
            }

        }

    }


    @Override
    public void setTvPageLoseFocus() {

    }


}
