package com.flyzebra.ppfunstv.utils;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.view.View;

import com.flyzebra.ppfunstv.R;
import com.flyzebra.ppfunstv.constant.Constants;
import com.flyzebra.ppfunstv.view.TipsDialog;

/**
 * Created by flyzebra on 17-5-5.
 */
public class DialogUtil {
    private static TipsDialog dialog;

    /**
     * 显示只带确定按键的提示框
     *
     * @param context
     * @param info    提示信息
     */
    public static void showDialog(Context context, String info) {
        View view = null;
        Activity activity = getActivity(context);
        if (activity != null) {
            view = activity.getWindow().getDecorView();
        }

        boolean isBlur = Boolean.parseBoolean(SystemPropertiesProxy.get(context, Constants.Property.DIALOG_BLUR, "false"));
        if (!isBlur) {
            TipsDialog.Builder builder = new TipsDialog.Builder(context).
                    setMessage(info).
                    setConfirmButton(context.getResources().getString(R.string.tv_confirm), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (dialog != null) {
                                dialog.dismiss();
                                dialog = null;
                            }
                        }
                    }).
                    //背景高斯模糊处理
                            setIsBlur(false).
                            setView(view);
            dialog = builder.create();
            dialog.show();
        } else {
            TipsDialog.Builder builder = new TipsDialog.Builder(context).
                    setMessage(info).
                    setConfirmButton(context.getResources().getString(R.string.tv_confirm), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (dialog != null) {
                                dialog.dismiss();
                                dialog = null;
                            }
                        }
                    }).
                    //背景高斯模糊处理
                            setIsBlur(true).
                            setView(view);
            dialog = builder.create();
            dialog.show();
        }

    }

    private static Activity getActivity(Context context) {
        while (!(context instanceof Activity) && context instanceof ContextWrapper) {
            context = ((ContextWrapper) context).getBaseContext();
        }

        if (context instanceof Activity) {
            return (Activity) context;
        }

        new Exception("").printStackTrace();

        return null;
    }

}
