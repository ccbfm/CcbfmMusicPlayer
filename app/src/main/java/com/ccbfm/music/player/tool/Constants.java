package com.ccbfm.music.player.tool;

import android.os.Environment;

public interface Constants {

    String ROOT_PATH = Environment.getExternalStorageDirectory().getPath();

    String SCAN_SUCCESS_NOTIFICATION = "scan_success_notification";

    int ONLY_RESET_SONG_LIST = -123;
}
