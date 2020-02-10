package com.ccbfm.music.player.data.model;

import com.ccbfm.music.player.data.adapter.SongListExpandableListAdapter;

public class SongListModel {

    private SongListExpandableListAdapter mAdapter;

    public SongListExpandableListAdapter getAdapter() {
        return mAdapter;
    }

    public void setAdapter(SongListExpandableListAdapter adapter) {
        mAdapter = adapter;
    }


}
