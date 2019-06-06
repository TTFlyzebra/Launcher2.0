package com.flyzebra.ppfunstv.utils;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by 李冰锋 on 2016/8/1 14:32.
 * E-mail:libf@ppfuns.com
 * Package: com.flyzebra.filemanager.utils
 */
public class UsbUtil {
    public final static String TAG = UsbUtil.class.getSimpleName();

    /**
     * 获取U盘的路径
     *
     * @return U盘路径
     */
    private static List<String> getOutSDPaths() {
        List<String> stringList = new ArrayList<>();
        try {
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec("mount");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            String line;

            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                String mount = "";
                if (line.contains("secure"))
                    continue;
                if (line.contains("asec"))
                    continue;

                if (line.contains("fat")
                        ||line.contains("fuse")
                        ||line.contains("ntfs")) {
                    String columns[] = line.split(" ");
                    if (columns.length > 1) {
                        mount = mount.concat(columns[1]);
                    }
                }
                FlyLog.i("外置SD卡路径"+mount);
                if (!TextUtils.isEmpty(mount)) {
                    stringList.add("file://" + mount);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Iterator<String> iterator = stringList.iterator();
        while (iterator.hasNext()) {
            String next = iterator.next();
            if (next.startsWith("file:///mnt/sd") && !next.startsWith("file:///mnt/sdcard")) {
            } else {
                iterator.remove();
            }
        }

        return stringList;
    }

    /**
     * 判断一个路径是否是u盘的
     *
     * @param path 路径
     * @return
     */
    public static boolean isUDiskPath(String path) {
        if (path == null) {
            return false;
        }

        if (path.startsWith("file:///mnt/sd") && !path.startsWith("file:///mnt/sdcard")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断是否存在U盘
     * @return
     */
    public static boolean isExistUsb(){
        List<String> paths = getOutSDPaths();
        if(paths == null || paths.size() == 0){
            return false;
        }
        return true;
    }
}
