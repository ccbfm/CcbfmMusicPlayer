package com.ccbfm.music.player.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.ccbfm.music.player.R;
import com.ccbfm.music.player.data.model.SelectFolderModel;
import com.ccbfm.music.player.databinding.ActivitySelectFolderBinding;
import com.ccbfm.music.player.tool.Constants;
import com.ccbfm.music.player.tool.StartActivityTools;
import com.ccbfm.music.player.ui.fragment.BaseFragment;

public class SelectFolderActivity extends BaseActivity<ActivitySelectFolderBinding> {
    public static final String RESULT_SELECT_FOLDER_NAME = "result_select_folder_name";
    public static final String SELECT_ROOT_FOLDER_PATH = "select_root_folder_path";

    private SelectFolderModel mSelectFolderModel;

    public static void startForResult(BaseActivity activity, String rootPath, int requestCode){
        if(TextUtils.isEmpty(rootPath)){
            rootPath = Constants.ROOT_PATH;
        }
        Bundle bundle = new Bundle();
        bundle.putString(SELECT_ROOT_FOLDER_PATH, rootPath);
        StartActivityTools.startForResult(activity, SelectFolderActivity.class, bundle, requestCode);
    }

    public static void startForResult(BaseFragment fragment, String rootPath, int requestCode){
        if(TextUtils.isEmpty(rootPath)){
            rootPath = Constants.ROOT_PATH;
        }
        Bundle bundle = new Bundle();
        bundle.putString(SELECT_ROOT_FOLDER_PATH, rootPath);
        StartActivityTools.startForResult(fragment, SelectFolderActivity.class, bundle, requestCode);
    }

    @Override
    protected void initView(ActivitySelectFolderBinding binding) {
        Intent intent = getIntent();
        String rootPath = null;
        if(intent != null){
            rootPath = intent.getStringExtra(SELECT_ROOT_FOLDER_PATH);
        }
        if(TextUtils.isEmpty(rootPath)){
            rootPath = Constants.ROOT_PATH;
        }
        SelectFolderModel model = new SelectFolderModel(rootPath, binding.musicSelectDirectoryList);
        mSelectFolderModel = model;
        binding.setSelectFolderModel(model);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_select_folder;
    }

    public void selectCancel(View view){
        setResult(RESULT_CANCELED);
        onBackPressed();
    }

    public void selectConfirm(View view){
        Intent intent = new Intent();
        intent.putExtra(RESULT_SELECT_FOLDER_NAME, mSelectFolderModel.getRootPath());
        setResult(RESULT_OK, intent);
        onBackPressed();
    }
}
