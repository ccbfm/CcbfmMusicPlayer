package com.ccbfm.music.player.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.customview.widget.ViewDragHelper;

import com.ccbfm.music.player.R;
import com.ccbfm.music.player.exception.ChildPlaceException;
import com.ccbfm.music.player.tool.LogTools;

public class SlidingMenuView extends FrameLayout {
    private static final String TAG = "SlidingMenuView";
    private static final boolean DEBUG = false;
    private int mSlidingViewId;
    private int mMaxDistance;
    private View mSlidingView;
    private ViewDragHelper mDragHelper;
    private float mDx, mDy;
    private boolean mIsOpen = false;
    private SlidingStateListener mSlidingStateListener;

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
            mMaxDistance = (int) maxDistance;
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        init();
    }

    private void init() {
        if (mSlidingViewId > 0) {
            mSlidingView = findViewById(mSlidingViewId);
            if (mSlidingView != null) {
                int index = indexOfChild(mSlidingView);
                if (index != getChildCount() - 1) {
                    throw new ChildPlaceException("滑动的View要在最后位置");
                }
            }
            mDragHelper = ViewDragHelper.create(this, new SlidingViewCallback());
        }
    }

    public void setSlidingStateListener(SlidingStateListener slidingStateListener) {
        mSlidingStateListener = slidingStateListener;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(DEBUG) {
            LogTools.d(TAG, "dispatchTouchEvent", isSlidable() + ",ev=" + ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(DEBUG) {
            LogTools.d(TAG, "onInterceptTouchEvent", isSlidable() + ",ev=" + ev);
        }
        if (mDragHelper != null) {
            return mDragHelper.shouldInterceptTouchEvent(ev);
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(DEBUG) {
            LogTools.d(TAG, "onTouchEvent", isSlidable() + ",ev=" + ev);
        }
        if (isSlidable()) {
            final int action = ev.getAction();

            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    mDx = ev.getX();
                    mDy = ev.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    if (mDx == ev.getX() && mDy == ev.getY() && !mIsOpen) {
                        performClick();
                    }
                    break;
            }
            mDragHelper.processTouchEvent(ev);
            return true;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public void computeScroll() {
        if (mDragHelper != null && mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private boolean isSlidable() {
        return mMaxDistance > 0 && mSlidingView != null && mDragHelper != null;
    }

    private void openSlidingView() {
        if(DEBUG) {
            LogTools.d(TAG, "openSlidingView", "mIsOpen=" + mIsOpen);
        }
        if(mIsOpen){
            return;
        }
        mIsOpen = true;
        mDragHelper.smoothSlideViewTo(mSlidingView, -mMaxDistance, 0);
        ViewCompat.postInvalidateOnAnimation(SlidingMenuView.this);
        changeSlidingStateListener(true);
    }

    public void closeSlidingView() {
        if(DEBUG) {
            LogTools.d(TAG, "closeSlidingView", "mIsOpen=" + mIsOpen);
        }
        if(!mIsOpen){
            return;
        }
        mIsOpen = false;
        mDragHelper.smoothSlideViewTo(mSlidingView, 0, 0);
        ViewCompat.postInvalidateOnAnimation(SlidingMenuView.this);
        changeSlidingStateListener(false);
    }

    private void changeSlidingStateListener(boolean open){
        if(mSlidingStateListener != null){
            int groupPosition = (int) getTag(R.id.tag_group_position);
            int childPosition = (int) getTag(R.id.tag_child_position);
            mSlidingStateListener.onSlidingState(open, groupPosition, childPosition);
        }
    }

    private class SlidingViewCallback extends ViewDragHelper.Callback {
        private int mLeft;

        @Override
        public boolean tryCaptureView(@NonNull View child, int pointerId) {
            return isSlidable();
        }

        @Override
        public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx, int dy) {
            mLeft = left;
        }

        @Override
        public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
            if(DEBUG) {
                LogTools.d(TAG, "onViewReleased", "mLeft=" + mLeft);
            }
            if (mSlidingView != null) {
                int left = mSlidingView.getLeft();
                if (left < 0) {
                    //left
                    int dis = -mMaxDistance;
                    if (mLeft < (dis / 2)) {
                        mIsOpen = false;
                        openSlidingView();
                    }
                    //right
                    else {
                        mIsOpen = true;
                        closeSlidingView();
                    }
                }
            }
        }

        @Override
        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
            if (left > 0) {
                left = 0;
            } else if (left < -mMaxDistance) {
                left = -mMaxDistance;
            }
            return left;
        }
    }

    public interface SlidingStateListener {
        void onSlidingState(boolean open, int groupIndex, int childIndex);
    }
}
