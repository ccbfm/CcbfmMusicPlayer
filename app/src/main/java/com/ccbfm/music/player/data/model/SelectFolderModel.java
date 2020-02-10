package com.ccbfm.music.player.data.model;

import android.view.View;
import android.widget.ListView;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.ccbfm.music.player.BR;
import com.ccbfm.music.player.R;
import com.ccbfm.music.player.data.adapter.SelectFolderAdapter;
import com.ccbfm.music.player.tool.Constants;
import com.ccbfm.music.player.tool.FileTools;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class SelectFolderModel extends BaseObservable implements View.OnClickListener {

    private String mRootPath;
    private String[] mSuffix;
    private SelectFolderAdapter mSelectFolderAdapter;
    private int mBackVisible = View.VISIBLE;
    private ListView mListView;
    private HashMap<String, Integer> mBackStack = new HashMap<>(8);

    public SelectFolderModel(String rootPath, ListView listView) {
        this(rootPath, listView, null);
    }

    public SelectFolderModel(String rootPath, ListView listView, String[] suffix) {
        setRootPath(rootPath);
        mSuffix = suffix;
        mListView = listView;
        if (listView == null) {
            throw new NullPointerException("listView为空！");
        }

        List<File> files = getListFiles(rootPath);
        SelectFolderAdapter adapter = new SelectFolderAdapter(files);
        adapter.setOnItemClickListener(new SelectFolderAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(File file) {
                if (file != null && file.isDirectory()) {
                    updateBackStack();
                    updateUI(file.getPath());
                }
            }
        });
        setSelectFolderAdapter(adapter);
    }

    private List<File> getListFiles(String path) {
        return FileTools.getListFiles(path, mSuffix);
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.music_back_root_path:
                mBackStack.clear();
                updateUI(Constants.ROOT_PATH);
                break;
            case R.id.music_back_pre:
                String parentPath = new File(mRootPath).getParent();
                updateUI(parentPath);
                break;
        }
    }

    private void updateUI(String path) {
        if (setRootPath(path)) {
            mSelectFolderAdapter.updateFiles(getListFiles(path), mListView, mBackStack.remove(path));
        }
    }

    private void updateBackStack() {
        mBackStack.put(getRootPath(), mListView.getFirstVisiblePosition());
    }

    @Bindable
    public String getRootPath() {
        return mRootPath;
    }

    public boolean setRootPath(String rootPath) {
        if (rootPath != null && rootPath.startsWith(Constants.ROOT_PATH)) {
            mRootPath = rootPath;
            notifyPropertyChanged(BR.rootPath);
            if (Constants.ROOT_PATH.equals(rootPath)) {
                setBackVisible(View.GONE);
            } else {
                setBackVisible(View.VISIBLE);
            }
            return true;
        }
        return false;
    }

    @Bindable
    public SelectFolderAdapter getSelectFolderAdapter() {
        return mSelectFolderAdapter;
    }

    public void setSelectFolderAdapter(SelectFolderAdapter selectFolderAdapter) {
        mSelectFolderAdapter = selectFolderAdapter;
        notifyPropertyChanged(BR.selectFolderModel);
    }

    @Bindable
    public int getBackVisible() {
        return mBackVisible;
    }

    public void setBackVisible(int backVisible) {
        mBackVisible = backVisible;
        notifyPropertyChanged(BR.backVisible);
    }
}
