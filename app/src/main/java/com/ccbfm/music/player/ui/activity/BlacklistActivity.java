package com.ccbfm.music.player.ui.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;

import com.ccbfm.music.player.R;
import com.ccbfm.music.player.callback.Callback;
import com.ccbfm.music.player.data.adapter.BlacklistAdapter;
import com.ccbfm.music.player.database.SongLoader;
import com.ccbfm.music.player.database.entity.Song;
import com.ccbfm.music.player.databinding.ActivityBlacklistBinding;
import com.ccbfm.music.player.tool.DialogTools;
import com.ccbfm.music.player.tool.LogTools;

import java.util.List;

public class BlacklistActivity extends BaseActivity<ActivityBlacklistBinding> {
    private static final String TAG = "BlacklistActivity";
    private BlacklistAdapter mAdapter;

    @Override
    protected void initView(ActivityBlacklistBinding binding) {
        mAdapter = new BlacklistAdapter(null);
        mAdapter.setClickListener(new BlacklistAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, final Song song) {
                Dialog dialog = DialogTools.buildRestoreDialog(BlacklistActivity.this, R.string.music_restore_hint,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SongLoader.restoreBlacklist(song, new Callback() {
                                    @Override
                                    public void callback() {
                                        loadData();
                                    }
                                });
                            }
                        });
                dialog.show();
            }
        });
        binding.musicBlacklist.setAdapter(mAdapter);

        loadData();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_blacklist;
    }

    private void loadData() {
        loadSongStart();
        LogTools.i(TAG, "loadData", "-------");
        SongLoader.loadBlacklist(new SongLoader.CallbackSongList() {
            @Override
            public void callback(List<Song> songList) {
                loadSongEnd(songList);
            }
        });
    }

    private void loadSongStart() {

    }

    private void loadSongEnd(List<Song> songList) {
        if (songList != null) {
            mAdapter.updateData(songList);
        }
    }
}
