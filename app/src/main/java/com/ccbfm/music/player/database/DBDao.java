package com.ccbfm.music.player.database;

import com.ccbfm.music.player.database.entity.Playlist;
import com.ccbfm.music.player.database.entity.Song;

import org.litepal.LitePal;

import java.util.List;

public final class DBDao {

    //query

    public static List<Song> queryAllSong(){
        return LitePal.findAll(Song.class);
    }

    public static List<Playlist> queryAllPlaylist(){
        return LitePal.findAll(Playlist.class);
    }


    //delete
    public static void deleteSongById(long id){
        LitePal.delete(Song.class, id);
    }

    public static void deletePlaylistById(long id){
        LitePal.delete(Playlist.class, id);
    }

    public static void deleteAllSong(){
        LitePal.deleteAll(Song.class);
    }

    public static void deleteAllPlaylist(){
        LitePal.deleteAll(Playlist.class);
    }

}
