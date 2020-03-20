package com.ccbfm.music.player.tool;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * SharedPreferences
 */
public final class SPTools {
    private static final String TAG = "SPTools";
    private static final boolean DEBUG = false;

    private static final String SHARED_MUSIC_PLAYER = "shared_music_player";
    private static Application sContext;
    private static SharedPreferences sPreferences;

    public static final String KEY_INIT_SHOW_PAGE = "init_show_page";

    public static final String KEY_INIT_PLAYLIST_INDEX = "init_playlist_index";
    public static final String KEY_INIT_SONG_INDEX = "init_song_index";
    public static final String KEY_INIT_SONG_MSEC = "init_song_msec";
    public static final String KEY_NEED_SEEK_TO = "need_seek_to";
    public static final String KEY_INIT_PLAY_MODE = "init_play_mode";

    public static void init(Application context) {
        sContext = context;
    }


    public static int getIntValue(String key) {
        return getIntValue(key, 0);
    }

    public static int getIntValue(String key, int defValue) {
        checkPreferences();
        int value = sPreferences.getInt(key, defValue);
        if (DEBUG) {
            LogTools.w(TAG, "getIntValue", key + " = " + value);
        }
        return value;
    }

    public static void putIntValue(String key, int value) {
        checkPreferences();
        if (DEBUG) {
            LogTools.w(TAG, "putIntValue", key + " = " + value);
        }

        if (TextUtils.equals(key, KEY_INIT_PLAYLIST_INDEX)) {
            int index = getIntValue(KEY_INIT_PLAYLIST_INDEX);
            if(index != value){
                putIntValue(KEY_NEED_SEEK_TO, 1);

            }
        } else if(TextUtils.equals(key, KEY_INIT_SONG_INDEX)){
            int index = getIntValue(KEY_INIT_SONG_INDEX);
            if(index != value){
                putIntValue(KEY_NEED_SEEK_TO, 1);
            }
        }
        sPreferences.edit().putInt(key, value).apply();
    }

    public static String getStringValue(String key) {
        return getStringValue(key, "");
    }

    public static String getStringValue(String key, String defValue) {
        checkPreferences();
        return sPreferences.getString(key, defValue);
    }

    public static void putStringValue(String key, String value) {
        checkPreferences();
        sPreferences.edit().putString(key, value).apply();
    }

    private static void checkPreferences() {
        if (sPreferences == null) {
            synchronized (SPTools.class) {
                if (sContext == null) {
                    throw new NullPointerException("Context == null");
                }
                if (sPreferences == null) {
                    sPreferences = sContext.getSharedPreferences(SHARED_MUSIC_PLAYER, Context.MODE_PRIVATE);
                }
            }
        }
    }
}
