package com.ccbfm.music.player.data.model;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.ccbfm.music.player.BR;
import com.ccbfm.music.player.data.adapter.SelectFolderAdapter;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SelectFolderModel extends BaseObservable {

    private String mRootPath;
    private String[] mSuffix;
    private SelectFolderAdapter mSelectFolderAdapter;

    public SelectFolderModel(String rootPath) {
        this(rootPath, null);
    }

    public SelectFolderModel(String rootPath, String[] suffix) {
        mRootPath = rootPath;
        mSuffix = suffix;
        List<File> files = getListFiles(rootPath);
        SelectFolderAdapter adapter = new SelectFolderAdapter(files);
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
                    return false;
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
        if(files == null || files.length == 0){
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

    @Bindable
    public String getRootPath() {
        return mRootPath;
    }

    public void setRootPath(String rootPath) {
        mRootPath = rootPath;
        notifyPropertyChanged(BR.rootPath);
    }

    @Bindable
    public SelectFolderAdapter getSelectFolderAdapter() {
        return mSelectFolderAdapter;
    }

    public void setSelectFolderAdapter(SelectFolderAdapter selectFolderAdapter) {
        mSelectFolderAdapter = selectFolderAdapter;
        notifyPropertyChanged(BR.selectFolderAdapter);
    }
}
