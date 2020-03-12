// IPlayerCallback.aidl
package com.ccbfm.music.player;

// Declare any non-default types here with import statements
import com.ccbfm.music.player.database.entity.Song;

interface IPlayerCallback {

    void callbackIndex(int index);

    void callbackMsec(int msec);

    void callbackError(int code, in Song song);
}
