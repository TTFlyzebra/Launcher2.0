package com.flyzebra.ppfunstv.utils;

import android.content.Context;
import android.media.AudioManager;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * Created by flyzebra on 16-12-9.
 */
public class SystemUtils {
    public static float getProcessCpuRate(long time) {

        float totalCpuTime1 = getTotalCpuTime();
        float processCpuTime1 = getAppCpuTime();
        try {
            Thread.sleep(time);
        } catch (Exception e) {
            FlyLog.d(e.toString());
        }

        float totalCpuTime2 = getTotalCpuTime();
        float processCpuTime2 = getAppCpuTime();

        float cpuRate = 100 * (processCpuTime2 - processCpuTime1) / (totalCpuTime2 - totalCpuTime1);

        return cpuRate;
    }

    public static long getTotalCpuTime() { // 获取系统总CPU使用时间
        String[] cpuInfos = null;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream("/proc/stat")), 1000);
            String load = reader.readLine();
            reader.close();
            cpuInfos = load.split(" ");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        long totalCpu = Long.parseLong(cpuInfos[2])
                + Long.parseLong(cpuInfos[3]) + Long.parseLong(cpuInfos[4])
                + Long.parseLong(cpuInfos[6]) + Long.parseLong(cpuInfos[5])
                + Long.parseLong(cpuInfos[7]) + Long.parseLong(cpuInfos[8]);
        return totalCpu;
    }

    public static long getAppCpuTime() { // 获取应用占用的CPU时间
        String[] cpuInfos = null;
        try {
            int pid = android.os.Process.myPid();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream("/proc/" + pid + "/stat")), 1000);
            String load = reader.readLine();
            reader.close();
            cpuInfos = load.split(" ");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        long appCpuTime = Long.parseLong(cpuInfos[13])
                + Long.parseLong(cpuInfos[14]) + Long.parseLong(cpuInfos[15])
                + Long.parseLong(cpuInfos[16]);
        return appCpuTime;
    }

    public static void setSystemMute(Context context, boolean bMute){
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        am.setStreamMute(AudioManager.STREAM_VOICE_CALL,bMute);
        am.setStreamMute(AudioManager.STREAM_SYSTEM,bMute);
        am.setStreamMute(AudioManager.STREAM_RING,bMute);
        am.setStreamMute(AudioManager.STREAM_MUSIC,bMute);
        am.setStreamMute(AudioManager.STREAM_ALARM,bMute);
    }
}
