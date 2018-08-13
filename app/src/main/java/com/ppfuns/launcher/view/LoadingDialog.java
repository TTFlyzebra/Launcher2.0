package com.ppfuns.launcher.view;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.TextView;

import com.ppfuns.launcher.R;
import com.ppfuns.ppfunstv.utils.FlyLog;


public class LoadingDialog extends AlertDialog {
	private Context context;
	private TextView content_text ;
	private String content ;
	private MyOnCancelListerner myOnCancelListerner;
	public LoadingDialog(Context context) {
		super(context, R.style.loadingDialog);
		this.context = context;
	}
	public LoadingDialog(Context context, String content) {
		super(context, R.style.loadingDialog);
		this.context = context;
		if(!TextUtils.isEmpty(content)){
			this.content = content ;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		setContentView(R.layout.loading_dialog);
		content_text = (TextView) findViewById(R.id.content_text) ;
		if(!TextUtils.isEmpty(content)){
			content_text.setText(content) ;
		}
	}
	public void setDialogContent(String content){
		if(content_text != null && !TextUtils.isEmpty(content)){
			content_text.setText(content) ;
		}
	}

	public MyOnCancelListerner getMyOnCancelListerner() {
		return myOnCancelListerner;
	}

	public void setMyOnCancelListerner(MyOnCancelListerner myOnCancelListerner) {
		this.myOnCancelListerner = myOnCancelListerner;
	}

	public interface MyOnCancelListerner{
		public void cancel();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		FlyLog.d("keyCode %d",keyCode);
		return true;
	}

	@Override
	public void show() {
		try {
			super.show();
			mHandler.removeMessages(MSG_DISMISS);
			mHandler.sendEmptyMessageDelayed(MSG_DISMISS,TIME_DISMISS);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void dismiss() {
		mHandler.removeMessages(MSG_DISMISS);
		super.dismiss();
	}

	private static final int TIME_DISMISS = 15 * 1000;
	private static final int MSG_DISMISS = 1;
	private Handler mHandler = new Handler(Looper.myLooper()){
		@Override
		public void dispatchMessage(Message msg) {
			switch (msg.what){
				case MSG_DISMISS:
					dismiss();
					break;
				default:
					break;
			}
			super.dispatchMessage(msg);
		}
	};

}
