package com.flyzebra.launcher.ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyzebra.launcher.R;
import com.flyzebra.launcher.adapter.GridAdapter;
import com.flyzebra.launcher.base.BaseActivity;
import com.flyzebra.launcher.receiver.UserIdReceiver;
import com.flyzebra.launcher.utils.FlyLog;
import com.flyzebra.launcher.utils.SystemPropertiesProxy;
import com.flyzebra.ppfunstv.constant.Constants;
import com.flyzebra.ppfunstv.receiver.CaReceiver;
import com.flyzebra.ppfunstv.utils.Utils;
import com.flyzebra.ppfunstv.view.TvView.CellView.CellClickAction.CommondTool;

import java.util.ArrayList;

/**
 * Created by lizongyuan on 2016/10/18.
 * E-mail:lizy@ppfuns.com
 */

public class MyInfoActivity extends BaseActivity implements CaReceiver.EventListener,UserIdReceiver.EventListener{

    private static final String LAUNCHER_ACTION_PREF = "com.flyzebra.launcher";
    private CaReceiver caReceiver;
    private UserIdReceiver userIdReceiver;
    private Context mContext;
    private ImageView ivPic;
    private TextView tvSmartId;
    private TextView tvUserId;
    private GridView mGridView;
    private GridAdapter mAdapter;
    private ArrayList<Integer> mImgs = new ArrayList<>();
    private ArrayList<String> mInfos = new ArrayList<>();

    public MyInfoActivity() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myinfo);
        mContext = this;
        caReceiver = new CaReceiver(this);
        caReceiver.register(this);
        userIdReceiver = new UserIdReceiver(this);
        userIdReceiver.register(this);
        init();
    }

    private void init(){
        ivPic = (ImageView) findViewById(R.id.my_pic);
        tvUserId = (TextView) findViewById(R.id.my_user_id);
        tvSmartId = (TextView) findViewById(R.id.my_smart_card_id);
        mGridView = (GridView) findViewById(R.id.my_gridview);

        TypedArray ar = getResources().obtainTypedArray(R.array.myInfoImages);
        int len = ar.length();
        for (int i = 0; i < len; i++){
            mImgs.add(ar.getResourceId(i, 0));
        }
        ar.recycle();

        String[] nameList = getResources().getStringArray(R.array.myInfoNames);
        mInfos.clear();
        for(String name:nameList){
            mInfos.add(name);
        }
        mGridView.setNumColumns(mInfos.size());
        final String actionList[] = getResources().getStringArray(R.array.myInfoActions);
        mAdapter = new GridAdapter(this);
        mAdapter.setData(mImgs,mInfos);
        mAdapter.setResId(R.layout.my_info_grid_item);
        mAdapter.setItemClick(new GridAdapter.OnItemClick(){
            @Override
            public void onItemClick(int pos) {
                if(pos < 0||pos>=actionList.length) {
                    FlyLog.d("Array Index Out Of Bounds pos=%d",pos);
                    return;
                }
                String[] infos = actionList[pos].split(Constants.line);
                String action = infos[0];
                String data = infos.length > 1 ?infos[1]:null;
                if(actionList[pos].startsWith(LAUNCHER_ACTION_PREF)){
                    startActivity(new Intent(action));
                }else{
                    CommondTool.execStartActivityAndShowTip(mContext,action,data,getString(R.string.app_not_install),false);
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

    /**
     * 更新用户ID信息
     */
    private void updateUserId(){
        String userId = SystemPropertiesProxy.get(mContext,Constants.Property.USER_ID,null);
        tvUserId.setText(String.format(getString(R.string.my_user_id),userId));
    }


    /**
     * 更新智能卡号信息
     */
    private void updateSmardCardId(){
        tvSmartId.setText(String.format(getString(R.string.my_smart_card_id), Utils.getCaId(mContext,null)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateSmardCardId();
        updateUserId();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        FlyLog.d(" dispatchKeyEvent: action "+ event.getAction()+" keycode:"+event.getKeyCode());
        if(KeyEvent.ACTION_DOWN == event.getAction()){
            switch (event.getKeyCode()){
                case KeyEvent.KEYCODE_ENTER:
                case KeyEvent.KEYCODE_DPAD_CENTER:{
                    mAdapter.getItemClick().onItemClick(mGridView.getSelectedItemPosition());
                    break;
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        caReceiver.unRegister();
        userIdReceiver.unRegister();
        super.onDestroy();
    }

    @Override
    public void cardIn() {
        updateSmardCardId();
    }

    @Override
    public void cardOut() {
        tvSmartId.setText(String.format(getString(R.string.my_smart_card_id),""));
    }

    @Override
    public void userIdChange() {
        updateUserId();
    }
}
