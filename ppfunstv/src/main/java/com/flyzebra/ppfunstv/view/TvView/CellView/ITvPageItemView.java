package com.flyzebra.ppfunstv.view.TvView.CellView;

import android.support.annotation.DrawableRes;
import android.view.View;
import android.widget.ImageView;

import com.flyzebra.ppfunstv.data.CellEntity;
import com.flyzebra.ppfunstv.module.BitmapCache;
import com.flyzebra.ppfunstv.module.UpdataVersion.IDiskCache;
import com.flyzebra.ppfunstv.view.ReflectImageView;

/**
 *
 * Created by pc1 on 2016/6/15.
 */
public interface ITvPageItemView  {
    /**
     * 显示图片
     * @param imgUrl
     */
    void showImage(String imgUrl);

    /**
     * 显示图片
     *
     * @param ResID  占位图
     */
    void setLoadImageResId(@DrawableRes int ResID);
    /**
     *
     */
    void doAction();

    /**
     * @param flag Intent启动flag
     */
    void doAction(int flag);

    /**
     * 设置与其关连的倒影控件
     */

    void bindReflectView(ReflectImageView reflectImageView);

    /**
     * 获取倒影镜像
     * @return
     */
    ReflectImageView getReflectImageView();

    /**
     * 获取显示图像的对像
     * @return
     */
    ImageView getMyImageView();

    /**
     * 设置cell数据
     *
     * @param cellEntity
     */
    View setCellData(CellEntity cellEntity);

    /**
     * 获取数据
     * @return
     */
    CellEntity getCellData();

    /**
     * @param iDiskCache 本地缓存存取接口
     */
    void setDiskCache(IDiskCache iDiskCache);

    /**
     * @param bitmapCache 用来统一缓存图片文件
     */
    void setBitmapCache(BitmapCache bitmapCache);


    /**
     * 配合监权业务，获取执行动作的包名
     */
    String getPackName();


    void setAnimtorDurtion(int durtion);


    void isUseWallPager(boolean isUseWallPager);


}
