package com.ppfuns.ppfunstv.view.TvView.CellView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ppfuns.ppfunstv.R;
import com.ppfuns.ppfunstv.constant.Constants;
import com.ppfuns.ppfunstv.data.CellEntity;
import com.ppfuns.ppfunstv.data.StateStyleEntity;
import com.ppfuns.ppfunstv.module.BitmapCache;
import com.ppfuns.ppfunstv.utils.FlyLog;
import com.ppfuns.ppfunstv.utils.FontManager;
import com.ppfuns.ppfunstv.utils.GsonUtil;
import com.ppfuns.ppfunstv.utils.Utils;
import com.ppfuns.ppfunstv.view.TvView.CellView.CellClickAction.ActionFactory;

import java.util.Map;

/**
 * 自定义每页item控件的基类
 * 备注: 图片加载机制有待完善
 * Created by lzy on 2016/6/14.
 */
public class SimpleCellView extends TvPageItemView {

    protected static final String TAG = "CellView";
    private final int DEFAULT_COLOR = 0xffffffff;
    /**
     * cell数据
     */
    protected ImageView mImageView = null;//显示的图片
    protected TextView mTvInfo = null;//显示的文字
    protected LinearLayout mLlMask = null;//字体背景mask
    protected TextView mSecodeText = null;
    protected int firstLineState = 4;
    protected int secondLineState = 5;

    protected BitmapCache mBitmapCache;
    /**
     * 多语言处理map
     */
    protected Map<String, String> mLangMap = null;
    String line = "|";
    /**
     * 阴影颜色
     */
    private int shadowColor;

    public SimpleCellView(Context context) {
        this(context, null);
    }

    public SimpleCellView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleCellView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        setAlpha(Constants.cellNoFocus);
    }

    /**
     * 需要修改布局请重载此方法
     *
     */
    @Override
    public void initView() {
        mLangMap = GsonUtil.json2Map(mCell.getText());
        inflate(mContext, R.layout.tv_cell_item, this);
        mImageView = (SubScriptView) findViewById(R.id.tv_iv_cell);
        mTvInfo = (TextView) findViewById(R.id.tv_tv_cell);
        mLlMask = (LinearLayout) findViewById(R.id.tv_cell_bg);
        setTextInfo(mTvInfo, mLlMask);
        if (mImageView != null && mImageView instanceof SubScriptView && mCell != null) {
            ((SubScriptView) mImageView).setCell(mCell,mBitmapCache);
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
                if (text.contains(line)) {
                    int index = text.indexOf(line);
                    view.setText(text.substring(0, index));
                    addAnotherTextView(text.substring(index + 1));
                } else {
                    view.setText(text);
                }
            }

            //设置背景
        } catch (Exception e) {
            FlyLog.e(e.toString());
        }

    }

    /**
     * @param view
     * @param index 1:第一行文本
     */
    protected void setTextEffect(TextView view, int index) {
        try {
            StateStyleEntity styleEntity = null;
            if (mCell.getStateStyle() != null) {
                for (StateStyleEntity item : mCell.getStateStyle()) {
                    if (index == item.getState()) {
                        styleEntity = item;
                        break;
                    }
                }
            }

            //TODO 字体处理
            //设置字体
            if (styleEntity != null && !TextUtils.isEmpty(styleEntity.getFont())) {
                view.setTypeface(FontManager.getTypefaceByFontName(mContext, styleEntity.getFont()));
            } else if (!TextUtils.isEmpty(mCell.getFont())) {
                view.setTypeface(FontManager.getTypefaceByFontName(mContext, mCell.getFont()));
            }

            view.setGravity(Gravity.CENTER);
            //设置文字大小
            if (styleEntity != null && styleEntity.getSize() > Constants.MIN_TEXT_SIZE) {
                view.setTextSize(styleEntity.getSize());
            } else if (mCell.getSize() > Constants.MIN_TEXT_SIZE) {
                view.setTextSize(mCell.getSize());
            }
            //设置字体颜色
            if (styleEntity != null && !TextUtils.isEmpty(styleEntity.getColor())) {
                view.setTextColor(Color.parseColor(styleEntity.getColor()));
            } else if (!TextUtils.isEmpty(mCell.getColor())) {
                view.setTextColor(Color.parseColor(mCell.getColor()));
            } else {
                view.setTextColor(DEFAULT_COLOR);
            }

            //设置alpha
            if (styleEntity != null && styleEntity.getAlpha() > 0) {
                view.setAlpha(styleEntity.getAlpha() * 1.0f / 255);
            }
        } catch (Exception e) {
            FlyLog.e(e.toString());
        }
    }

    /**
     * 添加第二行文本
     *
     * @param text
     */
    private void addAnotherTextView(String text) {
        mSecodeText = new TextView(mContext);
        mSecodeText.setTextAppearance(mContext, R.style.TV_SecondTextInfoStyle);
        setTextEffect(mSecodeText, secondLineState);
        mSecodeText.setText(text);
        mSecodeText.setSingleLine(true);
//        mSecodeText.setFocusable(true);
//        mSecodeText.setFocusableInTouchMode(true);
        mSecodeText.setEllipsize(TextUtils.TruncateAt.valueOf("MARQUEE"));
        mSecodeText.setMarqueeRepeatLimit(-1);
        mSecodeText.setBackground(null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = 13;
        lp.rightMargin = 13;
        lp.topMargin = -13;
        mSecodeText.setSelected(false);
        mLlMask.addView(mSecodeText, lp);
    }


    /**
     * 给控件添加数据,,必须调用setData给控件设置数据
     *
     * @param cellEntity 初始化cell数据
     */
    @Override
    public View setCellData(CellEntity cellEntity) {
        super.setCellData(cellEntity);

        return this;
    }

    @Override
    public void setBitmapCache(BitmapCache bitmapCache) {
        this.mBitmapCache = bitmapCache;
    }

    /**
     * 对其中的action做特殊处理
     * 不同子类的action不同,对应的处理方法请参考子类中的实现
     */
    public void setActionEntity() {
        clickEvent = ActionFactory.create(mContext, mCell);
    }


    public int getShadowColor() {
        return shadowColor;
    }

    public void setShadowColor(int shadowColor) {
        this.shadowColor = shadowColor;
    }

    @Override
    public ImageView getMyImageView() {
        return mImageView;
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if (selected) {
            setAlpha(Constants.cellFocus);
        } else {
            setAlpha(Constants.cellNoFocus);
        }
        if (mTvInfo != null) {
            mTvInfo.setSelected(selected);
        }
        if (mSecodeText != null) {
            mSecodeText.setSelected(selected);
        }
    }

    /**
     * 加载图片
     *
     * @param entity
     */
    protected void loadImageSrc(CellEntity entity) {
        String imgUrl = entity.getImgUrl();
        String filePath = iDiskCache != null ? imgUrl : iDiskCache.getBitmapPath(imgUrl);
        Glide.with(mContext)
                .load(filePath)
//                    .transform(new GlideRoundTransform(context, (int) (4 * screenScale)))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(mImageView);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }




}
