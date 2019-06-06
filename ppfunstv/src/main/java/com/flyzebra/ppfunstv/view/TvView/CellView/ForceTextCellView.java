package com.flyzebra.ppfunstv.view.TvView.CellView;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.text.Layout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyzebra.ppfunstv.R;
import com.flyzebra.ppfunstv.data.CellEntity;
import com.flyzebra.ppfunstv.utils.DisplayUtils;
import com.flyzebra.ppfunstv.utils.FlyLog;
import com.flyzebra.ppfunstv.utils.GsonUtil;
import com.flyzebra.ppfunstv.utils.Utils;

/**
 * Created with Android Studio.
 * User: Fargo
 * Date: 2017/8/15
 * Time: 上午10:39
 */

public class ForceTextCellView extends SimpleCellView {
    /**
     * 遮罩层总高度
     */
    private int allMaskHeight = 310;
    /**
     * 刚开始遮罩的高度
     */
    private int showMaskheight = 90;
    /**
     * 一行最多多少个字符
     */
    private int maxEms = 17;
    /**
     * 没有显示的遮罩高度
     */
    private int noShowMaskheight = allMaskHeight - showMaskheight;

    private int maxLine = 7;
    private int showLine = 2;
    private int duration = 500;

    public ForceTextCellView(Context context) {
        super(context);
    }

    public ForceTextCellView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ForceTextCellView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setClipChildren(true);
        setClipToPadding(true);
    }


    @Override
    public void initView() {
        LayoutParams lp = new LayoutParams(mCell.getWidth(), mCell.getHeight());
        setLayoutParams(lp);
        mLangMap = GsonUtil.json2Map(mCell.getText());
        float scaleScreen = DisplayUtils.getMetrices((Activity) mContext).widthPixels / 1920f;
//        textSize = (int) (textSize * scaleScreen);

        inflate(mContext, R.layout.tv_cell_item2, this);
        mImageView = (SubScriptView) findViewById(R.id.tv_iv_cell);
        mTvInfo = (TextView) findViewById(R.id.tv_tv_cell);
        mTvInfo.setMaxLines(mCell.getMaxTextLine());
        mTvInfo.setMaxEms(maxEms);
//        mTvInfo.setTextSize(textSize);

        mLlMask = (LinearLayout) findViewById(R.id.tv_cell_bg);

        mLlMask.setBackgroundColor(Color.parseColor(TextUtils.isEmpty(mCell.getTextMaskColor()) ? "#96000000" : mCell.getTextMaskColor()));
        allMaskHeight = (int) (mTvInfo.getLineHeight() * mTvInfo.getMaxLines() + mTvInfo.getPaddingTop() + mTvInfo.getPaddingBottom() + mTvInfo.getLineSpacingExtra() * mTvInfo.getMaxLines());
        mTvInfo.getLayoutParams().height = allMaskHeight;
        mLlMask.getLayoutParams().height = allMaskHeight;
        showMaskheight = (int) (mTvInfo.getPaddingTop() + mTvInfo.getLineHeight() * mCell.getMinTextLine() + mTvInfo.getLineSpacingExtra() * mCell.getMinTextLine());
        if (mCell.getMinTextLine() == 1) {
            showMaskheight = (int) mTvInfo.getLineSpacingExtra() + showMaskheight;
        }
        noShowMaskheight = allMaskHeight - showMaskheight;


        ((LayoutParams) mLlMask.getLayoutParams()).bottomMargin = -noShowMaskheight;

        setTextInfo(mTvInfo, mLlMask);
        if (mImageView != null && mImageView instanceof SubScriptView && mCell != null) {
            ((SubScriptView) mImageView).setCell(mCell, mBitmapCache);
        }
    }


    /**
     * 设置文字相关信息
     *
     * @param view
     */
    public void setTextInfo(TextView view, LinearLayout maskView) {
        try {
            setTextEffect(view, firstLineState);
            view.setGravity(Gravity.NO_GRAVITY);
            //设置字体内容
            String text = null;
            if (mLangMap != null) {
                text = Utils.getLocalLanguageString(mLangMap);
            } else {
                text = mCell.getText();
            }
            if (TextUtils.isEmpty(text) || TextUtils.isEmpty(text.trim())) {
                maskView.setVisibility(View.INVISIBLE);
            } else {
                view.setText(text);
            }

            //设置背景
        } catch (Exception e) {
            FlyLog.e(e.toString());
        }

    }

    private void performAnim(boolean show) {

        if (show) {
            ObjectAnimator.ofFloat(mLlMask, "translationY", 0, -noShowMaskheight).setDuration(duration).start();
        } else {
            ObjectAnimator.ofFloat(mLlMask, "translationY", -noShowMaskheight, 0).setDuration(duration).start();
        }

    }

    private int getTextViewHeight(TextView textView, int lineNum) {
        Layout layout = textView.getLayout();
        int desired = layout.getLineTop(lineNum);
        int padding = textView.getCompoundPaddingTop() +
                textView.getCompoundPaddingBottom();
        return desired + padding;
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        performAnim(gainFocus);
    }


    @Override
    public View setCellData(CellEntity cellEntity) {
        cellEntity.setFocusType(1);

        if (cellEntity.getMaxTextLine() == 0) {
            cellEntity.setMaxTextLine(7);
        }

        if (cellEntity.getMinTextLine() == 0) {
            cellEntity.setMinTextLine(2);
        }

        return super.setCellData(cellEntity);
    }


}
