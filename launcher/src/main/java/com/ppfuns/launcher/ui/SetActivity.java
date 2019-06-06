package com.ppfuns.launcher.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;

import com.ppfuns.launcher.R;
import com.ppfuns.launcher.adapter.GridAdapter;
import com.ppfuns.launcher.base.BaseActivity;
import com.ppfuns.launcher.base.BaseApplication;
import com.ppfuns.launcher.constant.Constants;
import com.ppfuns.launcher.utils.FlyLog;
import com.ppfuns.launcher.utils.ServiceUtils;
import com.ppfuns.launcher.utils.SystemPropertiesProxy;
import com.ppfuns.messageservice.IMessageService;
import com.ppfuns.ppfunstv.receiver.MessageReceiver;
import com.ppfuns.ppfunstv.receiver.NetworkReceiver;
import com.ppfuns.ppfunstv.receiver.UsbStateReceiver;
import com.ppfuns.ppfunstv.utils.DialogUtil;
import com.ppfuns.ppfunstv.view.TvView.CellView.CellClickAction.CommondTool;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

/**
 * Created by pc1 on 2016/7/6.
 */
public class SetActivity extends BaseActivity implements UsbStateReceiver.EventListener, NetworkReceiver.EventListener, MessageReceiver.EventListener {
    private static final String TAG = SetActivity.class.getSimpleName();
    private static final String ACTION_MESSAGE = "com.ppfuns.messageservice.action.history";
    private static final String TRUE = "true";
    private static final int UPDATE_BLUR_BITMAP = 1;
    private final int STATUS_SUM = 6;
    private final int IMG_INDEX_USER = 1;
    private final int IMG_INDEX_SEARCH = 2;
    private final int IMG_INDEX_SETTING = 3;
    private final int IMG_INDEX_WIRED = 4;
    private final int IMG_INDEX_MESSAGE = 0;
    private final int IMG_INDEX_USB = 5;
    MessageReceiver mMessageReceiver;
    private Context mContext;
    private GridView mGridView;
    private GridAdapter mAdapter;
    private ViewGroup mContent;
    private ViewGroup mFlContent;
    private ArrayList<Integer> mImgs = new ArrayList<>();
    private ArrayList<Integer> mInfos = new ArrayList<>();
    private NetworkReceiver mNetworkReceiver;
    private IMessageService mMessageService;
    private UsbStateReceiver usbReceiver;
//    private View mWeateherLine;
//    private RelativeLayout mRlWeather;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void dispatchMessage(Message msg) {
            FlyLog.d("receiver msg:" + msg.what);
            switch (msg.what) {
                case UPDATE_BLUR_BITMAP: {
                    try {
                        mContent.setBackground(new BitmapDrawable(mContext.getResources(), ((BaseApplication) getApplication()).mGaussBlurBitmap));
                        mFlContent.setBackgroundColor(getResources().getColor(R.color.gb_transucent));
                        FlyLog.d("onKeyDownGoUp updataBackGround time = " + System.currentTimeMillis());
                    } catch (Exception e) {
                        e.printStackTrace();
                        FlyLog.e(e.toString());
                    }
                    break;
                }
                default:
                    break;
            }
            super.dispatchMessage(msg);
        }
    };
    private ServiceConnection mMessageConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMessageService = IMessageService.Stub.asInterface(service);
            updateMessageView();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            ServiceUtils.bindServiceCls(mContext, Constants.MESSAGE_PACKAGE_NAME, Constants.MESSAGE_ACTIVITY_NAME, mMessageConnection);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);
        mContext = this;
        ServiceUtils.bindServiceCls(this, Constants.MESSAGE_PACKAGE_NAME, Constants.MESSAGE_ACTIVITY_NAME, mMessageConnection);
        initView();
        initData();
        updateBackGround();
        registerReceiver();
    }

    private void registerReceiver() {
        mNetworkReceiver = new NetworkReceiver(this);
        mNetworkReceiver.register(this);
        mMessageReceiver = new MessageReceiver(this);
        mMessageReceiver.register(this);
        usbReceiver = new UsbStateReceiver(this);
        usbReceiver.register(this);
    }

    private void unRegisterReceiver() {
        mNetworkReceiver.unRegister();
        mMessageReceiver.unRegister();
    }

    @Override
    protected void onResume() {
        MobclickAgent.onPageStart(TAG);
        super.onResume();
        updateVisivable();
        mGridView.requestFocus();
        updateMessageView();
    }

    private void updateVisivable(){
        String show = SystemPropertiesProxy.get(mContext,Constants.Property.SHOW_WEATHER,"");
        if(TextUtils.isEmpty(show)){
            show = getString(R.string.showWeather);
        }
        FlyLog.d("show:"+show +" property:"+SystemPropertiesProxy.get(mContext,Constants.Property.SHOW_WEATHER,""));
//        if(TRUE.equals(show)){
////            mRlWeather.setVisibility(View.VISIBLE);
//            mWeateherLine.setVisibility(View.VISIBLE);
//        }else {
////            mRlWeather.setVisibility(View.GONE);
//            mWeateherLine.setVisibility(View.GONE);
//        }

    }

    private void updateBackGround() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.dimAmount = 0.92f;
        getWindow().setAttributes(lp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

//        BlurBehind.getInstance()
//                .withAlpha(80)
//                .withFilterColor(Color.parseColor("#0075c0")) //or Color.RED
//                .setBackground(this);

        FlyLog.d("onKeyDownGoUp updateBackGround time = " + System.currentTimeMillis());
    }

    @Override
    protected void onPause() {
        MobclickAgent.onPageEnd(TAG);
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        ((BaseApplication) getApplication()).mGaussBlurBitmap = null;
    }

    @Override
    protected void onDestroy() {
        unRegisterReceiver();
        unbindService();
        usbReceiver.unRegister();

        super.onDestroy();
    }

    private void unbindService() {
        if (mMessageConnection != null) {
            unbindService(mMessageConnection);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        FlyLog.d(TAG + " dispatchKeyEvent: action " + event.getAction() + " keycode:" + event.getKeyCode());
        if (KeyEvent.ACTION_DOWN == event.getAction()) {
            switch (event.getKeyCode()) {
//                case KeyEvent.KEYCODE_DPAD_DOWN:
                case KeyEvent.KEYCODE_ESCAPE:
                case KeyEvent.KEYCODE_DPAD_DOWN:
                case KeyEvent.KEYCODE_BACK: {
                    finish();
                    break;
                }
                case KeyEvent.KEYCODE_ENTER:
                case KeyEvent.KEYCODE_DPAD_CENTER: {
                    mAdapter.getItemClick().onItemClick(mGridView.getSelectedItemPosition());
                    break;
                }
                case KeyEvent.KEYCODE_DPAD_LEFT:
                case KeyEvent.KEYCODE_DPAD_RIGHT: {
                    break;
                }
            }
//            if (BackdoorUtil.checkBackDoorEntry(event)) {
//                if(!CommondTool.execStartPackage(mContext,Constants.UPDATE_PACKAGE,Constants.UPDATE_CLASS)){
//                    ToastUtils.showMessage(mContext,R.string.app_not_install);
//                }
//                return false;
//            }else if(BackdoorUtil.checkSetPropertyBackDoor(event)){
//                if(!CommondTool.execStartPackage(mContext,Constants.PACKAGE_PROPERTY_SET)){
//                    ToastUtils.showMessage(mContext,R.string.app_not_install);
//                }
//                return false;
//            }
        }
        return super.dispatchKeyEvent(event);
    }

    private void initView() {
        mGridView = (GridView) findViewById(R.id.ac_set_gridview);
        mContent = (ViewGroup) findViewById(R.id.content);
        mFlContent = (ViewGroup) findViewById(R.id.fl_content);
//        mWeateherLine = findViewById(R.id.status_division_two);
//        mRlWeather = (RelativeLayout) findViewById(R.id.set_status_top_weather);
    }

    private void initData() {
        mImgs.clear();
        mImgs.add(R.drawable.tv_statu_message);
        mImgs.add(R.drawable.statu_personal);
        mImgs.add(R.drawable.statu_search);
        mImgs.add(R.drawable.statu_setting);
        mImgs.add(R.drawable.statu_no_wired);

        mInfos.clear();
        mInfos.add(R.string.status_info);
        mInfos.add(R.string.stuta_my);
        mInfos.add(R.string.stuta_search);
        mInfos.add(R.string.stuta_setting);
        mInfos.add(R.string.stuta_network);

        mAdapter = new GridAdapter(this, mImgs, mInfos);
        mAdapter.setItemClick(new GridAdapter.OnItemClick() {
            @Override
            public void onItemClick(int pos) {
                switch (pos) {
                    case IMG_INDEX_USER:
                        CommondTool.execStartActivityAndShowTip(mContext, Constants.Action.MY_INFO, null, getString(R.string.app_not_install), false);
                        break;
                    case IMG_INDEX_SEARCH:
                        String data = "searchType=all";
                        CommondTool.execStartActivityAndShowTip(mContext, Constants.Action.VOD_SEARCH, data, getString(R.string.app_not_install), false);
                        break;
                    case IMG_INDEX_SETTING:
                        CommondTool.execStartActivity(mContext,R.string.actionSettings);
                        break;
                    case IMG_INDEX_WIRED:
                        CommondTool.execStartActivity(mContext,R.string.actionSetinngNetwork);
                        break;
                    case IMG_INDEX_MESSAGE:
                        CommondTool.execStartActivityAndShowTip(mContext, ACTION_MESSAGE, null, getString(R.string.app_not_install), false);
                        break;
                    case IMG_INDEX_USB:
                        if (!CommondTool.execStartPackage(mContext, Constants.PACKAGE_FILE_MANAGER)) {
                            DialogUtil.showDialog(mContext, getString(R.string.app_not_install));
                        }
                        break;
                    default:
                        break;
                }
            }
        });
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mAdapter.setCurPostition(position);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.anim_exit);
    }

    @Override
    public void mounted() {
        if (STATUS_SUM == mAdapter.getCount() + 1) {
            mAdapter.addData(STATUS_SUM - 1, R.drawable.statu_usb, R.string.status_usb);
        }
        mGridView.setNumColumns(STATUS_SUM);
    }

    @Override
    public void unMounted() {
        if (STATUS_SUM == mAdapter.getCount()) {
            mAdapter.removeData(mAdapter.getCount() - 1);
        }
        mGridView.setNumColumns(STATUS_SUM - 1);
    }

    @Override
    public void wifiConnected() {
        mImgs.set(IMG_INDEX_WIRED, R.drawable.statu_wifi);
        mAdapter.setData(IMG_INDEX_WIRED, R.drawable.statu_wifi);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void wifiDisconnected() {
        mImgs.set(IMG_INDEX_WIRED, R.drawable.statu_no_wifi);
        mAdapter.setData(IMG_INDEX_WIRED, R.drawable.statu_no_wifi);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void ethernetConnected() {
        mImgs.set(IMG_INDEX_WIRED, R.drawable.statu_wired);
        mAdapter.setData(IMG_INDEX_WIRED, R.drawable.statu_wired);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void ethernetDisconnected() {
        mImgs.set(IMG_INDEX_WIRED, R.drawable.statu_no_wired);
        mAdapter.setData(IMG_INDEX_WIRED, R.drawable.statu_no_wired);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void messageChanged() {
        updateMessageView();
    }

    private void updateMessageView() {
        if (mMessageService != null) {
            try {
                int messageNum = mMessageService.getUnreadMessageNum();
                FlyLog.e(TAG + " message change,service num:" + messageNum);
                if (messageNum != 0) {
                    mAdapter.setData(IMG_INDEX_MESSAGE, R.drawable.tv_statu_message_new);
                } else {
                    mAdapter.setData(IMG_INDEX_MESSAGE, R.drawable.tv_statu_message);
                }
                mAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
                FlyLog.e(TAG + " error:" + e.toString());
            }
        } else {
            FlyLog.e(TAG + " message service is null");
        }
    }

}
