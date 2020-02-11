package com.ccbfm.music.player.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;

import com.ccbfm.android.permission.APermission;
import com.ccbfm.music.player.R;
import com.ccbfm.music.player.database.SongLoader;
import com.ccbfm.music.player.databinding.ActivityStartBinding;
import com.ccbfm.music.player.tool.RestartTools;

public class StartActivity extends BaseActivity<ActivityStartBinding> {

    private boolean mFlag = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        RestartTools.getInstance().setAppStatus(RestartTools.STATUS_NORMAL);
        super.onCreate(savedInstanceState);

        checkPermission(this);
    }

    @Override
    protected void initView(ActivityStartBinding binding) {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_start;
    }

    @APermission(permissions = {Manifest.permission.READ_EXTERNAL_STORAGE})
    private void checkPermission(Activity activity) {
        needJump();
    }

    private void needJump() {
        SongLoader.getSongData(null);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(StartActivity.this, MusicActivity.class);
                startActivity(intent);
                mFlag = true;
            }
        }, 1000);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mFlag) {
            finish();
        }
    }
}
