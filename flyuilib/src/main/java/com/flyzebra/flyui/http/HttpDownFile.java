package com.flyzebra.flyui.http;


import com.flyzebra.flyui.utils.FlyLog;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * 网络图片文件下载,需在线程中调用
 * Created by flyzebra on 2016/6/22.
 */
public class HttpDownFile {
    private static final int max_size = 2 * 1024 * 1024;
    private static final int CONNECT_TIME = 60000;//连接超时时间
    private static final int READ_TIME = 2000;//连接超时时间,ms
    private static final int READ_RETRY_MAX = 30;//连接超时重试次数

    /**
     * 建立HTTP请求，并获取Bitmap对象。
     *
     * @param imgUrl 图片的URL地址
     * @outputStream 保存图像的文件流
     */
    public static boolean downUrlToStream(String imgUrl, OutputStream outputStream) {
        HttpURLConnection urlConnection = null;
        BufferedOutputStream out = null;
        BufferedInputStream in = null;
        InputStream inputStream = null;
        boolean flag = false;
        try {
            final URL url = new URL(imgUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
//            urlConnection.setConnectTimeout(CONNECT_TIME);
//            urlConnection.setReadTimeout(CONNECT_TIME);
//            urlConnection.setDoInput(true);
            FlyLog.d("http response code = ", urlConnection.getResponseCode());

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();

                in = new BufferedInputStream(inputStream, max_size);
                out = new BufferedOutputStream(outputStream, max_size);

                int b;
                while ((b = in.read()) != -1) {
                    out.write(b);
                }
                out.flush();
                flag = true;
            }
        } catch (final IOException e) {
            flag = false;
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
        return flag;
    }

    /**
     * 建立HTTP请求，并获取Bitmap对象。
     *
     * @param imgUrl 图片的URL地址
     * @outputStream 保存图像的文件流
     * @speed 下载限速
     */
    public static boolean downUrlToStream(String imgUrl, OutputStream outputStream, int speed) {
        HttpURLConnection urlConnection = null;
        BufferedOutputStream out = null;
        BufferedInputStream in = null;
        InputStream inputStream = null;
        speed = speed / 10;
        boolean flag = false;
        int readRetryCount = 0;
        FlyLog.d("downloadImg: imgUrl = " + imgUrl);
        try {
            final URL url = new URL(imgUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(CONNECT_TIME);
            urlConnection.setReadTimeout(READ_TIME);
            urlConnection.setDoInput(true);
            int responseCode = urlConnection.getResponseCode();
            FlyLog.d("downloadImg: http response code = " + responseCode);
            if (responseCode == 200) {
                inputStream = urlConnection.getInputStream();

                byte[] buffer = new byte[1024 * 8];
                in = new BufferedInputStream(inputStream, max_size);
                out = new BufferedOutputStream(outputStream, max_size);

                int readlen = 0;
                int readsum = 0;
                long endTime;
                long startTime = System.currentTimeMillis();
                final long countStartTime = startTime;
                long countSum = 0;
                int c_p = 0;//用于统计标识每隔一秒打印一次下载速度
                while (true) {
                    if(readRetryCount > READ_RETRY_MAX) {
                        FlyLog.e("downloadImg: break, readRetryCount" + readRetryCount);
                        break;
                    }

                    try {
                        if ((readlen = in.read(buffer)) != -1) {
                            countSum += readlen;
                            readsum += readlen;
                            endTime = System.currentTimeMillis();
                            long useTime = endTime - startTime;
                            useTime = Math.max(useTime, 1);
                            if (useTime < 100) {
                                if (readsum > speed) {
                                    Thread.sleep(readsum * 100 / speed - useTime);
                                    FlyLog.d("downloadImg read = %d, usetime=%d, sleep = %d", readsum, useTime, readsum * 100 / speed - useTime);
                                    readsum = 0;
                                    startTime = endTime;
                                }
                            } else {
                                startTime = endTime;
                                readsum = 0;
                            }
                            out.write(buffer, 0, readlen);

                            //打印输出当前文件平均下载速度KBS
                            long tempTime = System.currentTimeMillis() - countStartTime;
                            tempTime = Math.max(tempTime, 1);
                            if (tempTime / 1000 >= c_p) {
                                FlyLog.d("downloadImg %d read = %d,usetime = %d,speed = %d Kbs", c_p, countSum, tempTime, countSum * 1000 / tempTime / 1024);
                                c_p++;
                            }

                            //FlyLog.d("downloadImg: read data continue");
                            readRetryCount = 0;
                        }else{
                            //所有数据读取成功
                            FlyLog.d("downloadImg: read data success");
                            flag = true;
                            break;
                        }
                    }catch(IOException e){
                        if (e instanceof SocketTimeoutException) {
                            FlyLog.d("downloadImg: read data timeout,readRetryCount "+readRetryCount);
                            //read超时处理
                            readRetryCount++;
                            try {
                                Thread.sleep(100);
                            }catch (Exception ee){
                                FlyLog.d("downloadImg: sleep error");
                            }
                        }else{
                            FlyLog.e("downloadImg: read data Exception");
                            e.printStackTrace();
                            FlyLog.d("Exception %s", e);
                            break;
                        }
                    }
                }

            }
            else{
                FlyLog.e("downloadImg: response is error");
            }
        } catch (Exception e) {
            e.printStackTrace();
            FlyLog.e("downloadImg: Exception %s", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (final IOException e) {
                e.printStackTrace();
                FlyLog.e("downloadImg: Exception %s", e);
            }
        }
        return flag;
    }

}
