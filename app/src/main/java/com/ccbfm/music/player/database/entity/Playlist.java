package com.ccbfm.music.player.database.entity;

import android.os.Parcel;
import android.os.Parcelable;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.util.List;

public class Playlist extends LitePalSupport implements Parcelable {

    @Column(nullable = false, defaultValue = "新建列表")
    private String name;

    private String description;

    private List<Song> songList;

    public Playlist() {
    }

    public Playlist(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Song> getSongList() {
        return songList;
    }

    public void setSongList(List<Song> songList) {
        this.songList = songList;
    }

    private Playlist(Parcel in){
        this.name = in.readString();
        this.description = in.readString();
        this.songList = in.createTypedArrayList(Song.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.description);
        dest.writeTypedList(this.songList);
    }

    public static Parcelable.Creator<Playlist> CREATOR = new Parcelable.Creator<Playlist>(){
        @Override
        public Playlist createFromParcel(Parcel source) {
            return new Playlist(source);
        }

        @Override
        public Playlist[] newArray(int size) {
            return new Playlist[size];
        }
    };
}
