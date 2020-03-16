package com.ccbfm.music.player.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;

import com.ccbfm.music.player.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;

public class RoundProgressBar extends View {

    private final static boolean DEBUG = false;
    private final static float DEFAULT_ROTATE_ANGLE = -90f;
    /**
     * 是否初始化参数
     */
    private int mDrawState = DrawState.IDLE;
    /**
     * View的中心点
     */
    private float[] mCenterPoint;

    /**
     * 初始角度, 结束角度，Progress角度
     */
    private float mInitialAngle = 0f, mFinishAngle = 360f, mProgressAngle, mUnitAngle, mDifferValue;

    /**
     * 圆环进度条的宽度 默认五分之一 {@link RoundProgressBar#mRoundProgressRadius}
     */
    private float mRoundWidth;
    /**
     * 圆环进度条地板颜色
     */
    private int mRoundFloorColor;
    /**
     * 圆环进度条开始颜色
     */
    private int mRoundProgressStartColor;
    /**
     * 圆环进度条结束颜色
     */
    private int mRoundProgressEndColor;
    /**
     * 圆环进度条半径 默认二分之一 view的 Math.min(宽度,高度)
     */
    private float mRoundProgressRadius;
    /**
     * 圆环中心百分比字体颜色
     */
    private int mRoundTextColor;
    /**
     * 圆环中心百分比字体大小
     */
    private float mRoundTextSize;
    private float mTextSizeHeight;
    /**
     * 底环画笔
     */
    private Paint mRoundPaint;
    /**
     * 圆环进度条画笔
     */
    private Paint mRoundProgressPaint;
    private Paint mLinePaint;
    private Paint mTextPaint;
    /**
     * 进度条圆环渐变shader
     */
    private Shader mRoundProgressShader;
    private int[] mShaderColors;

    private RectF mOvalBounds;

    private float mStartAngle, mEndAngle;
    private float mRotateAngle;
    private int mAnimationDuration = 10000;
    private int mDurationSecond = 10;

    private Animation mBarAnimation;
    private AnimationListenerAdapter mListenerAdapter;

    public RoundProgressBar(Context context) {
        this(context, null);
    }

    public RoundProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if(attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundProgressBar);
            mRoundWidth = typedArray.getDimension(R.styleable.RoundProgressBar_round_width, 0);
            mRoundFloorColor = typedArray.getColor(R.styleable.RoundProgressBar_round_floor_color, Color.parseColor(("#556677")));
            mRoundProgressStartColor = typedArray.getColor(R.styleable.RoundProgressBar_round_progress_start_color, Color.parseColor(("#FFFFFF")));
            mRoundProgressEndColor = typedArray.getColor(R.styleable.RoundProgressBar_round_progress_end_color, Color.parseColor(("#000000")));
            mRoundProgressRadius = typedArray.getDimension(R.styleable.RoundProgressBar_round_progress_radius, 0);
            mRoundTextColor = typedArray.getColor(R.styleable.RoundProgressBar_round_text_color, mRoundFloorColor);
            mRoundTextSize = typedArray.getDimension(R.styleable.RoundProgressBar_round_text_size, 0);
            typedArray.recycle();
        }
        init();
    }

    private void init() {
        mCenterPoint = new float[2];

        mRoundPaint = new Paint();
        // 抗锯齿效果
        mRoundPaint.setAntiAlias(true);
        mRoundPaint.setStyle(Paint.Style.STROKE);
        mRoundPaint.setColor(mRoundFloorColor);

        mRoundProgressPaint = new Paint();
        // 抗锯齿效果
        mRoundProgressPaint.setAntiAlias(true);
        mRoundProgressPaint.setStyle(Paint.Style.STROKE);
        // 圆形笔头
        mRoundProgressPaint.setStrokeCap(Paint.Cap.ROUND);

        if (mRoundTextSize != 0) {
            mTextPaint = new Paint();
            mTextPaint.setAntiAlias(true);
            mTextPaint.setStyle(Paint.Style.FILL);
            mTextPaint.setTextAlign(Paint.Align.CENTER);
            mTextPaint.setColor(mRoundTextColor);
            mTextPaint.setTextSize(mRoundTextSize);
        }

        if (DEBUG) {
            mLinePaint = new Paint();
            mLinePaint.setAntiAlias(true);
            mLinePaint.setStyle(Paint.Style.STROKE);
            mLinePaint.setStrokeWidth(2);
        }
    }

    public void startAnimation() {
        startAnimation(mInitialAngle, mFinishAngle, mAnimationDuration);
    }

    public void startAnimation(int duration) {
        startAnimation(mInitialAngle, mFinishAngle, duration);
    }

    public void startAnimation(float initialAngle, float finishAngle, int duration) {
        if (mBarAnimation == null) {
            mDrawState = DrawState.INIT;
            mAnimationDuration = duration;
            mInitialAngle = initialAngle;
            mFinishAngle = finishAngle;
            postInvalidate();
        } else {
            startAnimation(mBarAnimation);
        }
    }

    public void cancel() {
        if (mBarAnimation != null) {
            mBarAnimation.cancel();
            mBarAnimation = null;
        }
    }

    public void reset() {
        if (mBarAnimation != null) {
            mBarAnimation.reset();
        }
    }

    private Animation createAnimation(int duration, float startAngle, float finishAngle) {
        BarAnimation barAnimation = new BarAnimation(startAngle, finishAngle, new BarAnimation.CallBack() {
            @Override
            public void setProgress(float progressAngle) {
                mProgressAngle = progressAngle;
                postInvalidate();
            }
        });
        barAnimation.setDuration(duration);
        //动画差值器
        barAnimation.setInterpolator(new LinearInterpolator());
        barAnimation.setAnimationListener(mListenerAdapter);
        return barAnimation;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (initParametersNeedDraw()) {
            float x = mCenterPoint[0];
            float y = mCenterPoint[1];
            // ----绘制圆环（最底部）开始------
            canvas.drawCircle(x, y, mRoundProgressRadius, mRoundPaint);
            //----绘制圆环（最底部）结束------
            canvas.save();
            canvas.rotate(mRotateAngle, x, y);
            canvas.drawArc(mOvalBounds, (mStartAngle), (mEndAngle + mProgressAngle), (false), mRoundProgressPaint);
            canvas.restore();

            // 绘制中心文字
            if (mRoundTextSize != 0) {
                int num;
                if (mProgressAngle >= 0) {
                    num = (int) Math.floor(mProgressAngle / mUnitAngle);
                } else {
                    num = mDurationSecond - (int) Math.floor(-mProgressAngle / mUnitAngle);
                }
                canvas.drawText(String.valueOf(num), x, y + mTextSizeHeight, mTextPaint);
            }

            if (DEBUG) {
                canvas.drawLine(0, y, 2 * x, y, mLinePaint);
                canvas.drawLine(x, 0, x, 2 * y, mLinePaint);
            }
        }

        super.onDraw(canvas);
    }

    private boolean initParametersNeedDraw() {
        if (mDrawState == DrawState.INIT) {
            float centerX = getWidth() / 2;
            float centerY = getHeight() / 2;
            float radius = mRoundProgressRadius;
            float roundWidth = mRoundWidth;
            if (radius == 0) {
                radius = Math.min(centerX, centerY);
                if (roundWidth == 0) {
                    roundWidth = radius / 5;
                }
                radius = radius - (roundWidth / 2);
            }

            if (mRoundProgressShader == null) {
                int[] colors = mShaderColors;
                if (colors == null) {
                    colors = new int[]{mRoundProgressStartColor, mRoundProgressEndColor};
                }
                mRoundProgressShader = new SweepGradient(centerX, centerY, colors, null);
                mRoundProgressPaint.setShader(mRoundProgressShader);
            }
            mRoundPaint.setStrokeWidth(roundWidth);
            mRoundProgressPaint.setStrokeWidth(roundWidth);
            mCenterPoint[0] = centerX;
            mCenterPoint[1] = centerY;

            mOvalBounds = new RectF((centerX - radius), (centerY - radius), (centerX + radius), (centerY + radius));

            float offsetAngle = (float) radianToAngle(roundWidth, radius);
            float differValue = mFinishAngle - mInitialAngle;

            mRoundWidth = roundWidth;
            mRoundProgressRadius = radius;

            if (differValue < 0) {
                differValue += 2 * offsetAngle;
                mRotateAngle = DEFAULT_ROTATE_ANGLE + mFinishAngle;
                mStartAngle = offsetAngle;
                mEndAngle = -differValue;
            } else {
                differValue -= 2 * offsetAngle;
                mRotateAngle = DEFAULT_ROTATE_ANGLE + mInitialAngle;
                mStartAngle = offsetAngle;
                mEndAngle = 0;
            }

            if (mRoundTextSize != 0) {
                mTextSizeHeight = getFontHeight(mTextPaint);
                mDurationSecond = mAnimationDuration / 1000;
                mUnitAngle = Math.abs(differValue) / mDurationSecond;
            }

            mDifferValue = differValue;
            Animation animation = createAnimation(mAnimationDuration, (0), differValue);
            startAnimation(animation);
            mBarAnimation = animation;

            mDrawState = DrawState.PREP;

            return true;
        }
        return (mDrawState == DrawState.PREP);
    }


    /**
     * 已知圆半径和切线长求弧长公式
     *
     * @param progressWidth 切线长
     * @param radius        半径
     * @return Degrees
     */
    private double radianToAngle(float progressWidth, float radius) {
        double temp = progressWidth / 2 / radius;
        double result = Math.asin(temp);
        return Math.toDegrees(result);
    }

    private float getFontHeight(Paint paint) {
        Paint.FontMetrics fm = paint.getFontMetrics();
        return (-fm.ascent - fm.descent) / 2;
    }

    public void setRoundProgressShader(int[] shaderColors) {
        mShaderColors = shaderColors;
    }

    public void setAnimationListener(AnimationListenerAdapter listenerAdapter) {
        this.mListenerAdapter = listenerAdapter;
    }

    public void endAnimation() {
        mProgressAngle = mDifferValue;
        postInvalidate();
    }

    /**
     * 进度条动画
     */
    public static class BarAnimation extends Animation {

        interface CallBack {
            /**
             * 回调
             *
             * @param progressAngle progressAngle
             */
            void setProgress(float progressAngle);
        }

        private float mInitialAngle, mFinishAngle;
        private WeakReference<CallBack> mReference;

        private BarAnimation(float initialAngle, float finishAngle, CallBack callBack) {
            mInitialAngle = initialAngle;
            mFinishAngle = finishAngle;
            mReference = new WeakReference<>(callBack);
        }

        /**
         * 然后调用postInvalidate()不停的绘制view。
         */
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            float angle = mFinishAngle - mInitialAngle;
            float progressAngle = interpolatedTime * angle;
            if (mReference.get() != null) {
                mReference.get().setProgress(progressAngle);
            }
        }
    }

    public static class AnimationListenerAdapter<T> implements Animation.AnimationListener {

        protected WeakReference<T> mWeakReference;

        protected AnimationListenerAdapter(T reference) {
            this.mWeakReference = new WeakReference<>(reference);
        }

        @Override
        public void onAnimationStart(Animation animation) {
            T target = mWeakReference.get();
            if (target != null) {
                onAnimationStart(animation, target);
            }
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            T target = mWeakReference.get();
            if (target != null) {
                onAnimationEnd(animation, target);
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
            T target = mWeakReference.get();
            if (target != null) {
                onAnimationRepeat(animation, target);
            }
        }

        public void onAnimationStart(Animation animation, T target) {
        }

        public void onAnimationEnd(Animation animation, T target) {
        }

        public void onAnimationRepeat(Animation animation, T target) {
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    private @interface DrawState {
        int IDLE = 0;
        int INIT = 1;
        int PREP = 2;
    }
}
