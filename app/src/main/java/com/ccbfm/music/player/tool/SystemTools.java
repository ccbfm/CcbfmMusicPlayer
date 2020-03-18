package com.ccbfm.music.player.tool;

import android.app.ActivityManager;
import android.content.Context;

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
}
