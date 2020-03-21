package com.ccbfm.music.player.ui.widget.visualizer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

public class HiFiVisualizer extends BaseVisualizer {

    private static final int BAR_MAX_POINTS = 240;
    private static final int BAR_MIN_POINTS = 30;
    private static final float PER_RADIUS = .65f;
    private int mRadius;
    private int mPoints;
    private int[] mHeights;
    private Path mOuterPath;
    private Path mInnerPath;
    /**
     * This is the distance from center to bezier control point.
     * We can calculate the bezier control points of each segment this distance and its angle;
     */
    private int mBezierControlPointLen;

    public HiFiVisualizer(Context context) {
        super(context);
    }

    public HiFiVisualizer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public HiFiVisualizer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public HiFiVisualizer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void init() {
        mRadius = -1;
        mOuterPath = new Path();
        mInnerPath = new Path();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(1.0f);
        mPoints = (int) (BAR_MAX_POINTS * mDensity);
        if (mPoints < BAR_MIN_POINTS) mPoints = BAR_MIN_POINTS;
        mHeights = new int[mPoints];
    }

    @Override
    public void setPaintType(int paintType) {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mRadius == -1) {
            mRadius = (int) (Math.min(getWidth(), getHeight()) / 2 * PER_RADIUS);
            mBezierControlPointLen = (int) (mRadius / Math.cos(Math.PI / mPoints));
        }
        updateData();
        mOuterPath.reset();
        mInnerPath.reset();
        // start the outward path from the last point
        float cxL = (float) (getWidth() / 2 + Math.cos((360 - 360f / mPoints) * Math.PI / 180) * (mRadius + mHeights[mPoints - 1]));
        float cyL = (float) (getHeight() / 2 - Math.sin((360 - 360f / mPoints) * Math.PI / 180) * (mRadius + mHeights[mPoints - 1]));
        mOuterPath.moveTo(cxL, cyL);
        // start the inward path from the last point
        float cxL1 = (float) (getWidth() / 2 + Math.cos((360 - 360f / mPoints) * Math.PI / 180) * (mRadius - mHeights[mPoints - 1]));
        float cyL1 = (float) (getHeight() / 2 - Math.sin((360 - 360f / mPoints) * Math.PI / 180) * (mRadius - mHeights[mPoints - 1]));
        mInnerPath.moveTo(cxL1, cyL1);
        for (int i = 0; i < 360; i = i + 360 / mPoints) {
            // outward
            // the next point of path
            float cx = (float) (getWidth() / 2 + Math.cos(i * Math.PI / 180) * (mRadius + mHeights[i * mPoints / 360]));
            float cy = (float) (getHeight() / 2 - Math.sin(i * Math.PI / 180) * (mRadius + mHeights[i * mPoints / 360]));
            //second bezier control point
            float bx = (float) (getWidth() / 2 + Math.cos((i - (180f / mPoints)) * Math.PI / 180) * (mBezierControlPointLen + mHeights[i * mPoints / 360]));
            float by = (float) (getHeight() / 2 - Math.sin((i - (180f / mPoints)) * Math.PI / 180) * (mBezierControlPointLen + mHeights[i * mPoints / 360]));
            int lastPoint = i == 0 ? mPoints - 1 : i * mPoints / 360 - 1;
            //fist bezier control point
            float ax = (float) (getWidth() / 2 + Math.cos((i - (180f / mPoints)) * Math.PI / 180) * (mBezierControlPointLen + mHeights[lastPoint]));
            float ay = (float) (getHeight() / 2 - Math.sin((i - (180f / mPoints)) * Math.PI / 180) * (mBezierControlPointLen + mHeights[lastPoint]));
            mOuterPath.cubicTo(ax, ay, bx, by, cx, cy);
            // inward
            float cx1 = (float) (getWidth() / 2 + Math.cos(i * Math.PI / 180) * (mRadius - mHeights[i * mPoints / 360]));
            float cy1 = (float) (getHeight() / 2 - Math.sin(i * Math.PI / 180) * (mRadius - mHeights[i * mPoints / 360]));
            float bx1 = (float) (getWidth() / 2 + Math.cos((i - (180f / mPoints)) * Math.PI / 180) * (mBezierControlPointLen - mHeights[i * mPoints / 360]));
            float by1 = (float) (getHeight() / 2 - Math.sin((i - (180f / mPoints)) * Math.PI / 180) * (mBezierControlPointLen - mHeights[i * mPoints / 360]));
            float ax1 = (float) (getWidth() / 2 + Math.cos((i - (180f / mPoints)) * Math.PI / 180) * (mBezierControlPointLen - mHeights[lastPoint]));
            float ay1 = (float) (getHeight() / 2 - Math.sin((i - (180f / mPoints)) * Math.PI / 180) * (mBezierControlPointLen - mHeights[lastPoint]));
            mInnerPath.cubicTo(ax1, ay1, bx1, by1, cx1, cy1);
            canvas.drawLine(cx, cy, cx1, cy1, mPaint);
        }
        canvas.drawPath(mOuterPath, mPaint);
        canvas.drawPath(mInnerPath, mPaint);
    }

    private void updateData() {
        if (isVisualizationEnabled && mRawAudioBytes != null) {
            if (mRawAudioBytes.length == 0) return;
            for (int i = 0; i < mHeights.length; i++) {
                int x = (int) Math.ceil((i + 1) * ((float)mRawAudioBytes.length / mPoints));
                int t = 0;
                if (x < 1024)
                    t = ((byte) (Math.abs(mRawAudioBytes[x]) + 128)) * mRadius / 128;
                mHeights[i] = -t;
            }
        }
    }
}
