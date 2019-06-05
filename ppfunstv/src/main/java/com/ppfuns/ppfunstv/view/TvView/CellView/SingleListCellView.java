package com.ppfuns.ppfunstv.view.TvView.CellView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
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
import com.ppfuns.ppfunstv.utils.FlyLog;
import com.ppfuns.ppfunstv.utils.GsonUtil;
import com.ppfuns.ppfunstv.utils.Utils;
import com.ppfuns.ppfunstv.view.LoopPlayView.CellChildView;
import com.ppfuns.ppfunstv.view.LoopPlayView.ILoopPlayView;
import com.ppfuns.ppfunstv.view.LoopPlayView.LoopPlayCellView;
import com.ppfuns.ppfunstv.view.ReflectImageView;
import com.ppfuns.ppfunstv.view.TvView.CellView.CellClickAction.ActionFactory;

import org.json.JSONObject;

import java.util.Map;

/**
 * 竖版单排列表控件
 * Created by flyzebra on 17-5-3.
 */
public class SingleListCellView extends TvPageItemView {
    private ILoopPlayView mLoopPlayView;
    private TextView menuTextView;
    private TextView countTextView;
//    private Handler mHander = new Handler(Looper.getMainLooper());

//    private String[] mPalyImages;

    private ImageView showImageView;

    private int resId;
    private String refUrl;

    public SingleListCellView(Context context) {
        this(context, null);
    }

    public SingleListCellView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SingleListCellView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setClipChildren(true);
        setClipToPadding(true);
    }

    @Override
    public View setCellData(CellEntity cellEntity) {
        cellEntity.setFocusType(1);
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

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }


    @Override
    public void bindReflectView(ReflectImageView reflectImageView) {
        super.bindReflectView(reflectImageView);
        if (!TextUtils.isEmpty(refUrl)) {
//            setReflectView(refUrl, loadImageResId);
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
//                .override(mCell.getWidth(), mCell.getHeight())
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
        if (mLoopPlayView == null) {
            FlyLog.d("error don't createView.");
            return;
        }
    }

    @Override
    public ImageView getMyImageView() {
        return showImageView;
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
        if (mCell.getSubCellList() != null) {
            menuTextView = new TextView(mContext);
            LayoutParams lp1 = new LayoutParams(mCell.getWidth(), 40);
            menuTextView.setLayoutParams(lp1);
            menuTextView.setTextColor(0xffff6600);
            menuTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, 26);
            addView(menuTextView);

            String textStr = mCell.getText();
            Map str = GsonUtil.json2Map(textStr);
            menuTextView.setText(Utils.getLocalLanguageString(str));


            countTextView = new TextView(mContext);
            countTextView.setLayoutParams(lp1);
            countTextView.setGravity(Gravity.RIGHT);
            countTextView.setTextColor(0xffff6600);
            countTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, 26);
            addView(countTextView);

            final String mPalyImages[] = new String[mCell.getSubCellList().size()];
            for (int i = 0; i < mCell.getSubCellList().size(); i++) {
                mPalyImages[i] = mCell.getSubCellList().get(i).getImgUrl();
            }
            mLoopPlayView = new LoopPlayCellView(mContext);
            addView((View) mLoopPlayView);

            mLoopPlayView.notifyData(new ILoopPlayView.OnDataChanged() {
                @Override
                public void setChildViewData(ImageView itView, int num, String url) {
                    try {
                        Glide.with(mContext)
                                .load(iDiskCache.getBitmapPath(url))
//                                        .override(width, height)
                                .placeholder(mLoadImageResId)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .into(itView);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String textStr = mCell.getSubCellList().get(num).getText();
                    Map str = GsonUtil.json2Map(textStr);
                    ((CellChildView)itView).setText(Utils.getLocalLanguageString(str));
                }
            })
                    .setOnChildViewChanged(new ILoopPlayView.OnViewChangedListener() {
                        @Override
                        public void onViewChanged(ImageView lostView, ImageView focusView, int currentItem) {
                            if (lostView != null && focusView != null) {
//                                notifyFocusChanged(true);
                            }
                            countTextView.setText((currentItem + 1) + "/" + mCell.getSubCellList().size());
                            if (lostView != null) {
                                ((CellChildView) lostView).setTextColor(0x3fffffff);
                            }
                            if (focusView != null) {
                                ((CellChildView)focusView).setTextColor(0xffffffff);
                            }
                        }

                    })
                    .setImageViewUrls(mPalyImages)
                    .setTopTextHeight(50)
                    .setShowImageNum(Math.max(1,mCell.getShowImageNum()))
                    .setChildViewHeight(mCell.getHeight() - 50)
                    .setChildViewPadding(10)
                    .setFirstFocusItem(1)
                    .setMaxDuration(300)
                    .initView(mCell.getWidth(), mCell.getHeight());

            countTextView.setText("1/" + mCell.getSubCellList().size());


        } else {
            showImageView = new ImageView(mContext);
            LayoutParams lp = new LayoutParams(mCell.getWidth(), mCell.getHeight());
            showImageView.setLayoutParams(lp);
            showImageView.setScaleType(ImageView.ScaleType.FIT_XY);
            addView(showImageView);
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

    @Override
    public Rect getFocusRect() {
        if (mLoopPlayView == null) {
            return super.getFocusRect();
        }
        Rect rect = mLoopPlayView.getFocusRect();
        mFocusRect.left = rect.left + mCell.getX();
        mFocusRect.right = rect.right + mCell.getX();
        mFocusRect.top = rect.top + mCell.getY();
        mFocusRect.bottom = rect.bottom + mCell.getY();
        return mFocusRect;
    }


    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        mLoopPlayView.notifyFocusChanged(gainFocus);
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }


}
