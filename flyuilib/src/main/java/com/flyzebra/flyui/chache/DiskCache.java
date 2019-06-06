package com.flyzebra.flyui.chache;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.flyzebra.flyui.http.HttpDownFile;
import com.flyzebra.flyui.utils.EncodeUtil;
import com.flyzebra.flyui.utils.FlyLog;
import com.jakewharton.disklrucache.DiskLruCache;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.URI;


/**
 * 网络图片下载到本地磁盘缓存实现，对比本地已下载的文件，只下载未下载的图片，
 * <p/>
 * 以URL为唯一标识，用第三方库DiskLruCache作为缓存
 * Created by FlyZebra on 2016/6/21.
 */
public class DiskCache implements IDiskCache {
    private final int max_size = 100 * 1024 * 1024;
    private DiskLruCache mDiskLruCache;
    private String savePath;
    private Context context;
    private static final String DEFAULT_ASSETS_PATH = "file:///android_asset/zebra/";

    public DiskCache init(Context context) {
        return this.init(context, max_size);
    }

    public DiskCache init(Context context, int max_size) {
        this.context = context;
        return this.init(context, max_size, getSavePath());
    }

    @Override
    public DiskCache init(Context context, int size, String cachePath) {
        if (context == null) {
            this.context = context;
        }
        try {
            savePath = cachePath;
            File file = new File(savePath);
            if (!file.exists()) {
                file.mkdirs();
            }
            mDiskLruCache = DiskLruCache.open(file, getAppVersion(context), 1, max_size);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public Bitmap getBitmap(String imgUrl) {
        Bitmap bitmap = null;
        DiskLruCache.Snapshot snapShot = null;
        InputStream is = null;
        try {
            snapShot = mDiskLruCache.get(EncodeUtil.md5(imgUrl));
            if (snapShot != null) {
                is = snapShot.getInputStream(0);
                bitmap = BitmapFactory.decodeStream(is);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (snapShot != null) {
                    snapShot.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FlyLog.d("从磁盘缓存生成Bitmap对象，Bitmap=" + bitmap);
        return bitmap;
    }

    @Override
    public boolean saveBitmapFromBitmap(String url, Bitmap bitmap) {
        boolean flag = false;
        OutputStream outputStream = null;
        try {
            DiskLruCache.Editor editor = mDiskLruCache.edit(EncodeUtil.md5(url));
            if (editor == null) return false;
            outputStream = editor.newOutputStream(0);
            if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)) {
                editor.commit();
            } else {
                editor.abort();
            }
            mDiskLruCache.flush();
            flag = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FlyLog.d("保存Bitmap对像到缓存，是否成功" + flag);
        return flag;
    }


    /**
     * 为避免重复下载，考虑各种复杂的网络情况，
     * 先以临时文件名下载，下载完成后改为能正确取用的文件名
     * 成功返回true,失败返回false
     * NOTE:多线程不安全
     */
    @Override
    public synchronized boolean saveBitmapFromImgurl(String imgUrl) {
        if (checkFileExist(imgUrl)) {
            FlyLog.d("图片文件已经存在：" + imgUrl);
            return true;
        }
        FlyLog.d("开始下载图片文件：" + imgUrl);
        boolean flag = false;
        OutputStream outputStream = null;
        try {
            String key = EncodeUtil.md5(imgUrl);
            DiskLruCache.Editor editor = mDiskLruCache.edit(key);
            if (editor == null) {
                FlyLog.d("DiskLruCache 初始化Editor失败");
                return false;
            }
            outputStream = editor.newOutputStream(0);
            if (HttpDownFile.downUrlToStream(imgUrl, outputStream, 1024 * 1024)) {
                editor.commit();
            } else {
                editor.abort();
            }
            mDiskLruCache.flush();
            File file = getFile(key);
            if (file.exists()) {
                if (file.length() > 0) {
                    FlyLog.d(imgUrl + ",文件下载完成，文件名为：" + file.getAbsolutePath());
                    flag = true;
                } else {
                    FlyLog.d(imgUrl + ",文件下载失败，文件名为：" + file.getAbsolutePath());
                    boolean del = file.delete();
                    FlyLog.d("删除文件" + file.getAbsolutePath() + "---删除成功:" + del);
                    deleteFileSafely(file);
                }
            } else {
                FlyLog.d(imgUrl + " 图片文件下载失败！");
            }
        } catch (Exception e) {
            //TODO 可以添加其它异常处理，如磁盘空间不够，或不能建立文件等
            FlyLog.e(e.toString());
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                flag = false;
                e.printStackTrace();
                FlyLog.e(e.toString());
            }
        }
        return flag;
    }

    public int getAppVersion(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

    @Override
    public boolean delFileUrl(String url) {
        boolean flag = false;
        try {
            flag = mDiskLruCache.remove(EncodeUtil.md5(url));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flag;
    }

    @Override
    public boolean delFileKey(String key) {
        boolean flag = false;
        try {
            flag = mDiskLruCache.remove(EncodeUtil.md5(key));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flag;
    }


    @Override
    public String getBitmapPath(String imgUrl) {
        if (TextUtils.isEmpty(imgUrl)) {
            return "";
        }
        if (imgUrl.startsWith("file:///android_asset")) {
            return imgUrl;
        }
        String path = "file://" + savePath + File.separator + EncodeUtil.md5(imgUrl) + ".0";
        File file = new File(URI.create(path));
        if (!file.exists()) {
            path = DEFAULT_ASSETS_PATH + EncodeUtil.md5(imgUrl) + ".0";
        }
        return path;
    }

    @Override
    public String getString(String key) {
        FlyLog.d("读取磁盘缓存数据:" + key);
        String jsonStr = null;
        DiskLruCache.Snapshot snapShot = null;
        InputStream in = null;
        BufferedReader input = null;
        try {
            snapShot = mDiskLruCache.get(EncodeUtil.md5(key));
            if (snapShot != null) {
                in = snapShot.getInputStream(0);
                input = new BufferedReader(new InputStreamReader(in));
                String s;
                StringBuilder builder = new StringBuilder();
                while ((s = input.readLine()) != null) {
                    builder.append(s);
                }
                jsonStr = builder.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (input != null) {
                    input.close();
                }
                if (snapShot != null) {
                    snapShot.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FlyLog.d("读取磁盘缓存数据:" + key + ",json=" + jsonStr);
        return jsonStr;
    }

    @Override
    public boolean saveString(String str, String key) {
        if(TextUtils.isEmpty(str)) return false;
        boolean flag = false;
        OutputStream outputStream = null;
        try {
            DiskLruCache.Editor editor = mDiskLruCache.edit(EncodeUtil.md5(key));
            if (editor == null) return false;
            outputStream = editor.newOutputStream(0);
            byte[] bytes = str.getBytes("utf-8");
            outputStream.write(bytes);
            editor.commit();
            mDiskLruCache.flush();
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FlyLog.v("保存磁盘缓存数据key=" + key + ",成功=" + flag + ",jsonStr" + str);
        return flag;
    }

    @Override
    public boolean saveObj(Object object, String key) {
        boolean flag = false;
        OutputStream outputStream = null;

//        ByteArrayOutputStream bo = null;
        ObjectOutputStream objectOutputStream = null;

        try {
            DiskLruCache.Editor editor = mDiskLruCache.edit(EncodeUtil.md5(key));
            if (editor == null) return false;
            outputStream = editor.newOutputStream(0);
            objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(object);
            objectOutputStream.flush();
            editor.commit();
            mDiskLruCache.flush();
            flag = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                if (objectOutputStream != null) {
                    objectOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return flag;
    }

    @Override
    public Object getObj(String key) {
        Object obj = null;
        DiskLruCache.Snapshot snapShot = null;
        InputStream in = null;
        try {
            snapShot = mDiskLruCache.get(EncodeUtil.md5(key));
            if (snapShot != null) {
                in = snapShot.getInputStream(0);
                ObjectInputStream objectInputStream = new ObjectInputStream(in);
                obj = objectInputStream.readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (snapShot != null) {
                    snapShot.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return obj;
    }

    @Override
    public boolean checkFileExist(String imgUrl) {
        String fileName = savePath + File.separator + EncodeUtil.md5(imgUrl) + ".0";
        File file = new File(fileName);
        if (file.length() < 10) {
            return false;
        }
        return file.exists();
    }

    private File getFile(String key) {
        String fileName = savePath + File.separator + key + ".0";
        File file = new File(fileName);
        return file;
    }

    @Override
    public String getSavePath() {
        File str = context.getFilesDir();
        savePath = str.getAbsolutePath() + File.separator + "zebra";
        return savePath;
    }

    @Override
    public void release() {
        if (mDiskLruCache != null && !mDiskLruCache.isClosed()) {
            try {
                mDiskLruCache.close();
            } catch (IOException e) {
                FlyLog.d(" release diskLruCache failde" + e.toString());
                e.printStackTrace();
            }
        }
    }

    public static boolean deleteFileSafely(File file) {
        if (file != null) {
            String tmpPath = file.getParent() + File.separator + System.currentTimeMillis();
            File tmp = new File(tmpPath);
            file.renameTo(tmp);
            return tmp.delete();
        }
        return false;
    }
}
