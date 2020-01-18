package com.ccbfm.music.player.database.entity;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

public class Playlist extends LitePalSupport {

    @Column(nullable = false, defaultValue = "新建列表")
    private String name;

    private String description;

    private long[] songId;

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

    public long[] getSongId() {
        return songId;
    }

    public void setSongId(long[] songId) {
        this.songId = songId;
    }
}
