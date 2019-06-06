package com.flyzebra.launcher.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.flyzebra.flyui.chache.DiskCache;
import com.flyzebra.flyui.chache.IDiskCache;
import com.flyzebra.launcher.R;
import com.flyzebra.ppfunstv.data.ControlBean;
import com.flyzebra.ppfunstv.data.TemplateEntity;
import com.flyzebra.ppfunstv.data.TvCellBean;
import com.flyzebra.ppfunstv.module.UpdataVersion.IUpdataVersion;
import com.flyzebra.ppfunstv.module.UpdataVersion.UpdataVersion;
import com.flyzebra.ppfunstv.service.MarqueeService;
import com.flyzebra.ppfunstv.utils.FlyLog;
import com.flyzebra.ppfunstv.utils.SystemPropertiesProxy;
import com.flyzebra.ppfunstv.view.LoadAnimView.LoadAnimView;
import com.flyzebra.ppfunstv.view.TvView.ITvView;
import com.flyzebra.ppfunstv.view.TvView.TvViewFactory;

import java.util.List;

/**
 *
 * Created by flyzebra on 17-6-15.
 */
public class MainActivity extends Activity implements IUpdataVersion.CheckCacheResult, IUpdataVersion.UpResult {
    private FrameLayout mActivityRoot;
    private ITvView mItvView;
    protected LoadAnimView mLoadAnimView;
    private IUpdataVersion iUpDataVersion;
    private IDiskCache iDiskCache;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    //开机放大动画控件部分
    private long LOAD_ANIM_TIME = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_launcher);

        mActivityRoot = (FrameLayout) findViewById(R.id.ac_launcher_root);

        mLoadAnimView = (LoadAnimView) findViewById(R.id.ac_load_view);
        mLoadAnimView.addEvent(new LoadAnimView.LoadEvent() {
            @Override
            public void dismiss() {
                mItvView.startPlay();
            }

            @Override
            public void showing() {

            }
        });

        iDiskCache = new DiskCache().init(this);
        iUpDataVersion = new UpdataVersion(getApplicationContext(), iDiskCache) {
            @Override
            public void initApi() {
                String url = SystemPropertiesProxy.get(MainActivity.this, SystemPropertiesProxy.Property.URL_BASE, "http://192.168.1.81:9020");
                String areaCode = SystemPropertiesProxy.get(MainActivity.this, SystemPropertiesProxy.Property.AREA_CODE, "073101");
                String version = com.flyzebra.ppfunstv.utils.AppUtil.getVersionName(MainActivity.this);
                ApiUrl = TextUtils.isEmpty(url) ? "http://192.168.1.81:9020" : url;
                ApiVersion = "/api/ui-operation/api/v/launcher_version.json?areaCode=" + areaCode + "&type=launcher&version=" + version;
                ApiTemplate = "/api/ui-operation/api/v/launcher_tab.json?areaCode=" + areaCode + "&type=launcher&version=" + version;
                ApiCellList = "/api/ui-operation/api/v/launcher_cell.json?tabId=";
                ApiResource = "/api/ui-operation/api/v/launcher_resource.json?templateId=";
            }
        };

        /**
         * 播放开机动画，延时更新
         */
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                iUpDataVersion.getCacheData(MainActivity.this);
            }
        }, LOAD_ANIM_TIME);

        startService(new Intent(this, MarqueeService.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mItvView!=null){
            mItvView.onResume();
        }
    }

    @Override
    protected void onPause() {
        if(mItvView!=null){
            mItvView.onPause();
        }
        super.onPause();
    }


    private void upDateView (TemplateEntity templateEntity, List<TvCellBean> cellBeanList, ControlBean controlBean) {
        mItvView = TvViewFactory.create(this,templateEntity.getTemplateDetail());
        mItvView.createLogoView(controlBean.getLogo(),iDiskCache);
        mItvView.createMaqueeView(controlBean.getMarqueeEntity());
        mItvView.createPageView(templateEntity,cellBeanList,iDiskCache);
        mItvView.createStatusbarView();
        mActivityRoot.addView((View) mItvView);
    }

    @Override
    public void getCacheDataOK(TemplateEntity templateEntity, List<TvCellBean> cellBeanList, ControlBean controlBean) {
        FlyLog.d();
        upDateView(templateEntity,cellBeanList,controlBean);
        iUpDataVersion.startUpVersion(this);
    }

    @Override
    public void getCacheDataFaile(String error) {
        FlyLog.d(error);
    }

    @Override
    public void upVersionOK(TemplateEntity templateEntity, List<TvCellBean> cellBeanList, ControlBean controlBean) {
        FlyLog.d();
        upDateView(templateEntity,cellBeanList,controlBean);
    }

    @Override
    public void upVesionProgress(String msg, int sum, int progress) {
        FlyLog.d("%s进度%d/%d", msg, progress, sum);
    }

    @Override
    public void upVersionFaile(String error) {
        FlyLog.d(error);
    }
}
