package com.ppfuns.ppfunstv.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ppfuns.ppfunstv.R;
import com.ppfuns.ppfunstv.utils.BlurDrawable;


/**
 * Created by hmcxunxi on 2016/7/17.
 */
public class TipsDialog extends Dialog {

    private Context mContext;

    protected TipsDialog(Context context) {
        super(context);
        mContext = context;
    }

    public TipsDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
    }

    public static class Builder {
        private Context mContext;
        private TextView mMessage;
        private Button mBtnRetry;
        private Button mBtnCancel;
        private Button mBtnConfirm;

        private boolean isBlur; // 是否显示高斯模糊
        private View view;
        private RelativeLayout mBlurView;

        public static final int CONFIRM_BUTTON_FLAG = 0;
        public static final int RETRY_BUTTON_FLAG = 1;
        public static final int CANCEL_BUTTON_FLAG = 2;

        private String mMessageText = null;
        private String mRetryText = null;
        private String mConfirmText = null;
        private String mCancelText = null;

        private View.OnClickListener mConfirmListener;
        private View.OnClickListener mRetryListener;
        private View.OnClickListener mCanacelListener;

        public Builder(Context context) {
            mContext = context;
        }

        public TipsDialog create() {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final TipsDialog dialog = new TipsDialog(mContext, R.style.TV_DialogStyle);
            View layout = inflater.inflate(R.layout.tv_tips_dialog, null);
            mBlurView = (RelativeLayout) layout.findViewById(R.id.tv_blur_view);
            mMessage = (TextView) layout.findViewById(R.id.tv_message);
            mBtnRetry = (Button) layout.findViewById(R.id.tv_btn_retry);
            mBtnCancel = (Button) layout.findViewById(R.id.tv_btn_cancel);
            mBtnConfirm = (Button) layout.findViewById(R.id.tv_btn_confirm);
            dialog.addContentView(layout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ERROR);
            if(!TextUtils.isEmpty(mMessageText)) {
                mMessage.setText(mMessageText);
            }
            if (TextUtils.isEmpty(mConfirmText) && TextUtils.isEmpty(mRetryText) && TextUtils.isEmpty(mCancelText)) {
                //builder不设置button默认显示重试和取消按钮
                mBtnRetry.setVisibility(View.VISIBLE);
                mBtnCancel.setVisibility(View.VISIBLE);
            } else {
                if (!TextUtils.isEmpty(mConfirmText)) {
                    mBtnConfirm.setText(mConfirmText);
                    if(mConfirmListener != null) {
                        mBtnConfirm.setOnClickListener(mConfirmListener);
                    }
                    mBtnConfirm.setVisibility(View.VISIBLE);
                }
                if (!TextUtils.isEmpty(mRetryText)) {
                    if(mRetryListener != null) {
                        mBtnRetry.setOnClickListener(mRetryListener);
                    }
                    mBtnRetry.setText(mRetryText);
                    mBtnRetry.setVisibility(View.VISIBLE);
                }
                if (!TextUtils.isEmpty(mCancelText)) {
                    if(mCanacelListener != null) {
                        mBtnCancel.setOnClickListener(mCanacelListener);
                    }
                    mBtnCancel.setText(mCancelText);
                    mBtnCancel.setVisibility(View.VISIBLE);
                }
            }

            if(isBlur){ // 如果显示高斯模糊就载入高斯模糊背景图
                BlurDrawable blurDrawable = new BlurDrawable((Activity)mContext);
                blurDrawable.setBlurRadius(9); //模糊半径12，越大图片越平均
                blurDrawable.setDownsampleFactor(2); //图片抽样率，这里把图片缩放小了8倍
                blurDrawable.setOverlayColor(Color.argb(150, 0x0, 0x0, 0x0)); //模糊后再覆盖的一层颜色
                //blurDrawable.setDrawOffset(0,0); //顶部View与底部View的相对坐标差，由于这里都是(0,0)起步，所以相对位置偏移为0
                mBlurView.setBackgroundDrawable(blurDrawable);
            }

            return dialog;
        }

        public Builder setMessage(String message) {
            mMessageText = message;
            return this;
        }

        public Builder setConfirmButton(String text, View.OnClickListener listener) {
            setButton(CONFIRM_BUTTON_FLAG, text, listener);
            return this;
        }

        public Builder setRetryButton(String text, View.OnClickListener listener) {
            setButton(RETRY_BUTTON_FLAG, text, listener);
            return this;
        }

        public Builder setCancelButton(String text, View.OnClickListener listener) {
            setButton(CANCEL_BUTTON_FLAG, text, listener);
            return this;
        }

        public Builder setButton(int whichButton, String text, View.OnClickListener listener) {
            switch (whichButton) {
                case CONFIRM_BUTTON_FLAG:
                    mConfirmText = text;
                    mConfirmListener = listener;
                    break;
                case RETRY_BUTTON_FLAG:
                    mRetryText = text;
                    mRetryListener = listener;
                    break;
                case CANCEL_BUTTON_FLAG:
                    mCancelText = text;
                    mCanacelListener = listener;
                    break;
            }
            return this;
        }

        public Builder setIsBlur(boolean isBlur){
            this.isBlur = isBlur;
            return this;
        }
        public Builder setView(View view){
            this.view = view;
            return this;
        }

    }
}
