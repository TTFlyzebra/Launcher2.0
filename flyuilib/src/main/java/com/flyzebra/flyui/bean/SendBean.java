package com.flyzebra.flyui.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Author FlyZebra
 * 2019/5/14 11:07
 * Describ:
 **/
public class SendBean implements Parcelable {
    public String packName;
    public String className;
    public String intent;
    public String flyAction;
    public String eventId;
    public String eventContent;

    protected SendBean(Parcel in) {
        packName = in.readString();
        className = in.readString();
        intent = in.readString();
        flyAction = in.readString();
        eventId = in.readString();
        eventContent = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(packName);
        dest.writeString(className);
        dest.writeString(intent);
        dest.writeString(flyAction);
        dest.writeString(eventId);
        dest.writeString(eventContent);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SendBean> CREATOR = new Creator<SendBean>() {
        @Override
        public SendBean createFromParcel(Parcel in) {
            return new SendBean(in);
        }

        @Override
        public SendBean[] newArray(int size) {
            return new SendBean[size];
        }
    };
}
