package com.flyzebra.flyui.view.cellview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.flyzebra.flyui.bean.CellBean;
import com.flyzebra.flyui.bean.ImageBean;
import com.flyzebra.flyui.bean.TextBean;
import com.flyzebra.flyui.event.FlyEvent;
import com.flyzebra.flyui.module.RecycleViewDivider;
import com.flyzebra.flyui.utils.ByteUtil;
import com.flyzebra.flyui.utils.FlyLog;
import com.flyzebra.flyui.view.base.BaseRecyclerCellView;
import com.flyzebra.flyui.view.pageview.SimplePageView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;


/**
 * Author FlyZebra
 * 2019/4/12 16:13
 * Describ:
 **/
public class RecyclerCellView extends BaseRecyclerCellView {
    private List<Map<String, Object>> mList = new ArrayList<>();
    private FlyAdapter adapter;
    private String itemKey;

    public RecyclerCellView(Context context) {
        super(context);
    }


    @Override
    public void init(CellBean cellBean) {
        int column = mCellBean.width / mCellBean.pages.get(0).width;
        if (column > 1) {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), column);
            setLayoutManager(gridLayoutManager);
        } else {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            setLayoutManager(linearLayoutManager);
            addItemDecoration(new RecycleViewDivider(getContext(), LinearLayoutManager.HORIZONTAL, 1, 0x1FFFFFFF));
        }
        adapter = new FlyAdapter();
        setAdapter(adapter);

        try {
            if (!TextUtils.isEmpty(mCellBean.backColor)) {
                setBackgroundColor(Color.parseColor(mCellBean.backColor));
            }
        } catch (Exception e) {
            FlyLog.d("error! parseColor exception!" + e.toString());
        }

        if (mCellBean.recv != null) {
            if(mCellBean.recv.recvId!=null){
                recvEvent(ByteUtil.hexString2Bytes(mCellBean.recv.recvId));
            }
            if(mCellBean.recv.keyId!=null){
                recvEvent(ByteUtil.hexString2Bytes(mCellBean.recv.keyId));
            }
        }
    }


    @Override
    public boolean verify(CellBean cellBean) {
        return mCellBean != null && mCellBean.pages != null && !mCellBean.pages.isEmpty();
    }

    @Override
    public void loadingRes(CellBean cellBean) {

    }


    @Override
    public boolean recvEvent(byte[] key) {
        if (mCellBean == null || key==null) return false;
        String strkey = ByteUtil.bytes2HexString(key);
        if (TextUtils.isEmpty(strkey)) return false;
        if (mCellBean.recv != null && !TextUtils.isEmpty(mCellBean.recv.recvId)) {
            if (strkey.equals(mCellBean.recv.recvId)) {
                Object obj = FlyEvent.getValue(key);
                if (obj instanceof List) {
                    mList.clear();
                    try {
                        mList.addAll((Collection<? extends Map<String, Object>>) obj);
                    } catch (Exception e) {
                        FlyLog.e(e.toString());
                    }
                    refresh();
                }
            }
        }

        if (mCellBean.recv != null && !TextUtils.isEmpty(mCellBean.recv.keyId)) {
            if (strkey.equals(mCellBean.recv.keyId)) {
                Object obj = FlyEvent.getValue(mCellBean.recv.keyId);
                if (obj instanceof String) {
                    itemKey = (String) obj;
                    for (int i = 0; i < mList.size(); i++) {
                        if (itemKey.equals(mList.get(i).get(mCellBean.recv.keyId))) {
                            getLayoutManager().scrollToPosition(i);
                            break;
                        }
                    }
                    refresh();
                }
            }
        }

        return false;
    }


    private void refresh() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }


    class FlyAdapter extends RecyclerView.Adapter<ViewHolder> {

        @Override
        public int getItemCount() {
            return mList == null ? 0 : mList.size();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            SimplePageView simplePageView = new SimplePageView(getContext());
            simplePageView.setPageBean(mCellBean.pages.get(0));
            return new ViewHolder(simplePageView);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            holder.itemView.setTag(position);
            Object mainKey = "main key";
            if(mCellBean.recv!=null&&!TextUtils.isEmpty(mCellBean.recv.keyId)){
                mainKey = mList.get(position).get(mCellBean.recv.keyId);
            }

            holder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String sendKey = mCellBean.pages.get(0).send.eventId;
                        Object sendObj = mList.get((Integer) v.getTag()).get(mCellBean.recv.keyId);
                        FlyEvent.sendEvent(sendKey, sendObj);
                    } catch (Exception e) {
                        FlyLog.e(e.toString());
                    }
                }
            });

            boolean isSelect = mainKey != null && mainKey.equals(itemKey);
            holder.itemView.setEnabled(!isSelect);

            for (CellBean cellBean : mCellBean.pages.get(0).cellList) {
                if (cellBean.texts != null && !cellBean.texts.isEmpty()) {
                    for (TextBean textBean : cellBean.texts) {
                        if (textBean.recv == null || textBean.recv.keyId == null)
                            continue;
                        try {
                            int key = Integer.valueOf(textBean.recv.keyId, 16);
                            TextView textView = holder.texts.get(key);
                            if (textView != null) {
                                textView.setText(mList.get(position).get(textBean.recv.keyId) + "");
                                textView.setSelected(isSelect);
                            } else {
                                FlyLog.e("find by id empty");
                            }
                        } catch (Exception e) {
                            FlyLog.e(e.toString());
                        }
                    }
                }


                if (cellBean.images != null && !cellBean.images.isEmpty()) {
                    for (ImageBean imageBean : cellBean.images) {
                        if (imageBean.recv != null && imageBean.recv.keyId != null) {
                            try {
                                int key = Integer.valueOf(imageBean.recv.keyId, 16);
                                ImageView imageView = holder.images.get(key);
                                if (imageView != null) {
                                    if(imageBean.recv.recvId!=null){
                                        Object obj = mList.get(position).get(imageBean.recv.recvId);
                                        if(obj instanceof String){
                                            Glide.with(getContext()).load(obj).into(imageView);
                                        }

                                    }
                                    imageView.setSelected(isSelect);
                                } else {
                                    FlyLog.e("find by id empty");
                                }

                            } catch (Exception e) {
                                FlyLog.e(e.toString());
                            }
                        }
                    }
                }

                if (cellBean.celltype == CellType.TYPE_ANIMTOR) {
                    if (cellBean.recv == null || cellBean.recv.keyId == null)
                        continue;
                    try {
                        int key = Integer.valueOf(cellBean.recv.keyId, 16);
                        AnimtorCellView anim = holder.anims.get(key);
                        if (anim != null) {
                            anim.setSelected(isSelect);
                        } else {
                            FlyLog.e("find by id empty");
                        }
                    } catch (Exception e) {
                        FlyLog.e(e.toString());
                    }
                }

                if (cellBean.celltype == CellType.TYPE_IMAGE_TEXT) {
                    if (cellBean.recv == null || cellBean.recv.keyId == null)
                        continue;
                    try {
                        int key = Integer.valueOf(cellBean.recv.keyId, 16);
                        ImageTextCellView imgtx = holder.imgtxs.get(key);
                        if (imgtx != null) {
                            imgtx.setContentDrawable(mList.get(position).get(cellBean.recv.keyId) + "");
                            imgtx.setSelected(isSelect);
                        } else {
                            FlyLog.e("find by id empty");
                        }
                    } catch (Exception e) {
                        FlyLog.e(e.toString());
                    }
                }
            }
        }

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        Hashtable<Integer, TextView> texts = new Hashtable<>();
        Hashtable<Integer, ImageView> images = new Hashtable<>();
        Hashtable<Integer, AnimtorCellView> anims = new Hashtable<>();
        Hashtable<Integer, ImageTextCellView> imgtxs = new Hashtable<>();

        ViewHolder(View itemView) {
            super(itemView);
            for (CellBean cellBean : mCellBean.pages.get(0).cellList) {
                if (cellBean.texts != null && !cellBean.texts.isEmpty()) {
                    for (TextBean textBean : cellBean.texts) {
                        if (textBean.recv == null || textBean.recv.keyId == null)
                            continue;
                        try {
                            int key = Integer.valueOf(textBean.recv.keyId, 16);
                            TextView textView = itemView.findViewById(key);
                            if (textView != null) {
                                texts.put(key, textView);
                            } else {
                                FlyLog.e("find by id empty");
                            }
                        } catch (Exception e) {
                            FlyLog.e(e.toString());
                        }
                    }
                }

                if (cellBean.images != null && !cellBean.images.isEmpty()) {
                    for (ImageBean imageBean : cellBean.images) {
                        if (imageBean.recv == null || imageBean.recv.keyId == null)
                            continue;
                        try {
                            int key = Integer.valueOf(imageBean.recv.keyId, 16);
                            ImageView imageView = itemView.findViewById(key);
                            if (imageView != null) {
                                images.put(key, imageView);
                            } else {
                                FlyLog.d("find by id empty");
                            }
                        } catch (Exception e) {
                            FlyLog.e(e.toString());
                        }
                    }
                }

                if (cellBean.celltype == CellType.TYPE_ANIMTOR) {
                    if (cellBean.recv == null || cellBean.recv.keyId == null)
                        continue;
                    try {
                        int key = Integer.valueOf(cellBean.recv.keyId, 16);
                        AnimtorCellView anim = itemView.findViewById(key);
                        if (anim != null) {
                            anims.put(key, anim);
                        } else {
                            FlyLog.e("find by id empty");
                        }
                    } catch (Exception e) {
                        FlyLog.e(e.toString());
                    }
                }

                if (cellBean.celltype == CellType.TYPE_IMAGE_TEXT) {
                    if (cellBean.recv == null || cellBean.recv.keyId == null)
                        continue;
                    try {
                        int key = Integer.valueOf(cellBean.recv.keyId, 16);
                        ImageTextCellView imgtx = itemView.findViewById(key);
                        if (imgtx != null) {
                            imgtxs.put(key, imgtx);
                        } else {
                            FlyLog.e("find by id empty");
                        }
                    } catch (Exception e) {
                        FlyLog.e(e.toString());
                    }
                }
            }


        }
    }


}
