package com.ccbfm.music.player.database;

import com.ccbfm.music.player.database.entity.Playlist;
import com.ccbfm.music.player.database.entity.Song;

import org.litepal.LitePal;
import org.litepal.crud.async.UpdateOrDeleteExecutor;
import org.litepal.crud.callback.UpdateOrDeleteCallback;

import java.util.List;

public final class DBDao {

    //query
    public static List<Song> queryAllSong(){
        return queryAllSong("0");
    }
    public static List<Song> queryAllSong(String status){
        return LitePal.where("status=?", status).find(Song.class);
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

    public static UpdateOrDeleteExecutor deleteAllSongAsync(String status){
        return LitePal.deleteAllAsync(Song.class, "status=?", status);
    }

    public static void deleteAllPlaylist(){
        LitePal.deleteAll(Playlist.class);
    }

}
