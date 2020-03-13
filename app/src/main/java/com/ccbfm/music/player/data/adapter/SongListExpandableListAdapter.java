package com.ccbfm.music.player.data.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.ccbfm.music.player.R;
import com.ccbfm.music.player.control.MusicControl;
import com.ccbfm.music.player.database.entity.Playlist;
import com.ccbfm.music.player.database.entity.Song;
import com.ccbfm.music.player.tool.SharedPreferencesTools;

import java.util.LinkedList;
import java.util.List;

public class SongListExpandableListAdapter extends BaseExpandableListAdapter {

    private LayoutInflater mLayoutInflater;
    private LinkedList<Playlist> mPlaylists = new LinkedList<>();
    private OnChildClickListener mChildClickListener;

    public SongListExpandableListAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
        setChildClickListener(new OnChildClickListener() {
            @Override
            public void onClick(View view, int groupPosition, int childPosition) {
                SharedPreferencesTools.putIntValue(SharedPreferencesTools.KEY_INIT_PLAYLIST_INDEX, groupPosition);
                List<Song> songList = mPlaylists.get(groupPosition).getSongList();
                MusicControl.getInstance().setSongList(songList, childPosition);
            }
        });
    }

    public void updatePlaylist(List<Playlist> playlists) {
        mPlaylists.clear();
        mPlaylists.addAll(playlists);
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
        Playlist playlist = mPlaylists.get(groupPosition);
        if(playlist == null){
            return -1;
        }
        List<Song> songs = playlist.getSongList();
        if(songs== null){
            return -1;
        }
        return songs.size();
    }



    @Override
    public String getGroup(int groupPosition) {
        return mPlaylists.get(groupPosition).getName() + "(" + getChildrenCount(groupPosition) + "个)";
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

            convertView.setTag(childHolder);
        } else {
            childHolder = (ChildHolder) convertView.getTag();
        }

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

        childHolder.songName.setText(getChild(groupPosition, childPosition).getSongName());
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    private static class GroupHolder {
        TextView listName;
    }

    private static class ChildHolder {
        TextView songName;
    }

    public interface OnChildClickListener {
        void onClick(View view, int groupPosition, int childPosition);
    }
}
