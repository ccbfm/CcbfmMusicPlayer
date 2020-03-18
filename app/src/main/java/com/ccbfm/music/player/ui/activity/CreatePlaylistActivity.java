package com.ccbfm.music.player.ui.activity;

import android.content.Intent;
import android.view.View;

import com.ccbfm.music.player.R;
import com.ccbfm.music.player.data.model.CreatePlaylistModel;
import com.ccbfm.music.player.database.entity.Playlist;
import com.ccbfm.music.player.databinding.ActivityCreatePlaylistBinding;

public class CreatePlaylistActivity extends BaseActivity<ActivityCreatePlaylistBinding> {
    public static final String KEY_OLD_PLAYLIST = "key_old_playlist";

    private CreatePlaylistModel mModel;

    @Override
    protected void initView(ActivityCreatePlaylistBinding binding) {
        Playlist playlist = getIntent().getParcelableExtra(KEY_OLD_PLAYLIST);
        binding.musicCreatePlaylistTitle.setText((playlist == null ? R.string.music_create_playlist
                : R.string.music_create_playlist_edit));
        CreatePlaylistModel model = new CreatePlaylistModel(binding.musicCreatePlaylist, playlist);
        binding.setCreatePlaylistModel(model);
        mModel = model;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_create_playlist;
    }

    public void selectCancel(View view) {
        setResult(RESULT_CANCELED);
        onBackPressed();
    }

    public void selectConfirm(View view) {
        boolean flag = mModel.addOrUpdatePlaylist();
        if (!flag) {
            selectCancel(view);
            return;
        }
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        onBackPressed();
    }
}
