package com.ppfuns.ppfunstv.view.TvView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.ppfuns.ppfunstv.data.CellBean;
import com.ppfuns.ppfunstv.data.LogoEntity;
import com.ppfuns.ppfunstv.data.MarqueeEntity;
import com.ppfuns.ppfunstv.data.TemplateEntity;
import com.ppfuns.ppfunstv.module.UpdataVersion.IDiskCache;
import com.ppfuns.ppfunstv.utils.FlyLog;
import com.ppfuns.ppfunstv.utils.SPUtil;
import com.ppfuns.ppfunstv.view.MarqueeView.IMarquee;
import com.ppfuns.ppfunstv.view.MarqueeView.MarqueeFactory;
import com.ppfuns.ppfunstv.view.TvView.CellView.ITvPageItemView;
import com.ppfuns.ppfunstv.view.TvView.HeaderView.HeaderLayout;
import com.ppfuns.ppfunstv.view.TvView.PopupTV.PopupTvViewLayout;

import java.util.List;

/**
 *
 * Created by flyzebra on 17-6-15.
 */
public class SimpleTvView extends RelativeLayout implements ITvView{
    private Context mContext;
    private boolean isRunView = false;

    public SimpleTvView(Context context) {
        this(context,null);
    }

    public SimpleTvView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SimpleTvView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initContext(context);
    }

    private void initContext(Context context) {
        mContext = context;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onResume() {
        for(int i=0;i<getChildCount();i++){
            View view = getChildAt(i);
            if(view instanceof PopupTvViewLayout){
                ((PopupTvViewLayout)view).notifyPageChange(0);
            }
        }
    }

    @Override
    public void onPause() {
        for(int i=0;i<getChildCount();i++){
            View view = getChildAt(i);
            if(view instanceof PopupTvViewLayout){
                ((PopupTvViewLayout)view).notifyPageChange(1);
            }
        }
    }

    @Override
    public void onStop() {

    }

    @Override
    public void createLogoView(LogoEntity logoEntity,IDiskCache iDiskCache) {
        if(logoEntity!=null){
            FlyLog.d("create logo! %s",logoEntity.toString());
            ImageView imageView = new ImageView(mContext);
            LayoutParams lp = new LayoutParams(logoEntity.getWidth(),logoEntity.getHeight());
            lp.leftMargin = logoEntity.getX();
            lp.topMargin = logoEntity.getY();
            imageView.setLayoutParams(lp);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            addView(imageView);
            Glide.with(mContext).load(logoEntity.getImgUrl()).into(imageView);
        }else{
            FlyLog.d("logoEntiy data is null, do't create logo!");
        }
    }

    @Override
    public void createStatusbarView() {
        HeaderLayout mHeaderLayout = new HeaderLayout(mContext);
//        if (mControlBean != null) {
//            mHeaderLayout.setData(mControlBean.getLogo(), iDiskCache, screenScale);
//        }
        addView(mHeaderLayout);
    }

    @Override
    public void createPageView(TemplateEntity templateEntity, List<CellBean> mCellBeanList, IDiskCache iDiskCache) {
        if (mCellBeanList != null && mCellBeanList.size() > 0) {
            PopupTvViewLayout popupTvViewLayout = new PopupTvViewLayout.Builder()
                    .context(mContext)
                    .isShowReflect(true)
                    .iDiskCache(iDiskCache)
                    .onCellItemClick(new BaseTvView.OnCellItemClick() {
                        @Override
                        public void onCellItemClick(ITvPageItemView view) {
                            view.doAction();
                        }
                    })
                    .createView();

            LayoutParams lp2 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            popupTvViewLayout.setLayoutParams(lp2);
            int age_range = (int) SPUtil.get(mContext, SPUtil.FILE_CONFIG, SPUtil.CONFIG_AGE_RANGE, 0);
            if (mCellBeanList.size() <= age_range) {
                age_range = 0;
            }
            popupTvViewLayout.setData(mCellBeanList.get(age_range));
            addView(popupTvViewLayout);
        }
    }

    @Override
    public void createMaqueeView(MarqueeEntity marqueeEntity) {
        try {
            IMarquee mMarqueeview = MarqueeFactory.createView(mContext, marqueeEntity);
            if (mMarqueeview != null) {
                mMarqueeview.bind(this);
                if(isRunView){
                    mMarqueeview.play();
                }else {
                    mMarqueeview.stop();
                }
            }
        } catch (Exception e) {
            FlyLog.d("Add Marquee error! " + e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void startPlay() {
        isRunView = true;
        for(int i=0;i<getChildCount();i++){
            View view = getChildAt(i);
            if(view instanceof IMarquee){
                ((IMarquee)view).play();
            }
        }
    }

    @Override
    public void stopPlay() {
        isRunView = false;
        for(int i=0;i<getChildCount();i++){
            View view = getChildAt(i);
            if(view instanceof IMarquee){
                ((IMarquee)view).stop();
            }
        }
    }

}
