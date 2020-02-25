package com.ccbfm.music.player.control;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.ccbfm.music.player.IPlayer;
import com.ccbfm.music.player.IPlayerCallback;
import com.ccbfm.music.player.database.entity.Song;
import com.ccbfm.music.player.service.MusicService;
import com.ccbfm.music.player.tool.SharedPreferencesTools;

import java.util.List;

public class MusicControl implements ControlConstants {
    private PlayHandler mHandler;
    private IPlayer mPlayer;

    private static final class Singleton {
        private static final MusicControl INSTANCE = new MusicControl();
    }

    public static MusicControl getInstance() {
        return Singleton.INSTANCE;
    }

    private MusicControl() {

    }

    public void initMusicService(final Context context){
        Intent intent = new Intent(context, MusicService.class);
        context.getApplicationContext().startService(intent);
        context.getApplicationContext().bindService(intent, new ServiceConnection(){
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mPlayer = IPlayer.Stub.asInterface(service);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mPlayer = null;
            }
        }, Context.BIND_IMPORTANT);

    }

    public void registerCallback(IPlayerCallback callback) {
        if(mPlayer != null){
            try {
                mPlayer.registerCallback(callback);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void unregisterCallback(IPlayerCallback callback) {
        if(mPlayer != null){
            try {
                mPlayer.unregisterCallback(callback);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void prepare(String path){
        sendMessage(STATUS_PREPARE, path);
    }

    public void setSongList(List<Song> songList, int index){
        sendMessage(createMessage(STATUS_SET_LIST, songList, index), 0);
    }

    public void play(){
        sendMessage(createMessage(STATUS_PLAY, null), 300);
    }

    public void pause(){
        sendMessage(STATUS_PAUSE, null);
    }


    public boolean isPlaying(){
        try {
            return mPlayer != null && mPlayer.isPlaying();
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    private void sendMessage(int what, Object obj){
        sendMessage(createMessage(what, obj), 0);
    }

    private void sendMessage(Message message, long delayMillis){
        if(mPlayer == null){
            Log.e("MusicControl", ">>>>>>(mPlayer == null)");
            return;
        }
        if(mHandler == null){
            mHandler = new PlayHandler(mPlayer);
        }
        mHandler.sendMessageDelayed(message, delayMillis);
    }

    private Message createMessage(int what, Object obj){
        Message message = Message.obtain();
        message.what = what;
        message.obj = obj;
        return message;
    }

    private Message createMessage(int what, Object obj, int arg1){
        Message message = Message.obtain();
        message.what = what;
        message.obj = obj;
        message.arg1 = arg1;
        return message;
    }

    private static class PlayHandler extends Handler {
        private IPlayer mPlayer;

        private PlayHandler(IPlayer player) {
            mPlayer = player;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(mPlayer == null){
                return;
            }
            try {
                switch (msg.what) {
                    case STATUS_IDLE:
                        break;
                    case STATUS_SET_LIST:
                        List<Song> songs = (List<Song>)msg.obj;
                        mPlayer.setSongList(songs, msg.arg1);
                        seekTo(msg.arg1);
                        break;
                    case STATUS_PREPARE:
                        String path = (String)msg.obj;
                        mPlayer.prepare(path);
                        break;
                    case STATUS_PLAY:
                        mPlayer.play();
                        break;
                    case STATUS_PAUSE:
                        mPlayer.pause();
                        break;
                    case STATUS_STOP:
                        mPlayer.stop();
                        break;
                    case STATUS_RELEASE:
                        mPlayer.release();
                        break;
                    case STATUS_SEEK:
                        mPlayer.seekTo(msg.arg1);
                        break;
                    case STATUS_PREVIOUS:
                        mPlayer.previous();
                        break;
                    case STATUS_NEXT:
                        mPlayer.next();
                        break;
                    case STATUS_MODE:
                        mPlayer.mode(msg.arg1);
                        break;

                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        private void seekTo(int newIndex){
            if(newIndex < 0){
                return;
            }
            int index = SharedPreferencesTools.getIntValue(SharedPreferencesTools.KEY_INIT_SONG_INDEX);
            if(newIndex == index) {
                int msec = SharedPreferencesTools.getIntValue(SharedPreferencesTools.KEY_INIT_SONG_MSEC);
                Message message = Message.obtain();
                message.what = STATUS_SEEK;
                message.arg1 = msec;
                sendMessage(message);
            }
        }
    }
}
