package com.flyzebra.ppfunstv.view.TvView.CellView;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.flyzebra.ppfunstv.data.CellEntity;
import com.flyzebra.ppfunstv.utils.DisplayUtils;
import com.flyzebra.ppfunstv.utils.FlyLog;
import com.flyzebra.ppfunstv.utils.FontManager;
import com.flyzebra.ppfunstv.utils.GsonUtil;
import com.flyzebra.ppfunstv.utils.Utils;
import com.flyzebra.ppfunstv.view.EmptyInvisibleTextView;
import com.flyzebra.ppfunstv.view.LoopPlayView.ILoopPlayView;
import com.flyzebra.ppfunstv.view.LoopPlayView.LoopPlayImage2;
import com.flyzebra.ppfunstv.view.LoopPlayView.NaviForViewPager;
import com.flyzebra.ppfunstv.view.ReflectImageView;
import com.flyzebra.ppfunstv.view.TvView.CellView.CellClickAction.ActionFactory;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * 轮播图片控件
 * Created by flyzebra on 17-5-3.
 */
public class CarouselCellView2 extends TvPageItemView {
    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    private ILoopPlayView mLoopPlayView;
    private TextView textView;
//    private Handler mHander = new Handler(Looper.getMainLooper());

    private String[] mImageUrlArr;

    private ImageView imageview;

    private String refUrl;


    private int mDirection = RIGHT;
    private int mDuration = 5000;
    private Handler mHander = new Handler(Looper.getMainLooper());
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
                if (mLoopPlayView != null && getLocalVisibleRect(rect) && isTop) {
                    switch (mDirection) {
                        case LEFT:
                            ((View) mLoopPlayView).onKeyDown(KeyEvent.KEYCODE_DPAD_LEFT, null);
                            break;
                        case RIGHT:
                            ((View) mLoopPlayView).onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, null);
                            break;
                        default:
                            ((View) mLoopPlayView).onKeyDown(KeyEvent.KEYCODE_DPAD_LEFT, null);
                            break;
                    }
                }
                mHander.postDelayed(task, mDuration);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public CarouselCellView2(Context context) {
        this(context, null);
    }

    public CarouselCellView2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CarouselCellView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setClipChildren(true);
        setClipToPadding(true);
    }

    @Override
    public View setCellData(CellEntity cellEntity) {
        cellEntity.setFocusType(1);
        cellEntity.setFocusScale(1);
        super.setCellData(cellEntity);
        return this;
    }

    @Override
    public void setNextFocusLeftId(int nextFocusLeftId) {
        super.setNextFocusLeftId(getId());
    }

    @Override
    public void setNextFocusRightId(int nextFocusRightId) {
        super.setNextFocusRightId(getId());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mLoopPlayView != null) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    ((View) mLoopPlayView).onKeyDown(keyCode, event);
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    ((View) mLoopPlayView).onKeyDown(keyCode, event);
                    break;
            }
        }
        return super.onKeyDown(keyCode, event);
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
            setReflectView(refUrl);
        }
    }

    /**
     * 设置倒影控件
     *
     * @param url
     */
    private void setReflectView(final String url) {
        refUrl = url;
        if (reflectImageView == null) return;
        Glide.with(mContext)
                .asBitmap()
                .load(url)
//                .override(mCell.getWidth(), mCell.getHeight())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        showReflectView(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

    @Override
    public void showImage(final String imgUrl) {
        if (mLoopPlayView == null) {
            FlyLog.d("error don't createView.");
            return;
        }
        mLoopPlayView.notifyData(new ILoopPlayView.OnDataChanged() {
            @Override
            public void setChildViewData(ImageView imageView, int num, String url) {
                try {
                    Glide.with(mContext)
                            .load(url)
                            .placeholder(mLoadImageResId)
//                                .transform(new GlideRoundTransform(mContext, 15))
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
            if (mLoopPlayView != null) {
                int item = mLoopPlayView.getCurrentItem();
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
        imageview = null;
        mLoopPlayView = null;
        if (mCell.getSubCellList() != null) {
            mImageUrlArr = new String[mCell.getSubCellList().size()];
            for (int i = 0; i < mCell.getSubCellList().size(); i++) {
                mImageUrlArr[i] = iDiskCache.getBitmapPath(mCell.getSubCellList().get(i).getImgUrl());
            }
            mLoopPlayView = new LoopPlayImage2(mContext);
            addView((View) mLoopPlayView);

            int contentWidth = (int) (mCell.getWidth() / 1.6f);
            int contentHeight = mCell.getHeight();

            int showNum = (mCell.getShowImageNum() == 0 ? 5 : mCell.getShowImageNum());
            showNum = (showNum % 2 == 1 ? showNum : showNum + 1);

            mLoopPlayView.setImageViewUrls(mImageUrlArr)
                    .setShowImageNum(showNum)
                    .setChildViewWidth(contentWidth)
                    .setChildViewHeight(contentHeight)
                    .setChildViewPadding(0)
                    .setFirstFocusItem(showNum / 2 + 1)
                    .setMaxDuration(300)
                    .initView(mCell.getWidth(), mCell.getHeight());


            mLoopPlayView.setOnChildViewChanged(new ILoopPlayView.OnViewChangedListener() {
                @Override
                public void onViewChanged(ImageView lostView, ImageView focusView, int currentItem) {
                    showText(currentItem);
                }
            });

            int textSize = 36;

            int textColor = 0xffffffff;

            float screenScale = DisplayUtils.getMetrices((Activity) mContext).widthPixels / 1920f;

            try {
                textSize = (int) (mCell.getSubCellList().get(0).getSize() * screenScale);
                String strColor = mCell.getSubCellList().get(0).getColor();
                if (!TextUtils.isEmpty(strColor)) {
                    textColor = Color.parseColor(mCell.getSubCellList().get(0).getColor());
                }
            } catch (Exception e) {
                FlyLog.d(e.toString());
                e.printStackTrace();
            }

            textSize = (int) Math.max(textSize, 36 * screenScale);

            textView = new EmptyInvisibleTextView(mContext);
            LayoutParams lp2 = new LayoutParams(mCell.getWidth(), (int) (textSize * 1.67f));
            lp2.setMargins((mCell.getWidth() - contentWidth) / 2, (int) (mCell.getHeight() - textSize * 1.67f + 1), (mCell.getWidth() - contentWidth) / 2, 0);
            textView.setLayoutParams(lp2);
//            textView.setPadding(5, 0, 5, 0);
            textView.setTextColor(textColor);
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
//            textView.getPaint().setFakeBoldText(true);

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
            String textStr = mCell.getSubCellList().get(0).getText();
            Map str = GsonUtil.json2Map(textStr);
            textView.setText(Utils.getLocalLanguageString(str));

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
                JSONObject jsonObject = new JSONObject(mCell.getSubCellList().get(mLoopPlayView.getCurrentItem()).getIntent());
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

    @Override
    protected void setFocusRect(CellEntity cellEntity) {
        mOldRect.left = mFocusRect.left = (int) (cellEntity.getX() + (cellEntity.getWidth() - cellEntity.getWidth() / 1.6f) / 2);
        mOldRect.right = mFocusRect.left = (int) (mFocusRect.left + cellEntity.getWidth() / 1.6f);
        mOldRect.top = mFocusRect.left = cellEntity.getY();
        mOldRect.bottom = mFocusRect.left = mFocusRect.top + cellEntity.getHeight();
        mFocusRect.left = (int) (cellEntity.getX() + (cellEntity.getWidth() - cellEntity.getWidth() / 1.6f) / 2);
        mFocusRect.right = (int) (mFocusRect.left + cellEntity.getWidth() / 1.6f);
        mFocusRect.top = cellEntity.getY();
        mFocusRect.bottom = mFocusRect.top + cellEntity.getHeight();
    }
}
