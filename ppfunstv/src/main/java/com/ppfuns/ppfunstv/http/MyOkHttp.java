package com.ppfuns.ppfunstv.http;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;

import com.ppfuns.ppfunstv.utils.FlyLog;
import com.ppfuns.ppfunstv.utils.RegexpUtil;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.EncodedRequestBody;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.TlsVersion;

/**
 *
 * Created by FlyZebra on 2016/3/30.
 */
public class MyOkHttp implements IHttp {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    //TODO:以下请求网址将使用EncodedRequestBody编码
    public static final String URL_REGEXP = "http://192.168.*/boafrm/get_parameter";
    //初始化OkHttpClient
    private static OkHttpClient mOkHttpClient;
    private final int OK = 1;
    private final int FAIL = 2;
    public Map<Object, Set<Call>> map_Call = new HashMap<>();
    /**
     * 跟主线程通信用
     */
    private Context mContext;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    /**
     * SSL
     */
    private ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
            .tlsVersions(TlsVersion.TLS_1_2)
            .cipherSuites(
                    CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                    CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                    CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256)
            .build();

    private MyOkHttp() {
    }

    public static MyOkHttp getInstance() {
        return MyOkHttpHolder.sInstance;
    }

    /**
     * @param context
     */
    public void Init(Context context) {
        mContext = context;
    }

    public OkHttpClient getHttpClient() {
        if (mOkHttpClient == null) {
            synchronized (MyOkHttp.class) {
                OkHttpClient.Builder builder = new OkHttpClient.Builder()
                        .cache(new Cache(getDiskCacheDir("okhttp"), 50 * 1024 * 1024))
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS);
//                builder.connectionSpecs(Collections.singletonList(spec));
//                builder.sslSocketFactory(FlySSLSocketFactory.getStringCertificates().getSocketFactory());
                mOkHttpClient = builder.build();
            }
        }
        return mOkHttpClient;
    }

    @Override
    public void getString(String url, final Object tag, final HttpResult result) {
        FlyLog.d("getString:url=" + url);

        try {
            final Request request = new Request.Builder()
                    .tag(tag)
                    .url(url)
                    .build();
            Call call = getHttpClient().newCall(request);
            addCallSet(call, tag);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    removeCallSet(call, tag);
                    sendResult(result, e, FAIL);
                    FlyLog.d("getString->onFailure:e=" + e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    removeCallSet(call, tag);
                    String res = response.body().string();
                    sendResult(result, res, OK);
                    FlyLog.d("getString->onResponse:res=" + res);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getString(String url, final String headerName, final String headerValue, final Object tag, final HttpResult result) {
        FlyLog.d("getString:url=" + url);
        try {
            final Request request = new Request.Builder()
                    .tag(tag)
                    .url(url)
                    .addHeader(headerName, headerValue)
                    .build();
            Call call = getHttpClient().newCall(request);
            addCallSet(call, tag);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    removeCallSet(call, tag);
                    sendResult(result, e, FAIL);
                    FlyLog.d("getString->onFailure:e=" + e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    removeCallSet(call, tag);
                    String res = response.body().string();
                    sendResult(result, res, OK);
                    FlyLog.d("getString->onResponse:res=" + res);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void postString(String url, Map<String, String> params, final Object tag, final HttpResult result) {
        FlyLog.d("postString:url=" + url);
        try {
            RequestBody formBody;
            if(RegexpUtil.isAccept(url, URL_REGEXP)){
                EncodedRequestBody.Builder builder = new EncodedRequestBody.Builder();
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    builder.addEncoded(entry.getKey(), entry.getValue());
                }
                formBody = builder.build();
            }else{
                FormBody.Builder builder = new FormBody.Builder();
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    builder.add(entry.getKey(), entry.getValue());
                }
                formBody = builder.build();
            }
            final Request request = new Request.Builder()
                    .tag(tag)
                    .url(url)
                    .post(formBody)
                    .build();
            Call call = getHttpClient().newCall(request);
            addCallSet(call, tag);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    removeCallSet(call, tag);
                    sendResult(result, e, FAIL);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    removeCallSet(call, tag);
                    String res = response.body().string();
                    sendResult(result, res, OK);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cancelAll(Object tag) {
        Set<Call> set = map_Call.get(tag);
        if (set != null) {
            for (Iterator<Call> it = set.iterator(); it.hasNext(); ) {
                it.next().cancel();
            }
            set.clear();
        }
        mHandler.removeCallbacksAndMessages(null);
    }

    private void addCallSet(Call call, Object tag) {
        Set<Call> set = map_Call.get(tag);
        if (set == null) {
            set = new HashSet<Call>();
        }
        set.add(call);
    }

    private void removeCallSet(Call call, Object tag) {
        Set<Call> set = map_Call.get(tag);
        if (set != null) {
            set.remove(call);
        }
    }

    public File getDiskCacheDir(String uniqueName) {
        String cachePath;
        try{
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
                cachePath = mContext.getExternalCacheDir().getPath();
            } else {
                cachePath = mContext.getCacheDir().getPath();
            }
        }catch (Exception e){
            cachePath = mContext.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);
    }

    public void sendResult(final HttpResult result, final Object object, int type) {
        if (result != null) {
            switch (type) {
                case OK:
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            result.succeed(object);
                        }
                    });
                    break;
                case FAIL:
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            result.failed(object);
                        }
                    });
                    break;
                default:
            }
        }
    }

    /**
     * 使用反射读取磁盘缓存
     * 实现缓存需服务器配合HTTP缓存机制实现
     *
     * @param url
     * @return
     */
    @Override
    public String readDiskCache(String url) {
        Request request = new Request.Builder().url(url).build();
        Response response = null;
        java.lang.reflect.Method get;
        try {
            Class cls = getHttpClient().cache().getClass();
            get = cls.getDeclaredMethod("get", Request.class);
            get.setAccessible(true);
            response = (Response) get.invoke(getHttpClient().cache(), request);
        } catch (NoSuchMethodException e) {
            FlyLog.d("readListFromCache->NoSuchMethodException");
        } catch (InvocationTargetException e) {
            FlyLog.d("readListFromCache->InvocationTargetException");
        } catch (IllegalAccessException e) {
            FlyLog.d("readListFromCache->IllegalAccessException");
        }
        if (response != null) {
            try {
                return response.body().string();
            } catch (IOException e) {
            }
        }
        return null;
    }

    private static class MyOkHttpHolder {
        public static final MyOkHttp sInstance = new MyOkHttp();
    }

    /**
     * 拦截器
     */
    private class LoggingInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            long t1 = System.nanoTime();
            Response response = chain.proceed(request);
            long t2 = System.nanoTime();
            return response;
        }
    }
}

