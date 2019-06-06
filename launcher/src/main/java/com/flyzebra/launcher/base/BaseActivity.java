package com.flyzebra.launcher.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.view.View;

import com.flyzebra.launcher.R;
import com.flyzebra.launcher.view.LoadingDialog;
import com.flyzebra.ppfunstv.view.LoadAnimView.LoadAnimView;

/**
 * Created by Administrator on 2016/6/14.
 */
public class BaseActivity extends Activity {

    /**
     * 显示当前界面模糊后的效果的window
     */
    protected LoadingDialog loadingDialog;//加载动画
    protected LoadAnimView mLoadView;

    private static final String BOOT_COMPLETED = "com.flyzebra.launcher.BOOT_COMPLETED";
    protected static final String LOAD_FINISHED = "com.flyzebra.launcher.LOAD_FINISHED";
    private Handler mHander = new Handler(Looper.myLooper());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * 发送自定义开机广播
     */
    protected void sendBootBroadcast() {
        Intent intent = new Intent(BOOT_COMPLETED);
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        sendBroadcast(intent);
    }


    /**
     * 显示加载动画
     */
    public void showLoadingDialog() {
        if (mLoadView != null && mLoadView.getVisibility() == View.VISIBLE) {
            return;
        }
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog(this, getString(R.string.data_loading));
        }
        loadingDialog.show();
    }

    /**
     * 隐藏加载动画
     */
    private void hideLoadingDialogInner() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }

    public void hideLoadingDialogDelay(int millisecond) {
        mHander.postDelayed(new Runnable(){
            @Override
            public void run() {
                hideLoadingDialogInner();
            }
        },millisecond);
    }

    /**
     * 加载动画是否正在显示
     *
     * @return
     */
    public boolean isLoading() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            return true;
        }
        return false;
    }


}


