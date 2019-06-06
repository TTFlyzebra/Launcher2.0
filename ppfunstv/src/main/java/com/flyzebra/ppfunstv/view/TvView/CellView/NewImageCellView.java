package com.flyzebra.ppfunstv.view.TvView.CellView;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyzebra.ppfunstv.data.CellEntity;
import com.flyzebra.ppfunstv.utils.GsonUtil;
import com.flyzebra.ppfunstv.utils.Utils;

import java.util.Map;

/**
 * Created by fagro on 17-5-3.
 */

public class NewImageCellView extends TvPageItemView {
    private SubScriptView imageCellView;
    private TextView textView1;

    public NewImageCellView(Context context) {
        this(context,null);
    }

    public NewImageCellView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public NewImageCellView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setClipChildren(false);
        setClipToPadding(false);
    }


    @Override
    public View setCellData(CellEntity cellEntity) {
        cellEntity.setFocusScale(1);
        return super.setCellData(cellEntity);
    }

    @Override
    public void initView() {
        imageCellView = new SubScriptView(mContext);
        imageCellView.setScaleType(ImageView.ScaleType.FIT_XY);
        LayoutParams lp = new LayoutParams(mCell.getWidth(), mCell.getHeight());
        imageCellView.setLayoutParams(lp);
        addView(imageCellView);

        textView1 = new TextView(mContext);
        LayoutParams lp1 = new LayoutParams(mCell.getWidth(), LayoutParams.WRAP_CONTENT);
        lp1.leftMargin = 20;
        lp1.topMargin = mCell.getHeight()+10;
        textView1.setLayoutParams(lp1);

        Map mLangMap = GsonUtil.json2Map(mCell.getText());
        String text = null;
        if (mLangMap != null) {
            text = Utils.getLocalLanguageString(mLangMap);
        } else {
            text = mCell.getText();
        }
        textView1.setText(text);

        textView1.setTextColor(0xffffffff);
        textView1.getPaint().setAntiAlias(true);
        textView1.setTextSize(20);
        textView1.setAlpha(0);
        textView1.setLineSpacing(1.0f,1.2f);
        addView(textView1);

    }

    @Override
    public ImageView getMyImageView() {
        return imageCellView;
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        if(gainFocus){
            textView1.animate().alpha(1).setDuration(1000).start();
            textView1.animate().scaleY(0.1f).setDuration(0).start();
            textView1.animate().scaleY(1).setDuration(500).start();
        }else {
            textView1.animate().alpha(0).setDuration(100).start();
        }
    }
}
