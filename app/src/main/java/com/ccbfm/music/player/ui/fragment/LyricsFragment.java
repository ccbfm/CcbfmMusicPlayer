package com.ccbfm.music.player.ui.fragment;

import com.ccbfm.music.player.R;
import com.ccbfm.music.player.databinding.FragmentLyricsBinding;
import com.ccbfm.music.player.tool.ToastTools;

/**
 * 歌词
 * 示波器
 */
public class LyricsFragment extends BaseFragment<FragmentLyricsBinding> {

    @Override
    protected void initView(FragmentLyricsBinding binding) {
        
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_lyrics;
    }

    @Override
    public void onResume() {
        super.onResume();
        ToastTools.showToast(getContext(), getString(R.string.music_touch_the_fish, "歌词"));
    }
}
