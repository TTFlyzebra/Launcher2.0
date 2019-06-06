package com.flyzebra.launcher.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.flyzebra.launcher.R;
import com.flyzebra.launcher.base.BaseActivity;
import com.flyzebra.ppfunstv.utils.QRCodeUtil;

/**
 * Created by lizongyuan on 2016/12/22.
 * E-mail:lizy@ppfuns.com
 */

public class DownloadQRActivity extends BaseActivity {

    Context mContext;
    ImageView ivAndorid;
    ImageView ivIos;
    private String androidDownUrl = "http://120.27.6.14:8060/ftp/jshy/jsj/er_wei_ma.html";
    private String iosDownUrl = "http://120.27.6.14:8060/ftp/jshy/jsj/er_wei_ma.html";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_qr);
        mContext = this;
        initView();
    }

    private void initView() {
        ivAndorid = (ImageView) findViewById(R.id.iv_android);
        ivIos = (ImageView) findViewById(R.id.iv_ios);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initData() {
        int width = (int) getResources().getDimension(R.dimen.DIMEN_418PX);
        Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.tv_qrcode_smi);
        Bitmap android = QRCodeUtil.createQRImage(androidDownUrl,width,width,logo);
        ivAndorid.setImageBitmap(android);
//        Bitmap ios =  QRCodeUtil.createQRImage(iosDownUrl,width,width,logo);
//        ivIos.setImageBitmap(ios);
        Glide.with(mContext).load(R.drawable.under_develop).into(ivIos);
    }
}
