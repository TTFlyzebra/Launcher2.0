package com.ppfuns.ppfunstv.view.TvView.CellView.HorizontalCellView;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ppfuns.ppfunstv.R;
import com.ppfuns.ppfunstv.data.CellEntity;
import com.ppfuns.ppfunstv.module.UpdataVersion.IDiskCache;
import com.ppfuns.ppfunstv.utils.FlyLog;
import com.ppfuns.ppfunstv.utils.FontManager;
import com.ppfuns.ppfunstv.utils.GsonUtils;
import com.ppfuns.ppfunstv.utils.Utils;
import com.ppfuns.ppfunstv.view.MarqueeTextView;

import java.util.List;
import java.util.Map;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.SimpleItemViewHolder> {

    private IDiskCache iDiskCache;
    private int resID = R.drawable.tv_default;
    private List<CellEntity> list;
    private Context mContext;
    private int mShowItemNum = 5;
    private int mWidth;
    private int mHeight;
    private View.OnFocusChangeListener itemFocusChange;
    private View.OnClickListener itemClickListener;
    private float mAlpha = 0.4f;
    private CellEntity mCell;
    private int mItemPadding;

    public ItemAdapter(Context context, CellEntity cellEntity, @NonNull List<CellEntity> dateItems, int mItemPadding, int width, int height, int mShowItemNum, @NonNull IDiskCache iDiskCache, @DrawableRes int resID) {
        this.mContext = context;
        this.mCell = cellEntity;
        this.list = dateItems;
        this.mItemPadding = mItemPadding;
        this.mWidth = width;
        this.mHeight = height;
        this.mShowItemNum = mShowItemNum;
        this.iDiskCache = iDiskCache;
        this.resID = resID;
    }

    @Override
    public SimpleItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
//        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.tv_horizontalgrid_item1, viewGroup, false);
        CellEntity cellEntity = null;
        int textSize1 = 20;
        int textColor = 0xFFFFFFFF;
        if (list.isEmpty()) {
            cellEntity = list.get(0);
            textSize1 = cellEntity.getSize() == 0 ? 20 : cellEntity.getSize();
            String color = cellEntity.getColor();
            try {
                textColor = Color.parseColor(color);
            } catch (Exception e) {
                textColor = 0xFFFFFFFF;
                FlyLog.d("parseColor error! use defult color #FFFFFFFF.");
            }
        }

        RelativeLayout view = new RelativeLayout(mContext);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams((mWidth - 20) / mShowItemNum, mHeight);
        view.setLayoutParams(lp);
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.setPadding(mItemPadding, mItemPadding, mItemPadding, mItemPadding);
        view.setClipChildren(false);
        view.setClipToPadding(false);

        ImageView imageView = new ImageView(mContext);
        RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(lp1);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setId(R.id.tv_id_01);
        view.addView(imageView);

        TextView textView1 = new TextView(mContext);
        RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, textSize1 + 8);
        lp2.setMargins(0, mHeight - 16, 0, 0);
        textView1.setLayoutParams(lp2);
        textView1.setId(R.id.tv_id_02);
        textView1.setSingleLine();
        textView1.setTextColor(textColor);
        textView1.setAlpha(mAlpha);
        textView1.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize1);
        if (!TextUtils.isEmpty(mCell.getFont())) {
            textView1.setTypeface(FontManager.getTypefaceByFontName(mContext, mCell.getFont()));
        }
        view.addView(textView1);


        int textSize2 = Math.max(textSize1 - 4, 12);
        MarqueeTextView textView2 = new MarqueeTextView(mContext);
        RelativeLayout.LayoutParams lp3 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, textSize2 + 8);
        lp3.setMargins(0, mHeight - 16 + textSize1 + 6, 0, 0);
        textView2.setLayoutParams(lp3);
        textView2.setId(R.id.tv_id_03);
        textView2.setSingleLine();
        textView2.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        textView2.setMarqueeRepeatLimit(-1);
        textView2.setVisibility(View.INVISIBLE);
        textView2.setTextColor(textColor);
        textView2.setAlpha(mAlpha);
        textView2.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize2);
        if (!TextUtils.isEmpty(mCell.getFont())) {
            textView2.setTypeface(FontManager.getTypefaceByFontName(mContext, mCell.getFont()));
        }
        view.addView(textView2);

        switch (mCell.getCarouselPosition()) {
            case 1:
            default:
                textView1.setGravity(Gravity.LEFT);
                textView2.setGravity(Gravity.LEFT);
                break;
            case 2:
                textView1.setGravity(Gravity.CENTER);
                textView2.setGravity(Gravity.CENTER);
                break;
            case 3:
                textView1.setGravity(Gravity.RIGHT);
                textView2.setGravity(Gravity.RIGHT);
                break;
        }

        return new SimpleItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimpleItemViewHolder viewHolder, final int position) {
        viewHolder.itemView.setTag(position);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemClickListener != null) {
                    itemClickListener.onClick(view);
                }
            }
        });

        viewHolder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (itemFocusChange != null) {
                    itemFocusChange.onFocusChange(view, b);
                }
                if (b) {
                    viewHolder.textView1.setAlpha(1f);
                    viewHolder.textView2.setVisibility(View.VISIBLE);
                    viewHolder.textView2.setSelected(true);
                } else {
                    viewHolder.textView1.setAlpha(mAlpha);
                    viewHolder.textView2.setVisibility(View.INVISIBLE);
                    viewHolder.textView2.setSelected(false);
                }
            }
        });
        String textStr = list.get(position).getText();
        Map str = GsonUtils.json2Map(textStr);
        String texts = Utils.getLocalLanguageString(str);
        if (!TextUtils.isEmpty(texts)) {
            texts.replace("\\\\n", "\\n");
            String strs[] = texts.split("\\\\n");
            viewHolder.textView1.setText(strs[0]);
            if (strs.length > 1) {
                viewHolder.textView2.setText(strs[1]);
            }
        }

        Glide.with(mContext)
                .load(iDiskCache.getBitmapPath(list.get(position).getImgUrl()))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(resID)
                .override((mWidth - 20) / mShowItemNum - 20, mHeight - 20)
                .into(viewHolder.imageView);
    }

    @Override
    public int getItemCount() {
        return (this.list != null) ? this.list.size() : 0;
    }

    public void setItemFocusChange(View.OnFocusChangeListener itemFocusChange) {
        this.itemFocusChange = itemFocusChange;
    }

    public void setItemClickListener(View.OnClickListener onItemClickListener) {
        this.itemClickListener = onItemClickListener;
    }

    protected final static class SimpleItemViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView1;
        MarqueeTextView textView2;

        public SimpleItemViewHolder(View itemView) {
            super(itemView);
            this.imageView = (ImageView) itemView.findViewById(R.id.tv_id_01);
            this.textView1 = (TextView) itemView.findViewById(R.id.tv_id_02);
            this.textView2 = (MarqueeTextView) itemView.findViewById(R.id.tv_id_03);
        }
    }
}