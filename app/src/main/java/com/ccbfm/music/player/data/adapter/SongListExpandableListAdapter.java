package com.ccbfm.music.player.data.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
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
import com.ccbfm.music.player.database.SongLoader;
import com.ccbfm.music.player.database.entity.Playlist;
import com.ccbfm.music.player.database.entity.Song;
import com.ccbfm.music.player.tool.DialogTools;
import com.ccbfm.music.player.tool.LogTools;
import com.ccbfm.music.player.tool.SPTools;
import com.ccbfm.music.player.tool.StartActivityTools;
import com.ccbfm.music.player.ui.activity.CreatePlaylistActivity;
import com.ccbfm.music.player.ui.fragment.BaseFragment;
import com.ccbfm.music.player.ui.fragment.SongListFragment;
import com.ccbfm.music.player.ui.widget.SlidingMenuView;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class SongListExpandableListAdapter extends BaseExpandableListAdapter {
    private static final String TAG = "SongListExpandableListAdapter";
    private static final boolean DEBUG = false;
    private LayoutInflater mLayoutInflater;
    private LinkedList<Playlist> mPlaylists = new LinkedList<>();
    private OnChildClickListener mChildClickListener;
    private boolean mInitDisplay = true;
    private int mPlaylistIndex = -1;
    private int mSongIndex = -1;
    private ExpandableListView mListView;
    private View mCurrentView;
    private HashMap<GroupHolder, String> mGroupHolderMap;
    private HashMap<ChildHolder, String> mChildHolderMap;
    private SlidingMenuView.SlidingStateListener mGroupSlidingStateListener = new SlidingMenuView.SlidingStateListener() {
        @Override
        public void onSlidingState(boolean open, int groupIndex, int childIndex) {
            if (open) {
                changeGroupSlidingView(groupIndex);
                changeChildSlidingView(-1, -1);
            }
        }
    };
    private SlidingMenuView.SlidingStateListener mChildSlidingStateListener = new SlidingMenuView.SlidingStateListener() {
        @Override
        public void onSlidingState(boolean open, int groupIndex, int childIndex) {
            if (open) {
                changeGroupSlidingView(-1);
                changeChildSlidingView(groupIndex, childIndex);
            }
        }
    };

    public SongListExpandableListAdapter(final BaseFragment fragment, ExpandableListView listView) {
        final Context context = fragment.getContext();
        mListView = listView;
        listView.setAdapter(this);
        mGroupHolderMap = new HashMap<>(8);
        mChildHolderMap = new HashMap<>(16);
        mLayoutInflater = LayoutInflater.from(context);
        mListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                mChildHolderMap.clear();
            }
        });
        setChildClickListener(new OnChildClickListener() {
            @Override
            public void onClick(View view, final int groupPosition, final int childPosition) {
                final int id = view.getId();
                if (DEBUG) {
                    LogTools.d(TAG, "onClick", "groupPosition=" + groupPosition + ",childPosition=" + childPosition);
                }
                switch (id) {
                    case R.id.music_item_child_song:
                        changeChildSlidingView(groupPosition, childPosition);
                        if (mPlaylistIndex == groupPosition && mSongIndex == childPosition) {
                            boolean isPlaying = MusicControl.getInstance().isPlaying();
                            if (isPlaying) {
                                MusicControl.getInstance().pause();
                                return;
                            }
                        } else {
                            SPTools.putIntValue(SPTools.KEY_INIT_PLAYLIST_INDEX, groupPosition);
                            mPlaylistIndex = groupPosition;
                            mSongIndex = childPosition;
                        }
                        List<Song> songList = mPlaylists.get(groupPosition).getSongList();
                        MusicControl.getInstance().setSongList(songList, childPosition);
                        break;
                    case R.id.music_song_delete:
                        int messageId = (groupPosition == 0) ? R.string.music_delete_song_1
                                : R.string.music_delete_song_2;
                        if (mSongIndex > childPosition) {
                            mSongIndex--;
                            SPTools.putIntValue(SPTools.KEY_INIT_SONG_INDEX, mSongIndex);
                        }
                        Dialog dialog = DialogTools.buildDeleteDialog(context, messageId, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (DEBUG) {
                                    LogTools.d(TAG, "Dialog.onClick", "groupPosition=" + groupPosition + ",childPosition=" + childPosition);
                                }
                                SongLoader.deleteSong(groupPosition, childPosition);
                            }
                        });
                        dialog.show();
                        changeChildSlidingView(-1, -1);
                        break;
                    case R.id.music_playlist_delete:
                        Dialog dialogPlaylist = DialogTools.buildDeleteDialog(context, R.string.music_delete_playlist,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        SongLoader.deletePlaylist(groupPosition);
                                    }
                                });
                        dialogPlaylist.show();
                        changeGroupSlidingView(-1);
                        break;
                    case R.id.music_playlist_edit:
                        Playlist oldPlaylist = mPlaylists.get(groupPosition);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(CreatePlaylistActivity.KEY_OLD_PLAYLIST, oldPlaylist);
                        StartActivityTools.startForResult(fragment,
                                CreatePlaylistActivity.class, bundle,
                                SongListFragment.CODE_CREATE_PLAYLIST);
                        changeGroupSlidingView(-1);
                        break;
                }
            }
        });
    }

    public void updatePlaylist(List<Playlist> playlists) {
        if (DEBUG) {
            LogTools.d(TAG, "updatePlaylist-=" + mPlaylistIndex + "," + mSongIndex);
        }
        mPlaylists.clear();
        mPlaylists.addAll(playlists);

        boolean flag = mPlaylistIndex != -1;
        mPlaylistIndex = SPTools.getIntValue(SPTools.KEY_INIT_PLAYLIST_INDEX);
        mSongIndex = SPTools.getIntValue(SPTools.KEY_INIT_SONG_INDEX);

        if(flag) {
            MusicControl.getInstance().setSongList(
                    mPlaylists.get(mPlaylistIndex).getSongList(), mSongIndex,
                    MusicControl.getInstance().isPlaying());
        }

        notifyDataSetChanged();
        changeGroupSlidingView(-1);
        changeChildSlidingView(-1, -1);
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
        if (DEBUG) {
            LogTools.d(TAG, "getGroupView", "(convertView == null)=" + (convertView == null) + "," + groupPosition + "," + isExpanded);
        }
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_song_list_name, null);

            groupHolder = new GroupHolder();
            groupHolder.listName = convertView.findViewById(R.id.music_song_list_name);
            groupHolder.playlistDelete = convertView.findViewById(R.id.music_playlist_delete);
            groupHolder.playlistEdit = convertView.findViewById(R.id.music_playlist_edit);
            groupHolder.convertView = (SlidingMenuView) convertView;
            convertView.setTag(groupHolder);
        } else {
            groupHolder = (GroupHolder) convertView.getTag();
        }
        mGroupHolderMap.put(groupHolder, groupPosition + "");
        convertView.setTag(R.id.tag_group_position, groupPosition);
        convertView.setTag(R.id.tag_child_position, groupPosition);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int positionGroup = (int) v.getTag(R.id.tag_group_position);
                if (mListView.isGroupExpanded(positionGroup)) {
                    mListView.collapseGroup(positionGroup);
                } else {
                    mListView.expandGroup(positionGroup);
                }
                changeChildSlidingView(positionGroup, -1);
            }
        });
        groupHolder.playlistDelete.setTag(R.id.tag_group_position, groupPosition);
        groupHolder.playlistDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mChildClickListener != null && v != null) {
                    int groupPosition = (int) v.getTag(R.id.tag_group_position);
                    mChildClickListener.onClick(v, groupPosition, -1);
                }
            }
        });
        groupHolder.playlistEdit.setTag(R.id.tag_group_position, groupPosition);
        groupHolder.playlistEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mChildClickListener != null && v != null) {
                    int groupPosition = (int) v.getTag(R.id.tag_group_position);
                    mChildClickListener.onClick(v, groupPosition, -1);
                }
            }
        });

        groupHolder.convertView.setSlidingStateListener(mGroupSlidingStateListener);
        if (mInitDisplay && mPlaylistIndex == groupPosition) {
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
        if (DEBUG) {
            LogTools.d(TAG, "getChildView", "(convertView == null)=" + (convertView == null) + "," + groupPosition + "," + childPosition);
        }
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_child_song_name, null);
            childHolder = new ChildHolder();
            childHolder.songName = convertView.findViewById(R.id.music_song_name);
            childHolder.singerName = convertView.findViewById(R.id.music_singer_name);
            childHolder.songPlay = convertView.findViewById(R.id.music_song_play);
            childHolder.songDelete = convertView.findViewById(R.id.music_song_delete);
            childHolder.convertView = (SlidingMenuView) convertView;
            convertView.setTag(childHolder);
        } else {
            childHolder = (ChildHolder) convertView.getTag();
        }
        mChildHolderMap.put(childHolder, getStringKey(groupPosition, childPosition));
        if (mChildClickListener != null) {
            convertView.setTag(R.id.tag_group_position, groupPosition);
            convertView.setTag(R.id.tag_child_position, childPosition);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mChildClickListener != null && v != null) {
                        int groupPosition = (int) v.getTag(R.id.tag_group_position);
                        int childPosition = (int) v.getTag(R.id.tag_child_position);
                        mChildClickListener.onClick(v, groupPosition, childPosition);
                    }
                }
            });
            childHolder.songDelete.setTag(R.id.tag_group_position, groupPosition);
            childHolder.songDelete.setTag(R.id.tag_child_position, childPosition);
            childHolder.songDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mChildClickListener != null && v != null) {
                        int groupPosition = (int) v.getTag(R.id.tag_group_position);
                        int childPosition = (int) v.getTag(R.id.tag_child_position);
                        mChildClickListener.onClick(v, groupPosition, childPosition);
                    }
                }
            });
        }

        childHolder.convertView.setSlidingStateListener(mChildSlidingStateListener);

        if (mPlaylistIndex == groupPosition && mSongIndex == childPosition) {
            mCurrentView = childHolder.songPlay;
            childHolder.songPlay.setVisibility(View.VISIBLE);
            childHolder.songPlay.setBackgroundResource(MusicControl.getInstance().isPlaying() ?
                    R.drawable.ic_play_to_pause_40dp : R.drawable.ic_pause_to_play_40dp);
        } else {
            childHolder.songPlay.setVisibility(View.GONE);
        }
        final Song song = getChild(groupPosition, childPosition);
        childHolder.songName.setText(song.getSongName());
        childHolder.singerName.setText(song.getSingerName());
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private String getStringKey(int groupPosition, int childPosition) {
        return groupPosition + "#" + childPosition;
    }

    private void changeGroupSlidingView(int groupPosition) {
        if (mGroupHolderMap != null) {
            Set<Map.Entry<GroupHolder, String>> set = mGroupHolderMap.entrySet();
            String targetKey = groupPosition + "";
            for (Map.Entry<GroupHolder, String> entry : set) {
                if (TextUtils.equals(entry.getValue(), targetKey)) {
                    continue;
                }
                entry.getKey().convertView.closeSlidingView();
            }
        }
    }

    private void changeChildSlidingView(int groupPosition, int childPosition) {
        if (mChildHolderMap != null) {
            if (groupPosition == -1) {
                groupPosition = mPlaylistIndex;
            }
            Set<Map.Entry<ChildHolder, String>> set = mChildHolderMap.entrySet();
            String targetKey = getStringKey(groupPosition, childPosition);
            for (Map.Entry<ChildHolder, String> entry : set) {
                if (TextUtils.equals(entry.getValue(), targetKey)) {
                    continue;
                }
                entry.getKey().convertView.closeSlidingView();
            }
        }
    }

    public void changePlayView(int groupPosition, int childPosition, boolean isPlay) {
        mSongIndex = childPosition;
        if (mChildHolderMap != null) {
            if (groupPosition == -1) {
                groupPosition = mPlaylistIndex;
            }
            Set<Map.Entry<ChildHolder, String>> set = mChildHolderMap.entrySet();
            String targetKey = getStringKey(groupPosition, childPosition);
            if (DEBUG) {
                LogTools.d(TAG, "changePlayView", "set=" + set.size() + ",targetKey=" + targetKey);
            }
            for (Map.Entry<ChildHolder, String> entry : set) {
                if (TextUtils.equals(entry.getValue(), targetKey)) {
                    changePlayView(entry.getKey().convertView, isPlay);
                }
            }
        }
    }

    private void changePlayView(final View view, final boolean isPlay) {
        if (mListView != null) {
            mListView.post(new Runnable() {
                @Override
                public void run() {
                    if (view != null) {
                        View play = view.findViewById(R.id.music_song_play);
                        if (play != null) {
                            play.setVisibility(View.VISIBLE);
                            play.setBackgroundResource(isPlay ?
                                    R.drawable.ic_play_to_pause_40dp : R.drawable.ic_pause_to_play_40dp);
                        }
                        if (DEBUG) {
                            LogTools.d(TAG, "changePlayView", "mCurrentView=" + mCurrentView + ",\nplay=" + play);
                        }
                        if (mCurrentView != play) {
                            if (mCurrentView != null) {
                                mCurrentView.setVisibility(View.GONE);
                            }
                            mCurrentView = play;
                        }
                    }
                }
            });
        }
    }

    private static class GroupHolder {
        SlidingMenuView convertView;
        TextView listName;
        ImageButton playlistDelete;
        ImageButton playlistEdit;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            GroupHolder that = (GroupHolder) o;
            return Objects.equals(convertView, that.convertView) &&
                    Objects.equals(listName, that.listName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(convertView, listName);
        }
    }

    private static class ChildHolder {
        SlidingMenuView convertView;
        TextView songName;
        TextView singerName;
        ImageButton songPlay;
        ImageButton songDelete;

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
