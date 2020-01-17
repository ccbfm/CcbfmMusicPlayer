package com.ccbfm.music.player.data.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.ccbfm.music.player.R;

import java.util.LinkedList;

public class SongListExpandableListAdapter extends BaseExpandableListAdapter {

    private LayoutInflater mLayoutInflater;
    private LinkedList<String> mGroupItems = new LinkedList<>();
    private LinkedList<LinkedList<String>> mChildrenItems = new LinkedList<>();

    public SongListExpandableListAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
        mGroupItems.add("QWER1");
        mGroupItems.add("QWER2");
        mGroupItems.add("QWER3");

        LinkedList<String> list1 = new LinkedList<>();
        LinkedList<String> list2 = new LinkedList<>();
        LinkedList<String> list3 = new LinkedList<>();
        for (int i = 0; i < 20; i++) {
            list1.add("1");
            list2.add("2");
            list3.add("3");
        }
        mChildrenItems.add(list1);
        mChildrenItems.add(list2);
        mChildrenItems.add(list3);
    }

    @Override
    public int getGroupCount() {
        return mGroupItems.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mChildrenItems.get(groupPosition).size();
    }

    @Override
    public String getGroup(int groupPosition) {
        return mGroupItems.get(groupPosition);
    }

    @Override
    public String getChild(int groupPosition, int childPosition) {
        return mChildrenItems.get(groupPosition).get(childPosition);
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
        if(convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_song_list_name, null);
        }
        TextView listName = convertView.findViewById(R.id.music_song_list_name);
        listName.setText(getGroup(groupPosition));
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_child_song_name, null);
        }
        TextView songName = convertView.findViewById(R.id.music_song_name);
        songName.setText(getChild(groupPosition, childPosition));
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}
