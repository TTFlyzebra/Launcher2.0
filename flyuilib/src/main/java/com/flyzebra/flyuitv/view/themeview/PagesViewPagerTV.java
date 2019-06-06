package com.flyzebra.flyuitv.view.themeview;

import android.content.Context;

import com.flyzebra.flyui.view.pageview.IPage;
import com.flyzebra.flyui.view.themeview.PagesViewPager;
import com.flyzebra.flyuitv.view.pageview.SimplePageViewTV;

public class PagesViewPagerTV extends PagesViewPager {


    public PagesViewPagerTV(Context context) {
        super(context);
    }

    @Override
    protected IPage getNewPageView(Context context) {
        return new SimplePageViewTV(context);
    }
}
