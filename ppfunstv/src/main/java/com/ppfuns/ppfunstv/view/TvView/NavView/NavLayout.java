package com.ppfuns.ppfunstv.view.TvView.NavView;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Scroller;

import com.ppfuns.ppfunstv.data.TabEntity;
import com.ppfuns.ppfunstv.utils.DisplayUtils;
import com.ppfuns.ppfunstv.utils.FlyLog;
import com.ppfuns.ppfunstv.utils.SPUtil;
import com.ppfuns.ppfunstv.view.TvView.IOnKeyDownOutEnvent;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by pc1 on 2016/6/13.
 */
public class NavLayout extends LinearLayout{
    private Context mContext;
    public List<TabEntity> listItem = new ArrayList<>();
    private Scroller mScroller;

    public List<NavItemView> getListView() {
        return listView;
    }

    public List<NavItemView> listView;
    private double screenScale = 1.0;//屏幕缩放系数
    private int mDuration = 300;//屏幕滚动延续时长

    public int getSelectItem() {
        return selectItem;
    }

    /**
     *
     * @param selectItem
     * @param flag
     */
    public void setSelectItem(int selectItem,boolean flag) {
        FlyLog.d("set select item....");
        if(this.selectItem < listView.size() && this.selectItem > -1){
            (listView.get(this.selectItem)).setFocusEffect(2);
        }
        if(flag){
            (listView.get(selectItem)).setFocusEffect(0);
        }else {
            (listView.get(selectItem)).setFocusEffect(1);
        }

        this.selectItem = selectItem;
        scroll2place();
    }

    private int selectItem = 0;

    public NavLayout(Context context) {
        this(context, null);
    }

    public NavLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NavLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        mScroller = new Scroller(context);
        screenScale = DisplayUtils.getMetrices((Activity) mContext).widthPixels / 1920f;
    }

    public void setScreenScale(double screenScale) {
        this.screenScale = screenScale;
    }

    private boolean setData(final List<TabEntity> listItem) {
        if (listItem == null) {
            return false;
        }else{
            this.listItem.clear();
            this.listItem.addAll(listItem);
        }
        if (listView == null) {
            listView = new ArrayList<>();
        }else{
            removeAllViews();
            listView.clear();
        }
        for (int i = 0; i < listItem.size(); i++) {
            TabEntity item = listItem.get(i);
            NavItemView itemView = new NavItemView(mContext,item,i);
            LayoutParams lpItem = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            itemView.setLayoutParams(lpItem);
            lpItem.leftMargin = item.getMarging();
            if(i == selectItem){
                itemView.setFocusEffect(0);
            }else{
                itemView.setFocusEffect(2);
            }

            itemView.setTag(i);
            final int currentItem = i;
            itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(selectItem == currentItem){
                        //执行控件指定的动作
                    } else {
                        select(currentItem,v);

                    }
                }
            });

            listView.add(itemView);
            addView(itemView);
        }
        return true;
    }


    private void select(int currentItem, View v) {
        setSelectItem(currentItem,true);
        if (onItemClick != null) {
            onItemClick.onItemClick(v);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(listItem==null||listItem.size()<1){
            return super.onKeyDown(keyCode, event);
        }
        int left = listItem.get(selectItem).getLeft();
        int right = listItem.get(selectItem).getRight();
//        int up = listItem.get(selectItem).getUp();
//        int down = listItem.get(selectItem).getDown();
        switch (keyCode){
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if(onKeyDownOutEnvent !=null){
                    onKeyDownOutEnvent.onKeyDownGoDown(this);
                }
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                if(onKeyDownOutEnvent!=null){
                    onKeyDownOutEnvent.onKeyDownGoUp(this);
                }
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if(left>=0){
                    int cur = selectItem == -1 ? 0:selectItem;
                    boolean tvpage_loop = (boolean) SPUtil.get(mContext, SPUtil.FILE_CONFIG, SPUtil.CONFIG_CIRCULATION_FLAG, false);
                    int next = cur - 1>= 0 ?cur -1 : (tvpage_loop ? listView.size()-1:0);//listView.size()-1  不让循环
                    select(next,listView.get(next));
                }else{
                    if(onKeyDownOutEnvent !=null){
                        onKeyDownOutEnvent.onKeyDownGoLeft(this);
                    }
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if(right>=0){
                    int cur = selectItem == -1 ? 0:selectItem;
                    boolean tvpage_loop = (boolean) SPUtil.get(mContext, SPUtil.FILE_CONFIG, SPUtil.CONFIG_CIRCULATION_FLAG, false);
                    int next = cur + 1 == listView.size()?(tvpage_loop?0:listView.size() -1):cur +1;//listView.size() 不让循环
                    select(next,listView.get(next));
                }else{
                    if(onKeyDownOutEnvent !=null){
                        onKeyDownOutEnvent.onKeyDownGoRight(this);
                    }
                }
                break;
        }

        return super.onKeyDown(keyCode, event);
    }

    public List<TabEntity> getItemList() {
        return listItem;
    }
    private OnItemClick onItemClick;

    public void setOnItemClick(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }
	
	    public interface OnItemClick {
        void onItemClick(View view);
    }

    private IOnKeyDownOutEnvent onKeyDownOutEnvent;

    public void setOnKeyDownOutEnvent(IOnKeyDownOutEnvent onKeyDownOutEnvent){
        this.onKeyDownOutEnvent = onKeyDownOutEnvent;
    }


    public void setTabData(List<TabEntity> tabEntities) {
        if(tabEntities != null && tabEntities.size() > 0){
            setData(tabEntities);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        for(int i=0;i<getChildCount();i++){
            measureChild(getChildAt(i),widthMeasureSpec,heightMeasureSpec);
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if(mScroller.computeScrollOffset()){
            scrollTo(mScroller.getCurrX(), 0);
            postInvalidate();
        }
    }

    /**
     * 平移到正确位置
     */
    public void scroll2place(){
        NavItemView cur = getListView().get(getSelectItem());
        float curStartX = cur.getX();
        int curScrollX = getScrollX();
        if(curStartX - curScrollX < 0){
            int dx = (int)(curStartX);
            mScroller.startScroll(0,0,dx,0,0);
            postInvalidate();

        }else{
            float curEndX = curStartX + cur.getWidth() + ((LinearLayout.LayoutParams)cur.getLayoutParams()).rightMargin;
            float layoutEndX = getWidth();
            if(curEndX - curScrollX > layoutEndX){
                int dx = (int)(curEndX - layoutEndX);
                mScroller.startScroll(0,0,dx,0,0);
                postInvalidate();
            }
        }
    }
}
