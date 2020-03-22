package com.ccbfm.music.player.ui.fragment;

import android.view.View;
import android.widget.TextView;

import androidx.lifecycle.Observer;

import com.ccbfm.music.player.R;
import com.ccbfm.music.player.callback.PlayerCallbackAdapter;
import com.ccbfm.music.player.control.MusicControl;
import com.ccbfm.music.player.control.PlayerErrorCode;
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
import com.ccbfm.music.player.ui.widget.PlayPauseView;
import com.ccbfm.music.player.ui.widget.PreNextView;
import com.ccbfm.music.player.ui.widget.visualizer.BaseVisualizer;
import com.ccbfm.music.player.ui.widget.visualizer.VisualizerTools;

import java.util.List;

public class ControlFragment extends BaseFragment<FragmentControlBinding> {
    private static final String TAG = "ControlFragment";
    private List<Playlist> mPlaylists;
    private List<Song> mSongList;
    private int mSongIndex;
    private TextView mControlTitle;
    private TextView mControlSinger;
    private PlayPauseView mPlayPauseView;
    private BaseVisualizer mVisualizerBar;

    private PlayerCallbackAdapter mPlayerCallback = new PlayerCallbackAdapter() {
        @Override
        public void callbackIndex(int index) {
            mSongIndex = index;
            int playlistIndex = SPTools.getIntValue(SPTools.KEY_INIT_PLAYLIST_INDEX);
            if (mPlaylists != null) {
                List<Song> songs = mPlaylists.get(playlistIndex).getSongList();
                mSongList = songs;
                if (songs != null && songs.size() > 0) {
                    Song song = songs.get(index);
                    updateUI(song);
                }
            }
        }

        @Override
        public void callbackError(int code, Song song) {
            super.callbackError(code, song);
            if (code == PlayerErrorCode.NULL) {
                if (mPlayPauseView != null) {
                    mPlayPauseView.setBarPlayingState(false);
                }
            }
        }

        @Override
        public void callbackMsec(int msec) {
            super.callbackMsec(msec);
            if (mSongList != null && mPlayPauseView != null && mSongList.size() > 0) {
                Song song = mSongList.get(mSongIndex);
                mPlayPauseView.setRingProgress(msec, song.getDuration());
            }
        }

        @Override
        public void callbackAudioSession(int id) {
            super.callbackAudioSession(id);
            VisualizerTools.getInstance().addVisualizer(id, mVisualizerBar);
        }
    };

    @Override
    protected void initView(final FragmentControlBinding binding) {
        mControlTitle = binding.musicControlTitle;
        mControlSinger = binding.musicControlSinger;
        mVisualizerBar = binding.musicAudioVisualizerBar;

        LocalService.addPlayerCallbackAdapter(mPlayerCallback);

        LiveDataBus.get().<Boolean>with(Constants.SCAN_SUCCESS_NOTIFICATION_CONTROL).observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean flag) {
                LogTools.i(TAG, "onChanged", "flag=" + flag);
                if (flag != null && flag) {
                    loadData(mViewDataBinding);
                }
            }
        });

        loadData(binding);
        mPlayPauseView = binding.musicControlPlay;
        binding.musicControlPlay.setCallbackClick(new PlayPauseView.CallbackClick() {

            @Override
            public boolean onClickPlay(boolean isPlaying) {
                if (!MusicControl.getInstance().isPlaying()) {
                    MusicControl.getInstance().setSongList(mSongList, mSongIndex);
                } else {
                    MusicControl.getInstance().pause();
                }
                return true;
            }

            @Override
            public void onClickSeek(int msec) {
                MusicControl.getInstance().seekTo(msec);
            }

        });

        binding.musicControlPrevious.setCallbackClick(new PreNextView.CallbackClick() {
            @Override
            public void onClick() {
                MusicControl.getInstance().previous();
            }
        });

        binding.musicControlNext.setCallbackClick(new PreNextView.CallbackClick() {
            @Override
            public void onClick() {
                MusicControl.getInstance().next();
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
        playlistIndex = MathTools.calculateIndex(playlistIndex, playlistSize);
        if (playlistIndex >= 0 && playlistIndex < playlistSize) {
            List<Song> songList = playlists.get(playlistIndex).getSongList();
            if (songList != null) {
                mSongList = songList;
                int songListSize = songList.size();

                songIndex = MathTools.calculateIndex(songIndex, songListSize);
                if (songIndex >= 0 && songIndex < songListSize) {
                    mSongIndex = songIndex;
                    Song song = songList.get(songIndex);
                    MusicControl.getInstance().setSongList(mSongList, mSongIndex, false);
                    updateUI(song);
                } else {
                    updateUI(null);
                }
            }
        }
    }

    private void updateUI(final Song song) {

        mViewDataBinding.getRoot().post(new Runnable() {
            @Override
            public void run() {
                String songName;
                String singerName = "";
                if (song != null) {
                    songName = song.getSongName();
                    singerName = song.getSingerName();
                } else {
                    songName = getString(R.string.app_name);
                }

                if (mControlTitle != null) {
                    mControlTitle.setText(songName);
                }
                if (mControlSinger != null) {
                    mControlSinger.setText(singerName);
                }
                if (mPlayPauseView != null) {
                    if (song != null) {
                        int msec = SPTools.getIntValue(SPTools.KEY_INIT_SONG_MSEC);
                        mPlayPauseView.setRingProgress(msec, song.getDuration());
                    }
                    boolean isPlaying = MusicControl.getInstance().isPlaying();
                    mPlayPauseView.setBarPlayingState(isPlaying);
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
        VisualizerTools.getInstance().releaseVisualizer();
        LocalService.removePlayerCallbackAdapter(mPlayerCallback);
        LiveDataBus.get().<Boolean>with(Constants.SCAN_SUCCESS_NOTIFICATION_CONTROL).postValue(null);
    }
}
