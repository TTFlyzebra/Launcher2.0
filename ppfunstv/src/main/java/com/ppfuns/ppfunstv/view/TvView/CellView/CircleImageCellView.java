package com.ppfuns.ppfunstv.view.TvView.CellView;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.ppfuns.ppfunstv.R;
import com.ppfuns.ppfunstv.data.CellEntity;
import com.ppfuns.ppfunstv.utils.DisplayUtils;
import com.ppfuns.ppfunstv.utils.FlyLog;
import com.ppfuns.ppfunstv.utils.GsonUtils;
import com.ppfuns.ppfunstv.utils.Utils;

/**
 * Created with Android Studio.
 * User: Fargo
 * Date: 2017/8/21
 * Time: 下午2:29
 */

public class CircleImageCellView extends SimpleCellView {
    private TextView scTextView;

    public CircleImageCellView(Context context) {
        this(context, null);
    }

    public CircleImageCellView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleImageCellView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    @Override
    public View setCellData(CellEntity cellEntity) {
        cellEntity.setFocusType(3);
        return super.setCellData(cellEntity);
    }

    @Override
    public void initView() {
//        super.initView();


        LayoutParams lp = new LayoutParams(mCell.getWidth(), mCell.getHeight());
        setLayoutParams(lp);
        mLangMap = GsonUtils.json2Map(mCell.getText());
        float scaleScreen = DisplayUtils.getMetrices((Activity) mContext).widthPixels / 1920f;
        inflate(mContext, R.layout.tv_circle_image_cell_item, this);
        mImageView = (SubScriptView) findViewById(R.id.tv_iv_cell);
        scTextView = (TextView) findViewById(R.id.tv_tv_cell2);
        mImageView.getLayoutParams().height = mCell.getWidth();
        mImageView.getLayoutParams().width = mCell.getWidth();
        mTvInfo = (TextView) findViewById(R.id.tv_tv_cell);
        setTextInfo(mTvInfo);
    }


    public void setFocusRect() {
        mFocusRect.top = mImageView.getTop() + mCell.getY();
        mFocusRect.left = mImageView.getLeft() + mCell.getX();
        mFocusRect.right = mImageView.getRight() + mCell.getX();
        mFocusRect.bottom = mImageView.getBottom() + mCell.getY();

        FlyLog.d("set Rect = " + mFocusRect);
    }




    @Override
    public Rect getFocusRect() {
        mFocusRect.top = mImageView.getTop() + mCell.getY();
        mFocusRect.left = mImageView.getLeft() + mCell.getX();
        mFocusRect.right = mImageView.getRight() + mCell.getX();
        mFocusRect.bottom = mImageView.getBottom() + mCell.getY();

        FlyLog.d("Rect = " + mFocusRect);
        return mFocusRect;
    }

    /**
     * 设置文字相关信息
     *
     * @param view
     */
    public void setTextInfo(TextView view) {
        try {
            String text = null;
            if (mLangMap != null) {
                text = Utils.getLocalLanguageString(mLangMap);
            } else {
                text = mCell.getText();
            }


            if (!TextUtils.isEmpty(text)) {
                text.replace("\\\\n", "\\n");
                String strs[] = text.split("\\n");
                view.setText(strs[0]);
                if (strs.length > 1) {
                    scTextView.setText(strs[1]);
                }
            }


//            FlyLog.d("text = " + text);
//            if (TextUtils.isEmpty(text) || TextUtils.isEmpty(text.trim())) {
//                view.setVisibility(INVISIBLE);
//            } else {
//                view.setVisibility(VISIBLE);
//                view.setText(text);
//            }
            //设置背景
        } catch (Exception e) {
            FlyLog.e(e.toString());
        }

    }


    @Override
    public void showImage(String imgUrl) {
        super.showImage(imgUrl);
        loadImageSrc(mCell);

    }

    @Override
    protected void loadImageSrc(CellEntity entity) {
//        super.loadImageSrc(entity);

        String imgUrl = entity.getImgUrl();
        String filePath = iDiskCache != null ? imgUrl : iDiskCache.getBitmapPath(imgUrl);
        Glide.with(mContext)
                .load(filePath)
                .asBitmap()
                .centerCrop()
                .into(new BitmapImageViewTarget(mImageView) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        mImageView.setImageDrawable(circularBitmapDrawable);
                    }
                });
    }


}
