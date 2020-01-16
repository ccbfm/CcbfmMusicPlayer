package com.ccbfm.music.player.data.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.ccbfm.music.player.ui.fragment.BaseFragment;

import java.util.LinkedList;
import java.util.List;

public class MusicFragmentAdapter extends FragmentPagerAdapter {

    private List<BaseFragment> mFragments = new LinkedList<>();

    public MusicFragmentAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    public MusicFragmentAdapter(@NonNull FragmentManager fm, List<BaseFragment> fragments, int behavior) {
        super(fm, behavior);
        mFragments.addAll(fragments);
    }

    public void addFragment(BaseFragment fragment){
        mFragments.add(fragment);
    }

    @NonNull
    @Override
    public BaseFragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }
}
