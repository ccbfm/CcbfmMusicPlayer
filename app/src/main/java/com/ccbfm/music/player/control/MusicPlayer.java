package com.ccbfm.music.player.control;

import android.media.MediaPlayer;
import android.text.TextUtils;

import com.ccbfm.music.player.IPlayerCallback;
import com.ccbfm.music.player.database.entity.Song;
import com.ccbfm.music.player.service.MusicService;
import com.ccbfm.music.player.tool.Constants;
import com.ccbfm.music.player.tool.LogTools;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MusicPlayer implements IControlPlayer {
    private static final String TAG = "MusicPlayer";
    private MediaPlayer mPlayer;
    private List<Song> mSongList;
    private int mSongIndex = 0;
    private int mMode = ControlConstants.MODE_LIST;
    private Random mRandom;
    private String mCurrentPath;
    private boolean mIsPrepared = false;
    private boolean mIsResetSongList = true;
    private Timer mTimer;
    private int mSeekTime = 0;
    private MusicService.NotificationCallback mCallback;
    private IPlayerCallback mPlayerCallback;

    public MusicPlayer(MusicService.NotificationCallback callback) {
        mCallback = callback;
        initPlayer();
    }

    private void initPlayer() {
        if (mPlayer != null) {
            return;
        }
        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                LogTools.i(TAG, "onCompletion", "mIsResetSongList=" + mIsResetSongList);
                if(!mIsResetSongList) {
                    next();
                }
            }
        });
        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                LogTools.i(TAG, "onPrepared", "mp=" + mp);
                mIsPrepared = true;
                play();
            }
        });
    }

    @Override
    public void prepare(String path) {
        initPlayer();

        if (!TextUtils.isEmpty(mCurrentPath)
                && TextUtils.equals(mCurrentPath, path)) {
            if (mIsPrepared) {
                play();
            }
            return;
        }

        if (TextUtils.isEmpty(path)) {
            if (mSongList != null) {
                Song song = mSongList.get(mSongIndex);
                path = song.getSongPath();
            }
        }
        if (mPlayer != null && !TextUtils.isEmpty(path)) {
            mCurrentPath = path;
            LogTools.w(TAG, "prepare", "path=" + path);
            try {
                mIsPrepared = false;
                mPlayer.reset();
                mPlayer.setDataSource(path);
                mPlayer.prepareAsync();
            } catch (Exception e) {
                LogTools.e(TAG, "prepare", "Exception--- ", e);
                callbackError(PlayerErrorCode.PREPARE);
                next();
            }
        }
    }

    @Override
    public void play() {
        if (mPlayer != null && !isPlaying() && mIsPrepared) {
            LogTools.i(TAG, "play", "------" + mIsResetSongList);
            startTimer();
            mPlayer.start();
            //在start之后执行
            seekTo(mSeekTime);
            changeDisplay();
            mIsResetSongList = false;
        }
    }

    @Override
    public void stop() {
        if (mPlayer != null && isPlaying()) {
            LogTools.i(TAG, "stop", "------");
            mPlayer.stop();
            cancelTimer();
            changeDisplay();
        }
    }

    @Override
    public void release() {
        if (mPlayer != null) {
            LogTools.i(TAG, "release", "------");
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
            mSongList = null;
            mCurrentPath = null;
            mSongIndex = 0;
            mIsPrepared = false;
            mIsResetSongList = true;
            mSeekTime = 0;
            cancelTimer();
            changeDisplay();
        }
    }

    @Override
    public void pause() {
        if (mPlayer != null && isPlaying()) {
            LogTools.i(TAG, "pause", "------");
            mPlayer.pause();
            cancelTimer();
            changeDisplay();
        }
    }

    @Override
    public void seekTo(int msec) {
        if (mPlayer != null && isPlaying()) {
            if (mSeekTime > 0) {
                mPlayer.seekTo(msec);
                mSeekTime = 0;
            }
        } else {
            mSeekTime = msec;
        }
    }

    @Override
    public boolean isPlaying() {
        return mPlayer != null && mPlayer.isPlaying();
    }

    @Override
    public void setSongList(List<Song> songList, int position, boolean isPlay) {
        if (songList == null) {
            LogTools.w(TAG, "setSongList", "songList == null");
            return;
        }
        if (mSongList == null) {
            mSongList = new ArrayList<>(songList);
        } else {
            mSongList.clear();
            mSongList.addAll(songList);
        }

        if (position == Constants.ONLY_RESET_SONG_LIST) {
            position = mSongIndex;
        } else {
            int size = songList.size();
            if (position < 0 || size <= 0) {
                position = 0;
            } else if (position > size - 1) {
                position = size - 1;
            }
        }
        mSongIndex = (position);

        mIsResetSongList = true;
        if(isPlay) {
            Song song = mSongList.get(position);
            String path = song.getSongPath();
            LogTools.w(TAG, "setSongList", "path=" + path + ","+mIsResetSongList);
            prepare(path);
        }
    }

    private void callbackSongIndex(int songIndex) {
        if (mPlayerCallback != null) {
            try {
                mPlayerCallback.callbackIndex(songIndex);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void previous() {
        if (size() == 0) {
            return;
        }
        pause();
        calculationIndex(-1);
        prepare(null);
    }

    private void calculationIndex(int vector) {
        int index = mSongIndex;
        switch (mMode) {
            case ControlConstants.MODE_SINGLE:
            case ControlConstants.MODE_LIST:
                if (vector > 0) {
                    int size = size();
                    if (index < size - 1) {
                        index++;
                    } else {
                        index = 0;
                    }
                } else if (vector < 0) {
                    int size = size();
                    if (index > 0) {
                        index--;
                    } else {
                        index = size - 1;
                    }
                }
                mSongIndex = index;
                break;
            case ControlConstants.MODE_RANDOM:
                if (mRandom == null) {
                    mRandom = new Random();
                }
                index = mRandom.nextInt(size());
                mSongIndex = index;
                break;
        }
    }

    private int size() {
        return mSongList != null ? mSongList.size() : 0;
    }


    @Override
    public void next() {
        if (size() == 0) {
            return;
        }
        LogTools.i(TAG, "next", "------");
        pause();
        calculationIndex(1);
        prepare(null);
    }

    @Override
    public void mode(int mode) {
        mMode = mode;
    }

    private void startTimer() {
        if (mTimer != null) {
            mTimer.cancel();
        }

        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (mPlayerCallback != null && mPlayer != null) {
                    try {
                        int msec = mPlayer.getCurrentPosition();
                        mPlayerCallback.callbackMsec(msec);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }, 0, 500);
    }

    private void cancelTimer() {
        if (mTimer != null) {
            mTimer.cancel();
        }
    }

    private void changeDisplay() {
        if (mCallback != null) {
            mCallback.changeDisplay(getCurrentSong(), isPlaying());
        }
        callbackSongIndex(mSongIndex);
    }

    @Override
    public Song getCurrentSong() {
        return mSongList != null ? mSongList.get(mSongIndex) : null;
    }

    @Override
    public void setPlayerCallback(IPlayerCallback callback) {
        mPlayerCallback = callback;
    }

    private void callbackError(@PlayerErrorCode int code) {
        if (mPlayerCallback != null) {
            try {
                mPlayerCallback.callbackError(code, getCurrentSong());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
