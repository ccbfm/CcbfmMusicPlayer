package com.ccbfm.music.player.ui.fragment;

import android.view.View;

import com.ccbfm.music.player.R;
import com.ccbfm.music.player.control.MusicControl;
import com.ccbfm.music.player.database.SongLoader;
import com.ccbfm.music.player.database.entity.Playlist;
import com.ccbfm.music.player.database.entity.Song;
import com.ccbfm.music.player.databinding.FragmentControlBinding;
import com.ccbfm.music.player.tool.SharedPreferencesTools;

import java.util.List;

public class ControlFragment extends BaseFragment<FragmentControlBinding> {

    private List<Playlist> mPlaylists;
    private List<Song> mSongList;
    private Song mCurrentSong;

    @Override
    protected void initView(final FragmentControlBinding binding) {
        List<Playlist> playlists = SongLoader.getSongData(new SongLoader.LoadSongCallBack() {
            @Override
            public void onPreExecute() {

            }

            @Override
            public void onPostExecute(List<Playlist> playlists) {
                initData(binding, playlists);
            }

            @Override
            public void onCancelled() {

            }
        });
        if(playlists != null){
            initData(binding, playlists);
        }

        binding.musicControlPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!MusicControl.getInstance().isPlaying()){
                    MusicControl.getInstance().prepare(mCurrentSong.getSongPath());
                    MusicControl.getInstance().play();
                } else {
                    MusicControl.getInstance().pause();
                }
            }
        });
    }

    private void initData(FragmentControlBinding binding, List<Playlist> playlists){
        mPlaylists = playlists;
        int playlistIndex = SharedPreferencesTools.getIntValue(SharedPreferencesTools.KEY_INIT_PLAYLIST_INDEX);
        int songIndex = SharedPreferencesTools.getIntValue(SharedPreferencesTools.KEY_INIT_SONG_INDEX);
        int playlistSize = playlists.size();
        if(playlistIndex >= 0 && playlistIndex < playlistSize){
            List<Song> songList = playlists.get(playlistIndex).getSongList();
            if(songList != null) {
                mSongList = songList;
                int songListSize = songList.size();
                if (songIndex >= 0 && songIndex < songListSize) {
                    Song song = songList.get(songIndex);
                    mCurrentSong = song;
                    binding.musicControlTitle.setText(song.getSongName());
                    binding.musicControlSinger.setText(song.getSingerName());
                }
            }
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_control;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
