package com.ccbfm.music.player.data.model;

import android.os.AsyncTask;

import com.ccbfm.music.player.data.adapter.SongListExpandableListAdapter;
import com.ccbfm.music.player.database.SongLoader;
import com.ccbfm.music.player.database.entity.Playlist;

import java.util.List;

public class SongListModel {

    private SongListExpandableListAdapter mAdapter;

    public SongListExpandableListAdapter getAdapter() {
        return mAdapter;
    }

    public void setAdapter(SongListExpandableListAdapter adapter) {
        mAdapter = adapter;
        new LoadDBSong(this).execute();
    }

    private void loadSongStart() {

    }

    private void loadSongEnd(List<Playlist> playlists) {
        if(playlists != null && playlists.size() != 0){
            mAdapter.updatePlaylist(playlists);
        }
    }

    private static class LoadDBSong extends AsyncTask<Void, Integer, List<Playlist>> {
        private SongListModel mSongListModel;

        private LoadDBSong(SongListModel songListModel) {
            mSongListModel = songListModel;
        }

        @Override
        protected List<Playlist> doInBackground(Void... voids) {
            return SongLoader.loadDBSong();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mSongListModel.loadSongStart();
        }

        @Override
        protected void onPostExecute(List<Playlist> playlists) {
            super.onPostExecute(playlists);
            mSongListModel.loadSongEnd(playlists);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
}
