package com.ccbfm.music.player.data.model;

import android.view.View;
import android.widget.ListView;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.ccbfm.music.player.BR;
import com.ccbfm.music.player.R;
import com.ccbfm.music.player.data.adapter.SelectFolderAdapter;
import com.ccbfm.music.player.tool.Constants;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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
        if (path == null) {
            return new ArrayList<>();
        }
        File file = new File(path);
        File[] files = file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (mSuffix == null || mSuffix.length == 0) {
                    return true;
                }
                if (pathname.isFile()) {
                    for (String suffix : mSuffix) {
                        if (pathname.getName().endsWith(suffix)) {
                            return true;
                        }
                    }
                }
                return false;
            }
        });
        if (files == null || files.length == 0) {
            return new ArrayList<>();
        }
        List<File> fileList = Arrays.asList(files);
        Collections.sort(fileList, COMPARATOR);
        return fileList;
    }

    private static final Comparator<File> COMPARATOR = new Comparator<File>() {
        @Override
        public int compare(File o1, File o2) {
            if (o1.isFile() && o2.isDirectory()) {
                return 1;
            } else if (o1.isDirectory() && o2.isFile()) {
                return -1;
            }
            return o1.getName().compareTo(o2.getName());
        }
    };

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
        notifyPropertyChanged(BR.selectFolderAdapter);
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
