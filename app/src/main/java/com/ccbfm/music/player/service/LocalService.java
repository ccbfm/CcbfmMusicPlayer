package com.ccbfm.music.player.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import androidx.annotation.Nullable;

import com.ccbfm.music.player.IPlayerCallback;
import com.ccbfm.music.player.control.PlayerErrorCode;
import com.ccbfm.music.player.database.entity.Song;
import com.ccbfm.music.player.tool.SPTools;
import com.ccbfm.music.player.tool.ToastTools;

import java.util.LinkedList;
import java.util.List;

import static com.ccbfm.music.player.tool.SPTools.KEY_INIT_SONG_INDEX;
import static com.ccbfm.music.player.tool.SPTools.KEY_INIT_SONG_MSEC;

public class LocalService extends Service {

    private LocalBinder mBinder;
    private static List<IPlayerCallback> sPlayerCallbackAdapters;

    @Override
    public void onCreate() {
        super.onCreate();
        mBinder = new LocalBinder();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private class LocalBinder extends IPlayerCallback.Stub {
        @Override
        public void callbackIndex(int index) throws RemoteException {
            SPTools.putIntValue(KEY_INIT_SONG_INDEX, index);
            if (isNotifyCallback()) {
                for (IPlayerCallback adapter : sPlayerCallbackAdapters) {
                    adapter.callbackIndex(index);
                }
            }
        }

        @Override
        public void callbackMsec(int msec) throws RemoteException {
            SPTools.putIntValue(KEY_INIT_SONG_MSEC, msec);
            if (isNotifyCallback()) {
                for (IPlayerCallback adapter : sPlayerCallbackAdapters) {
                    adapter.callbackMsec(msec);
                }
            }
        }

        @Override
        public void callbackError(int code, Song song) throws RemoteException {
            if (isNotifyCallback()) {
                for (IPlayerCallback adapter : sPlayerCallbackAdapters) {
                    adapter.callbackError(code, song);
                }
            }

            String message = "";
            switch (code) {
                case PlayerErrorCode.PREPARE:
                    message = "歌曲 " + song.getSongName() + " 播放错误";
                    break;
                case PlayerErrorCode.NULL:
                    message = "没有可播放音乐";
                    break;
            }
            ToastTools.showToast(getApplicationContext(), message);
        }

        @Override
        public void callbackStatus(int status) throws RemoteException {
            if (isNotifyCallback()) {
                for (IPlayerCallback adapter : sPlayerCallbackAdapters) {
                    adapter.callbackStatus(status);
                }
            }
        }
    }

    private boolean isNotifyCallback() {
        return sPlayerCallbackAdapters != null && sPlayerCallbackAdapters.size() != 0;
    }

    public static void addPlayerCallbackAdapter(IPlayerCallback adapter) {
        if (sPlayerCallbackAdapters == null) {
            sPlayerCallbackAdapters = new LinkedList<>();
        }
        sPlayerCallbackAdapters.add(adapter);
    }

    public static void removePlayerCallbackAdapter(IPlayerCallback adapter) {
        if (sPlayerCallbackAdapters == null || sPlayerCallbackAdapters.size() == 0) {
            return;
        }
        sPlayerCallbackAdapters.remove(adapter);
    }
}
