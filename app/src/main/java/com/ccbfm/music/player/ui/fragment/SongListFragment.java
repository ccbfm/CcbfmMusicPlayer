package com.ccbfm.music.player.ui.fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.ccbfm.music.player.R;
import com.ccbfm.music.player.data.adapter.SongListExpandableListAdapter;
import com.ccbfm.music.player.data.model.SongListModel;
import com.ccbfm.music.player.databinding.FragmentSongListBinding;
import com.ccbfm.music.player.ui.widget.PinnedHeaderExpandableListView;

public class SongListFragment extends BaseFragment<FragmentSongListBinding> {

    @Override
    protected void initView(FragmentSongListBinding binding) {
        View headView = LayoutInflater.from(getContext()).inflate(R.layout.item_song_list_name, null);
        final TextView headName = headView.findViewById(R.id.music_song_list_name);
        binding.musicSongList.setPinnedHeader(headView, new PinnedHeaderExpandableListView.PinnedHeaderListener() {
            @Override
            public void changeContent(View view, Object object) {
                if(object instanceof String) {
                    headName.setText(object.toString());
                }
            }
        });
        SongListModel model = new SongListModel(new SongListModel.CallBack<String>() {
            @Override
            public void changeContent(String groupItem) {
                headName.setText(groupItem);
            }
        });
        model.setAdapter(new SongListExpandableListAdapter(getContext()));
        binding.setSongListModel(model);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_song_list;
    }
}
