package com.flyzebra.flyui.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.Locale;

public class LanguageText implements Parcelable {
    public String zh_rCN;
    public String zh_rTW;
    public String en;
    public String ru;
    public String el;
    public String pl;
    public String tr;
    public String ar;
    public String fa;
    public String ro;
    public String fr;
    public String hu;
    public String it;
    public String th;
    public String de;
    public String uk;
    public String es;
    public String pt;

    protected LanguageText(Parcel in) {
        zh_rCN = in.readString();
        zh_rTW = in.readString();
        en = in.readString();
        ru = in.readString();
        el = in.readString();
        pl = in.readString();
        tr = in.readString();
        ar = in.readString();
        fa = in.readString();
        ro = in.readString();
        fr = in.readString();
        hu = in.readString();
        it = in.readString();
        th = in.readString();
        de = in.readString();
        uk = in.readString();
        es = in.readString();
        pt = in.readString();
    }

    public static final Creator<LanguageText> CREATOR = new Creator<LanguageText>() {
        @Override
        public LanguageText createFromParcel(Parcel in) {
            return new LanguageText(in);
        }

        @Override
        public LanguageText[] newArray(int size) {
            return new LanguageText[size];
        }
    };

    public String getText() {
        String text = "";
        String language = Locale.getDefault().getLanguage();
        String country = Locale.getDefault().getCountry();
        String type = language + "-" + country;
        type = type.toLowerCase();

        if(type.startsWith("zh-cn")){
            text = zh_rCN;
        }else if(type.startsWith("zh-")){
            text = zh_rTW;
        }else if(type.startsWith("en")){
            text = en;
        }else if(type.startsWith("ru")){
            text = ru;
        }else if(type.startsWith("el")){
            text = el;
        }else if(type.startsWith("pl")){
            text = pl;
        }else if(type.startsWith("tr")){
            text = tr;
        }else if(type.startsWith("ar")){
            text = ar;
        }else if(type.startsWith("fa")){
            text = fa;
        }else if(type.startsWith("ro")){
            text = ro;
        }else if(type.startsWith("fr")){
            text = fr;
        }else if(type.startsWith("hu")){
            text = hu;
        }else if(type.startsWith("it")){
            text = it;
        }else if(type.startsWith("th")){
            text = th;
        }else if(type.startsWith("de")){
            text = de;
        }else if(type.startsWith("uk")){
            text = uk;
        }else if(type.startsWith("es")){
            text = es;
        }else if(type.startsWith("pt")){
            text = pt;
        }else{
            text = en;
        }

        text =  TextUtils.isEmpty(text)?zh_rCN:text;
        text = text.replace("\\n","\n");
        return text;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(zh_rCN);
        dest.writeString(zh_rTW);
        dest.writeString(en);
        dest.writeString(ru);
        dest.writeString(el);
        dest.writeString(pl);
        dest.writeString(tr);
        dest.writeString(ar);
        dest.writeString(fa);
        dest.writeString(ro);
        dest.writeString(fr);
        dest.writeString(hu);
        dest.writeString(it);
        dest.writeString(th);
        dest.writeString(de);
        dest.writeString(uk);
        dest.writeString(es);
        dest.writeString(pt);
    }
}
