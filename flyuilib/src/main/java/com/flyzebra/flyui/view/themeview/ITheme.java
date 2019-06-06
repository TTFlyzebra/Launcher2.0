package com.flyzebra.flyui.view.themeview;


import android.content.Context;
import android.view.View;

import com.flyzebra.flyui.bean.CellBean;
import com.flyzebra.flyui.bean.ThemeBean;

public interface ITheme {

    void onCreate(Context context);


    void onDestory();
    /**
     * 更新数据
     * @param themeBean
     */
    void upData(ThemeBean themeBean);

    /**
     * 显示选择页面
     * @param page
     */
    void selectPage(int page);

    //TV
    /**
     * 选择项获取焦点
     * @param cell
     */
    void selectCell(CellBean cell);

    View findViewById(String id);
}
