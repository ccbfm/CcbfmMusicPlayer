package com.ccbfm.music.player.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;

import com.ccbfm.music.player.tool.MathTools;

/**
 * 增加头部固定
 */
public class PinnedHeaderExpandableListView extends ExpandableListView {

    private View mPinnedHeader;
    private boolean mCheckPinnedHeaderContent = true;
    private PinnedHeaderListener mPinnedHeaderListener;
    private int mPinnedHeaderWidth;
    private int mPinnedHeaderHeight;

    public PinnedHeaderExpandableListView(Context context) {
        super(context);
        initView();
    }

    public PinnedHeaderExpandableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public PinnedHeaderExpandableListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public PinnedHeaderExpandableListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    public void initView() {
        setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (mPinnedHeader == null) {
                    return;
                }

                int firstGroup = getPackedPositionGroup(getExpandableListPosition(firstVisibleItem));
                int nextPosition = firstVisibleItem + 1;
                int nextGroup = getPackedPositionGroup(getExpandableListPosition(nextPosition));
                View childView = getChildAt(1);
                if (childView == null) {
                    return;
                }
                int top = childView.getTop();

                if (nextGroup == firstGroup + 1) {
                    if (top <= mPinnedHeaderHeight) {
                        int delta = mPinnedHeaderHeight - top;
                        mPinnedHeader.layout(0, -delta, mPinnedHeaderWidth, mPinnedHeaderHeight - delta);
                    } else {
                        mPinnedHeader.layout(0, 0, mPinnedHeaderWidth, mPinnedHeaderHeight);
                    }
                    if (!mCheckPinnedHeaderContent) {
                        if (mPinnedHeaderListener != null) {
                            Object groupItem = null;
                            if (getExpandableListAdapter() instanceof BaseExpandableListAdapter) {
                                BaseExpandableListAdapter adapter = (BaseExpandableListAdapter) getExpandableListAdapter();
                                groupItem = adapter.getGroup(firstGroup);
                            }
                            mPinnedHeaderListener.changeContent(mPinnedHeader, groupItem);
                        }
                        mCheckPinnedHeaderContent = true;
                    }
                } else {
                    if (mCheckPinnedHeaderContent) {
                        if (mPinnedHeaderListener != null) {
                            Object groupItem = null;
                            if (getExpandableListAdapter() instanceof BaseExpandableListAdapter) {
                                BaseExpandableListAdapter adapter = (BaseExpandableListAdapter) getExpandableListAdapter();
                                groupItem = adapter.getGroup(firstGroup);
                            }
                            mPinnedHeaderListener.changeContent(mPinnedHeader, groupItem);
                        }
                        mCheckPinnedHeaderContent = false;
                    }
                    mPinnedHeader.layout(0, 0, mPinnedHeaderWidth, mPinnedHeaderHeight);
                }
            }
        });
    }


    public void setPinnedHeader(View view, PinnedHeaderListener listener) {
        if (view != null) {
            mPinnedHeader = view;
            mPinnedHeader.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            mPinnedHeaderListener = listener;
            requestLayout();
            postInvalidate();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mPinnedHeader == null) {
            return;
        }
        measureChild(mPinnedHeader, widthMeasureSpec, heightMeasureSpec);
        mPinnedHeaderWidth = mPinnedHeader.getMeasuredWidth();
        mPinnedHeaderHeight = mPinnedHeader.getMeasuredHeight();
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (mPinnedHeader == null) {
            return;
        }
        mPinnedHeader.layout(0, 0, mPinnedHeaderWidth, mPinnedHeaderHeight);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mPinnedHeader == null) {
            return;
        }
        drawChild(canvas, mPinnedHeader, getDrawingTime());
    }

    private boolean mIsClickPinnedHeader = false;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int x = (int) ev.getX();
        int y = (int) ev.getY();

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mPinnedHeader != null && y >= mPinnedHeader.getTop() && y <= mPinnedHeader.getBottom()) {
                    if(mPinnedHeader instanceof ViewGroup){
                        ViewGroup ph = (ViewGroup)mPinnedHeader;
                        int count = ph.getChildCount();
                        for (int i = 0; i < count; i++) {
                            View view = ph.getChildAt(i);
                            boolean clickable = view.isClickable();
                             if(!clickable){
                                 continue;
                             }
                            boolean flag = MathTools.inRangeOfView(view, x, y);
                             if(flag) {
                                 view.performClick();
                                 return true;
                             }
                        }
                    }
                    mIsClickPinnedHeader = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mPinnedHeader != null && mIsClickPinnedHeader &&
                        y >= mPinnedHeader.getTop() && y <= mPinnedHeader.getBottom()) {
                    int position = pointToPosition(x, y);
                    int positionGroup = getPackedPositionGroup(getExpandableListPosition(position));
                    if (positionGroup != INVALID_POSITION) {
                        if (isGroupExpanded(positionGroup)) {
                            collapseGroup(positionGroup);
                        } else {
                            expandGroup(positionGroup);
                        }
                    }
                    mIsClickPinnedHeader = false;
                    return true;
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    public interface PinnedHeaderListener {
        void changeContent(View view, Object groupItem);
    }
}
