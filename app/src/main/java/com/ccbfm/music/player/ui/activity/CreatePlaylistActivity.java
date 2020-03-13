package com.ccbfm.music.player.ui.activity;

import android.content.Intent;
import android.view.View;

import com.ccbfm.music.player.R;
import com.ccbfm.music.player.data.model.CreatePlaylistModel;
import com.ccbfm.music.player.database.entity.Playlist;
import com.ccbfm.music.player.databinding.ActivityCreatePlaylistBinding;

public class CreatePlaylistActivity extends BaseActivity<ActivityCreatePlaylistBinding> {

    private Playlist mPlaylist;

    @Override
    protected void initView(ActivityCreatePlaylistBinding binding) {
        CreatePlaylistModel model = new CreatePlaylistModel(binding.musicCreatePlaylist);
        binding.setCreatePlaylistModel(model);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_create_playlist;
    }

    public void selectCancel(View view){
        setResult(RESULT_CANCELED);
        onBackPressed();
    }

    public void selectConfirm(View view){
        if(mPlaylist == null){
            selectCancel(view);
            return;
        }
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        onBackPressed();
    }
}
