package com.ccbfm.music.player.control;

import android.media.MediaPlayer;
import android.os.RemoteCallbackList;
import android.text.TextUtils;

import com.ccbfm.music.player.IPlayerCallback;
import com.ccbfm.music.player.database.entity.Song;
import com.ccbfm.music.player.service.MusicService;
import com.ccbfm.music.player.tool.SharedPreferencesTools;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MusicPlayer implements IControlPlayer {

    private MediaPlayer mPlayer;
    private List<Song> mSongList;
    private int mSongIndex;
    private int mMode = ControlConstants.MODE_LIST;
    private Random mRandom;
    private String mCurrentPath;
    private boolean mIsPrepared = false;
    private RemoteCallbackList<IPlayerCallback> mCallbackList;
    private Timer mTimer;
    private int mSeekTime = 0;
    private MusicService.NotificationCallback mCallback;

    public MusicPlayer(RemoteCallbackList<IPlayerCallback> callbackList,
                       MusicService.NotificationCallback callback) {
        mCallbackList = callbackList;
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
                next();
            }
        });
        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mIsPrepared = true;
                play();
            }
        });
    }

    @Override
    public void prepare(String path) {
        initPlayer();

        if (TextUtils.equals(mCurrentPath, path)) {
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

            try {
                mIsPrepared = false;
                mPlayer.reset();
                mPlayer.setDataSource(path);
                mPlayer.prepareAsync();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void play() {
        if (mPlayer != null && !isPlaying() && mIsPrepared) {
            startTimer();
            mPlayer.start();
            seekTo(mSeekTime);
            changeDisplay();
        }
    }

    @Override
    public void stop() {
        if (mPlayer != null && isPlaying()) {
            mPlayer.stop();
            cancelTimer();
            changeDisplay();
        }
    }

    @Override
    public void release() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
            cancelTimer();
            changeDisplay();
        }
    }

    @Override
    public void pause() {
        if (mPlayer != null && isPlaying()) {
            mPlayer.pause();
            cancelTimer();
            changeDisplay();
        }
    }

    @Override
    public void seekTo(int msec) {
        if (mPlayer != null && isPlaying()) {
            if(mSeekTime > 0) {
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
    public void setSongList(List<Song> songList, int position) {
        if (songList == null) {
            return;
        }
        if (mSongList == null) {
            mSongList = new ArrayList<>(songList);
        } else {
            mSongList.clear();
            mSongList.addAll(songList);
        }

        int size = songList.size();
        if (position < 0) {
            position = 0;
        } else if (position > size - 1) {
            position = size - 1;
        }
        setSongIndex(position);

        Song song = mSongList.get(position);

        prepare(song.getSongPath());
    }

    private void setSongIndex(int songIndex) {
        mSongIndex = songIndex;
        SharedPreferencesTools.putIntValue(SharedPreferencesTools.KEY_INIT_SONG_INDEX, songIndex);

        if (mCallbackList != null) {
            try {
                int size = mCallbackList.beginBroadcast();
                for (int i = 0; i < size; i++) {
                    mCallbackList.getBroadcastItem(i).callbackIndex(songIndex);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mCallbackList.finishBroadcast();
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
                setSongIndex(index);
                break;
            case ControlConstants.MODE_RANDOM:
                if (mRandom == null) {
                    mRandom = new Random();
                }
                index = mRandom.nextInt(size());
                setSongIndex(index);
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
                if (mCallbackList != null && mPlayer != null) {
                    try {
                        int msec = mPlayer.getCurrentPosition();
                        SharedPreferencesTools.putIntValue(SharedPreferencesTools.KEY_INIT_SONG_MSEC, msec);
                        int size = mCallbackList.beginBroadcast();
                        for (int i = 0; i < size; i++) {
                            mCallbackList.getBroadcastItem(i).callbackMsec(msec);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        mCallbackList.finishBroadcast();
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

    private void changeDisplay(){
        if(mCallback != null){
            mCallback.changeDisplay(getCurrentSong(), isPlaying());
        }
    }

    @Override
    public Song getCurrentSong() {
        return mSongList != null ? mSongList.get(mSongIndex) : null;
    }
}
