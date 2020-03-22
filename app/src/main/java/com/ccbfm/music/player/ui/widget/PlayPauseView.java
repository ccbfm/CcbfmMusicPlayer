package com.ccbfm.music.player.ui.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.ccbfm.music.player.R;
import com.ccbfm.music.player.tool.DateTools;
import com.ccbfm.music.player.tool.LogTools;
import com.ccbfm.music.player.tool.MathTools;

import java.text.DateFormat;
import java.util.Date;

public class PlayPauseView extends View {
    private static final String TAG = "PlayPauseView";
    private Paint mBgPaint;
    private Paint mBarPaint;
    private Paint mRingPaint;
    private Paint mRingTextPaint;
    private RectF mRingRectF;
    private Path mLeftPath;
    private Path mRightPath;
    private int mWidth, mHeight, mCenterWidth, mCenterHeight;
    private int mBarWidth, mBarHeight, mBarPadding, mBarHalfPadding, mBarSpace, mBarHalfSpace, mRadius;
    private float mProgress, mRingProgress;
    private String mMescText = "", mMaxMescText = "";
    private long mMaxMesc = 0;
    private int mBarBgColor, mBarColor, mBarActiveColor;
    private boolean mIsClockWise = false;
    private boolean mBarPlayingState = false;
    private CallbackClick mCallbackClick;

    public PlayPauseView(Context context) {
        this(context, null);
    }

    public PlayPauseView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayPauseView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public PlayPauseView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PlayPauseView);
            mBarWidth = (int) typedArray.getDimension(R.styleable.PlayPauseView_pp_barWidth, 50);
            mBarHeight = (int) typedArray.getDimension(R.styleable.PlayPauseView_pp_barHeight, 150);
            mBarHalfPadding = (int) typedArray.getDimension(R.styleable.PlayPauseView_pp_barPadding, 50);
            mBarBgColor = typedArray.getColor(R.styleable.PlayPauseView_pp_barBgColor, getResources().getColor(R.color.color_f2f2f2));
            mBarColor = typedArray.getColor(R.styleable.PlayPauseView_pp_barColor, getResources().getColor(R.color.color_515151));
            mBarActiveColor = typedArray.getColor(R.styleable.PlayPauseView_pp_barActiveColor, getResources().getColor(R.color.color_698B22));
            mBarPlayingState = typedArray.getBoolean(R.styleable.PlayPauseView_pp_barPlayingState, false);
            typedArray.recycle();
            mProgress = mBarPlayingState ? 0f : 1f;
            mBarPadding = mBarHalfPadding + mBarHalfPadding;
            mBarSpace = mBarHeight - mBarWidth * 2;
            mBarHalfSpace = mBarSpace / 2;
            mRadius = mBarHeight / 2 + mBarHalfPadding;
            mWidth = mHeight = (mRadius + mBarHalfPadding) * 2;
        }
        init();
    }

    private void init() {
        setClickable(true);
        setFocusable(true);
        mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBgPaint.setColor(mBarBgColor);
        mBgPaint.setStyle(Paint.Style.FILL);

        mRingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRingPaint.setColor(mBarActiveColor);
        mRingPaint.setStyle(Paint.Style.STROKE);


        mRingTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRingTextPaint.setColor(mBarBgColor);
        mRingTextPaint.setStyle(Paint.Style.FILL);


        mBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBarPaint.setColor(mBarColor);
        mBarPaint.setStyle(Paint.Style.FILL);

        mLeftPath = new Path();
        mRightPath = new Path();

        setCenterPosition();
    }

    private void setCenterPosition() {
        mCenterWidth = mWidth / 2;
        mCenterHeight = mHeight / 2;
        float bhhp = mBarHalfPadding / 2f;
        float temp = mRadius + bhhp;
        mRingRectF = new RectF(mCenterWidth - temp,
                mCenterHeight - temp,
                mCenterWidth + temp,
                mCenterHeight + temp);
        if (mRingPaint != null) {
            mRingPaint.setStrokeWidth(mBarHalfPadding);
        }
        if (mRingTextPaint != null) {
            mRingTextPaint.setTextSize(bhhp);
        }
    }

    private void setAttributes(int temp) {
        mWidth = mHeight = temp;
        mRadius = mWidth / 2 - mBarHalfPadding;
        mBarPadding = mRadius - mBarHeight / 2 + mBarHalfPadding;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measureHeight = MeasureSpec.getSize(heightMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY || heightMode == MeasureSpec.EXACTLY) {
            int temp = Math.min(measureWidth, measureHeight);
            if (temp != mWidth) {
                setAttributes(temp);
            }
            setCenterPosition();

            setMeasuredDimension(mWidth, mHeight);
        } else if (widthMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.AT_MOST) {
            mWidth = mHeight = (mRadius + mBarHalfPadding) * 2;
            setCenterPosition();
            setMeasuredDimension(mWidth, mHeight);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int centerW = mCenterWidth;
        int centerH = mCenterHeight;
        canvas.drawCircle(centerW, centerH, mRadius, mBgPaint);
        mRingPaint.setColor(mBarColor);
        canvas.drawArc(mRingRectF, 0, 360, false, mRingPaint);
        canvas.save();
        //旋转画布
        canvas.rotate(-90, centerW, centerH);
        mRingPaint.setColor(mBarActiveColor);
        canvas.drawArc(mRingRectF, 0, mRingProgress * 360, false, mRingPaint);
        canvas.restore();
        canvas.save();
        mRingTextPaint.setColor(mBarBgColor);
        float textWidth = mRingTextPaint.measureText(mMescText);
        canvas.rotate(mRingProgress * 360, centerW, centerH);
        canvas.drawText(mMescText, centerW - textWidth / 2f, mBarHalfPadding * 3 / 4f, mRingTextPaint);
        canvas.restore();

        mRingTextPaint.setColor(mBarActiveColor);
        float textMaxWidth = mRingTextPaint.measureText(mMaxMescText);
        canvas.drawText(mMaxMescText, centerW - textMaxWidth / 2f, mBarHalfPadding * 3 / 2f, mRingTextPaint);

        mLeftPath.rewind();
        mRightPath.rewind();
        int padding = mBarPadding, barWidth = mBarWidth, barHhWidth = mBarWidth / 4,
                barSpace = mBarSpace, barHalfSpace = mBarHalfSpace, barHeight = mBarHeight;
        final float progress = mProgress;
        if (progress > 0.5) {
            mBarPaint.setColor(mBarColor);
        } else {
            mBarPaint.setColor(mBarActiveColor);
        }
        float proWhs = (barWidth + barHalfSpace) * progress;
        float proHs = barHalfSpace * progress;
        float proHhw = barHhWidth * progress;
        int pw = padding + barWidth;
        int ph = padding + barHeight;
        int pw2s = padding + barWidth * 2 + barSpace;
        int pws = pw + barSpace;
        if (mIsClockWise) {
            mLeftPath.moveTo(padding + proWhs, padding);
            mLeftPath.lineTo(padding - proHhw, ph);
            mLeftPath.lineTo(pw + proHs, ph);
            mLeftPath.lineTo(pw + proHs, padding);
            mLeftPath.close();

            mRightPath.moveTo(pws - proHs, padding);
            mRightPath.lineTo(pws - proHs, ph);
            mRightPath.lineTo(pw2s + proHhw, ph);
            mRightPath.lineTo(pw2s - proWhs, padding);
            mRightPath.close();
        } else {
            mLeftPath.moveTo(padding - proHhw, padding);
            mLeftPath.lineTo(padding + proWhs, ph);
            mLeftPath.lineTo(pw + proHs, ph);
            mLeftPath.lineTo(pw + proHs, padding);
            mLeftPath.close();

            mRightPath.moveTo(pws - proHs, padding);
            mRightPath.lineTo(pws - proHs, ph);
            mRightPath.lineTo(pw2s - proWhs, ph);
            mRightPath.lineTo(pw2s + proHhw, padding);
            mRightPath.close();
        }
        int corner;
        if (mIsClockWise) {
            corner = 90;
        } else {
            corner = -90;
        }
        canvas.save();
        canvas.translate((barHalfSpace) * progress, 0);

        float rotation = corner * progress;
        //旋转画布
        canvas.rotate(rotation, centerW, centerH);

        canvas.drawPath(mLeftPath, mBarPaint);
        canvas.drawPath(mRightPath, mBarPaint);
        canvas.restore();

    }

    private Animator mAnimator;

    private Animator getAnimator(boolean isPlaying) {
        float start = isPlaying ? 0 : 1;
        float end = isPlaying ? 1 : 0;
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

                mIsClockWise = mBarPlayingState;
                mBarPlayingState = !mBarPlayingState;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mAnimator = null;
            }
        });
        return animator;
    }

    public void onClickPlay() {
        if (mCallbackClick != null) {
            if (mCallbackClick.onClickPlay(mBarPlayingState)) {
                if (mAnimator != null) {
                    mAnimator.cancel();
                    mAnimator = null;
                }
                mAnimator = getAnimator(mBarPlayingState);
                mAnimator.start();
            }
        }
    }

    public void setBarPlayingState(final boolean barPlayingState) {
        if (barPlayingState == mBarPlayingState) {
            return;
        }
        post(new Runnable() {
            @Override
            public void run() {
                if (mAnimator != null) {
                    mAnimator.cancel();
                }
                mAnimator = getAnimator(mBarPlayingState);
                mAnimator.start();
            }
        });
    }

    public void setCallbackClick(CallbackClick callbackClick) {
        mCallbackClick = callbackClick;
    }

    public interface CallbackClick {
        boolean onClickPlay(boolean isPlaying);

        void onClickSeek(int msec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float tx = event.getX();
        float ty = event.getY();
        switch (action) {
            case MotionEvent.ACTION_UP:
                boolean inBar = MathTools.inRangeOfCircle(tx, ty, mCenterWidth, mCenterHeight, mRadius);
                if (inBar) {
                    onClickPlay();
                } else {
                    //boolean inRing = MathTools.inRangeOfCircle(tx, ty, mCenterWidth,
                    //        mCenterHeight, mRadius + mBarHalfPadding);
                    //if (inRing) {
                        float angle = MathTools.calculateXYAngle(mCenterWidth, mCenterHeight, tx, ty);
                        if (mCallbackClick != null) {
                            mCallbackClick.onClickSeek((int) (mMaxMesc * angle / 360f));
                        }
                    //}
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    public void setRingProgress(float ringProgress) {
        mRingProgress = ringProgress;
        invalidate();
    }

    public void setRingProgress(int msec, long maxMesc) {
        mRingProgress = (float) msec / maxMesc;
        DateFormat dateFormat = DateTools.getDateFormat(DateTools.FORMAT_MS);
        mMescText = dateFormat.format(new Date(msec));
        mMaxMescText = dateFormat.format(new Date(maxMesc));
        mMaxMesc = maxMesc;
        invalidate();
    }

}
