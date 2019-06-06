package com.flyzebra.flyui.view.themeview;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.flyzebra.flyui.bean.CellBean;
import com.flyzebra.flyui.bean.PageBean;
import com.flyzebra.flyui.bean.ThemeBean;
import com.flyzebra.flyui.config.GV;
import com.flyzebra.flyui.event.FlyEvent;
import com.flyzebra.flyui.event.IFlyEvent;
import com.flyzebra.flyui.utils.ByteUtil;
import com.flyzebra.flyui.utils.FlyLog;
import com.flyzebra.flyui.view.pageview.IPage;
import com.flyzebra.flyui.view.pageview.SimplePageView;

import java.util.ArrayList;
import java.util.List;

public class PagesViewPager extends ViewPager implements IFlyEvent {
    private List<PageBean> pageList = new ArrayList<>();
    private ThemeBean themeBean;
    private MyPgaeAdapter myPgaeAdapter = new MyPgaeAdapter();

    public PagesViewPager(Context context) {
        super(context);
        init(context);
    }

    @Override
    protected void onAttachedToWindow() {
        FlyLog.v("onAttachedToWindow");
        super.onAttachedToWindow();
        FlyEvent.register(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        FlyLog.v("onDetachedFromWindow");
        FlyEvent.unregister(this);
        super.onDetachedFromWindow();
    }

    private void init(Context context) {
        setAdapter(myPgaeAdapter);
    }

    @Override
    public void setPageTransformer(boolean reverseDrawingOrder, PageTransformer transformer) {
        super.setPageTransformer(reverseDrawingOrder, transformer);
    }

    public void upData(ThemeBean themeBean) {
        if (themeBean == null || themeBean.pageList == null || themeBean.pageList.isEmpty()) {
            return;
        }
        this.themeBean = themeBean;
        List<PageBean> mPageBeanList = themeBean.pageList;

        pageList.clear();
        if (mPageBeanList.size() > 1) {
            pageList.add(mPageBeanList.get(mPageBeanList.size() - 1));
            pageList.addAll(mPageBeanList);
            pageList.add(mPageBeanList.get(0));
            myPgaeAdapter.notifyDataSetChanged();
            setCurrentItem(1);
        } else {
            pageList.addAll(mPageBeanList);
            myPgaeAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (GV.viewPage_scoller) {
            return super.onInterceptTouchEvent(ev);
        } else {
            return false;
        }
    }

    public void selectPage(int page) {

    }

    public void selectCell(CellBean cell) {

    }

    @Override
    public boolean recvEvent(byte[] key) {
        switch (ByteUtil.bytes2HexString(key)) {
            case "400301":
                Object obj = FlyEvent.getValue(key);
                if (obj instanceof String) {
                    byte[] data = ByteUtil.hexString2Bytes((String) obj);
                    if (data.length > 1) {
                        int page = data[0];
                        setCurrentItem(page,false);
                    }
                }
                break;
        }
        return false;
    }

    public class MyPgaeAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return pageList == null ? 0 : pageList.size();
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
            IPage pageView = getNewPageView(getContext());
            ((View) pageView).setTag(position);
            pageView.showMirror(themeBean.isMirror != 0);
            pageView.setPageBean(pageList.get(position));
            container.addView((View) pageView);
            return pageView;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            FlyEvent.saveValue("400301", new byte[]{(byte) position, (byte) (getCount() - 2)});
            FlyEvent.sendEvent("400301");
            //循环滚动
            try {
                if (position == 0 && pageList != null && pageList.size() > 1) {
                    setCurrentItem(pageList.size() - 2, false);
                    for (int i = 0; i < getCount(); i++) {
                        View view = getChildAt(i);
                        if (view != null) {
                            view.setTranslationX(0);
                            view.setRotation(0);
                        }
                    }
                }
                if (pageList != null && position == pageList.size() - 1 && pageList.size() > 1) {
                    setCurrentItem(1, false);
                    for (int i = 0; i < getCount(); i++) {
                        View view = getChildAt(i);
                        if (view != null) {
                            view.setTranslationX(0);
                            view.setRotation(0);
                        }
                    }
                }
            } catch (Exception e) {
                FlyLog.e(e.toString());
            }
        }

    }

    protected IPage getNewPageView(Context context) {
        return new SimplePageView(context);
    }


}
