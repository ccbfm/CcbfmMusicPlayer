package com.ccbfm.music.player.aidl;

import android.os.RemoteException;

import com.ccbfm.music.player.IPlayerCallback;

public class IPlayerCallbackStub extends IPlayerCallback.Stub {
    @Override
    public void callbackIndex(int index) throws RemoteException {

    }

    @Override
    public void callbackMsec(int msec) throws RemoteException {

    }
}
