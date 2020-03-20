package com.ccbfm.music.player.tool;

import android.app.ActivityManager;
import android.content.Context;
import android.os.PowerManager;

import java.util.List;

public class SystemTools {

    public static void killAppProcess(Context context) {
        LogTools.d("SystemTools", "killAppProcess", "context=" + context);
        if (context == null) {
            return;
        }
        //注意：不能先杀掉主进程，否则逻辑代码无法继续执行，需先杀掉相关进程最后杀掉主进程
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            List<ActivityManager.RunningAppProcessInfo> mList = activityManager.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : mList) {
                if (runningAppProcessInfo.pid != android.os.Process.myPid()) {
                    android.os.Process.killProcess(runningAppProcessInfo.pid);
                }
            }
        }
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    /**
     * //如果持有该类型的wakelock锁，则按Power键灭屏后，即使允许屏幕、按键灯灭，也不会释放该锁，CPU不会进入休眠状态
     * public static final int PARTIAL_WAKE_LOCK;
     * //Deprecated，如果持有该类型的wakelock锁，则使屏幕保持亮/Dim的状态，键盘灯允许灭，按Power键灭屏后，会立即释放
     * public static final int SCREEN_DIM_WAKE_LOCK;
     * //Deprecated，如果持有该类型的wakelock锁，则使屏幕保持亮的状态，键盘灯允许灭，按Power键灭屏后，会立即释放
     * public static final int SCREEN_BRIGHT_WAKE_LOCK
     * //Deprecated，如果持有该类型的wakelock锁，则使屏幕、键盘灯都保持亮，按Power键灭屏后，会立即释放
     * public static final int FULL_WAKE_LOCK
     * //如果持有该锁，则当PSensor检测到有物体靠近时关闭屏幕，远离时又亮屏，该类型锁不会阻止系统进入睡眠状态，比如
     * //当到达休眠时间后会进入睡眠状态，但是如果当前屏幕由该wakelock关闭，则不会进入睡眠状态。
     * public static final int PROXIMITY_SCREEN_OFF_WAKE_LOCK
     * //如果持有该锁，则会使屏幕处于DOZE状态，同时允许CPU挂起，该锁用于DreamManager实现Doze模式，如SystemUI的DozeService
     * public static final int DOZE_WAKE_LOCK
     * //如果持有该锁,则会时设备保持唤醒状态，以进行绘制屏幕，该锁常用于WindowManager中，允许应用在系统处于Doze状态下时进行绘制
     * public static final int DRAW_WAKE_LOCK
     * <p>
     * <p>
     * //该值为0x0000FFFF，用于根据flag判断Wakelock的级别，如：
     * //if((wakeLock.mFlags & PowerManager.WAKE_LOCK_LEVEL_MASK) == PowerManager.PARTIAL_WAKE_LOCK){}
     * public static final int WAKE_LOCK_LEVEL_MASK
     * //用于在申请锁时唤醒设备，一般情况下，申请wakelock锁时不会唤醒设备，它只会导致屏幕保持打开状态，如果带有这个flag，则会在申
     * //请wakelock时就点亮屏幕,如常见通知来时屏幕亮，该flag不能和PowerManager.PARTIAL_WAKE_LOCK一起使用。
     * public static final int ACQUIRE_CAUSES_WAKEUP
     * //在释放锁时，如果wakelock带有该标志，则会小亮一会再灭屏，该flag不能和PowerManager.PARTIAL_WAKE_LOCK 一起使用。
     * public static final int ON_AFTER_RELEASE
     * //和其他标记不同，该标记是作为release()方法的参数，且仅仅用于释放PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK 类型的
     * //锁，如果带有该参数，则会延迟释放锁，直到传感器不再感到对象接近
     * public static final int RELEASE_FLAG_WAIT_FOR_NO_PROXIMITY
     */
    public static PowerManager.WakeLock newWakeLock(Context context, String tag) {
        PowerManager powerManager = (PowerManager) context.getApplicationContext().getSystemService(Context.POWER_SERVICE);
        if(powerManager != null) {
            return powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, tag);
        }
        return null;
    }
}
