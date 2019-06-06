package com.flyzebra.launcher.ui;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyzebra.launcher.R;
import com.flyzebra.launcher.base.BaseActivity;
import com.flyzebra.ppfunstv.utils.ToastUtils;
import com.flyzebra.ppfunstv.view.TvView.CellView.CellClickAction.CommondTool;

import java.util.ArrayList;
import java.util.List;

public class AppListActivity extends BaseActivity {


    RecyclerView recyclerView;
    public List<MyAppInfo> myAppInfos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list);
        recyclerView = (RecyclerView) findViewById(R.id.rv_app);
        RecyclerView.LayoutManager lm = new GridLayoutManager(this, 4);
        myAppInfos = getAppList();
        Adapter adapter = new Adapter(myAppInfos);
        adapter.setOnItemClick(new Adapter.OnItemClick() {
            @Override
            public void onClick(View view, int postion) {
                if(!CommondTool.execStartPackage(view.getContext(), myAppInfos.get(postion).packagename)){
                    ToastUtils.showMessage(view.getContext(),"无法打开应用");
                };
            }
        });
        recyclerView.setLayoutManager(lm);
        recyclerView.setAdapter(adapter);

    }


    public List<MyAppInfo> getAppList() {
        final ActivityManager am = (ActivityManager)
                this.getSystemService(Context.ACTIVITY_SERVICE);
        List<PackageInfo> appinfo = getPackageManager().getInstalledPackages(0);
        List<MyAppInfo> myAppInfos = new ArrayList<>();
        for (PackageInfo packageInfo : appinfo) {
            MyAppInfo myAppInfo = new MyAppInfo();
            myAppInfo.packagename = packageInfo.packageName;
            myAppInfo.appName = packageInfo.applicationInfo.loadLabel(getPackageManager()).toString();


            myAppInfo.icon = packageInfo.applicationInfo.loadIcon(getPackageManager());
            myAppInfos.add(myAppInfo);
        }
        return myAppInfos;
    }


    public class MyAppInfo {
        public String appName;
        public String packagename;
        public Drawable icon;
    }


    public static class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {


        private Context mContext;
        private List<MyAppInfo> myAppInfos;
        public OnItemClick onItemClick;

        public Adapter(List<MyAppInfo> myAppInfos) {
            this.myAppInfos = myAppInfos;
        }

        public Adapter(List<MyAppInfo> myAppInfos, Adapter.OnItemClick onItemClick) {
            this.myAppInfos = myAppInfos;
            this.onItemClick = onItemClick;
        }

        public OnItemClick getOnItemClick() {
            return onItemClick;
        }

        public void setOnItemClick(OnItemClick onItemClick) {
            this.onItemClick = onItemClick;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(mContext).inflate(R.layout.layout_item_app, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            holder
                    .imageView
                    .setImageDrawable(myAppInfos.get(position).icon);
            holder
                    .textView
                    .setText(myAppInfos.get(position).appName);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClick != null)
                        onItemClick.onClick(v, position);
                }
            });

        }

        @Override
        public int getItemCount() {
            return myAppInfos != null ? myAppInfos.size() : 0;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageView;
            public TextView textView;

            public ViewHolder(View itemView) {
                super(itemView);
                imageView = (ImageView) itemView.findViewById(R.id.iv_icon);
                textView = (TextView) itemView.findViewById(R.id.iv_name);

            }
        }


        public interface OnItemClick {
            void onClick(View view, int postion);
        }
    }
}
