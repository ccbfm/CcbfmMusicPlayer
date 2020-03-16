package com.ccbfm.music.player.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.customview.widget.ViewDragHelper;

import com.ccbfm.music.player.R;
import com.ccbfm.music.player.exception.ChildPlaceException;

public class SlidingMenuView extends FrameLayout {
    private int mSlidingViewId;
    private int mMaxDistance;
    private View mSlidingView;
    private ViewDragHelper mDragHelper;

    public SlidingMenuView(Context context) {
        this(context, null);
    }

    public SlidingMenuView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingMenuView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public SlidingMenuView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SlidingMenuView);
            mSlidingViewId = typedArray.getResourceId(R.styleable.SlidingMenuView_sliding_view_id, 0);
            final float maxDistance = typedArray.getDimension(R.styleable.SlidingMenuView_sliding_max_distance, 0f);
            typedArray.recycle();
            mMaxDistance = (int)maxDistance;
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        init();
    }

    private void init(){
        Log.w("wds", "mSlidingViewId = " + mSlidingViewId);
        Log.w("wds", "mMaxDistance = " + mMaxDistance);
        if(mSlidingViewId > 0){
            mSlidingView = findViewById(mSlidingViewId);

            Log.w("wds", "mSlidingView = " + mSlidingView);

            if(mSlidingView != null){
                int index = indexOfChild(mSlidingView);
                if(index != getChildCount() - 1){
                    throw new ChildPlaceException("需要滑动的View要在最后位置");
                }
            }
            mDragHelper = ViewDragHelper.create(this, new SlidingViewCallback());
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(mDragHelper != null){
            return mDragHelper.shouldInterceptTouchEvent(ev);
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.w("wds", "onTouchEvent = " + isSlidable() +",event="+event);
        if(isSlidable()){
            final int action = event.getAction();
            switch (action){
                case MotionEvent.ACTION_UP:
                    mDragHelper.smoothSlideViewTo(mSlidingView, -mMaxDistance, 0);
                    break;
            }
            mDragHelper.processTouchEvent(event);
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void computeScroll() {
        if (mDragHelper != null && mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private boolean isSlidable(){
        return mMaxDistance > 0 && mSlidingView != null && mDragHelper != null;
    }

    private class SlidingViewCallback extends ViewDragHelper.Callback{
        @Override
        public boolean tryCaptureView(@NonNull View child, int pointerId) {
            return true;
        }

        @Override
        public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
        }

        @Override
        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
            if(left > 0){
                left = 0;
            } else if(left < -mMaxDistance){
                left = -mMaxDistance;
            }
            return left;
        }
    }
}
