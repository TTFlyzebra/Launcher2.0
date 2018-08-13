package com.ppfuns.ppfunstv.view.TvView.CellView.HorizontalCellView;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.ppfuns.ppfunstv.data.CellEntity;
import com.ppfuns.ppfunstv.utils.DisplayUtils;
import com.ppfuns.ppfunstv.utils.FlyLog;
import com.ppfuns.ppfunstv.utils.FontManager;
import com.ppfuns.ppfunstv.utils.GsonUtils;
import com.ppfuns.ppfunstv.utils.Utils;
import com.ppfuns.ppfunstv.view.TvView.CellView.CellClickAction.ActionFactory;
import com.ppfuns.ppfunstv.view.TvView.CellView.TvPageItemView;

import java.util.Map;

/**
 * 单行列表控件
 * Created by flyzebra on 17-8-25.
 */

public class HorizontalGridCellView extends TvPageItemView {
    private HorizontalGridView mHorizontalGridView;
    private int topTextHight = 40;
    private int bottomTextHight = 48;
    private ItemAdapter mAdapter;
    private TextView menuTextView;
    private TextView countTextView;
    private View onClickView;
    private float MAXSCALE = 1.08f;
    private int mItemPadding = 12;

    public HorizontalGridCellView(Context context) {
        this(context, null);
    }

    public HorizontalGridCellView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalGridCellView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFocusable(false);
        setFocusableInTouchMode(false);
        setClipChildren(false);
        setClipToPadding(false);
    }

    @Override
    public View setCellData(CellEntity cellEntity) {
        cellEntity.setX(cellEntity.getX() - 10);
        cellEntity.setWidth(cellEntity.getWidth() + 20);
        cellEntity.setFocusType(-1);
        cellEntity.setFocusScale(1);
        super.setCellData(cellEntity);
        cellEntity.setCanFocus(false);
        return this;
    }

    @Override
    public void initView() {
        if (mCell.getSubCellList() != null) {
            float screenScale = DisplayUtils.getMetrices((Activity) mContext).widthPixels / 1920f;
            int textSize = (int) (40 * screenScale);
            if (mCell.getSize() != 0) {
                textSize = (int) (mCell.getSize() * screenScale);
            }
            textSize = Math.max((int) (20 * screenScale), textSize);
            textSize = Math.min((int) (80 * screenScale), textSize);

            topTextHight = textSize + 16;

            int textColor = 0xFFFFFFFF;
            String color = mCell.getColor();
            try {
                textColor = Color.parseColor(color);
            } catch (Exception e) {
                textColor = 0xFFFFFFFF;
                FlyLog.d("parseColor error! use defult color #FFFFFFFF.");
            }

            mItemPadding = iTvFocusAnimat==null?12:iTvFocusAnimat.getShadowAmend();

            menuTextView = new TextView(mContext);
            LayoutParams lp1 = new LayoutParams(mCell.getWidth(), topTextHight);
            menuTextView.setLayoutParams(lp1);
            menuTextView.setTextColor(textColor);
            menuTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            menuTextView.setPadding(12, 0, 0, 0);
            if (!TextUtils.isEmpty(mCell.getFont())) {
                menuTextView.setTypeface(FontManager.getTypefaceByFontName(mContext, mCell.getFont()));
            }
            addView(menuTextView);

            String textStr = mCell.getText();
            Map str = GsonUtils.json2Map(textStr);
            menuTextView.setText(Utils.getLocalLanguageString(str));

            countTextView = new TextView(mContext);
            countTextView.setLayoutParams(lp1);
            countTextView.setGravity(Gravity.RIGHT);
            countTextView.setTextColor(textColor);
            countTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            countTextView.setPadding(0, 0, 24, 0);
            addView(countTextView);
            countTextView.setText("1/" + mCell.getSubCellList().size());
            mHorizontalGridView = new HorizontalGridView(mContext);
            //判断子项有多少行文字，确定子项高度
            if (mCell.getSubCellList().size() > 0) {
                Map strm = GsonUtils.json2Map(mCell.getSubCellList().get(0).getText());
                String text = Utils.getLocalLanguageString(strm);
                text.replace(" ", "");
                if (TextUtils.isEmpty(text)) {
                    bottomTextHight = 0;
                } else if (text.contains("\n")) {
//                    bottomTextHight = 32;
                }else{
//                    bottomTextHight = 48;
                }
            }

            final int showRows = Math.max(1, mCell.getShowRows());
            final int showNums = Math.max(1, mCell.getShowImageNum());

            LayoutParams lp = new LayoutParams(mCell.getWidth(), mCell.getHeight() - topTextHight - bottomTextHight);
            lp.setMargins(0, topTextHight, 0, 0);
            mHorizontalGridView.setLayoutParams(lp);
            addView(mHorizontalGridView);
            mHorizontalGridView.setClipChildren(false);
            mHorizontalGridView.setClipToPadding(false);
            mHorizontalGridView.setNumRows(showRows);
            mAdapter = new ItemAdapter(mContext, mCell, mCell.getSubCellList(),mItemPadding,
                    mCell.getWidth() * showRows, (mCell.getHeight() - topTextHight - bottomTextHight) / showRows, showNums,
                    iDiskCache, mLoadImageResId);
            mHorizontalGridView.setAdapter(mAdapter);
            mAdapter.setItemFocusChange(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    if (hasFocus) {
                        view.animate().scaleX(MAXSCALE).scaleY(MAXSCALE).setDuration(mAnimDuration).setInterpolator(new AccelerateInterpolator()).start();
                        view.setBackgroundResource(iTvFocusAnimat.getShadowResID(1));
                        int position = (int) view.getTag();
                        countTextView.setText(String.valueOf(position + 1) + "/" + mCell.getSubCellList().size());
                        mFocusRect.left = mCell.getX() + view.getLeft();
                        mFocusRect.right = mFocusRect.left + view.getWidth();
                        mFocusRect.top = mCell.getY() + view.getTop();
                        mFocusRect.bottom = mFocusRect.top + (mCell.getHeight() - topTextHight - bottomTextHight) / showRows - bottomTextHight;
                    } else {
                        view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(mAnimDuration).setInterpolator(new AccelerateInterpolator()).start();
                        view.setBackground(null);
                        mOldRect.left = mCell.getX() + view.getLeft();
                        mOldRect.right = mFocusRect.left + view.getWidth();
                        mOldRect.top = mCell.getY() + view.getTop();
                        mOldRect.bottom = mFocusRect.top + (mCell.getHeight() - topTextHight - bottomTextHight) / showRows - bottomTextHight;
                    }
                    if (hasFocus) {
                        iTvFocusAnimat.startAnim(HorizontalGridCellView.this, true);
                    }
                }
            });

            mAdapter.setItemClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickView = v;
                    HorizontalGridCellView.this.callOnClick();
                }
            });

        }
    }

    @Override
    public ImageView getMyImageView() {
        return null;
    }


    @Override
    public void doAction(int flag) {
        try {
            if (onClickView != null) {
                clickEvent = ActionFactory.create(mContext, mCell.getSubCellList().get((Integer) onClickView.getTag()));
                onClickView = null;
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

}
