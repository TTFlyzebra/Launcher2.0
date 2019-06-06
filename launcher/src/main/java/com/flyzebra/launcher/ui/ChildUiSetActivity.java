package com.flyzebra.launcher.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.flyzebra.launcher.R;
import com.flyzebra.launcher.base.BaseActivity;
import com.flyzebra.launcher.utils.FlyLog;
import com.flyzebra.launcher.utils.SPUtil;
import com.flyzebra.ppfunstv.constant.Constants;
import com.umeng.analytics.MobclickAgent;

/**
 * 设置儿童版显示页面
 */

public class ChildUiSetActivity extends BaseActivity implements View.OnClickListener{
    private static final String TAG = ChildUiSetActivity.class.getSimpleName();
    private static final String GIRL = "girl";
    private static final String BOY = "boy";
    private RelativeLayout rlChild0_3;//0-3岁
    private RelativeLayout rlChild3_6;//3-6岁
    private RelativeLayout rlChild7;//>6岁
    private ImageView ivChild0;//0-3岁
    private ImageView ivChild1;//3-6岁
    private ImageView ivChild2;//>6岁
    private ImageView ivGirl;
    private ImageView ivBoy;
    private ImageView ivRadioGirl;
    private ImageView ivRadioBoy;
    private RelativeLayout rlBoy;
    private RelativeLayout rlGirl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_child_ui);
        init();
    }

    /**
     * 初始化数据
     */
    private void init(){
        rlChild0_3 = (RelativeLayout) findViewById(R.id.child_0);
        rlChild3_6 = (RelativeLayout) findViewById(R.id.child_1);
        rlChild7 = (RelativeLayout) findViewById(R.id.child_2);
        ivBoy = (ImageView) findViewById(R.id.iv_boy);
        ivGirl = (ImageView) findViewById(R.id.iv_girl);
        ivRadioGirl = (ImageView) findViewById(R.id.iv_radio_girl);
        ivRadioBoy = (ImageView) findViewById(R.id.iv_radio_boy);
        rlBoy = (RelativeLayout) findViewById(R.id.ll_boy);
        rlGirl = (RelativeLayout) findViewById(R.id.ll_girl);
        rlChild0_3.setOnClickListener(this);
        rlChild3_6.setOnClickListener(this);
        rlChild7.setOnClickListener(this);
        rlBoy.setOnClickListener(this);
        rlGirl.setOnClickListener(this);
        ivChild0 = (ImageView) findViewById(R.id.iv_child0);
        Glide.with(this).load(R.mipmap.child_ui0).into(ivChild0);
        ivChild1 = (ImageView) findViewById(R.id.iv_child1);
        Glide.with(this).load(R.mipmap.child_ui1).into(ivChild1);
        ivChild2 = (ImageView) findViewById(R.id.iv_child2);
        Glide.with(this).load(R.mipmap.child_ui2).into(ivChild2);
        Glide.with(this).load(R.mipmap.child_boy).into(ivBoy);
        Glide.with(this).load(R.mipmap.child_girl).into(ivGirl);

    }

    @Override
    protected void onResume() {
        MobclickAgent.onPageStart(TAG);
        super.onResume();
        setSexFocus();
    }

    /**
     * 设置sex选择焦点
     */
    private void setSexFocus(){
        String sex = (String) SPUtil.get(this, SPUtil.FILE_CONFIG, SPUtil.CONFIG_SEX, BOY);
        initAgeEffect();
        if(BOY.equals(sex)){
            onClick(rlBoy);
        }else{
            onClick(rlGirl);
        }
    }

    private void initAgeEffect(){
        int ageIndex = (int) SPUtil.get(this, SPUtil.FILE_CONFIG, SPUtil.CONFIG_AGE_RANGE, 0);
        switch (ageIndex){
            case 1:
                rlChild3_6.requestFocus();
                rlChild3_6.setSelected(true);
                break;
            case 2:
                rlChild7.requestFocus();
                rlChild7.setSelected(true);
                break;
            default:
                rlChild0_3.requestFocus();
                rlChild0_3.setSelected(true);
                break;
        }
    }


    @Override
    protected void onPause() {
        MobclickAgent.onPageEnd(TAG);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        FlyLog.d(TAG+" dispatchKeyEvent: action "+ event.getAction()+" keycode:"+event.getKeyCode());
        if(KeyEvent.ACTION_DOWN == event.getAction()){
            View curView = getCurrentFocus();
            switch (event.getKeyCode()){
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    if(curView != rlBoy && curView != rlGirl){
                        setSexFocus();
                    }
                    return true;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    if(curView == rlBoy || curView == rlGirl){
                        initAgeEffect();
                    }
                    return true;
                default:
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onClick(View v) {
        FlyLog.d(TAG+" onclick item");
        switch (v.getId()){
            case R.id.child_0:
                clickAction(0);
                rlChild0_3.setSelected(true);
                rlChild3_6.setSelected(false);
                rlChild7.setSelected(false);
                break;
            case R.id.child_1:
                clickAction(1);
                rlChild0_3.setSelected(false);
                rlChild3_6.setSelected(true);
                rlChild7.setSelected(false);
                break;
            case R.id.child_2:
                clickAction(2);
                rlChild0_3.setSelected(false);
                rlChild3_6.setSelected(false);
                rlChild7.setSelected(true);
                break;
            case R.id.ll_boy:
                Glide.with(this).load(R.mipmap.child_raido_on).into(ivRadioBoy);
                Glide.with(this).load(R.mipmap.child_raido_out).into(ivRadioGirl);
                SPUtil.set(this, SPUtil.FILE_CONFIG, SPUtil.CONFIG_SEX, BOY);
                rlBoy.requestFocus();
                break;
            case R.id.ll_girl:
                Glide.with(this).load(R.mipmap.child_raido_on).into(ivRadioGirl);
                Glide.with(this).load(R.mipmap.child_raido_out).into(ivRadioBoy);
                SPUtil.set(this, SPUtil.FILE_CONFIG, SPUtil.CONFIG_SEX, GIRL);
                rlGirl.requestFocus();
                break;
            default:
                break;
        }
    }

    /**
     * 执行点击动作
     * @param index 被点击view的index
     */
    private void clickAction(int index){
        FlyLog.d(TAG+" clickAction index:"+index);
        int curIndex = (int) SPUtil.get(this, SPUtil.FILE_CONFIG, SPUtil.CONFIG_AGE_RANGE, -1);
        if(curIndex != index){
            SPUtil.set(this, SPUtil.FILE_CONFIG, SPUtil.CONFIG_AGE_RANGE, index);
            Intent intent = new Intent(Constants.Action.CHILD_CHANGE_UI);
            sendBroadcast(intent);
        }else{
            FlyLog.d(TAG+" ui is the same, no need to change ui....");
            //不进行提示
//            ToastUtils.showMessage(this,R.string.tip_child_ui_is_same);
        }

        //关闭当前页面
        finish();
    }
}
