package com.ccbfm.music.player.data.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ccbfm.music.player.R;
import com.ccbfm.music.player.database.entity.Song;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class BlacklistAdapter extends BaseAdapter {
    private static final String TAG = "BlacklistAdapter";
    private List<Song> mSongList;
    private LayoutInflater mLayoutInflater;
    private OnItemClickListener mClickListener;

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
            holder.convertView = convertView;
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.songRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mClickListener != null){
                    mClickListener.onItemClick(v, getItem(position));
                }
            }
        });
        final Song song = getItem(position);
        holder.fileName.setText(song.getSongName());

        return convertView;
    }

    private static class ViewHolder {
        View convertView;
        TextView fileName;
        ImageButton songRestore;
    }

    public interface OnItemClickListener {
        void onItemClick(View v, Song song);
    }
}
