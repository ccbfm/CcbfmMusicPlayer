package com.ccbfm.music.player.data.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ccbfm.music.player.R;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class SelectFolderAdapter extends BaseAdapter {

    private List<File> mFiles = new LinkedList<>();
    private LayoutInflater mLayoutInflater;
    private OnItemClickListener mOnItemClickListener;
    private Drawable mFolderDrawable;
    private Drawable mFileDrawable;
    private Handler mHandler;

    public SelectFolderAdapter(List<File> files) {
        mFiles.addAll(files);
    }

    public void updateFiles(List<File> files, final ListView listView, Integer position) {
        mFiles.clear();
        mFiles.addAll(files);
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
        ViewHolder holder;
        if (convertView == null) {
            if (mLayoutInflater == null) {
                Context context = parent.getContext();
                mLayoutInflater = LayoutInflater.from(context);
                mFolderDrawable = context.getDrawable(R.drawable.ic_folder_asset_24dp);
                mFileDrawable = context.getDrawable(R.drawable.ic_file_asset_24dp);
            }
            convertView = mLayoutInflater.inflate(R.layout.item_select_folder_name, null);
            holder = new ViewHolder();
            holder.fileName = (TextView) convertView.findViewById(R.id.music_directory_name);
            holder.convertView = convertView;
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final File file = mFiles.get(position);
        holder.fileName.setText(file.getName());
        if (file.isDirectory()) {
            holder.fileName.setCompoundDrawablesWithIntrinsicBounds(mFolderDrawable, null, null, null);
        } else {
            holder.fileName.setCompoundDrawablesWithIntrinsicBounds(mFileDrawable, null, null, null);
        }

        holder.convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClickListener(file);
                }
            }
        });
        return convertView;
    }

    private static class ViewHolder {
        View convertView;
        TextView fileName;
    }

    public interface OnItemClickListener {
        void onItemClickListener(File file);
    }

}
