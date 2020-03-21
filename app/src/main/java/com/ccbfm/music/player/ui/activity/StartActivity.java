package com.ccbfm.music.player.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;

import androidx.annotation.Nullable;

import com.ccbfm.android.permission.APermission;
import com.ccbfm.music.player.R;
import com.ccbfm.music.player.database.SongLoader;
import com.ccbfm.music.player.databinding.ActivityStartBinding;
import com.ccbfm.music.player.tool.LogTools;
import com.ccbfm.music.player.tool.RestartTools;
import com.ccbfm.music.player.ui.widget.RoundProgressBar;

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

    @APermission(permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.MODIFY_AUDIO_SETTINGS})
    private void checkPermission(Activity activity) {
        needJump();
    }

    private void needJump() {
        LogTools.d("StartActivity", "needJump", "---");
        SongLoader.getSongData(null);

        final RoundProgressBar progressBar = mViewDataBinding.musicRoundProgress;
        progressBar.setAnimationListener(new RoundProgressBar.AnimationListenerAdapter<Activity>((this)) {
            @Override
            public void onAnimationEnd(Animation animation, final Activity target) {
                progressBar.endAnimation();
                getMainHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mFlag = true;
                        Intent intent = new Intent(StartActivity.this, MusicActivity.class);
                        startActivity(intent);
                    }
                }, 10);
            }
        });

        progressBar.startAnimation((360F), (0F), (1000));
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mFlag) {
            finish();
        }
    }

    @Override
    public void onBackPressed() {

    }
}
