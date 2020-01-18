package com.ccbfm.music.player.data.entity;

public class SongInfo {

    private String songName;
    private String singerName;
    private String lyricist;
    private String composer;
    private long duration;
    private String songPath;

    public SongInfo() {
    }

    public SongInfo(String songName, long duration, String songPath) {
        this.songName = songName;
        this.duration = duration;
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
}
