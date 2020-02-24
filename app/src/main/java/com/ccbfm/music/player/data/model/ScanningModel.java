package com.ccbfm.music.player.data.model;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.ccbfm.music.player.BR;
import com.ccbfm.music.player.R;
import com.ccbfm.music.player.database.SongLoader;
import com.ccbfm.music.player.tool.Constants;
import com.ccbfm.music.player.tool.LiveDataBus;
import com.ccbfm.music.player.tool.SharedPreferencesTools;
import com.ccbfm.music.player.tool.ToastTools;
import com.ccbfm.music.player.ui.activity.SelectFolderActivity;
import com.ccbfm.music.player.ui.fragment.ScanningFragment;

import static com.ccbfm.music.player.tool.Constants.SCAN_SUCCESS_NOTIFICATION;

public class ScanningModel extends BaseObservable implements View.OnClickListener {

    private static final int CODE_SELECT_FOLDER_NAME = 0x11;
    private static final String KEY_SELECT_DIRECTORY_NAME = "select_directory_name";
    private ScanningFragment mFragment;
    private String mDirectoryName;
    private LoadSong mLoadSong;

    public ScanningModel(ScanningFragment fragment) {
        mFragment = fragment;
        mDirectoryName = SharedPreferencesTools.getStringValue(KEY_SELECT_DIRECTORY_NAME, Constants.ROOT_PATH);
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.music_scanning:
                if (mLoadSong == null) {
                    mLoadSong = new LoadSong(this);
                    mLoadSong.execute(mDirectoryName);
                } else {
                    ToastTools.showToast(mFragment.getContext(), "扫描中。。。");
                }
                break;
            case R.id.music_select_directory:
                SelectFolderActivity.startForResult(mFragment, getDirectoryName(), CODE_SELECT_FOLDER_NAME);
                break;
        }
    }


    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (CODE_SELECT_FOLDER_NAME == requestCode && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                String selectFolderName = data.getStringExtra(SelectFolderActivity.RESULT_SELECT_FOLDER_NAME);
                if (!TextUtils.isEmpty(selectFolderName)) {
                    setDirectoryName(selectFolderName);
                }
            }
        }
    }

    @Bindable
    public String getDirectoryName() {
        return mDirectoryName;
    }

    public void setDirectoryName(String directoryName) {
        mDirectoryName = directoryName;
        notifyPropertyChanged(BR.directoryName);
        SharedPreferencesTools.putStringValue(KEY_SELECT_DIRECTORY_NAME, directoryName);
    }

    private void loadSongEnd(boolean isOk) {
        mLoadSong = null;
        ToastTools.showToast(mFragment.getContext(), "扫描完成：" + (isOk ? "成功" : "失败"));
        if(isOk) {
            LiveDataBus.get().<Boolean>with(SCAN_SUCCESS_NOTIFICATION).postValue(true);
        }
    }

    private static class LoadSong extends AsyncTask<String, Integer, Boolean> {
        private ScanningModel mScanningModel;

        private LoadSong(ScanningModel scanningModel) {
            mScanningModel = scanningModel;
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            String path = mScanningModel.getDirectoryName();
            return SongLoader.loadAudioSong(mScanningModel.mFragment.getContext(), path);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            mScanningModel.loadSongEnd(aBoolean);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            mScanningModel.loadSongEnd(false);
        }
    }
}
