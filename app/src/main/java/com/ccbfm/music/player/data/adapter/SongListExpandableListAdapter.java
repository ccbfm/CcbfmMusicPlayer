package com.ccbfm.music.player.data.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ccbfm.music.player.R;
import com.ccbfm.music.player.control.MusicControl;
import com.ccbfm.music.player.database.entity.Playlist;
import com.ccbfm.music.player.database.entity.Song;
import com.ccbfm.music.player.tool.LogTools;
import com.ccbfm.music.player.tool.SPTools;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class SongListExpandableListAdapter extends BaseExpandableListAdapter {

    private LayoutInflater mLayoutInflater;
    private LinkedList<Playlist> mPlaylists = new LinkedList<>();
    private OnChildClickListener mChildClickListener;
    private boolean mInitDisplay = true;
    private int mPlaylistIndex;
    private int mSongIndex;
    private ExpandableListView mListView;
    private View mCurrentView;
    private HashMap<ChildHolder, String> mChildHolderMap;

    public SongListExpandableListAdapter(Context context, ExpandableListView listView) {
        mListView = listView;
        listView.setAdapter(this);
        mChildHolderMap = new HashMap<>(16);
        mLayoutInflater = LayoutInflater.from(context);
        setChildClickListener(new OnChildClickListener() {
            @Override
            public void onClick(View view, int groupPosition, int childPosition) {
                SPTools.putIntValue(SPTools.KEY_INIT_PLAYLIST_INDEX, groupPosition);
                List<Song> songList = mPlaylists.get(groupPosition).getSongList();
                MusicControl.getInstance().setSongList(songList, childPosition);
                mPlaylistIndex = groupPosition;
                mSongIndex = childPosition;
                changePlayView(view, true);
            }
        });
    }

    public void updatePlaylist(List<Playlist> playlists) {
        mPlaylists.clear();
        mPlaylists.addAll(playlists);
        mPlaylistIndex = SPTools.getIntValue(SPTools.KEY_INIT_PLAYLIST_INDEX);
        mSongIndex = SPTools.getIntValue(SPTools.KEY_INIT_SONG_INDEX);
        notifyDataSetChanged();
    }

    public void setChildClickListener(OnChildClickListener childClickListener) {
        mChildClickListener = childClickListener;
    }

    @Override
    public int getGroupCount() {
        return mPlaylists.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mPlaylists.get(groupPosition).getSongList().size();
    }

    @Override
    public String getGroup(int groupPosition) {
        return mPlaylists.get(groupPosition).getName() + "(" + getChildrenCount(groupPosition) + ")";
    }

    @Override
    public Song getChild(int groupPosition, int childPosition) {
        return mPlaylists.get(groupPosition).getSongList().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupHolder groupHolder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_song_list_name, null);

            groupHolder = new GroupHolder();
            groupHolder.listName = convertView.findViewById(R.id.music_song_list_name);

            convertView.setTag(groupHolder);
        } else {
            groupHolder = (GroupHolder) convertView.getTag();
        }
        if(mInitDisplay && mPlaylistIndex == groupPosition){
            mInitDisplay = false;
            mListView.expandGroup(groupPosition);
            int position = (mSongIndex > 3 ? mSongIndex - 3 : 0) + groupPosition;
            //滚动到指定child位置
            mListView.smoothScrollToPositionFromTop(position, 0, 0);
        }
        groupHolder.listName.setText(getGroup(groupPosition));
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildHolder childHolder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_child_song_name, null);
            childHolder = new ChildHolder();
            childHolder.songName = convertView.findViewById(R.id.music_song_name);
            childHolder.songPlay = convertView.findViewById(R.id.music_song_play);
            childHolder.convertView = convertView;
            convertView.setTag(childHolder);
        } else {
            childHolder = (ChildHolder) convertView.getTag();
        }
        mChildHolderMap.put(childHolder, getStringKey(groupPosition, childPosition));
        if (mChildClickListener != null) {
            final View view = convertView;
            convertView.setTag(R.id.tag_group_position, groupPosition);
            convertView.setTag(R.id.tag_child_position, childPosition);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mChildClickListener != null && v != null) {
                        int groupPosition = (int) v.getTag(R.id.tag_group_position);
                        int childPosition = (int) v.getTag(R.id.tag_child_position);
                        mChildClickListener.onClick(view, groupPosition, childPosition);
                    }
                }
            });
        }

        if(mPlaylistIndex == groupPosition && mSongIndex == childPosition){
            mCurrentView = childHolder.songPlay;
            childHolder.songPlay.setVisibility(View.VISIBLE);
            childHolder.songPlay.setBackgroundResource(MusicControl.getInstance().isPlaying() ?
                    R.drawable.ic_play_to_pause_40dp : R.drawable.ic_pause_to_play_40dp);
        } else {
            childHolder.songPlay.setVisibility(View.GONE);
        }

        childHolder.songName.setText(getChild(groupPosition, childPosition).getSongName());
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private String getStringKey(int groupPosition, int childPosition){
        return groupPosition + "#" + childPosition;
    }

    public void changePlayView(int groupPosition, int childPosition, boolean isPlay){
        mSongIndex = childPosition;
        if(mChildHolderMap != null){
            if(groupPosition == -1){
                groupPosition = mPlaylistIndex;
            }
            Set<Map.Entry<ChildHolder, String>> set = mChildHolderMap.entrySet();
            for (Map.Entry<ChildHolder, String> entry : set) {
                if(TextUtils.equals(entry.getValue(), getStringKey(groupPosition, childPosition))){
                    changePlayView(entry.getKey().convertView, isPlay);
                }
            }
        }
    }

    private void changePlayView(final View view, final boolean isPlay){
        if(mListView != null){
            mListView.post(new Runnable() {
                @Override
                public void run() {
                    if(view != null){
                        View play = view.findViewById(R.id.music_song_play);
                        if(play != null){
                            play.setVisibility(View.VISIBLE);
                            play.setBackgroundResource(isPlay ?
                                    R.drawable.ic_play_to_pause_40dp : R.drawable.ic_pause_to_play_40dp);
                        }
                        if(mCurrentView != play){
                            mCurrentView.setVisibility(View.GONE);
                            mCurrentView = play;
                        }
                    }
                }
            });
        }
    }

    private static class GroupHolder {
        TextView listName;
    }

    private static class ChildHolder {
        View convertView;
        TextView songName;
        ImageButton songPlay;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ChildHolder that = (ChildHolder) o;
            return Objects.equals(convertView, that.convertView) &&
                    Objects.equals(songName, that.songName) &&
                    Objects.equals(songPlay, that.songPlay);
        }

        @Override
        public int hashCode() {
            return Objects.hash(convertView, songName, songPlay);
        }
    }

    public interface OnChildClickListener {
        void onClick(View view, int groupPosition, int childPosition);
    }
}
