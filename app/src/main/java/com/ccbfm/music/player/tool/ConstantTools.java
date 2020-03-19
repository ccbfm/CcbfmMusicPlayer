package com.ccbfm.music.player.tool;

public final class ConstantTools {

    public static void postScanSuccess(){
        LiveDataBus.get().<Boolean>with(Constants.SCAN_SUCCESS_NOTIFICATION_CONTROL).postValue(true);
        LiveDataBus.get().<Boolean>with(Constants.SCAN_SUCCESS_NOTIFICATION_SONG_LIST).postValue(true);
    }
}
