package com.ccbfm.music.player.data.model;

import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.ccbfm.music.player.BR;
import com.ccbfm.music.player.R;
import com.ccbfm.music.player.data.adapter.CreatePlaylistAdapter;
import com.ccbfm.music.player.database.SongLoader;
import com.ccbfm.music.player.database.entity.Playlist;
import com.ccbfm.music.player.database.entity.Song;
import com.ccbfm.music.player.tool.LogTools;

import java.util.LinkedList;
import java.util.List;

public class CreatePlaylistModel extends BaseObservable {
    private static final String TAG = "CreatePlaylistModel";
    private CreatePlaylistAdapter mCreatePlaylistAdapter;
    private ListView mListView;
    private List<Song> mSongList;
    private String mPlaylistName;

    public CreatePlaylistModel(ListView listView) {
        mListView = listView;
        loadData();
    }

    private void loadData() {
        LogTools.i(TAG, "loadData", "-------");
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

    public boolean addPlaylist(){
        return SongLoader.addPlaylist(getPlaylistName(), mCreatePlaylistAdapter.getSelectSongList());
    }

    private void loadSongStart() {

    }

    private void loadSongEnd(List<Playlist> playlists) {
        if (playlists != null && playlists.size() != 0) {
            mSongList = playlists.get(0).getSongList();
            if(mCreatePlaylistAdapter == null){
                CreatePlaylistAdapter adapter = new CreatePlaylistAdapter(mSongList);
                setCreatePlaylistAdapter(adapter);
            } else if(mListView != null){
                mCreatePlaylistAdapter.updateData(mSongList, mListView, 0);
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

    @Bindable
    public String getPlaylistName() {
        return mPlaylistName;
    }

    public void setPlaylistName(String playlistName) {
        mPlaylistName = playlistName;
        notifyPropertyChanged(BR.playlistName);
    }
}
