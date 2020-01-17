package com.ccbfm.music.player.data.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ccbfm.music.player.R;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class SelectFolderAdapter extends BaseAdapter {

    private List<File> mFiles = new LinkedList<>();
    private LayoutInflater mLayoutInflater;

    public SelectFolderAdapter(List<File> files) {
        mFiles.addAll(files);
        Log.w("wds", "getCount-="+getCount());
    }

    public void updateFiles(List<File> files){
        mFiles.clear();
        mFiles.addAll(files);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mFiles.size();
    }

    @Override
    public File getItem(int position) {
        return mFiles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.w("wds", "getView-="+position);
        ViewHolder holder;
        if (convertView == null) {
            if(mLayoutInflater == null){
                mLayoutInflater = LayoutInflater.from(parent.getContext());
            }
            convertView = mLayoutInflater.inflate(R.layout.item_select_folder_name, null);
            holder = new ViewHolder();
            holder.fileName = (TextView) convertView.findViewById(R.id.music_directory_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        File file = mFiles.get(position);
        holder.fileName.setText(file.getName());
        return convertView;
    }

    private static class ViewHolder {
        TextView fileName;
    }
}
