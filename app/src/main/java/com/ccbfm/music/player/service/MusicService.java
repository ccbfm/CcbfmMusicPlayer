package com.ccbfm.music.player.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.text.TextUtils;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;

import com.ccbfm.music.player.IPlayer;
import com.ccbfm.music.player.IPlayerCallback;
import com.ccbfm.music.player.control.IControlPlayer;
import com.ccbfm.music.player.control.MusicPlayer;
import com.ccbfm.music.player.database.entity.Song;
import com.ccbfm.music.player.tool.MusicNotificationTool;

import java.util.List;

public class MusicService extends Service {

    private PlayerBinder mBinder;
    private Notification mNotification;
    private RemoteViews mRemoteViews;

    @Override
    public void onCreate() {
        mRemoteViews = MusicNotificationTool.createMusicView(getApplicationContext());
        mNotification = MusicNotificationTool.createNotification(getApplicationContext(), mRemoteViews);
        //startForeground(MusicNotificationTool.NOTIFY_ID_MUSIC, mNotification);
        mBinder = new PlayerBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final String action = intent.getAction();
        if (mBinder != null && !TextUtils.isEmpty(action)) {
            try {
                switch (action) {
                    case MusicNotificationTool.ACTION_PREVIOUS:
                        mBinder.previous();
                        break;
                    case MusicNotificationTool.ACTION_PLAY:
                        if (mBinder.isPlaying()) {
                            mBinder.pause();
                        } else {
                            mBinder.play();
                        }
                        break;
                    case MusicNotificationTool.ACTION_NEXT:
                        mBinder.next();
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    public class PlayerBinder extends IPlayer.Stub {
        private IControlPlayer mControlPlayer;
        private RemoteCallbackList<IPlayerCallback> mCallbackList;

        private PlayerBinder() {
            mCallbackList = new RemoteCallbackList<>();
            mControlPlayer = new MusicPlayer(mCallbackList, mCallback);
        }

        @Override
        public void setSongList(List<Song> songList, int index) throws RemoteException {
            mControlPlayer.setSongList(songList, index);
        }

        @Override
        public void prepare(String path) throws RemoteException {
            mControlPlayer.prepare(path);
        }

        @Override
        public void play() throws RemoteException {
            mControlPlayer.play();
        }

        @Override
        public void pause() throws RemoteException {
            mControlPlayer.pause();
        }

        @Override
        public void stop() throws RemoteException {
            mControlPlayer.stop();
        }

        @Override
        public void previous() throws RemoteException {
            mControlPlayer.previous();
        }

        @Override
        public void next() throws RemoteException {
            mControlPlayer.next();
        }

        @Override
        public boolean isPlaying() throws RemoteException {
            return mControlPlayer.isPlaying();
        }

        @Override
        public void seekTo(int msec) throws RemoteException {
            mControlPlayer.seekTo(msec);
        }

        @Override
        public void mode(int mode) throws RemoteException {
            mControlPlayer.mode(mode);
        }

        @Override
        public void release() throws RemoteException {
            mControlPlayer.release();
        }

        @Override
        public void registerCallback(IPlayerCallback callback) throws RemoteException {
            if (mCallbackList != null) {
                mCallbackList.register(callback);
            }
        }

        @Override
        public void unregisterCallback(IPlayerCallback callback) throws RemoteException {
            if (mCallbackList != null) {
                mCallbackList.unregister(callback);
            }
        }

    }

    private NotificationCallback mCallback = new NotificationCallback() {
        @Override
        public void changeDisplay(Song song, boolean isPlaying) {
            showNotification(song, isPlaying);
        }
    };

    public void showNotification(Song song, boolean isPlaying) {
        if (song != null) {
            MusicNotificationTool.showNotification(this, mNotification, mRemoteViews,
                    MusicNotificationTool.buildTitle(song.getSongName(),
                            "  --  ", song.getSingerName()), isPlaying);
        }
    }

    public interface NotificationCallback {
        void changeDisplay(Song song, boolean isPlaying);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            mBinder.release();
        } catch (Exception ignore) {
        }
    }
}
