package com.flyzebra.launcher.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.flyzebra.launcher.R;
import com.flyzebra.launcher.adapter.RecentAppAdapter;
import com.flyzebra.launcher.utils.AppUtil;

/**
 * Created by lizongyuan on 2016/11/22.
 * E-mail:lizy@ppfuns.com
 * 最近使用应用界面
 */

public class RecentAppActivity extends Activity {

    private Context mContext;
    private GridView gvApp;
    private TextView tvTip;
    private RecentAppAdapter mAdapter;

    public static String[] filter = {
//            "com.flyzebra.",
//            "com.alliance.homeshell"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_recent_app);
        initView();
        initData();
    }

    private void initView() {
        gvApp = (GridView) findViewById(R.id.gv_app);
        gvApp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.callOnClick();
            }
        });
        tvTip = (TextView) findViewById(R.id.tv_recent_app_tip);
    }

    private void initData(){
        mAdapter = new RecentAppAdapter(mContext, AppUtil.getExcludeApp(this,filter,false));
        gvApp.setAdapter(mAdapter);
        if(0 == mAdapter.getCount()){
            tvTip.setVisibility(View.VISIBLE);
        }else{
            tvTip.setVisibility(View.GONE);
        }
    }

}
