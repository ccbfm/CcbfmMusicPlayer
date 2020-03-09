package com.ccbfm.music.player.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.ccbfm.music.player.tool.RestartTools;
import com.ccbfm.screen.adapter.ScreenAdapter;

public abstract class BaseActivity<T extends ViewDataBinding> extends AppCompatActivity {

    protected T mViewDataBinding;
    private Handler mMainHandler;

    @Override
    @ScreenAdapter
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        switch (RestartTools.getInstance().getAppStatus()) {
            case RestartTools.STATUS_FORCE_KILLED:
                restartApp();
                break;
            case RestartTools.STATUS_NORMAL:
                break;
            default:
                break;
        }
        mMainHandler = new Handler();
        mViewDataBinding = DataBindingUtil.setContentView(this, getLayoutId());
        initView(mViewDataBinding);
    }

    protected abstract void initView(T binding);
    protected abstract int getLayoutId();

    public Handler getMainHandler() {
        return mMainHandler;
    }

    private void restartApp() {
        Intent intent = new Intent(this, StartActivity.class);
        startActivity(intent);
        finish();
    }
}
