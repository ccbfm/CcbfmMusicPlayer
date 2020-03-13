package com.ccbfm.music.player.data.model;

import android.util.Log;

import com.ccbfm.music.player.data.adapter.SongListExpandableListAdapter;
import com.ccbfm.music.player.database.SongLoader;
import com.ccbfm.music.player.database.entity.Playlist;
import com.ccbfm.music.player.ui.fragment.BaseFragment;

import java.util.List;

public class SongListModel {

    private SongListExpandableListAdapter mAdapter;
    private CallBack mCallBack;
    private BaseFragment mFragment;

    public SongListModel() {
    }

    public SongListModel(BaseFragment fragment, CallBack callBack) {
        mFragment = fragment;
        mCallBack = callBack;
    }

    public SongListExpandableListAdapter getAdapter() {
        return mAdapter;
    }

    public void setAdapter(SongListExpandableListAdapter adapter) {
        mAdapter = adapter;
        loadData();
    }

    public void loadData() {
        Log.i("SongListModel", "loadData--->---");
        List<Playlist> playlists = SongLoader.getSongData(new SongLoader.LoadSongCallBack() {
            @Override
            public void onPreExecute() {
                loadSongStart();
            }

            @Override
            public void onPostExecute(List<Playlist> playlists) {
                loadSongEnd(playlists);
            }

            @Override
            public void onCancelled() {

            }
        });
        if (playlists != null) {
            loadSongEnd(playlists);
        }
    }

    private void loadSongStart() {

    }

    @SuppressWarnings("unchecked")
    private void loadSongEnd(List<Playlist> playlists) {
        if (playlists != null && playlists.size() != 0) {
            mAdapter.updatePlaylist(playlists);
            if (mCallBack != null) {
                Playlist playlist = playlists.get(0);
                String content = playlist.getName() + "(" + playlist.getSongList().size() + "个)";
                mCallBack.changeContent(content);
            }
        }
    }

    public interface CallBack<T> {
        void changeContent(T groupItem);
    }
}
