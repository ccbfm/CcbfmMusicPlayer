// IPlayerCallback.aidl
package com.ccbfm.music.player;

// Declare any non-default types here with import statements

interface IPlayerCallback {

    void callbackIndex(int index);

    void callbackMsec(int msec);
}
