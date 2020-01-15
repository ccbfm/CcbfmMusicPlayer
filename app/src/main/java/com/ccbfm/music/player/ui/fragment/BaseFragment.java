package com.ccbfm.music.player.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

public abstract class BaseFragment<T extends ViewDataBinding> extends Fragment {

    protected T mViewDataBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), null);
        mViewDataBinding = DataBindingUtil.bind(view);
        initView(mViewDataBinding);
        return view;
    }

    protected abstract void initView(T binding);
    protected abstract int getLayoutId();
}
