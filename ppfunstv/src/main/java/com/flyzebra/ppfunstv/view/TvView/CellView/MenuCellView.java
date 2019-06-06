package com.flyzebra.ppfunstv.view.TvView.CellView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.flyzebra.ppfunstv.data.CellEntity;

/**
 *
 * Created by flyzebra on 17-6-8.
 */
public class MenuCellView extends TvPageItemView{
    private SubScriptView imageCellView;

    public MenuCellView(Context context) {
        super(context);
    }

    public MenuCellView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MenuCellView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public View setCellData(CellEntity cellEntity) {
        cellEntity.setFocusType(2);
        cellEntity.setFocusZorder(1);
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
