package com.ppfuns.ppfunstv.view.TvView.CellView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import com.ppfuns.ppfunstv.R;
import com.ppfuns.ppfunstv.constant.Constants;
import com.ppfuns.ppfunstv.utils.FlyLog;
import com.ppfuns.ppfunstv.utils.FontManager;
import com.ppfuns.ppfunstv.utils.SPUtil;


/**
 * 儿童版内部设置UI的CELL,自动生成性别,年龄信息
 */
public class ChildSetCellView extends SimpleCellView {
    private static final String TAG = ChildSetCellView.class.getSimpleName();
    private static final String BOY = "boy";

    private TextView tvAge;
    private TextView tvSex;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            FlyLog.d(TAG, "child ui changed...");
            updateView();
        }
    };

    public ChildSetCellView(Context context) {
        this(context,null);
    }

    public ChildSetCellView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChildSetCellView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);bringToFront();
    }

    protected void initView(Context context) {
        FlyLog.d("init cell:" + mCell.toString());
        mContext = context;
        LayoutParams lp = new LayoutParams(mCell.getWidth(), mCell.getHeight());
        setLayoutParams(lp);
        inflate(context, R.layout.tv_child_set_cell_item, this);
        mImageView = (SubScriptView) findViewById(R.id.tv_iv_cell);
        tvSex = (TextView) findViewById(R.id.tv_sex);
        tvAge = (TextView) findViewById(R.id.tv_age);
        if (!TextUtils.isEmpty(mCell.getFont())) {
            tvSex.setTypeface(FontManager.getTypefaceByFontName(mContext, mCell.getFont()));
            tvAge.setTypeface(FontManager.getTypefaceByFontName(mContext, mCell.getFont()));
        }
        updateView();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        registerReceiver();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        unregisterReceiver();
    }

    private void registerReceiver(){
        IntentFilter intentFilter = new IntentFilter(Constants.Action.CHILD_UI_SET);
        mContext.registerReceiver(mReceiver,intentFilter);
    }

    private void  unregisterReceiver(){
        mContext.unregisterReceiver(mReceiver);
    }

    private void updateView(){
        String sex = (String) SPUtil.get(mContext, SPUtil.FILE_CONFIG, SPUtil.CONFIG_SEX, BOY);
        if(BOY.equals(sex)){
//            tvSex.setText();
        }else{

        }

        int ageIndex = (int) SPUtil.get(mContext,SPUtil.FILE_CONFIG,SPUtil.CONFIG_AGE_RANGE,0);
        String[] ages = {"0-3岁", "3-6岁", "6岁"};
        if(ageIndex >= ages.length){
            FlyLog.d(TAG," ageIndex is wrong,ageIndex:"+ageIndex+" age size:"+ages.length);
            ageIndex = 0;
        }
        FlyLog.d(TAG," sex:"+sex+" age:"+ages[ageIndex]);

    }
}
