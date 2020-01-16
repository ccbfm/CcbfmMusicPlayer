package com.ccbfm.music.player.ui.fragment;

import com.ccbfm.music.player.R;
import com.ccbfm.music.player.data.adapter.SongListExpandableListAdapter;
import com.ccbfm.music.player.data.model.SongListModel;
import com.ccbfm.music.player.databinding.FragmentSongListBinding;

public class SongListFragment extends BaseFragment<FragmentSongListBinding> {

    @Override
    protected void initView(FragmentSongListBinding binding) {
        SongListModel model = new SongListModel();
        model.setAdapter(new SongListExpandableListAdapter(getContext()));
        binding.setSongListModel(model);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_song_list;
    }
}
