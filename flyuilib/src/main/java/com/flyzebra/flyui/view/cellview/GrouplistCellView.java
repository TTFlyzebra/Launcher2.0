package com.flyzebra.flyui.view.cellview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.flyzebra.flyui.bean.CellBean;
import com.flyzebra.flyui.bean.ImageBean;
import com.flyzebra.flyui.bean.PageBean;
import com.flyzebra.flyui.bean.TextBean;
import com.flyzebra.flyui.event.FlyEvent;
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
public class GrouplistCellView extends BaseRecyclerCellView {
    private List<Map<String, Object>> mShowList = new ArrayList<>();
    private List<Map<String, Object>> mAllList = new ArrayList<>();
    private FlyAdapter adapter;
    private ArrayMap<String, Bitmap> mAllBitmap = new ArrayMap<>();
    private int maxColumn = 1;
    private Map<String, Object> mShowMap = null;

    private String itemKey = "main key";

    public GrouplistCellView(Context context) {
        super(context);
    }

    @Override
    public boolean verify(CellBean cellBean) {
        return mCellBean != null && mCellBean.pages != null && mCellBean.pages.size() > 1;
    }

    @Override
    public void init(CellBean cellBean) {
        FlyLog.d("ListCellView x=%d,y=%d", mCellBean.x, mCellBean.y);
        for (PageBean pageBean : mCellBean.pages) {
            maxColumn = Math.max(maxColumn, cellBean.width / pageBean.width);
        }

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), maxColumn);
        setLayoutManager(gridLayoutManager);
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
            if (mCellBean.recv.recvId != null) {
                recvEvent(ByteUtil.hexString2Bytes(mCellBean.recv.recvId));
            }
            if (mCellBean.recv.keyId != null) {
                recvEvent(ByteUtil.hexString2Bytes(mCellBean.recv.keyId));
            }
        }

    }


    private Bitmap getBitmapById(String imageUrl) {
        return mAllBitmap.isEmpty() ? null : mAllBitmap.get(imageUrl);
    }


    private void addChildItem(Map<String, Object> mSelectMap) {
        FlyLog.d("addChildItem");
        mShowList.clear();
        boolean flag = false;
        for (int i = 0; i < mAllList.size(); i++) {
            Map<String, Object> map = mAllList.get(i);
            if (0 == (int) map.get("10FF02")) {
                mShowList.add(map);
                flag = map.equals(mSelectMap);
            } else {
                if (flag) {
                    mShowList.add(map);
                }
            }
        }
        FlyLog.d("mShowList size=%s", mShowList.size());
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }


    @Override
    public boolean recvEvent(byte[] key) {
        if (mCellBean == null)
            return false;
        String strkey = ByteUtil.bytes2HexString(key);
        if (mCellBean.recv.recvId != null && mCellBean.recv.recvId.equals(strkey)) {
            Object obj = FlyEvent.getValue(key);
            if (obj instanceof List) {
                mAllList.clear();
                try {
                    mAllList.addAll((Collection<? extends Map<String, Object>>) obj);
                    scrollToKeyCurrent();
                } catch (Exception e) {
                    FlyLog.e(e.toString());
                }
                FlyLog.d("addChildItem 3");
            }
        }

        if (mCellBean.recv != null && !TextUtils.isEmpty(mCellBean.recv.keyId)) {
            if (mCellBean.recv.keyId.equals(strkey)) {
                Object obj = FlyEvent.getValue(mCellBean.recv.keyId);
                if (obj instanceof String) {
                    itemKey = (String) obj;
                    scrollToKeyCurrent();
                }
            }
        }

        return false;
    }


    /**
     * 滚动到当前播放歌曲
     */
    private void scrollToKeyCurrent() {
        if (mCellBean == null || mCellBean.pages == null || mCellBean.pages.isEmpty() || mAllList == null || mAllList.isEmpty())
            return;

        int num = 0;
        for (int i = mAllList.size() - 1; i >= 0; i--) {
            if (itemKey.equals(mAllList.get(i).get(mCellBean.recv.keyId))) {
                mAllList.get(i).put("select", true);
                num = i;
            } else {
                mAllList.get(i).put("select", false);
            }
        }

        for (int j = num; j >= 0; j--) {
            if (0 == (int) mAllList.get(j).get("10FF02")) {
                mAllList.get(j).put("select", true);
                mShowMap = mAllList.get(j);
                break;
            }
        }

//        跳转到子歌曲
        addChildItem(mShowMap);
        if (mShowList == null || mShowList.isEmpty()) return;
        for (int i = mShowList.size() - 1; i >= 0; i--) {
            if ((boolean) mShowList.get(i).get("select")) {
                getLayoutManager().scrollToPosition(i);
                break;
            }
        }

        //不跳转到子歌曲
//        addChildItem(null);
//        if (mShowList == null || mShowList.isEmpty()) return;
//        if (mShowMap != null) {
//            for (int i = 0; i < mShowList.size(); i++) {
//                if (mShowList.get(i).equals(mShowMap)) {
//                    getLayoutManager().scrollToPosition(i);
//                }
//            }
//        }

    }

    class FlyAdapter extends Adapter<ViewHolder> {

        @Override
        public int getItemCount() {
            return mShowList == null ? 0 : mShowList.size();
        }

        @Override
        public int getItemViewType(int position) {
            return (int) mShowList.get(position).get("10FF02");
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            SimplePageView simplePageView = new SimplePageView(getContext());
            simplePageView.setPageBean(mCellBean.pages.get(viewType));
            return new ViewHolder(simplePageView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            int type = (Integer) mShowList.get(position).get("10FF02");
            final PageBean pageBean = mCellBean.pages.get(type);
            holder.itemView.setTag(position);
            boolean isSelect = false;
            Object select = mShowList.get(position).get("select");
            if (select instanceof Boolean) {
                isSelect = (boolean) mShowList.get(position).get("select");
            }
            holder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = (int) v.getTag();
                    Object objtype = mShowList.get(pos).get("10FF02");
                    int type = -1;
                    if (objtype instanceof Integer) type = (int) objtype;
                    if (type == 0) {
                        if (mShowMap == null) {
                            mShowMap = mShowList.get(pos);
                        } else {
                            if (mShowMap.equals(mShowList.get(pos))) {
                                mShowMap = null;
                            } else {
                                mShowMap = mShowList.get(pos);
                            }
                        }
                        addChildItem(mShowMap);
                        if (mShowMap != null) {
                            for (int i = 0; i < mShowList.size(); i++) {
                                if (mShowList.get(i).equals(mShowMap)) {
                                    getLayoutManager().scrollToPosition(i);
                                }
                            }
                        }
                    } else {
                        if (pageBean.send != null && !TextUtils.isEmpty(pageBean.send.eventId) && mCellBean.recv != null && !TextUtils.isEmpty(mCellBean.recv.keyId)) {
                            String sendKey = pageBean.send.eventId;
                            Object sendObj = mShowList.get((Integer) v.getTag()).get(mCellBean.recv.keyId);
                            FlyEvent.sendEvent(sendKey, sendObj);
                        }
                    }

                }
            });
            if (type != 0) {
                holder.itemView.setEnabled(!isSelect);
            }
            for (CellBean cellBean : pageBean.cellList) {
                if (cellBean.texts != null && !cellBean.texts.isEmpty()) {
                    for (TextBean textBean : cellBean.texts) {
                        if (textBean.recv == null || textBean.recv.keyId == null)
                            continue;
                        try {
                            int key = Integer.valueOf(textBean.recv.keyId, 16);
                            TextView textView = holder.texts.get(key);
                            if (textView != null) {
                                textView.setText(mShowList.get(position).get(textBean.recv.keyId) + "");
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
                                        Object obj = mShowList.get(position).get(imageBean.recv.recvId);
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
                            if (type == 0 && "10FFF3".equals(cellBean.recv.keyId)) {
                                boolean isOpen = mShowList.get(position).equals(mShowMap);
                                imgtx.setContentDrawable(isOpen ? "open" : "close");
                            } else {
                                imgtx.setContentDrawable(mShowList.get(position).get(cellBean.recv.keyId) + "");
                            }
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

        @Override
        public void onAttachedToRecyclerView(@NonNull final RecyclerView recyclerView) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int type = (int) mShowList.get(position).get("10FF02");
                    return maxColumn / (mCellBean.width / mCellBean.pages.get(type).width);
                }
            });
        }

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        Hashtable<Integer, TextView> texts = new Hashtable<>();
        Hashtable<Integer, ImageView> images = new Hashtable<>();
        Hashtable<Integer, AnimtorCellView> anims = new Hashtable<>();
        Hashtable<Integer, ImageTextCellView> imgtxs = new Hashtable<>();

        ViewHolder(View itemView) {
            super(itemView);
            for (PageBean pageBean : mCellBean.pages) {
                for (CellBean cellBean : pageBean.cellList) {
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
                                    FlyLog.d("find by id empty");
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
                                FlyLog.d("find by id empty");
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
                                FlyLog.d("find by id empty");
                            }
                        } catch (Exception e) {
                            FlyLog.e(e.toString());
                        }
                    }
                }

            }
        }
    }

}
