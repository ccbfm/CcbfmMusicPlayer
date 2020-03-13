package com.ccbfm.music.player.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.ccbfm.music.player.R;
import com.ccbfm.music.player.data.adapter.SongListExpandableListAdapter;
import com.ccbfm.music.player.data.model.SongListModel;
import com.ccbfm.music.player.databinding.FragmentSongListBinding;
import com.ccbfm.music.player.tool.StartActivityTools;
import com.ccbfm.music.player.ui.activity.CreatePlaylistActivity;
import com.ccbfm.music.player.ui.widget.PinnedHeaderExpandableListView;

public class SongListFragment extends BaseFragment<FragmentSongListBinding> {

    private static final int CODE_CREATE_PLAYLIST = 0x12;
    private SongListModel mSongListModel;

    @Override
    protected void initView(FragmentSongListBinding binding) {
        View headView = LayoutInflater.from(getContext()).inflate(R.layout.item_song_list_header, null);
        final TextView headName = headView.findViewById(R.id.music_song_list_name);
        binding.musicSongList.setPinnedHeader(headView, new PinnedHeaderExpandableListView.PinnedHeaderListener() {
            @Override
            public void changeContent(View view, Object object) {
                if (object instanceof String) {
                    headName.setText(object.toString());
                }
            }
        });

        SongListModel model = new SongListModel(this, new SongListModel.CallBack<String>() {
            @Override
            public void changeContent(String groupItem) {
                headName.setText(groupItem);
            }
        });
        model.setAdapter(new SongListExpandableListAdapter(getContext()));
        binding.setSongListModel(model);
        mSongListModel = model;

        final View addSongList = headView.findViewById(R.id.music_song_list_add);
        addSongList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartActivityTools.startForResult((SongListFragment.this), CreatePlaylistActivity.class, CODE_CREATE_PLAYLIST);
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_song_list;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (CODE_CREATE_PLAYLIST == requestCode && resultCode == Activity.RESULT_OK) {
            if(mSongListModel != null){
                mSongListModel.loadData();
            }
        }
    }
}
