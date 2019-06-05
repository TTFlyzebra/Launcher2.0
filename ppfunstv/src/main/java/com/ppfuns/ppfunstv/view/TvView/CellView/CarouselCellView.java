package com.ppfuns.ppfunstv.view.TvView.CellView;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.ppfuns.ppfunstv.data.CellEntity;
import com.ppfuns.ppfunstv.utils.DisplayUtils;
import com.ppfuns.ppfunstv.utils.FlyLog;
import com.ppfuns.ppfunstv.utils.FontManager;
import com.ppfuns.ppfunstv.utils.GsonUtil;
import com.ppfuns.ppfunstv.utils.Utils;
import com.ppfuns.ppfunstv.view.LoopPlayView.ILoopPlayView;
import com.ppfuns.ppfunstv.view.LoopPlayView.LoopPlayImage;
import com.ppfuns.ppfunstv.view.LoopPlayView.NaviForViewPager;
import com.ppfuns.ppfunstv.view.ReflectImageView;
import com.ppfuns.ppfunstv.view.TvView.CellView.CellClickAction.ActionFactory;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * 轮播图片控件
 * Created by flyzebra on 17-5-3.
 */
public class CarouselCellView extends TvPageItemView {
    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    private int mDuration = 5000;
    private ILoopPlayView loopPlayImages;
    private NaviForViewPager naviForViewPager;
    private TextView textView;
    private int mDirection = RIGHT;
    private Handler mHander = new Handler(Looper.getMainLooper());

//    private String[] mPalyImages;

    private ImageView imageview;

    private String refUrl;

    public CarouselCellView(Context context) {
        this(context, null);
    }

    public CarouselCellView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CarouselCellView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setClipChildren(true);
        setClipToPadding(true);
    }

    public void setDuration(int duration) {
        mDuration = duration;
    }

    public void setDirection(int mDirection) {
        this.mDirection = mDirection;
    }

    Rect rect = new Rect();
    private Runnable task = new Runnable() {
        @Override
        public void run() {
            try {
                Boolean isTop = false;
                try {
                    ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
                    List<ActivityManager.RunningTaskInfo> appTask = am.getRunningTasks(1);
                    isTop = appTask.size() > 0 && appTask.get(0).topActivity.equals(((Activity) mContext).getIntent().getComponent());
                } catch (SecurityException e) {
                    e.printStackTrace();
                    FlyLog.d(e.toString());
                }
                if (loopPlayImages != null && getLocalVisibleRect(rect) && isTop) {
//                    FlyLog.d("GoTo next item");
                    switch (mDirection) {
                        case LEFT:
                            ((View) loopPlayImages).onKeyDown(KeyEvent.KEYCODE_DPAD_LEFT, null);
                            break;
                        case RIGHT:
                            ((View) loopPlayImages).onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, null);
                            break;
                        default:
                            ((View) loopPlayImages).onKeyDown(KeyEvent.KEYCODE_DPAD_LEFT, null);
                            break;
                    }
                }
                mHander.postDelayed(task, mDuration);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public View setCellData(CellEntity cellEntity) {
        cellEntity.setFocusType(1);
        super.setCellData(cellEntity);
        return this;
    }


    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mDuration = Math.max(mCell.getCarouselTime(), 2000);
        mDirection = mCell.getCarouselType();
        mHander.postDelayed(task, mDuration);
    }


    @Override
    protected void onDetachedFromWindow() {
        mHander.removeCallbacksAndMessages(null);
        super.onDetachedFromWindow();
    }


    @Override
    public void bindReflectView(ReflectImageView reflectImageView) {
        super.bindReflectView(reflectImageView);
        if (!TextUtils.isEmpty(refUrl)) {
            setReflectView(refUrl, mLoadImageResId);
        }
    }

    /**
     * 设置倒影控件
     *
     * @param url
     * @param resId
     */
    private void setReflectView(final String url, int resId) {
        refUrl = url;
        if (reflectImageView == null) return;
        Glide.with(mContext)
                .load(url)
                .asBitmap()
                .override(mCell.getWidth(), mCell.getHeight())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        showReflectView(resource);
                    }
                });
    }

    @Override
    public void showImage(final String imgUrl) {
        if (loopPlayImages == null) {
            FlyLog.d("error don't createView.");
            return;
        }
        loopPlayImages.notifyData(new ILoopPlayView.OnDataChanged() {
            @Override
            public void setChildViewData(ImageView imageView, int num, String url) {
                try {
                    Glide.with(mContext)
                            .load(url)
                            .placeholder(mLoadImageResId)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .into(imageView);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public ImageView getMyImageView() {
        return imageview;
    }

    @Override
    public void doAction(int flag) {
        try {
            if (loopPlayImages != null) {
                int item = loopPlayImages.getCurrentItem();
                clickEvent = ActionFactory.create(mContext, mCell.getSubCellList().get(item));
            } else {
                clickEvent = ActionFactory.create(mContext, mCell);
            }
            if (clickEvent != null) {
                clickEvent.doAction(flag);
            }
        } catch (Exception e) {
            e.printStackTrace();
            FlyLog.d(e.toString());
        }
    }

    @Override
    public void initView() {
        if (mCell.getSubCellList() != null) {
            final String mPalyImages[] = new String[mCell.getSubCellList().size()];
            for (int i = 0; i < mCell.getSubCellList().size(); i++) {
                mPalyImages[i] = iDiskCache.getBitmapPath(mCell.getSubCellList().get(i).getImgUrl());
            }

            loopPlayImages = new LoopPlayImage(mContext);
            LayoutParams lp0 = new LayoutParams(mCell.getWidth(), mCell.getHeight());
            ((View)loopPlayImages).setLayoutParams(lp0);
            loopPlayImages.setImageViewUrls(mPalyImages)
                    .setShowImageNum(1)
                    .setChildViewWidth(mCell.getWidth())
                    .setChildViewHeight(mCell.getHeight())
                    .setChildViewPadding(0)
                    .initView(mCell.getWidth(), mCell.getHeight());

            addView((View) loopPlayImages);

            loopPlayImages.setOnChildViewChanged(new ILoopPlayView.OnViewChangedListener() {
                @Override
                public void onViewChanged(ImageView lostView, ImageView focusView, int item) {
                    naviForViewPager.setCurrentItem(item);
                    showText(item);
                    setReflectView(mPalyImages[item], mLoadImageResId);
                }
            });


            int textSize = 18;

            int textColor = 0xffffffff;

            float screenScale = DisplayUtils.getMetrices((Activity)mContext).widthPixels / 1920f;

            try{
                textSize = (int) (mCell.getSubCellList().get(0).getSize()*screenScale);
                String strColor = mCell.getSubCellList().get(0).getColor();
                if(!TextUtils.isEmpty(strColor)){
                    textColor = Color.parseColor(mCell.getSubCellList().get(0).getColor());
                }
            }catch (Exception e){
                FlyLog.d(e.toString());
                e.printStackTrace();
            }

            textSize =  Math.max(textSize,18);

            textView = new TextView(mContext);
            LayoutParams lp2 = new LayoutParams(mCell.getWidth(), (int) (textSize*1.5));
            lp2.setMargins(0, (int) (mCell.getHeight() - textSize*1.5), 0, 0);
            textView.setLayoutParams(lp2);
//            textView.setPadding(5, 0, 5, 0);
            textView.setTextColor(textColor);
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
//            textView.getPaint().setFakeBoldText(true);

            naviForViewPager = new NaviForViewPager(mContext);
            naviForViewPager.setGravity(mCell.getCarouselPosition());//默认居中
//            naviForViewPager.setBackgroundColor(0x4f000000);
            LayoutParams lp1 = new LayoutParams(mCell.getWidth(), 16);
            lp1.setMargins(0, (int) (mCell.getHeight() - (textSize*1.5+20)), 0, 32);
            naviForViewPager.setLayoutParams(lp1);
            naviForViewPager.setSumItem(mPalyImages.length);
            addView(naviForViewPager);
            naviForViewPager.setCurrentItem(loopPlayImages.getCurrentItem());

            switch (mCell.getCarouselPosition()) {
                case NaviForViewPager.LEFT:
                    textView.setGravity(Gravity.LEFT);
                    break;
                case NaviForViewPager.RIGHT:
                    textView.setGravity(Gravity.RIGHT);
                    break;
                case NaviForViewPager.MID:
                    textView.setGravity(Gravity.CENTER);
                    break;
                default:
                    textView.setGravity(Gravity.LEFT);
                    break;
            }
            textView.setBackgroundColor(0xAF000000);
            if (!TextUtils.isEmpty(mCell.getFont())) {
                textView.setTypeface(FontManager.getTypefaceByFontName(mContext, mCell.getFont()));
            }
            addView(textView);
            showText(0);
        } else {
            imageview = new ImageView(mContext);
            LayoutParams lp = new LayoutParams(mCell.getWidth(), mCell.getHeight());
            imageview.setLayoutParams(lp);
            imageview.setScaleType(ImageView.ScaleType.FIT_XY);
            addView(imageview);
            super.showImage(mCell.getImgUrl());
        }
    }


    @Override
    public String getPackName() {
        String packageName = "";
        try {
            if (mCell != null && mCell.getSubCellList() != null) {
                JSONObject jsonObject = new JSONObject(mCell.getSubCellList().get(loopPlayImages.getCurrentItem()).getIntent());
                packageName = jsonObject.getString("packageName");
            }
        } catch (Exception e) {
            FlyLog.d("get app packageName failed!");
        }
        return packageName;
    }

    private void showText(int currentItem) {
        try {
            String textStr = mCell.getSubCellList().get(currentItem).getText();
            Map str = GsonUtil.json2Map(textStr);
            textView.setText(Utils.getLocalLanguageString(str));
        } catch (Exception e) {
            FlyLog.d(e.toString());
        }

    }

}
