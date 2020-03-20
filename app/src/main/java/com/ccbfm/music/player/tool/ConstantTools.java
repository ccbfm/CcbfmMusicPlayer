package com.ccbfm.music.player.tool;

public final class ConstantTools implements Constants {

    public static void postScanSuccess() {
        LiveDataBus.get().<Boolean>with(SCAN_SUCCESS_NOTIFICATION_CONTROL).postValue(true);
        LiveDataBus.get().<Boolean>with(SCAN_SUCCESS_NOTIFICATION_SONG_LIST).postValue(true);
    }
}
