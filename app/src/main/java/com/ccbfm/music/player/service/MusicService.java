package com.ccbfm.music.player.service;

import android.app.Notification;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
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
    private IPlayerCallback mPlayerCallback;
    private IControlPlayer mControlPlayer;

    @Override
    public void onCreate() {
        mRemoteViews = MusicNotificationTool.createMusicView(getApplicationContext());
        mNotification = MusicNotificationTool.createNotification(getApplicationContext(), mRemoteViews);
        //startForeground(MusicNotificationTool.NOTIFY_ID_MUSIC, mNotification);
        mControlPlayer = new MusicPlayer(mCallback);
        mBinder = new PlayerBinder(mControlPlayer);

        Intent intent = new Intent(this, LocalService.class);
        getApplicationContext().startService(intent);
        getApplicationContext().bindService(intent, new ServiceConnection(){
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mPlayerCallback = IPlayerCallback.Stub.asInterface(service);
                mControlPlayer.setPlayerCallback(mPlayerCallback);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mPlayerCallback = null;
                mControlPlayer.setPlayerCallback(null);
            }
        }, Context.BIND_IMPORTANT);
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
                    case MusicNotificationTool.ACTION_CLOSE:
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


        private PlayerBinder(IControlPlayer controlPlayer) {
            mControlPlayer = controlPlayer;
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
                            "  -  ", song.getSingerName()), isPlaying);
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
