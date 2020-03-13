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

import java.util.LinkedList;
import java.util.List;

public class CreatePlaylistModel extends BaseObservable {

    private CreatePlaylistAdapter mCreatePlaylistAdapter;
    private ListView mListView;
    private List<Song> mSongList;
    private String mPlaylistName;

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

    private List<Song> getSelectSong(){
        LinkedList<Song> songs = new LinkedList<>();
        ListView listView = mListView;
        List<Song> songList = mSongList;
        if(listView != null){
            int count = listView.getChildCount();
            for (int i = 0; i < count; i++) {
                View view = listView.getChildAt(i);
                CheckBox checkView = (CheckBox) view.findViewById(R.id.music_create_playlist_check);
                if(checkView != null && checkView.isChecked()){
                    songs.add(songList.get(i));
                }
            }
        }
        return songs;
    }

    public boolean addPlaylist(){
        return SongLoader.addPlaylist(getPlaylistName(), getSelectSong());
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
