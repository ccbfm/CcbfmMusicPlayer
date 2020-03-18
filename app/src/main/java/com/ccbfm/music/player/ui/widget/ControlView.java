package com.ccbfm.music.player.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.ccbfm.music.player.R;

public class ControlView extends View {

    private Paint mBgPaint;
    private Paint mBarPaint;
    private Path mPlayPath;
    private Path mPausePath;

    public ControlView(Context context) {
        this(context, null);
    }

    public ControlView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ControlView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBgPaint.setColor(getContext().getResources().getColor(R.color.color_f2f2f2));
        mBgPaint.setStyle(Paint.Style.FILL);

        mBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBarPaint.setColor(getContext().getResources().getColor(R.color.color_515151));
        mBarPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawCircle(100, 100, 100, mBgPaint);

        mPlayPath.rewind();
        mPausePath.rewind();

        
    }
}
