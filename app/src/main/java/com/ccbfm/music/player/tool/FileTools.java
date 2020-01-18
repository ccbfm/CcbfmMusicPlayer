package com.ccbfm.music.player.tool;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FileTools {

    public static List<File> getListFiles(String path, final String[] suffix) {
        if (path == null) {
            return new ArrayList<>();
        }
        File file = new File(path);
        File[] files = file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (suffix == null || suffix.length == 0) {
                    return true;
                }
                if (pathname.isFile()) {
                    for (String suffix : suffix) {
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
}
