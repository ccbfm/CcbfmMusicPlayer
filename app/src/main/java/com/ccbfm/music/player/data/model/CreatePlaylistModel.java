package com.ccbfm.music.player.data.model;

import android.widget.ListView;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.ccbfm.music.player.BR;
import com.ccbfm.music.player.data.adapter.CreatePlaylistAdapter;
import com.ccbfm.music.player.database.SongLoader;
import com.ccbfm.music.player.database.entity.Playlist;
import com.ccbfm.music.player.database.entity.Song;
import com.ccbfm.music.player.tool.LogTools;

import java.util.List;

public class CreatePlaylistModel extends BaseObservable {
    private static final String TAG = "CreatePlaylistModel";
    private CreatePlaylistAdapter mCreatePlaylistAdapter;
    private ListView mListView;
    private List<Song> mSongList;
    private String mPlaylistName;
    private Playlist mOldPlaylist;

    public CreatePlaylistModel(ListView listView, Playlist oldPlaylist) {
        mListView = listView;
        mOldPlaylist = oldPlaylist;
        if(oldPlaylist != null){
            setPlaylistName(oldPlaylist.getName());
        }
        loadData(oldPlaylist);
    }

    private void loadData(final Playlist oldPlaylist) {
        LogTools.i(TAG, "loadData", "-------");
        List<Playlist> playlists = SongLoader.getSongData(new SongLoader.LoadSongCallBack() {
            @Override
            public void onPreExecute() {
                loadSongStart();
            }

            @Override
            public void onPostExecute(List<Playlist> playlists) {
                loadSongEnd(playlists, oldPlaylist);
            }

            @Override
            public void onCancelled() {

            }
        });
        if (playlists != null) {
            loadSongEnd(playlists, oldPlaylist);
        }
    }

    public boolean addOrUpdatePlaylist(){
        return SongLoader.addOrUpdatePlaylist(mOldPlaylist, getPlaylistName(),
                mCreatePlaylistAdapter.getSelectSongList());
    }

    private void loadSongStart() {

    }

    private void loadSongEnd(List<Playlist> playlists, Playlist oldPlaylist) {
        if (playlists != null && playlists.size() != 0) {
            mSongList = playlists.get(0).getSongList();
            List<Song> oldSongs = oldPlaylist != null ? oldPlaylist.getSongList() : null;
            if(mCreatePlaylistAdapter == null){
                CreatePlaylistAdapter adapter = new CreatePlaylistAdapter(mSongList, oldSongs);
                setCreatePlaylistAdapter(adapter);
            } else if(mListView != null){
                mCreatePlaylistAdapter.updateData(mSongList, oldSongs, mListView, 0);
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
