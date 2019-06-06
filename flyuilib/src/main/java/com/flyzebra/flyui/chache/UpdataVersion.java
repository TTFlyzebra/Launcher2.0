package com.flyzebra.flyui.chache;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.text.TextUtils;

import com.flyzebra.flyui.bean.CellBean;
import com.flyzebra.flyui.bean.ImageBean;
import com.flyzebra.flyui.bean.PageBean;
import com.flyzebra.flyui.bean.ThemeBean;
import com.flyzebra.flyui.bean.VersionBean;
import com.flyzebra.flyui.http.FlyOkHttp;
import com.flyzebra.flyui.http.IHttp;
import com.flyzebra.flyui.utils.EncodeUtil;
import com.flyzebra.flyui.utils.FileUtil;
import com.flyzebra.flyui.utils.FlyLog;
import com.flyzebra.flyui.utils.GsonUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 版本更新 *
 * Created by FlyZebra on 2016/6/21.
 */
public class UpdataVersion implements IUpdataVersion, IUpDataVersionError {

    private static final String ASSETS_PATH = "zebra/";
    private String themeName;
    private Context mContext;
    private IDiskCache iDiskCache;
    private UpResult upResult;
    private CheckCacheResult checkCacheResult;
    private String localVersion = "0.00";
    private List<String> mDownImageList = new ArrayList<>();
    private Set<String> files = new HashSet<>();
    private int mDownImageSum = 0;
    private AtomicInteger mAtomicImgCount = new AtomicInteger(0);
    private String mThemeJson;
    private ThemeBean mThemeBean;
    private String HTTPTAG = "UpdataVersion" + Math.random();
    private boolean isUpSuccess = true;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private static ExecutorService executor = Executors.newFixedThreadPool(1);
    private static final HandlerThread sWorkerThread = new HandlerThread("flyui-task");

    static {
        sWorkerThread.start();
    }

    private static final Handler tHandler = new Handler(sWorkerThread.getLooper());
    private boolean isUPVeriosnRunning = false;

    public static int UPDATE_IINTERVAL = 15 * 60 * 1000;//3 * 60 *
    /**
     * 前端可配置默认最小更新时间(10s)
     */
    public static int UPDATE_MIN_IINTERVAL = 10 * 1000;
    /**
     * 对应请求API接口
     */
    protected String ApiUrl = "http://192.168.1.119:801/uiweb";
    protected String token = "";
    protected String tokenFromat = "&token=%s";
    protected String ApiVersion = "/api/version?areaCode=%s&type=launcher&version=%s&versionSw=%s&devCode=%s&versionHw=%s&userId=%s";
    protected String ApiTheme = "/api/app?areaCode=%s&type=launcher&version=%s&versionSw=%s&devCode=%s&versionHw=%s&userId=%s";
    protected String VERSION_KEY = "/api/version";
    protected String TEMPLATE_KEY = "/api/app";

    public UpdataVersion(Context context) {
        this.mContext = context;
        iDiskCache = new DiskCache().init(context);
    }

    public UpdataVersion(Context context, IDiskCache iDiskCache) {
        this.mContext = context;
        this.iDiskCache = iDiskCache;
    }


    @Override
    public void initApi(String apiUrl, String apiThemeFormat, String type, String themeName, String version, String token) {
        ApiUrl = apiUrl;
        this.themeName = themeName;
        ApiTheme = String.format(Locale.CHINESE, apiThemeFormat, type, themeName, version);
        this.token = token;
    }


    private boolean checkUrl() {
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
                mThemeBean = getThemeBean();
                if (mThemeBean == null) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (checkCacheResult != null) {
                                checkCacheResult.getCacheDataFaile(UP_ASSETS_ERROR);
                            }
                        }
                    });
                } else {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (checkCacheResult != null)
                                checkCacheResult.getCacheDataOK(mThemeBean);
                        }
                    });
                }
            }
        });
    }

    private ThemeBean getThemeBean() {
        String mThemeBeanJson = iDiskCache.getString(themeName+TEMPLATE_KEY);
        if (TextUtils.isEmpty(mThemeBeanJson)) {
            mThemeBeanJson = getAssetsFileString(themeName+TEMPLATE_KEY);
            if (TextUtils.isEmpty(mThemeBeanJson)) {
                SAVE_PATH = "file://" + iDiskCache.getSavePath();
            } else {
                SAVE_PATH = "file:///android_asset/zebra";
            }
        } else {
            SAVE_PATH = "file://" + iDiskCache.getSavePath();
        }
        mThemeBean = GsonUtil.json2Object(mThemeBeanJson, ThemeBean.class);
        return mThemeBean;
    }


    @Override
    public void startUpVersion(UpResult upResult) {
        FlyLog.d("startUpVersion");
        if (!checkUrl()) {
            FlyLog.e("invalid url! url=%s", ApiUrl);
            return;
        }
        this.upResult = upResult;
        if (!isUPVeriosnRunning) {
            isUPVeriosnRunning = true;
            isUpSuccess = true;
            checkVersion(ApiUrl + ApiVersion + String.format(tokenFromat, token));
        }
    }

    @Override
    public void forceUpVersion(UpResult upResult) {
        FlyLog.d("forceUpVersion");
        FlyOkHttp.getInstance().cancelAll(HTTPTAG);
        tHandler.removeCallbacksAndMessages(null);
        mHandler.removeCallbacksAndMessages(null);
        isUPVeriosnRunning = false;

        if (!checkUrl()) {
            FlyLog.e("invalid url! url=%s", ApiUrl);
            return;
        }

        this.upResult = upResult;
        mHandler.removeCallbacksAndMessages(null);
        if (!isUPVeriosnRunning) {
            isUPVeriosnRunning = true;
            isUpSuccess = true;
            getThemeBean(ApiUrl + ApiTheme + String.format(tokenFromat, token));
        }
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
                String version = iDiskCache.getString(themeName+VERSION_KEY);
                if (version != null) {
                    VersionBean bean = GsonUtil.json2Object(version, VersionBean.class);
                    if (bean != null) {
                        localVersion = bean.version;
                        if (bean.versionInterval > UPDATE_MIN_IINTERVAL) {
                            UPDATE_IINTERVAL = bean.versionInterval * 1000;
                        }
                    }
                }
                String newVersion = null;
                if (object != null) {
                    VersionBean bean = GsonUtil.json2Object(object.toString(), VersionBean.class);
                    if (bean != null && bean.isValid()) {
                        newVersion = bean.version;
                        if (bean.versionInterval > UPDATE_MIN_IINTERVAL) {
                            UPDATE_IINTERVAL = bean.versionInterval * 1000;
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

                    getThemeBean(ApiUrl + ApiTheme + String.format(tokenFromat, token));
                } else {
                    FlyLog.d(" no new version!!! no need to update version.....");
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

    private void getThemeBean(String urlAPI) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (upResult != null)
                    upResult.upVesionProgress("Get json text...", 1, 0);
            }
        });

        FlyOkHttp.getInstance().getString(urlAPI, HTTPTAG, new IHttp.HttpResult() {
            @Override
            public void succeed(Object object) {
                mThemeJson = object.toString();
                ThemeBean bean = GsonUtil.json2Object(mThemeJson, ThemeBean.class);
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
                    mThemeBean = bean;
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (upResult != null)
                                upResult.upVesionProgress("Get image...", 1, 1);
                        }
                    });
                    getAllDownImage(mThemeBean);
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
     * 获取所有资源信息
     *
     * @param themeBean
     */
    private void getAllDownImage(final ThemeBean themeBean) {
        FlyLog.d("getAllDownImage");
        final List<PageBean> pageList = themeBean.pageList;
        mDownImageList.clear();
        addDownImageUrl(mThemeBean.imageurl);
        //TopPage
        if (mThemeBean.topPage != null && mThemeBean.topPage.cellList != null) {
            for (CellBean cellBean : mThemeBean.topPage.cellList) {
                addCellImageUrls(cellBean);
            }
        }
        //AllPage
        if (pageList != null) {
            for (PageBean pageBean : pageList) {
                addDownImageUrl(pageBean.imageurl);
                for (CellBean cellBean : pageBean.cellList) {
                    addCellImageUrls(cellBean);
                }
            }
        }
        downloadAllImage(mDownImageList);
    }

    private void addCellImageUrls(CellBean cellBean) {
        if (cellBean.images != null && cellBean.images.size() > 0) {
            for (ImageBean imageBean : cellBean.images) {
                addDownImageUrl(imageBean.url);
            }
        }
        if (cellBean.subCells != null) {
            for (CellBean subCell : cellBean.subCells) {
                addCellImageUrls(subCell);
            }
        }

        if(cellBean.pages!=null) {
            for(PageBean pageBean:cellBean.pages){
                for (CellBean pageCellBean : pageBean.cellList) {
                    addCellImageUrls(pageCellBean);
                }
            }

         }
    }

    private void addSaveFileNames(CellBean cellBean) {
        if (cellBean.images != null && cellBean.images.size() > 0) {
            for (ImageBean imageBean : cellBean.images) {
                files.add(EncodeUtil.md5(imageBean.url) + ".0");
            }
        }
        if (cellBean.subCells != null) {
            for (CellBean subCell : cellBean.subCells) {
                addSaveFileNames(subCell);
            }
        }

        if(cellBean.pages!=null) {
            for(PageBean pageBean:cellBean.pages){
                for (CellBean pageCellBean : pageBean.cellList) {
                    addSaveFileNames(pageCellBean);
                }
            }

        }
    }

    private void addDownImageUrl(String imageurl) {
        if (!TextUtils.isEmpty(imageurl)) {
            mDownImageList.add(imageurl);
        }
    }

    private void downloadAllImage(List<String> imageList) {
        if (imageList == null || imageList.size() == 0) {
            upVersion();
            return;
        }

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (upResult != null)
                    upResult.upVesionProgress("down image...", mAtomicImgCount.get(), 0);
            }
        });
        mAtomicImgCount.set(imageList.size());
        mDownImageSum = imageList.size();
        for (final String imagurl : imageList) {
            tHandler.post(new Runnable() {
                @Override
                public void run() {
                    final boolean flag = iDiskCache.saveBitmapFromImgurl(imagurl);
                    if (!flag) {
                        isUpSuccess = false;
                    }
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
                                            upResult.upVesionProgress("tv_up_version_ok", mDownImageSum, mDownImageSum - mAtomicImgCount.get());
                                        } else {
                                            upResult.upVesionProgress("tv_up_version_image_ok", mDownImageSum, mDownImageSum - mAtomicImgCount.get());
                                        }
                                } else {
                                    if (upResult != null)
                                        upResult.upVesionProgress(imagurl + "tv_up_version_image_faile", mDownImageSum, mDownImageSum - mAtomicImgCount.get());
                                }
                            }
                        });
                    }
                }
            });
        }
    }


    private void upVersion() {
        SAVE_PATH = "file://" + iDiskCache.getSavePath();
        if (!isUpSuccess) {
            FlyLog.d("upVersion Failed!");
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (upResult != null) upResult.upVersionFaile(UP_FILE_FAILD);
                }
            });
        } else {
            iDiskCache.saveString(localVersion, themeName+VERSION_KEY);
            iDiskCache.saveString(mThemeJson, themeName+TEMPLATE_KEY);
            //保存资源json文件
            FlyLog.d("upVersion OK!");
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    FlyLog.d("upVersion OK!");
                    if (upResult != null)
                        upResult.upVersionOK(mThemeBean);
                }
            });
//            DelNoUseFiles();
            /**
             * 回收资源
             */
            localVersion = null;
            mThemeJson = null;
            mDownImageList.clear();
        }

        //重置升级状态
        isUPVeriosnRunning = false;
        isUpSuccess = true;
    }

    public void cancelAllTasks() {
        FlyOkHttp.getInstance().cancelAll(HTTPTAG);
        tHandler.removeCallbacksAndMessages(null);
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
        FlyLog.d("start delete no use file");
        files.clear();
        files.add("journal");
        if (mThemeBean == null) return;
        files.add(EncodeUtil.md5(VERSION_KEY) + ".0");
        files.add(EncodeUtil.md5(TEMPLATE_KEY) + ".0");
        if (mThemeBean.pageList == null) return;
        files.add(EncodeUtil.md5(mThemeBean.imageurl) + ".0");
        //TOPPAGE
        if (mThemeBean.topPage != null && mThemeBean.topPage.cellList != null) {
            for (CellBean cellBean : mThemeBean.topPage.cellList) {
                addSaveFileNames(cellBean);
            }
        }
        //PAGE
        for (PageBean pageBean : mThemeBean.pageList) {
            files.add(EncodeUtil.md5(pageBean.imageurl) + ".0");
            for (CellBean cellBean : pageBean.cellList) {
                addSaveFileNames(cellBean);
            }
        }

        File rootFile = new File(iDiskCache.getSavePath());
        File[] savefiles = rootFile.listFiles();
        for (File f : savefiles) {
            String realFile = f.getName();
//                realFile = realFile.substring(0,realFile.lastIndexOf(".")-1);
            if (!files.contains(realFile)) {
                FlyLog.d("del file：" + realFile);
                if (!f.delete()) {
                    FlyLog.e("Delete file failed! file=%s", f.getAbsolutePath());
                }
                DiskCache.deleteFileSafely(f);
            }
        }
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
            FlyLog.e("getAssets failed!");
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return value;
    }

    private static String SAVE_PATH = "file:///android_asset/zebra";

    public static String getNativeFilePath(String imgUrl) {
        return SAVE_PATH + File.separator + EncodeUtil.md5(imgUrl) + ".0";
    }

}
