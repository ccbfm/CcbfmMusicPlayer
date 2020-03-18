package com.ccbfm.music.player.database.entity;

import android.os.Parcel;
import android.os.Parcelable;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.util.List;
import java.util.Objects;

public class Song extends LitePalSupport implements Parcelable {
    private long id;

    @Column(nullable = false, unique = true)
    private String songName;

    private String singerName;

    private String lyricist;

    private String composer;

    private long duration;

    private String songPath;

    private String lyricsPath;

    private String album;

    private long albumId;

    private int status = 0;

    private List<Playlist> playlistList;

    public Song() {
    }

    public Song(String songName, String singerName, String songPath) {
        this.songName = songName;
        this.singerName = singerName;
        this.songPath = songPath;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<Playlist> getPlaylistList() {
        return playlistList;
    }

    public void setPlaylistList(List<Playlist> playlistList) {
        this.playlistList = playlistList;
    }

    @Override
    public int describeContents() {
        return 0;
    }


    public Song(long id, String songName, String singerName, String lyricist, String composer,
                long duration, String songPath, String lyricsPath, String album, long albumId) {
        this.id = id;
        this.songName = songName;
        this.singerName = singerName;
        this.lyricist = lyricist;
        this.composer = composer;
        this.duration = duration;
        this.songPath = songPath;
        this.lyricsPath = lyricsPath;
        this.album = album;
        this.albumId = albumId;
    }

    private Song(Parcel in) {
        this(in.readLong(),
                in.readString(),
                in.readString(),
                in.readString(),
                in.readString(),
                in.readLong(),
                in.readString(),
                in.readString(),
                in.readString(),
                in.readLong());
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(id);
        out.writeString(songName);
        out.writeString(singerName);
        out.writeString(lyricist);
        out.writeString(composer);
        out.writeLong(duration);
        out.writeString(songPath);
        out.writeString(lyricsPath);
        out.writeString(album);
        out.writeLong(albumId);
    }

    public static Parcelable.Creator<Song> CREATOR = new Parcelable.Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel source) {
            return new Song(source);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    @Override
    public String toString() {
        return "Song{" +
                "songName='" + songName + '\'' +
                ", singerName='" + singerName + '\'' +
                ", lyricist='" + lyricist + '\'' +
                ", composer='" + composer + '\'' +
                ", duration=" + duration +
                ", songPath='" + songPath + '\'' +
                ", lyricsPath='" + lyricsPath + '\'' +
                ", album='" + album + '\'' +
                ", albumId=" + albumId +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Song song = (Song) o;
        return Objects.equals(songPath, song.songPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(songPath);
    }
}
