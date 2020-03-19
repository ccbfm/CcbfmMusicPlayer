package com.ccbfm.music.player;

import com.ccbfm.music.player.tool.LogTools;
import com.ccbfm.music.player.tool.SystemTools;

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "CrashHandler";

    private static class SINGLE {
        private static CrashHandler INSTANCE = new CrashHandler();
    }

    private CrashHandler() {
    }

    public static CrashHandler getInstance() {
        return SINGLE.INSTANCE;
    }

    private Thread.UncaughtExceptionHandler mDefaultHandler;

    /**
     * 初始化
     */
    public void init() {
        //获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        //设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        if (!handleException(throwable) && mDefaultHandler != null) {
            //如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, throwable);
        } else {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                LogTools.e(TAG, "uncaughtException", "Exception ", e);
            }
            //退出程序
            SystemTools.killAppProcess(App.getApp());
        }

    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param e Throwable
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(Throwable e) {
        if (e == null) {
            return false;
        }
        LogTools.e(TAG, "handleException", "Exception ", e);
        return true;
    }
}
