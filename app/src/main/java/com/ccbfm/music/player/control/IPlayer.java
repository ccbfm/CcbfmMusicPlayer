package com.ccbfm.music.player.control;

public interface IPlayer {

    void prepare(String path);

    void play();

    void pause();

    void stop();

    void release();

    boolean isPlaying();

    void seekTo(int position);
}
