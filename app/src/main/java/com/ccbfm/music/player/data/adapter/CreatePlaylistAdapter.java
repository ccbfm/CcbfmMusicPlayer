package com.ccbfm.music.player.data.adapter;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.ccbfm.music.player.R;
import com.ccbfm.music.player.database.entity.Song;
import com.ccbfm.music.player.tool.LogTools;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class CreatePlaylistAdapter extends BaseAdapter {
    private static final String TAG = "CreatePlaylistAdapter";
    private List<Song> mSongList;
    private LayoutInflater mLayoutInflater;
    private OnItemClickListener mOnItemClickListener;
    private Handler mHandler;
    private HashSet<Integer> mCheckedSet;

    public CreatePlaylistAdapter(List<Song> songList, List<Song> oldSongs) {
        if (songList != null) {
            if (oldSongs != null) {
                matchChecked(songList, oldSongs);
            } else {
                if (mSongList == null) {
                    mSongList = new ArrayList<>(songList);
                } else {
                    mSongList.clear();
                    mSongList.addAll(songList);
                }
            }
        } else {
            mSongList = new LinkedList<>();
        }
    }

    public void updateData(List<Song> songList, List<Song> oldSongs,
                           final ListView listView, Integer position) {
        if (songList == null) {
            return;
        }
        if (oldSongs != null) {
            matchChecked(songList, oldSongs);
        } else {
            mSongList.clear();
            mSongList.addAll(songList);
        }
        notifyDataSetChanged();
        if (mHandler == null) {
            mHandler = new Handler();
        }
        if (position == null) {
            position = 0;
        }
        final int pos = position;
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                listView.setSelection(pos);
            }
        }, 0);
    }

    private void matchChecked(List<Song> songList, List<Song> oldSongs) {
        if (mSongList == null) {
            mSongList = new LinkedList<>();
        } else {
            mSongList.clear();
        }
        if (mCheckedSet == null) {
            mCheckedSet = new HashSet<>(getCount() / 4);
        } else {
            mCheckedSet.clear();
        }
        int size = songList.size();
        for (int i = 0; i < size; i++) {
            Song song = songList.get(i);
            if (oldSongs.contains(song)) {
                mCheckedSet.add(i);
            }
            mSongList.add(song);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            if (mLayoutInflater == null) {
                Context context = parent.getContext();
                mLayoutInflater = LayoutInflater.from(context);
            }
            convertView = mLayoutInflater.inflate(R.layout.item_create_playlist_song, null);
            holder = new ViewHolder();
            holder.fileName = (TextView) convertView.findViewById(R.id.music_create_playlist_song);
            holder.checkBox = convertView.findViewById(R.id.music_create_playlist_check);
            holder.convertView = convertView;
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final Song song = getItem(position);
        holder.fileName.setText(song.getSongName());
        holder.checkBox.setTag(position);

        if (mCheckedSet != null) {
            holder.checkBox.setChecked(mCheckedSet.contains(position));
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox cb = holder.checkBox;
                cb.setChecked(!cb.isChecked());
            }
        });

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int pos = (int) buttonView.getTag();
                handleSelectSongList(pos, isChecked);
            }
        });
        return convertView;
    }

    private void handleSelectSongList(int pos, boolean isChecked) {
        LogTools.i(TAG, "handleSelectSongList", "pos=" + pos + ",isChecked=" + isChecked);
        if (mCheckedSet == null) {
            mCheckedSet = new HashSet<>(getCount() / 4);
        }
        if (isChecked) {
            mCheckedSet.add(pos);
        } else {
            mCheckedSet.remove(pos);
        }
    }

    public List<Song> getSelectSongList() {
        if (mCheckedSet == null) {
            return null;
        }
        int size = mCheckedSet.size();
        LogTools.i(TAG, "getSelectSongList", "size=" + size);
        if (size > 0) {
            List<Song> songs = new LinkedList<>();
            for (Integer key : mCheckedSet) {
                if (key != null) {
                    songs.add(mSongList.get(key));
                }
            }
            return songs;
        }
        return null;
    }

    private static class ViewHolder {
        View convertView;
        TextView fileName;
        CheckBox checkBox;
    }

    public interface OnItemClickListener {
        void onItemClick();
    }

}
