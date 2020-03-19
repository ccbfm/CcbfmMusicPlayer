package com.ccbfm.music.player.ui.activity;

import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.TextView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import com.ccbfm.music.player.R;
import com.ccbfm.music.player.callback.Callback;
import com.ccbfm.music.player.data.adapter.MusicFragmentAdapter;
import com.ccbfm.music.player.databinding.ActivityMusicBinding;
import com.ccbfm.music.player.tool.SPTools;
import com.ccbfm.music.player.tool.SystemTools;
import com.ccbfm.music.player.tool.ExitTimeTools;
import com.ccbfm.music.player.ui.fragment.BaseFragment;
import com.ccbfm.music.player.ui.fragment.ControlFragment;
import com.ccbfm.music.player.ui.fragment.LyricsFragment;
import com.ccbfm.music.player.ui.fragment.ScanningFragment;
import com.ccbfm.music.player.ui.fragment.SongListFragment;
import com.ccbfm.music.player.ui.widget.VerticalViewPager;

public class MusicActivity extends BaseActivity<ActivityMusicBinding> {

    private static final int INDEX_CONTROL = 1;
    private int mCurrentPosition = 0;
    private int mScrollState;
    private BaseFragment mSongListFragment;
    private boolean mIsShowSongList = false;
    private DrawerLayout mMusicMenu;
    private TextView mExitTime;

    @Override
    protected void initView(ActivityMusicBinding binding) {
        mMusicMenu = binding.musicMenu;
        mExitTime = binding.musicExitTime;
        MusicFragmentAdapter adapter = new MusicFragmentAdapter(getSupportFragmentManager());
        adapter.addFragment(new ScanningFragment());
        adapter.addFragment(new ControlFragment());
        adapter.addFragment(new LyricsFragment());
        binding.musicViewPager.setAdapter(adapter);
        binding.musicViewPager.addOnPageChangeListener(new VerticalViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mCurrentPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                mScrollState = state;
            }
        });

        int initPage = SPTools.getIntValue(SPTools.KEY_INIT_SHOW_PAGE);
        binding.musicViewPager.setCurrentItem(initPage);
        SPTools.putIntValue(SPTools.KEY_INIT_SHOW_PAGE, INDEX_CONTROL);

        setExitTime();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_music;
    }

    private float mDownX, mDownY;
    private VelocityTracker mVelocityTracker;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
       /* if (mCurrentPosition != INDEX_CONTROL) {
            return super.dispatchTouchEvent(ev);
        }*/
        if (mMusicMenu.isDrawerOpen(GravityCompat.START)) {
            return super.dispatchTouchEvent(ev);
        }

        final int action = ev.getAction();
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);

        final float x = ev.getX();
        final float y = ev.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mDownX = x;
                mDownY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                if (mScrollState == VerticalViewPager.SCROLL_STATE_IDLE) {
                    mVelocityTracker.computeCurrentVelocity(1000, 8000);
                    float vx = mVelocityTracker.getXVelocity();
                    float dx = x - mDownX;
                    float dy = y - mDownY;
                    if (vx < -1000 && dx < -250) {
                        rightSwipe();
                    } else if (vx > 1000 && dx > 250) {
                        leftSwipe();
                    }
                }
                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onBackPressed() {
        if (mIsShowSongList) {
            removeFragment(mSongListFragment);
            mIsShowSongList = false;
            return;
        }
        super.onBackPressed();
    }

    private void rightSwipe() {
        if (mSongListFragment == null) {
            mSongListFragment = new SongListFragment();
        }
        if (mIsShowSongList) {
            return;
        }
        replaceFragment(mSongListFragment);
        mIsShowSongList = true;
        mMusicMenu.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    private void leftSwipe() {
        if (!mIsShowSongList) {
            return;
        }
        if (mSongListFragment != null) {
            removeFragment(mSongListFragment);
            mIsShowSongList = false;
        }
        mMusicMenu.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    private void replaceFragment(BaseFragment fragment) {
        if (fragment == null) {
            return;
        }
        mViewDataBinding.musicSongList.setFocusable(true);
        getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .setCustomAnimations(R.anim.slide_in_form_right, R.anim.slide_out_to_right)
                .replace(R.id.music_song_list, fragment)
                .commitNowAllowingStateLoss();
    }

    private void removeFragment(BaseFragment fragment) {
        if (fragment == null) {
            return;
        }
        mViewDataBinding.musicSongList.setFocusable(false);
        getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                .setCustomAnimations(R.anim.slide_in_form_right, R.anim.slide_out_to_right)
                .remove(mSongListFragment)
                .commitNowAllowingStateLoss();
    }

    public void onClickOpenMenu(View view) {
        if (mMusicMenu.isDrawerOpen(GravityCompat.START)) {
            mMusicMenu.closeDrawer(GravityCompat.START);
        } else {
            mMusicMenu.openDrawer(GravityCompat.START);
        }
    }

    public void onClickOpenPlaylist(View view) {
        rightSwipe();
    }

    public void onClickMenuView(View view) {
        final int id = view.getId();
        switch (id) {
            case R.id.music_menu_timing:
                ExitTimeTools.showExitTimePicker(this, new Callback() {
                    @Override
                    public void callback() {
                        setExitTime();
                    }
                });
                break;
            case R.id.music_menu_exit:
                SystemTools.killAppProcess(this);
                break;
        }
    }

    private void setExitTime(){
        String exitTime = ExitTimeTools.getExitTime();
        mExitTime.setText(exitTime);
    }
}
