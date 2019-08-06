package com.flyzebra.launcher.ui;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.flyzebra.flyui.bean.CellBean;
import com.flyzebra.flyui.bean.PageBean;
import com.flyzebra.flyui.bean.ThemeBean;
import com.flyzebra.flyui.chache.DiskCache;
import com.flyzebra.flyui.chache.IDiskCache;
import com.flyzebra.flyui.chache.IUpdataVersion;
import com.flyzebra.flyui.chache.UpdataVersion;
import com.flyzebra.launcher.R;
import com.flyzebra.launcher.base.BaseActivity;
import com.flyzebra.launcher.constant.Constants;
import com.flyzebra.launcher.module.SoundPlay;
import com.flyzebra.launcher.service.AAAServiceConnect;
import com.flyzebra.launcher.utils.AppUtil;
import com.flyzebra.launcher.utils.BackdoorUtil;
import com.flyzebra.launcher.utils.FlyLog;
import com.flyzebra.launcher.utils.MemoryManager;
import com.flyzebra.launcher.utils.SPUtil;
import com.flyzebra.launcher.utils.WallPaperUtils;
import com.flyzebra.launcher.view.ChangeTemplateDialog;
import com.flyzebra.marqueeservice.IMarqueeService;
import com.flyzebra.ppfunstv.data.CellEntity;
import com.flyzebra.ppfunstv.data.ControlBean;
import com.flyzebra.ppfunstv.data.TabEntity;
import com.flyzebra.ppfunstv.data.TemplateEntity;
import com.flyzebra.ppfunstv.data.TvCellBean;
import com.flyzebra.ppfunstv.module.EventMessage;
import com.flyzebra.ppfunstv.service.MarqueeService;
import com.flyzebra.ppfunstv.utils.BehavioralUtil;
import com.flyzebra.ppfunstv.utils.DialogUtil;
import com.flyzebra.ppfunstv.utils.DisplayUtils;
import com.flyzebra.ppfunstv.utils.SystemPropertiesProxy;
import com.flyzebra.ppfunstv.utils.ToastUtils;
import com.flyzebra.ppfunstv.utils.Utils;
import com.flyzebra.ppfunstv.utils.wallpaper.FastWallpaper;
import com.flyzebra.ppfunstv.view.LoadAnimView.LoadAnimView;
import com.flyzebra.ppfunstv.view.TvView.BaseTvView;
import com.flyzebra.ppfunstv.view.TvView.CellView.AdsModule.AdsModule;
import com.flyzebra.ppfunstv.view.TvView.CellView.AdsModule.IAdsModule;
import com.flyzebra.ppfunstv.view.TvView.CellView.CellType;
import com.flyzebra.ppfunstv.view.TvView.CellView.ITvPageItemView;
import com.flyzebra.ppfunstv.view.TvView.CellView.TvPageItemView;
import com.flyzebra.ppfunstv.view.TvView.FocusAnimat.ITvFocusAnimat;
import com.flyzebra.ppfunstv.view.TvView.HeaderView.HeaderLayout;
import com.flyzebra.ppfunstv.view.TvView.HomeWatcher;
import com.flyzebra.ppfunstv.view.TvView.IOnKeyDownOutEnvent;
import com.flyzebra.ppfunstv.view.TvView.PPfunsTV.PPfunsTvView;
import com.flyzebra.ppfunstv.view.TvView.PopupTV.PopupTvView;
import com.flyzebra.ppfunstv.view.TvView.TvViewFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LauncherActivity extends BaseActivity implements IUpdataVersion.CheckCacheResult, IUpdataVersion.UpResult, IOnKeyDownOutEnvent {

    private static final int MSG_UPDATE = 0x1;//更新消息
    private static final int MSG_UPDATE_TIME_THEME = 5000;//根据配置(状态栏-我的-UI定制)更换界面
    private static final int MSG_DELAY_PLAY = 6000;
    private static final int MSG_DELAY_UPDATE_VERSION = 5;
    private static final int delayPlayTime = 80;

    private static String TAG = "LauncherActivity";
    public FrameLayout mActivityRoot;
    public BaseTvView mTvView;
    public IUpdataVersion iUpDataVersion;
    public IDiskCache iDiskCache;
    //鉴权服务
    public AAAServiceConnect aaaServiceConnect;
    private Context mContext;
    /**
     * 我们有初始版本的缓存（位于assets中），更新版本后本地保存的缓存，存放于本地磁盘中
     * 此参数决定TvPageLayout加载缓存图片的位置，ture为从assets加载，false从本地磁盘加载
     */
//    private boolean bDefaultData = false;//是否为默认数据
    private MyBroadcast myBroadcast = new MyBroadcast();
    private ChangeTemplateDialog mChangeTemplateDialog;
    private TemplateEntity mTemplateEntity;
    private List<TvCellBean> mCellList = new ArrayList<>();
    private ControlBean mControlBean;
    //开机放大动画控件部分
    private long mAnimTime = 3000;
    private float mAnimScale = 1.2f;
    //声音播放
    private SoundPlay soundPlay;
    private int soundIndex = 1;
    private int defaultSoundIndex = 7;
    private int errCount = 0;
    private int baseSecond = 10000;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void dispatchMessage(Message msg) {
            FlyLog.d("receive message:" + msg.what);
            switch (msg.what) {
                case MSG_UPDATE:
                    if (iUpDataVersion.isUPVeriosnRunning()) {
                        mHandler.sendEmptyMessageDelayed(MSG_UPDATE, 60000);
                    } else {
                        iUpDataVersion.startUpVersion(LauncherActivity.this);
                        mHandler.sendEmptyMessageDelayed(MSG_UPDATE, UpdataVersion.UPDATE_IINTERVAL);
                    }
                    break;
                case MSG_UPDATE_TIME_THEME:
                    //TODO 判断为哪个分支
                    //如果为阿里分支,此片代码需要完善
//                    if ("".equals("ALI")) {
//                        String retInfo = TemplateUtil.updateTimeTheme(mContext, iUpDataVersion, mHandler, MSG_UPDATE_TIME_THEME);
//                        if (!TextUtils.isEmpty(retInfo)) {
//                            showChangelTemplateTipAndReport(retInfo);
//                        }
//                    }
                    break;
                case MSG_DELAY_PLAY: {
                    if (mTvView != null) {
                        mTvView.notifyPageChange(0);
                    }
                    break;
                }
                case MSG_DELAY_UPDATE_VERSION: {
                    //进入launcher主动更新
                    iUpDataVersion.startUpVersion(LauncherActivity.this);
                    break;
                }
                default:
                    break;
            }
            super.dispatchMessage(msg);
        }
    };

    //test
    private int showHideNum = 0;
    private boolean backDoor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        FlyLog.d("onCreate start....");

        //调用系统默认launcher中的设置属性操作，解决HOME键不响应的问题
        try {
            Settings.Global.putInt(getContentResolver(), Settings.Global.DEVICE_PROVISIONED, 1);
            Settings.Secure.putInt(getContentResolver(), "user_setup_complete"/*Settings.Secure.USER_SETUP_COMPLETE*/, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //luancher启动时先静音，启动画面隐藏时再把小视窗音量放开
        AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        am.setStreamMute(android.media.AudioManager.STREAM_MUSIC, true);

        //发送模拟开机广播
        sendBootBroadcast();
        setContentView(R.layout.activity_launcher);

        initView();
        registerReceiver();
        //NOTE:此类必须在Activity中实例化，TvPageLayout加载图片需调用其缓存
        //从磁盘读取已升级成功的数据，如磁盘没有升级数据，加载APK发布版本的数据
        iDiskCache = new DiskCache().init(this);
        iUpDataVersion = new UpdataVersion(getApplicationContext(), iDiskCache);

        /**
         * 播放开机动画，延时更新
         */
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                iUpDataVersion.getCacheData(LauncherActivity.this);
//            }
//        }, mAnimTime);

        mHandler.sendEmptyMessageDelayed(MSG_DELAY_UPDATE_VERSION, Constants.DELAY_UPDATE_VERSION_TIME);
        FlyLog.d("onCreate end....");

        initAdsModule();
        init();
        String themeName = (String) SPUtil.get(this, "themeName", "Launcher-AP1");
        String url =  "http://192.168.1.88/uiweb";
        String token = "1234567890";
        String ApiTheme = "/api/app?type=%s&themeName=%s&version=%s";
        String type = "launcher";
        String version = AppUtil.getVersionName(this);
        if (!TextUtils.isEmpty(themeName) && !themeName.equals(themeName)) {
            iUpDataVersion.initApi(url, ApiTheme, type, themeName, version, token);
        }
        binded = bindService(new Intent(mContext, MarqueeService.class), conn, mContext.BIND_AUTO_CREATE);

        iUpDataVersion.forceUpVersion(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        boolean from = intent.getBooleanExtra("fromLoad", false);
        if (from && iUpDataVersion != null) {
            iUpDataVersion.getCacheData(this);
        }
        FlyLog.d("from load:" + from);
    }


    private void init() {
        FlyLog.d("backgroup is showing..");
        /**
         * 鉴权服务 直播服务 绑定启动
         */
        aaaServiceConnect = new AAAServiceConnect(this);
        mHandler.sendEmptyMessage(MSG_UPDATE_TIME_THEME);
        Utils.updateInterval(mContext);
        mHandler.removeMessages(MSG_UPDATE);
        mHandler.sendEmptyMessageDelayed(MSG_UPDATE, UpdataVersion.UPDATE_IINTERVAL);
//        aaaServiceConnect.registerPropertyListener(Constants.AAA_TOKEN_QUERY_KEY, new AAAServiceConnect.PropertyListener() {
//            @Override
//            public void onPropertyChanged(String pNew, String pOld) {
////                iUpDataVersion.forceUpVersion(LauncherActivity.this);
//                iUpDataVersion.initApi();
////                iUpDataVersion.startUpVersion(LauncherActivity.this);
//
//
//
//
//            }
//        });
    }


    private boolean isUpdata = false;
    private TemplateEntity needUPtemplateEntity;
    private List<TvCellBean> needUPcellBeanList;
    private ControlBean needUPcontrolBean;
    private boolean isUpdataViewRunning = false;

    /**
     * 更新UI界面
     */
    private void updataView(final TemplateEntity templateEntity, final List<TvCellBean> cellBeanList, final ControlBean controlBean) {
//        Glide.get(this).clearMemory();
        FlyLog.d("updataView start....");
        if (!AppUtil.isTop(LauncherActivity.this)) {
            FlyLog.d("Activity is not top, updataView return");
            return;
        }

        if (!isActivityRunning) {
            FlyLog.d("Activity is not Running, updataView return");
            return;

        }
        if (!isUpdata) {
            FlyLog.d("no data need updata, updataView return");
            return;
        }
        isUpdata = false;
        showLoadingDialog();
        isUpdataViewRunning = true;
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mTemplateEntity = templateEntity;
                mCellList.clear();
                if (cellBeanList != null) {
                    mCellList.addAll(cellBeanList);
                }
                mControlBean = controlBean;
                mHandler.removeMessages(MSG_DELAY_PLAY);
                Constants.RECENT_APP_INDEX = 0;
                if (mTemplateEntity == null) {
                    FlyLog.d("updataView mTemplateEntity is null, updataView return");
                } else {
                    SPUtil.setTemplate(LauncherActivity.this, SPUtil.TEMPLATE_ID, mTemplateEntity.getTemplateId());
                    if (mTvView != null) {
                        mTvView.removeAllViews();
                        mActivityRoot.removeView(mTvView);
                    }

                    //设置壁纸
                    if (mTemplateEntity != null && !TextUtils.isEmpty(mTemplateEntity.getBackgroundImage())) {
                        String imgUrl = mTemplateEntity.getBackgroundImage();
                        String filePath = iDiskCache.getBitmapPath(imgUrl);
                        if (!TextUtils.isEmpty(filePath)) {
                            TvPageItemView.sDefaultBgUrl = imgUrl;
                            TvPageItemView.sCurrentBgrUrl = imgUrl;
                            WallPaperUtils.setWallPaper(LauncherActivity.this, filePath, DisplayUtils.getMetrices(LauncherActivity.this).widthPixels, DisplayUtils.getMetrices(LauncherActivity.this).heightPixels);
                        }
                    }

                    mTvView = TvViewFactory.create(LauncherActivity.this, mTemplateEntity);
                    mTvView.setIMarqueeService(marqueeService);
                    HeaderLayout.HeadEntity headEntity = new HeaderLayout.HeadEntity(false, null, true);

                    headEntity.showWeather = false;
                    mTvView.setDiskCache(iDiskCache)
                            .setTvPageData(mCellList)
                            .setNavData(mTemplateEntity)
                            .setControlData(mControlBean)
                            .setShowReflect(true)//是否生成镜像
                            .setHeadEntity(headEntity)
                            .setAnimStyle(ITvFocusAnimat.TV_PAGE_NOT_MOVE_ANIM)
                            .setShadowAmend(12)
                            .setAnimDuration(500)
                            .setCreateNavLayout(true)
                            .setCreateHeaderLayout(true)
                            .createView();
                    mTvView.setLoadShowing(mLoadView.getVisibility() == View.VISIBLE);
                    mTvView.setOnCellItemClick(new BaseTvView.OnCellItemClick() {
                        @Override
                        public void onCellItemClick(ITvPageItemView view) {
                            FlyLog.d("click-->" + view.getCellData().toString());
                            //TODO 执行鉴权
                            final String packageName = view.getPackName();
                            if (view.getCellData().isNeedAuth()) {
                                try {
                                    if (aaaServiceConnect != null && aaaServiceConnect.getApplyCheck(packageName)) {
                                        view.doAction();
                                    } else {
                                        DialogUtil.showDialog(LauncherActivity.this, getString(R.string.aaa_not_auth));
                                    }
                                } catch (Exception e) {
                                    DialogUtil.showDialog(LauncherActivity.this, getString(R.string.aaa_error));
                                    FlyLog.d(e.toString());
//                                    e.printStackTrace();
                                }
                            } else {
                                view.doAction();
                            }
                        }
                    });

                    mTvView.setOnKeyDownOutEnvent(LauncherActivity.this);

                    mActivityRoot.addView(mTvView);
                    /**
                     * 提取广告类型set
                     */
                    Set<Integer> adsTypes = new HashSet<>();
                    for (TvCellBean bean : mCellList) {
                        List<CellEntity> cellList = bean.getCellList();
                        if (cellList == null || cellList.size() == 0) {
                            continue;
                        }
                        for (CellEntity enitity : cellList) {
                            if (enitity.getType() == CellType.TYPE_ADS_IMAGE
                                    || enitity.getType() == CellType.TYPE_LIVE
                                    || enitity.getType() == CellType.TYPE_ADS_VIDEO
                                    ) {
                                try {
                                    int id = Integer.parseInt(enitity.getAdsId());
                                    adsTypes.add(id);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    FlyLog.e(e.toString());
                                }
                            }
                        }
                    }
                    loadAdsData(adsTypes);

                    //上报事件
                    String curName = mTemplateEntity.getTemplateName();
                    String curId = mTemplateEntity.getTemplateId() + "";
                    BehavioralUtil.reportTemplateEvent(mContext, curName, curId);
                    FlyLog.d("updataView end....");
//                    TemplateUtil.setSimpleTemplateInfo(mContext, iUpDataVersion.getTemplateBean());
                }
                hideLoadingDialogDelay(5000);
                isUpdataViewRunning = false;
                if (mLoadView != null && mLoadView.getVisibility() == View.VISIBLE) {
                    mLoadView.checkCpuLoad();
                } else {
                    FlyLog.d("LoadAnimView is InVisible");
                }
            }
        }, 100);
    }

    /**
     * 注册广告更新的广播监听
     */
    private void initAdsModule() {
        IAdsModule instance = AdsModule.getInstance();
        instance.registerReceiver(getApplicationContext());
    }

    /**
     * 加载广告数据
     */
    private void loadAdsData(Set<Integer> adsTypes) {
        IAdsModule instance = AdsModule.getInstance();
        instance.loadAdsData(getApplicationContext(), adsTypes);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        FlyLog.d("onWindowFocusChanged " + hasFocus);
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        FlyLog.d(TAG + " dispatchKeyEvent: action " + event.getAction() + " keycode:" + event.getKeyCode());
        if (isLoading() || isUpdataViewRunning) {
            FlyLog.d("dispatchKeyEvent return true!");
            return true;
        }

        if (event.getAction() == KeyEvent.ACTION_DOWN) {

            if (BackdoorUtil.checkBackDoorEntry(event)) {
                //获取后门入口系统属性
                backDoor = Boolean.valueOf(SystemPropertiesProxy.get(this, Constants.Property.BACKDOOR_SWITCH,
                        "false"));
                if (backDoor) {
                    try {
                        Intent it = new Intent(Constants.Action.BACK_DOOR);
                        startActivity(it);
                    } catch (Exception e) {
                        e.printStackTrace();
                        FlyLog.d(e.toString());
                    }
                }
                return false;
            } else if (BackdoorUtil.checkUpdateBackDoor(event)) {
                FlyLog.d(TAG + " backdoor,start up version...");
                ToastUtils.showMessage(this, getString(R.string.back_door_update_tip));
                iUpDataVersion.forceUpVersion(this);
                return false;
            } else if (BackdoorUtil.checkMarqueeBackDoor(event)) {
                FlyLog.d(TAG + " backdoor,show marquee...");
                return false;
            } else if (event.getKeyCode() == KeyEvent.KEYCODE_MENU || event.getKeyCode() == KeyEvent.KEYCODE_M) {
//                showChangeTemplateDialog();
            }
        }
        //测试用代码，升级后门
//        if (event.getAction() == KeyEvent.ACTION_DOWN
//                && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
//            showHideNum++;
//            if (showHideNum > 10) {
//                FlyLog.d(TAG + " start up version...");
//                ToastUtils.showMessage(this, getString(R.string.back_door_update_tip));
//                iUpDataVersion.startUpVersion(this);
//            }
//        }
        if (event.getAction() == KeyEvent.ACTION_UP
                && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
            showHideNum = 0;
        }

        //ViewPager按键冲突问题
        if (mTvView != null && !(mTvView instanceof PopupTvView)) {
            if (event.getAction() == KeyEvent.ACTION_DOWN
                    && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
                onKeyDown(KeyEvent.KEYCODE_DPAD_LEFT, event);
                return false;
            }
            if (event.getAction() == KeyEvent.ACTION_DOWN
                    && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
                onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, event);
                return false;
            }
            if (event.getAction() == KeyEvent.ACTION_DOWN
                    && (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER
                    || event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                onKeyDown(KeyEvent.KEYCODE_DPAD_CENTER, event);
                return false;
            }
        }
        return super.dispatchKeyEvent(event);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean bRet = false;
        if (mTvView != null && mTvView instanceof PPfunsTvView) {
            mTvView.onKeyDown(keyCode, event);
        }
        boolean sound_flag = (boolean) SPUtil.get(this, SPUtil.FILE_CONFIG, SPUtil.CONFIG_SOUND_FLAG, true);
        if (sound_flag && keyCode != KeyEvent.KEYCODE_F12/*语音键*/) {
            //声音播放处理
            if (soundPlay == null) {
                soundPlay = new SoundPlay(this);
                soundIndex = (int) SPUtil.get(this, SPUtil.FILE_CONFIG, SPUtil.CONFIG_SOUND_INDEX, defaultSoundIndex);
            }
            soundPlay.playSound(soundIndex, 0);
        }

        if (keyCode != KeyEvent.KEYCODE_F12/*语音键*/) {

        }
        bRet = super.onKeyDown(keyCode, event);
        return bRet;
    }

    @Override
    protected void onResume() {
        isActivityRunning = true;
        FlyLog.d("onResume start....");
        FastWallpaper.getInstance().show();

        Constants.IS_LAUNCHER_ON_TOP = true;
        super.onResume();
        if (mTvView != null && !HomeWatcher.IS_HOME_KEY) {
            mHandler.sendEmptyMessageDelayed(MSG_DELAY_PLAY, delayPlayTime);
            mTvView.onResume();
        } else if (mTvView != null) {
            mTvView.notifyPageChange(0);
        }
        HomeWatcher.IS_HOME_KEY = false;
//        Constants.cellNoFocus = (float) SPUtil.get(mContext, SPUtil.CONFIG_ALPHA_CELL, 1.0f);
        BehavioralUtil.reportInEvent(mContext);
//        updataView(needUPtemplateEntity, needUPcellBeanList, needUPcontrolBean);
        FlyLog.d("onResume end....");
    }


    private boolean isActivityRunning = false;

    @Override
    protected void onPause() {
        isActivityRunning = false;
        FlyLog.d(TAG + " onPause,is home key:" + HomeWatcher.IS_HOME_KEY);
        super.onPause();
        Constants.IS_LAUNCHER_ON_TOP = false;
        if (mTvView != null && !HomeWatcher.IS_HOME_KEY) {
            mHandler.removeMessages(MSG_DELAY_PLAY);
            mTvView.notifyPageChange(1);
            mTvView.onPause();
        }
    }

    /**
     * 屏蔽退出按键
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        FlyLog.d(" onStart...");
        super.onStart();
    }

    @Override
    protected void onStop() {
        FlyLog.d(" onStop...");
        super.onStop();
        if (mTvView != null) {
            mTvView.notifyPageChange(1);
        }
        BehavioralUtil.reportOutEvent(mContext);
    }

    @Override
    protected void onDestroy() {
        if (binded) {
            FlyLog.d("====unbindService");
            try {
                marqueeService.stop();
                marqueeService.release();
            } catch (RemoteException e) {
                e.printStackTrace();

            }
            unbindService(conn);
            binded = false;

        }
        aaaServiceConnect.unregisterPropertyListener(Constants.AAA_TOKEN_QUERY_KEY);
        iUpDataVersion.cancelAllTasks();
        iDiskCache.release();
        hideLoadingDialogDelay(0);
        isUpdataViewRunning = false;
        mHandler.removeCallbacksAndMessages(null);
        unregisterReceiver();
        super.onDestroy();
        MemoryManager.fixInputMethodManagerLeak(this);
        aaaServiceConnect.unbindService(this);
//        playerServiceConnect.unbindService(this);
        AdsModule.getInstance().unRegisterReceiver(getApplicationContext());
    }

    /**
     * 数据已在线程中进行正确性检测，无需再做判断，加载完缓存数据再执行版本更新
     *
     */
//    @Override
//    public void getCacheDataOK(TemplateEntity templateEntity, List<CellBean> cellBeanList, ControlBean controlBean) {
//        FlyLog.d();
//        this.needUPtemplateEntity = templateEntity;
//        this.needUPcellBeanList = cellBeanList;
//        this.needUPcontrolBean = controlBean;
//        isUpdata = true;
//        updataView(templateEntity, cellBeanList, controlBean);
//    }

    @Override
    public void getCacheDataOK(ThemeBean themeBean) {
        FlyLog.d(""+themeBean);
    }

    @Override
    public void getCacheDataFaile(String error) {
        FlyLog.d(error);
        mHandler.removeMessages(MSG_UPDATE);
        mHandler.sendEmptyMessageDelayed(MSG_UPDATE, UpdataVersion.UPDATE_IINTERVAL);
    }

    /**
     * 以下为线程中检测是否有版本更新所返回的信息数据结果
     */
//    @Override
//    public void upVersionOK(TemplateEntity templateEntity, List<CellBean> cellBeanList, ControlBean controlBean) {
//        FlyLog.d();
//        this.needUPtemplateEntity = templateEntity;
//        this.needUPcellBeanList = cellBeanList;
//        this.needUPcontrolBean = controlBean;
//        isUpdata = true;
//        updataView(templateEntity, cellBeanList, controlBean);
//    }

    @Override
    public void upVersionOK(ThemeBean themeBean) {
        TemplateEntity templateEntity = new TemplateEntity();
        templateEntity.templateId = themeBean.themeId;
        templateEntity.templateName = themeBean.themeName;
        templateEntity.isdefault = "true";
        templateEntity.backgroundImage = themeBean.imageurl;
        templateEntity.backgroundColor = "#00000000";
        templateEntity.x = 0;
        templateEntity.y = 0;
        templateEntity.tabList = new ArrayList<>();
        List<TvCellBean> cellBeanList = new ArrayList<>();
        for(PageBean pageBean:themeBean.pageList){
            TabEntity tabEntity = new TabEntity();
            tabEntity.name = pageBean.pageName;
            templateEntity.tabList.add(tabEntity);

            TvCellBean cellBean = new TvCellBean();
            cellBean.cellList = converPageList(pageBean.cellList);
            cellBeanList.add(cellBean);
        }

        ControlBean controlBean = new ControlBean();
        isUpdata = true;
        updataView(templateEntity, cellBeanList, controlBean);
    }

    private List<CellEntity> converPageList(List<CellBean> cellList) {
        List<CellEntity> cellEntities = new ArrayList<>();
        for(CellBean cell:cellList){
            try {
                CellEntity cellEntity = new CellEntity();
                cellEntity.imgUrl = cell.images.get(0).url;
                cellEntity.x = cell.x;
                cellEntity.y = cell.y;
                cellEntity.width = cell.width;
                cellEntity.height = cell.height;
                cellEntity.text = cell.texts.get(0).text.getText();
                cellEntities.add(cellEntity);
            }catch (Exception e){
                FlyLog.e(e.toString());
            }
        }
        return cellEntities;
    }

    @Override
    public void upVesionProgress(String msg, int sum, int progress) {
        FlyLog.d("%s进度%d/%d", msg, progress, sum);
        boolean tip_flag = (boolean) SPUtil.get(this, SPUtil.CONFIG_UPDATE_TIPS, SPUtil.Default.tips);
        if (tip_flag) {//通过后门选择控制提示信息
            ToastUtils.showMessage(this, msg + "(" + progress + "/" + sum + ")");
        }
    }

    @Override
    public void upVersionFaile(String error) {
        ToastUtils.showMessage(this, error);
        FlyLog.d(error);
        mHandler.removeMessages(MSG_UPDATE);
        errCount++;
        int delayTime = errCount * errCount * baseSecond;
        mHandler.sendEmptyMessageDelayed(MSG_UPDATE, delayTime > UpdataVersion.UPDATE_IINTERVAL ? UpdataVersion.UPDATE_IINTERVAL : delayTime);
    }

    private void initView() {
        FlyLog.d("initview start....");

        mActivityRoot = (FrameLayout) findViewById(R.id.ac_launcher_root);

//        mBgView = findViewById(R.id.ac_launcher_bg);
        mLoadView = (LoadAnimView) findViewById(R.id.ac_load_view);
//        mLoadView.loadImageView(R.mipmap.back);
        mLoadView.addEvent(new LoadAnimView.LoadEvent() {
            @Override
            public void dismiss() {
                if (mTvView != null) {
                    mTvView.loadingDismiss();
                }

                //通知其它应用Launcher已经加载完成
                Intent intent = new Intent(LOAD_FINISHED);
                intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                sendStickyBroadcast(intent);

                //通知其它应用Launcher已经加载完成的系统属性标记
                SystemPropertiesProxy.set(LauncherActivity.this, "sys.launcher.load.finished", String.valueOf(1));

                //luancher启动画面隐藏时再把小视窗音量放开
                AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
                am.setStreamMute(android.media.AudioManager.STREAM_MUSIC, false);

            }

            @Override
            public void showing() {
                if (mTvView != null) {
                    mTvView.setLoadShowing(true);
                }
            }
        });
        FlyLog.d(TAG + "  initview end....");
    }

    public void onEvent(EventMessage msg) {
        if (EventMessage.MSG_UPDATE_VERSION == msg.index) {
            FlyLog.d(TAG + " start update version");
            iUpDataVersion.startUpVersion(this);
        } else if (EventMessage.MSG_CHANGE_TEMPLATE == msg.index) {
            FlyLog.d(TAG + " change template");
            if (isLoading()) {
                return;
            }
            showChangelTemplateTipAndReport(mContext.getString(R.string.tip_switch_ui));
//            iUpDataVersion.switchTemplate();
        } else if (EventMessage.MSG_UPDATE_SOUND_INDEX == msg.index) {
            soundIndex = (int) SPUtil.get(this, SPUtil.FILE_CONFIG, SPUtil.CONFIG_SOUND_INDEX, defaultSoundIndex);
            FlyLog.d(TAG + " change sound --sound index;" + soundIndex);
        } else if (EventMessage.MSG_UPDATE_TIME_THEME == msg.index) {
            mHandler.sendEmptyMessage(MSG_UPDATE_TIME_THEME);
        }
    }

    /**
     * 注册广播
     */
    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter(Constants.Action.CHANGE_TEMPLATE);
        intentFilter.addAction(Constants.Action.SPEECH_SWITCH_UI);
        intentFilter.addAction(Constants.Action.CHILD_CHANGE_UI);
        registerReceiver(myBroadcast, intentFilter);
    }

    /**
     * 取消广播注册
     */
    private void unregisterReceiver() {
        unregisterReceiver(myBroadcast);
    }

    private void showChangelTemplateTipAndReport(String info) {
        ToastUtils.showMessage(mContext, info);
        if (info.startsWith(getString(R.string.tip_switch_ui))) {
            showLoadingDialog();
        }
    }

    /**
     * 显示切换模板提示框
     */
    private void showChangeTemplateDialog() {
//        FlyLog.i(TAG + "showChangeTemplateDialog");
//        Map<Integer, String> data = new ArrayMap<>();
//        TemplateBean bean = iUpDataVersion.getTemplateBean();
//        if (bean != null && bean.getTemplate() != null) {
//            for (TemplateEntity entity : bean.getTemplate()) {
//                if (entity.getIsdefault().equals("true")) {
//                    data.put(entity.getTemplateId(), entity.getTemplateName());
//                }
//            }
//        } else {
//            FlyLog.e(TAG + "template is null,no data to show change template dialog...");
//            return;
//        }
//        if (data == null || data.size() < 2) {
//            ToastUtils.showMessage(mContext, R.string.one_template);
//            return;//只有一个模板时不显示切换模板
//        }
//        if (mChangeTemplateDialog == null) {
//            mChangeTemplateDialog = new ChangeTemplateDialog(this);
//        }
//        if (mChangeTemplateDialog.isShowing()) {
//            mChangeTemplateDialog.dismiss();
//        }
//        boolean isBlur = Boolean.parseBoolean(SystemPropertiesProxy.get(mContext, com.flyzebra.ppfunstv.constant.Constants.Property.DIALOG_BLUR, "false"));
//        View view = getWindow().getDecorView();
//        if (!isBlur) {
//            mChangeTemplateDialog.setIsBlur(false);
//            mChangeTemplateDialog.setView(view);
//            mChangeTemplateDialog.setData(data);
//            mChangeTemplateDialog.show();
//        } else {
//            mChangeTemplateDialog.setIsBlur(true);
//            mChangeTemplateDialog.setView(view);
//            mChangeTemplateDialog.setData(data);
//            mChangeTemplateDialog.show();
//        }
    }

    @Override
    public boolean onKeyDownGoLeft(View view) {
        return false;
    }

    @Override
    public boolean onKeyDownGoRight(View view) {
        return false;
    }


//    private Toast toast;

    @Override
    public boolean onKeyDownGoUp(View view) {
//        long curTime = new Date().getTime();
//        if (curTime - lastPressTime < default_interval) {
//            if (toast != null) {
//                toast.cancel();
//            }
//            try {
//                Intent intent = new Intent(this, SetActivity.class);
//                startActivity(intent);
//            } catch (Exception e) {
//                FlyLog.d(e.toString());
//                e.printStackTrace();
//            }
//        } else {
//            toast = Toast.makeText(mContext, mContext.getString(R.string.tip_show_status_set), Toast.LENGTH_SHORT);
//            toast.show();
//        }
//        lastPressTime = curTime;

        return true;
    }

    @Override
    public boolean onKeyDownGoDown(View view) {
        return false;
    }

    /**
     * 广播接收器
     * 1:切换模块广播
     * 2:儿童版内部切换UI广播
     */
    class MyBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Constants.Action.CHANGE_TEMPLATE.equals(action) || Constants.Action.SPEECH_SWITCH_UI.equals(action)) {
                //切换模板
//                String name = intent.getStringExtra(Constants.KEY_TEMPLATE_NAME);
//                int id = intent.getIntExtra(Constants.KEY_TEMPLATE_ID, -1);
//                if (isLoading()) {
//                    return;
//                }
//                String info = TemplateUtil.changeTemplate(mContext, iUpDataVersion, id, name);
//                if (!TextUtils.isEmpty(info)) {
//                    showChangelTemplateTipAndReport(info);
//                }
            } else if (Constants.Action.CHILD_CHANGE_UI.endsWith(action)) {
                //儿童版内部切换ui
                int ageIndex = intent.getIntExtra("ageIndex", -1);
                if (-1 != ageIndex) {
                    SPUtil.set(context, SPUtil.FILE_CONFIG, SPUtil.CONFIG_AGE_RANGE, ageIndex);
                } else {
                    ageIndex = (int) SPUtil.get(context, SPUtil.FILE_CONFIG, SPUtil.CONFIG_AGE_RANGE, 0);
                }
                FlyLog.i(TAG + " change child ui,ageIndex:" + ageIndex);
                if (mTvView != null && mTvView instanceof PopupTvView) {
                    mTvView.createView();
                } else {
                    FlyLog.e(TAG + " not in child version,no need to change ui...");
                }
            }
        }
    }

    boolean binded = false;

    private IMarqueeService marqueeService;

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            FlyLog.d("====onServiceConnected");
            marqueeService = IMarqueeService.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            FlyLog.d("onServiceDisconnected");
            try {
                FlyLog.d();
                marqueeService.stop();
                marqueeService.release();
                marqueeService = null;
            } catch (RemoteException e) {
                e.printStackTrace();

            }

        }
    };

}
