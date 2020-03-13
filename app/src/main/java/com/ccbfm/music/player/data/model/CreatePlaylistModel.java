package com.ccbfm.music.player.data.model;

import android.util.Log;
import android.widget.ListView;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.ccbfm.music.player.BR;
import com.ccbfm.music.player.data.adapter.CreatePlaylistAdapter;
import com.ccbfm.music.player.database.SongLoader;
import com.ccbfm.music.player.database.entity.Playlist;

import java.util.List;

public class CreatePlaylistModel extends BaseObservable {

    private CreatePlaylistAdapter mCreatePlaylistAdapter;
    private ListView mListView;

    public CreatePlaylistModel(ListView listView) {
        mListView = listView;
        loadData();
    }

    private void loadData() {
        Log.i("CreatePlaylistModel", "loadData--->---");
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

    private void loadSongEnd(List<Playlist> playlists) {
        if (playlists != null && playlists.size() != 0) {
            if(mCreatePlaylistAdapter == null){
                CreatePlaylistAdapter adapter = new CreatePlaylistAdapter(playlists.get(0).getSongList());
                setCreatePlaylistAdapter(adapter);
            } else if(mListView != null){
                mCreatePlaylistAdapter.updateData(playlists.get(0).getSongList(), mListView, 0);
            }
        }
    }

    @Bindable
    public CreatePlaylistAdapter getCreatePlaylistAdapter() {
        return mCreatePlaylistAdapter;
    }

    public void setCreatePlaylistAdapter(CreatePlaylistAdapter createPlaylistAdapter) {
        mCreatePlaylistAdapter = createPlaylistAdapter;
        notifyPropertyChanged(BR.createPlaylistAdapter);
    }
}
