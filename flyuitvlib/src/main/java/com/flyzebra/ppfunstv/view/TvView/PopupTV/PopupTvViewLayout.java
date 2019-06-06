package com.flyzebra.ppfunstv.view.TvView.PopupTV;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Scroller;
import android.widget.TextView;

import com.flyzebra.flyui.chache.IDiskCache;
import com.flyzebra.ppfunstv.R;
import com.flyzebra.ppfunstv.data.CellEntity;
import com.flyzebra.ppfunstv.data.TvCellBean;
import com.flyzebra.ppfunstv.module.BitmapCache;
import com.flyzebra.ppfunstv.utils.DisplayUtils;
import com.flyzebra.ppfunstv.utils.FlyLog;
import com.flyzebra.ppfunstv.view.ReflectImageView;
import com.flyzebra.ppfunstv.view.TvView.BaseTvView;
import com.flyzebra.ppfunstv.view.TvView.CellSortable;
import com.flyzebra.ppfunstv.view.TvView.CellView.ITvPageItemView;
import com.flyzebra.ppfunstv.view.TvView.CellView.PopupCellView.CenterImageView;
import com.flyzebra.ppfunstv.view.TvView.CellView.PopupCellView.IPopupAnimatView;
import com.flyzebra.ppfunstv.view.TvView.CellView.SimpleCellView;
import com.flyzebra.ppfunstv.view.TvView.CellView.TvPageItemView;
import com.flyzebra.ppfunstv.view.TvView.CellView.TvPageViewItemFactory;
import com.flyzebra.ppfunstv.view.TvView.FocusAnimat.FocusShadowView;
import com.flyzebra.ppfunstv.view.TvView.FocusAnimat.IAnimatView;
import com.flyzebra.ppfunstv.view.TvView.FocusAnimat.ITvFocusAnimat;
import com.flyzebra.ppfunstv.view.TvView.FocusAnimat.PopupTvAnimat;
import com.flyzebra.ppfunstv.view.TvView.FocusAnimat.PopupTvAnimat2;
import com.flyzebra.ppfunstv.view.TvView.ICellSortable;
import com.flyzebra.ppfunstv.view.TvView.IOnKeyDownOutEnvent;
import com.flyzebra.ppfunstv.view.TvView.IPageChangeListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 实现的动态布局控制控件
 * Created by FlyZebra on 2016/8/4.
 */
public class PopupTvViewLayout extends ViewGroup {
    private static int USER_ID = 0x4f000000;
    private int width;
    private int height;
    private Context context;
    private Scroller mScroller;
    private ITvFocusAnimat iTvFocusAnimat;
    private IPopupAnimatView hasFocusPView = null;
    private View hasFocusView = null;
    //    private Rect hasFocusRect = new Rect();
//    private Rect lastFocusRect = new Rect();
    private int mAnimDuration = 300;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private List<CellEntity> mCellEntityList = new ArrayList<>();

    /**
     * 需要投影的控件列表
     */
    private List<ReflectImageView> refImageViewlist = new ArrayList<>();
    private List<View> mChildViewList;
    private float screenScale;


    /**
     * 磁盘缓存接口
     */
    private IDiskCache iDiskCache;
    private BitmapCache mBitmapCache;
    private Runnable mZoomTask = new Runnable() {
        @Override
        public void run() {
            if (hasFocusPView != null) {
                hasFocusPView.playZoomAnimat(mAnimDuration, true);
            }
        }
    };
    private IOnKeyDownOutEnvent onKeyDownOutEnvent;
    private BaseTvView.OnCellItemClick onCellItemClick;
    private int resId = R.drawable.tv_default;
    private boolean isShowReflect;
    private boolean isUseWallPager;


    public PopupTvViewLayout setShowReflect(boolean showReflect) {
        isShowReflect = showReflect;
        return this;
    }

    public PopupTvViewLayout(Context context) {
        super(context);
        init(context);
    }

    public PopupTvViewLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PopupTvViewLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setFocusable(false);
        setFocusableInTouchMode(false);
        setClipChildren(false);
        setClipToPadding(false);
        this.context = context;
        screenScale = DisplayUtils.getMetrices((Activity) context).widthPixels / 1920f;
        mScroller = new Scroller(context);

        mChildViewList = new ArrayList<>();
    }

    /**
     * 设置占位图
     *
     * @param resId
     */
    public void setLoadImageResId(int resId) {
        this.resId = resId;
    }


    public PopupTvViewLayout setiDiskCache(IDiskCache iDiskCache) {
        this.iDiskCache = iDiskCache;
        return this;
    }


    public PopupTvViewLayout setBitmapCache(BitmapCache bitmapCache) {
        this.mBitmapCache = bitmapCache;
        return this;
    }

    private void AddChildView(final List<CellEntity> cellList) {
        //初始化焦点框
        for (int i = 0; i < cellList.size(); i++) {
            CellEntity cellEntity = cellList.get(i);
            final ITvPageItemView view = TvPageViewItemFactory.createView(context, iDiskCache, mBitmapCache, cellEntity,resId);
            ((View) view).setTag(i);
            if(view instanceof IAnimatView){
                ((IAnimatView)view).setFocusAnimate(iTvFocusAnimat);
            }
            view.setAnimtorDurtion(mAnimDuration);
            view.isUseWallPager(isUseWallPager);
            view.setCellData(cellEntity);
            TvPageViewItemFactory.handleExtend(context,cellEntity);

            mChildViewList.add((View) view);


            addView((View) view);

            ((View) view).setFocusable(!(view instanceof IPopupAnimatView) && cellEntity.getCanFocus());
            ((View) view).setFocusableInTouchMode(!(view instanceof IPopupAnimatView) && cellEntity.getCanFocus());

            ((View) view).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onCellItemClick != null) {
                        onCellItemClick.onCellItemClick((TvPageItemView) v);
                    }
                }
            });


             /*
             *最后一排显示倒影
             */
            if (needReflect(cellEntity, i) && isShowReflect) {
                final ReflectImageView refView = new ReflectImageView(context);
                refView.setTag(i);
                refView.setCellData(cellEntity);
                refView.setRefHeight((int) (100 * screenScale));
                refView.setScaleType(ImageView.ScaleType.FIT_XY);
                refImageViewlist.add(refView);
                super.addView(refView);
                view.bindReflectView(refView);
            }

            final String url = cellList.get(i).getImgUrl();
            final int n = i;
//            view.showImage(cellList.get(n).getImgUrl(), loadImageResId);
//            if(! (view instanceof CarouselCellView)) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    view.showImage(url);
                }
            }, i * 10);
//            }
//            else
//            {
//                view.showImage(url, loadImageResId);
//            }

            if (view instanceof IPopupAnimatView) {
                IPopupAnimatView popupAnimatView = (IPopupAnimatView) view;
                popupAnimatView.setOnSizeZoom(new IPopupAnimatView.OnSizeZoom() {
                    @Override
                    public void onSizeZoom(IPopupAnimatView view, boolean isZoomIn) {
                        onLayoutWithChangeView(view, isZoomIn);
                    }
                });
                popupAnimatView.setOnSelectListener(new IPopupAnimatView.OnSelectListener() {
                    @Override
                    public void onSelectListener(final IPopupAnimatView parentView, View v, boolean hasFocus, boolean parentLostFocus) {
                        if (hasFocus) {
                            hasFocusPView = parentView;
                            hasFocusView = v;
                        }
                        if (hasFocus) {
//                            iTvFocusAnimat.startAnim((TvPageItemView) parentView, mAnimDuration, parentView.isLostFocus());
                            if (parentView.isLostFocus()) {
                                mHandler.postDelayed(mZoomTask, mAnimDuration);
                            }
                        } else {
                            if (parentLostFocus) {
                                parentView.playZoomAnimat(mAnimDuration, false);
                                mHandler.removeCallbacks(mZoomTask);
                            }
                            //兼容鼠标切换焦点事件
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (((TvPageItemView) parentView).getFocusedChild() == null) {
                                        if (!parentView.isLostFocus()) {
                                            parentView.setLostFocus(true);
                                            parentView.playZoomAnimat(mAnimDuration, false);
                                        }
                                    }
                                }
                            }, 100);
                        }
                    }
                });

            } else {
                ((View) view).setOnFocusChangeListener(new OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            hasFocusView = v;
//                            iTvFocusAnimat.startAnim((TvPageItemView) v, mAnimDuration, true);
                        }
                    }
                });
            }
        }

        setChildResID();
        setChildFocusOrder();
        //设置第一个控件获取焦点

    }

    /**
     * 设置子控件ID
     */
    private void setChildResID() {
        for (int i = 0; i < mChildViewList.size(); i++) {
            View view = mChildViewList.get(i);
            if (view instanceof IPopupAnimatView) {
                view = ((IPopupAnimatView) view).getCenterImageView();
            }
            view.setId(i * 0x1000 + USER_ID);
        }
    }

    /**
     * 指定控件移动的焦点。
     **/
    private void setChildFocusOrder() {
        final int maxNum = mChildViewList.size() - 1;
        if (maxNum < 1) return;
        for (int i = 0; i <= maxNum; i++) {
            if (mChildViewList.get(i) instanceof IPopupAnimatView) {
                View mCenterView = ((IPopupAnimatView) mChildViewList.get(i)).getCenterImageView();
                List<TextView> textViewList = ((IPopupAnimatView) mChildViewList.get(i)).getTextViewList();
                List<ImageView> imageViewList = ((IPopupAnimatView) mChildViewList.get(i)).getImageViewList();
                if (i == 0) {
                    int leftId = mChildViewList.get(0) instanceof IPopupAnimatView
                            ? ((IPopupAnimatView) mChildViewList.get(0)).getCenterImageView().getId()
                            : mChildViewList.get(0).getId();

                    int rightId = mChildViewList.get(i + 1) instanceof IPopupAnimatView
                            ? ((IPopupAnimatView) mChildViewList.get(i + 1)).getCenterImageView().getId()
                            : mChildViewList.get(i + 1).getId();

                    mCenterView.setNextFocusLeftId(leftId);
                    mCenterView.setNextFocusRightId(rightId);

                    //TextView
                    for (TextView tv : textViewList) {
                        tv.setNextFocusLeftId(leftId);
                        tv.setNextFocusRightId(rightId);
                    }

                    //ImageView
                    for (int n = 0; n < imageViewList.size(); n++) {
                        ImageView iv = imageViewList.get(n);
                        switch (n % 3) {
                            case 0:
                                iv.setNextFocusLeftId(leftId);
                                break;
                            case 1:
                                break;
                            case 2:
                                iv.setNextFocusRightId(rightId);
                                break;
                        }
                        if (n == imageViewList.size() - 1) {
                            iv.setNextFocusRightId(rightId);
                        }
                    }
                } else if (i == maxNum) {

                    int leftId = mChildViewList.get(i - 1) instanceof IPopupAnimatView
                            ? ((IPopupAnimatView) mChildViewList.get(i - 1)).getCenterImageView().getId()
                            : mChildViewList.get(i - 1).getId();

                    int rightId = mChildViewList.get(maxNum) instanceof IPopupAnimatView
                            ? ((IPopupAnimatView) mChildViewList.get(maxNum)).getCenterImageView().getId()
                            : mChildViewList.get(maxNum).getId();

                    mCenterView.setNextFocusLeftId(leftId);
                    mCenterView.setNextFocusRightId(rightId);
                    for (TextView tv : textViewList) {
                        tv.setNextFocusLeftId(leftId);
                        tv.setNextFocusRightId(rightId);
                    }

                    //ImageView
                    for (int n = 0; n < imageViewList.size(); n++) {
                        ImageView iv = imageViewList.get(n);
                        switch (n % 3) {
                            case 0:
                                iv.setNextFocusLeftId(leftId);
                                break;
                            case 1:
                                break;
                            case 2:
                                iv.setNextFocusRightId(rightId);
                                break;
                        }
                        if (n == imageViewList.size() - 1) {
                            iv.setNextFocusRightId(rightId);
                        }
                    }

                } else {
                    int leftId = mChildViewList.get(i - 1) instanceof IPopupAnimatView
                            ? ((IPopupAnimatView) mChildViewList.get(i - 1)).getCenterImageView().getId()
                            : mChildViewList.get(i - 1).getId();
                    int rightId = mChildViewList.get(i + 1) instanceof IPopupAnimatView
                            ? ((IPopupAnimatView) mChildViewList.get(i + 1)).getCenterImageView().getId()
                            : mChildViewList.get(i + 1).getId();

                    mCenterView.setNextFocusLeftId(leftId);
                    mCenterView.setNextFocusRightId(rightId);
                    for (TextView tv : textViewList) {
                        tv.setNextFocusLeftId(leftId);
                        tv.setNextFocusRightId(rightId);
                    }
                    //ImageView
                    for (int n = 0; n < imageViewList.size(); n++) {
                        ImageView iv = imageViewList.get(n);
                        switch (n % 3) {
                            case 0:
                                iv.setNextFocusLeftId(leftId);
                                break;
                            case 1:
                                break;
                            case 2:
                                iv.setNextFocusRightId(rightId);
                                break;
                        }
                        if (n == imageViewList.size() - 1) {
                            iv.setNextFocusRightId(rightId);
                        }
                    }
                }

            } else if (!(mChildViewList.get(i) instanceof SimpleCellView)) {
                View view = mChildViewList.get(i);
                view.setNextFocusDownId(view.getId());
                if (i == 0) {
                    int leftId = mChildViewList.get(0) instanceof IPopupAnimatView
                            ? ((IPopupAnimatView) mChildViewList.get(0)).getCenterImageView().getId()
                            : mChildViewList.get(0).getId();

                    int rightId = mChildViewList.get(i + 1) instanceof IPopupAnimatView
                            ? ((IPopupAnimatView) mChildViewList.get(i + 1)).getCenterImageView().getId()
                            : mChildViewList.get(i + 1).getId();

                    view.setNextFocusLeftId(leftId);
                    view.setNextFocusRightId(rightId);

                } else if (i == maxNum) {

                    int leftId = mChildViewList.get(i - 1) instanceof IPopupAnimatView
                            ? ((IPopupAnimatView) mChildViewList.get(i - 1)).getCenterImageView().getId()
                            : mChildViewList.get(i - 1).getId();

                    int rightId = mChildViewList.get(maxNum) instanceof IPopupAnimatView
                            ? ((IPopupAnimatView) mChildViewList.get(maxNum)).getCenterImageView().getId()
                            : mChildViewList.get(maxNum).getId();

                    view.setNextFocusLeftId(leftId);
                    view.setNextFocusRightId(rightId);
                } else {
                    int leftId = mChildViewList.get(i - 1) instanceof IPopupAnimatView
                            ? ((IPopupAnimatView) mChildViewList.get(i - 1)).getCenterImageView().getId()
                            : mChildViewList.get(i - 1).getId();
                    int rightId = mChildViewList.get(i + 1) instanceof IPopupAnimatView
                            ? ((IPopupAnimatView) mChildViewList.get(i + 1)).getCenterImageView().getId()
                            : mChildViewList.get(i + 1).getId();

                    view.setNextFocusLeftId(leftId);
                    view.setNextFocusRightId(rightId);
                }
            }
        }

        for (int i = 0; i < mCellEntityList.size(); i++) {
            int left = mCellEntityList.get(i).getLeft();
            int right = mCellEntityList.get(i).getRight();
            int up = mCellEntityList.get(i).getUp();
            int down = mCellEntityList.get(i).getDown();
            View view;
            if (mChildViewList.get(i) instanceof IPopupAnimatView) {
                view = ((IPopupAnimatView) mChildViewList.get(i)).getCenterImageView();
            } else {
                view = mChildViewList.get(i);
            }

            if (left >= 0) {
                View leftView;
                if (mChildViewList.get(left) instanceof IPopupAnimatView) {
                    leftView = ((IPopupAnimatView) mChildViewList.get(left)).getCenterImageView();
                } else {
                    leftView = mChildViewList.get(left);
                }
                view.setNextFocusLeftId(leftView.getId());
            }else {
                view.setNextFocusLeftId(view.getId());
            }
            if (right < mCellEntityList.size()) {
                View rightView;
                if (mChildViewList.get(right) instanceof IPopupAnimatView) {
                    rightView = ((IPopupAnimatView) mChildViewList.get(right)).getCenterImageView();
                } else {
                    rightView = mChildViewList.get(right);
                }
                view.setNextFocusRightId(rightView.getId());
            }else {
                view.setNextFocusRightId(view.getId());
            }

            if (up >= 0) {
                View upView;
                if (mChildViewList.get(up) instanceof IPopupAnimatView) {
                    upView = ((IPopupAnimatView) mChildViewList.get(up)).getCenterImageView();
                } else {
                    upView = mChildViewList.get(up);
                }
                view.setNextFocusUpId(upView.getId());
            }
            if (down < mCellEntityList.size()) {
                View downView;
                if (mChildViewList.get(down) instanceof IPopupAnimatView) {
                    downView = ((IPopupAnimatView) mChildViewList.get(down)).getCenterImageView();
                } else {
                    downView = mChildViewList.get(down);
                }
                view.setNextFocusDownId(downView.getId());
            } else {
                if (view instanceof CenterImageView) {
                    //TODO:最下排控件指定移动焦点
                } else {
                    view.setNextFocusDownId(view.getId());
                }
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int height = MeasureSpec.getSize(heightMeasureSpec);
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
        }
        setMeasuredDimension(width, height);
    }

    /**
     * 重载onLayout，自行决定控件放置位置
     *
     * @param changed
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            int childNum = getChildCount();
            for (int i = 0; i < childNum; i++) {
                View child = getChildAt(i);
                //焦点控件
                if (child instanceof FocusShadowView) {
                    if (hasFocusView != null) {
                        child.setVisibility(VISIBLE);
                    } else {
                        child.setVisibility(GONE);
                    }
                }
                //IPopupAnimatView控件
                else if (child instanceof IPopupAnimatView) {
                    int left = ((IPopupAnimatView) child).getFinalX();
                    int right = left + ((IPopupAnimatView) child).getFinalW();
                    int top = ((IPopupAnimatView) child).getFinalY() - ((IPopupAnimatView) child).getFinalH() / 2;
                    int bottom = top + ((IPopupAnimatView) child).getFinalH() + ((IPopupAnimatView) child).getFinalH();
//                        FlyLog.d("<PPfunsScrollView>onLayout->child-->left=" + left + ",top=" + top + ",right=" + right + ",bottom=" + bottom);
                    child.layout(left, top, right, bottom);
                }
                //倒影控件
                else if (child instanceof ReflectImageView) {
                    CellEntity cellEntity = ((ReflectImageView) child).getCellData();
                    int left = cellEntity.getX();
                    int top = (int) (cellEntity.getY() + cellEntity.getHeight() + 16 * screenScale);
                    int right = left + cellEntity.getWidth();
                    int bottom = (int) (top + ((ReflectImageView) child).getRefHeight());
                    child.layout(left, top, right, bottom);
                }
                //其它子控件
                else {
                    int num = (int) child.getTag();
                    int left = mCellEntityList.get(num).getX();
                    int right = left + mCellEntityList.get(num).getWidth();
                    int top = mCellEntityList.get(num).getY();
                    int bottom = top + mCellEntityList.get(num).getHeight();
//                        FlyLog.d("<PPfunsScrollView>onLayout->child-->left=" + left + ",top=" + top + ",right=" + right + ",bottom=" + bottom);
                    child.layout(left, top, right, bottom);
                }
            }
        }
    }

    /**
     * 子控件尺寸发生缩放改变，需要重新布局可视控件
     *
     * @param pView
     */
    public void onLayoutWithChangeView(IPopupAnimatView pView, boolean isZoomIn) {
//        FlyLog.d("PopupTvView move onLayoutWithChangeView view width=%d,scorllX=%d,changeWidth=%d",pView.getCellData().getWidth(),getScrollX(),pView.getCellData().getWidth()-pView.getFinalW());
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            int left = 0;
            int right = 0;
            int top = 0;
            int bottom = 0;
            CellEntity cellEntity = null;
            try {
                if (mCellEntityList == null)
                    continue;
                if (child.getTag() == null)
                    continue;
                cellEntity = mCellEntityList.get((Integer) child.getTag());
            } catch (Exception e) {
                e.printStackTrace();
                FlyLog.d(e.toString());
                continue;
            }
            if (child instanceof IPopupAnimatView) {
                left = cellEntity.getX();
                right = left + cellEntity.getWidth();
                top = ((IPopupAnimatView) child).getFinalY() - ((IPopupAnimatView) child).getFinalH() / 2;
                bottom = top + ((IPopupAnimatView) child).getFinalH() + ((IPopupAnimatView) child).getFinalH();
            } else {
                left = cellEntity.getX();
                right = left + cellEntity.getWidth();
                top = cellEntity.getY();
                bottom = top + cellEntity.getHeight();
            }

            //如果不是当前指定的View(包括失去焦点和获得焦点)，做移位处理
            if (((Integer) child.getTag()) != ((Integer) pView.getTag())) {
                if (left < pView.getFinalX()) {
                    left = left - pView.getCellData().getWidth() / 2 + pView.getFinalW() / 2;
                    right = left + cellEntity.getWidth();
                } else if (left > pView.getFinalX()) {
                    left = left + pView.getCellData().getWidth() / 2 - pView.getFinalW() / 2;
                    right = left + cellEntity.getWidth();
                }
            } else {
                if (child instanceof ReflectImageView) {
                    left = pView.getFinalX();
                    right = left + pView.getFinalW();
                }
            }

            if (child instanceof ReflectImageView) {
                top = (int) (pView.getFinalY() + pView.getFinalH() + 16 * screenScale);
                bottom = (int) (top + ((ReflectImageView) child).getRefHeight());
            }

            child.layout(left, top, right, bottom);
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
        }
    }

    /**
     * 设置数据显示控件需显示内容
     *
     * @param cellBean
     */
    public void setData(@NonNull TvCellBean cellBean) {
        List<CellEntity> list = cellBean.getCellList();
        setData(list);
    }

    /**
     * 设置数据显示控件需显示内容
     *
     * @param list
     */
    public void setData(List<CellEntity> list) {

        if (list == null || list.size() == 0) return;
        //删除透明控件
        for (int i = list.size() - 1; i >= 0; i--) {
            if (list.get(i).getText().contains("透明控件")) {
                FlyLog.d("remove cell = %s", list.get(i).toString());
                list.remove(list.get(i));
            }
        }

        Collections.sort(list, new Comparator<CellEntity>() {
            @Override
            public int compare(CellEntity lhs, CellEntity rhs) {
                int compare = lhs.getX() - rhs.getX();
                if (compare == 0) {
                    return lhs.getY() - rhs.getY();
                }
                return compare;
            }
        });

        ICellSortable iCellSortable = new CellSortable();
        list = iCellSortable.getSortedEntity(list);
        int left = Integer.MAX_VALUE;
        int top = Integer.MAX_VALUE;
//        int bottom = Integer.MIN_VALUE;
        mCellEntityList.clear();
        for (CellEntity cell : list) {
            CellEntity entity = cell.clone();
            if (entity == null) {
                entity = cell;
            }
            entity.setCarouselTime(entity.getCarouselTime());
            entity.setX((int) (entity.getX() * screenScale));
            entity.setY((int) (entity.getY() * screenScale));
            entity.setWidth((int) (entity.getWidth() * screenScale));
            entity.setHeight((int) (entity.getHeight() * screenScale));
            entity.setSize((int) (entity.getSize() * screenScale));
            entity.setImageMarginTop((int) (entity.getImageMarginTop()*screenScale*1920f/1280f));
            left = Math.min(left, entity.getX());
            top = Math.min(top, entity.getY());
//            bottom = Math.max(bottom,entity.getY()+entity.getWidth());
            width = Math.max(width, entity.getX() + entity.getWidth());
            height = Math.max(height, entity.getY() + entity.getHeight());
            mCellEntityList.add(entity);
        }
        if (width > 1920 * screenScale) {
            width = left + width;
        }
        if (height > 1080 * screenScale) {
            height = (int) (height + 100 * screenScale);
        }
//        height = top + height;
        AddChildView(mCellEntityList);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        //在此进行控件初始化设置

        //设置第一个控件获取焦点
        if (mChildViewList != null && mChildViewList.size() != 0) {
            View view = mChildViewList.get(0);
            view.requestFocus();
        }

        if (mChildViewList != null) {
            for (int i = 0; i < mChildViewList.size(); i++) {
                View view = mChildViewList.get(i);
                if (view instanceof IPageChangeListener) {
                    ((IPageChangeListener) view).pageIn();
                }
            }
        }


    }

    @Override
    protected void onDetachedFromWindow() {
        //处理各种资源释放工作
        mHandler.removeCallbacksAndMessages(null);

        if (mChildViewList != null) {
            for (int i = 0; i < mChildViewList.size(); i++) {
                View view = mChildViewList.get(i);
                if (view instanceof IPageChangeListener) {
                    ((IPageChangeListener) view).pageOut();
                }
            }
        }

        super.onDetachedFromWindow();
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        if (mChildViewList != null) {
            for (int i = 0; i < mChildViewList.size(); i++) {
                View view = mChildViewList.get(i);
                if (view instanceof IPageChangeListener) {
                    ((IPageChangeListener) view).pageScroll();
                }
            }
        }
    }

    public void setOnKeyDownOutEnvent(IOnKeyDownOutEnvent onKeyDownOutEnvent) {
        this.onKeyDownOutEnvent = onKeyDownOutEnvent;
    }

    /**
     * 所有全局的按键消息放在这里处理
     *
     * @param event
     * @return
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return super.dispatchKeyEvent(event);
    }

    public void setonCellItemClick(BaseTvView.OnCellItemClick onCellItemClick) {
        this.onCellItemClick = onCellItemClick;
    }

    public void loseFocus() {
        iTvFocusAnimat.removeAllSelect();
    }

    /**
     * 判断控件是否需要镜像
     *
     * @param cellEntity
     * @param meNum
     * @return
     */
    private boolean needReflect(CellEntity cellEntity, int meNum) {
        int i_x1 = cellEntity.getX();
        int i_x2 = i_x1 + cellEntity.getWidth();
        int i_y1 = cellEntity.getY();
        int i_y2 = i_y1 + cellEntity.getHeight();
        boolean flag = true;
        for (int j = 0; j < mCellEntityList.size(); j++) {
            int j_x1 = mCellEntityList.get(j).getX();
            int j_x2 = j_x1 + mCellEntityList.get(j).getWidth();
            int j_y1 = mCellEntityList.get(j).getY();
            int j_y2 = j_y1 + mCellEntityList.get(j).getHeight();
            if (j != meNum) {
                if (((j_x1 >= i_x1 && j_x1 <= i_x2) || (j_x2 >= i_x1 && j_x2 <= i_x2)) || ((i_x1 >= j_x1 && i_x1 <= j_x2) || (i_x2 >= j_x1 && i_x2 <= j_x2))) {
                    if (j_y2 > i_y2) {
                        flag = false;
                        break;
                    }
                }
            }
        }
        return flag;
    }


    public PopupTvViewLayout initListenerAnimateState() {

        iTvFocusAnimat.setListenerAnimateState(new ITvFocusAnimat.ListenerAnimtaState() {
            @Override
            public void finishAnimate() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (hasFocusView != null) ;
                        hasFocusView.bringToFront();
                    }
                });
            }
        });


        return this;
    }

    public void notifyPageChange(int type) {
//        FlyLog.d("trigger live event, 0 is play in and 1 is stop out,type:" + type);
//        Boolean isTop = false;
//        try {
//            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//            List<ActivityManager.RunningTaskInfo> appTask = am.getRunningTasks(1);
//            isTop = appTask.size() > 0 && appTask.get(0).topActivity.equals(((Activity) context).getIntent().getComponent());
//        } catch (SecurityException e) {
//            e.printStackTrace();
//            FlyLog.e(e.toString());
//        }
        if (mChildViewList == null) return;
        if (0 == type) {
//            if (isTop) {
            for (int i = 0; i < mChildViewList.size(); i++) {
                View view = mChildViewList.get(i);
                if (view instanceof IPageChangeListener) {
                    ((IPageChangeListener) view).pageIn();
                }
            }
//            } else {
//                FlyLog.d("not in launcher ,no need to play live");
//            }
        } else {
            for (int i = 0; i < mChildViewList.size(); i++) {
                View view = mChildViewList.get(i);
                if (view instanceof IPageChangeListener) {
                    ((IPageChangeListener) view).pageOut();
                }
            }
        }
    }


    public static class Builder {
        private Context context;
        private IOnKeyDownOutEnvent onKeyDownOutEnvent;
        private BaseTvView.OnCellItemClick onCellItemClick;
        private int resId = R.drawable.tv_default;
        private boolean isShowReflect = true;
        private int animStyle;
        private boolean shadowBringToFront;
        private int shadowAmend;
        private IDiskCache iDiskCache;
        private BitmapCache mBitmapcache;
        private int animDuration;
        private int[] mFocusResIDs;
        private boolean isUseWallPager;


        public Builder setLoadImageResID(int resId) {
            this.resId = resId;
            return this;
        }

        public Builder animDuration(int animDuration) {
            this.animDuration = animDuration;
            return this;
        }

        public Builder context(Context context) {
            this.context = context;
            return this;
        }

        public Builder iDiskCache(IDiskCache iDiskCache) {
            this.iDiskCache = iDiskCache;
            return this;
        }

        public Builder setBitmapCache(BitmapCache bitmapCache) {
            this.mBitmapcache = bitmapCache;
            return this;
        }

        public Builder onKeyDownOutEnvent(IOnKeyDownOutEnvent onKeyDownOutEnvent) {
            this.onKeyDownOutEnvent = onKeyDownOutEnvent;
            return this;
        }

        public Builder onCellItemClick(BaseTvView.OnCellItemClick onCellItemClick) {
            this.onCellItemClick = onCellItemClick;
            return this;
        }

        public Builder isShowReflect(boolean isShowReflect) {
            this.isShowReflect = isShowReflect;
            return this;
        }

        public Builder animStyle(int animStyle) {
            this.animStyle = animStyle;
            return this;
        }


        public Builder shadowBringToFront(boolean shadowBringToFront) {
            this.shadowBringToFront = shadowBringToFront;
            return this;
        }

        public Builder setFocusResIDs(@DrawableRes int[] shadowRids) {
            this.mFocusResIDs = shadowRids;
            return this;
        }

        public Builder shadowAmend(int shadowAmend) {
            this.shadowAmend = shadowAmend;
            return this;
        }

        public Builder isUseWallPager(boolean isUseWallPager){
            this.isUseWallPager= isUseWallPager;
            return this;
        }


        public PopupTvViewLayout createView() {
            PopupTvViewLayout popupTvViewLayout = new PopupTvViewLayout(context);
            popupTvViewLayout.setOnKeyDownOutEnvent(onKeyDownOutEnvent);
            popupTvViewLayout.setonCellItemClick(onCellItemClick);
            popupTvViewLayout.iDiskCache = iDiskCache;
            popupTvViewLayout.mBitmapCache = mBitmapcache;
            popupTvViewLayout.isShowReflect = isShowReflect;
            popupTvViewLayout.isUseWallPager = isUseWallPager;

            if (this.animDuration == 0) {
                this.animDuration = 300;
            }

            popupTvViewLayout.resId = resId;
            popupTvViewLayout.mAnimDuration = animDuration;

            switch (animStyle) {
                case ITvFocusAnimat.TV_PAGE_NOT_MOVE_ANIM:
                    popupTvViewLayout.iTvFocusAnimat = new PopupTvAnimat2(popupTvViewLayout);
                    break;
                case ITvFocusAnimat.TV_PAGE_MOVE_ANIM:
                default:
                    popupTvViewLayout.iTvFocusAnimat = new PopupTvAnimat(popupTvViewLayout);
                    break;
            }
            popupTvViewLayout.iTvFocusAnimat.setShadowAmend(shadowAmend);
            popupTvViewLayout.iTvFocusAnimat.setAnimduartion(animDuration);
            if(mFocusResIDs!=null){
                popupTvViewLayout.iTvFocusAnimat.setFocusResIDs(mFocusResIDs);
            }
            popupTvViewLayout.initListenerAnimateState();

            return popupTvViewLayout;
        }
    }


}
