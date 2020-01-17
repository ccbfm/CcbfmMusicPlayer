package com.ccbfm.music.player.data.model;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.ccbfm.music.player.BR;
import com.ccbfm.music.player.R;
import com.ccbfm.music.player.tool.Constants;
import com.ccbfm.music.player.ui.activity.SelectFolderActivity;
import com.ccbfm.music.player.ui.fragment.ScanningFragment;

public class ScanningModel extends BaseObservable implements View.OnClickListener {

    private static final int CODE_SELECT_FOLDER_NAME = 0x11;
    private ScanningFragment mFragment;
    private String mDirectoryName;

    public ScanningModel(ScanningFragment fragment) {
        mFragment = fragment;
        mDirectoryName = "Constants.ROOT_PATH";
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.music_scanning:
                break;
            case R.id.music_select_directory:
                SelectFolderActivity.startForResult(mFragment, "", CODE_SELECT_FOLDER_NAME);
                break;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(CODE_SELECT_FOLDER_NAME == requestCode && resultCode == Activity.RESULT_OK){
            if(data != null){
                String selectFolderName = data.getStringExtra(SelectFolderActivity.RESULT_SELECT_FOLDER_NAME);
                if(!TextUtils.isEmpty(selectFolderName)){
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
    }
}
