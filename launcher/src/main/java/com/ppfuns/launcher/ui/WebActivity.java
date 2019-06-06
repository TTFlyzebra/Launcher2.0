package com.ppfuns.launcher.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.ppfuns.launcher.R;
import com.ppfuns.launcher.base.BaseActivity;
import com.ppfuns.launcher.utils.FlyLog;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by 李宗源 on 2016/7/25.
 * E-mail:lizy@ppfuns.com
 */
public class WebActivity extends BaseActivity {

    private static final String TAG = WebActivity.class.getSimpleName();
    private WebView mWebview;
    private ProgressBar mProgressBar;
    private String mUrl;
    private final String URL = "url";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_html);
        mUrl = getIntent().getStringExtra(URL);
        init();
    }

    @Override
    protected void onResume() {
        MobclickAgent.onPageStart(TAG);
        super.onResume();
    }

    @Override
    protected void onPause() {
        MobclickAgent.onPageEnd(TAG);
        super.onPause();
    }

    /**
     * 增加监听按键监听返回键的实现，用于控制webview 返回的跳转
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebview.canGoBack()) {
            mWebview.goBack();
            return true;
        } else {
            finish();

        }
        return super.onKeyDown(keyCode, event);
    }

    protected void init() {
        mProgressBar = (ProgressBar) findViewById(R.id.web_load_progress);
        mWebview = (WebView) findViewById(R.id.webview);
        WebSettings mWebSettings = mWebview.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setBuiltInZoomControls(true);
//        mWebSettings.setLightTouchEnabled(true);
        mWebSettings.setSupportZoom(true);
        mWebview.setHapticFeedbackEnabled(false);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if(TextUtils.isEmpty(mUrl)){
                    mWebview.loadUrl("http://www.baidu.com");
                }else{
                    mWebview.loadUrl(mUrl);
                }

            }
        });

        /**此处设置 html的跳转全部都在webview里面**/
        mWebview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);   //在当前的webview中跳转到新的url
                return true;
            }
        });

        /**设置html加载进度*/
        mWebview.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, final int newProgress) {

                if(newProgress == 100){
                    mProgressBar.setVisibility(View.GONE);
                }else{
                    if(mProgressBar.getVisibility() != View.VISIBLE){
                        mProgressBar.setVisibility(View.VISIBLE);
                    }
                    /**
                     * 如果遇上连接不通的网址(www.google.com),newProgress一直为0,导致进度条的效果为不显示
                     * 现如果newProgress=0时给以其初始值2,使进度条显示
                     */
                    if(newProgress == 0){
                        mProgressBar.setProgress(2);
                    }else{
                        mProgressBar.setProgress(newProgress);
                    }
                }
                super.onProgressChanged(view, newProgress);
            }


        });
    }

    Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        FlyLog.d(TAG+" dispatchKeyEvent: action "+ event.getAction()+" keycode:"+event.getKeyCode());
        return super.dispatchKeyEvent(event);
    }
}
