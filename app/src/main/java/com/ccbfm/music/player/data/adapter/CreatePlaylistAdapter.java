package com.ccbfm.music.player.data.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.ccbfm.music.player.R;
import com.ccbfm.music.player.database.entity.Song;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class CreatePlaylistAdapter extends BaseAdapter {

    private List<Song> mSongList;
    private LayoutInflater mLayoutInflater;
    private OnItemClickListener mOnItemClickListener;
    private Handler mHandler;

    public CreatePlaylistAdapter(List<Song> songList) {
        if(mSongList == null){
            mSongList = new LinkedList<>();
        } else {
            mSongList.clear();
        }
        if(songList != null) {
            mSongList.addAll(songList);
        }
    }

    public void updateData(List<Song> songList, final ListView listView, Integer position) {
        mSongList.clear();
        mSongList.addAll(songList);
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
        final Song song = mSongList.get(position);
        holder.fileName.setText(song.getSongName());
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox cb = holder.checkBox;
                cb.setChecked(!cb.isChecked());
            }
        });
        return convertView;
    }

    private static class ViewHolder {
        View convertView;
        TextView fileName;
        CheckBox checkBox;
    }

    public interface OnItemClickListener {
        void onItemClickListener(File file);
    }

}
