package com.ccbfm.music.player.control;

import android.media.MediaPlayer;
import android.util.Log;

public class MusicPlayer implements IPlayer {

    private MediaPlayer mPlayer;

    public MusicPlayer() {
        initPlayer();
    }

    private void initPlayer(){
        if(mPlayer != null){
            return;
        }
        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

            }
        });
        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

            }
        });
    }

    @Override
    public void prepare(String path) {
        initPlayer();

        if(mPlayer != null){
            try {
                mPlayer.reset();
                mPlayer.setDataSource(path);
                mPlayer.prepareAsync();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void play() {
        if(mPlayer != null && !isPlaying()){
            mPlayer.start();
        }
    }

    @Override
    public void stop() {
        if(mPlayer != null && isPlaying()) {
            mPlayer.stop();
        }
    }

    @Override
    public void release() {
        if(mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
    }

    @Override
    public void pause() {
        if(mPlayer != null && isPlaying()) {
            mPlayer.pause();
        }
    }

    @Override
    public void seekTo(int position) {
        if(mPlayer != null) {
            mPlayer.seekTo(position);
        }
    }

    @Override
    public boolean isPlaying() {
        if(mPlayer != null) {
            Log.w("wds", "getCurrentPosition=" + mPlayer.getCurrentPosition()+","+mPlayer.getDuration());
        }
        return mPlayer != null && mPlayer.isPlaying();
    }
}
