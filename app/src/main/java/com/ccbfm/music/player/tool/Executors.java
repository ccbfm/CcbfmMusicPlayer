package com.ccbfm.music.player.tool;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class Executors {

    public static final Executor EXECUTOR = new ThreadPoolExecutor(1, 5, 1,
            TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(100));
}
