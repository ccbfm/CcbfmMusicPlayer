package com.ccbfm.music.player.ui.fragment;

import android.view.View;
import android.widget.TextView;

import androidx.lifecycle.Observer;

import com.ccbfm.music.player.IPlayerCallback;
import com.ccbfm.music.player.R;
import com.ccbfm.music.player.callback.PlayerCallbackAdapter;
import com.ccbfm.music.player.control.MusicControl;
import com.ccbfm.music.player.database.SongLoader;
import com.ccbfm.music.player.database.entity.Playlist;
import com.ccbfm.music.player.database.entity.Song;
import com.ccbfm.music.player.databinding.FragmentControlBinding;
import com.ccbfm.music.player.service.LocalService;
import com.ccbfm.music.player.tool.Constants;
import com.ccbfm.music.player.tool.LiveDataBus;
import com.ccbfm.music.player.tool.LogTools;
import com.ccbfm.music.player.tool.MathTools;
import com.ccbfm.music.player.tool.SPTools;

import java.util.List;

public class ControlFragment extends BaseFragment<FragmentControlBinding> {
    private static final String TAG = "ControlFragment";
    private List<Playlist> mPlaylists;
    private List<Song> mSongList;
    private int mSongIndex;
    private TextView mControlTitle;
    private TextView mControlSinger;

    private IPlayerCallback mPlayerCallback = new PlayerCallbackAdapter() {
        @Override
        public void callbackIndex(int index) {
            mSongIndex = index;
            int playlistIndex = SPTools.getIntValue(SPTools.KEY_INIT_PLAYLIST_INDEX);
            if (mPlaylists != null) {
                List<Song> songs = mPlaylists.get(playlistIndex).getSongList();
                mSongList = songs;
                Song song = songs.get(index);
                updateUI(song.getSongName(), song.getSingerName());
            }

        }
    };

    @Override
    protected void initView(final FragmentControlBinding binding) {
        mControlTitle = binding.musicControlTitle;
        mControlSinger = binding.musicControlSinger;

        LocalService.addPlayerCallbackAdapter(mPlayerCallback);

        LiveDataBus.get().<Boolean>with(Constants.SCAN_SUCCESS_NOTIFICATION).observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean flag) {
                LogTools.i(TAG, "onChanged", "flag="+flag);
                if (flag != null && flag) {
                    loadData(mViewDataBinding);
                }
            }
        });

        loadData(binding);

        binding.musicControlPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!MusicControl.getInstance().isPlaying()) {
                    MusicControl.getInstance().setSongList(mSongList, mSongIndex);
                } else {
                    MusicControl.getInstance().pause();
                }
            }
        });
    }

    private void loadData(final FragmentControlBinding binding) {
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
        if (playlists != null) {
            initData(binding, playlists);
        }
    }

    private void initData(FragmentControlBinding binding, List<Playlist> playlists) {
        LogTools.i(TAG, "initData", "--------");
        mPlaylists = playlists;
        int playlistIndex = SPTools.getIntValue(SPTools.KEY_INIT_PLAYLIST_INDEX);
        int songIndex = SPTools.getIntValue(SPTools.KEY_INIT_SONG_INDEX);
        int playlistSize = playlists.size();
        playlistIndex = MathTools.calculationIndex(playlistIndex, playlistSize);
        if (playlistIndex >= 0 && playlistIndex < playlistSize) {
            List<Song> songList = playlists.get(playlistIndex).getSongList();
            if (songList != null) {
                mSongList = songList;
                int songListSize = songList.size();

                songIndex = MathTools.calculationIndex(songIndex, songListSize);
                if (songIndex >= 0 && songIndex < songListSize) {
                    mSongIndex = songIndex;
                    Song song = songList.get(songIndex);
                    updateUI(song.getSongName(), song.getSingerName());
                } else {
                    updateUI("xx", "xx");
                }
            }
        }
    }

    private void updateUI(final String songName, final String singerName) {
        mViewDataBinding.getRoot().post(new Runnable() {
            @Override
            public void run() {
                if (mControlTitle != null) {
                    mControlTitle.setText(songName);
                }
                if (mControlSinger != null) {
                    mControlSinger.setText(singerName);
                }
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_control;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalService.removePlayerCallbackAdapter(mPlayerCallback);
        LiveDataBus.get().<Boolean>with(Constants.SCAN_SUCCESS_NOTIFICATION).postValue(null);
    }
}
