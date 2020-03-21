package com.ccbfm.music.player.ui.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.ccbfm.music.player.R;

public class PreNextView extends View {
    private static final String TAG = "PreNextView";
    private Paint mBarPaint;
    private Path mBarPath;
    private float mBarStrokeWidth = 10;
    private int mBarColor, mBarActiveColor;
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
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PreNextView);
            mBarStrokeWidth = typedArray.getDimension(R.styleable.PreNextView_pn_barStrokeWidth, 10);
            mBarColor = typedArray.getColor(R.styleable.PreNextView_pn_barColor, getResources().getColor(R.color.color_f2f2f2));
            mBarActiveColor = typedArray.getColor(R.styleable.PreNextView_pn_barActiveColor, getResources().getColor(R.color.colorAccent));
            mDirection = typedArray.getInt(R.styleable.PreNextView_pn_barDirection, Direction.LEFT);
            typedArray.recycle();
        }
        init();
    }

    private void init() {
        setFocusable(true);
        setClickable(true);
        mBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBarPaint.setColor(mBarColor);
        mBarPaint.setStrokeWidth(mBarStrokeWidth);
        mBarPaint.setStyle(Paint.Style.STROKE);

        mBarPath = new Path();
    }

    private void setCenterPosition() {
        mCenterWidth = mWidth / 2;
        mCenterHeight = mHeight / 2;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        setCenterPosition();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int centerW = mCenterWidth;
        int centerH = mCenterHeight;
        int width = mWidth, height = mHeight;
        float progress = mProgress;
        if (progress > 0.5f) {
            mBarPaint.setColor(mBarActiveColor);
        } else {
            mBarPaint.setColor(mBarColor);
        }
        float startX = 0, endX = 0, startY = 0, endY = 0;
        float vertexX1 = 0, vertexY1 = 0, vertexX2 = 0, vertexY2 = 0;
        switch (mDirection) {
            case Direction.LEFT:
                startX = endX = width - centerW / 4f + centerW / 4f * progress;
                startY = (centerH / 2f) * progress;
                endY = height - startY;
                vertexX1 = -(centerW + centerW / 2f);
                vertexY1 = centerH;
                vertexX2 = 0;
                vertexY2 = centerH;
                break;
            case Direction.RIGHT:
                startX = endX = centerW / 4f - centerW / 4f * progress;
                startY = (centerH / 2f) * progress;
                endY = height - startY;
                vertexX1 = width + (centerW + centerW / 2f);
                vertexY1 = centerH;
                vertexX2 = width;
                vertexY2 = centerH;
                break;
            case Direction.TOP:
                startX = (centerW / 2f) * progress;
                endX = width - startX;
                startY = endY = height - centerH / 4f + centerH / 4f * progress;
                vertexX1 = centerW;
                vertexY1 = -(centerH + centerH / 2f);
                vertexX2 = centerW;
                vertexY2 = 0;
                break;
            case Direction.BOTTOM:
                startX = (centerW / 2f) * progress;
                endX = width - startX;
                startY = endY = centerH / 4f - centerH / 4f * progress;
                vertexX1 = centerW;
                vertexY1 = height + (centerH + centerH / 2f);
                vertexX2 = centerW;
                vertexY2 = height;
                break;
        }
        mBarPath.rewind();
        mBarPath.moveTo(startX, startY);
        mBarPath.cubicTo(startX, startY, vertexX1, vertexY1, endX, endY);
        mBarPath.cubicTo(endX, endY, vertexX2, vertexY2, startX, startY);
        canvas.drawPath(mBarPath, mBarPaint);
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
                postInvalidate();
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

    private void startAnimator(final boolean flag) {
        post(new Runnable() {
            @Override
            public void run() {
                if (mAnimator != null) {
                    mAnimator.cancel();
                }
                mAnimator = getAnimator(flag);
                mAnimator.start();
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                startAnimator(true);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                startAnimator(false);
                break;
        }
        super.onTouchEvent(event);
        return true;
    }

    public @interface Direction {
        int LEFT = 1;
        int RIGHT = 2;
        int TOP = 3;
        int BOTTOM = 4;
    }
}
