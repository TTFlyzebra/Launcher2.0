package com.ppfuns.ppfunstv.view.TvView.CellView.CellAnim;

/**
 * Created by pc1 on 2016/7/14.
 */

import android.graphics.Camera;
import android.graphics.Matrix;
import android.os.Build;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * 自定义3D播放动画类
 */
public class Rotate3dAnimation extends Animation {
    private float mFromDegrees;
    private float mToDegrees;
    private float mCenterX;
    private float mCenterY;
    private float mDepthZ;
    private Camera mCamera;
    private View mView;
    private int mNums;
    private float currentDegress;
    private float new_x = 0;
    private float new_z = 0;

    public Rotate3dAnimation(View view, float fromDegrees, float toDegrees, int nums) {
        mFromDegrees = fromDegrees;
        mToDegrees = toDegrees;
        mCenterX = view.getWidth() / 2;
        mCenterY = view.getHeight() / 2;
        mDepthZ = (float) (mCenterX / (Math.tan(((180 / nums) / 180f) * Math.PI)));
        mView = view;
        mNums = nums;
    }

    public Rotate3dAnimation() {
    }

    public void Rotate3dAnimation(View view, float fromDegrees, float toDegrees, int nums) {
        mFromDegrees = fromDegrees;
        mToDegrees = toDegrees;
        mCenterX = view.getWidth() / 2;
        mCenterY = view.getHeight() / 2;
        mDepthZ = (float) (mCenterX / (Math.tan(((180 / nums) / 180f) * Math.PI)));
        mView = view;
        mNums = nums;
    }

    public void init(View view, float fromDegrees, float toDegrees, int nums) {
        mFromDegrees = fromDegrees;
        mToDegrees = toDegrees;
        mCenterX = view.getWidth() / 2;
        mCenterY = view.getHeight() / 2;
        mDepthZ = (float) (mCenterX / (Math.tan(((180 / nums) / 180f) * Math.PI)));
        mView = view;
        mNums = nums;
    }

    public void init(View view, float toDegrees, int nums) {
        mFromDegrees = currentDegress;
        mToDegrees = toDegrees;
        mCenterX = view.getWidth() / 2;
        mCenterY = view.getHeight() / 2;
        mDepthZ = (float) (mCenterX / (Math.tan(((180 / nums) / 180f) * Math.PI)));
        mView = view;
        mNums = nums;
    }


    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
        mCamera = new Camera();
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        currentDegress = mFromDegrees + ((mToDegrees - mFromDegrees) * (interpolatedTime));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            float Zorder = (currentDegress + 360) % 360;
            Zorder = Zorder > 180 ? 180 - Zorder % 180 : Zorder;
            mView.setTranslationZ(180f - Zorder);
        } else {
            float mDegrees = currentDegress % 360;
            float frontDegrees = 360f / mNums;
            if (mToDegrees % 360 == 0) {
                if (mDegrees >= 360 - frontDegrees) {
//                    mView.bringToFront();
                } else if (mDegrees < frontDegrees) {
                    mView.bringToFront();
                }
            }
        }
        final float centerX = mCenterX;
        final float centerY = mCenterY;
        final Camera camera = mCamera;
        final Matrix matrix = t.getMatrix();
        camera.save();


        new_x = (float) Math.sin(Math.PI * currentDegress / 180) * mDepthZ;
        new_z = (float) Math.cos(Math.PI * currentDegress / 180) * mDepthZ;

        float degress = (currentDegress + 360) % 360;
        float move_x = (float) (Math.cos(Math.PI * degress / 180) * centerX);
        if (degress <= 90) {
            camera.translate(new_x - move_x + mCenterX, 0.0f, mDepthZ - new_z);
        } else if (degress <= 180) {
            camera.translate(new_x + move_x + mCenterX, 0.0f, mDepthZ - new_z);
        } else if (degress <= 270) {
            camera.translate(new_x - move_x - mCenterX, 0.0f, mDepthZ - new_z);
        } else if (degress <= 360) {
            camera.translate(new_x + move_x - mCenterX, 0.0f, mDepthZ - new_z);
        }

        camera.rotateY(currentDegress);

        camera.getMatrix(matrix);
        camera.restore();

        matrix.preTranslate(-centerX, -centerY);
        matrix.postTranslate(centerX, centerY);
    }
}
