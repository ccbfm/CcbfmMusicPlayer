package com.ccbfm.music.player.control;

import com.ccbfm.music.player.IPlayerCallback;
import com.ccbfm.music.player.database.entity.Song;

import java.util.List;

public interface IControlPlayer {

    void setSongList(List<Song> songList, int position);

    void prepare(String path);

    void play();

    void pause();

    void stop();

    void previous();

    void next();

    boolean isPlaying();

    void seekTo(int msec);

    void mode(int mode);

    void release();

    Song getCurrentSong();

    void setPlayerCallback(IPlayerCallback callback);
}
