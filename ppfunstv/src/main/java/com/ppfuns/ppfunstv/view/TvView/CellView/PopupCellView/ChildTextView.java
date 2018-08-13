package com.ppfuns.ppfunstv.view.TvView.CellView.PopupCellView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.ppfuns.ppfunstv.data.CellEntity;
import com.ppfuns.ppfunstv.module.BitmapCache;
import com.ppfuns.ppfunstv.view.TvView.FocusAnimat.IAnimatView;
import com.ppfuns.ppfunstv.view.TvView.FocusAnimat.ITvFocusAnimat;

/**
 * Created by FlyZebra on 2016/8/25.
 */
public class ChildTextView extends TextView implements IAnimatView{
    private Paint mBitmapPaint ;
    private Bitmap mBitmap;
    private int mPadding = 20;
    private int mHeight = 36;
    private int mWidth = 0;

    public void setmPadding(int mPadding) {
        this.mPadding = mPadding;
    }

    public void setmHeight(int mHeight) {
        this.mHeight = mHeight;
    }

    public void setmWidth(int mWidth) {
        this.mWidth = mWidth;
    }

    public ChildTextView(Context context) {
        this(context, null);
    }

    public ChildTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChildTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
        initView(context);
    }

    private void initView(Context context) {
        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setStyle(Paint.Style.FILL);
    }

    public void setCellData(@NonNull CellEntity cellEntity){
        final String bkUrl = cellEntity.getImgUrl();
        if(TextUtils.isEmpty(bkUrl)) return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                mBitmap = BitmapCache.createBitmapFromLocal(getContext(),bkUrl);
                if(mBitmap!=null){
                    Matrix matrix = new Matrix();
                    int height = mHeight*4/5;
                    int width = height;
                    matrix.postScale((float) width / (float) mBitmap.getWidth(), (float) height / (float) mBitmap.getHeight());
                    mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, false);
                }
                if(mBitmap!=null){
                    postInvalidate();
                }
            }
        }).start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(mBitmap !=null){
            canvas.drawBitmap(mBitmap,getMeasuredWidth()-mHeight-mPadding, mHeight/10,mBitmapPaint);
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
            mRect.left = view.getFinalX() + view.getVerticalPadding();
            mRect.right = view.getFinalX() + view.getFinalW() - view.getVerticalPadding();
            mRect.top = view.getFinalY() + view.getFinalH() + view.getVerticalPadding() + ((int) this.getTag()) * (view.getTextViewHight() + view.getVerticalPadding());
            mRect.bottom = mRect.top + view.getTextViewHight();
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
