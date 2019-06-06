package com.flyzebra.flyui.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Author FlyZebra
 * 2019/5/14 11:07
 * Describ:
 **/
public class RecvBean implements Parcelable {
    public String recvId;
    public String visibleContent;
    public String disVisibleContent;
    public String animId;
    public String startAnim;
    public String animType;

    public String keyId;

    protected RecvBean(Parcel in) {
        recvId = in.readString();
        visibleContent = in.readString();
        disVisibleContent = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(recvId);
        dest.writeString(visibleContent);
        dest.writeString(disVisibleContent);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RecvBean> CREATOR = new Creator<RecvBean>() {
        @Override
        public RecvBean createFromParcel(Parcel in) {
            return new RecvBean(in);
        }

        @Override
        public RecvBean[] newArray(int size) {
            return new RecvBean[size];
        }
    };
}
