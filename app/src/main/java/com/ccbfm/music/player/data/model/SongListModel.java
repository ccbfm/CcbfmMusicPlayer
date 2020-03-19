package com.ccbfm.music.player.data.model;

import androidx.lifecycle.Observer;

import com.ccbfm.music.player.data.adapter.SongListExpandableListAdapter;
import com.ccbfm.music.player.database.SongLoader;
import com.ccbfm.music.player.database.entity.Playlist;
import com.ccbfm.music.player.databinding.FragmentSongListBinding;
import com.ccbfm.music.player.tool.Constants;
import com.ccbfm.music.player.tool.LiveDataBus;
import com.ccbfm.music.player.tool.LogTools;
import com.ccbfm.music.player.ui.fragment.BaseFragment;

import java.util.List;

public class SongListModel {
    private static final String TAG = "SongListModel";
    private SongListExpandableListAdapter mAdapter;
    private CallBack mCallBack;
    private BaseFragment<FragmentSongListBinding> mFragment;

    public SongListModel() {
    }

    public SongListModel(BaseFragment<FragmentSongListBinding> fragment, CallBack callBack) {
        mFragment = fragment;
        mCallBack = callBack;
        LiveDataBus.get().<Boolean>with(Constants.SCAN_SUCCESS_NOTIFICATION_SONG_LIST).observe(fragment, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean flag) {
                LogTools.i(TAG, "onChanged", "flag=" + flag);
                if (flag != null && flag) {
                    loadData();
                }
            }
        });
    }

    public SongListExpandableListAdapter getAdapter() {
        return mAdapter;
    }

    public void setAdapter(SongListExpandableListAdapter adapter) {
        mAdapter = adapter;
        loadData();
    }

    public void loadData() {
        LogTools.i(TAG, "loadData", "--->---");
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

    @SuppressWarnings("unchecked")
    private void loadSongEnd(List<Playlist> playlists) {
        if (playlists != null && playlists.size() != 0) {
            mAdapter.updatePlaylist(playlists);
            if (mCallBack != null) {
                Playlist playlist = playlists.get(0);
                String content = playlist.getName() + "(" + playlist.getSongList().size() + ")";
                mCallBack.changeContent(content);
            }
        }
    }

    public interface CallBack<T> {
        void changeContent(T groupItem);
    }
}
