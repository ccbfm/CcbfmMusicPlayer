package com.ccbfm.music.player.ui.activity;

import com.ccbfm.music.player.R;
import com.ccbfm.music.player.data.adapter.MusicFragmentAdapter;
import com.ccbfm.music.player.databinding.ActivityMusicBinding;
import com.ccbfm.music.player.ui.fragment.ControlFragment;
import com.ccbfm.music.player.ui.fragment.LyricsFragment;
import com.ccbfm.music.player.ui.fragment.ScanningFragment;

public class MusicActivity extends BaseActivity<ActivityMusicBinding> {

    @Override
    protected void initView(ActivityMusicBinding binding) {
        MusicFragmentAdapter adapter = new MusicFragmentAdapter(getSupportFragmentManager(), 0);
        adapter.addFragment(new ScanningFragment());
        adapter.addFragment(new ControlFragment());
        adapter.addFragment(new LyricsFragment());
        binding.musicViewPager.setAdapter(adapter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_music;
    }
}
