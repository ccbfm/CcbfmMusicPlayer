package com.ccbfm.music.player.callback;

import android.os.RemoteException;

import com.ccbfm.music.player.IPlayerCallback;
import com.ccbfm.music.player.database.entity.Song;

public class PlayerCallbackAdapter extends IPlayerCallback.Stub {
    @Override
    public void callbackIndex(int index) {

    }

    @Override
    public void callbackMsec(int msec) {

    }

    @Override
    public void callbackError(int code, Song song) {

    }

    @Override
    public void callbackStatus(int status) {

    }
}
