package com.flyzebra.ppfunstv.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.flyzebra.ppfunstv.constant.Constants;
import com.google.gson.JsonObject;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.HashMap;
import java.util.Map;

/**
 * 二维码生成工具类
 */
public class QRCodeUtil {

    private static final String PARA_DIV = "|";
    private static final String KEY_NAME = "friendlyName";
    private static final String KEY_CA = "ca";

    private static Map<String, Bitmap> mQrImage = new HashMap<>();

    /**
     * 生成二维码Bitmap
     *
     * @param content   内容
     * @param widthPix  图片宽度
     * @param heightPix 图片高度
     * @param logoBm    二维码中心的Logo图标（可以为null）
     * @return 生成二维码及保存文件是否成功
     */
    public static Bitmap createQRImage(String content, int widthPix, int heightPix, Bitmap logoBm) {
        try {
            if (content == null || "".equals(content)) {
                return null;
            }
            if (mQrImage.containsKey(content)) {
                FlyLog.d("get QrImage from local..." + content);
                return mQrImage.get(content);
            }

            //配置参数
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            //容错级别
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            //设置空白边距的宽度
            hints.put(EncodeHintType.MARGIN, 0); //default is 4

            // 图像数据转换，使用了矩阵转换
            BitMatrix bitMatrix = new QRCodeWriter().encode(content, widthPix, heightPix, hints);

            int pixW = bitMatrix.getWidth();
            int pixH = bitMatrix.getHeight();
            int[] pixels = new int[pixW * pixH];
            // 下面这里按照二维码的算法，逐个生成二维码的图片，
            // 两个for循环是图片横列扫描的结果
            for (int y = 0; y < pixH; y++) {
                for (int x = 0; x < pixW; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * pixW + x] = 0xff000000;
                    } else {
                        pixels[y * pixW + x] = 0xffffffff;
                    }
                }
            }

            // 生成二维码图片的格式，使用ARGB_8888
            Bitmap bitmap = Bitmap.createBitmap(pixW, pixH, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, pixW, 0, 0, pixW, pixH);


            if (logoBm != null) {
                bitmap = addLogo(bitmap, logoBm);
            }

            //必须使用compress方法将bitmap保存到文件中再进行读取。直接返回的bitmap是没有任何压缩的，内存消耗巨大！
//            return bitmap != null && bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(filePath));
            mQrImage.put(content, bitmap);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 在二维码中间添加Logo图案
     */
    private static Bitmap addLogo(Bitmap src, Bitmap logo) {
        if (src == null) {
            return null;
        }

        if (logo == null) {
            return src;
        }

        //获取图片的宽高
        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();
        int logoWidth = logo.getWidth();
        int logoHeight = logo.getHeight();

        if (srcWidth == 0 || srcHeight == 0) {
            return null;
        }

        if (logoWidth == 0 || logoHeight == 0) {
            return src;
        }

        //logo大小为二维码整体大小的1/4
        float scaleFactor = srcWidth * 1.0f / 4 / logoWidth;
        Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
        try {
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(src, 0, 0, null);
            canvas.scale(scaleFactor, scaleFactor, srcWidth / 2, srcHeight / 2);
            canvas.drawBitmap(logo, (srcWidth - logoWidth) / 2, (srcHeight - logoHeight) / 2, null);

            canvas.save();
            canvas.restore();
        } catch (Exception e) {
            bitmap = null;
            e.getStackTrace();
        }

        return bitmap;
    }

    /**
     * 生成二维码控件内容,格式name(盒子名称_mac后六位)|sn
     *
     * @return
     */
    public static String createContent(Context context) {
        JsonObject object = new JsonObject();
        String sn = Utils.getSn(context, "sn_default");
        String udn = SystemPropertiesProxy.get(context, Constants.Property.FRIENDLY_NAME, "udn_default");
        if ("udn_default".equals(udn)) {
            udn = (String) SPUtil.get(context, SPUtil.FILE_CONFIG, SPUtil.CONFIG_UDN_NAME, "udn_default");
        }
        object.addProperty(KEY_NAME, udn);
        object.addProperty(KEY_CA, sn);

        return udn + PARA_DIV + sn;
//        return object.toString();
    }

    /**
     * 生成二维码控件内容,格式name(盒子名称_mac后六位)|sn
     * 如果content不为空,生成格式name(盒子名称_mac后六位)|sn|content
     *
     * @return
     */
    public static String createContent(Context context, String content) {
        if (content != null && !content.startsWith(PARA_DIV)) {
            content += PARA_DIV;
        } else if (content == null) {
            content = "";
        }
        return createContent(context) + content;
    }

}