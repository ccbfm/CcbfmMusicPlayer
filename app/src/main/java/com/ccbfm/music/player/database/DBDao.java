package com.ccbfm.music.player.database;

import com.ccbfm.music.player.database.entity.Playlist;
import com.ccbfm.music.player.database.entity.Song;

import org.litepal.LitePal;

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
        return LitePal.order("orderId asc").find(Playlist.class, true);
    }

    public static int queryPlaylistCount(){
        return LitePal.count(Playlist.class);
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

    public static int deleteAllSong(String status){
        return LitePal.deleteAll(Song.class, "status=?", status);
    }

    public static int deleteAllPlaylist(){
        return LitePal.deleteAll(Playlist.class);
    }

}
