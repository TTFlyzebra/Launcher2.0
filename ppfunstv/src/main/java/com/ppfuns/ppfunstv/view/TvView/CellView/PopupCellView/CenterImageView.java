package com.ppfuns.ppfunstv.view.TvView.CellView.PopupCellView;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.ppfuns.ppfunstv.data.CellEntity;
import com.ppfuns.ppfunstv.utils.DisplayUtils;
import com.ppfuns.ppfunstv.utils.FlyLog;
import com.ppfuns.ppfunstv.view.TvView.FocusAnimat.IAnimatView;
import com.ppfuns.ppfunstv.view.TvView.FocusAnimat.ITvFocusAnimat;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by FlyZebra on 2016/8/12.
 */
public class CenterImageView extends ImageView implements IAnimatView {
    private Context context;
    private Paint text_paint1;
    private Paint back_paint1;
    private Paint bitmap_paint1;
    private int textSize = 40;
    private String text = "聚视互娱";

    private static final int MOVE_LEFT = 0;
    private static final int MOVE_RIGHT = 1;
    private static final int ZOOM_IN = 2;
    private static final int ZOOM_OUT = 3;
    private int palyTypeArr[] = new int[]{MOVE_LEFT, MOVE_RIGHT};
    private int CountPlayType = 0;
    private Bitmap roleBitmap;
    private Bitmap bkAnimatBitmap;
    private Bitmap moveBkAnimatBitmap;
    private float bkAnimatPlayScale = 0.0f;
    private static final int ZOOM = 1;
    private static final int MOVE = 2;
    private int bkAnimeType = MOVE;
    Rect srcRect = new Rect();
    Rect dstRect = new Rect();


    private static ExecutorService executors = Executors.newFixedThreadPool(1);
    private CellEntity mCell;
    private Context mContext;

    private float screenScale = 1.0f;

    private int roundPix = 12;

    public CellEntity getmCell() {
        return mCell;
    }

    public void setmCell(CellEntity mCell) {
        this.mCell = mCell;
    }

    public CenterImageView(Context context) {
        this(context, null);
    }

    public CenterImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CenterImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        mContext = context;
//        setAlpha(1.0f);
    }

    private void init(Context context) {
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.context = context;
        text_paint1 = new Paint();
        text_paint1.setColor(Color.parseColor("#FFFFFF"));
        text_paint1.setAntiAlias(true);
        text_paint1.setTextSize(textSize);
        text_paint1.setStyle(Paint.Style.FILL);
        text_paint1.setTextAlign(Paint.Align.CENTER);
        text_paint1.setFakeBoldText(true);

        back_paint1 = new Paint();
        back_paint1.setColor(0xff000000);
        back_paint1.setAntiAlias(true);
        back_paint1.setStyle(Paint.Style.FILL);
        back_paint1.setAlpha(20);

        bitmap_paint1 = new Paint();
        bitmap_paint1.setColor(0xff000000);
        bitmap_paint1.setAntiAlias(true);
        bitmap_paint1.setStyle(Paint.Style.FILL);

        screenScale = DisplayUtils.getMetrices((Activity) context).widthPixels / 1920f;
    }

    public void setRoleBitmap(String url, final int width, final int heigth) {
        Glide.with(context)
                .load(url)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        Bitmap bitmap = Bitmap.createScaledBitmap(resource, width, heigth, true);
                        roleBitmap = bitmap;
                        postInvalidate();
                    }
                });
    }

    public void setBackAnimatBitmap(String url) {
        //TODO 以下为硬编码，如后台实现相应用更换需修改
        if (mCell.getText().contains("动漫教育")) {
            url = "file:///android_asset/testimg/bk1.png";
            bkAnimeType = MOVE;
        } else if (mCell.getText().contains("游戏中心")) {
            url = "file:///android_asset/testimg/bk2.png";
            bkAnimeType = MOVE;
        } else if (mCell.getText().contains("应用商店")) {
            url = "file:///android_asset/testimg/bk2.png";
            bkAnimeType = MOVE;
        } else if (mCell.getText().contains("电视购物")) {
            url = "file:///android_asset/testimg/bk1.png";
            bkAnimeType = MOVE;
        } else if (mCell.getText().contains("音乐中心")) {
            url = "file:///android_asset/testimg/bk1.png";
            bkAnimeType = MOVE;
        }

        Glide.with(context)
                .load(url)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .override(mCell.getWidth(), mCell.getHeight())
//                .transform(new GlideRoundTransform(context, (int) (roundPix*screenScale)))
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        FlyLog.d("y:" + getY() + " height:" + getHeight() + " bitHight:" + resource.getHeight());
                        bkAnimatBitmap = resource;
                        srcRect = new Rect(0, 0, bkAnimatBitmap.getWidth(), bkAnimatBitmap.getHeight());
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    moveBkAnimatBitmap = Bitmap.createBitmap(bkAnimatBitmap, 0, 0, mCell.getWidth(), mCell.getHeight());
                                    moveBkAnimatBitmap = GetRoundedCornerBitmap(moveBkAnimatBitmap, roundPix * screenScale);
                                } catch (Exception e) {
                                    FlyLog.e(e.toString());
                                    moveBkAnimatBitmap = bkAnimatBitmap;
                                    e.printStackTrace();
                                }
                                postInvalidate();
                            }
                        }).start();
                    }
                });

    }


    public void setTextSize(int textSize) {
        this.textSize = textSize;
        text_paint1.setTextSize(textSize);
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //给制默认背景
        //绘制动态背景
        //截获空指针异常
        try {
            if (bkAnimatBitmap != null) {
                switch (bkAnimeType) {
                    case ZOOM:
                        dstRect.left = (int) (0 - getWidth() * (bkAnimatPlayScale + 1.5f) * 0.3f);
                        dstRect.top = (int) (0 - getWidth() * (bkAnimatPlayScale + 1.5f) * 0.3f);
                        dstRect.right = (int) (getWidth() + getWidth() * (bkAnimatPlayScale + 1.5f) * 0.3f);
                        dstRect.bottom = (int) (getWidth() + getWidth() * (bkAnimatPlayScale + 1.5f) * 0.3f);
                        canvas.drawBitmap(bkAnimatBitmap, srcRect, dstRect, bitmap_paint1);
                        break;
                    case MOVE:
                        if (moveBkAnimatBitmap != null) {
                            canvas.drawBitmap(moveBkAnimatBitmap, 0, 0, bitmap_paint1);
                        } else {
                            int movex = (int) (-(bkAnimatBitmap.getWidth() - getWidth()) * bkAnimatPlayScale * 0.4f);
                            canvas.drawBitmap(bkAnimatBitmap, movex, 0, bitmap_paint1);
                        }
                        break;
                }
            }
//
            //绘制中间人物
            if (roleBitmap != null) {
                canvas.drawBitmap(roleBitmap, 0, 0, bitmap_paint1);
            }

            //绘制文字
//        canvas.drawRect(0, getHeight() - textSize - 16, getWidth(), getHeight() - 8, back_paint1);
//            canvas.drawText(text, getWidth() / 2, getHeight() - textSize / 2, text_paint1);
        } catch (Exception e) {
            e.printStackTrace();
            FlyLog.d(e.toString());
        }
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }

    public void setCellData(CellEntity cellEntity) {
        this.mCell = cellEntity;
    }


    public void playBackAnimat(int mDuration) {
        executors.execute(new BackGroundAnimTask(mDuration));
    }

    @Override
    public void setFocusAnimate(ITvFocusAnimat iTvFocusAnimat) {

    }

    private Rect mRect = new Rect();

    @Override
    public Rect getFocusRect() {
        if (getParent() instanceof PopupAnimatView) {
            PopupAnimatView view = (PopupAnimatView) getParent();
            mRect.left = view.getFinalX();
            mRect.right = view.getFinalX() + view.getFinalW();
            mRect.top = view.getFinalY();
            mRect.bottom = view.getFinalY() + view.getFinalH();
        }
        return mRect;
    }

    @Override
    public Rect getOldRect() {
        return mRect;
    }

    @Override
    public int getFocusZorder() {
        return 0;
    }

    @Override
    public int getFocusScale() {
        return 1;
    }

    @Override
    public int getFocusType() {
        return 0;
    }

    @Override
    public View getReflectImageView() {
        return null;
    }

    private class BackGroundAnimTask implements Runnable {
        private int mDuration;
        private int ONCE_TIME = 25;

        public BackGroundAnimTask(int mDuration) {
            this.mDuration = mDuration * 2;
        }

        @Override
        public void run() {
            if (bkAnimatBitmap == null) {
                return;
            }
            moveBkAnimatBitmap = null;
            int count = mDuration / ONCE_TIME;
            if (bkAnimatPlayScale < 0.5f) {
                bkAnimatPlayScale = 0;
                for (int i = 0; i < count; i++) {
                    bkAnimatPlayScale = i / (float) count;
                    postInvalidate();
                    try {
                        Thread.sleep(ONCE_TIME);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                bkAnimatPlayScale = 1.0f;
                for (int i = count - 1; i >= 0; i--) {
                    bkAnimatPlayScale = i / (float) count;
                    postInvalidate();
                    try {
                        Thread.sleep(ONCE_TIME);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            int movex = (int) ((bkAnimatBitmap.getWidth() - getWidth()) * bkAnimatPlayScale * 0.4f);
            moveBkAnimatBitmap = Bitmap.createBitmap(bkAnimatBitmap, movex, 0, getWidth(), getHeight());
            moveBkAnimatBitmap = GetRoundedCornerBitmap(moveBkAnimatBitmap, roundPix * screenScale);
            postInvalidate();
        }
    }


    /**
     * 生成圆角图片
     *
     * @param bitmap
     * @param roundPx 圆角弧度（像素值）
     * @return
     */
    public static Bitmap GetRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
        try {
            Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            final RectF rectF = new RectF(new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()));
            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(Color.BLACK);
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            final Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            canvas.drawBitmap(bitmap, src, rect, paint);
            return output;
        } catch (Exception e) {
            return bitmap;
        }
    }


}
