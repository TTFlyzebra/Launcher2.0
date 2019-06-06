package com.flyzebra.ppfunstv.view.TvView.CellView.PopupCellView;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.flyzebra.flyui.chache.IDiskCache;
import com.flyzebra.ppfunstv.data.CellEntity;
import com.flyzebra.ppfunstv.view.TvView.CellView.CellClickAction.DefaultAction;
import com.flyzebra.ppfunstv.view.TvView.FocusAnimat.IAnimatView;
import com.flyzebra.ppfunstv.view.TvView.FocusAnimat.ITvFocusAnimat;

/**
 * Created by FlyZebra on 2016/8/18.
 */
public class ChildImageView extends ImageView implements IAnimatView{
    protected int num;
    protected CellEntity mCellEntity;
    protected IDiskCache iDiskCache;
    protected Context mContext;

    public ChildImageView(Context context) {
        this(context, null);
    }

    public ChildImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChildImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                doAction();
            }
        });
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public CellEntity getCellEntity() {
        return mCellEntity;
    }

    public void setCellEntity(CellEntity mCellEntity) {
        this.mCellEntity = mCellEntity;
    }

    public IDiskCache getiDiskCache() {
        return iDiskCache;
    }

    public void setiDiskCache(IDiskCache iDiskCache) {
        this.iDiskCache = iDiskCache;
    }

    public void doAction() {
        new DefaultAction(mContext, mCellEntity.getAction(), false).doAction();
    }


    public void showImage() {
        if (iDiskCache != null && mCellEntity != null) {
            String imgUrl = iDiskCache.getBitmapPath(mCellEntity.getImgUrl());
            Glide.with(getContext())
                    .load(imgUrl)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
//                    .transform(new GlideRoundTransform(getContext(), 20))
                    .into(this);
        }
    }

    @Override
    public void setFocusAnimate(ITvFocusAnimat iTvFocusAnimat) {

    }

    private Rect mRect = new Rect();
    @Override
    public Rect getFocusRect() {
        if (getParent() instanceof PopupAnimatView) {
            PopupAnimatView view = (PopupAnimatView) getParent();
            int num = getNum();
            int width = (view.getFinalW() - view.getVerticalPadding() * 4) / 3;
            mRect.left = view.getFinalX() + view.getVerticalPadding() + (width + view.getVerticalPadding()) * (num % 3);
            mRect.right = mRect.left + width;
            int addtop = view.getTextViewList().size() * (view.getVerticalPadding() + view.getTextViewHight());
            mRect.top = view.getFinalY() + view.getFinalH() + addtop + view.getVerticalPadding() + (view.getVerticalPadding() + width) * (num / 3);
            mRect.bottom = mRect.top + width;
        }
        return mRect;
    }

    @Override
    public Rect getOldRect() {
        return mRect;
    }

    @Override
    public int getFocusZorder() {
        return 0;
    }

    @Override
    public int getFocusScale() {
        return 1;
    }

    @Override
    public int getFocusType() {
        return 0;
    }

    @Override
    public View getReflectImageView() {
        return null;
    }
}
