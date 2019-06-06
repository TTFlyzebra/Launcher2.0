package com.flyzebra.launcher.ui;

import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.flyzebra.launcher.R;
import com.flyzebra.launcher.base.BaseActivity;
import com.flyzebra.launcher.utils.FlyLog;

public class VrPictureShowActivity extends BaseActivity {

    private ImageView mImageShow;
    private int type; //1 二维码 ，2 vr自制 3 云端 4终端
    private final String TYPE="type";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vr_picture_show);
        init();
    }

    public void init(){
        mImageShow=(ImageView) this.findViewById(R.id.image_show);
        Bundle extras = getIntent().getExtras();
        if (null != extras) {
            type = extras.getInt(TYPE);
        }

        switch (type){
            case 1:
                Glide.with(this).load(R.mipmap.down_load_qrcode).into(mImageShow);
                break;
            case 2:
                Glide.with(this).load(R.mipmap.ar_self).into(mImageShow);
                break;
            case 3:
                Glide.with(this).load(R.mipmap.yunduan).into(mImageShow);
                break;
            case 4:
                Glide.with(this).load(R.mipmap.zongduan).into(mImageShow);
                break;
            default:
                Glide.with(this).load(R.mipmap.down_load_qrcode).into(mImageShow);
                break;
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        FlyLog.d("dispatchKeyEvent: action "+ event.getAction()+" keycode:"+event.getKeyCode());
        return super.dispatchKeyEvent(event);
    }
}
