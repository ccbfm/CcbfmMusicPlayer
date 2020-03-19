package com.ccbfm.music.player.ui.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class PreNextView extends View {
    private static final String TAG = "PreNextView";
    private Paint mBarPaint;
    private Path mBarPath;
    private RectF mOuterRectF, mInnerRectF;
    private int mDirection = Direction.LEFT;
    private int mWidth, mHeight, mCenterWidth, mCenterHeight;
    private float mProgress = 0;

    public PreNextView(Context context) {
        this(context, null);
    }

    public PreNextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PreNextView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public PreNextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        if (attrs != null) {

        }
        init();
    }

    private void init() {
        mBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBarPaint.setColor(Color.WHITE);
        mBarPaint.setStrokeWidth(10);
        mBarPaint.setStyle(Paint.Style.STROKE);

        mBarPath = new Path();
        mOuterRectF = new RectF();
        mInnerRectF = new RectF();
    }

    private void setCenterPosition() {
        mCenterWidth = mWidth / 2;
        mCenterHeight = mHeight / 2;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measureHeight = MeasureSpec.getSize(heightMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY || heightMode == MeasureSpec.EXACTLY) {
            mWidth = measureWidth;
            mHeight = measureHeight;
            setCenterPosition();
        } else if (widthMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.AT_MOST) {
            mWidth = mHeight = 10;
            setCenterPosition();
            setMeasuredDimension(mWidth, mHeight);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int centerW = mCenterWidth;
        int centerH = mCenterHeight;
        int width = mWidth, height = mHeight;
        float progress = mProgress;
        float startX = 0, endX = 0, startY = 0, endY = 0;
        float vertexX1 = 0, vertexY1 = 0, vertexX2 = 0, vertexY2 = 0;
        switch (mDirection) {
            case Direction.LEFT:
                startX = endX = width - centerW / 4f + centerW / 4f * progress;
                startY = (centerH / 2f) * progress;
                endY = height - (centerH / 2f) * progress;
                vertexX1 = -(centerW + centerW / 2f);
                vertexY1 = centerH;
                vertexX2 = 0;
                vertexY2 = centerH;
                break;
            case Direction.RIGHT:
                startX = endX = width - centerW / 4f + centerW / 4f * progress;
                startY = (centerH / 2f) * progress;
                endY = height - (centerH / 2f) * progress;
                vertexX1 = width + (centerW + centerW / 2f);
                vertexY1 = centerH;
                vertexX2 = width;
                vertexY2 = centerH;
                break;
        }
        mBarPath.rewind();
        mBarPath.moveTo(startX, startY);
        mBarPath.cubicTo(startX, startY, vertexX1, vertexY1, endX, endY);
        mBarPath.cubicTo(startX, endY, vertexX2, vertexY2, endX, startY);
        canvas.drawPath(mBarPath, mBarPaint);
        //canvas.drawCircle(mCenterWidth, mCenterHeight, 10, mBarPaint);
    }

    private Animator getAnimator(boolean flag) {
        float start = flag ? 0 : 1;
        float end = flag ? 1 : 0;
        ValueAnimator animator = ValueAnimator.ofFloat(start, end);
        animator.setDuration(200);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mProgress = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mAnimator = null;
            }
        });
        return animator;
    }

    private Animator mAnimator;

    private void startAnimator(boolean flag) {
        if (mAnimator != null) {
            mAnimator.cancel();
        }
        mAnimator = getAnimator(flag);
        mAnimator.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                startAnimator(true);
                break;
            case MotionEvent.ACTION_UP:
                startAnimator(false);
                break;
        }
        return true;
    }

    public @interface Direction {
        int LEFT = 1;
        int RIGHT = 2;
    }
}
