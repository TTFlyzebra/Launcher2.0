package com.flyzebra.ppfunstv.view.TvView.CellView;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.annotation.RawRes;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyzebra.flyui.chache.IDiskCache;
import com.flyzebra.ppfunstv.data.CellEntity;
import com.flyzebra.ppfunstv.module.BitmapCache;
import com.flyzebra.ppfunstv.utils.DisplayUtils;
import com.flyzebra.ppfunstv.utils.FlyLog;
import com.flyzebra.ppfunstv.utils.FontManager;
import com.flyzebra.ppfunstv.utils.GsonUtil;
import com.flyzebra.ppfunstv.utils.Utils;
import com.flyzebra.ppfunstv.view.ReflectImageView;
import com.flyzebra.ppfunstv.view.TvView.FocusAnimat.IAnimatView;
import com.flyzebra.ppfunstv.view.TvView.FocusAnimat.ITvFocusAnimat;

import java.util.Map;

/**
 * 普通文本控件
 * Created by flyzebra on 17-8-29.
 */

public class TextCellView extends TextView implements ITvPageItemView, IAnimatView {
    private CellEntity mCell;
    private Context mContext;

    public TextCellView(Context context) {
        this(context, null);
    }

    public TextCellView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextCellView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
    }

    @Override
    public void setFocusAnimate(ITvFocusAnimat iTvFocusAnimat) {

    }

    @Override
    public Rect getFocusRect() {
        return null;
    }

    @Override
    public Rect getOldRect() {
        return null;
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

    @Override
    public void showImage(String imgUrl) {

    }

    @Override
    public void setLoadImageResId(@RawRes int ResID) {

    }

    @Override
    public void doAction() {

    }

    @Override
    public void doAction(int flag) {

    }

    @Override
    public void bindReflectView(ReflectImageView reflectImageView) {

    }

    @Override
    public ReflectImageView getReflectImageView() {
        return null;
    }

    @Override
    public ImageView getMyImageView() {
        return null;
    }

    @Override
    public View setCellData(CellEntity cellEntity) {
        try {

            float screenScale = DisplayUtils.getMetrices((Activity) mContext).widthPixels / 1920f;
            this.mCell = cellEntity;
            if (mCell.getSize() == 0) {
                mCell.setSize((int) (36 * screenScale));
            }


            if (!TextUtils.isEmpty(mCell.getFont())) {
                setTypeface(FontManager.getTypefaceByFontName(mContext, mCell.getFont()));
            }

            setTextSize(TypedValue.COMPLEX_UNIT_PX, Math.max(15, mCell.getSize()));

            int textColor = 0xFFFFFFFF;
            String color = cellEntity.getColor();
            try {
                textColor = Color.parseColor(color);
            } catch (Exception e) {
                textColor = 0xFFFFFFFF;
                FlyLog.d("parseColor error! use defult color #FFFFFFFF.");
            }

            setTextColor(textColor);


            String textStr = mCell.getText();
            Map str = GsonUtil.json2Map(textStr);
            setText(Utils.getLocalLanguageString(str));
        }catch (Exception e){
            FlyLog.d(e.toString());
        }
        return this;
    }

    @Override
    public CellEntity getCellData() {
        return mCell;
    }

    @Override
    public void setDiskCache(IDiskCache iDiskCache) {

    }

    @Override
    public void setBitmapCache(BitmapCache bitmapCache) {

    }

    @Override
    public String getPackName() {
        return null;
    }

    @Override
    public void setAnimtorDurtion(int durtion) {

    }

    @Override
    public void isUseWallPager(boolean isUseWallPager) {

    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
    }
}
