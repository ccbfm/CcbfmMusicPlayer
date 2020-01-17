package com.ccbfm.music.player.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import androidx.fragment.app.Fragment;

import com.ccbfm.music.player.R;
import com.ccbfm.music.player.data.model.SelectFolderModel;
import com.ccbfm.music.player.databinding.ActivitySelectFolderBinding;
import com.ccbfm.music.player.tool.Constants;

public class SelectFolderActivity extends BaseActivity<ActivitySelectFolderBinding> {
    public static final String RESULT_SELECT_FOLDER_NAME = "result_select_folder_name";
    public static final String SELECT_ROOT_FOLDER_PATH = "select_root_folder_path";

    private SelectFolderModel mSelectFolderModel;

    public static void startForResult(Activity activity, String rootPath, int requestCode){
        Intent intent = new Intent(activity, SelectFolderActivity.class);
        if(TextUtils.isEmpty(rootPath)){
            rootPath = Constants.ROOT_PATH;
        }
        intent.putExtra(SELECT_ROOT_FOLDER_PATH, rootPath);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void startForResult(Fragment fragment, String rootPath, int requestCode){
        Intent intent = new Intent(fragment.getContext(), SelectFolderActivity.class);
        if(TextUtils.isEmpty(rootPath)){
            rootPath = Constants.ROOT_PATH;
        }
        intent.putExtra(SELECT_ROOT_FOLDER_PATH, rootPath);
        fragment.startActivityForResult(intent, requestCode);
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
        SelectFolderModel model = new SelectFolderModel(rootPath);
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
