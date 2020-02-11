package com.ccbfm.music.player.data.model;

import com.ccbfm.music.player.data.adapter.SongListExpandableListAdapter;
import com.ccbfm.music.player.database.SongLoader;
import com.ccbfm.music.player.database.entity.Playlist;

import java.util.List;

public class SongListModel {

    private SongListExpandableListAdapter mAdapter;
    private CallBack mCallBack;

    public SongListModel() {
    }

    public SongListModel(CallBack callBack) {
        mCallBack = callBack;
    }

    public SongListExpandableListAdapter getAdapter() {
        return mAdapter;
    }

    public void setAdapter(SongListExpandableListAdapter adapter) {
        mAdapter = adapter;
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
        if(playlists != null){
            loadSongEnd(playlists);
        }
    }

    private void loadSongStart() {

    }

    @SuppressWarnings("unchecked")
    private void loadSongEnd(List<Playlist> playlists) {
        if(playlists != null && playlists.size() != 0){
            mAdapter.updatePlaylist(playlists);
            if(mCallBack != null){
                mCallBack.changeContent(playlists.get(0).getName());
            }
        }
    }

    public interface CallBack<T>{
        void changeContent(T groupItem);
    }
}
