package com.flyzebra.flyui.view.cellview;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;

import com.flyzebra.flyui.bean.CellBean;
import com.flyzebra.flyui.bean.TextBean;
import com.flyzebra.flyui.utils.FlyLog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class DateCellView extends SimpleCellView {
    private Timer mTimer;
    private String[] textStr;
    private static final String[] DATE_FORMAT = new String[]{"HH:mm", "yyyy-MM-dd", "E", "a"};

    private Handler mHandler = new Handler(Looper.getMainLooper());

    public DateCellView(Context context) {
        super(context);
    }

    @Override
    public boolean verify(CellBean cellBean) {
        return mCellBean != null && ((mCellBean.texts != null && (mCellBean.texts.size() > 0)));
    }

    @Override
    public void init(CellBean cellBean) {
        super.init(cellBean);
        textStr = new String[mCellBean.texts.size()];
        for (int i = 0; i < textStr.length; i++) textStr[i] = "";
        if (mTimer == null) {
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    boolean isUpdate = false;
                    for (int i = 0; i < mCellBean.texts.size(); i++) {
                        String text = getCurrentDate(getFormat(mCellBean.texts.get(i), i));
                        if (!TextUtils.isEmpty(text)) {
                            text = text.replace("周", "星期");
                            text = text.replace("週", "星期");
                            if (!textStr[i].equals(text)) {
                                textStr[i] = text;
                                if (!isUpdate) {
                                    isUpdate = true;
                                }
                            }
                        }
                    }

                    if (isUpdate) {
                        mHandler.removeCallbacksAndMessages(null);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                refresh(mCellBean);
                            }
                        });
                    }
                }
            }, 0, 1000);
        }

        try {
            if(!TextUtils.isEmpty(mCellBean.backColor)){
                setBackgroundColor(Color.parseColor(mCellBean.backColor));
            }
        } catch (Exception e) {
            FlyLog.e("error! parseColor exception!" + e.toString());
        }
    }

    private String getFormat(TextBean textBean, int i) {
        boolean bTime24 = false;
        try{
            bTime24 = Settings.System.getString(getContext().getContentResolver(), Settings.System.TIME_12_24).equals("24");
        }catch (Exception e){
            FlyLog.e(e.toString());
        }
        if (textBean.recv == null) {
            return i < 3 ? DATE_FORMAT[i] : "";
        } else {
            if (TextUtils.isEmpty(textBean.recv.recvId)) {
                return i < 3 ? DATE_FORMAT[i] : "";
            } else {
                switch (textBean.recv.recvId) {
                    case "100101":
                        if(bTime24){
                            return DATE_FORMAT[0].replace("h","H");
                        }else{
                            return DATE_FORMAT[0].replace("H","h");
                        }
                    case "100102":
                        return DATE_FORMAT[1];
                    case "100103":
                        return DATE_FORMAT[2];
                    case "100104":
                        return bTime24?"":DATE_FORMAT[3];
                    default:
                        return "";
                }
            }
        }
    }

    @Override
    public void refresh(CellBean cellBean) {
        super.refresh(cellBean);
        try {
            FlyLog.d("up time");
            for (int i = 0; i < textViewList.size(); i++) {
                textViewList.get(i).setText(textStr[i]);
            }
        } catch (Exception e) {
            FlyLog.e(e.toString());
        }
    }

    private static String getCurrentDate(String dateFormat) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.getDefault());
            Date date = new Date(System.currentTimeMillis());
            return sdf.format(date);
        }catch (Exception e){
            return "";
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        mHandler.removeCallbacksAndMessages(null);
        if (mTimer != null) {
            mTimer.cancel();
        }
        super.onDetachedFromWindow();
    }
}
