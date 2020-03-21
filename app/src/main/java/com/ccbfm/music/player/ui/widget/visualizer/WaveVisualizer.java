package com.ccbfm.music.player.ui.widget.visualizer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import java.util.Random;

public class WaveVisualizer extends BaseVisualizer {
    private static final int WAVE_MAX_POINTS = 54;
    private static final int WAVE_MIN_POINTS = 3;

    private int mMaxBatchCount;
    private Path mWavePath;
    private int mPoints;
    private PointF[] mBezierPoints, mBezierControlPoints1, mBezierControlPoints2;
    private float[] mSrcY, mDestY;
    private float mWidthOffset;
    private Rect mClipBounds;
    private int mBatchCount;

    private Random mRandom;

    public WaveVisualizer(Context context) {
        super(context);
    }

    public WaveVisualizer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public WaveVisualizer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public WaveVisualizer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void init() {
        mPoints = (int) (WAVE_MAX_POINTS * mDensity);
        if (mPoints < WAVE_MIN_POINTS) {
            mPoints = WAVE_MIN_POINTS;
        }

        mWidthOffset = -1;
        mBatchCount = 0;

        setAnimSpeed(mAnimSpeed);

        mRandom = new Random();
        mClipBounds = new Rect();
        mWavePath = new Path();

        mSrcY = new float[mPoints + 1];
        mDestY = new float[mPoints + 1];

        //initialize mBezierPoints
        mBezierPoints = new PointF[mPoints + 1];
        mBezierControlPoints1 = new PointF[mPoints + 1];
        mBezierControlPoints2 = new PointF[mPoints + 1];
        for (int i = 0; i < mBezierPoints.length; i++) {
            mBezierPoints[i] = new PointF();
            mBezierControlPoints1[i] = new PointF();
            mBezierControlPoints2[i] = new PointF();
        }
    }

    @Override
    public void setAnimSpeed(int animSpeed) {
        super.setAnimSpeed(animSpeed);
        this.mMaxBatchCount = MAX_ANIM_BATCH_COUNT - mAnimSpeed;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = mWidth;
        int height = mHeight;
        if (mWidthOffset == -1) {
            canvas.getClipBounds(mClipBounds);
            mWidthOffset = (float) width / mPoints;
            //initialize bezier points
            for (int i = 0; i < mBezierPoints.length; i++) {
                float posX = mClipBounds.left + (i * mWidthOffset);
                float posY;
                if (mGravity == Gravity.TOP) {
                    posY = mClipBounds.top;
                } else {
                    posY = mClipBounds.bottom;
                }

                mSrcY[i] = posY;
                mDestY[i] = posY;
                mBezierPoints[i].set(posX, posY);
            }
        }

        //create the path and draw
        if (isVisualizationEnabled && mRawAudioBytes != null) {
            if (mRawAudioBytes.length == 0) {
                return;
            }

            mWavePath.rewind();
            //find the destination bezier point for a batch
            if (mBatchCount == 0) {
                float randPosY = mDestY[mRandom.nextInt(mPoints)];
                for (int i = 0; i < mBezierPoints.length; i++) {
                    int x = (int) Math.ceil((i + 1) * (int)(mRawAudioBytes.length / mPoints));
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

                mDestY[mBezierPoints.length - 1] = randPosY;
            }

            //increment batch count
            mBatchCount++;

            //for smoothing animation
            for (int i = 0; i < mBezierPoints.length; i++) {
                mBezierPoints[i].y = mSrcY[i] + (((float) (mBatchCount) / mMaxBatchCount) * (mDestY[i] - mSrcY[i]));
            }

            //reset the batch count
            if (mBatchCount == mMaxBatchCount)
                mBatchCount = 0;

            //calculate the bezier curve control points
            for (int i = 1; i < mBezierPoints.length; i++) {
                mBezierControlPoints1[i].set((mBezierPoints[i].x + mBezierPoints[i - 1].x) / 2, mBezierPoints[i - 1].y);
                mBezierControlPoints2[i].set((mBezierPoints[i].x + mBezierPoints[i - 1].x) / 2, mBezierPoints[i].y);
            }

            //create the path
            mWavePath.moveTo(mBezierPoints[0].x, mBezierPoints[0].y);
            for (int i = 1; i < mBezierPoints.length; i++) {
                mWavePath.cubicTo(mBezierControlPoints1[i].x, mBezierControlPoints1[i].y,
                        mBezierControlPoints2[i].x, mBezierControlPoints2[i].y,
                        mBezierPoints[i].x, mBezierPoints[i].y);
            }

            //add last 3 line to close the view
            //mWavePath.lineTo(mClipBounds.right, mBezierPoints[0].y);
            if (mPaintType == PaintType.FILL) {
                mWavePath.lineTo(mClipBounds.right, mClipBounds.bottom);
                mWavePath.lineTo(mClipBounds.left, mClipBounds.bottom);
                mWavePath.close();
            }

            canvas.drawPath(mWavePath, mPaint);
        }

        super.onDraw(canvas);
    }
}
