package com.flyzebra.ppfunstv.view.TvView.PPfunsTV;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.DrawableRes;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Scroller;

import com.flyzebra.flyui.chache.IDiskCache;
import com.flyzebra.ppfunstv.R;
import com.flyzebra.ppfunstv.data.CellEntity;
import com.flyzebra.ppfunstv.module.BitmapCache;
import com.flyzebra.ppfunstv.utils.DisplayUtils;
import com.flyzebra.ppfunstv.utils.FlyLog;
import com.flyzebra.ppfunstv.view.ReflectImageView;
import com.flyzebra.ppfunstv.view.TvView.CellSortable;
import com.flyzebra.ppfunstv.view.TvView.CellView.CarouselCellView;
import com.flyzebra.ppfunstv.view.TvView.CellView.ITvPageItemView;
import com.flyzebra.ppfunstv.view.TvView.CellView.TvPageItemView;
import com.flyzebra.ppfunstv.view.TvView.CellView.TvPageViewItemFactory;
import com.flyzebra.ppfunstv.view.TvView.FocusAnimat.IAnimatView;
import com.flyzebra.ppfunstv.view.TvView.FocusAnimat.ITvFocusAnimat;
import com.flyzebra.ppfunstv.view.TvView.FocusAnimat.TvPageAnimat;
import com.flyzebra.ppfunstv.view.TvView.FocusAnimat.TvPageNotMoveAnimat;
import com.flyzebra.ppfunstv.view.TvView.ICellSortable;
import com.flyzebra.ppfunstv.view.TvView.IOnKeyDownOutEnvent;
import com.flyzebra.ppfunstv.view.TvView.IPageChangeListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 * Created by FlyZebra on 2016/6/13.
 */
public class TvPageLayout extends ViewGroup {
    public boolean isFouce = false;
    private int width;
    private Scroller mScroller;
    private int startScrollX, endScrollX, startX;
    /**
     * Cell数据列表
     */
    private List<CellEntity> mCellEntityList = new ArrayList<>();
    /**
     * Cell控件列表
     */
    private List<ITvPageItemView> cellViewList;
    /**
     * 播放动画所需的控件列表
     */
    private List<View> addAminaView = new ArrayList<>();
    /**
     * 需要投影的控件列表
     */
    private List<ReflectImageView> refImageViewlist = new ArrayList<>();
    private ITvFocusAnimat iTvFocusAnimat;
    private ICellSortable iCellSortable;
    private Context context;
    /**
     * 默认屏幕宽度
     */
    private double SCREEN_WIDTH = 1024;
    /**
     * 多分辨率适配系数
     */
    private double screenScale = 1.0;
    /**
     * 磁盘缓存接口
     */
    private IDiskCache iDiskCache;
    private BitmapCache mBitmapCache;
    /**
     * 滚动显示下一个Item所需时间
     */
    private int scrollDuration = 150;
    /**
     * 最左边的控件跟屏幕左边的边距
     */
    private int min_x;
    private boolean isDelayPlayAnim = false;
    private int selectItem = -1;
    private boolean isAnimPlay = false;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private int mAnimDuration = 200;
    private Runnable animTask = new Runnable() {
        @Override
        public void run() {
            isAnimPlay = false;
        }
    };
    private OnCellItemClick onCellItemClick;
    private IOnKeyDownOutEnvent onKeyDownOutEnvent;
    private List<IPageChangeListener> mPageChangeListener = new ArrayList<>();
    private int leftUpIndex = -1; //左上角cell index
    private int rightUpIndex = -1;//右上角cell index
    private boolean isShowReflect;
    private int resId = R.drawable.tv_default;
    private boolean isUseWallPager;


    public TvPageLayout(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        setFocusable(false);
//        iTvFocusAnimat = new TvPageAnim(this);

        iCellSortable = new CellSortable();
        mScroller = new Scroller(context);
        screenScale = DisplayUtils.getMetrices((Activity) context).widthPixels / 1024f;
        setClipChildren(false);
        setClipToPadding(false);
    }

    public void setDelayPlayAnim(boolean delayPlayAnim) {
        isDelayPlayAnim = delayPlayAnim;
    }


    /**
     * 设置占位图
     *
     * @param resId
     */
    public void setLoadImageResId(int resId) {
        this.resId = resId;
    }

    public TvPageLayout setShowReflect(boolean showReflect) {
        isShowReflect = showReflect;
        return this;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (cellViewList == null) return;
        if (changed) {
            /**
             * 动画控件
             */
            for (int i = 0; i < addAminaView.size(); i++) {
                if (selectItem >= 0 && selectItem < cellViewList.size()) {
                    View view = addAminaView.get(i);
                    int left = view.getLeft();
                    int top = view.getTop();
                    int width = view.getLayoutParams().width;
                    int height = view.getLayoutParams().height;
                    int right = left + width;
                    int bottom = top + height;
                    view.layout(left, top, right, bottom);
                }
            }
            /**
             * Cell控件
             */
            for (int i = 0; i < cellViewList.size(); i++) {
                int left = cellViewList.get(i).getCellData().getX();
                int top = cellViewList.get(i).getCellData().getY();
                int right = left + cellViewList.get(i).getCellData().getWidth();
                int bottom = top + cellViewList.get(i).getCellData().getHeight();
                ((View) cellViewList.get(i)).layout(left, top, right, bottom);
            }

            /**
             * 倒影控件
             */
            for (int i = 0; i < refImageViewlist.size(); i++) {
                CellEntity cellEntity = refImageViewlist.get(i).getCellData();
                int left = cellEntity.getX();
                int top = (int) (cellEntity.getY() + cellEntity.getHeight() + 4 * screenScale);
                int right = left + cellEntity.getWidth();
                int bottom = (int) (top + refImageViewlist.get(i).getRefHeight() * screenScale);
                refImageViewlist.get(i).layout(left, top, right, bottom);
            }
        }
    }

    /**
     * 设置子控件数据
     *
     * @param list
     * @return
     */
    public TvPageLayout setCellData(List<CellEntity> list, int beforCount) {
        FlyLog.d("setCellData start");
        if (list == null || list.size() == 0) {
            return this;
        }

        //排序
        Collections.sort(list, new Comparator<CellEntity>() {
            @Override
            public int compare(CellEntity lhs, CellEntity rhs) {
                int compare = lhs.getX() - rhs.getX();
                if (compare == 0) {
                    compare = lhs.getY() - rhs.getY();
                }
                return compare;
            }
        });


        List<CellEntity> lists = iCellSortable.getSortedEntity(list);
        if (lists != null) {
            mCellEntityList.clear();
            min_x = Integer.MAX_VALUE;
            int max_x = Integer.MIN_VALUE;
            for (CellEntity cell : lists) {
                CellEntity entity = cell.clone();
                if (entity == null) {
                    entity = cell;
                }
                entity.setCarouselTime(entity.getCarouselTime());
                entity.setX((int) (entity.getX() * screenScale));
                entity.setY((int) (entity.getY() * screenScale));
                entity.setWidth((int) (entity.getWidth() * screenScale) + (int) (entity.getWidth() * screenScale) % 2);
                entity.setHeight((int) (entity.getHeight() * screenScale) + (int) (entity.getHeight() * screenScale) % 2);
                entity.setSize((int) (entity.getSize() * screenScale));
                min_x = Math.min(min_x, entity.getX());
                max_x = Math.max(max_x, entity.getX() + entity.getWidth());
                mCellEntityList.add(entity);
            }
            width = min_x + max_x;
            setData(mCellEntityList, beforCount);

        }
        FlyLog.d("setCellData end");
        return this;
    }

    private boolean setData(List<CellEntity> cellEntityList, int beforCount) {
        if (cellEntityList == null) {
            return false;
        }
        if (cellViewList == null) {
            cellViewList = new ArrayList<>();
        } else {
            removeAllViews();
            cellViewList.clear();
        }

        if (refImageViewlist != null) {
            refImageViewlist.clear();
        }

        for (int i = 0; i < cellEntityList.size(); i++) {
            final int currentItem = i;
            final CellEntity cellEntity = cellEntityList.get(i);
            FlyLog.d("create Cell=%s",cellEntity.toString());
            final ITvPageItemView view = TvPageViewItemFactory.createView(context, iDiskCache, mBitmapCache, cellEntity,resId);
            if(view ==null){
                continue;
            }
            view.setAnimtorDurtion(mAnimDuration);
            view.isUseWallPager(isUseWallPager);
            if(view instanceof IAnimatView){
                ((IAnimatView)view).setFocusAnimate(iTvFocusAnimat);
            }
            TvPageViewItemFactory.handleExtend(context,cellEntity);
            view.setCellData(cellEntity);
            ((View) view).setTag(i);
            cellViewList.add(view);
            super.addView(((View) view));
//            try {
                measureChild((View) view, getMeasuredWidth(), getMeasuredHeight());
//            }catch (Exception e){
//                FlyLog.d(e.toString());
//            }
            if (view instanceof IPageChangeListener) {
                addPageChangeListener((IPageChangeListener) view);
            }

            ((View) view).setFocusable(false);
            ((View) view).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        int i = (int) v.getTag();
                        if (currentItem == selectItem) {
                            //如果鼠标点击已经选中的View，触发控件的动作事件
                            if (onCellItemClick != null) {
                                onCellItemClick.onItemClick((TvPageItemView) cellViewList.get(i));
                            }
                        } else {
                            Select(currentItem);
                            if (onCellItemClick != null && v instanceof TvPageItemView) {
                                onCellItemClick.onItemClick((TvPageItemView) v);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        FlyLog.e(e.toString());
                    }
                }
            });

            /*
             *最后一排显示倒影
             */
            if (needReflect(cellEntity, currentItem) && isShowReflect) {
                final ReflectImageView refView = new ReflectImageView(context);
                refView.setCellData(cellEntity);
                refView.setRefHeight((int) (100 * screenScale));
                refView.setScaleType(ImageView.ScaleType.FIT_XY);
                refImageViewlist.add(refView);
                super.addView(refView);
                view.bindReflectView(refView);
            }
            final int n = beforCount + i;
            /**
             * 显示图像
             */
            //view.showImage(cellEntity.getImgUrl(), loadImageResId);
            //CarouselCellView的实现不支持延时加载
            if (!(view instanceof CarouselCellView)) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        FlyLog.d("showImage-%d, %s", n, cellEntity.getImgUrl());
                        view.showImage(cellEntity.getImgUrl());
                    }
                }, (beforCount + i) * 10);
            } else {
                view.showImage(cellEntity.getImgUrl());
            }
        }

        return true;
    }

    private void Select(int currentItem) {
        FlyLog.d("<TvPageLayout>Select currentItem=" + currentItem);
        if (mCellEntityList == null || mCellEntityList.size() == 0) {
            if (onKeyDownOutEnvent != null) {
                onKeyDownOutEnvent.onKeyDownGoUp(this);
            }
            return;
        }
        ScrollToCurrentItem(currentItem);
        playAnim(selectItem, currentItem);
        if (selectItem >= 0 && selectItem < cellViewList.size()) {
//            cellViewList.get(selectItem).notifyFocusChanged(false);

        }
        selectItem = currentItem;
        if (selectItem >= 0 && selectItem < cellViewList.size()) {
//            cellViewList.get(selectItem).notifyFocusChanged(true);
        }
    }

    public void setOnCellItemClick(OnCellItemClick onCellItemClick) {
        this.onCellItemClick = onCellItemClick;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (isAnimPlay && isDelayPlayAnim) {
            return true;
        }
        if (selectItem == -1 || mCellEntityList == null || mCellEntityList.size() <= selectItem) {
            Select(0);
            return super.onKeyDown(keyCode, event);
        }

        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP:
                int up = mCellEntityList.get(selectItem).getUp();
                /**
                 * 跳过不能获取焦点的控件
                 */
                while ((up >= 0 && up < mCellEntityList.size()) && !cellViewList.get(up).getCellData().getCanFocus()) {
                    if (mCellEntityList.get(selectItem).getUps().peek() == null) {
                        up = Integer.MAX_VALUE;
                        break;
                    }
                    up = mCellEntityList.get(selectItem).getUps().poll();
                    mCellEntityList.get(selectItem).setUp(up);
                }
                if (up >= 0) {
                    Select(up);
                } else {
                    setFocusState(false);
                    if (onKeyDownOutEnvent != null) {
                        onKeyDownOutEnvent.onKeyDownGoUp(this);
                    }
                }
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                int down = mCellEntityList.get(selectItem).getDown();
                /**
                 * 跳过不能获取焦点的控件
                 */
                while (down >= 0 && down < mCellEntityList.size() && !cellViewList.get(down).getCellData().getCanFocus()) {
                    if (mCellEntityList.get(selectItem).getDowns().peek() == null) {
                        down = Integer.MAX_VALUE;
                        break;
                    }
                    down = mCellEntityList.get(selectItem).getDowns().poll();
                    mCellEntityList.get(selectItem).setDown(down);
                }
                if (down < cellViewList.size()) {
                    Select(down);
                } else {
                    if (onKeyDownOutEnvent != null) {
                        onKeyDownOutEnvent.onKeyDownGoDown(this);
                    }
                }
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                int left = mCellEntityList.get(selectItem).getLeft();
                /**
                 * 跳过不能获取焦点的控件
                 */
                while ((left >= 0 && left < mCellEntityList.size()) && !cellViewList.get(left).getCellData().getCanFocus()) {
                    if (mCellEntityList.get(selectItem).getLefts().peek() == null) {
                        left = -1;
                        break;
                    }
                    left = mCellEntityList.get(selectItem).getLefts().poll();
                    mCellEntityList.get(selectItem).setLeft(left);
                }
                if (left >= 0) {
                    Select(left);
                } else {
                    if (onKeyDownOutEnvent != null) {
                        if (onKeyDownOutEnvent.onKeyDownGoLeft(this)) {
                            setFocusState(false);
                            notifyPageChange(1);
                        }
                    }
                }
                return true;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                int right = mCellEntityList.get(selectItem).getRight();
                /**
                 * 跳过不能获取焦点的控件
                 */
                while (right >= 0 && right < mCellEntityList.size() && !cellViewList.get(right).getCellData().getCanFocus()) {
                    if (mCellEntityList.get(selectItem).getRights().peek() == null) {
                        right = Integer.MAX_VALUE;
                        break;
                    }
                    right = mCellEntityList.get(selectItem).getRights().poll();
                    mCellEntityList.get(selectItem).setRight(right);
                }
                if (right < cellViewList.size()) {
                    Select(right);
                } else {
                    if (onKeyDownOutEnvent != null) {
                        if (onKeyDownOutEnvent.onKeyDownGoRight(this)) {
                            setFocusState(false);
                            notifyPageChange(1);
                        }
                    }
                }
                break;
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_DPAD_CENTER:
//                cellViewList.get(selectItem).doAction();
                if (onCellItemClick != null) {
                    onCellItemClick.onItemClick(cellViewList.get(selectItem));
                }
                break;
        }
        if (event.getAction() == KeyEvent.ACTION_UP) {
            mHandler.removeCallbacks(animTask);
            isAnimPlay = false;
        }

        return super.onKeyDown(keyCode, event);
    }

    public void setOnKeyDownOutEnvent(IOnKeyDownOutEnvent onKeyDownOutEnvent) {
        this.onKeyDownOutEnvent = onKeyDownOutEnvent;
    }

    public void RemoveAllselect() {
        iTvFocusAnimat.removeAllSelect();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = (int) event.getX();
                startScrollX = getScrollX();
                break;
            case MotionEvent.ACTION_MOVE:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                endScrollX = (int) (startX - event.getX()) + startScrollX;
                if (endScrollX < 0) {
                    endScrollX = 0;
                } else if (endScrollX > getWidth() - SCREEN_WIDTH * screenScale) {
                    endScrollX = (int) (getWidth() - SCREEN_WIDTH * screenScale);
                }
                scrollTo(endScrollX, 0);
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
        } else {
            setVisibility(VISIBLE);
        }
    }

    @Override
    public void addView(View child) {
        super.addView(child);
        addAminaView.add(child);
    }

    public void addPageChangeListener(IPageChangeListener listener) {
        if (!mPageChangeListener.contains(listener)) {
            mPageChangeListener.add(listener);
        }
    }

    /**
     * 通知页面改变
     *
     * @param type 0:进入页面 1:退出页面
     */
    public void notifyPageChange(int type) {
        FlyLog.d("trigger live event, 0 is play in and 1 is stop out,type:" + type);
        Boolean isTop = false;
        try {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> appTask = am.getRunningTasks(1);
            isTop = appTask.size() > 0 && appTask.get(0).topActivity.equals(((Activity) context).getIntent().getComponent());
        } catch (SecurityException e) {
            e.printStackTrace();
            FlyLog.e(e.toString());
        }
        if (mPageChangeListener != null) {
            if (0 == type) {
                if (isTop) {
                    for (IPageChangeListener item : mPageChangeListener) {
                        item.pageIn();
                    }

                } else {
                    FlyLog.d("not in launcher ,no need to play live");
                }
            } else {
                for (IPageChangeListener item : mPageChangeListener) {
                    item.pageOut();
                }
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        FlyLog.d("<TvPageLayout>onAttachedToWindow");
    }

    @Override
    protected void onDetachedFromWindow() {
        mHandler.removeCallbacksAndMessages(null);
        super.onDetachedFromWindow();
        FlyLog.d("<TvPageLayout>onDetachedFromWindow");
    }

    /**
     * 以选择项为目标滚动窗口
     *
     * @param currentItem
     */
    private void ScrollToCurrentItem(int currentItem) {
        int crt_x = getScrollX();
        int left_x = mCellEntityList.get(currentItem).getX() - min_x;
        int right_x = mCellEntityList.get(currentItem).getX() + mCellEntityList.get(currentItem).getWidth() + min_x;
        int screen_w = (int) (SCREEN_WIDTH * screenScale);

        if (left_x - crt_x < 0) {
            mScroller.startScroll(crt_x, 0, left_x - crt_x, 0, scrollDuration);
        } else if ((right_x - crt_x) >= screen_w) {
            mScroller.startScroll(crt_x, 0, (int) (right_x - SCREEN_WIDTH * screenScale) - crt_x, 0, scrollDuration);
        }
    }

    public TvPageLayout setDiskCache(IDiskCache iDiskCache) {
        this.iDiskCache = iDiskCache;
        return this;
    }


    public TvPageLayout setBitmapCache(BitmapCache bitmapCache) {
        this.mBitmapCache = bitmapCache;
        return this;
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

    public boolean getFocusState() {
        return isFouce;
    }

    public void setFocusState(boolean flag) {
        this.isFouce = flag;
        if (flag) {
            if (selectItem == -1) {
                selectItem = getLeftUpCellIndex();
            }
            playAnim(-1, selectItem);
        } else {
            RemoveAllselect();
        }
    }

    /**
     * 移动到特定位置(左上角和右上角)
     *
     * @param direction 0:移动到左上角
     *                  1:移动到右上角
     * @param focus     true:获取焦点
     *                  false:不获取焦点
     */
    public void setFocusEffect(int direction, boolean focus) {
        if (0 == direction) {
            scrollTo(0, 0);
            selectItem = getLeftUpCellIndex();
            //跳过不能获取焦点的控件
            while (selectItem >= 0 && selectItem < mCellEntityList.size() && !cellViewList.get(selectItem).getCellData().getCanFocus()) {
                selectItem = mCellEntityList.get(selectItem).getRight();
            }
            if (selectItem >= mCellEntityList.size()) {//没有找到可或焦的
                selectItem = mCellEntityList.size() - 1;
            }
        } else {
            int width = this.width;
            int windowWidth = DisplayUtils.getMetrices((Activity) context).widthPixels;
            if (width > windowWidth) {//只有需要移动的时候进行平移
                scrollTo(width - windowWidth, 0);
            }
            selectItem = getRightUpCellIndex();
            //跳过不能获取焦点的控件
            while (selectItem >= 0 && !cellViewList.get(selectItem).getCellData().getCanFocus()) {
                selectItem = mCellEntityList.get(selectItem).getLeft();
            }
            if (selectItem < 0) {//没有找到可或焦的
                selectItem = 0;
            }
        }

        if (focus) {
            playAnim(-1, selectItem);
        }
    }

    private void playAnim(int selectItem, int currentItem) {
        if (currentItem < 0 || cellViewList == null || cellViewList.size() <= currentItem) {
            return;
        }
        if (selectItem == -1) {
            iTvFocusAnimat.startAnim(null, (IAnimatView)cellViewList.get(currentItem));
        } else {
            iTvFocusAnimat.startAnim((IAnimatView)cellViewList.get(selectItem), (IAnimatView)cellViewList.get(currentItem));
        }
        isAnimPlay = true;
        mHandler.postDelayed(animTask, mAnimDuration);
    }

    /**
     * 判断本页是否有数据
     *
     * @return true:有数据
     * false:没有数据
     */
    public boolean hasData() {
        return !(cellViewList == null || cellViewList.size() == 0);
    }

    /**
     * 获取左上角cell index
     */
    public int getLeftUpCellIndex() {
        if (leftUpIndex == -1) {
            leftUpIndex = iCellSortable.getLeftUpIndex(mCellEntityList);
        }
        return leftUpIndex;
    }

    /**
     * 获取右上角cell index
     */
    public int getRightUpCellIndex() {
        if (rightUpIndex == -1) {
            rightUpIndex = iCellSortable.getRightUpIndex(mCellEntityList);
        }
        return rightUpIndex;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        if (cellViewList != null) {
            for (int i = 0; i < cellViewList.size(); i++) {
                View view = (View) cellViewList.get(i);
                if (view instanceof IPageChangeListener) {
                    ((IPageChangeListener) view).pageScroll();
                }
            }
        }
    }

    /**
     * 设置启动时被要求设置成焦点的控件为获取焦点状态
     *
     * @return true 有默认焦点控件已经设置，false 无默认焦点控件没有设置
     */
    public boolean setDefaultFocus(int cellId) {
        for (int i = 0; i < mCellEntityList.size(); i++) {
            if (mCellEntityList.get(i).getCellId() == cellId) {
                selectItem = i;
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        playAnim(-1, selectItem);
                    }
                }, 100);
                return true;
            }
        }
        return false;
    }


    public interface OnCellItemClick {
        void onItemClick(ITvPageItemView view);
    }


    public static class Builder {

        private Context context;
        private IOnKeyDownOutEnvent onKeyDownOutEnvent;
        private OnCellItemClick onCellItemClick;
        private int resId = R.drawable.tv_default;
        private boolean isShowReflect = true;
        private int animStyle;
        private int[] mFocusResIDs;
        private int shadowAmend;
        private IDiskCache iDiskCache;
        private BitmapCache mBitmapCache;
        private int animDuration;

        private List<CellEntity> list;
        private int beforeCount;
        private boolean isUseWallPager;

        public Builder loadImageResId(int resId) {
            this.resId = resId;
            return this;
        }

        public Builder cellDate(List<CellEntity> list, int beforeCount) {
            this.list = list;
            this.beforeCount = beforeCount;
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

        public Builder bitmapCache(BitmapCache bitmapCache) {
            this.mBitmapCache = bitmapCache;
            return this;
        }

        public Builder onKeyDownOutEnvent(IOnKeyDownOutEnvent onKeyDownOutEnvent) {
            this.onKeyDownOutEnvent = onKeyDownOutEnvent;
            return this;
        }

        public Builder onCellItemClick(OnCellItemClick onCellItemClick) {
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

        public Builder setFocusResIDs(@DrawableRes int[] shadowRid) {
            this.mFocusResIDs = shadowRid;
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


        public TvPageLayout createView() {
            TvPageLayout tvPageLayout = new TvPageLayout(this.context);

            tvPageLayout.setOnKeyDownOutEnvent(onKeyDownOutEnvent);
            tvPageLayout.setOnCellItemClick(onCellItemClick);
            tvPageLayout.isShowReflect = isShowReflect;
            tvPageLayout.iDiskCache = iDiskCache;
            tvPageLayout.mBitmapCache = mBitmapCache;
            tvPageLayout.isUseWallPager = isUseWallPager;
            if (this.animDuration == 0) {
                this.animDuration = 300;
            }
            tvPageLayout.mAnimDuration = animDuration;
            tvPageLayout.setLoadImageResId(resId);
            switch (animStyle) {
                case ITvFocusAnimat.TV_PAGE_NOT_MOVE_ANIM:
                    tvPageLayout.iTvFocusAnimat = new TvPageNotMoveAnimat(tvPageLayout);
                    break;
                case ITvFocusAnimat.TV_PAGE_MOVE_ANIM:
                default:
                    tvPageLayout.iTvFocusAnimat = new TvPageAnimat(tvPageLayout);
                    break;
            }
            tvPageLayout.iTvFocusAnimat.setAnimduartion(animDuration);
            tvPageLayout.iTvFocusAnimat.setShadowAmend(shadowAmend);
            if(mFocusResIDs!=null){
                tvPageLayout.iTvFocusAnimat.setFocusResIDs(mFocusResIDs);
            }

            tvPageLayout.setCellData(this.list, beforeCount);
            return tvPageLayout;
        }
    }

}

