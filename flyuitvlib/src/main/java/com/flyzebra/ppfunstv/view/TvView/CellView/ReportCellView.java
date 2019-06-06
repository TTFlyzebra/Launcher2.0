package com.flyzebra.ppfunstv.view.TvView.CellView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.ImageView;

import com.flyzebra.ppfunstv.utils.FlyLog;

/**
 *
 * 显示列表控件
 * Created by flyzebra on 17-6-8.
 */
public class ReportCellView extends TvPageItemView{
    private SubScriptView imageCellView;
    private MyBroad myBroad;
    public ReportCellView(Context context) {
        super(context);
    }

    public ReportCellView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ReportCellView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void initView() {
//        addBackImage();
        FlyLog.d("width = %d,height = %d",mCell.getWidth(),mCell.getHeight());
        LayoutParams lp = new LayoutParams(mCell.getWidth(), mCell.getHeight());
    }

    private void addBackImage() {
        imageCellView = new SubScriptView(mContext);
        imageCellView.setScaleType(ImageView.ScaleType.FIT_XY);
        LayoutParams lp = new LayoutParams(mCell.getWidth(), mCell.getHeight());
        imageCellView.setLayoutParams(lp);
        addView(imageCellView);
    }


    @Override
    public ImageView getMyImageView() {
        return imageCellView;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if(myBroad==null){
            myBroad = new MyBroad();
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction("TESTREPORT");
        mContext.registerReceiver(myBroad, filter);
    }

    @Override
    protected void onDetachedFromWindow() {
        mContext.unregisterReceiver(myBroad);
        super.onDetachedFromWindow();
    }

    class MyBroad extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("TESTREPORT")){
                FlyLog.d();
            }
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        FlyLog.d("XXXXXXXXXXX");
        return super.onKeyDown(keyCode, event);
    }
}
