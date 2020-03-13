package com.ccbfm.music.player.tool;

import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

public class MathTools {

    public static int calculationIndex(int index, int size) {
        if (index > size - 1) {
            index = size - 1;
        } else if (index < 0) {
            index = 0;
        }
        return index;
    }

    public static boolean inRangeOfView(View view, MotionEvent ev) {
        return inRangeOfView(view, ev.getX(), ev.getY());
    }

    /**
     * 检测坐标 cx xy 是否在view 范围中
     *
     * @param view view
     * @param cx   cx
     * @param cy   cy
     * @return boolean
     */
    public static boolean inRangeOfView(View view, float cx, float cy) {
        return inRangeOfView(view, 1, cx, cy);
    }

    private static boolean inRangeOfView(View view, int mode, float cx, float cy) {
        switch (mode) {
            case 2:
                int[] location = new int[2];
                view.getLocationOnScreen(location);
                int x = location[0];
                int y = location[1];
                if (x == 0 && y == 0) {
                    x = view.getLeft();
                    y = view.getTop();
                }
                return !(cx < x || cx > (x + view.getWidth()) || cy < y || cy > (y + view.getHeight()));
            case 1:
            default:
                Rect rect = new Rect();
                view.getHitRect(rect);
                return rect.contains((int) cx, (int) cy);
        }
    }
}
