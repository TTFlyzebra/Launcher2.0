package com.flyzebra.launcher.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.flyzebra.launcher.R;
import com.flyzebra.launcher.base.BaseActivity;
import com.flyzebra.launcher.utils.FlyLog;
import com.flyzebra.launcher.utils.SPUtil;
import com.flyzebra.ppfunstv.utils.ToastUtils;

/**
 * Created by lizongyuan on 2016/10/19.
 * E-mail:lizy@ppfuns.com
 * 设置时段主题界面
 */

public class TimeThemeActivity extends BaseActivity implements View.OnClickListener,View.OnFocusChangeListener{

    private static final String TIME_DEFAULT = "00";
    private static final String TIME_HALF_HOUR = "30";
    private static final int DIRECTION_UP = 0;
    private static final int DIRECTION_DOWN = 1;
    private static final int BASE_HOUR = 24;
    private Context mContext;
    private EditText etChildContinue;
    private EditText etChildHour;
    private EditText etChildMin;
    private TextView tvChildTime;
    private TextView tvPublicTime;
    private View vContinueUp;
    private View vContinueDown;
    private View vHourUp;
    private View vHourDown;
    private View vMinUp;
    private View vMinDown;

    private String childTimeContinue;//儿童版持续时间
    private String childTimeHour;//儿童版开始时间中的小时部分
    private String childTimeMin;//儿童版开始时间中的分钟部分
    private String childTimeHourEnd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_theme);
        mContext = this;
        initView();
        hideSoftInput();
        initListener();
    }


    @Override
    protected void onResume() {
        super.onResume();
        initData();
        updateTimeTip();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.child_time_continue:
            case R.id.child_time_hour:
            case R.id.child_time_min:
                //屏蔽软键盘
                break;
            case R.id.child_time_continue_down:
                updateTimeText(1,etChildContinue);
                break;
            case R.id.child_time_hour_down:
                updateTimeText(1,etChildHour);
                break;
            case R.id.child_time_continue_up:
                updateTimeText(0,etChildContinue);
                break;
            case R.id.child_time_hour_up:
                updateTimeText(0,etChildHour);
                break;
            case R.id.child_time_min_up:
            case R.id.child_time_min_down:
                updateMinText();
                break;
            default:
                break;
        }
    }

    private void updateMinText(){
        String minText = etChildMin.getText().toString();
        if(TIME_DEFAULT.equals(minText)){
            etChildMin.setText(TIME_HALF_HOUR);
        }else{
            etChildMin.setText(TIME_DEFAULT);
        }
    }

    /**
     * @param direction 0:up
     *                  1:down
     * @param view
     */
    private void updateTimeText(int direction,TextView view){
        try {
            String text;
            if(TextUtils.isEmpty(view.getText())){
                text = (String) view.getTag();
            }else{
                text = view.getText().toString();
            }
            int num = Integer.parseInt(text);
            if(DIRECTION_UP == direction){
                num += 1;
            }else{
                num -= 1;
            }
            if(view == etChildContinue){
                num += 25;
                num %= 25;
            }else if(view == etChildHour){
                num += 24;
                num %= 24;
            }
            view.setText(num+"");
        }catch (Exception e){
            FlyLog.e(e.toString());
        }

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        View curView =  getCurrentFocus();
        if(KeyEvent.ACTION_DOWN == event.getAction()){
            switch (event.getKeyCode()){
                case KeyEvent.KEYCODE_DPAD_UP:
                    if(curView == vContinueUp
                            || curView == vHourUp
                            || curView == vMinUp){
                        onClick(curView);
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    if(curView == vContinueDown
                            || curView == vHourDown
                            || curView == vMinDown){
                        onClick(curView);
                    }
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private void initView(){
        etChildContinue = (EditText) findViewById(R.id.child_time_continue);
        etChildHour = (EditText) findViewById(R.id.child_time_hour);
        etChildMin = (EditText) findViewById(R.id.child_time_min);
        tvChildTime = (TextView) findViewById(R.id.tv_child_time);
        tvPublicTime = (TextView) findViewById(R.id.tv_public_time);
        vContinueUp = findViewById(R.id.child_time_continue_up);
        vContinueDown = findViewById(R.id.child_time_continue_down);
        vHourUp = findViewById(R.id.child_time_hour_up);
        vHourDown = findViewById(R.id.child_time_hour_down);
        vMinUp = findViewById(R.id.child_time_min_up);
        vMinDown = findViewById(R.id.child_time_min_down);
    }

    private void initListener(){
        //文本变化事件
        etChildContinue.addTextChangedListener(new MyTextWatcher(etChildContinue));
        etChildHour.addTextChangedListener(new MyTextWatcher(etChildHour));
        etChildMin.addTextChangedListener(new MyTextWatcher(etChildMin));
        //焦点变化事件
        etChildContinue.setOnFocusChangeListener(this);
        etChildHour.setOnFocusChangeListener(this);
        etChildMin.setOnFocusChangeListener(this);
        //点击事件
        etChildContinue.setOnClickListener(this);
        etChildHour.setOnClickListener(this);
        etChildMin.setOnClickListener(this);
        vContinueUp.setOnClickListener(this);
        vContinueDown.setOnClickListener(this);
        vHourUp.setOnClickListener(this);
        vHourDown.setOnClickListener(this);
        vMinUp.setOnClickListener(this);
        vMinDown.setOnClickListener(this);

    }
    private void initData(){
        childTimeContinue = (String) SPUtil.get(mContext,SPUtil.FILE_TIME_THEME,SPUtil.CHILD_TIME_CONTINUE,TIME_DEFAULT);
        childTimeHour = (String) SPUtil.get(mContext,SPUtil.FILE_TIME_THEME,SPUtil.CHILD_TIME_HOUR_START,TIME_DEFAULT);
        childTimeMin = (String) SPUtil.get(mContext,SPUtil.FILE_TIME_THEME,SPUtil.CHILD_TIME_MIN,TIME_DEFAULT);
        childTimeHourEnd = (String) SPUtil.get(mContext,SPUtil.FILE_TIME_THEME,SPUtil.CHILD_TIME_HOUR_END,TIME_DEFAULT);
        setHint(etChildContinue,childTimeContinue);
        setHint(etChildHour,childTimeHour);
        setHint(etChildMin,childTimeMin);
        SPUtil.set(mContext,SPUtil.FILE_TIME_THEME,SPUtil.CHILD_TIME_CONTINUE,childTimeContinue);
        SPUtil.set(mContext,SPUtil.FILE_TIME_THEME,SPUtil.CHILD_TIME_HOUR_START,childTimeHour);
        SPUtil.set(mContext,SPUtil.FILE_TIME_THEME,SPUtil.CHILD_TIME_MIN,childTimeMin);
        SPUtil.set(mContext,SPUtil.FILE_TIME_THEME,SPUtil.CHILD_TIME_HOUR_END,childTimeHourEnd);
        FlyLog.d("init childTimeContinue=%s,childTimehour=%s,childTimeMin=%s",childTimeContinue,childTimeHour,childTimeMin);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(hasFocus){
            FlyLog.d("onFocusChange childTimeContinue=%s,childTimehour=%s,childTimeMin=%s",childTimeContinue,childTimeHour,childTimeMin);
            switch (v.getId()){
                case R.id.child_time_continue:{
                    setHint(v,childTimeContinue);
                    break;
                }
                case R.id.child_time_hour:{
                    setHint(v,childTimeHour);
                    break;
                }
                case R.id.child_time_min:{
                    setHint(v,childTimeMin);
                    break;
                }
            }
        }
    }

    private void setHint(View v,String text){
        if(v instanceof TextView){
            TextView textText = (EditText)v;
            textText.setText("");
            textText.setHint(text);
            textText.setTag(text);
        }
    }

    public void updateTimeTip() {
        String curDay = getString(R.string.cur_day);
        String nextDay = getString(R.string.next_day);
        String childStartTime = childTimeHour + ":" + childTimeMin;
        String childEndTime = childTimeHourEnd + ":" + childTimeMin;
        if (childEndTime.compareTo(childStartTime) < 0 || "24".equals(childTimeContinue)) {
            tvChildTime.setText(String.format(getString(R.string.time_theme_time), curDay, childStartTime, nextDay, childEndTime));
            tvPublicTime.setText(String.format(getString(R.string.time_theme_time), curDay, childEndTime, curDay, childStartTime));
        } else {
            tvChildTime.setText(String.format(getString(R.string.time_theme_time), curDay, childStartTime, curDay, childEndTime));
            tvPublicTime.setText(String.format(getString(R.string.time_theme_time), curDay, childEndTime, nextDay, childStartTime));
        }
    }

    private String updateEndHour() {
        try {
            int end = Integer.parseInt(childTimeHour) + Integer.parseInt(childTimeContinue);
            end %= 24;
            return int2timeString(end);
        } catch (Exception e) {
            FlyLog.e(e.toString());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param num
     * @return
     */
    private String int2timeString(int num) {
        String time = num + "";
        if (num < 10) {
            time = "0" + num;
        }
        return time;
    }

    private void hideSoftInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etChildHour.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(etChildMin.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(etChildContinue.getWindowToken(), 0);
    }

    class MyTextWatcher implements TextWatcher{
        private EditText editText;
        public MyTextWatcher(EditText edit){
            editText = edit;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            FlyLog.d("afterTextChanged:"+s);
            FlyLog.d("afterTextChanged enter childTimeContinue=%s,childTimehour=%s,childTimeMin=%s",childTimeContinue,childTimeHour,childTimeMin);
            int num = 0;
            String data = "00";
            try {
                if(TextUtils.isEmpty(s.toString())) {
                    return;
                }else{
                    num = Integer.parseInt(s.toString());
                }
                if(num<10){
                    data = "0"+num;
                }else{
                    data = ""+num;
                }
            }catch (Exception e){

            }
            switch (editText.getId()){
                case R.id.child_time_continue:
                    if(BASE_HOUR < num){
                        setHint(editText,TIME_DEFAULT);
                        ToastUtils.showMessage(mContext,getString(R.string.data_err_0_24));
                    }else{
                        childTimeContinue = data;
                        SPUtil.set(mContext,SPUtil.FILE_TIME_THEME,SPUtil.CHILD_TIME_CONTINUE, data);
                        childTimeHourEnd = updateEndHour();
                        SPUtil.set(mContext,SPUtil.FILE_TIME_THEME,SPUtil.CHILD_TIME_HOUR_END,childTimeHourEnd);
                    }
                    break;
                case R.id.child_time_hour:
                    if(BASE_HOUR < num){
                        setHint(editText,TIME_DEFAULT);
                        ToastUtils.showMessage(mContext,getString(R.string.data_err_0_24));
                    }else{
                        childTimeHour = data;
                        SPUtil.set(mContext,SPUtil.FILE_TIME_THEME,SPUtil.CHILD_TIME_HOUR_START, data);
                        childTimeHourEnd = updateEndHour();
                        SPUtil.set(mContext,SPUtil.FILE_TIME_THEME,SPUtil.CHILD_TIME_HOUR_END,childTimeHourEnd);
                    }
                    break;
                case R.id.child_time_min:
                    if(s != null){
                        childTimeMin = data;
                        SPUtil.set(mContext,SPUtil.FILE_TIME_THEME,SPUtil.CHILD_TIME_MIN, data);
                    }
                    break;
                default:
                    break;
            }
            updateTimeTip();
            FlyLog.d("afterTextChanged after childTimeContinue=%s,childTimehour=%s,childTimeMin=%s",childTimeContinue,childTimeHour,childTimeMin);
        }
    }
}
