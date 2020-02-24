package com.ccbfm.music.player.ui.fragment;

import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.lifecycle.Observer;

import com.ccbfm.music.player.IPlayerCallback;
import com.ccbfm.music.player.R;
import com.ccbfm.music.player.control.MusicControl;
import com.ccbfm.music.player.database.SongLoader;
import com.ccbfm.music.player.database.entity.Playlist;
import com.ccbfm.music.player.database.entity.Song;
import com.ccbfm.music.player.databinding.FragmentControlBinding;
import com.ccbfm.music.player.tool.LiveDataBus;
import com.ccbfm.music.player.tool.MathTools;
import com.ccbfm.music.player.tool.SharedPreferencesTools;

import java.util.List;

import static com.ccbfm.music.player.tool.Constants.SCAN_SUCCESS_NOTIFICATION;

public class ControlFragment extends BaseFragment<FragmentControlBinding> {

    private List<Playlist> mPlaylists;
    private List<Song> mSongList;
    private int mSongIndex;
    private TextView mControlTitle;
    private TextView mControlSinger;

    private IPlayerCallback mPlayerCallback = new IPlayerCallback.Stub() {
        @Override
        public void callbackIndex(int index) throws RemoteException {
            SharedPreferencesTools.putIntValue(SharedPreferencesTools.KEY_INIT_SONG_INDEX, index);
            mSongIndex = index;
            if (mSongList != null) {
                Song song = mSongList.get(index);
                updateUI(song.getSongName(), song.getSingerName());
            }

        }

        @Override
        public void callbackMsec(int msec) throws RemoteException {
            SharedPreferencesTools.putIntValue(SharedPreferencesTools.KEY_INIT_SONG_MSEC, msec);
        }
    };

    @Override
    protected void initView(final FragmentControlBinding binding) {
        mControlTitle = binding.musicControlTitle;
        mControlSinger = binding.musicControlSinger;

        MusicControl.getInstance().registerCallback(mPlayerCallback);

        LiveDataBus.get().<Boolean>with(SCAN_SUCCESS_NOTIFICATION).observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean flag) {
                if (flag != null && flag && (mSongList == null || mSongList.size() == 0)) {
                    loadData(mViewDataBinding);
                }
            }
        });

        loadData(binding);

        binding.musicControlPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!MusicControl.getInstance().isPlaying()) {
                    Log.w("wds", ">>>>onClick=" + mSongIndex + "," + mSongList.size());
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
        mPlaylists = playlists;
        int playlistIndex = SharedPreferencesTools.getIntValue(SharedPreferencesTools.KEY_INIT_PLAYLIST_INDEX);
        int songIndex = SharedPreferencesTools.getIntValue(SharedPreferencesTools.KEY_INIT_SONG_INDEX);
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
        MusicControl.getInstance().unregisterCallback(mPlayerCallback);
        LiveDataBus.get().<Boolean>with(SCAN_SUCCESS_NOTIFICATION).postValue(null);
    }
}
