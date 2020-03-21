package com.ccbfm.music.player.ui.widget.visualizer;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.audiofx.Visualizer;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.ccbfm.music.player.R;

public abstract class BaseVisualizer extends View {

    protected static final float DEFAULT_DENSITY = 0.25f;
    protected static final int DEFAULT_COLOR = Color.BLACK;
    protected static final float DEFAULT_STROKE_WIDTH = 6.0f;
    protected static final int MAX_ANIM_BATCH_COUNT = 4;

    protected byte[] mRawAudioBytes;
    protected Paint mPaint;
    protected Visualizer mVisualizer;
    protected int mWidth, mHeight, mCenterWidth, mCenterHeight;
    protected int mPaintType = PaintType.FILL;
    protected int mGravity = Gravity.BOTTOM;
    protected int mColor = DEFAULT_COLOR;
    protected float mStrokeWidth = DEFAULT_STROKE_WIDTH;
    protected float mDensity = DEFAULT_DENSITY;
    protected int mAnimSpeed = AnimSpeed.MEDIUM;
    protected boolean isVisualizationEnabled = true;

    public BaseVisualizer(Context context) {
        this(context, null);
    }

    public BaseVisualizer(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseVisualizer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public BaseVisualizer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
        init();
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BaseVisualizer);
            this.mDensity = typedArray.getFloat(R.styleable.BaseVisualizer_bv_density, DEFAULT_DENSITY);
            this.mColor = typedArray.getColor(R.styleable.BaseVisualizer_bv_color, DEFAULT_COLOR);
            this.mStrokeWidth = typedArray.getDimension(R.styleable.BaseVisualizer_bv_strokeWidth, DEFAULT_STROKE_WIDTH);
            this.mPaintType = typedArray.getInt(R.styleable.BaseVisualizer_bv_paintType, PaintType.FILL);
            this.mGravity = typedArray.getInt(R.styleable.BaseVisualizer_bv_gravity, Gravity.BOTTOM);
            this.mAnimSpeed = typedArray.getInt(R.styleable.BaseVisualizer_bv_animSpeed, AnimSpeed.MEDIUM);
            typedArray.recycle();
        }

        mPaint = new Paint();
        mPaint.setColor(mColor);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setStyle(mPaintType == PaintType.FILL ? Paint.Style.FILL : Paint.Style.STROKE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        mCenterWidth = mWidth / 2;
        mCenterHeight = mHeight / 2;
    }

    public void setColor(int color) {
        this.mColor = color;
        this.mPaint.setColor(this.mColor);
    }

    public void setDensity(float density) {
        synchronized (this) {
            this.mDensity = density;
            init();
        }
    }

    public void setPaintType(int paintType) {
        mPaintType = paintType;
        mPaint.setStyle(mPaintType == PaintType.FILL ? Paint.Style.FILL : Paint.Style.STROKE);
    }

    public void setGravity(int gravity) {
        mGravity = gravity;
    }

    public void setAnimSpeed(int animSpeed) {
        mAnimSpeed = animSpeed;
    }

    public void setStrokeWidth(float width) {
        this.mStrokeWidth = width;
        this.mPaint.setStrokeWidth(width);
    }

    public void setRawAudioBytes(byte[] bytes) {
        this.mRawAudioBytes = bytes;
        this.invalidate();
    }

    public void setAudioSessionId(int audioSessionId) {
        if (mVisualizer != null) {
            release();
        }
        mVisualizer = new Visualizer(audioSessionId);
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);

        mVisualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
            @Override
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes,
                                              int samplingRate) {
                setRawAudioBytes(bytes);
                invalidate();
            }

            @Override
            public void onFftDataCapture(Visualizer visualizer, byte[] bytes,
                                         int samplingRate) {
            }
        }, Visualizer.getMaxCaptureRate() / 2, true, false);

        mVisualizer.setEnabled(true);
    }

    /**
     * Releases the visualizer
     */
    public void release() {
        if (mVisualizer != null) {
            mVisualizer.release();
        }
    }

    /**
     * Enable Visualization
     */
    public void show() {
        this.isVisualizationEnabled = true;
    }

    /**
     * Disable Visualization
     */
    public void hide() {
        this.isVisualizationEnabled = false;
    }

    protected abstract void init();

    protected @interface PaintType {
        int STROKE = 1;
        int FILL = 2;
    }

    protected @interface Gravity {
        int LEFT = 1;
        int RIGHT = 2;
        int TOP = 3;
        int BOTTOM = 4;
    }

    protected @interface AnimSpeed {
        int SLOW = 1;
        int MEDIUM = 2;
        int FAST = 3;
    }

}
