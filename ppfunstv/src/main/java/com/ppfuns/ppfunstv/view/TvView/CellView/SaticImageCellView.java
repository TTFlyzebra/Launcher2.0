package com.ppfuns.ppfunstv.view.TvView.CellView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.ppfuns.ppfunstv.data.CellEntity;

/**
 *
 * Created by flyzebra on 17-6-21.
 */
public class SaticImageCellView extends TvPageItemView{
    private SubScriptView imageCellView;
    public SaticImageCellView(Context context) {
        super(context);
    }

    public SaticImageCellView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SaticImageCellView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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
    }

    @Override
    public ImageView getMyImageView() {
        return imageCellView;
    }
}
