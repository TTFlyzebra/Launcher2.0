package com.flyzebra.flyui.bean;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;
import java.util.Queue;

public class CellBean implements Parcelable{
    public int cellId;
    public int celltype;
    public int resId;
    public int x;
    public int y;
    public int width;
    public int height;
    public String backColor;
    public String filterColor;
    public RecvBean recv;
    public SendBean send;
    public List<TextBean> texts;
    public List<ImageBean> images;
    public List<PageBean> pages;
    public List<CellBean> subCells;
    public String remark;
    public boolean isFoucus;

    public int left;
    public int right;
    public int up;
    public int down;
    public Queue<Integer> lefts;
    public Queue<Integer> rights;
    public Queue<Integer> ups;
    public Queue<Integer> downs;

    protected CellBean(Parcel in) {
        cellId = in.readInt();
        celltype = in.readInt();
        resId = in.readInt();
        x = in.readInt();
        y = in.readInt();
        width = in.readInt();
        height = in.readInt();
        backColor = in.readString();
        filterColor = in.readString();
        recv = in.readParcelable(RecvBean.class.getClassLoader());
        send = in.readParcelable(SendBean.class.getClassLoader());
        texts = in.createTypedArrayList(TextBean.CREATOR);
        images = in.createTypedArrayList(ImageBean.CREATOR);
        subCells = in.createTypedArrayList(CellBean.CREATOR);
        remark = in.readString();
    }

    public static final Creator<CellBean> CREATOR = new Creator<CellBean>() {
        @Override
        public CellBean createFromParcel(Parcel in) {
            return new CellBean(in);
        }

        @Override
        public CellBean[] newArray(int size) {
            return new CellBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(cellId);
        dest.writeInt(celltype);
        dest.writeInt(resId);
        dest.writeInt(x);
        dest.writeInt(y);
        dest.writeInt(width);
        dest.writeInt(height);
        dest.writeString(backColor);
        dest.writeString(filterColor);
        dest.writeParcelable(recv, flags);
        dest.writeParcelable(send, flags);
        dest.writeTypedList(texts);
        dest.writeTypedList(images);
        dest.writeTypedList(subCells);
        dest.writeString(remark);
    }
}
