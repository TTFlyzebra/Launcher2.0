package com.flyzebra.flyui.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

/**
 * Author FlyZebra
 * 2019/5/8 9:47
 * Describ:
 **/
public class ImageBean implements Parcelable {
    public int imageId = 0;
    public int width;
    public int height;
    public String url;
    public String filterColor;
    public int left;
    public int top;
    public int right;
    public int bottom;
    public int scaleType;
    public int shapeType;
    public RecvBean recv;
    public SendBean send;

    protected ImageBean(Parcel in) {
        width = in.readInt();
        height = in.readInt();
        url = in.readString();
        filterColor = in.readString();
        left = in.readInt();
        top = in.readInt();
        right = in.readInt();
        bottom = in.readInt();
        scaleType = in.readInt();
        recv = in.readParcelable(RecvBean.class.getClassLoader());
        send = in.readParcelable(SendBean.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(width);
        dest.writeInt(height);
        dest.writeString(url);
        dest.writeString(filterColor);
        dest.writeInt(left);
        dest.writeInt(top);
        dest.writeInt(right);
        dest.writeInt(bottom);
        dest.writeInt(scaleType);
        dest.writeParcelable(recv, flags);
        dest.writeParcelable(send, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ImageBean> CREATOR = new Creator<ImageBean>() {
        @Override
        public ImageBean createFromParcel(Parcel in) {
            return new ImageBean(in);
        }

        @Override
        public ImageBean[] newArray(int size) {
            return new ImageBean[size];
        }
    };

    public ImageView.ScaleType getScaleType() {
        switch (scaleType) {
            case 0:
                return ImageView.ScaleType.FIT_XY;
            case 1:
                return ImageView.ScaleType.FIT_CENTER;
            case 2:
                return ImageView.ScaleType.CENTER;
            case 3:
                return ImageView.ScaleType.CENTER_CROP;
            case 4:
                return ImageView.ScaleType.CENTER_INSIDE;
            default:
                return ImageView.ScaleType.CENTER;
        }
    }



}
