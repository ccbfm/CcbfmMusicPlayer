package com.ccbfm.music.player.control;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

public class MusicControl implements ControlConstants {
    private PlayHandler mHandler;

    private static final class Singleton {
        private static final MusicControl INSTANCE = new MusicControl();
    }

    public static MusicControl getInstance() {
        return Singleton.INSTANCE;
    }

    private MusicControl() {
        mHandler = new PlayHandler();
    }

    public void prepare(String path){
        sendMessage(STATUS_PREPARE, path);
    }

    public void play(){
        sendMessage(STATUS_PLAY, null, 300);
    }

    public void pause(){
        sendMessage(STATUS_PAUSE, null);
    }

    public int getCurrentStatus() {
        return mHandler.getCurrentStatus();
    }

    public boolean isPlaying(){
        return getCurrentStatus() == STATUS_PLAY;
    }

    private void sendMessage(int what, Object obj){
        sendMessage(what, obj, 0);
    }

    private void sendMessage(int what, Object obj, long delayMillis){
        Message message = Message.obtain();
        message.what = what;
        message.obj = obj;
        mHandler.sendMessageDelayed(message, delayMillis);
    }

    private static class PlayHandler extends Handler {
        private IPlayer mIPlayer;
        private int mCurrentStatus = STATUS_IDLE;

        private PlayHandler() {
            mIPlayer = new MusicPlayer();
        }

        public int getCurrentStatus() {
            return mCurrentStatus;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            mCurrentStatus = msg.what;
            switch (msg.what) {
                case STATUS_IDLE:
                    break;
                case STATUS_PREPARE:
                    String path = (String)msg.obj;
                    mIPlayer.prepare(path);
                    break;
                case STATUS_PLAY:
                    mIPlayer.play();
                    break;
                case STATUS_PAUSE:
                    mIPlayer.pause();
                    break;
                case STATUS_STOP:
                    mIPlayer.stop();
                    break;
                case STATUS_RELEASE:
                    mIPlayer.release();
                    break;
                case STATUS_SEEK:
                    int position = (int)msg.obj;
                    mIPlayer.seekTo(position);
                    break;
            }
        }
    }
}
