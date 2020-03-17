package com.ccbfm.music.player.data.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ccbfm.music.player.R;
import com.ccbfm.music.player.database.entity.Song;
import com.ccbfm.music.player.ui.widget.SlidingMenuView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BlacklistAdapter extends BaseAdapter {
    private static final String TAG = "BlacklistAdapter";
    private List<Song> mSongList;
    private LayoutInflater mLayoutInflater;
    private OnItemClickListener mClickListener;
    private HashMap<ViewHolder, String> mViewHolderMap;

    private SlidingMenuView.SlidingStateListener mViewSlidingStateListener = new SlidingMenuView.SlidingStateListener() {
        @Override
        public void onSlidingState(boolean open, int groupIndex, int childIndex) {
            if (open) {
                changeViewSlidingView(groupIndex);
            }
        }
    };

    public BlacklistAdapter(List<Song> songList) {
        if (songList != null) {
            if (mSongList == null) {
                mSongList = new ArrayList<>(songList);
            } else {
                mSongList.clear();
                mSongList.addAll(songList);
            }
        } else {
            mSongList = new LinkedList<>();
        }
        mViewHolderMap = new HashMap<>(16);
    }

    public void setClickListener(OnItemClickListener clickListener) {
        mClickListener = clickListener;
    }

    public void updateData(List<Song> songList) {
        mSongList.clear();
        mSongList.addAll(songList);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mSongList.size();
    }

    @Override
    public Song getItem(int position) {
        return mSongList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            if (mLayoutInflater == null) {
                Context context = parent.getContext();
                mLayoutInflater = LayoutInflater.from(context);
            }
            convertView = mLayoutInflater.inflate(R.layout.item_blacklist_song, null);
            holder = new ViewHolder();
            holder.fileName = (TextView) convertView.findViewById(R.id.music_blacklist_song);
            holder.songRestore = convertView.findViewById(R.id.music_blacklist_song_restore);
            holder.convertView = (SlidingMenuView) convertView;
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        convertView.setTag(R.id.tag_group_position, position);
        convertView.setTag(R.id.tag_child_position, position);
        mViewHolderMap.put(holder, position+"");
        holder.convertView.setSlidingStateListener(mViewSlidingStateListener);
        if(mClickListener != null) {
            holder.songRestore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mClickListener != null) {
                        mClickListener.onItemClick(v, getItem(position));
                    }
                    changeViewSlidingView(-1);
                }
            });
        }
        final Song song = getItem(position);
        holder.fileName.setText(song.getSongName());

        return convertView;
    }

    private void changeViewSlidingView(int position) {
        if (mViewHolderMap != null) {
            Set<Map.Entry<ViewHolder, String>> set = mViewHolderMap.entrySet();
            String targetKey = position + "";
            for (Map.Entry<ViewHolder, String> entry : set) {
                if (TextUtils.equals(entry.getValue(), targetKey)) {
                    continue;
                }
                entry.getKey().convertView.closeSlidingView();
            }
        }
    }

    private static class ViewHolder {
        SlidingMenuView convertView;
        TextView fileName;
        ImageButton songRestore;
    }

    public interface OnItemClickListener {
        void onItemClick(View v, Song song);
    }
}
