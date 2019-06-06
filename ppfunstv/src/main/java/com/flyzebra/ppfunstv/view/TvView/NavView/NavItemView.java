package com.flyzebra.ppfunstv.view.TvView.NavView;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyzebra.ppfunstv.R;
import com.flyzebra.ppfunstv.data.StateStyleEntity;
import com.flyzebra.ppfunstv.data.TabEntity;
import com.flyzebra.ppfunstv.utils.DisplayUtils;
import com.flyzebra.ppfunstv.utils.FlyLog;
import com.flyzebra.ppfunstv.utils.FontManager;
import com.flyzebra.ppfunstv.utils.GsonUtil;
import com.flyzebra.ppfunstv.utils.Utils;

import java.util.List;
import java.util.Map;

/**
 * 一级栏目每个tab的自定义控件
 * Created by lzy on 2016/6/15.
 */
public class NavItemView extends RelativeLayout{
    private Context mContext = null;
    private LayoutInflater mInflater = null;
    private ImageView mImage = null;//图片
    private View vBackgroud;
    private TextView mTvName = null;
    private Map<String,String> mLangMap = null;
    private float mScale = 1.0f;//缩放大小
    private int mDefaultFontSize = 36;//单位px
    private float ALPHA_FOCUS_ON = 1f;
    private float ALPHA_FOCUS_OUT = 0.3f;

    public NavItemView(Context context) {
        super(context);
    }

    public NavItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NavItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public NavItemView(Context context, TabEntity tab,int index){
        super(context,null);
        this.mTab = tab;
        mLangMap = GsonUtil.json2Map(mTab.getName());
        init(context,index);
    }

    private void init(Context context,final int index){
        mContext = context;
        mScale =  DisplayUtils.getMetrices((Activity) mContext).widthPixels / 1920f;
        this.mInflater = LayoutInflater.from(context);
        inflate(context, R.layout.tv_tab_item, this);
        mImage = (ImageView) findViewById(R.id.iv_tab);
        mTvName = (TextView) findViewById(R.id.tv_tab);
        vBackgroud = findViewById(R.id.fl_bg);
//        mTvName.setAlpha(ALPHA_FOCUS_OUT);
        if(mLangMap != null){
            mTvName.setText(Utils.getLocalLanguageString(mLangMap));
        }else{
            mTvName.setText(mTab.getName());
        }

        if(!TextUtils.isEmpty(mTab.getFont())){
            mTvName.setTypeface(FontManager.getTypefaceByFontName(mContext,mTab.getFont()));
        }

    }

    public TabEntity mTab = null;

    public void setTextColor(int color){
        mTvName.setTextColor(color);
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }

    /**
     * 设置当前状态效果
     * @param curStatus 0:当前聚焦
     *                  1:当前非聚焦
     *                  2:非当前
     */
    public void setFocusEffect(int curStatus){
        try{
            List<StateStyleEntity> stateStyles = mTab.getStateStyle();
            StateStyleEntity styleEntity = null;
            if(stateStyles != null){
                for(StateStyleEntity item : stateStyles){
                    if(curStatus + 1 == item.getState()){
                        styleEntity = item;
                        break;
                    }
                }
            }
            if(styleEntity != null){
                float alpha = 1.0f;
                if(styleEntity.getAlpha()>1){
                    alpha = alpha * styleEntity.getAlpha() / 255;
                }
                int color = 0xfffffff;

                try {
                    color = Color.parseColor(styleEntity.getColor());
                }catch (Exception e){
                    e.printStackTrace();
                    FlyLog.d(e.toString());
                }
                mTvName.setTextColor(color);
                mTvName.setAlpha(alpha);
                mTvName.setTextSize(TypedValue.COMPLEX_UNIT_PX,(float)styleEntity.getSize()*mScale);
                switch (curStatus){
                    case 0://
                        //Glide.with(mContext).load(R.drawable.tv_focus_on).into(rlFocusBg);
                        vBackgroud.setBackgroundResource(R.drawable.tv_focus_on);
                        break;
                    case 1:
                        //Glide.with(mContext).load(R.drawable.tv_focus_out).into(rlFocusBg);
                        vBackgroud.setBackgroundResource(R.drawable.tv_focus_out);
                        break;

                    case 2:
                        //Glide.with(mContext).load("").into(rlFocusBg);
                        vBackgroud.setBackground(null);
                        break;
                }
            }else{
                mTvName.setTextColor(Color.WHITE);
                mTvName.setAlpha(ALPHA_FOCUS_ON);
                mTvName.setTextSize(TypedValue.COMPLEX_UNIT_PX,(float)mDefaultFontSize*mScale);
                switch (curStatus){
                    case 0://
                        //Glide.with(mContext).load(R.drawable.tv_focus_on).into(rlFocusBg);
                        vBackgroud.setBackgroundResource(R.drawable.tv_focus_on);
                        break;
                    case 1://
                        ///Glide.with(mContext).load(R.drawable.tv_focus_out).into(rlFocusBg);
                        vBackgroud.setBackgroundResource(R.drawable.tv_focus_out);
                        break;
                    default:
                       // Glide.with(mContext).load("").into(rlFocusBg);rlFocusBg.setImageBitmap(null);
                        vBackgroud.setBackground(null);
                        break;
                }
            }
        }catch (Exception e){
            FlyLog.d(e.toString());
            e.printStackTrace();
        }
    }

    public TextView getTextView(){
        return mTvName;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|
                Paint.FILTER_BITMAP_FLAG));
        super.onDraw(canvas);
    }
}
