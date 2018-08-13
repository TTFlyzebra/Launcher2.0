package com.ppfuns.launcher.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ppfuns.launcher.R;
import com.ppfuns.launcher.utils.SPUtil;
import com.ppfuns.ppfunstv.module.EventMessage;
import com.ppfuns.ppfunstv.utils.BlurDrawable;
import com.ppfuns.ppfunstv.utils.ToastUtils;
import com.ppfuns.ppfunstv.view.TvView.CellView.CellClickAction.MobclickConstants;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;

/**
 * Created by 李宗源 on 2016/8/23.
 * E-mail:lizy@ppfuns.com
 */
public class ChangeTemplateDialog extends Dialog{
    private Context mContext;

    private RelativeLayout rlBlur;
    private boolean isBlur; // 是否显示高斯模糊
    private View view;

    private LinearLayout llTemplate;
    public ChangeTemplateDialog(Context context) {
        this(context,R.style.DialogStyle);
    }

    public ChangeTemplateDialog(Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;
        init();
    }

    private void init(){
        setContentView(R.layout.change_template_dialog);
        rlBlur = (RelativeLayout) findViewById(R.id.rl_blur);
        llTemplate = (LinearLayout) findViewById(R.id.ll_template_layout);
        Window window = getWindow();
        window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ERROR);
        window.setGravity(Gravity.CENTER);
    }

    public void setData(Map<Integer,String> templates){
        if(templates != null){
            llTemplate.removeAllViews();
            for(Map.Entry<Integer,String> entry : templates.entrySet()){
                Button btn = new Button(mContext);
                btn.setSingleLine();
                btn.setEllipsize(TextUtils.TruncateAt.valueOf("MARQUEE"));
                btn.setText(entry.getValue());
                final int templateId = entry.getKey();
                final int selectId = SPUtil.getTemplate(mContext,SPUtil.TEMPLATE_ID,-1);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(selectId != templateId){
                            SPUtil.setTemplate(mContext, SPUtil.TEMPLATE_ID, templateId);
                            EventBus.getDefault().post(new EventMessage(EventMessage.MSG_CHANGE_TEMPLATE));
                        }else{
//                            Toast.makeText(mContext,R.string.template_is_the_same,Toast.LENGTH_SHORT).show();
                            showChangelTemplateTipAndReport(mContext.getString(R.string.template_is_the_same));
                        }
                        ChangeTemplateDialog.this.dismiss();
                    }
                });
                btn.setTextSize(30);
                btn.setTextColor(Color.WHITE);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(220, 150);
                btn.setPadding(35,0,35,0);
                btn.setLayoutParams(lp);
                btn.setBackgroundResource(R.drawable.tv_album_btn_bg);
                llTemplate.addView(btn);
                if(selectId == templateId){
                    btn.requestFocus();
                }
            }
            if(isBlur){ // 如果显示高斯模糊就载入高斯模糊背景图
                BlurDrawable blurDrawable = new BlurDrawable((Activity)mContext);
                blurDrawable.setBlurRadius(9); //模糊半径12，越大图片越平均
                blurDrawable.setDownsampleFactor(2); //图片抽样率，这里把图片缩放小了8倍
                blurDrawable.setOverlayColor(Color.argb(150, 0x0, 0x0, 0x0)); //模糊后再覆盖的一层颜色
                //blurDrawable.setDrawOffset(0,0); //顶部View与底部View的相对坐标差，由于这里都是(0,0)起步，所以相对位置偏移为0
                rlBlur.setBackgroundDrawable(blurDrawable);
            }
        }
    }

    public void setIsBlur(boolean isBlur){
        this.isBlur = isBlur;
    }
    public void setView(View view){
        this.view = view;
    }

    private void showChangelTemplateTipAndReport(String info){
        ToastUtils.showMessage(mContext,info);
        if(info.startsWith(mContext.getString(R.string.tip_switch_ui))){
            MobclickAgent.onEvent(mContext, MobclickConstants.TYPE_SWITCH_UI);

        }else{
            MobclickAgent.reportError(mContext,info);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_M:
            case KeyEvent.KEYCODE_MENU:{
                if(rlBlur != null){
                    rlBlur.setBackground(null);
                }
                dismiss();
                break;
            }
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
}
