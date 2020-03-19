package com.ccbfm.music.player.tool;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class Executors {

    public static final Executor SONG_LOADER_EXECUTOR = new ThreadPoolExecutor(1,
            2, 1, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(100));

    private static final HandlerThread HANDLER_THREAD = new HandlerThread("HandlerThread");

    public static Looper getHandlerLooper(){
        if(!HANDLER_THREAD.isAlive()){
            HANDLER_THREAD.start();
        }
        return HANDLER_THREAD.getLooper();
    }
}
