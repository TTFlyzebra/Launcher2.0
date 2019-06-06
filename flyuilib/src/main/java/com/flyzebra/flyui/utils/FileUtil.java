package com.flyzebra.flyui.utils;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by lenovo on 2016/6/13.
 * 文件相关操作辅助类
 */
public class FileUtil {

    private static final String TAG = "FileUtil";
    private static final String ENCODE = "UTF-8";


    /**
     * 读取指定位置文件内容
     *
     * @param path 文件路径
     * @return 文件内容
     */
    public static String readFile(String path) {
        File file = new File(path);
        return readFile(file);
    }

    public static String readFile(InputStream is) {
        StringBuilder fileInfo = new StringBuilder();
        InputStreamReader read = null;
        BufferedReader bufferedReader = null;

        try {
            read = new InputStreamReader(is, ENCODE);
            bufferedReader = new BufferedReader(read);
            String lineTxt = null;
            while ((lineTxt = bufferedReader.readLine()) != null) {
                fileInfo.append(lineTxt);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (read != null) {
                try {
                    read.close();
                    read = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return fileInfo.toString();
    }

    /**
     * 读取指定文件内容
     *
     * @param file 指定文件
     * @return 文件内容
     */
    public static String readFile(File file) {
        StringBuilder fileInfo = new StringBuilder();
        InputStreamReader read = null;
        BufferedReader bufferedReader = null;
        try {
            if (file.isFile() && file.exists()) {
                read = new InputStreamReader(
                        new FileInputStream(file), ENCODE);
                bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    fileInfo.append(lineTxt);
                }
            } else {
                FlyLog.d(file.getPath() + " file is not exists..");
            }
        } catch (Exception e) {
            FlyLog.d("read file err:" + e.toString());
        } finally {
            if (read != null) {
                try {
                    read.close();
                    read = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return fileInfo.toString();
    }

    public static void copyFile(String source, String target) {
        FlyLog.d("copyFile source:" + source + "  target:" + target);
        File sourceFile = new File(source);
        File targetFile = new File(target);
        if (sourceFile.isDirectory()) {
            File[] files = sourceFile.listFiles();
            for (File file : files) {
                if (file.isFile()) {
                    copyFile(file, new File(target + file.getName()));//复制文件
                }
                if (file.isDirectory()) {
                    String sorceDir = source + File.separator + file.getName();
                    String targetDir = target + File.separator + file.getName();
                    copyFile(sorceDir, targetDir);
                }
            }
        } else {
            copyFile(sourceFile, targetFile);
        }
    }

    public static void copyFile(File sourcefile, File targetFile) {
        FileInputStream input = null;
        BufferedInputStream inbuff = null;
        FileOutputStream out = null;
        BufferedOutputStream outbuff = null;
        try {
            input = new FileInputStream(sourcefile);
            inbuff = new BufferedInputStream(input);
            out = new FileOutputStream(targetFile);
            outbuff = new BufferedOutputStream(out);
            byte[] b = new byte[1024 * 5];
            int len = 0;
            while ((len = inbuff.read(b)) != -1) {
                outbuff.write(b, 0, len);
            }
            outbuff.flush();
        } catch (Exception e) {
            e.printStackTrace();
            FlyLog.d("copyFile failed :" + e.toString());
        } finally {
            try {
                if (inbuff != null) {
                    inbuff.close();
                    inbuff = null;
                }
                if (outbuff != null) {
                    outbuff.close();
                    outbuff = null;
                }
                if (out != null) {
                    out.close();
                    out = null;
                }
                if (input != null) {
                    input.close();
                    input = null;
                }
            } catch (Exception e) {
            }
        }
    }


    public static void copyFile(InputStream is, String path) {
        BufferedInputStream inbuff = null;
        FileOutputStream out = null;
        BufferedOutputStream outbuff = null;
        File file = new File(path);
        try {
            inbuff = new BufferedInputStream(is);
            out = new FileOutputStream(file);
            outbuff = new BufferedOutputStream(out);
            byte[] b = new byte[1024 * 5];
            int len = 0;
            while ((len = inbuff.read(b)) != -1) {
                outbuff.write(b, 0, len);
            }
            outbuff.flush();
        } catch (Exception e) {
            e.printStackTrace();
            FlyLog.d("copyFile failed :" + e.toString());
        } finally {
            try {
                if (inbuff != null) {
                    inbuff.close();
                    inbuff = null;
                }
                if (outbuff != null) {
                    outbuff.close();
                    outbuff = null;
                }
                if (out != null) {
                    out.close();
                    out = null;
                }
            } catch (Exception e) {
            }
        }
    }


    /**
     * 复制asset文件
     *
     * @param context 上下文
     */
    public static void copyAssetFile(Context context) {
        FlyLog.d("sstart copy asset file...");
        AssetManager assets = context.getAssets();
        try {
            String basePath = context.getFilesDir().getAbsolutePath() + "/launcherData";
            String[] imagePaths = assets.list("launcherData/images");
            String imageBase = basePath + "/images/";
            File imageFile = new File(imageBase);
            if (!imageFile.exists()) {
                imageFile.mkdirs();
            }
            for (String path : imagePaths) {
                InputStream is = assets.open("launcherData/images/" + path);
                copyFile(is, imageBase + path);
            }

            String[] jsonPaths = assets.list("launcherData/json");
            String jsonBase = basePath + "/json/";
            File jsonFile = new File(jsonBase);
            if (!jsonFile.exists()) {
                jsonFile.mkdirs();
            }
            for (String path : jsonPaths) {
                InputStream is = assets.open("launcherData/json/" + path);
                copyFile(is, jsonBase + path);
            }
        } catch (IOException e) {
            e.printStackTrace();
            FlyLog.d("file not found...");
        }
    }

    public static boolean fileIsExists(String path) {
        try {
            if (path.startsWith("file:///")) {
                path = path.substring(7);
            }
            File f = new File(path);
            if (f.exists()) {
                return true;
            }
        } catch (Exception e) {

        }
        return false;
    }


}
