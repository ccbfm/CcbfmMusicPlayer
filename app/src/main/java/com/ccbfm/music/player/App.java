package com.ccbfm.music.player;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.ccbfm.music.player.control.MusicControl;
import com.ccbfm.music.player.tool.AndroidPermissionTool;
import com.ccbfm.music.player.tool.SharedPreferencesTools;

import org.litepal.LitePal;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferencesTools.init(this);
        if(!isMainProcess()){
            return;
        }
        LitePal.initialize(this);

        AndroidPermissionTool.init();

        MusicControl.getInstance().initMusicService(this);
    }

    private boolean isMainProcess() {
        return TextUtils.equals(getApplicationContext().getPackageName(),
                getCurrentProcessName());
    }

    /**
     * 获取当前进程名
     */
    private String getCurrentProcessName() {
        int pid = android.os.Process.myPid();
        String processName = "";
        ActivityManager manager = (ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            for (ActivityManager.RunningAppProcessInfo process : manager.getRunningAppProcesses()) {
                if (process.pid == pid) {
                    processName = process.processName;
                }
            }
        }
        return processName;
    }

}
