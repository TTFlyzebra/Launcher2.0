package com.flyzebra.flyui.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.Gravity;

/**
 * Author FlyZebra
 * 2019/5/8 9:47
 * Describ:
 **/
public class TextBean implements Parcelable {
    public int textId = 0;
    public int textSize =24;
    public int textLines = 1;
    public String textColor;
    public String textFilter;
    public LanguageText text;
    public int left = 0;
    public int top = 0;
    public int right = 0;
    public int bottom = 0;
    public int gravity = 0;
    public RecvBean recv;
    public SendBean send;

    protected TextBean(Parcel in) {
        textSize = in.readInt();
        textLines = in.readInt();
        textColor = in.readString();
        textFilter = in.readString();
        text = in.readParcelable(LanguageText.class.getClassLoader());
        left = in.readInt();
        top = in.readInt();
        right = in.readInt();
        bottom = in.readInt();
        gravity = in.readInt();
        recv = in.readParcelable(RecvBean.class.getClassLoader());
        send = in.readParcelable(SendBean.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(textSize);
        dest.writeInt(textLines);
        dest.writeString(textColor);
        dest.writeString(textFilter);
        dest.writeParcelable(text, flags);
        dest.writeInt(left);
        dest.writeInt(top);
        dest.writeInt(right);
        dest.writeInt(bottom);
        dest.writeInt(gravity);
        dest.writeParcelable(recv, flags);
        dest.writeParcelable(send, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TextBean> CREATOR = new Creator<TextBean>() {
        @Override
        public TextBean createFromParcel(Parcel in) {
            return new TextBean(in);
        }

        @Override
        public TextBean[] newArray(int size) {
            return new TextBean[size];
        }
    };

    public int getGravity() {
        switch (gravity) {
            case 0:
                return Gravity.CENTER;
            case 1:
                return Gravity.START;
            case 2:
                return Gravity.END;
            case 3:
                return Gravity.START|Gravity.CENTER;
            case 4:
                return Gravity.END|Gravity.CENTER;
            default:
                return Gravity.CENTER;
        }
    }

}
