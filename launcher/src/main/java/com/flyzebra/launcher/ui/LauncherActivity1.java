package com.flyzebra.launcher.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.flyzebra.flyui.chache.DiskCache;
import com.flyzebra.flyui.chache.IDiskCache;
import com.flyzebra.launcher.R;
import com.flyzebra.launcher.constant.Constants;
import com.flyzebra.launcher.service.AAAServiceConnect;
import com.flyzebra.ppfunstv.data.ControlBean;
import com.flyzebra.ppfunstv.data.TemplateEntity;
import com.flyzebra.ppfunstv.data.TvCellBean;
import com.flyzebra.ppfunstv.module.UpdataVersion.IUpdataVersion;
import com.flyzebra.ppfunstv.module.UpdataVersion.UpdataVersion;
import com.flyzebra.ppfunstv.utils.FlyLog;
import com.flyzebra.ppfunstv.utils.SystemPropertiesProxy;
import com.flyzebra.ppfunstv.view.TvView.BaseTvView;
import com.flyzebra.ppfunstv.view.TvView.CellView.ITvPageItemView;
import com.flyzebra.ppfunstv.view.TvView.FocusAnimat.ITvFocusAnimat;
import com.flyzebra.ppfunstv.view.TvView.HeaderView.HeaderLayout;
import com.flyzebra.ppfunstv.view.TvView.PopupTV.PopupTvView;

import java.util.List;


/**
 * Created by fagro on 17-6-17.
 */

public class LauncherActivity1 extends Activity implements IUpdataVersion.CheckCacheResult {
    private static final String NAME = "name";
    private FrameLayout root;
    private IDiskCache iDiskCache;
    private IUpdataVersion iUpdataVersion;
    private BaseTvView mTvView;
    private final static String TEMPLATE = "template";
    private String tempLate = "";
    private Context mContext;
    private ImageView shade;


    Handler handler = new Handler(Looper.getMainLooper());
    private long delayMillisToShowView = 500;


    public Drawable defaultBackground;
    private String title;
    private int oldId;
    private AAAServiceConnect aaaServiceConnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launchers);
        mContext = this;
        iDiskCache = new DiskCache().init(this);


        aaaServiceConnect = new AAAServiceConnect(this);
//        shade = (ImageView) findViewById(R.id.shade);
        iUpdataVersion = new UpdataVersion(getApplicationContext(), iDiskCache) {
            @Override
            public void initApi() {
                String url = SystemPropertiesProxy.get(mContext, SystemPropertiesProxy.Property.URL_BASE, "http://192.168.1.81:9020");
                String areaCode = SystemPropertiesProxy.get(mContext, SystemPropertiesProxy.Property.AREA_CODE, "0");
                String version = com.flyzebra.ppfunstv.utils.AppUtil.getVersionName(mContext);
                String tokenJson = aaaServiceConnect.query(Constants.AAA_TOKEN_QUERY_KEY);
                String token = "";
                if (!TextUtils.isEmpty(tokenJson)) {
//                    AAAServiceConnect.TokenBean tokenBean = GsonUtils.json2Object(tokenJson, AAAServiceConnect.TokenBean.class);
//                    if (tokenBean != null)
//                        token = tokenBean.result;
                }

                this.token = token;
                ApiUrl = TextUtils.isEmpty(url) ? "http://192.168.1.81:9020" : url;
                ApiVersion = "/api/ui-operation/api/v/launcher_version.json?areaCode=" + areaCode + "&type=launcher&version=" + version;
                ApiTemplate = "/api/ui-operation/api/v/launcher_tab.json?areaCode=" + areaCode + "&type=launcher&version=" + version;
                ApiCellList = "/api/ui-operation/api/v/launcher_cell.json?" + "tabId=";
                ApiResource = "/api/ui-operation/api/v/launcher_resource.json?" + "templateId=";
            }
        };

        root = (FrameLayout) findViewById(R.id.root);

        String tempLate = getIntent().getStringExtra(TEMPLATE);
        title = getIntent().getStringExtra(NAME);
        FlyLog.d("onCreate template=" + tempLate);
        upTempLate(tempLate == null ? "" : tempLate);
        setMainUpView();
    }


    @Override
    protected void onStart() {
//        FastWallpaper.getInstance().show();
        super.onStart();
    }

    private void upTempLate(String tempLate) {
        FlyLog.d("upTempLate template=" + tempLate);
        if (tempLate != null && !TextUtils.equals(this.tempLate, tempLate)) {
            if (root != null) {
                root.setVisibility(View.INVISIBLE);
            }
            this.tempLate = tempLate;
            iUpdataVersion.setDefualtTemplate(this.tempLate);
//            iUpdataVersion.cancelAllTasks();
            iUpdataVersion.getCacheData(this);
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        FlyLog.d("onNewIntent template=" + tempLate);
        upTempLate(intent.getStringExtra(TEMPLATE));
        if (mTvView != null) {
            mTvView.setVisibility(View.VISIBLE);
        }
        super.onNewIntent(intent);
    }

    @Override
    protected void onPause() {
        if (mTvView != null) {
            mTvView.notifyPageChange(1);
        }
        unregisterHomeKeyReceiver(this);
        super.onPause();

//        iUpdataVersion.cancelAllTasks();
        FlyLog.d("onPause");
    }

    @Override
    protected void onStop() {
//        FastWallpaper.getInstance().hide();
        super.onStop();
        FlyLog.d("onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        if (iUpdataVersion != null) {
//            iUpdataVersion.cancelAllTasks();
//        }
    }

    @Override
    protected void onDestroy() {
        Glide.get(this).clearMemory();

        FlyLog.d("onDestroy");
        super.onDestroy();
        aaaServiceConnect.unbindService(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerHomeKeyReceiver(this);
        if (mTvView != null) {
            mTvView.notifyPageChange(0);
        }
    }

    private void upViewData(TemplateEntity templateEntity, List<TvCellBean> cellBeanList, ControlBean controlBean, boolean bDefaultData) {
        if (mTvView != null) {
            root.removeView(mTvView);
        }
        /**
         * 设置背景图片
         */
        FlyLog.d("change the background url = " + templateEntity.getBackgroundImage());
        try {
            Glide.with(this)
                    .load(iDiskCache.getBitmapPath(templateEntity.getBackgroundImage()))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(new CustomTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            FlyLog.d("change the background" + resource.toString());
                            defaultBackground = resource;
                            getWindow().getDecorView().setBackground(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            FlyLog.e(e.toString());
        }

        if (cellBeanList != null) {

            HeaderLayout.HeadEntity headEntity = new HeaderLayout.HeadEntity(true, title, false);
//            if (TextUtils.equals("政务", title)) {
            headEntity.showSearch = false;
            headEntity.showLogo = true;
            headEntity.showFilter = false;
            headEntity.showTitle = false;
            headEntity.showWeather = false;
//            }
//                headEntity.showSearch = true;
//                headEntity.showFilter = true;
////            } else {
//                headEntity.showSearch = true;
//                headEntity.showFilter = false;
//            }
//            String serarchActionJson = "{'intent':'com.flyzebra.vod.action.ACTION_VIEW_SEARCH','data':'','cmd':''}";
//            headEntity.searchAction = GsonUtils.json2Object(serarchActionJson, ActionEntity.class);


            mTvView = new PopupTvView(this);
            mTvView.setLoadShowing(false);
            // 设置倒影
            mTvView.setShowReflect(true)
                    .setAnimStyle(ITvFocusAnimat.TV_PAGE_MOVE_ANIM)
                    .setShadowAmend(12)
                    .setAnimDuration(200)
                    .setCreateNavLayout(true)
                    .setCreateHeaderLayout(true)
                    .setDiskCache(iDiskCache)
                    .setTvPageData(cellBeanList)
                    .setControlData(controlBean)
                    .setHeadEntity(headEntity)
                    .createView();
            root.addView(mTvView);
            mTvView.setOnCellItemClick(new BaseTvView.OnCellItemClick() {
                @Override
                public void onCellItemClick(ITvPageItemView tvPageItemView) {
                    tvPageItemView.doAction(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
            });
            if (root != null) {
                delayShowView();
            }

        }
    }


    @Override
    public void getCacheDataOK(TemplateEntity templateEntity, List<TvCellBean> cellBeanList, ControlBean controlBean) {
        FlyLog.d();
        upViewData(templateEntity, cellBeanList, controlBean, false);
    }

    @Override
    public void getCacheDataFaile(String error) {
        FlyLog.d(error);
        if (root != null) {
            delayShowView();
        }
    }

    public void delayShowView() {
        if (root != null) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    root.setVisibility(View.VISIBLE);
//                    shade.setVisibility(View.GONE);
                }
            }, delayMillisToShowView);
        }
    }

    @Override
    public void finish() {
        //TODOAuto-generatedmethodstub
        super.finish();
        //关闭窗体动画显示
//        this.overridePendingTransition(com.flyzebra.ppfunstv.R.anim.tv_anim_activity_exit,com.flyzebra.ppfunstv.R.anim.tv_not_anim);
        this.overridePendingTransition(android.R.anim.fade_in, com.flyzebra.ppfunstv.R.anim.tv_anim_activity_exit);
    }


    public class HomeWatcherReceiver extends BroadcastReceiver {
        private static final String LOG_TAG = "HomeReceiver";
        private static final String SYSTEM_DIALOG_REASON_KEY = "reason";
        private static final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
        private static final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
        private static final String SYSTEM_DIALOG_REASON_LOCK = "lock";
        private static final String SYSTEM_DIALOG_REASON_ASSIST = "assist";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i(LOG_TAG, "onReceive: action: " + action);
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                // android.intent.action.CLOSE_SYSTEM_DIALOGS
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                Log.i(LOG_TAG, "reason: " + reason);

                if (SYSTEM_DIALOG_REASON_HOME_KEY.equals(reason)) {
                    // 短按Home键
                    Log.i(LOG_TAG, "homekey");
                    finish();
//                    overridePendingTransition(com.flyzebra.ppfunstv.R.anim.tv_not_anim,com.flyzebra.ppfunstv.R.anim.tv_anim_activity_exit);
                } else if (SYSTEM_DIALOG_REASON_RECENT_APPS.equals(reason)) {
                    // 长按Home键 或者 activity切换键
                    Log.i(LOG_TAG, "long press home key or activity switch");

                } else if (SYSTEM_DIALOG_REASON_LOCK.equals(reason)) {
                    // 锁屏
                    Log.i(LOG_TAG, "lock");
                } else if (SYSTEM_DIALOG_REASON_ASSIST.equals(reason)) {
                    // samsung 长按Home键
                    Log.i(LOG_TAG, "assist");
                }

            }
        }

    }


    private HomeWatcherReceiver mHomeKeyReceiver = null;

    private void registerHomeKeyReceiver(Context context) {
        if (mHomeKeyReceiver == null) {
            try {
                mHomeKeyReceiver = new HomeWatcherReceiver();
                final IntentFilter homeFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);

                context.registerReceiver(mHomeKeyReceiver, homeFilter);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    private void unregisterHomeKeyReceiver(Context context) {

        if (null != mHomeKeyReceiver) {
            try {
                context.unregisterReceiver(mHomeKeyReceiver);
                mHomeKeyReceiver = null;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {

            }
        }
    }


    /**
     * 控制焦点移动相关的方法
     */
    private void setMainUpView() {
        root.getViewTreeObserver().addOnGlobalFocusChangeListener(new ViewTreeObserver.OnGlobalFocusChangeListener() {
            @Override
            public void onGlobalFocusChanged(View oldFocus, final View newFocus) {
                if (newFocus != null) {
                    switch (newFocus.getId()) {
                        case R.id.tv_head_serarch:
                        case R.id.tv_head_filter:
                            if (oldFocus != null && oldFocus instanceof ITvPageItemView) {
                                oldId = oldFocus.getId();
                                if (mTvView != null && mTvView.getHeaderLayout() != null) {
                                    mTvView.getHeaderLayout().iv_search.setNextFocusDownId(oldId);
                                    mTvView.getHeaderLayout().iv_filter.setNextFocusDownId(oldId);
                                }
                            }
                            newFocus.setAlpha(0.2f);
                            if (mTvView != null && oldFocus != null) {
                                mTvView.setTvPageLoseFocus();
                            }
                            break;
                    }
                }

                if (oldFocus != null) {
                    switch (oldFocus.getId()) {
                        case R.id.tv_head_serarch:
                        case R.id.tv_head_filter:
                            if (newFocus != null && newFocus instanceof ITvPageItemView) {
                                oldId = oldFocus.getId();
                                newFocus.setNextFocusUpId(oldId);
                            }
                            oldFocus.setAlpha(1.0f);
                            break;
                    }
                }
            }
        });
    }


}