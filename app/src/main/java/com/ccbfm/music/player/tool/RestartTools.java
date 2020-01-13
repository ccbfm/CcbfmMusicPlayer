package com.ccbfm.music.player.tool;

public class RestartTools {

    //应用在后台被强杀了
    public static final int STATUS_FORCE_KILLED = -1;
    //APP正常态
    public static final int STATUS_NORMAL = 2;

    //默认为被后台回收了
    private int mAppStatus = STATUS_FORCE_KILLED;

    private static RestartTools sAppStatusManager;

    public static RestartTools getInstance() {
        if (sAppStatusManager == null) {
            sAppStatusManager = new RestartTools();
        }
        return sAppStatusManager;
    }

    public int getAppStatus() {
        return mAppStatus;
    }

    public void setAppStatus(int appStatus) {
        this.mAppStatus = appStatus;
    }

}
