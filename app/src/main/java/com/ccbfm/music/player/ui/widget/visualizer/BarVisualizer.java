package com.ccbfm.music.player.ui.widget.visualizer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import java.util.Random;

public class BarVisualizer extends BaseVisualizer {
    private static final int BAR_MAX_POINTS = 120;
    private static final int BAR_MIN_POINTS = 3;

    private int mMaxBatchCount;
    private int mPoints;
    private float[] mSrcY, mDestY;
    private float mBarWidth;
    private Rect mClipBounds;
    private int mBatchCount;
    private Random mRandom;

    public BarVisualizer(Context context) {
        super(context);
    }

    public BarVisualizer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BarVisualizer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BarVisualizer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void init() {
        mPoints = (int) (BAR_MAX_POINTS * mDensity);
        if (mPoints < BAR_MIN_POINTS) {
            mPoints = BAR_MIN_POINTS;
        }

        mBarWidth = -1;
        mBatchCount = 0;

        setAnimSpeed(mAnimSpeed);

        mRandom = new Random();

        mClipBounds = new Rect();

        mSrcY = new float[mPoints];
        mDestY = new float[mPoints];
    }

    @Override
    public void setAnimSpeed(int animSpeed) {
        super.setAnimSpeed(animSpeed);
        mMaxBatchCount = MAX_ANIM_BATCH_COUNT - mAnimSpeed;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = mWidth;
        int height = mHeight;
        if (mBarWidth == -1) {
            canvas.getClipBounds(mClipBounds);
            mBarWidth = (float) width / mPoints;
            //initialize points
            for (int i = 0; i < mSrcY.length; i++) {
                float posY;
                if (mGravity == Gravity.TOP) {
                    posY = mClipBounds.top;
                } else {
                    posY = mClipBounds.bottom;
                }

                mSrcY[i] = posY;
                mDestY[i] = posY;
            }
        }

        //create the path and draw
        if (isVisualizationEnabled && mRawAudioBytes != null) {

            if (mRawAudioBytes.length == 0) {
                return;
            }

            //find the destination bezier point for a batch
            if (mBatchCount == 0) {
                float randPosY = mDestY[mRandom.nextInt(mPoints)];
                for (int i = 0; i < mSrcY.length; i++) {

                    int x = (int) Math.ceil((i + 1) * ((float)mRawAudioBytes.length / mPoints));
                    int t = 0;
                    if (x < 1024)
                        t = height +
                                ((byte) (Math.abs(mRawAudioBytes[x]) + 128)) * height / 128;

                    float posY;
                    if (mGravity == Gravity.TOP) {
                        posY = mClipBounds.bottom - t;
                    } else {
                        posY = mClipBounds.top + t;
                    }
                    //change the source and destination y
                    mSrcY[i] = mDestY[i];
                    mDestY[i] = posY;
                }

                mDestY[mSrcY.length - 1] = randPosY;
            }

            //increment batch count
            mBatchCount++;

            //calculate bar position and draw
            for (int i = 0; i < mSrcY.length; i++) {
                float barY = mSrcY[i] + (((float) (mBatchCount) / mMaxBatchCount) * (mDestY[i] - mSrcY[i]));
                float barX = (i * mBarWidth) + (mBarWidth / 2);
                canvas.drawLine(barX, height, barX, barY, mPaint);
            }

            //reset the batch count
            if (mBatchCount == mMaxBatchCount)
                mBatchCount = 0;

        }

    }
}
