package com.ppfuns.ppfunstv.view;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ppfuns.messageservice.IMessageService;
import com.ppfuns.ppfunstv.R;
import com.ppfuns.ppfunstv.receiver.MessageReceiver;
import com.ppfuns.ppfunstv.utils.FlyLog;
import com.ppfuns.ppfunstv.utils.ServiceUtils;

/**
 * Created by 李宗源 on 2016/8/31.
 * E-mail:lizy@ppfuns.com
 */
public class MessageView extends ImageView implements MessageReceiver.EventListener{


    public static final String MESSAGE_PACKAGE_NAME = "com.ppfuns.com.ppfuns.messageservice";
    public static final String MESSAGE_ACTIVITY_NAME = "com.ppfuns.com.ppfuns.messageservice.service.MessageService";
    private static final int HEADER_STATE = 1;
    private IMessageService mMessageService;
    private MessageReceiver mMessageReceiver;
    private int mState = 1;//0 for setActivity; 1 for headerLayout
    private Context mContext;
    /**
     * 文字
     */
    private int textNum = 0;

    public MessageView(Context context) {
        this(context,null);
    }

    public MessageView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MessageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    public void setState(int state){
        mState = state;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ServiceUtils.bindServiceCls( mContext, MESSAGE_PACKAGE_NAME, MESSAGE_ACTIVITY_NAME, mMessageConnection);

        mMessageReceiver = new MessageReceiver(mContext);
        mMessageReceiver.register(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        mMessageReceiver.unRegister();
        mContext.unbindService(mMessageConnection);
        super.onDetachedFromWindow();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(mMessageService != null){
            try {
                textNum = mMessageService.getUnreadMessageNum();
                FlyLog.e(" message service num:"+textNum);
            } catch (RemoteException e) {
                e.printStackTrace();
                FlyLog.e(" error:"+e.toString());
            }
        }else{
            FlyLog.e(" message service is null");
        }
        if (textNum > 0) {
            Paint paint = new Paint();
            paint.setColor(Color.RED);
            paint.setStrokeWidth(2);
            paint.setStyle(Paint.Style.STROKE);
//            paint.setTextSize(textSize);
//            canvas.drawText(textString, canvas.getWidth() - ( textSize * textString.length() ) ,canvas.getHeight() - 50, paint);
            canvas.drawText(textNum+"",10,10,paint);
        }
    }

    private ServiceConnection mMessageConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMessageService = IMessageService.Stub.asInterface(service);
            updateView();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            ServiceUtils.bindServiceCls( mContext, MESSAGE_PACKAGE_NAME, MESSAGE_ACTIVITY_NAME, mMessageConnection);
        }
    };

    @Override
    public void messageChanged() {
        updateView();
    }

    private void updateView(){
        if(mMessageService != null){
            try {
                textNum = mMessageService.getUnreadMessageNum();
                FlyLog.i("message change,service num:"+textNum);
                int imgId = R.drawable.tv_statu_message_new;
                if(textNum != 0){
                    if(HEADER_STATE == mState){
                        imgId = R.drawable.tv_message_new;
                    }
                }else{
                    imgId = R.drawable.tv_statu_message;
                    if(HEADER_STATE == mState){
                        imgId = R.drawable.tv_header_message;
                    }
                }
                Glide.with(mContext).load(imgId).into(this);
            } catch (RemoteException e) {
                e.printStackTrace();
                FlyLog.e("error:"+e.toString());
                ServiceUtils.bindServiceCls(mContext, MESSAGE_PACKAGE_NAME, MESSAGE_ACTIVITY_NAME, mMessageConnection);
            }
        }else{
            FlyLog.e("message service is null");
            ServiceUtils.bindServiceCls( mContext, MESSAGE_PACKAGE_NAME, MESSAGE_ACTIVITY_NAME, mMessageConnection);
        }
    }
}
