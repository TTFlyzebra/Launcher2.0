package com.flyzebra.flyuitv.view;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Scroller;

import com.flyzebra.flyui.bean.CellBean;
import com.flyzebra.flyui.bean.PageBean;
import com.flyzebra.flyui.view.cellview.CellViewFactory;
import com.flyzebra.flyui.view.cellview.ICell;
import com.flyzebra.flyui.view.customview.MirrorView;
import com.flyzebra.flyui.view.pageview.IPage;
import com.flyzebra.flyuitv.CellSortable;
import com.flyzebra.flyuitv.ICellSortable;
import com.flyzebra.ppfunstv.utils.DisplayUtils;
import com.flyzebra.ppfunstv.utils.FlyLog;
import com.flyzebra.ppfunstv.view.TvView.FocusAnimat.IAnimatView;
import com.flyzebra.ppfunstv.view.TvView.FocusAnimat.TvPageAnimat;
import com.flyzebra.ppfunstv.view.TvView.IOnKeyDownOutEnvent;
import com.flyzebra.ppfunstv.view.TvView.IPageChangeListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by FlyZebra on 2016/6/13.
 */
public class TvPageView extends FrameLayout implements IPage {
    public boolean isFouce = false;
    private int width;
    private Scroller mScroller;
    private int startScrollX, endScrollX, startX;
    /**
     * Cell数据列表
     */
    private List<CellBean> cellBeans = new ArrayList<>();
    /**
     * Cell控件列表
     */
    private List<View> addAminaView = new ArrayList<>();
    private ICellSortable iCellSortable;
    private Context context;
    /**
     * 默认屏幕宽度
     */
    private double SCREEN_WIDTH = 1920;
    /**
     * 多分辨率适配系数
     */
    private double screenScale = 1.0;
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
    private IOnKeyDownOutEnvent onKeyDownOutEnvent;
    private List<IPageChangeListener> mPageChangeListener = new ArrayList<>();
    private int leftUpIndex = -1; //左上角cell index
    private int rightUpIndex = -1;//右上角cell index
    private PageBean mPageBean;
    private TvPageAnimat iTvFocusAnimat;


    public TvPageView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        setFocusable(false);
        iTvFocusAnimat = new TvPageAnimat(this);

        iCellSortable = new CellSortable();
        mScroller = new Scroller(context);
        screenScale = DisplayUtils.getMetrices((Activity) context).widthPixels / 1920f;
        setClipChildren(false);
        setClipToPadding(false);
    }

    public void setDelayPlayAnim(boolean delayPlayAnim) {
        isDelayPlayAnim = delayPlayAnim;
    }


    /**
     * 设置子控件数据
     *
     * @param list
     * @return
     */
    public void setCellData(List<CellBean> list) {
        FlyLog.d("setCellData start");
        if (list == null || list.size() == 0) {
            return;
        }

        //排序
        Collections.sort(list, new Comparator<CellBean>() {
            @Override
            public int compare(CellBean lhs, CellBean rhs) {
                int compare = lhs.x - rhs.x;
                if (compare == 0) {
                    compare = lhs.y - rhs.y;
                }
                return compare;
            }
        });


        List<CellBean> lists = iCellSortable.getSortedEntity(list);
        if (lists != null) {
            cellBeans.clear();
            cellBeans.addAll(lists);
            min_x = Integer.MAX_VALUE;
            int max_x = Integer.MIN_VALUE;
            for (CellBean cell : lists) {
                min_x = Math.min(min_x, cell.x);
                max_x = Math.max(max_x, cell.x + cell.width);
            }
            width = min_x + max_x;

        }
        addAllItemView(cellBeans);
        FlyLog.d("setCellData end");
    }

    private void addAllItemView(List<CellBean> cellBeans) {
        if (cellBeans == null || cellBeans.isEmpty()) return;
        for (int i = 0; i < cellBeans.size(); i++) {
            //多出的Cell不进行绘制
            CellBean cellBean = cellBeans.get(i);
            ICell iCellView = CellViewFactory.createView(getContext(), cellBean);
            LayoutParams lp;

            lp = new LayoutParams(cellBean.width, cellBean.height);
            lp.setMarginStart(cellBean.x);
            lp.topMargin = cellBean.y;

            //添加镜像
            LayoutParams lpMirror;
            lpMirror = new LayoutParams(cellBean.width, MirrorView.MIRRORHIGHT);
            lpMirror.setMarginStart(cellBean.x);
            lpMirror.topMargin = lp.topMargin + cellBean.height;
            iCellView.bindMirrorView(this, lpMirror);
            iCellView.setCellBean(cellBean);
            addView((View) iCellView, lp);
        }
    }

    private void select(int currentItem) {
        FlyLog.d("<TvPageLayout>select currentItem=" + currentItem);
        if (cellBeans == null || cellBeans.size() == 0) {
            if (onKeyDownOutEnvent != null) {
                onKeyDownOutEnvent.onKeyDownGoUp(this);
            }
            return;
        }
        scrollToCurrentItem(currentItem);
        playAnim(selectItem, currentItem);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (isAnimPlay && isDelayPlayAnim) {
            return true;
        }
        if (selectItem == -1 || cellBeans == null || cellBeans.size() <= selectItem) {
            select(0);
            return super.onKeyDown(keyCode, event);
        }

        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP:
                int up = cellBeans.get(selectItem).up;
                /**
                 * 跳过不能获取焦点的控件
                 */
                while ((up >= 0 && up < cellBeans.size()) && !cellBeans.get(up).isFocus) {
                    if (cellBeans.get(selectItem).ups.peek() == null) {
                        up = Integer.MAX_VALUE;
                        break;
                    }
                    up = cellBeans.get(selectItem).ups.poll();
                    cellBeans.get(selectItem).up = up;
                }
                if (up >= 0) {
                    select(up);
                } else {
                    setFocusState(false);
                    if (onKeyDownOutEnvent != null) {
                        onKeyDownOutEnvent.onKeyDownGoUp(this);
                    }
                }
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                int down = cellBeans.get(selectItem).down;
                /**
                 * 跳过不能获取焦点的控件
                 */
                while (down >= 0 && down < cellBeans.size() && !cellBeans.get(down).isFocus) {
                    if (cellBeans.get(selectItem).downs.peek() == null) {
                        down = Integer.MAX_VALUE;
                        break;
                    }
                    down = cellBeans.get(selectItem).downs.poll();
                    cellBeans.get(selectItem).down = down;
                }
                if (down < cellBeans.size()) {
                    select(down);
                } else {
                    if (onKeyDownOutEnvent != null) {
                        onKeyDownOutEnvent.onKeyDownGoDown(this);
                    }
                }
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                int left = cellBeans.get(selectItem).left;
                /**
                 * 跳过不能获取焦点的控件
                 */
                while ((left >= 0 && left < cellBeans.size()) && !cellBeans.get(left).isFocus) {
                    if (cellBeans.get(selectItem).lefts.peek() == null) {
                        left = -1;
                        break;
                    }
                    left = cellBeans.get(selectItem).lefts.poll();
                    cellBeans.get(selectItem).left = left;
                }
                if (left >= 0) {
                    select(left);
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
                int right = cellBeans.get(selectItem).right;
                /**
                 * 跳过不能获取焦点的控件
                 */
                while (right >= 0 && right < cellBeans.size() && !cellBeans.get(right).isFocus) {
                    if (cellBeans.get(selectItem).rights.peek() == null) {
                        right = Integer.MAX_VALUE;
                        break;
                    }
                    right = cellBeans.get(selectItem).rights.poll();
                    cellBeans.get(selectItem).right = right;
                }
                if (right < cellBeans.size()) {
                    select(right);
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
//                if (onCellItemClick != null) {
//                    onCellItemClick.onItemClick(cellViewList.get(selectItem));
//                }
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
                } else if (endScrollX > width - SCREEN_WIDTH * screenScale) {
                    endScrollX = (int) (width - SCREEN_WIDTH * screenScale);
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
    private void scrollToCurrentItem(int currentItem) {
        int crt_x = getScrollX();
        int left_x = cellBeans.get(currentItem).x - min_x;
        int right_x = cellBeans.get(currentItem).x + cellBeans.get(currentItem).width + min_x;
        int screen_w = (int) (SCREEN_WIDTH * screenScale);

        if (left_x - crt_x < 0) {
            mScroller.startScroll(crt_x, 0, left_x - crt_x, 0, scrollDuration);
        } else if ((right_x - crt_x) >= screen_w) {
            mScroller.startScroll(crt_x, 0, (int) (right_x - SCREEN_WIDTH * screenScale) - crt_x, 0, scrollDuration);
        }
    }

    /**
     * 判断控件是否需要镜像
     *
     * @param CellBean
     * @param meNum
     * @return
     */
    private boolean needReflect(CellBean CellBean, int meNum) {
        int i_x1 = CellBean.x;
        int i_x2 = i_x1 + CellBean.width;
        int i_y1 = CellBean.y;
        int i_y2 = i_y1 + CellBean.height;
        boolean flag = true;
        for (int j = 0; j < cellBeans.size(); j++) {
            int j_x1 = cellBeans.get(j).x;
            int j_x2 = j_x1 + cellBeans.get(j).width;
            int j_y1 = cellBeans.get(j).y;
            int j_y2 = j_y1 + cellBeans.get(j).height;
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
            while (selectItem >= 0 && selectItem < cellBeans.size() && !cellBeans.get(selectItem).isFocus) {
                selectItem = cellBeans.get(selectItem).right;
            }
            if (selectItem >= cellBeans.size()) {//没有找到可或焦的
                selectItem = cellBeans.size() - 1;
            }
        } else {
            int width = this.width;
            int windowWidth = DisplayUtils.getMetrices((Activity) context).widthPixels;
            if (width > windowWidth) {//只有需要移动的时候进行平移
                scrollTo(width - windowWidth, 0);
            }
            selectItem = getRightUpCellIndex();
            //跳过不能获取焦点的控件
            while (selectItem >= 0 && !cellBeans.get(selectItem).isFocus) {
                selectItem = cellBeans.get(selectItem).left;
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
        if (currentItem < 0 || cellBeans.size() <= currentItem) {
            return;
        }
        if (selectItem == -1) {
            iTvFocusAnimat.startAnim(null, (IAnimatView) cellViewList.get(currentItem));
        } else {
            iTvFocusAnimat.startAnim((IAnimatView) cellViewList.get(selectItem), (IAnimatView) cellViewList.get(currentItem));
        }
        isAnimPlay = true;
        mHandler.postDelayed(animTask, mAnimDuration);
    }

    /**
     * 获取左上角cell index
     */
    public int getLeftUpCellIndex() {
        if (leftUpIndex == -1) {
            leftUpIndex = iCellSortable.getLeftUpIndex(cellBeans);
        }
        return leftUpIndex;
    }

    /**
     * 获取右上角cell index
     */
    public int getRightUpCellIndex() {
        if (rightUpIndex == -1) {
            rightUpIndex = iCellSortable.getRightUpIndex(cellBeans);
        }
        return rightUpIndex;
    }

    /**
     * 设置启动时被要求设置成焦点的控件为获取焦点状态
     * @return true 有默认焦点控件已经设置，false 无默认焦点控件没有设置
     */
    public boolean setDefaultFocus(int cellId) {
        for (int i = 0; i < cellBeans.size(); i++) {
            if (cellBeans.get(i).cellId == cellId) {
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

    @Override
    public void setmPageBean(PageBean mPageBean) {
        this.mPageBean = mPageBean;
        List<CellBean> cellBeans = mPageBean.cellList;
        if (cellBeans == null || cellBeans.isEmpty()) return;
        addAllItemView(cellBeans);
    }

    @Override
    public void showMirror(boolean flag) {

    }

}

