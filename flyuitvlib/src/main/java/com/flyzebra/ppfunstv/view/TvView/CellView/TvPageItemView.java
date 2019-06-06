package com.flyzebra.ppfunstv.view.TvView.CellView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.flyzebra.flyui.chache.IDiskCache;
import com.flyzebra.ppfunstv.R;
import com.flyzebra.ppfunstv.data.CellEntity;
import com.flyzebra.ppfunstv.module.BitmapCache;
import com.flyzebra.ppfunstv.utils.DisplayUtils;
import com.flyzebra.ppfunstv.utils.FlyLog;
import com.flyzebra.ppfunstv.utils.FontManager;
import com.flyzebra.ppfunstv.utils.wallpaper.FastWallpaper;
import com.flyzebra.ppfunstv.view.ReflectImageView;
import com.flyzebra.ppfunstv.view.TvView.CellView.CellClickAction.ActionFactory;
import com.flyzebra.ppfunstv.view.TvView.CellView.CellClickAction.IClickEvent;
import com.flyzebra.ppfunstv.view.TvView.FocusAnimat.IAnimatView;
import com.flyzebra.ppfunstv.view.TvView.FocusAnimat.ITvFocusAnimat;

import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by pc1 on 2016/6/15.
 */
public abstract class TvPageItemView extends RelativeLayout implements ITvPageItemView, IAnimatView {
    protected Context mContext;
    protected CellEntity mCell = null;
    protected IDiskCache iDiskCache;
    protected ReflectImageView reflectImageView;
    protected IClickEvent clickEvent = null;

    public static String sDefaultBgUrl = "";
    public static String sCurrentBgrUrl = "";
    protected BitmapCache mBitmapCache;
    private AtomicBoolean isCancleSetWallpager = new AtomicBoolean(false);

    protected Rect mFocusRect = new Rect();
    protected Rect mOldRect = new Rect();

    private final static ExecutorService executors = Executors.newCachedThreadPool();
    protected ITvFocusAnimat iTvFocusAnimat = null;
    protected int mLoadImageResId = R.drawable.tv_default;
    protected int mAnimDuration = 300;
    protected boolean isUseWallPager = false;

    public TvPageItemView(Context context) {
        this(context, null);
    }

    public TvPageItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TvPageItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
    }

    @Override
    public View setCellData(CellEntity cellEntity) {
        setFocusRect(cellEntity);
        mCell = cellEntity;
        LayoutParams lp = new LayoutParams(mCell.getWidth(), mCell.getHeight());
        setLayoutParams(lp);
        initView();
        return this;
    }

    protected void setFocusRect(CellEntity cellEntity) {
        mOldRect.left = cellEntity.getX();
        mOldRect.top = cellEntity.getY();
        mOldRect.right = cellEntity.getX() + cellEntity.getWidth();
        mOldRect.bottom = cellEntity.getY() + cellEntity.getHeight();
        mFocusRect.left = cellEntity.getX();
        mFocusRect.top = cellEntity.getY();
        mFocusRect.right = cellEntity.getX() + cellEntity.getWidth();
        mFocusRect.bottom = cellEntity.getY() + cellEntity.getHeight();
    }

    @Override
    public CellEntity getCellData() {
        return mCell;
    }

    @Override
    public void setDiskCache(IDiskCache iDiskCache) {
        this.iDiskCache = iDiskCache;
    }


    @Override
    public void bindReflectView(ReflectImageView reflectImageView) {
        this.reflectImageView = reflectImageView;
    }

    @Override
    public ReflectImageView getReflectImageView() {
        return reflectImageView;
    }


    @Override
    public void setLoadImageResId(@DrawableRes int ResID) {
        this.mLoadImageResId = ResID;
    }

    @Override
    public void showImage(String imgUrl) {
        final ImageView imageView = getMyImageView();
        if (imageView == null) return;

        String filePath = iDiskCache.getBitmapPath(imgUrl);
        Glide.with(mContext)
                .asBitmap()
                .load(filePath)
                .override(mCell.getWidth(), mCell.getHeight())
                .placeholder(mLoadImageResId)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        imageView.setImageBitmap(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });

        /**
         * 显示镜像图片，用Glide拉伸图片完美显示比例
         */
        if (reflectImageView == null) return;
        Glide.with(mContext)
                .asBitmap()
                .load(filePath)
                .override(mCell.getWidth(), mCell.getHeight())
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

    /**
     * 最后一排图像显示倒影镜像
     *
     * @param resource
     */
    protected void showReflectView(Bitmap resource) {
        if (reflectImageView != null) {
            reflectImageView.showRefImage(resource);
        }
    }

    /**
     * 点击确认按键后要执行的操作
     */
    @Override
    public void doAction() {
        doAction(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    /**
     * 点击确认按键后要执行的操作
     */
    @Override
    public void doAction(int flag) {
        if (clickEvent == null) {
            clickEvent = ActionFactory.create(mContext, mCell);
        }
        if (clickEvent != null) {
            clickEvent.doAction(flag);
        }
    }

    public abstract void initView();

    @Override
    public abstract ImageView getMyImageView();

    @Override
    public String getPackName() {
        String packageName = "";
        try {
            if (mCell != null) {
                JSONObject jsonObject = new JSONObject(mCell.getIntent());
                packageName = jsonObject.getString("packageName");
            }
        } catch (Exception e) {
            FlyLog.d("get app packageName failed!");
        }
        return packageName;
    }

    @Override
    public void setBitmapCache(BitmapCache bitmapCache) {
        this.mBitmapCache = bitmapCache;
    }


    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        //移动焦点框框
        startFocusAnimat(gainFocus);

        //设置背景图片
        if (!(this instanceof StateListCellView)) {
            setFastWallPer(gainFocus);
        }

    }

    private void startFocusAnimat(boolean gainFocus) {
        if (iTvFocusAnimat == null) return;
        if (gainFocus) {
            iTvFocusAnimat.startAnim(this, true);
        }
    }

    protected void setFastWallPer(boolean gainFocus) {
        if(!isUseWallPager) return;
        if (gainFocus) {
            String mCellImgUrlBg = mCell.getImgUrlBg();
            if (TextUtils.isEmpty(mCellImgUrlBg)) {
                mCellImgUrlBg = sDefaultBgUrl;
                FlyLog.d("mCellImgUrlBg is empty,set mCellImgUrlBg to: " + mCellImgUrlBg);
            }
            final String imageUrl = mCellImgUrlBg;
            FlyLog.d("setFastWallPer:imageUrl: " + imageUrl);
            FlyLog.d("setFastWallPer:sDefaultBgUrl: " + sDefaultBgUrl);
            FlyLog.d("setFastWallPer:sCurrentBgrUrl: " + sCurrentBgrUrl);
            isCancleSetWallpager.set(false);
            executors.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(mAnimDuration+50);
                        if (!imageUrl.equals(sCurrentBgrUrl) && !isCancleSetWallpager.get()) {
                            sCurrentBgrUrl = imageUrl;
                            if(Runtime.getRuntime().freeMemory()>1024*1024*8) {
                                Bitmap bitmap = BitmapCache.createBitmapFromLocal(mContext, imageUrl);
                                if (bitmap != null) {
                                    bitmap = Bitmap.createScaledBitmap(bitmap,
                                            DisplayUtils.getMetrices((Activity) mContext).widthPixels,
                                            DisplayUtils.getMetrices((Activity) mContext).heightPixels,
                                            true);
                                    if (!isCancleSetWallpager.get()) {
                                    FastWallpaper.getInstance().setBitmapAlphaAnimation(bitmap, 1);
//                                        FastWallpaper.getInstance().setBitmap(bitmap);
                                    }
                                }
                            }else{
                                FlyLog.d("freeMemory = "+Runtime.getRuntime().freeMemory());
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        FlyLog.d("TvPageItemView->setFastWallPer", e.toString());
                    }
                }
            });
        } else {
            isCancleSetWallpager.set(true);
        }
    }

    @Override
    public void isUseWallPager(boolean isUseWallPager) {
        this.isUseWallPager = isUseWallPager;
    }

    @Override
    public void setFocusAnimate(ITvFocusAnimat iTvFocusAnimat) {
        this.iTvFocusAnimat = iTvFocusAnimat;
    }

    @Override
    public Rect getFocusRect() {
        return mFocusRect;
    }


    @Override
    public Rect getOldRect() {
        return mOldRect;
    }

    @Override
    public int getFocusZorder() {
        if (mCell != null) {
            return mCell.getFocusZorder();
        } else {
            return 0;
        }
    }

    @Override
    public int getFocusScale() {
        if (mCell != null) {
            return mCell.getFocusScale();
        } else {
            return 0;
        }
    }

    @Override
    public int getFocusType() {
        if (mCell != null) {
            return mCell.getFocusType();
        } else {
            return 0;
        }
    }


    protected void setTextEffect(TextView view) {
        try {
            //TODO 字体处理
            //设置字体
            if (!TextUtils.isEmpty(mCell.getFont())) {
                view.setTypeface(FontManager.getTypefaceByFontName(mContext, mCell.getFont()));
            }
            //设置文字大小
             if (mCell.getSize() > 15) {
                view.setTextSize(TypedValue.COMPLEX_UNIT_PX,mCell.getSize());
            }
            //设置字体颜色
            if (!TextUtils.isEmpty(mCell.getColor())) {
                view.setTextColor(Color.parseColor(mCell.getColor()));
            } else {
                view.setTextColor(Color.WHITE);
            }
        } catch (Exception e) {
            FlyLog.e(e.toString());
        }
    }

    @Override
    public void setAnimtorDurtion(int durtion) {
        this.mAnimDuration = durtion;
    }
}
