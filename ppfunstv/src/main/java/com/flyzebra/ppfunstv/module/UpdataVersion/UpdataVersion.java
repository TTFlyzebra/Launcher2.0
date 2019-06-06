package com.flyzebra.ppfunstv.module.UpdataVersion;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.flyzebra.flyui.chache.DiskCache;
import com.flyzebra.flyui.chache.IDiskCache;
import com.flyzebra.ppfunstv.R;
import com.flyzebra.ppfunstv.data.CellEntity;
import com.flyzebra.ppfunstv.data.ControlBean;
import com.flyzebra.ppfunstv.data.SubScript;
import com.flyzebra.ppfunstv.data.TabEntity;
import com.flyzebra.ppfunstv.data.TemplateBean;
import com.flyzebra.ppfunstv.data.TemplateEntity;
import com.flyzebra.ppfunstv.data.TvCellBean;
import com.flyzebra.ppfunstv.data.VersionBean;
import com.flyzebra.ppfunstv.http.FlyOkHttp;
import com.flyzebra.ppfunstv.http.IHttp;
import com.flyzebra.ppfunstv.utils.EncodeUtil;
import com.flyzebra.ppfunstv.utils.FileUtil;
import com.flyzebra.ppfunstv.utils.FlyLog;
import com.flyzebra.ppfunstv.utils.GsonUtil;
import com.flyzebra.ppfunstv.utils.SPUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 版本更新 *
 * 该类还没有增加空值判断处理
 * 为提高性能，不出现重复读取操作，出现了各种代码，各种空值判断可以略过不要去阅读理解
 * Created by FlyZebra on 2016/6/21.
 */
public class UpdataVersion implements IUpdataVersion, IUpDataVersionError {


    private static final String TAG = UpdataVersion.class.getSimpleName();
    private static final String ASSETS_PATH = "ppfuns/";
    private Context mContext;
    private IDiskCache iDiskCache;
    private Set<AsyncTask> taskCollection = new HashSet<>();
    private UpResult upResult;
    private CheckCacheResult checkCacheResult;
    private String localVersion = "0.00";
    private Map<String, String> mAllCellBeanJsons = new HashMap<>();
    private AtomicInteger mResourceJsonSum = new AtomicInteger(0);
    private List<String> mImageList = new ArrayList<>();
    private int mResourceImgSum = 0;
    private AtomicInteger mAtomicImgCount = new AtomicInteger(0);
    /**
     * 当前模板所有页面Cell数据列表
     */
    private List<TvCellBean> mCurrentCellBeanList = new ArrayList<>();
    private List<TvCellBean> mAllCellBeanList = new ArrayList<>();
    private Map<String, String> mAllControlJsons = new HashMap<>();
    private Map<Integer, ControlBean> mAllControlList = new HashMap<>();
    private String mTemplateBeanJson;
    private TemplateBean mTemplateBean;
    private AtomicInteger tabEntitySum = new AtomicInteger(0);
    private String HTTPTAG = "UpdataVersion" + Math.random();
    private boolean isUpSuccess = true;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    private static ExecutorService executor = Executors.newFixedThreadPool(1);

    private boolean isUPVeriosnRunning = false;

    public static int UPDATE_IINTERVAL = 15 * 60 * 1000;//3 * 60 *
    /**
     * 前端可配置默认最小更新时间(10s)
     */
    public static int UPDATE_MIN_IINTERVAL = 10 * 1000;
    /**
     * 对应请求API接口
     */
    protected String ApiUrl = "http://192.168.1.12:9020";

    //token
    protected String token = "";
    protected String tokenFromat = "&token=%s";
    //版本号
    protected String ApiVersion = "/api/ui-operation/api/v/launcher_version.json?areaCode=%s&type=launcher&version=%s&versionSw=%s&devCode=%s&versionHw=%s&userId=%s";
    //Template更新地址
    protected String ApiTemplate = "/api/ui-operation/api/v/launcher_tab.json?areaCode=%s&type=launcher&version=%s&versionSw=%s&devCode=%s&versionHw=%s&userId=%s";
    //CellList更新地址
    protected String ApiCellList = "/api/ui-operation/api/v/launcher_cell.json?tabId=";
    //跑马灯Logo更新地址
    protected String ApiResource = "/api/ui-operation/api/v/launcher_resource.json?templateId=";

    protected String VERSION_KEY = "/api/ui-operation/api/v/launcher_version.json";
    protected String TEMPLATE_KEY = "/api/ui-operation/api/v/launcher_tab.json";
    private String templateCode;

    public UpdataVersion(Context context) {
        this.mContext = context;
        iDiskCache = new DiskCache().init(context);
    }

    public UpdataVersion(Context context, @NonNull IDiskCache iDiskCache) {
        this.mContext = context;
        this.iDiskCache = iDiskCache;
    }


    @Override
    public void initApi() {
    }


    private boolean checkUrl() {
        initApi();
        return ApiUrl.startsWith("http://") || ApiUrl.startsWith("https://");
    }

    @Override
    public UpdataVersion setDiskCache(IDiskCache iDiskCache) {
        this.iDiskCache = iDiskCache;
        return this;
    }

    @Override
    public void getCacheData(final CheckCacheResult checkResult) {
        this.checkCacheResult = checkResult;
        executor.execute(new Runnable() {
            @Override
            public void run() {
                mTemplateBean = getTemplateBean();
                if (mTemplateBean == null) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (checkCacheResult != null) {
                                checkCacheResult.getCacheDataFaile(UP_ASSETS_ERROR);
                            }
                        }
                    });
                } else {
                    final int templateIndex = getDefaultTemplateIndex(mTemplateBean);
                    final TemplateEntity templateEntity = mTemplateBean.getTemplate().get(templateIndex);
                    final List<TvCellBean> cellBeans = getCellBeanList(templateIndex);
                    final ControlBean controlBean = getControlBean(templateIndex);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (checkCacheResult != null)
                                checkCacheResult.getCacheDataOK(templateEntity, cellBeans, controlBean);
                        }
                    });
                }
            }
        });
    }

    @Override
    public TemplateBean getTemplateBean() {
        String mTemplateBeanJson = iDiskCache.getString(TEMPLATE_KEY);
        if (TextUtils.isEmpty(mTemplateBeanJson)) {
            mTemplateBeanJson = getAssetsFileString(TEMPLATE_KEY);
        }
        mTemplateBean = GsonUtil.json2Object(mTemplateBeanJson, TemplateBean.class);
        return mTemplateBean;
    }


    /**
     * 根据template获取controlBean信息
     *
     * @param templateIndex 被选中的模板的index
     * @return
     */
    private ControlBean getControlBean(int templateIndex) {
        ControlBean mCurrentControlBean = null;
        if (mTemplateBean != null && mTemplateBean.getTemplate() != null && mTemplateBean.getTemplate().size() > templateIndex) {
            String key = ApiResource + mTemplateBean.getTemplate().get(templateIndex).getTemplateId();
            String value = iDiskCache.getString(key);
            if (TextUtils.isEmpty(value)) {
                value = getAssetsFileString(key);
            }
            mCurrentControlBean = GsonUtil.json2Object(value, ControlBean.class);
        }
        return mCurrentControlBean;
    }

    public List<TvCellBean> getCellBeanList(int templateIndex) {
        if (mTemplateBean == null) {
            mTemplateBean = getTemplateBean();
        }
        if (mTemplateBean == null) {
            return null;
        }
        if (mTemplateBean.getTemplate() != null && mTemplateBean.getTemplate().size() > templateIndex) {
            List<TabEntity> tabList = mTemplateBean.getTemplate().get(templateIndex).getTabList();
            mCurrentCellBeanList.clear();
            for (int i = 0; i < tabList.size(); i++) {
                String key = ApiCellList + tabList.get(i).getId();
                FlyLog.d("tab size:" + tabList.size() + " current key:" + key);
                String json = iDiskCache.getString(key);
                if (TextUtils.isEmpty(json)) {
                    json = getAssetsFileString(key);
                }
                if (TextUtils.isEmpty(json)) {
                    return null;
                }
                TvCellBean cell = GsonUtil.json2Object(json, TvCellBean.class);
                //TODO 前台数据错误处理
                mCurrentCellBeanList.add(cell);
            }
        }
        return mCurrentCellBeanList;
    }


    @Override
    public void startUpVersion(UpResult upResult) {
        FlyLog.d();
        if (!checkUrl()) {
            FlyLog.d("invalid url! url=%s", ApiUrl);
            return;
        }
        this.upResult = upResult;
        if (!isUPVeriosnRunning) {
            isUPVeriosnRunning = true;
            isUpSuccess = true;
            checkVersion(ApiUrl + ApiVersion+ String.format(tokenFromat, token));
        }
    }

    @Override
    public void forceUpVersion(UpResult upResult) {
        FlyLog.d();
        FlyOkHttp.getInstance().cancelAll(HTTPTAG);
        if (taskCollection != null) {
            for (AsyncTask task : taskCollection) {
                task.cancel(true);
            }
            taskCollection.clear();
        }
        isUPVeriosnRunning = false;

        if (!checkUrl()) {
            FlyLog.d("invalid url! url=%s", ApiUrl);
            return;
        }

        this.upResult = upResult;
        mHandler.removeCallbacksAndMessages(null);
        if (!isUPVeriosnRunning) {
            isUPVeriosnRunning = true;
            isUpSuccess = true;
            getTemplateBean(ApiUrl + ApiTemplate+ String.format(tokenFromat, token));
        }
    }

    @Override
    public void setDefualtTemplate(String templateCode) {
        this.templateCode = templateCode;
    }

    @Override
    public void switchTemplate() {
        getCacheData(checkCacheResult);
    }

    /**
     * 检测版本更新数据
     *
     * @param urlAPI
     */
    private void checkVersion(final String urlAPI) {
        FlyOkHttp.getInstance().getString(urlAPI, HTTPTAG, new IHttp.HttpResult() {
            @Override
            public void succeed(final Object object) {
                String version = iDiskCache.getString(VERSION_KEY);
                if (version != null) {
                    VersionBean bean = GsonUtil.json2Object(version, VersionBean.class);
                    if (bean != null) {
                        localVersion = bean.getVersion();
                        if (bean.getVersionInterval() > UPDATE_MIN_IINTERVAL) {
                            UPDATE_IINTERVAL = bean.getVersionInterval() * 1000;
                        }
                    }
                }
                String newVersion = null;
                if (object != null) {
                    VersionBean bean = GsonUtil.json2Object(object.toString(), VersionBean.class);
                    if (bean != null && bean.isValid()) {
                        newVersion = bean.getVersion();
                        if (bean.getVersionInterval() > UPDATE_MIN_IINTERVAL) {
                            UPDATE_IINTERVAL = bean.getVersionInterval() * 1000;
                        }
                    }
                }

                if (newVersion != null && !newVersion.equals(localVersion)) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (upResult != null)
                                upResult.upVesionProgress(FIND_NEW_VERSION + "(" + localVersion + "->" + object + ")", 1, 1);
                        }
                    });
                    localVersion = object.toString();

                    getTemplateBean(ApiUrl + ApiTemplate + String.format(tokenFromat, token));
                } else {
//                    mHandler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (upResult != null) upResult.upVersionFaile(NO_NEW_VERSION);
//                        }
//                    });
                    FlyLog.d(TAG + " no new version!!! no need to update version.....");
                    isUPVeriosnRunning = false;
                    isUpSuccess = false;
                }
            }

            @Override
            public void failed(Object object) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (upResult != null) upResult.upVersionFaile(NETWORK_ERROR);
                    }
                });
                isUPVeriosnRunning = false;
                //TODO 更新失败是否需要重连......
            }
        });
    }

    private void getTemplateBean(String urlAPI) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (upResult != null)
                    upResult.upVesionProgress(mContext.getString(R.string.tv_up_version_nav_msg), 1, 0);
            }
        });

        FlyOkHttp.getInstance().getString(urlAPI, HTTPTAG, new IHttp.HttpResult() {
            @Override
            public void succeed(Object object) {
                mTemplateBeanJson = object.toString();
                TemplateBean bean = GsonUtil.json2Object(mTemplateBeanJson, TemplateBean.class);
                if (bean == null) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (upResult != null) upResult.upVersionFaile(TAB_LIST_ERROR);
                        }
                    });
                    isUPVeriosnRunning = false;
                    isUpSuccess = false;
                } else if (bean.isValid()) {
                    mTemplateBean = bean;
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (upResult != null)
                                upResult.upVesionProgress(mContext.getString(R.string.tv_up_version_nav_msg), 1, 1);
                        }
                    });
                    getAllResourceData(mTemplateBean);
                } else {
                    //TODO 后台给的数据不对
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (upResult != null) upResult.upVersionFaile(UP_TEMPLATE_NULL);
                        }
                    });
                    isUPVeriosnRunning = false;
                    isUpSuccess = false;
                }
            }

            @Override
            public void failed(Object object) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (upResult != null) upResult.upVersionFaile(NETWORK_ERROR);
                    }
                });
                isUPVeriosnRunning = false;
                isUpSuccess = false;
            }
        });
    }

    /**
     * 获取所有模板的Cell并保存
     *
     * @param mTemplateBean
     */
    private void getAllCellBean(TemplateBean mTemplateBean) {
        final List<TemplateEntity> templateList = mTemplateBean.getTemplate();
        int mSum = 0;
        for (int n = 0; n < templateList.size(); n++) {
            final List<TabEntity> tabList = templateList.get(n).getTabList();
            mSum = mSum + tabList.size();
        }
        mAllCellBeanJsons.clear();
        mAllCellBeanList.clear();
        tabEntitySum.set(mSum);
        for (int n = 0; n < templateList.size(); n++) {
            final List<TabEntity> tabList = templateList.get(n).getTabList();
            for (int i = 0; i < tabList.size(); i++) {
                final String url = ApiUrl + ApiCellList + tabList.get(i).getId()+ String.format(tokenFromat, token);
                final String key = ApiCellList + tabList.get(i).getId();
                FlyOkHttp.getInstance().getString(url, HTTPTAG, new IHttp.HttpResult() {
                    @Override
                    public void succeed(Object object) {
                        if (object == null || object.equals("")) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (upResult != null)
                                        upResult.upVersionFaile(NETWORK_DATA_ERROR);
                                }
                            });
                            isUpSuccess = false;
                            isUPVeriosnRunning = false;
                            return;
                        }
                        String str = object.toString();
                        TvCellBean cellBean = GsonUtil.json2Object(str, TvCellBean.class);
                        if (cellBean == null || !cellBean.isValid()) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (upResult != null) upResult.upVersionFaile(TAB_LIST_ERROR);
                                }
                            });
                            isUpSuccess = false;
                            isUPVeriosnRunning = false;
                            return;
                        }

                        mAllCellBeanJsons.put(key, str);
                        mAllCellBeanList.add(cellBean);

                        if (tabEntitySum.getAndDecrement() == 1) {
//                            mAllCellBeanList.clear();
                            //所有列表更新完毕。开始下载更新图片
//                            for (Map.Entry<String, String> entry : mAllCellBeanJsons.entrySet()) {
                            for (TvCellBean cell : mAllCellBeanList) {
//                                CellBean cell = GsonUtils.json2Object(entry.getValue(), CellBean.class);
//                                if (cell != null) {
//                                    mAllCellBeanList.add(cell);

                                //添加图片
                                List<CellEntity> cells = cell.getCellList();
                                if (cells != null && cells.size() > 0) {
                                    for (CellEntity entity : cells) {
                                        //添加显示图片
                                        if (!TextUtils.isEmpty(entity.getImgUrl())) {
                                            mImageList.add(entity.getImgUrl());
                                        }
                                        //添加焦点切换要设置的背景图片
                                        if (!TextUtils.isEmpty(entity.getImgUrlBg())) {
                                            mImageList.add(entity.getImgUrlBg());
                                        }
                                        List<SubScript> subScripts = entity.getSubScripts();
                                        if (subScripts != null && subScripts.size() > 0) {
                                            for (SubScript item : subScripts) {
                                                //添加角标图片
                                                if (!TextUtils.isEmpty(item.url)) {
                                                    mImageList.add(item.url);
                                                }
                                            }
                                        }
                                        List<CellEntity> subCells = entity.getSubCellList();
                                        if (subCells != null && subCells.size() > 0) {
                                            for (CellEntity item : subCells) {
                                                //添加轮播图片
                                                if (!TextUtils.isEmpty(item.getImgUrl())) {
                                                    mImageList.add(item.getImgUrl());
                                                }
                                            }
                                        }
                                    }
                                }
                            }
//                            }
                            downloadResourceImage(mImageList);
                        }
                    }

                    @Override
                    public void failed(Object object) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (upResult != null) upResult.upVersionFaile(NETWORK_ERROR);
                            }
                        });
                        isUPVeriosnRunning = false;
                        isUpSuccess = false;
                    }
                });
            }
        }
    }

    /**
     * 获取所有资源信息
     *
     * @param mTemplateBean
     */
    private void getAllResourceData(final TemplateBean mTemplateBean) {
        final List<TemplateEntity> templateList = mTemplateBean.getTemplate();
        mAllControlJsons.clear();
        mResourceJsonSum.set(templateList.size());
        for (int n = 0; n < templateList.size(); n++) {
            final int templateId = templateList.get(n).getTemplateId();
            final String url = ApiUrl + ApiResource + templateId+ String.format(tokenFromat, token);
            final String key = ApiResource + templateList.get(n).getTemplateId();
            FlyOkHttp.getInstance().getString(url, HTTPTAG, new IHttp.HttpResult() {
                @Override
                public void succeed(Object object) {
                    if (object == null || object.equals("")) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (upResult != null) upResult.upVersionFaile(NETWORK_DATA_ERROR);
                            }
                        });
                        isUpSuccess = false;
                        isUPVeriosnRunning = false;
                        return;
                    }
                    String str = object.toString();
                    ControlBean bean = GsonUtil.json2Object(str, ControlBean.class);
                    if (bean == null || !bean.isValid()) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (upResult != null) upResult.upVersionFaile(TAB_LIST_ERROR);
                            }
                        });
                        isUpSuccess = false;
                        isUPVeriosnRunning = false;
                        return;
                    }

                    mAllControlJsons.put(key, str);
                    mAllControlList.put(templateId, bean);
                    if (mResourceJsonSum.getAndDecrement() == 1) {
                        mImageList.clear();
                        //添加待下载资源文件
                        for (Map.Entry<String, String> entry : mAllControlJsons.entrySet()) {
                            ControlBean controlBean = GsonUtil.json2Object(entry.getValue(), ControlBean.class);
                            if (controlBean != null && controlBean.getLogo() != null && !TextUtils.isEmpty(controlBean.getLogo().getImgUrl())) {
                                mImageList.add(controlBean.getLogo().getImgUrl());
                            }
                        }
                        //添加背景文件
                        for (TemplateEntity entity : templateList) {
                            if (!TextUtils.isEmpty(entity.getBackgroundImage())) {
                                mImageList.add(entity.getBackgroundImage());
                            }
                        }
                        //所有资源更新完毕,开始获取cell数据
                        getAllCellBean(mTemplateBean);
                    }
                }

                @Override
                public void failed(Object object) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (upResult != null) upResult.upVersionFaile(NETWORK_ERROR);
                        }
                    });
                    isUPVeriosnRunning = false;
                    isUpSuccess = false;
                }
            });
        }
    }

    private void downloadResourceImage(List<String> images) {
        if (images == null || images.size() == 0) {
            upVersion();
            return;
        }

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (upResult != null)
                    upResult.upVesionProgress(mContext.getString(R.string.tv_up_version_image_start), mAtomicImgCount.get(), 0);
            }
        });
        mAtomicImgCount.set(images.size());
        mResourceImgSum = images.size();
        for (int i = 0; i < images.size(); i++) {
            final String imgUrl = images.get(i);
            if (!TextUtils.isEmpty(imgUrl)) {
                //TODO 下载LOGO图片
                DownloadResourceImgTask task = new DownloadResourceImgTask();
                taskCollection.add(task);
                task.execute(imgUrl);
            } else {
                //TODO 没有配置图片
                if (mAtomicImgCount.getAndDecrement() == 1) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (upResult != null)
                                upResult.upVesionProgress(mContext.getString(R.string.tv_up_version_image_finish), mResourceImgSum, mResourceImgSum - mAtomicImgCount.get());
                        }
                    });
                    //所有更新已经完毕
                    upVersion();
                }
            }
        }
    }

    private class DownloadResourceImgTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            final boolean flag = iDiskCache.saveBitmapFromImgurl(params[0]);
            if (!flag) {
                isUpSuccess = false;
            }
            final String imgUrl = params[0];

            if (mAtomicImgCount.getAndDecrement() == 1) {
                //所有更新已经完毕
                upVersion();
            } else {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (flag) {
                            if (upResult != null)
                                if (0 == mAtomicImgCount.get()) {
                                    upResult.upVesionProgress(mContext.getString(R.string.tv_up_version_ok), mResourceImgSum, mResourceImgSum - mAtomicImgCount.get());
                                } else {
                                    upResult.upVesionProgress(imgUrl + mContext.getString(R.string.tv_up_version_image_ok), mResourceImgSum, mResourceImgSum - mAtomicImgCount.get());
                                }
                        } else {
                            if (upResult != null)
                                upResult.upVesionProgress(imgUrl + mContext.getString(R.string.tv_up_version_image_faile), mResourceImgSum, mResourceImgSum - mAtomicImgCount.get());
                        }
                    }
                });
            }
            return null;
        }
    }

    private void upVersion() {
        if (!isUpSuccess) {
            FlyLog.d("upVersion Failed!");
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (upResult != null) upResult.upVersionFaile(UP_FILE_FAILD);
                }
            });
        } else {
            iDiskCache.saveString(localVersion, VERSION_KEY);
//        iDiskCache.saveString(tabBeanJson, URL_TabBeanAPI);
            iDiskCache.saveString(mTemplateBeanJson, TEMPLATE_KEY);
            for (Map.Entry<String, String> entry : mAllCellBeanJsons.entrySet()) {
                iDiskCache.saveString(entry.getValue(), entry.getKey());
            }
            //保存资源json文件
            for (Map.Entry<String, String> entry : mAllControlJsons.entrySet()) {
                iDiskCache.saveString(entry.getValue(), entry.getKey());
            }
            final int templateIndex = getDefaultTemplateIndex(mTemplateBean);
            final TemplateEntity templateEntity = mTemplateBean.getTemplate().get(templateIndex);
            final List<TvCellBean> cellBeans = getCellBeanList(templateIndex);
            final ControlBean controlBean = getControlBean(templateIndex);
            FlyLog.d("upVersion OK!");
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    FlyLog.d("upVersion OK!");
                    if (upResult != null)
                        upResult.upVersionOK(templateEntity, cellBeans, controlBean);
                }
            });
            DelNoUseFiles();
        }

        //重置升级状态
        isUPVeriosnRunning = false;
        isUpSuccess = true;
    }


    public void cancelAllTasks() {
        FlyOkHttp.getInstance().cancelAll(HTTPTAG);
        if (taskCollection != null) {
            for (AsyncTask task : taskCollection) {
                task.cancel(true);
            }
            taskCollection.clear();
        }
        mHandler.removeCallbacksAndMessages(null);
        upResult = null;
        checkCacheResult = null;
    }


    /**
     * 思路
     * 根据缓存的tabjson文件遍历读取所需要的文件名放入Set集合
     * 遍历缓存目录中的文件，将set集合中不存在的文件名删除
     */
    private void DelNoUseFiles() {
        FlyLog.d("开始删除不用的缓存文件");
        Set<String> files = new HashSet<>();
        files.add("journal");
        mTemplateBean = getTemplateBean();
        if (mTemplateBean == null) return;
        files.add(EncodeUtil.md5(VERSION_KEY) + ".0");
        files.add(EncodeUtil.md5(TEMPLATE_KEY) + ".0");

        List<TemplateEntity> templateList = mTemplateBean.getTemplate();
        if (templateList == null) return;
        for (int n = 0; n < templateList.size(); n++) {
            final List<TabEntity> tabList = templateList.get(n).getTabList();
            for (int i = 0; i < tabList.size(); i++) {
                files.add(EncodeUtil.md5(ApiCellList + tabList.get(i).getId()) + ".0");
            }
            //添加资源文件
            files.add(EncodeUtil.md5(ApiResource + templateList.get(n).getTemplateId()) + ".0");
        }

        if (mAllCellBeanList == null) return;

        for (int i = 0; i < mAllCellBeanList.size(); i++) {
            List<CellEntity> cellList = mAllCellBeanList.get(i).getCellList();
            if (cellList != null) {
                for (int j = 0; j < cellList.size(); j++) {
                    files.add(EncodeUtil.md5(cellList.get(j).getImgUrl()) + ".0");
                }
            }
        }

        //添加logo和背景图片
        if (mImageList != null && mImageList.size() > 0) {
            for (String url : mImageList) {
                files.add(EncodeUtil.md5(url) + ".0");
            }
        }

        File rootFile = new File(iDiskCache.getSavePath());
        File[] savefiles = rootFile.listFiles();
        for (File f : savefiles) {
            String realFile = f.getName();
//                realFile = realFile.substring(0,realFile.lastIndexOf(".")-1);
            if (!files.contains(realFile)) {
                FlyLog.d("<UpdataVersion>删除多余文件：" + realFile);
                f.delete();
                DiskCache.deleteFileSafely(f);
            }
        }
    }

    /**
     * 获取已经设置的模板id对应的index
     *
     * @return
     */
    private int getDefaultTemplateIndex(TemplateBean mTemplateBean) {
        int templateId = SPUtil.getTemplate(mContext, SPUtil.TEMPLATE_ID, -1);
        if (mTemplateBean != null) {
            List<TemplateEntity> templates = mTemplateBean.getTemplate();
            if (templates != null && templates.size() > 0) {

                //如果设置了对应的模板编码
                if (!TextUtils.isEmpty(templateCode)) {
                    for (int i = 0; i < templates.size(); i++) {
                        if (templateCode.equals(templates.get(i).getTemplateCode())) {
                            FlyLog.i(" select template, use templateCode= " + templateCode);
                            return i;
                        }
                    }
                }

                //如果当前选定了模板,如果存在该模板,直接返回
                if (-1 != templateId) {
                    for (int i = 0; i < templates.size(); i++) {
                        if (templates.get(i).getTemplateId() == templateId) {
                            FlyLog.i(TAG + " select template index = " + i + "  template = " + templateId);
                            return i;
                        }
                    }
                }

                //如果当前没有选定模板,或者当前选定的模板不存在
                for (int i = 0; i < templates.size(); i++) {
                    //查找默认模板,并返回
                    if ("true".equals(templates.get(i).getIsdefault())) {
                        SPUtil.setTemplate(mContext, SPUtil.TEMPLATE_ID, templates.get(i).getTemplateId());
                        FlyLog.i(TAG + " no select template, use default index  = " + i);
                        return i;
                    }
                }
            }
        }
        FlyLog.i(TAG + " no select template, use index  = " + 0);
        return 0;//如果以上都不满足,返回第一个作为选定模板
    }

    @Override
    public boolean isUPVeriosnRunning() {
        return isUPVeriosnRunning;
    }

    /**
     * 从assets中读取文件内容
     * NOTO:文件路径和名称有约定
     *
     * @param key
     * @return
     */
    private String getAssetsFileString(String key) {
        String value = null;
        InputStream is = null;
        try {
            is = mContext.getAssets().open(ASSETS_PATH + EncodeUtil.md5(key) + ".0");
            value = FileUtil.readFile(is);
        } catch (IOException e) {
            e.printStackTrace();
            FlyLog.d(e.toString());
        } finally {
            if (is != null) {
                try {
                    is.close();
                    is = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return value;
    }

}
