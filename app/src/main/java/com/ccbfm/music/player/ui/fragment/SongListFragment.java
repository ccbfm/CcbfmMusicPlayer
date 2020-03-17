package com.ccbfm.music.player.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.ccbfm.music.player.IPlayerCallback;
import com.ccbfm.music.player.R;
import com.ccbfm.music.player.callback.PlayerCallbackAdapter;
import com.ccbfm.music.player.control.MusicControl;
import com.ccbfm.music.player.data.adapter.SongListExpandableListAdapter;
import com.ccbfm.music.player.data.model.SongListModel;
import com.ccbfm.music.player.databinding.FragmentSongListBinding;
import com.ccbfm.music.player.service.LocalService;
import com.ccbfm.music.player.tool.Constants;
import com.ccbfm.music.player.tool.LiveDataBus;
import com.ccbfm.music.player.tool.LogTools;
import com.ccbfm.music.player.tool.StartActivityTools;
import com.ccbfm.music.player.ui.activity.CreatePlaylistActivity;
import com.ccbfm.music.player.ui.widget.PinnedHeaderExpandableListView;


public class SongListFragment extends BaseFragment<FragmentSongListBinding> {
    private static final String TAG = "SongListFragment";
    public static final int CODE_CREATE_PLAYLIST = 0x12;
    private SongListModel mSongListModel;
    private TextView mHeadName;
    private View mAddSongList;

    private IPlayerCallback mPlayerCallback = new PlayerCallbackAdapter() {
        @Override
        public void callbackIndex(int index) {
            boolean isPlaying = MusicControl.getInstance().isPlaying();
            LogTools.d(TAG, "callbackIndex", "index="+index+",isPlaying="+isPlaying);
            mSongListModel.getAdapter().changePlayView(-1, index, isPlaying);
        }
    };

    @Override
    protected void initView(FragmentSongListBinding binding) {
        View headView = LayoutInflater.from(getContext()).inflate(R.layout.item_song_list_header, null);
        mHeadName = headView.findViewById(R.id.music_song_list_name);
        mAddSongList = headView.findViewById(R.id.music_song_list_add);
        binding.musicSongList.setPinnedHeader(headView, new PinnedHeaderExpandableListView.PinnedHeaderListener() {
            @Override
            public void changeContent(View view, Object object) {
                if (object instanceof String) {
                    setHeadNameText(object.toString());
                }
            }
        });

        SongListModel model = new SongListModel(this, new SongListModel.CallBack<String>() {
            @Override
            public void changeContent(String groupItem) {
                setHeadNameText(groupItem);
            }
        });
        model.setAdapter(new SongListExpandableListAdapter(this, binding.musicSongList));
        binding.setSongListModel(model);
        mSongListModel = model;

        mAddSongList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartActivityTools.startForResult((SongListFragment.this), CreatePlaylistActivity.class, CODE_CREATE_PLAYLIST);
            }
        });

        LocalService.addPlayerCallbackAdapter(mPlayerCallback);
    }

    private void setHeadNameText(String text){
        if(mHeadName != null && text != null){
            mHeadName.setText(text);
            mAddSongList.setVisibility((text.startsWith("全部歌曲") ? View.VISIBLE : View.GONE));
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_song_list;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (CODE_CREATE_PLAYLIST == requestCode && resultCode == Activity.RESULT_OK) {
            if (mSongListModel != null) {
                mSongListModel.loadData();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalService.removePlayerCallbackAdapter(mPlayerCallback);
        LiveDataBus.get().<Boolean>with(Constants.SCAN_SUCCESS_NOTIFICATION).postValue(null);
    }
}
