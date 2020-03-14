package com.ccbfm.music.player.data.adapter;

import android.content.Context;
import android.os.Handler;
import android.util.SparseIntArray;
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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class CreatePlaylistAdapter extends BaseAdapter {
    private static final String TAG = "CreatePlaylistAdapter";
    private List<Song> mSongList;
    private LayoutInflater mLayoutInflater;
    private OnItemClickListener mOnItemClickListener;
    private Handler mHandler;
    private SparseIntArray mCheckedArray;

    public CreatePlaylistAdapter(List<Song> songList) {
        if(songList != null) {
            if(mSongList == null){
                mSongList = new ArrayList<>(songList);
            } else {
                mSongList.clear();
                mSongList.addAll(songList);
            }
        }
        mCheckedArray = new SparseIntArray(getCount() / 2);
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
        final Song song = getItem(position);
        holder.fileName.setText(song.getSongName());
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox cb = holder.checkBox;
                cb.setChecked(!cb.isChecked());
            }
        });
        holder.checkBox.setTag(position);
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int pos = (int) buttonView.getTag();
                handleSelectSongList(pos, isChecked);
            }
        });
        return convertView;
    }

    private void handleSelectSongList(int pos, boolean isChecked){
        if(isChecked) {
            mCheckedArray.put(pos, 1);
        } else {
            mCheckedArray.removeAt(pos);
        }
    }

    public List<Song> getSelectSongList(){
        int size = mCheckedArray.size();
        if(size > 0){
            List<Song> songs = new LinkedList<>();
            for (int i = 0; i < size; i++) {
                int key = mCheckedArray.keyAt(i);
                songs.add(mSongList.get(key));
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
        void onItemClickListener(File file);
    }

}
