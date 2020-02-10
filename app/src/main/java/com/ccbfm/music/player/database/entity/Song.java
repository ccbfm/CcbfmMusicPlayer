package com.ccbfm.music.player.database.entity;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

public class Song extends LitePalSupport {

    @Column(nullable = false)
    private String songName;

    private String singerName;

    private String lyricist;

    private String composer;

    private long duration;

    private String songPath;

    private String lyricsPath;

    private String album;

    private int albumId;

    public Song() {
    }

    public Song(String songName, String singerName, String songPath) {
        this.songName = songName;
        this.singerName = singerName;
        this.songPath = songPath;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSingerName() {
        return singerName;
    }

    public void setSingerName(String singerName) {
        this.singerName = singerName;
    }

    public String getLyricist() {
        return lyricist;
    }

    public void setLyricist(String lyricist) {
        this.lyricist = lyricist;
    }

    public String getComposer() {
        return composer;
    }

    public void setComposer(String composer) {
        this.composer = composer;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getSongPath() {
        return songPath;
    }

    public void setSongPath(String songPath) {
        this.songPath = songPath;
    }

    public String getLyricsPath() {
        return lyricsPath;
    }

    public void setLyricsPath(String lyricsPath) {
        this.lyricsPath = lyricsPath;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }
}
