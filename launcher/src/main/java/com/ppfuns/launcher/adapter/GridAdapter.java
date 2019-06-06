package com.ppfuns.launcher.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ppfuns.launcher.R;

import java.util.ArrayList;

/**
 * Created by 李宗源 on 2016/7/25.
 * E-mail:lizy@ppfuns.com
 */
public class GridAdapter extends BaseAdapter{
    private Context mContext;
    private ArrayList<Integer> mImgs = new ArrayList<>();
    private ArrayList<String> mInfos = new ArrayList<>();
    private int curPostition = 0;
    private int resId = -1;
    private final static float FOCUS_ON = 1.0f;
    private final static float FOCUS_OUT = 0.4f;

    public GridAdapter(Context context, ArrayList<Integer> imgs, ArrayList<Integer> infos){
        mContext = context;
        mImgs = imgs;
        for(int i = 0;i<infos.size();i++){
            mInfos.add(i,context.getString(infos.get(i)));
        }
    }

    public GridAdapter(Context context){
        mContext = context;

    }
    public void setData(ArrayList<Integer> imgs, ArrayList<String> infos){
        mImgs = imgs;
        mInfos = infos;
    }

    public void setResId(@LayoutRes int id){
        resId = id;
    }


    /**
     * 设置某一位置图片
     * @param index 位置
     * @param resId 资源
     */
    public void setData(int index,int resId){
        if(mImgs.size() > index){
            mImgs.set(index,resId);
        }
    }

    /**
     * 在index位置添加一个数据,如果index>length,在最后添加
     * @param resId
     * @param infoId
     */
    public void addData(int index,int resId,int infoId){
        if(getCount() > index){
            mImgs.add(index,resId);
            mInfos.add(index,mContext.getString(infoId));
        }else{
            mImgs.add(resId);
            mInfos.add(mContext.getString(infoId));
        }

    }

    /**
     * 删除一个数据
     * @param index
     */
    public void removeData(int index){
        if(getCount() > index){
            mImgs.remove(index);
            mInfos.remove(index);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        if(mImgs == null || mInfos == null || mInfos.size() != mImgs.size()){
            return 0;
        }
        return mImgs.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null){
            if(resId != -1){
                convertView = View.inflate(mContext,resId,null);
            }else{
                convertView = View.inflate(mContext,R.layout.set_grid_item,null);
            }
            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.set_grid_item_image);
            viewHolder.tvInfo = (TextView) convertView.findViewById(R.id.set_grid_item_tv);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(itemClick != null){
                        itemClick.onItemClick(position);
                    }
                }
            });
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Glide.with(mContext).load(mImgs.get(position)).into(viewHolder.imageView);
        viewHolder.tvInfo.setText(mInfos.get(position));
        viewHolder.tvInfo.setAlpha(FOCUS_OUT);
        viewHolder.imageView.setAlpha(FOCUS_OUT);
        if(curPostition == position){
            viewHolder.tvInfo.setAlpha(FOCUS_ON);
            viewHolder.imageView.setAlpha(FOCUS_ON);
        }

        return convertView;
    }


    class ViewHolder{
        ImageView imageView;
        TextView tvInfo;
    }

    public interface OnItemClick{
        void onItemClick(int pos);
    }

    private OnItemClick itemClick;

    public OnItemClick getItemClick() {
        return itemClick;
    }

    public void setItemClick(OnItemClick itemClick) {
        this.itemClick = itemClick;
    }

    public void setCurPostition(int curPostition) {
        this.curPostition = curPostition;
    }
}
