package com.ccbfm.music.player.tool;

import android.os.Environment;

public interface Constants {

    String ROOT_PATH = Environment.getExternalStorageDirectory().getPath();

    String SCAN_SUCCESS_NOTIFICATION_CONTROL = "scan_success_notification_control";
    String SCAN_SUCCESS_NOTIFICATION_SONG_LIST = "scan_success_notification_song_list";

    int ONLY_RESET_SONG_LIST = -123;
}
