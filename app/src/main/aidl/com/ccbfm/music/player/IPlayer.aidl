// IPlayer.aidl
package com.ccbfm.music.player;

// Declare any non-default types here with import statements
import com.ccbfm.music.player.database.entity.Song;
import com.ccbfm.music.player.IPlayerCallback;
import java.util.List;

interface IPlayer {

    void setSongList(in List<Song> songList, in int index, boolean isPlay);

    void prepare(in String path);

    void play();

    void pause();

    void stop();

    void previous();

    void next(boolean flag);

    boolean isPlaying();

    void seekTo(int msec, boolean isPlay);

    void mode(int mode);

    void release();

}
