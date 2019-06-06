package com.flyzebra.ppfunstv.view.TvView.CellView.AdsModule;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.flyzebra.flyui.chache.DiskCache;
import com.flyzebra.flyui.chache.IDiskCache;
import com.flyzebra.ppfunstv.constant.Constants;
import com.flyzebra.ppfunstv.data.AdsEntity;
import com.flyzebra.ppfunstv.module.ads.AdsClient;
import com.flyzebra.ppfunstv.module.ads.AdsInfo;
import com.flyzebra.ppfunstv.module.ads.DataEmptyException;
import com.flyzebra.ppfunstv.module.ads.ServiceNotExistException;
import com.flyzebra.ppfunstv.utils.FlyLog;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * Created by lenovo on 2016/6/29.
 */
public class AdsModule implements IAdsModule {
    private final static String TAG = AdsModule.class.getSimpleName();
    private static final String ADS_UPDATE_ACTION = "com.flyzebra.ads.update";

    private static final String KEY_ADSBEAN = "key_adsbean";
    private static final String KEY_ADSFILE_KEYSET = "key_adsfile_keyset";
    private static final String ADS_CACHE_PATH = "/ads_cache";
    private static final int MSG_UPDATE = 1;
    private static AdsModule module;
    private IDiskCache mDiskCache;
    private Hashtable<Integer, AdsEntity> mAdsEntityMap;
    private BroadcastReceiver mBroadcastReceiver;
    private Set<Integer> mAdsTypes;
    private ArrayList<BaseAdsUpdateListener> mAdsUpdateListenerArrayList;
    private Handler mHandler = new Handler(Looper.myLooper()) {
        @Override
        public void dispatchMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATE: {
                    for (BaseAdsUpdateListener listener : mAdsUpdateListenerArrayList) {
                        if (listener.type == msg.arg1 || -1 == msg.arg1) {
                            listener.onAdsUpdate();
                        }
                    }
                    break;
                }
            }
            super.dispatchMessage(msg);
        }
    };

    private AdsModule() {
        mAdsUpdateListenerArrayList = new ArrayList<>();
        mAdsEntityMap = new Hashtable<>();
    }

    public static IAdsModule getInstance() {
        if (module == null) {
            module = new AdsModule();
        }
        return module;
    }

    @Override
    public void loadAdsData(Context context, Set<Integer> adsTypes) {
        if (mAdsTypes == null) {
            mAdsTypes = adsTypes;
        } else {
            mAdsTypes.clear();
            mAdsTypes.addAll(adsTypes);
        }
        new MakeData(-1).execute(context);
    }

    /**
     * 刷新广告数据
     */
    @Override
    public void updateAdsData(Context context, int type) {
        if (mAdsTypes != null) {
            for (Integer adsType : mAdsTypes) {
                if (type == -1 || adsType == type) {
                    new MakeData(type).execute(context);
                    break;
                }
            }
        }

    }

    @Override
    public int getAdsImgColor() {
        // TODO: 2016/7/20 获取广告图片的采样颜色
        return 0;
    }

    @Override
    public boolean getAdsFocusable(int type) {
        AdsEntity adsEntity = mAdsEntityMap.get(type);
        if (adsEntity != null && adsEntity.isAvaliable()) {
            return adsEntity.mAdsBeanArrayList.get(0).focusable;
        }
        return false;
    }

    @Override
    public void registerReceiver(Context context) {
        IntentFilter intentFilter = new IntentFilter(ADS_UPDATE_ACTION);
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int type = intent.getIntExtra("type", -1);
                FlyLog.d("onReceive: 广告服务更新le, ads type :" + type);
                updateAdsData(context.getApplicationContext(), type);
            }
        };
        context.registerReceiver(mBroadcastReceiver, intentFilter);
    }

    @Override
    public void unRegisterReceiver(Context context) {
        if (mBroadcastReceiver != null) {
            context.unregisterReceiver(mBroadcastReceiver);
        }
    }

    @Override
    public void addOnAdsUpdateListener(BaseAdsUpdateListener onAdsUpdateListener) {
        if (!mAdsUpdateListenerArrayList.contains(onAdsUpdateListener)) {
            mAdsUpdateListenerArrayList.add(onAdsUpdateListener);
        }
    }

    @Override
    public void removeOnUpdateListener(BaseAdsUpdateListener onAdsUpdateListener) {
        mAdsUpdateListenerArrayList.remove(onAdsUpdateListener);
    }

    @Override
    public void removeAllListenter() {
        mAdsUpdateListenerArrayList.clear();
    }

    @Override
    public String getIntentInfo(int adsType) {
        AdsEntity adsEntity = mAdsEntityMap.get(adsType);
        AdsEntity.AdsBean beanById;

        if (adsEntity != null && adsEntity.isAvaliable()) {
            beanById = adsEntity.mAdsBeanArrayList.get(0);
            if (beanById != null) {
                return beanById.strIntent;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public List<String> getFileNames(int adsType) {
        AdsEntity adsEntity = mAdsEntityMap.get(adsType);
        if (adsEntity != null && adsEntity.isAvaliable()) {
            return adsEntity.mFileNames;
        } else {
            return null;
        }
    }

    @Override
    public Bitmap getAdsImg(int adsType) {
            AdsEntity adsEntity = mAdsEntityMap.get(adsType);
            AdsEntity.AdsFile adsFile;

            if (adsEntity != null && adsEntity.isAvaliable()) {
                adsFile = adsEntity.mAdsFileArrayList.get(0);
                if (adsFile != null) {
                    return adsFile.adsImg;
                } else {
                    return null;
                }
            } else {
                return null;
            }
    }

    /**
     * 将从广告服务获取的数据，进行封装
     *
     * @param adsInfoList 从广告服务获取回来的数据
     * @param context     上下文
     * @return 封装了的广告数据
     */
    private AdsEntity makeAdsEntity(List<AdsInfo> adsInfoList, Context context) {
        if (adsInfoList == null) {
            return null;
        }

        ArrayList<AdsEntity.AdsBean> adsBeanList = new ArrayList<>();
        ArrayList<AdsEntity.AdsFile> adsFileList = new ArrayList<>();
        ArrayList<String> fileNames = new ArrayList<>();

        for (AdsInfo info : adsInfoList) {
            AdsEntity.AdsBean adsBean = new AdsEntity.AdsBean();
            AdsEntity.AdsFile adsFile = new AdsEntity.AdsFile();

            adsBean.adId = info.getAdId();
            adsBean.strIntent = info.getThirdurl();
            // TODO: 2016/8/5 是否可或焦
            adsBean.focusable = !TextUtils.isEmpty(info.getThirdurl());
            if (!TextUtils.isEmpty(info.getAdfileurl())) {
                String[] items = info.getAdfileurl().split("/");
                fileNames.add(Constants.ADS_FILE_PATH + items[items.length - 1]);
            } else {
                fileNames.add("null");
            }
            try {
                InputStream inputStream = AdsClient.getADfile(context, info.getAdfileurl());
                FlyLog.d("inputStream: " + inputStream);
                adsFile.adsImg = BitmapFactory.decodeStream(inputStream);
                if (adsFile.adsImg == null) {
                    FlyLog.d("adsFile.adsImg: null");
                }
                //文件缓存的key为其id
                adsFile.cacheKey = "" + info.getAdId();
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            adsBeanList.add(adsBean);
            adsFileList.add(adsFile);
        }

        return new AdsEntity(adsBeanList, adsFileList, fileNames);
    }

    /**
     * 缓存广告数据
     *
     * @param adsEntity 被缓存的广告数据
     * @param context   上下文
     * @param adsType   广告类型
     */
    private void cacheAdsEntity(AdsEntity adsEntity, Context context, int adsType) {
        createCacheIfNotExist(context);
        /**
         * 缓存数据
         */
        mDiskCache.saveObj(adsEntity.mAdsBeanArrayList, adsType + KEY_ADSBEAN);

        /**
         * 缓存文件
         * 文件的key/文件本身,需单独缓存
         */
        String fileCacheKeySet = null;
        for (AdsEntity.AdsFile file : adsEntity.mAdsFileArrayList) {
            if (fileCacheKeySet != null) {
                //缓存的文件的key拼接而成的字符串,各key之间,以"#"分割
                fileCacheKeySet = "#" + file.cacheKey;
            } else {
                fileCacheKeySet = file.cacheKey;
            }
            if (file.cacheKey != null && file.adsImg != null) {
                mDiskCache.saveBitmapFromBitmap(file.cacheKey, file.adsImg);
            }
        }
        mDiskCache.saveString(fileCacheKeySet, adsType + KEY_ADSFILE_KEYSET);
    }

    /**
     * 删除原有广告缓存
     *
     * @param context 上下文
     * @param adsType 需要删除的广告的类型
     * @return true删除成功，false删除失败
     */
    private boolean removeOldAdsCache(Context context, int adsType) {
        boolean result;

        createCacheIfNotExist(context);


        String keySet = mDiskCache.getString(adsType + KEY_ADSFILE_KEYSET);
        result = mDiskCache.delFileKey(adsType + KEY_ADSFILE_KEYSET);
        if (keySet != null) {
            for (String key : keySet.split("#")) {
                result = mDiskCache.delFileUrl(key);
            }
        }

        return result;
    }

    /**
     * 读取缓存，获取缓存广告的数据
     *
     * @param context 上下文
     * @param adsType 广告类型
     * @return 广告数据
     */
    @Deprecated
    private AdsEntity getCachedAdsEntity(Context context, int adsType) {
        createCacheIfNotExist(context);

        ArrayList<AdsEntity.AdsBean> adsBeanList = (ArrayList<AdsEntity.AdsBean>) mDiskCache.getObj(KEY_ADSBEAN);
        ArrayList<AdsEntity.AdsFile> adsFileList = new ArrayList<>();
        ArrayList<String> fileNames = new ArrayList<>();

        String keySet = mDiskCache.getString(adsType + KEY_ADSFILE_KEYSET);
        if (keySet != null) {
            for (String key : keySet.split("#")) {
                AdsEntity.AdsFile adsFile = new AdsEntity.AdsFile();
                adsFile.cacheKey = key;
                adsFile.adsImg = mDiskCache.getBitmap(key);
                adsFileList.add(adsFile);
            }
        }

        return new AdsEntity(adsBeanList, adsFileList, fileNames);
    }

    /**
     * 缓存不存在时，创建disk缓存
     *
     * @param context 上下文
     */
    private void createCacheIfNotExist(Context context) {
        if (mDiskCache == null) {
            mDiskCache = new DiskCache().init(context, 10 * 1024 * 1024, context.getCacheDir() + ADS_CACHE_PATH);
        }
    }

    public class MakeData extends AsyncTask<Context, String, String> {
        private int type = -1;

        public MakeData(int type) {
            this.type = type;
        }

        @Override
        protected synchronized String doInBackground(Context... params) {
                FlyLog.d("update ads type :" + type);
                Map tmp = new Hashtable<Integer,AdsEntity>(mAdsEntityMap);
                try {
                    if (-1 != type) {//缓存单个广告
                        for (Integer adsType : mAdsTypes) {
                            if (adsType == type) { //需要缓存
                                if (mAdsEntityMap.containsKey(type)) {
                                    mAdsEntityMap.remove(type); //删除旧的
                                }
                                List<AdsInfo> adsInfoList = AdsClient.getADinfo(params[0], type);
                                AdsEntity adsEntity = makeAdsEntity(adsInfoList, params[0]);
                                mAdsEntityMap.put(type, adsEntity);
                                FlyLog.d("AdsCellView: 缓存了单个广告" + type);
                                break;
                            }
                        }
                    } else {
                        mAdsEntityMap.clear();
                        for (Integer adsType : mAdsTypes) {
                            try {
                                List<AdsInfo> adsInfoList = AdsClient.getADinfo(params[0], adsType);
                                AdsEntity adsEntity = makeAdsEntity(adsInfoList, params[0]);
                                mAdsEntityMap.put(adsType, adsEntity);
                                FlyLog.d("AdsCellView: 缓存了广告" + adsType);
                            } catch (ServiceNotExistException e) {
                                FlyLog.d("updateAdsData: ServiceNotExistException 获取广告更新数据错误" + e.toString());
                                mAdsEntityMap.putAll(tmp);
                            } catch (DataEmptyException e) {
                                FlyLog.d("updateAdsData:DataEmptyException  获取广告更新数据错误" + e.toString());
                            }
                        }
                    }
                } catch (Exception e) {
                    FlyLog.e(e.toString());
                    e.printStackTrace();
                }
                Message msg = new Message();
                msg.what = MSG_UPDATE;
                msg.arg1 = type;
                mHandler.sendMessage(msg);
            return null;
        }
    }
}
