package com.ccbfm.music.player.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;

import androidx.annotation.Nullable;

import com.ccbfm.music.player.IPlayer;
import com.ccbfm.music.player.IPlayerCallback;
import com.ccbfm.music.player.control.IControlPlayer;
import com.ccbfm.music.player.control.MusicPlayer;
import com.ccbfm.music.player.database.entity.Song;

import java.util.List;

public class MusicService extends Service {

    private PlayerBinder mBinder;
    private IControlPlayer mControlPlayer;
    private RemoteCallbackList<IPlayerCallback> mCallbackList;

    @Override
    public void onCreate() {
        mCallbackList = new RemoteCallbackList<>();
        mControlPlayer = new MusicPlayer(mCallbackList);
        mBinder = new PlayerBinder(mControlPlayer);
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

        @Override
        public void registerCallback(IPlayerCallback callback) throws RemoteException {
            if(mCallbackList != null){
                mCallbackList.register(callback);
            }
        }

        @Override
        public void unregisterCallback(IPlayerCallback callback) throws RemoteException {
            if(mCallbackList != null){
                mCallbackList.unregister(callback);
            }
        }
    }
}
