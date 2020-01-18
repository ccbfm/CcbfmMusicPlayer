package com.ccbfm.music.player.tool;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public final class SharedPreferencesTools {

    private static final String SHARED_MUSIC_PLAYER = "shared_music_player";
    private static Application sContext;
    private static SharedPreferences sPreferences;

    public static final String KEY_INIT_SHOW_PAGE = "init_show_page";

    public static void init(Application context){
        sContext = context;
    }


    public static int getIntValue(String key){
        return getIntValue(key, 0);
    }

    public static int getIntValue(String key, int defValue){
        checkPreferences();
        return sPreferences.getInt(key, defValue);
    }

    public static void putIntValue(String key, int value){
        checkPreferences();
        sPreferences.edit().putInt(key, value).apply();
    }

    public static String getStringValue(String key){
        return getStringValue(key, "");
    }

    public static String getStringValue(String key, String defValue){
        checkPreferences();
        return sPreferences.getString(key, defValue);
    }

    public static void putStringValue(String key, String value){
        checkPreferences();
        sPreferences.edit().putString(key, value).apply();
    }

    private static void checkPreferences(){
        if(sPreferences == null){
            if(sContext == null){
                throw new NullPointerException("Context == null");
            }
            sPreferences = sContext.getSharedPreferences(SHARED_MUSIC_PLAYER, Context.MODE_PRIVATE);
        }
    }
}
