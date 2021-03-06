package com.ccbfm.music.player.control;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.ccbfm.music.player.App;
import com.ccbfm.music.player.IPlayer;
import com.ccbfm.music.player.database.entity.Song;
import com.ccbfm.music.player.service.MusicService;
import com.ccbfm.music.player.tool.Executors;
import com.ccbfm.music.player.tool.LogTools;
import com.ccbfm.music.player.tool.SPTools;

import java.util.List;

public class MusicControl implements ControlConstants {
    private static final String TAG = "MusicControl";
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

    public void initMusicService(final Context context, final Message message) {
        Intent intent = new Intent(context.getApplicationContext(), MusicService.class);
        ContextCompat.startForegroundService(context, intent);
        context.getApplicationContext().bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mPlayer = IPlayer.Stub.asInterface(service);
                LogTools.d(TAG, "onServiceConnected", "mPlayer=" + mPlayer);
                if(mHandler != null){
                    mHandler.setPlayer(mPlayer);
                }
                if(message != null) {
                    sendMessage(message, 0);
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mPlayer = null;
                LogTools.d(TAG, "onServiceDisconnected", "---");
            }
        }, Context.BIND_IMPORTANT);

    }

    public void prepare(String path) {
        sendMessage(STATUS_PREPARE, path);
    }

    public void setSongList(List<Song> songList, int index) {
        setSongList(songList, index, true);
    }

    public void setSongList(List<Song> songList, int index, boolean isPlay) {
        sendMessage(createMessage(STATUS_SET_LIST, songList, index, (isPlay ? 1 : 0)), 300);
    }

    public void play() {
        sendMessage(createMessage(STATUS_PLAY, null), 300);
    }

    public void pause() {
        sendMessage(STATUS_PAUSE, null);
    }

    public void seekTo(int msec) {
        sendMessage(createMessage(STATUS_SEEK, null, msec, 1), 300);
    }

    public void previous() {
        sendMessage(STATUS_PREVIOUS, null);
    }

    public void next() {
        sendMessage(STATUS_NEXT, null);
    }

    public void mode() {
        sendMessage(STATUS_MODE, null);
    }

    public void release() {
        sendMessage(STATUS_RELEASE, null);
    }

    public boolean isPlaying() {
        try {
            return mPlayer != null && mPlayer.isPlaying();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void sendMessage(int what, Object obj) {
        sendMessage(createMessage(what, obj), 0);
    }

    private void sendMessage(Message message, long delayMillis) {
        if (mPlayer == null) {
            LogTools.e(TAG, "sendMessage", ">>>>>>(mPlayer == null)", new Throwable());
            initMusicService(App.getApp(), message);
            return;
        }
        if (mHandler == null) {
            mHandler = new PlayHandler(Executors.getHandlerLooper(), mPlayer);
        }
        mHandler.removeMessages(message.what);
        mHandler.sendMessageDelayed(message, (delayMillis == 0 ? 100 : delayMillis));
    }

    private Message createMessage(int what, Object obj) {
        Message message = Message.obtain();
        message.what = what;
        message.obj = obj;
        return message;
    }

    private Message createMessage(int what, Object obj, int arg1, int arg2) {
        Message message = Message.obtain();
        message.what = what;
        message.obj = obj;
        message.arg1 = arg1;
        message.arg2 = arg2;
        return message;
    }

    private static class PlayHandler extends Handler {
        private IPlayer mPlayer;

        public PlayHandler(@NonNull Looper looper, IPlayer player) {
            super(looper);
            mPlayer = player;
        }

        private PlayHandler(IPlayer player) {
            mPlayer = player;
        }

        public void setPlayer(IPlayer player) {
            mPlayer = player;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            LogTools.i(TAG, "handleMessage", "msg.what=" + msg.what + ",mPlayer=" + mPlayer);
            if (mPlayer == null) {
                return;
            }
            try {
                switch (msg.what) {
                    case STATUS_IDLE:
                        break;
                    case STATUS_SET_LIST:
                        seekTo(msg.arg1);
                        List<Song> songs = (List<Song>) msg.obj;
                        mPlayer.setSongList(songs, msg.arg1, (msg.arg2 == 1));
                        sendEmptyMessage(STATUS_MODE);
                        break;
                    case STATUS_PREPARE:
                        String path = (String) msg.obj;
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
                        mPlayer.seekTo(msg.arg1, msg.arg2 == 1);
                        break;
                    case STATUS_PREVIOUS:
                        mPlayer.previous();
                        break;
                    case STATUS_NEXT:
                        mPlayer.next(true);
                        break;
                    case STATUS_MODE:
                        mPlayer.mode(SPTools.getIntValue(SPTools.KEY_INIT_PLAY_MODE));
                        break;

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void seekTo(int newIndex) {
            if (newIndex < 0) {
                return;
            }
            int need = SPTools.getIntValue(SPTools.KEY_NEED_SEEK_TO);
            if (need == 1) {
                SPTools.putIntValue(SPTools.KEY_NEED_SEEK_TO, 0);
                return;
            }
            int index = SPTools.getIntValue(SPTools.KEY_INIT_SONG_INDEX);
            if (newIndex == index) {
                int msec = SPTools.getIntValue(SPTools.KEY_INIT_SONG_MSEC);
                Message message = Message.obtain();
                message.what = STATUS_SEEK;
                message.arg1 = msec;
                message.arg2 = 0;
                sendMessage(message);
            }
        }
    }
}
