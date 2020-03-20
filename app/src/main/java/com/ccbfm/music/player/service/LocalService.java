package com.ccbfm.music.player.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;

import androidx.annotation.Nullable;

import com.ccbfm.music.player.IPlayerCallback;
import com.ccbfm.music.player.callback.PlayerCallbackAdapter;
import com.ccbfm.music.player.control.PlayerErrorCode;
import com.ccbfm.music.player.database.entity.Song;
import com.ccbfm.music.player.tool.Executors;
import com.ccbfm.music.player.tool.LogTools;
import com.ccbfm.music.player.tool.SPTools;
import com.ccbfm.music.player.tool.ToastTools;

import java.util.LinkedList;
import java.util.List;

import static com.ccbfm.music.player.tool.SPTools.KEY_INIT_SONG_INDEX;
import static com.ccbfm.music.player.tool.SPTools.KEY_INIT_SONG_MSEC;

public class LocalService extends Service {
    private static final String TAG = "LocalService";
    private LocalBinder mBinder;
    private static List<PlayerCallbackAdapter> sPlayerCallbackAdapters;
    private Handler mHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        mBinder = new LocalBinder();
        mHandler = new Handler(Executors.getHandlerLooper());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private class LocalBinder extends IPlayerCallback.Stub {
        @Override
        public void callbackIndex(final int index) throws RemoteException {
            LogTools.d(TAG, "callbackIndex", "index=" + index);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    SPTools.putIntValue(KEY_INIT_SONG_INDEX, index);
                    if (isNotifyCallback()) {
                        for (PlayerCallbackAdapter adapter : sPlayerCallbackAdapters) {
                            adapter.callbackIndex(index);
                        }
                    }
                }
            });
        }

        @Override
        public void callbackMsec(final int msec) throws RemoteException {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    SPTools.putIntValue(KEY_INIT_SONG_MSEC, msec);
                    if (isNotifyCallback()) {
                        for (PlayerCallbackAdapter adapter : sPlayerCallbackAdapters) {
                            adapter.callbackMsec(msec);
                        }
                    }
                }
            });
        }

        @Override
        public void callbackError(final int code, final Song song) throws RemoteException {
            LogTools.d(TAG, "callbackError", "code=" + code);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (isNotifyCallback()) {
                        for (PlayerCallbackAdapter adapter : sPlayerCallbackAdapters) {
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
            });
        }

        @Override
        public void callbackStatus(final int status) throws RemoteException {
            LogTools.d(TAG, "callbackStatus", "status=" + status);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (isNotifyCallback()) {
                        for (PlayerCallbackAdapter adapter : sPlayerCallbackAdapters) {
                            adapter.callbackStatus(status);
                        }
                    }
                }
            });
        }
    }

    private boolean isNotifyCallback() {
        return sPlayerCallbackAdapters != null && sPlayerCallbackAdapters.size() != 0;
    }

    public static void addPlayerCallbackAdapter(PlayerCallbackAdapter adapter) {
        if (sPlayerCallbackAdapters == null) {
            sPlayerCallbackAdapters = new LinkedList<>();
        }
        sPlayerCallbackAdapters.add(adapter);
    }

    public static void removePlayerCallbackAdapter(PlayerCallbackAdapter adapter) {
        if (sPlayerCallbackAdapters == null || sPlayerCallbackAdapters.size() == 0) {
            return;
        }
        sPlayerCallbackAdapters.remove(adapter);
    }
}
