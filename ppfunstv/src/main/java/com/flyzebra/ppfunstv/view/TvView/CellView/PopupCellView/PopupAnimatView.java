package com.flyzebra.ppfunstv.view.TvView.CellView.PopupCellView;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.flyzebra.ppfunstv.R;
import com.flyzebra.ppfunstv.data.CellEntity;
import com.flyzebra.ppfunstv.data.PopupBitmapEntity;
import com.flyzebra.ppfunstv.utils.FlyLog;
import com.flyzebra.ppfunstv.utils.GsonUtil;
import com.flyzebra.ppfunstv.utils.Utils;
import com.flyzebra.ppfunstv.view.TvView.CellView.CellClickAction.DefaultAction;
import com.flyzebra.ppfunstv.view.TvView.CellView.CellType;
import com.flyzebra.ppfunstv.view.TvView.CellView.TvPageItemView;
import com.flyzebra.ppfunstv.view.TvView.FocusAnimat.IAnimatView;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by FlyZebra on 2016/8/2.
 * 自定义控件，包含以下功能：
 * 1.控件可缩放改变大小。
 * 2.周围能动画弹出小图片。
 * 3.中间用自定义控件实现特殊ImageView效果。
 * 4.底部用TextView和ImageView实现菜单。
 */
public class PopupAnimatView extends TvPageItemView implements IPopupAnimatView, View.OnFocusChangeListener {
    private static int USER_ID = 0x5f000000;
    private Context context;
    private int finalX;
    private int finalY;
    private int finalW;
    private int finalH;

    private CenterImageView mCenterImageView;//中间图像(一张图片和一张背景合成)
    private String[] mPopupBitmapUrls;//播放动画的小图片的网络获取地址
    private int mPopupBitmapWidth = 165;
    private int mPopupBitmapHeight = 217;
    private PopupBitmapEntity[] mPopupBitmapEndRects;
    private PopupBitmapEntity[] mPopupBitmapStartRects;
    private PopupBitmapEntity[] mPopupBitmapCurrentRects;

    private List<TextView> textViewList = new ArrayList<>();//
    private List<ImageView> imageViewList = new ArrayList<>();

    private Paint bitmap_paint1;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private static ExecutorService executors = Executors.newCachedThreadPool();
    private float maxScale = 2.0f;
    private float minScale = 1.0f;

    private boolean isDownAnimta = false;
    private Hashtable<String, Bitmap> mPopupBitmapTable = new Hashtable<>();

    private PlayZoomAnimtaTask zoomAnimtaTask;
    private AtomicBoolean isZoomAnimtaTaskRunnning = new AtomicBoolean(false);
    private AtomicBoolean isZoomIn = new AtomicBoolean(false);
    private OnSizeZoom onSizeZoom;
    private int mZoomAnimtaCount = 0;//缩放过程进度计数


    private PopupBitmapAnimationTask mPopupBitmapAnimationTask;
    private AtomicBoolean isPopupBitmapAnimatTaskRunnning = new AtomicBoolean(false);
    /**
     * 动画开始放大显示小图片
     */
    private boolean isShowPopupBitmap = false;

    /**
     * 动画实际已进行的帧数
     */
    private int mActualAnimatedFrame = 0;

    /**
     * 如果该控件任一子控件有焦点则为true,否则为false
     */
    private boolean isLostFocus = true;
    //    private boolean isHasFocus = false; //当前控件是否获的焦点

    /**
     * 屏幕分辨率setChildTextViews适配
     */
    private float screenScale = 1.0f;
    private float scaleW = 1.0f;
    private float scaleH = 1.0f;


    private int textViewHight = 54;
    private int textViewSize = 30;
    private int roundPix = 30;
    private int verticalPadding = 18;//垂直排列的控件的间距
    private int textViewWidth;
    private int minSize = 34;
    private OnClickListener onClickListener;


    //最大子组件数
    private int MAX_CHILD_TEXT = 3;
    private int MAX_CHILD_IMAGE = 6;


    public PopupAnimatView(Context context) {
        this(context, null);
    }

    public PopupAnimatView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        setClipChildren(true);
        setClipToPadding(true);
    }

    public ImageView getCenterImageView() {
        return mCenterImageView;
    }

    public CellEntity getCellEntity() {
        return mCell;
    }

    @Override
    public void doAction() {
        new DefaultAction(mContext, mCell).doAction();
    }

    @Override
    public void initView() {

    }

    public void setMaxScale(float maxScale) {
        this.maxScale = maxScale;
    }

    public void setMinScale(float minScale) {
        this.minScale = minScale;
    }

    public int getFinalY() {
        return finalY;
    }

    public void setFinalY(int finalY) {
        this.finalY = finalY;
    }

    public int getFinalX() {
        return finalX;
    }

    public void setFinalX(int finalX) {
        this.finalX = finalX;
    }

    public int getFinalW() {
        return finalW;
    }

    public void setFinalW(int finalW) {
        this.finalW = finalW;
    }

    public int getFinalH() {
        return finalH;
    }

    public void setFinalH(int finalH) {
        this.finalH = finalH;
    }

    public List<TextView> getTextViewList() {
        return textViewList;
    }

    public void setTextViewList(List<TextView> textViewList) {
        this.textViewList = textViewList;
    }

    public List<ImageView> getImageViewList() {
        return imageViewList;
    }

    public void setImageViewList(List<ImageView> imageViewList) {
        this.imageViewList = imageViewList;
    }

    public boolean isLostFocus() {
        return isLostFocus;
    }

    public void setLostFocus(boolean lostFocus) {
        isLostFocus = lostFocus;
    }

    public int getVerticalPadding() {
        return verticalPadding;
    }

    public void setVerticalPadding(int verticalPadding) {
        this.verticalPadding = verticalPadding;
    }

    public int getTextViewWidth() {
        return textViewWidth;
    }

    public void setTextViewWidth(int textViewWidth) {
        this.textViewWidth = textViewWidth;
    }

    public int getTextViewHight() {
        return textViewHight;
    }

    public void setTextViewHight(int textViewHight) {
        this.textViewHight = textViewHight;
    }

    private void init(Context context) {
        this.context = context;

        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenScale = dm.widthPixels / 1920f;

        //初始值适配分辩率
        textViewHight = (int) (textViewHight * screenScale);
        textViewSize = (int) (textViewSize * screenScale);
//        textViewWidth = (int) (textViewWidth *screenScale);
        verticalPadding = (int) (verticalPadding * screenScale);
        roundPix = (int) (roundPix * screenScale);

        mPopupBitmapWidth = (int) (mPopupBitmapWidth * screenScale);
        mPopupBitmapHeight = (int) (mPopupBitmapHeight * screenScale);

        bitmap_paint1 = new Paint();
        bitmap_paint1.setColor(0xff000000);
        bitmap_paint1.setAntiAlias(true);
        bitmap_paint1.setStyle(Paint.Style.FILL);
        setWillNotDraw(false);
//        this.setGravity(Gravity.CENTER);
        setFocusable(false);
        setFocusableInTouchMode(false);
    }


    /**
     * 设置中间图片的请求地址
     *
     * @param url
     */
    @Override
    public IPopupAnimatView setCenterImage(@NonNull String url) {
        if (mCenterImageView != null) {
            this.removeView(mCenterImageView);
        }
        mCenterImageView = new CenterImageView(context);
        mCenterImageView.setCellData(mCell);
        LayoutParams lp = new LayoutParams(mCell.getWidth(), mCell.getHeight());
        mCenterImageView.setLayoutParams(lp);
        mCenterImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        if (mCell.getSize() > minSize) {
            mCenterImageView.setTextSize(mCell.getSize());
        } else {
            mCenterImageView.setTextSize(minSize);
        }

        mCenterImageView.setFocusable(true);
        mCenterImageView.setFocusableInTouchMode(true);
//        mCenterImageView.setText(Utils.getLocalLanguageString(GsonUtils.json2Map(mCell.getText())));
        this.addView(mCenterImageView);

        mCenterImageView.setId(USER_ID + mCell.getX() * 1080 + mCell.getY() - 1);
        mCenterImageView.setNextFocusDownId(mCenterImageView.getId());
//        mCenterImageView.setAlpha(128);
        mCenterImageView.setOnFocusChangeListener(this);
        mCenterImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onClick(PopupAnimatView.this);
            }
        });
        return this;
    }

    /**
     * 设置控件底部文字菜单信息
     *
     * @param subCellList
     */
    @Override
    public IPopupAnimatView setChildTextViews(List<CellEntity> subCellList) {
        if (!textViewList.isEmpty()) {
            removeViewList(textViewList);
        }
        textViewList.clear();
        textViewWidth = (finalW - verticalPadding * 2);
        int count = 0;
        for (int i = 6; i < subCellList.size(); i++) {
            final CellEntity cell = subCellList.get(i);
            if (cell.getType() == CellType.TYPE_IMAGE || cell.getType() == CellType.TYPE_RECENT_APP) {
                continue;
            }
            final ChildTextView tv = new ChildTextView(context);
            LayoutParams lp = new LayoutParams(textViewWidth, textViewHight);
            tv.setmWidth(textViewWidth);
            tv.setHeight(textViewHight);
            tv.setmPadding(roundPix);
//            tv.setFinalHeight(textViewHight);
            tv.setLayoutParams(lp);
            tv.getPaint().setAntiAlias(true);
            tv.setBackgroundResource(R.drawable.tv_rectangle);
            tv.setPadding(roundPix, (int) (4 * screenScale), 0, 0);
            Map mLangMap = GsonUtil.json2Map(cell.getText());
            String text = null;
            if (mLangMap != null) {
                text = Utils.getLocalLanguageString(mLangMap);
            } else {
                text = cell.getText();
            }
            tv.setText(text);
            tv.setGravity(Gravity.LEFT);
            tv.setSingleLine(true);
            tv.setAlpha(0);
            tv.setTag(count);
            int mTextColor = 0xffffffff;
            try {
                mTextColor = Color.parseColor(mCell.getColor());
            } catch (Exception e) {
                FlyLog.d("parseColor error set defualt color = %d", mTextColor);
                mTextColor = 0xffffffff;
                e.printStackTrace();
            }
            tv.setTextColor(mTextColor);
            if (cell.getSize() != 0) {
                textViewSize = (int) (cell.getSize() * screenScale);
            }
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX,textViewSize);
            tv.setCellData(cell);
            this.addView(tv);
            tv.setId(USER_ID + mCell.getX() * 1080 + mCell.getY() + i);
            tv.setNextFocusDownId(tv.getId());
            tv.setFocusable(true);
            tv.setFocusableInTouchMode(true);
            textViewList.add(tv);
            tv.setOnFocusChangeListener(this);
            tv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    new DefaultAction(context, cell.getAction(), false).doAction();
                }
            });
            count++;
            //最多添加三个此类控件
            if (count >= MAX_CHILD_TEXT) {
                break;
            }
        }
        return this;

    }

    @Override
    public IPopupAnimatView setChildImageViews(@NonNull List<CellEntity> subCellList) {
        if (!imageViewList.isEmpty()) {
            removeViewList(imageViewList);
        }
        imageViewList.clear();

        int count = 0;
        for (int i = 6; i < subCellList.size(); i++) {
            final CellEntity cell = subCellList.get(i);
            if (cell.getType() == CellType.TYPE_IMAGE || cell.getType() == CellType.TYPE_RECENT_APP) {
                FlyLog.d("add child imageView");
                ChildImageView iv = null;
                if (cell.getType() == CellType.TYPE_RECENT_APP) {
                    iv = new RecentAppCellImageView(context);
                } else {
                    iv = new ChildImageView(context);
                }
                LayoutParams lp = new LayoutParams(finalW - verticalPadding * 2, (textViewHight - verticalPadding * 4) / 3);
                iv.setLayoutParams(lp);
                iv.setScaleType(ImageView.ScaleType.FIT_XY);
                iv.setAlpha(0);
                this.addView(iv);
                iv.setFocusable(true);
                iv.setFocusableInTouchMode(true);
                iv.setNum(count);
                iv.setId(USER_ID + mCell.getX() * 1080 + mCell.getY() + i * 0xff);
                iv.setNextFocusDownId(iv.getId());
                iv.setCellEntity(cell);
                iv.setiDiskCache(iDiskCache);
                imageViewList.add(iv);
                iv.setOnFocusChangeListener(this);
                iv.showImage();
                count++;
                //最多添加六个此类控件
                if (count >= MAX_CHILD_IMAGE) {
                    break;
                }
            }
        }
        return this;
    }

    /**
     * 设置控件底部文字菜单信息
     *
     * @param str
     */
    @Override
    public IPopupAnimatView setChildTextViews(@NonNull String[] str) {
        if (!textViewList.isEmpty()) {
            removeViewList(textViewList);
        }
        textViewList.clear();
        textViewWidth = (finalW - verticalPadding * 2);
        for (int i = 0; i < Math.min(MAX_CHILD_TEXT, str.length); i++) {
            final ChildTextView tv = new ChildTextView(context);
            LayoutParams lp = new LayoutParams(textViewWidth, LayoutParams.WRAP_CONTENT);
            tv.setLayoutParams(lp);
            tv.getPaint().setAntiAlias(true);
            tv.setText(str[i]);
            tv.setAlpha(0);
            tv.setTag(i);
            tv.setTextColor(0xffffffff);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX,textViewSize);
            tv.setLineSpacing(1.0f, 1.2f);
            this.addView(tv);
            textViewList.add(tv);
            tv.setOnFocusChangeListener(this);
        }
        return this;
    }

    /**
     * 设置控件底部图像菜单信息
     *
     * @param str
     */
    @Override
    public IPopupAnimatView setChildImageViews(@NonNull String[] str) {
        if (!imageViewList.isEmpty()) {
            removeViewList(imageViewList);
        }
        imageViewList.clear();

        for (int i = 0; i < Math.min(MAX_CHILD_IMAGE, str.length); i++) {
            final ChildImageView iv = new ChildImageView(context);
            LayoutParams lp = new LayoutParams(finalW - verticalPadding * 2, (textViewHight - verticalPadding * 4) / 3);
            iv.setLayoutParams(lp);
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            iv.setAlpha(0);
            this.addView(iv);
            iv.setFocusable(true);
            iv.setFocusableInTouchMode(true);
            iv.setNum(i);
            imageViewList.add(iv);
            Glide.with(context)
                    .load(str[i])
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
//                    .transform(new GlideRoundTransform(context, roundPix))
                    .into(iv);

            iv.setOnFocusChangeListener(this);
        }
        return this;
    }

    /**
     * 设置播放动画的图片(周围的小图片)的请求地址，此接口无需定义弹出的图片的位置，Hard
     *
     * @param urls
     */
    @Override
    public IPopupAnimatView setPopupBitmapUrls(@NonNull String[] urls) {
        bitmap_paint1.setAlpha(255);
        mPopupBitmapUrls = new String[urls.length];
        System.arraycopy(urls, 0, mPopupBitmapUrls, 0, urls.length);
        for (int i = 0; i < mPopupBitmapUrls.length; i++) {
            final String key = mPopupBitmapUrls[i];
            final String url = iDiskCache.getBitmapPath(mPopupBitmapUrls[i]);
            Glide.with(context)
                    .asBitmap()
                    .load(url)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull final Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {

                                    Bitmap bitmap = Bitmap.createScaledBitmap(resource, mPopupBitmapWidth, mPopupBitmapHeight, true);
                                    mPopupBitmapTable.put(key, bitmap);
                                }
                            }).start();
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }
                    });
        }
        return this;
    }

    /**
     * 设置动态布局解析出来的值
     *
     * @param cellEntity
     */
    @Override
    public View setCellData(@NonNull CellEntity cellEntity) {
        cellEntity.setFocusScale(1);
        super.setCellData(cellEntity);
        finalX = cellEntity.getX();
        finalY = cellEntity.getY();
        finalW = cellEntity.getWidth();
        finalH = cellEntity.getHeight();

        //中间显示图片
        setCenterImage(cellEntity.getImgUrl());

        //初始化弹出图片
        if (cellEntity.getSubCellList() != null && !cellEntity.getSubCellList().isEmpty()) {
            String imgurlArr[] = new String[Math.min(cellEntity.getSubCellList().size(), 6)];
            for (int i = 0; i < imgurlArr.length; i++) {
                imgurlArr[i] = cellEntity.getSubCellList().get(i).getImgUrl();
            }
            setPopupBitmapUrls(imgurlArr);
        } else {

            String str = "youxi";
            if (cellEntity.getText().contains("TV名师")) {
                str = "shaoer";
            } else if (cellEntity.getText().contains("游戏中心")) {
                str = "youxi";
            } else if (cellEntity.getText().contains("芒果TV")) {
                str = "yingyong";
            } else if (cellEntity.getText().contains("苏宁618年中庆")) {
                str = "gouwu";
            } else if (cellEntity.getText().contains("音悦台")) {
                str = "music";
            } else if (cellEntity.getText().contains("阳光政务")) {
                str = "zhengwu";
            }
            setPopupBitmapUrls(new String[]{
                    "file:///android_asset/testimg/" + str + "1.png",
                    "file:///android_asset/testimg/" + str + "2.png",
                    "file:///android_asset/testimg/" + str + "3.png",
                    "file:///android_asset/testimg/" + str + "4.png",
                    "file:///android_asset/testimg/" + str + "5.png",
                    "file:///android_asset/testimg/" + str + "6.png"});
        }

        //底部文字，小图片//如果配置子项超过6项，底部可获焦，否则底部不可获焦只显示文字信息
        if (cellEntity.getSubCellList() != null && (cellEntity.getSubCellList().size() > 6)) {
            setChildTextViews(cellEntity.getSubCellList());
            setChildImageViews(cellEntity.getSubCellList());
        } else {
            Map mLangMap = GsonUtil.json2Map(cellEntity.getText());
            String text = null;
            if (mLangMap != null) {
                text = Utils.getLocalLanguageString(mLangMap);
            } else {
                text = cellEntity.getText();
            }
            setChildTextViews(new String[]{text});
            textViewHight = mCell.getHeight();
        }


        initChildBitmapPoint();

        return this;
    }

    /**
     * 设置焦点顺序
     */
    private void setFocusOrder() {
        if (mCell.getSubCellList() != null && (mCell.getSubCellList().size() > 6)) {
            //中间图片焦点
            if (textViewList.isEmpty() && imageViewList.isEmpty()) {
                mCenterImageView.setNextFocusDownId(mCenterImageView.getId());
            } else if (textViewList.isEmpty()) {
                mCenterImageView.setNextFocusDownId(imageViewList.get(0).getId());
            } else {
                mCenterImageView.setNextFocusDownId(textViewList.get(0).getId());
            }

            //底部TextView焦点
            if (textViewList.size() > 0) {
                for (int i = 0; i < textViewList.size(); i++) {
                    textViewList.get(i).setNextFocusDownId(textViewList.get(Math.min(textViewList.size() - 1, i + 1)).getId());
                }
            }

            //底部小图像焦点
            if (imageViewList.size() > 0) {
                if (textViewList.size() > 0) {
                    textViewList.get(textViewList.size() - 1).setNextFocusDownId(imageViewList.get(0).getId());
                }
                for (int i = 0; i < imageViewList.size(); i++) {
                    imageViewList.get(i).setNextFocusDownId(imageViewList.get(i).getId());
                }
            }
        } else {
            mCenterImageView.setNextFocusDownId(mCenterImageView.getId());
        }
    }


    @Override
    public IPopupAnimatView initChildBitmapPoint() {
        //如果没有设置图片位置 ，初始化图片默认位置
        if (mPopupBitmapStartRects == null) {
            mPopupBitmapStartRects = new PopupBitmapEntity[]{
                    new PopupBitmapEntity(finalW - mPopupBitmapWidth / 2, finalH - mPopupBitmapHeight, "", 1.04f, 0.5f, 0),
                    new PopupBitmapEntity(finalW - mPopupBitmapWidth / 2, finalH - mPopupBitmapHeight, "", 1.10f, 0.4f, 0.1f),
                    new PopupBitmapEntity(finalW - mPopupBitmapWidth / 2, finalH - mPopupBitmapHeight, "", 1.07f, 0.3f, 0.2f),
                    new PopupBitmapEntity(finalW - mPopupBitmapWidth / 2, finalH - mPopupBitmapHeight, "", 1.08f, 0.2f, 0.3f),
                    new PopupBitmapEntity(finalW - mPopupBitmapWidth / 2, finalH - mPopupBitmapHeight, "", 1.10f, 0.1f, 0.4f),
                    new PopupBitmapEntity(finalW - mPopupBitmapWidth / 2, finalH - mPopupBitmapHeight, "", 1.10f, 0, 0.5f)
            };
            mPopupBitmapCurrentRects = new PopupBitmapEntity[]{
                    new PopupBitmapEntity(finalW - mPopupBitmapWidth / 2, finalH - mPopupBitmapHeight, "", 1.04f),
                    new PopupBitmapEntity(finalW - mPopupBitmapWidth / 2, finalH - mPopupBitmapHeight, "", 1.10f),
                    new PopupBitmapEntity(finalW - mPopupBitmapWidth / 2, finalH - mPopupBitmapHeight, "", 1.07f),
                    new PopupBitmapEntity(finalW - mPopupBitmapWidth / 2, finalH - mPopupBitmapHeight, "", 1.08f),
                    new PopupBitmapEntity(finalW - mPopupBitmapWidth / 2, finalH - mPopupBitmapHeight, "", 1.10f),
                    new PopupBitmapEntity(finalW - mPopupBitmapWidth / 2, finalH - mPopupBitmapHeight, "", 1.10f)
            };
        }
        if (mPopupBitmapEndRects == null) {
            float marginHorizontal1 = 30;
            float marginHorizontal2 = 55;
            float marginTop1 = 0.5f;
            float marginTop2 = 0.6f;

            mPopupBitmapEndRects = new PopupBitmapEntity[]{
                    new PopupBitmapEntity(
                            (int) ((finalW * maxScale - 2 * mPopupBitmapWidth) / 2),
                            (int) (mPopupBitmapHeight * marginTop1),
                            "", 1.0f
                    ),
                    new PopupBitmapEntity(
                            (int) ((finalW * maxScale - 2 * mPopupBitmapWidth) / 2) + mPopupBitmapWidth,
                            (int) (mPopupBitmapHeight * marginTop2),
                            "", 1.0f
                    ),
                    new PopupBitmapEntity(
                            (int) (marginHorizontal1 * screenScale),
                            (int) (mPopupBitmapHeight * 1.1f),
                            "", 1.0f
                    ),
                    new PopupBitmapEntity(
                            (int) (finalW * maxScale - marginHorizontal1 * screenScale - mPopupBitmapWidth),
                            (int) (mPopupBitmapHeight * 1.2f),
                            "", 1.0f
                    ),
                    new PopupBitmapEntity(
                            (int) (marginHorizontal2 * screenScale),
                            (int) (mPopupBitmapHeight * 2.1f),
                            "", 1.0f
                    ),
                    new PopupBitmapEntity(
                            (int) (finalW * maxScale - marginHorizontal2 * screenScale - mPopupBitmapWidth),
                            (int) (mPopupBitmapHeight * 2.2f),
                            "", 1.0f
                    )
            };
        }
        return this;
    }

    @Override
    public ImageView getMyImageView() {
        return getCenterImageView();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        FlyLog.d("onFocusChange hasFocus = " + hasFocus + ",view =" + v);

        //计算焦点位置
        if (hasFocus) {
            if (v instanceof CenterImageView) {
                mFocusRect.left = finalX;
                mFocusRect.right = finalX + finalW;
                mFocusRect.top = finalY;
                mFocusRect.bottom = finalY + finalH;
            } else if (v instanceof ChildTextView) {
                mFocusRect.left = finalX + verticalPadding;
                mFocusRect.right = finalX + finalW - verticalPadding;
                mFocusRect.top = finalY + finalH + verticalPadding + ((int) v.getTag()) * (textViewHight + verticalPadding);
                mFocusRect.bottom = mFocusRect.top + textViewHight;
            } else if (v instanceof ChildImageView) {
                int num = ((ChildImageView) v).getNum();
                int width = (finalW - verticalPadding * 4) / 3;
                mFocusRect.left = finalX + verticalPadding + (width + verticalPadding) * (num % 3);
                mFocusRect.right = mFocusRect.left + width;
                int addtop = textViewList.size() * (verticalPadding + textViewHight);
                mFocusRect.top = finalY + finalH + addtop + verticalPadding + (verticalPadding + width) * (num / 3);
                mFocusRect.bottom = mFocusRect.top + width;
            }
        }


        if (onSelectListener != null) {
            onSelectListener.onSelectListener(this, v, hasFocus, isLostFocus);
        }

        //如果是首次获的焦点需要切换背景图片
        if(isLostFocus){
            setFastWallPer(hasFocus);
        }

        if(hasFocus){
            if(iTvFocusAnimat!=null){
                iTvFocusAnimat.startAnim((IAnimatView) v,isLostFocus);
            }
        }

        if (hasFocus) {
            isLostFocus = false;
        }


    }

    /**
     * 控件放大过程动画的控制实现线程
     */
    private class PlayZoomAnimtaTask implements Runnable {
        private int duration;//播放完成一个动画所需要的总时间
        private static final int ONCE_TIME = 20;//执行一帧动画的间隔时间

        public PlayZoomAnimtaTask(int duration) {
            this.duration = duration;
        }

        @Override
        public void run() {
            FlyLog.d("<PopupAnimatView>PlayZoomAnimtaTask --PlayZoomAnimtaTask Start--" + getTag());
            isZoomAnimtaTaskRunnning.set(true);
            final float setpWidth = Math.round(finalW * maxScale - finalW * minScale) / ((duration / 2) / (float) ONCE_TIME);
            final float setpHeight = Math.round(finalH * maxScale - finalH * minScale) / ((duration / 2) / (float) ONCE_TIME);
            for (; ; ) {
                if (isZoomIn.get()) {
//                    FlyLog.d("<PopupAnimatView>PlayZoomAnimtaTask --ZooM In Start--" + getTag());
                    mZoomAnimtaCount++;
                    int centerViewX = (int) (mZoomAnimtaCount * setpWidth / 2);
                    int centerViewY = (int) (mZoomAnimtaCount * setpHeight / 2);
                    int cx = finalX - centerViewX;
                    int cy = finalY - centerViewY;
                    int cwidth = (int) Math.min(finalW * maxScale, finalW + centerViewX * 2);
                    int cheight = (int) Math.min(finalH * maxScale, finalH + centerViewY * 2);
                    if (cwidth >= ((int) (finalW * maxScale))) {
                        //校验图像位置
                        centerViewX = (int) ((finalW * maxScale - finalW) / 2);
                        centerViewY = (int) ((finalH * maxScale - finalH) / 2);
                        cx = finalX - centerViewX;
                        cy = finalY - centerViewY;
                    }
                    mCell.setX(cx);
                    mCell.setY(cy);
                    mCell.setWidth(cwidth);
                    mCell.setHeight(cheight);

                    if (cwidth >= ((int) (finalW * maxScale))) {
                        FlyLog.d("<PopupAnimatView>PlayZoomAnimtaTask --ZooM In END--" + getTag());
                        //放大结束，执行显示弹出图片动画
                        isDownAnimta = true;
                        if (mPopupBitmapAnimationTask == null) {
                            mPopupBitmapAnimationTask = new PopupBitmapAnimationTask(duration);
                        }
                        isShowPopupBitmap = true;
                        if (!isPopupBitmapAnimatTaskRunnning.get()) {
                            executors.execute(mPopupBitmapAnimationTask);
                            //中间图片背景动画
                            mCenterImageView.playBackAnimat(duration);
                        }
                        try {
                            Thread.sleep(ONCE_TIME);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        //结束当前动画循环
                        if (isZoomIn.get()) {
                            break;
                        }
                    } else {
                        try {
                            Thread.sleep(ONCE_TIME);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    mZoomAnimtaCount--;
                    int centerViewX = (int) (mZoomAnimtaCount * setpWidth / 2);
                    int centerViewY = (int) (mZoomAnimtaCount * setpHeight / 2);
                    int cx = finalX - centerViewX;
                    int cy = finalY - centerViewY;
                    int cwidth = (int) Math.max(finalW * minScale, finalW + centerViewX * 2);
                    int cheight = (int) Math.max(finalH * minScale, finalH + centerViewY * 2);
                    if (cwidth <= finalW) {
                        cx = finalX;
                        cy = finalY;
                        isDownAnimta = false;
                        mZoomAnimtaCount = 0;
                    }
                    mCell.setX(cx);
                    mCell.setY(cy);
                    mCell.setWidth(cwidth);
                    mCell.setHeight(cheight);

                    if (cwidth <= finalW) {
                        FlyLog.d("<PopupAnimatView>PlayZoomAnimtaTask --ZooM Out END--" + getTag());
                        isDownAnimta = false;
                        try {
                            Thread.sleep(ONCE_TIME);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (!isZoomIn.get()) {
                            break;
                        }
                    } else {
                        try {
                            Thread.sleep(ONCE_TIME);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (onSizeZoom != null) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            onSizeZoom.onSizeZoom(PopupAnimatView.this, isZoomIn.get());
                        }
                    });
                }
            }
            isZoomAnimtaTaskRunnning.set(false);
            if (onSizeZoom != null) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        onSizeZoom.onSizeZoom(PopupAnimatView.this, isZoomIn.get());
                    }
                });
            }
            FlyLog.d("<PopupAnimatView>PlayZoomAnimtaTask --PlayZoomAnimtaTask End--" + getTag());
        }
    }


    /**
     * 隐藏和显示的子控件动画过程的控制线程
     */
    private class PopupBitmapAnimationTask implements Runnable {
        private int duration;//播放完成一个动画所需要的总时间
        private static final int ONCE_TIME = (int) (1f / 60 * 1000);//执行一帧动画的间隔时间

        private Interpolator mInterpolator;

        PopupBitmapAnimationTask(int duration) {
            this.duration = duration;
            prepare();
        }


        private float[] mDelayAfter;
        private float[] mDelayBefore;

        private void prepare() {
            mInterpolator = new OvershootInterpolator();
            mDelayAfter = new float[mPopupBitmapStartRects.length];
            mDelayBefore = new float[mPopupBitmapStartRects.length];
            for (int i = 0; i < mDelayAfter.length; i++) {
                float delayBefore = mPopupBitmapStartRects[i].getDelayBefore();
                float delayAfter = mPopupBitmapStartRects[i].getDelayAfter();

                mDelayBefore[i] = delayBefore < 1f ? delayBefore : 1f;
                mDelayAfter[i] = delayAfter < 1f ? delayAfter : 1f;

                if (mDelayAfter[i] + mDelayBefore[i] > 1) {
                    mDelayAfter[i] = 1 - mDelayBefore[i];
                }
            }
        }

        /**
         * 计算结合 起始延时/结束延时 动画的实际进度
         *
         * @param index        小图片索引
         * @param currentFrame 当前已进行帧数
         * @return 动画实际进度(0~1)
         */
        private float getRate(int index, int currentFrame) {
            float result;
            float currentTime = currentFrame * ONCE_TIME;
            try {
                float delayAfterDuration = mDelayAfter[index] * duration;
                float delayBeforeDuration = mDelayBefore[index] * duration;
                float realDuration = duration - delayAfterDuration - delayBeforeDuration;

                if (realDuration <= 0) {
                    result = currentTime / duration;
                } else {
                    float startTime = delayBeforeDuration;
                    float endTime = duration - delayAfterDuration;

                    if (currentTime <= startTime) {
                        result = 0;
                    } else if (currentTime >= endTime) {
                        result = 1;
                    } else {
                        result = (currentTime - startTime) / realDuration;
                    }
                }
            } catch (Exception pE) {
                pE.printStackTrace();
                result = currentTime / duration;
            }


            return result;
        }


        @Override
        public synchronized void run() {
            isPopupBitmapAnimatTaskRunnning.set(true);

            if (isShowPopupBitmap) {
                mActualAnimatedFrame = 0;
            }
            int totalFrames = (int) Math.ceil(duration / ONCE_TIME);
            float rate;
            for (int frame = 1; frame <= totalFrames; frame++) {
//                Log.v(TAG, "frame: " + frame + "          " + "totalFrames: " + totalFrames + "         " + "mActualAnimatedFrame: " + mActualAnimatedFrame + "         " + (isShowPopupBitmap ? "彈出" : "收起"));
                if (isShowPopupBitmap) {
                    /*
                    显示小图片
                     */
                    mActualAnimatedFrame = frame; //统计当前已作动画的帧数
                    rate = frame * 1f / totalFrames; //当前动画进度


                    //小图片播放动画
                    for (int i = 0; i < mPopupBitmapCurrentRects.length; i++) {
                        int dx = mPopupBitmapEndRects[i].getX() - mPopupBitmapStartRects[i].getX();
                        int dy = mPopupBitmapEndRects[i].getY() - mPopupBitmapStartRects[i].getY();
//                        Log.d(TAG, "dx: " + dx * mInterpolator.getInterpolation(getRate(i, frame)) + "          " + "dy: " + dy * mInterpolator.getInterpolation(rate));
                        float r = getRate(i, frame);
                        mPopupBitmapCurrentRects[i].setX(
                                (int) (mPopupBitmapStartRects[i].getX() + dx * mInterpolator.getInterpolation(r) + 0.5f)
                        );
                        mPopupBitmapCurrentRects[i].setY(
                                (int) (mPopupBitmapStartRects[i].getY() + dy * mInterpolator.getInterpolation(r) + 0.5f)
                        );
                    }

                    //文字动画
                    for (int i = 0; i < textViewList.size(); i++) {
                        final int num = i;
                        int w = (int) (rate * (finalW - verticalPadding * 2));
                        textViewWidth = Math.max(w, 0);
                        textViewWidth = Math.min(w, finalW - verticalPadding * 2);
                        final int alpha = (int) Math.min(255, rate * 255);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                final int left = textViewList.get(num).getLeft();
                                final int top = textViewList.get(num).getTop();
                                final int right = left + textViewWidth;
                                final int bottom = top + textViewHight;
                                textViewList.get(num).layout(left, top, right, bottom);
                                textViewList.get(num).setAlpha(alpha);
                            }
                        });
                    }

                    //图像动画，每列三个，逐个放大
                    for (int i = 0; i < imageViewList.size(); i++) {
                        final int alpha = (int) Math.min(255, rate * 255);
                        final int num = i;
                        final float finalRate1 = rate;
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                float scale = Math.min(1.0f, finalRate1 * 3 - num * finalRate1 + 0.1f);
                                scale = Math.max(0, scale);
                                imageViewList.get(num).setScaleX(scale);
                                imageViewList.get(num).setScaleY(scale);
                                imageViewList.get(num).setAlpha(alpha);
                            }
                        });
                    }
                } else {
                    /*
                    隐藏小图片
                     */

                    mActualAnimatedFrame--;
                    if (mActualAnimatedFrame < 0) {
                        mActualAnimatedFrame = 0;
                    }
                    rate = mActualAnimatedFrame * 1.0f / totalFrames; //根据已进行的动画帧数,计算当前收缩时的进度

                    //小图片播放动画
                    for (int i = 0; i < mPopupBitmapCurrentRects.length; i++) {
                        int addX = (int) ((mPopupBitmapStartRects[i].getX() - mPopupBitmapEndRects[i].getX()) * (rate));
                        int addY = (int) ((mPopupBitmapStartRects[i].getY() - mPopupBitmapEndRects[i].getY()) * (rate));
                        int x = mPopupBitmapStartRects[i].getX() - addX;
                        int y = mPopupBitmapStartRects[i].getY() - addY;
                        mPopupBitmapCurrentRects[i].setX(x);
                        mPopupBitmapCurrentRects[i].setY(y);
//                        FlyLog.d("<PopupAnimatView>PopupBitmapAnimationTask Hide--x=" + x + ",y=" + y + ",addX=" + addX + ",mPopupBitmapAnimatCount=" + mPopupBitmapAnimatCount + ",setpPercentage=" + setpPercentage);
                    }

                    //文字动画
                    for (int i = 0; i < textViewList.size(); i++) {
                        int w = (int) ((rate) * (finalW - verticalPadding * 2));
                        textViewWidth = Math.max(w, 0);
                        textViewWidth = Math.min(w, finalW - verticalPadding * 2);
                        final int num = i;
                        final int alpha = (int) Math.min(255, rate * 255);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                final int left = textViewList.get(num).getLeft();
                                final int top = textViewList.get(num).getTop();
                                final int right = left + textViewWidth;
                                final int bottom = top + textViewHight;
                                textViewList.get(num).setAlpha(alpha);
//                                FlyLog.d(textViewList.get(num).getId()+" left=%d,right=%d,top=%d,bottom=%d,alpha=%d",left,right,top,bottom,alpha);
                                textViewList.get(num).layout(left, top, right, bottom);
                            }
                        });
                    }

                    //图像动画
                    for (int i = 0; i < imageViewList.size(); i++) {
                        final int alpha = (int) Math.max(0, (rate) * 255);
                        final int num = i;
                        final float finalRate = rate;
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                float scale = Math.min(1.0f, (finalRate) * 3 - num * (finalRate));
                                scale = Math.max(0, scale);
                                imageViewList.get(num).setScaleX(scale);
                                imageViewList.get(num).setScaleY(scale);
                                imageViewList.get(num).setAlpha(alpha);
                            }
                        });
                    }

                    if (mPopupBitmapCurrentRects[0].getX() >= mPopupBitmapStartRects[0].getX()) {
                        for (int i = 0; i < mPopupBitmapCurrentRects.length; i++) {
                            mPopupBitmapCurrentRects[i].setX(mPopupBitmapStartRects[i].getX());
                            mPopupBitmapCurrentRects[i].setY(mPopupBitmapStartRects[i].getY());
                        }
                        postInvalidate();
                        isPopupBitmapAnimatTaskRunnning.set(false);
                        break;
                    }

                }
                postInvalidate();
                isPopupBitmapAnimatTaskRunnning.set(false);
                try {
                    Thread.sleep(ONCE_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            isPopupBitmapAnimatTaskRunnning.set(false);
        }
    }

    /**
     * 启动播放控件缩放动画
     *
     * @param duration 播放时间
     * @param isZoomIn 是弹出还是收起
     */
    public synchronized void playZoomAnimat(int duration, boolean isZoomIn) {
        FlyLog.i("<PopupAnimatView>playZoomAnim---" + getTag() + "----isZoomIn=" + isZoomIn);
        this.isZoomIn.set(isZoomIn);
        if (zoomAnimtaTask == null) {
            zoomAnimtaTask = new PlayZoomAnimtaTask(duration);
        }
        if (!isZoomAnimtaTaskRunnning.get()) {
            executors.execute(zoomAnimtaTask);
            FlyLog.i("<PopupAnimatView>playZoomAnim---" + getTag() + " ----executors.execute(zoomAnimtaTask)");
        }
        //缩小动画
        if (!isZoomIn) {
            isShowPopupBitmap = false;
            if (mPopupBitmapAnimationTask == null) {
                mPopupBitmapAnimationTask = new PopupBitmapAnimationTask(duration);
            }
            if (!isPopupBitmapAnimatTaskRunnning.get()) {
                executors.execute(mPopupBitmapAnimationTask);
            }
            FlyLog.i("<PopupAnimatView>playZoomAnim---" + getTag() + " ----executors.execute(mPopupBitmapAnimationTask)");
        } else {
            isShowPopupBitmap = true;
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setFocusOrder();
    }

    @Override
    protected void onDetachedFromWindow() {
        mHandler.removeCallbacksAndMessages(null);
        super.onDetachedFromWindow();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            //布置中间图片
            final int c_left = (getWidth() - finalW) / 2;
            final int c_top = (getHeight() - finalH) / 2;
            final int c_right = (getWidth() - finalW) / 2 + finalW;
            final int c_bottom = c_top + finalH;
            mCenterImageView.layout(c_left, c_top, c_right, c_bottom);
            //布置TextView
            int lastTop = c_bottom + verticalPadding;

            for (int i = 0; i < textViewList.size(); i++) {
                int left = c_left + verticalPadding;
                int right = left + textViewWidth;
                textViewList.get(i).layout(left, lastTop, right, lastTop + textViewHight);
                lastTop = lastTop + textViewHight + verticalPadding;
            }
            //布置ImageView
            for (int i = 0; i < imageViewList.size(); i++) {
                int width = (finalW - verticalPadding * 4) / 3;
                int left = c_left + verticalPadding + i % 3 * (width + verticalPadding);
                int right = left + width;
                int top = lastTop + (i / 3) * (width + verticalPadding);
                int bottom = top + width;
                imageViewList.get(i).layout(left, top, right, bottom);
            }
        }
    }


    public void setOnSizeZoom(OnSizeZoom onSizeZoom) {
        this.onSizeZoom = onSizeZoom;
    }

    private OnSelectListener onSelectListener;

    public void setOnSelectListener(OnSelectListener onSelectListener) {
        this.onSelectListener = onSelectListener;
    }

    /**
     * 生成圆角图片
     *
     * @param bitmap
     * @param roundPx 圆角弧度（像素值）
     * @return
     */
    public static Bitmap GetRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
        try {
            Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            final RectF rectF = new RectF(new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()));
            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(Color.BLACK);
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            final Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            canvas.drawBitmap(bitmap, src, rect, paint);
            return output;
        } catch (Exception e) {
            return bitmap;
        }
    }

    /**
     * 拦截按键事件，处理各种焦点问题
     *
     * @param event
     * @return
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        isLostFocus = confirmLostFocus(event);
        return super.dispatchKeyEvent(event);
    }

    /**
     * 此算法完成计算，确认移动后所有子控件是否都会失去焦点
     *
     * @param event
     * @return
     */
    private boolean confirmLostFocus(KeyEvent event) {
        boolean isLostFocus = false;
        View focusView = getFocusedChild();
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_DPAD_UP:
                    if (focusView != null && (focusView instanceof CenterImageView)) {
                        isLostFocus = true;
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    if (focusView != null && (focusView instanceof ChildImageView)) {
                        int num = ((ChildImageView) focusView).getNum();
                        isLostFocus = (num % 3 == 0);
                    } else {
                        isLostFocus = true;
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    if (focusView != null && (focusView instanceof ChildImageView)) {
                        int num = ((ChildImageView) focusView).getNum();
                        isLostFocus = (num % 3 == 2) || (num == imageViewList.size() - 1);
                    } else {
                        isLostFocus = true;
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    if (textViewList.isEmpty() && imageViewList.isEmpty()) {
                        isLostFocus = true;
                    } else if (!imageViewList.isEmpty()) {
                        if (focusView instanceof ChildImageView) {
                            isLostFocus = ((ChildImageView) focusView).getNum() >= ((ChildImageView) focusView).getNum() / 3 * 3;
                        }
                    } else if (!textViewList.isEmpty()) {
                        if (focusView instanceof ChildTextView) {
                            isLostFocus = (focusView.equals(textViewList.get(textViewList.size() - 1)));
                        }
                    }
                    break;
                default:
                    isLostFocus = false;
                    break;
            }
        }
        return isLostFocus;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        scaleW = getWidth() / (finalW * maxScale);
        scaleH = getHeight() / (finalH * maxScale);
        if (mPopupBitmapUrls != null && isDownAnimta) {
            for (int i = Math.min(mPopupBitmapUrls.length - 1, 5); i >= 0; i--) {
                Bitmap bitmap = mPopupBitmapTable.get(mPopupBitmapUrls[i]);
                if (bitmap == null) {
                    continue;
                } else {
                    canvas.drawBitmap(bitmap,
                            mPopupBitmapCurrentRects[i].getX() * scaleW - (mPopupBitmapWidth - mPopupBitmapWidth * scaleW) / 2,
                            mPopupBitmapCurrentRects[i].getY() * scaleH - (mPopupBitmapHeight - mPopupBitmapHeight * scaleH) / 2,
                            bitmap_paint1);
                }
            }
        }
    }

    private <T extends View> void removeViewList(List<T> viewList) {
        for (View view : viewList) {
            removeView(view);
        }
    }

    @Override
    public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
        if (mCenterImageView != null) {
            return mCenterImageView.requestFocus();
        } else {
            return false;
        }
    }

    @Override
    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

}
