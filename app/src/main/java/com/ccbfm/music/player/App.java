package com.ccbfm.music.player;

import android.app.Application;

import com.ccbfm.music.player.tool.AndroidPermissionTool;
import com.ccbfm.music.player.tool.SharedPreferencesTools;

import org.litepal.LitePal;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        LitePal.initialize(this);

        AndroidPermissionTool.init();
        SharedPreferencesTools.init(this);
    }
}
