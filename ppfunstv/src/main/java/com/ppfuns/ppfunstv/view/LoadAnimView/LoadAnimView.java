package com.ppfuns.ppfunstv.view.LoadAnimView;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ppfuns.ppfunstv.utils.SystemUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by flyzebra on 16-12-9.
 */
public class LoadAnimView extends RelativeLayout {
    private Context mContext = null;
    private boolean isRun = true;

    //最长等待时间
    private int MIN_WAIT_TIME = 3;
    private int mWaitTime = 30;
    private long COUNT_CPU_SLEEP = 1000;
    private int count = 0;
    private float cpuUsed = 2f;
    private long memoryAdd = 1024;

    private Handler mHandler = new Handler(Looper.myLooper());
    //开机放大动画控件部分
    private long mAnimTime = 3000;
    private float mAnimScale = 1.0f;

    private static ExecutorService executors = Executors.newFixedThreadPool(1);
    private ImageView imageView;


    public LoadAnimView(Context context) {
        this(context, null);
    }

    public LoadAnimView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadAnimView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        bringToFront();
    }


    public void loadImageView(int resID) {
        imageView = new ImageView(mContext);
        addView(imageView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        imageView.setImageResource(resID);
    }

    private void init(Context context) {
        this.mContext = context;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        isRun = true;
    }


    public void checkCpuLoad(){
        animate().setDuration(mAnimTime).scaleX(mAnimScale).scaleY(mAnimScale).start();
        executors.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(MIN_WAIT_TIME * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                while (isRun && count < mWaitTime) {
                    long useMemory1 = Runtime.getRuntime().totalMemory();
                    count++;
                    long useMemory2 = Runtime.getRuntime().totalMemory();
                    long startTime = SystemClock.elapsedRealtime();
                    float n = SystemUtils.getProcessCpuRate(COUNT_CPU_SLEEP);
                    long endTime = SystemClock.elapsedRealtime();
                    if((endTime-startTime)>COUNT_CPU_SLEEP+100) {
                        continue;
                    }
                    if ((n < cpuUsed )|| count >= mWaitTime) {
                        count = 0;
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                setVisibility(GONE);
                                if (mEvent != null) {
                                    mEvent.dismiss();
                                }
                            }
                        });
                        break;
                    }
                }
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        isRun = false;
        super.onDetachedFromWindow();
    }


    public interface LoadEvent {
        void dismiss();

        void showing();
    }

    private LoadEvent mEvent = null;

    public void addEvent(LoadEvent event) {
        mEvent = event;
    }

    public void setWaitTime(int mWaitTime) {
        this.mWaitTime = mWaitTime;
    }
}
