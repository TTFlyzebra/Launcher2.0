package com.flyzebra.launcher.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.flyzebra.flyui.chache.DiskCache;
import com.flyzebra.flyui.chache.IDiskCache;
import com.flyzebra.launcher.R;
import com.flyzebra.launcher.base.BaseActivity;
import com.flyzebra.launcher.module.SoundPlay;
import com.flyzebra.launcher.utils.FlyLog;
import com.flyzebra.launcher.utils.SPUtil;
import com.flyzebra.launcher.utils.SystemPropertiesProxy;
import com.flyzebra.ppfunstv.constant.Constants;
import com.flyzebra.ppfunstv.data.TemplateBean;
import com.flyzebra.ppfunstv.data.TemplateEntity;
import com.flyzebra.ppfunstv.utils.LoadData;
import com.flyzebra.ppfunstv.view.TvView.CellView.CellClickAction.CommondTool;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static com.flyzebra.launcher.R.id.btn_blur;

/**
 * Created by 李宗源 on 2016/7/22.
 * E-mail:lizy@ppfuns.com
 */
public class BackDoorActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = BackDoorActivity.class.getSimpleName();
    private final String USER_CONFIG = "user_config.properties";
    private final String KEY_SVN_VERSION = "svn_version";
    private Context mContext;
    private IDiskCache iDiskCache;
    private TextView tvSvn;
    private LinearLayout llTemplate;
    private LinearLayout llSetAge;
    private LinearLayout llSounds;
    private RadioGroup rgSounds;
    private RadioGroup rgAgeRange;
    private Button btn_sound;
    private Button btn_circulation;
    private Button btn_shake;
    private Button btn_debug;
    private Button btn_update;
    private Button btn_update_tip;
    private String[] mSoundRes;
    private TextView tvAlpha;
    private ProgressBar pbBar;
    private Button btnCheckVersion;
    private Button btnSetting;
    private Button btnUrl;
    private Button btnBlur;
    private Button btnSetting_Wraper;
    private Button btnAppLists;

    //声音播放
    private SoundPlay soundPlay;
    private int defaultSoundIndex = 7;
    private boolean isBlur;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_back_door);
        mContext = this;
        iDiskCache = new DiskCache().init(this);
        initView();
        initListener();

        //声音播放处理
        soundPlay = new SoundPlay(this);
        mSoundRes = soundPlay.soundStrings;
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        iDiskCache.release();
        super.onDestroy();
    }

    private void initView() {
        tvSvn = (TextView) findViewById(R.id.back_door_svn);
        llTemplate = (LinearLayout) findViewById(R.id.back_door_template);
        llSetAge = (LinearLayout) findViewById(R.id.back_door_set_child_age_ll);
        llSounds = (LinearLayout) findViewById(R.id.back_door_sounds_ll);
        rgSounds = (RadioGroup) findViewById(R.id.back_door_sound);
        rgAgeRange = (RadioGroup) findViewById(R.id.back_door_child_age);
        btn_sound = (Button) findViewById(R.id.btn_sound);
        btn_circulation = (Button) findViewById(R.id.btn_circulation);
        btn_shake = (Button) findViewById(R.id.btn_shake);
        btn_debug = (Button) findViewById(R.id.btn_debug);
        btn_update = (Button) findViewById(R.id.btn_update);
        btn_update_tip = (Button) findViewById(R.id.btn_update_tip);
        tvAlpha = (TextView) findViewById(R.id.tv_alpha);
        pbBar = (ProgressBar) findViewById(R.id.pb_alpha);
        btnCheckVersion = (Button) findViewById(R.id.btn_check_version);
        btnSetting = (Button)findViewById(R.id.btn_setting);
        btnUrl = (Button) findViewById(R.id.btn_proper_url);
        btnBlur = (Button) findViewById(btn_blur);
        btnSetting_Wraper = (Button) findViewById(R.id.btn_setting_wrapper);
        btnAppLists=(Button)findViewById(R.id.btn_app_lists);
    }

    private void initListener() {
        btn_sound.setOnClickListener(this);
        btn_circulation.setOnClickListener(this);
        btn_shake.setOnClickListener(this);
        btn_debug.setOnClickListener(this);
        btn_update.setOnClickListener(this);
        btn_update_tip.setOnClickListener(this);
        btnCheckVersion.setOnClickListener(this);
        btnSetting.setOnClickListener(this);
        btnUrl.setOnClickListener(this);
        btnBlur.setOnClickListener(this);
        btnSetting_Wraper.setOnClickListener(this);
        btnAppLists.setOnClickListener(this);
    }

    private void initData() {
        tvSvn.setText(getUserConfig(KEY_SVN_VERSION) + "");
        String templateString = iDiskCache.getString("/api/ui-operation/api/v/launcher_tab.json");
        TemplateBean bean = null;
        if (bean == null) {
            bean = LoadData.getInstance().loadTemplateData(this,"/api/ui-operation/api/v/launcher_tab.json");
        }
        if (bean != null && bean.getTemplate() != null) {
            for (final TemplateEntity entity : bean.getTemplate()) {
                Button button = new Button(this);
                final int templateId = entity.getTemplateId();
                button.setText(entity.getTemplateName() + " " + entity.getTemplateId());
                button.setFocusable(true);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (entity.getTabList() != null && entity.getTabList().size() > 0) {
                            FlyLog.d( "on template item click:" + templateId);
                            int curTemplate = SPUtil.getTemplate(BackDoorActivity.this, SPUtil.TEMPLATE_ID, -1);
                            if (curTemplate != templateId) {
                                FlyLog.d( "curTemplate id != select template id,need to change template");
                                SPUtil.setTemplate(BackDoorActivity.this, SPUtil.TEMPLATE_ID, templateId);
                                finish();
                            } else {
                                FlyLog.d("curTemplate id == select template id, no need to change template");
                            }
                        } else {
                            FlyLog.d( "tab list == null(no data), no need to change template");
                        }
                    }
                });
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.gravity = Gravity.CENTER_VERTICAL;
                llTemplate.addView(button,lp);
            }
        }
        if (mSoundRes != null && mSoundRes.length > 0) {
            rgSounds.removeAllViews();
            for (int i = 0; i < mSoundRes.length; i++) {
                final int index = i + 1;
                RadioButton btn = new RadioButton(this);
                btn.setText(mSoundRes[i]);
                btn.setTag(i);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SPUtil.set(mContext, SPUtil.FILE_CONFIG, SPUtil.CONFIG_SOUND_INDEX, index);
                        setSoundRadio();
                        soundPlay.stopSound();
                        soundPlay.playSound(index,0);
                    }
                });
                rgSounds.addView(btn);
            }

            setSoundRadio();
        }
        int templateId = (int) SPUtil.get(this,SPUtil.FILE_CONFIG,SPUtil.TEMPLATE_ID,0);
        //TODO 儿童版模板判断待修正
        if(templateId == 2){
//            String[] ages = getResources().getStringArray(R.array.age_range);
//            rgAgeRange.removeAllViews();
//            for(int i = 0;i<ages.length;i++){
//                final int ageId = i;
//                final RadioButton btn = new RadioButton(this);
//                btn.setText(ages[ageId]);
//                btn.setTag(ageId);
//                btn.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        SPUtil.set(mContext, SPUtil.FILE_CONFIG, SPUtil.CONFIG_AGE_RANGE, ageId);
//                        Intent intent = new Intent(Constants.Action.CHILD_CHANGE_UI);
//                        sendBroadcast(intent);
//                        setAgeRadio();
//                    }
//                });
//                rgAgeRange.addView(btn);
//            }
//            setAgeRadio();
//            llSetAge.setVisibility(View.VISIBLE);
        }

        boolean sound_flag = (boolean) SPUtil.get(this, SPUtil.FILE_CONFIG, SPUtil.CONFIG_SOUND_FLAG, false);
        if (!sound_flag) {
            btn_sound.setText(getString(R.string.back_door_open_sound));
            llSounds.setVisibility(View.GONE);
        }

        boolean circulation_flag = (boolean) SPUtil.get(this, SPUtil.FILE_CONFIG, SPUtil.CONFIG_CIRCULATION_FLAG, false);
        if (circulation_flag) {
            btn_circulation.setText(getString(R.string.back_door_close_circulation));
        }

        boolean shake_flag = (boolean) SPUtil.get(this, SPUtil.FILE_CONFIG, SPUtil.CONFIG_SHAKE, false);
        if(shake_flag){
            btn_shake.setText(getString(R.string.back_door_close_shake));
        }

        String debug = SystemPropertiesProxy.get(this,Constants.Property.DEBUG,"false");
        if("true".equals(debug)){
            btn_debug.setText(getString(R.string.back_door_close_debug));
            startService(Constants.Action.OPEN_DEBUG);
        }

        boolean tip_flag = (boolean) SPUtil.get(this, SPUtil.CONFIG_UPDATE_TIPS, SPUtil.Default.tips);
        if (tip_flag) {
            btn_update_tip.setText(R.string.back_door_close_update_tip);
        }

        float cellAlpha = (float) SPUtil.get(this,SPUtil.CONFIG_ALPHA_CELL,1.0f);
        pbBar.setProgress((int) (cellAlpha*pbBar.getMax()));
        tvAlpha.setText(String.format(getString(R.string.back_door_alpha),cellAlpha));
        boolean blur_flag = Boolean.parseBoolean(SystemPropertiesProxy.get(this, Constants.Property.DIALOG_BLUR, "false"));
        if(!blur_flag){ //true
            btnBlur.setText("打开高斯模糊");
        }
    }

    /**
     * 设置儿童版年龄选择项
     */
    private void setAgeRadio(){
        int ageIndex = (int) SPUtil.get(mContext, SPUtil.FILE_CONFIG, SPUtil.CONFIG_AGE_RANGE, 0);
        {
            for (int i = 0; i < rgAgeRange.getChildCount(); i++) {
                if (rgAgeRange.getChildAt(i) instanceof RadioButton) {
                    RadioButton view = (RadioButton) rgAgeRange.getChildAt(i);
                    if ((int) view.getTag() == ageIndex) {
                        view.setChecked(true);
                        break;
                    }
                }
            }
        }
    }

    /**
     * 设置声音选择项
     */
    private void setSoundRadio() {
        int soundIndex = (int) SPUtil.get(mContext, SPUtil.FILE_CONFIG, SPUtil.CONFIG_SOUND_INDEX, defaultSoundIndex);
        {
            for (int i = 0; i < rgSounds.getChildCount(); i++) {
                if (rgSounds.getChildAt(i) instanceof RadioButton) {
                    RadioButton view = (RadioButton) rgSounds.getChildAt(i);
                    if ((int) view.getTag() == (soundIndex-1)) {
                        view.setChecked(true);
                        break;
                    }
                }
            }
        }
    }

    private String getUserConfig(String key) {
        Properties pro = new Properties();
        InputStream is = null;
        try {
            is = getAssets().open(USER_CONFIG);
            pro.load(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String value = (String) pro.get(key);

        return value;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sound: {
                boolean sound_flag = (boolean) SPUtil.get(this, SPUtil.FILE_CONFIG, SPUtil.CONFIG_SOUND_FLAG, false);
                if (sound_flag) {
                    SPUtil.set(this, SPUtil.FILE_CONFIG, SPUtil.CONFIG_SOUND_FLAG, false);
                    btn_sound.setText(getString(R.string.back_door_open_sound));
                    llSounds.setVisibility(View.GONE);
                } else {
                    SPUtil.set(this, SPUtil.FILE_CONFIG, SPUtil.CONFIG_SOUND_FLAG, true);
                    btn_sound.setText(getString(R.string.back_door_close_sound));
                    llSounds.setVisibility(View.VISIBLE);
                }
                break;
            }
            case R.id.btn_circulation: {
                boolean circulation_flag = (boolean) SPUtil.get(this, SPUtil.FILE_CONFIG, SPUtil.CONFIG_CIRCULATION_FLAG, false);
                if (circulation_flag) {
                    SPUtil.set(this, SPUtil.FILE_CONFIG, SPUtil.CONFIG_CIRCULATION_FLAG, false);
                    btn_circulation.setText(getString(R.string.back_door_open_circulation));
                } else {
                    SPUtil.set(this, SPUtil.FILE_CONFIG, SPUtil.CONFIG_CIRCULATION_FLAG, true);
                    btn_circulation.setText(getString(R.string.back_door_close_circulation));
                }
                break;
            }
            case R.id.btn_update:{
                break;
            }
            case R.id.btn_shake:{
                boolean shake_flag = (boolean) SPUtil.get(this, SPUtil.FILE_CONFIG, SPUtil.CONFIG_SHAKE, false);
                if (shake_flag) {
                    SPUtil.set(this, SPUtil.FILE_CONFIG, SPUtil.CONFIG_SHAKE, false);
                    btn_shake.setText(getString(R.string.back_door_open_shake));
                } else {
                    SPUtil.set(this, SPUtil.FILE_CONFIG, SPUtil.CONFIG_SHAKE, true);
                    btn_shake.setText(getString(R.string.back_door_close_shake));
                }
                break;
            }
            case R.id.btn_debug:{
                String debug = SystemPropertiesProxy.get(this,Constants.Property.DEBUG,"false");
                if ("true".equals(debug)) {
                    btn_debug.setText(getString(R.string.back_door_open_debug));
                    SystemPropertiesProxy.set(mContext,Constants.Property.DEBUG,"false");
                    startService(Constants.Action.CLOSE_DEBUG);
                } else {
                    btn_debug.setText(getString(R.string.back_door_close_debug));
                    SystemPropertiesProxy.set(mContext,Constants.Property.DEBUG,"true");
                    startService(Constants.Action.OPEN_DEBUG);
                }
                break;
            }
            case R.id.btn_update_tip:{
                boolean tip_flag = (boolean) SPUtil.get(this, SPUtil.CONFIG_UPDATE_TIPS, SPUtil.Default.tips);
                if (tip_flag) {
                    SPUtil.set(this, SPUtil.FILE_CONFIG, SPUtil.CONFIG_UPDATE_TIPS, false);
                    btn_update_tip.setText(getString(R.string.back_door_open_update_tip));
                } else {
                    SPUtil.set(this, SPUtil.CONFIG_UPDATE_TIPS, true);
                    btn_update_tip.setText(getString(R.string.back_door_close_update_tip));
                }
                break;
            }
            case R.id.btn_check_version:{
                CommondTool.execStartActivityAndShowTip(this,Constants.Action.CHECK_VERSION,null,getString(R.string.app_not_install),false);
                break;
            }
            case R.id.btn_setting:
                CommondTool.execCommand("am start com.android.settings/.Settings");
                break;
            case R.id.btn_proper_url:
                CommondTool.execStartPackage(mContext, com.flyzebra.launcher.constant.Constants.PACKAGE_PROPERTY_SET);
                break;
            case R.id.btn_blur:
                isBlur = Boolean.parseBoolean(SystemPropertiesProxy.get(this, Constants.Property.DIALOG_BLUR, "false"));
                if(isBlur){
                    btnBlur.setText("打开高斯模糊");
                    SystemPropertiesProxy.set(this,Constants.Property.DIALOG_BLUR,"false");
                }else{
                    btnBlur.setText("关闭高斯模糊");
                    SystemPropertiesProxy.set(this,Constants.Property.DIALOG_BLUR,"true");
                }
                break;
            case R.id.btn_setting_wrapper:
                CommondTool.execStartActivity(this,R.string.actionSettings);
                break;
            case R.id.btn_app_lists:
                CommondTool.execStartPackage(this,this.getPackageName(),AppListActivity.class.getName());
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        FlyLog.d(" onKeyDown  keycode:"+keyCode);
        View curView = getCurrentFocus();
        if(curView == pbBar){
            if(KeyEvent.KEYCODE_DPAD_LEFT == keyCode){
                int progress = pbBar.getProgress();
                int curPro = progress - 5 > 10 ?progress-5:10;
                updateAlpha(curPro);
                return true;
            }else if(KeyEvent.KEYCODE_DPAD_RIGHT == keyCode){
                int progress = pbBar.getProgress();
                int curPro = progress +5 < pbBar.getMax() ?progress+5:pbBar.getMax();
                updateAlpha(curPro);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void updateAlpha(int curPro){
        pbBar.setProgress(curPro);
        float cellAlpha = curPro*1.0f/pbBar.getMax();
        SPUtil.set(mContext,SPUtil.CONFIG_ALPHA_CELL,cellAlpha);
        tvAlpha.setText(String.format(getString(R.string.back_door_alpha),cellAlpha));
        Constants.cellNoFocus = cellAlpha;
    }

    private void startService(String action){
        FlyLog.d("strtService, action:"+action);
        Intent it = new Intent(action);
        startService(it);
    }
}
