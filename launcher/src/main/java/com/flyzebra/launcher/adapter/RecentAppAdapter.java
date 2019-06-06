package com.flyzebra.launcher.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyzebra.launcher.R;
import com.flyzebra.ppfunstv.data.RecentTag;
import com.flyzebra.ppfunstv.view.TvView.CellView.CellClickAction.CommondTool;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class RecentAppAdapter extends BaseAdapter{
    private Context mContext;
    private List<RecentTag> mInfos = new ArrayList<>();
    private int resId = -1;

    public RecentAppAdapter(Context context, List<RecentTag> info){
        mContext = context;
        mInfos = info;
    }

    public void setResId(@LayoutRes int id){
        resId = id;
    }


    @Override
    public int getCount() {
        if(mInfos == null){
            return 0;
        }
        return mInfos.size();
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
                convertView = View.inflate(mContext,R.layout.recent_app_item,null);
            }
            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.iv_recent_app);
            viewHolder.tvInfo = (TextView) convertView.findViewById(R.id.tv_recent_app);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doAction(position);
                }
            });
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.imageView.setImageDrawable(mInfos.get(position).icon);
        viewHolder.tvInfo.setText(mInfos.get(position).name);
        return convertView;
    }


    /**
     * 执行点击事件
     * @param position
     */
    private void doAction(int position) {
        RecentTag tag = mInfos.get(position);
//        if (tag.recentTaskInfo.id >= 0) {
//            // 这个Task没有退出，直接移动到前台
//            final ActivityManager am = (ActivityManager)
//                    mContext.getSystemService(Context.ACTIVITY_SERVICE);
//            am.moveTaskToFront(tag.recentTaskInfo.id, ActivityManager.MOVE_TASK_WITH_HOME);
//        } else if (tag.intent != null) {
//            //task退出了的话，id为-1，则使用RecentTag中的Intent重新启动
//            try {
//                mContext.startActivity(tag.intent);
//            } catch (ActivityNotFoundException e) {
//                Log.w("Recent", "Unable to launch recent task", e);
//            }
//        }
        if(tag != null){
            CommondTool.execStartPackage(mContext,tag.packageName);
        }
    }


    class ViewHolder{
        ImageView imageView;
        TextView tvInfo;
    }

}
