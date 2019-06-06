package com.ppfuns.launcher.ui;

import android.os.Bundle;
import android.view.KeyEvent;

import com.ppfuns.launcher.R;
import com.ppfuns.launcher.base.BaseActivity;
import com.ppfuns.launcher.utils.FlyLog;
import com.ppfuns.launcher.view.Play3DView;


/**
 * by FlyZebra on 2016/3/27.
 */
public class WelcomeActivity extends BaseActivity {

    private static final String TAG = WelcomeActivity.class.getSimpleName();

    private Play3DView welPlay3d;
    private String[] urlArray = {"http://192.168.1.12/fly/Uploads/launcher/image/2016-07-14/57872e2a9fdd3.png",
            "http://192.168.1.12/fly/Uploads/launcher/image/2016-07-14/57872e2a9fdd3.png",
            "http://192.168.1.12/fly/Uploads/launcher/image/2016-07-14/57872e2a9fdd3.png",
            "http://192.168.1.12/fly/Uploads/launcher/image/2016-07-14/57872e2a9fdd3.png",
            "http://192.168.1.12/fly/Uploads/launcher/image/2016-07-14/57872e2a9fdd3.png",
            "http://192.168.1.12/fly/Uploads/launcher/image/2016-07-14/57872e2a9fdd3.png",
            "http://192.168.1.12/fly/Uploads/launcher/image/2016-07-14/57872e2a9fdd3.png"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        welPlay3d = (Play3DView) findViewById(R.id.ac_wel_play3d);
        welPlay3d.setDuration(1000)
                .setShowMillis(2000)
                .setImageAlpha(0.95f);
        welPlay3d.setImageUrlArray(urlArray).Init();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        finish();
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        FlyLog.d(TAG+" dispatchKeyEvent: action "+ event.getAction()+" keycode:"+event.getKeyCode());
        return super.dispatchKeyEvent(event);
    }
}
