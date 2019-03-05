package com.swak.app.core;

import android.util.Log;

/**
 * 日志工具类
 */
public class Logger {

    private static final String TAG = Logger.class.getSimpleName();

    /**
     * 上线后关闭log
     */
    private static Boolean DEBUG = true;

    /**
     * 控制是否关闭日志输出
     *
     * @param isDebug
     */
    public static void isDebug(boolean isDebug) {
        DEBUG = isDebug;
    }

    public static void d(String tag, String msg) {
        if (DEBUG) {
            tag = Thread.currentThread().getName() + ":" + tag;
            Log.d(TAG, tag + " : " + msg);
        }
    }

    public static void d(String tag, String msg, Throwable error) {
        if (DEBUG) {
            tag = Thread.currentThread().getName() + ":" + tag;
            Log.d(TAG, tag + " : " + msg, error);
        }
    }

    public static void i(String tag, String msg) {
        if (DEBUG) {
            tag = Thread.currentThread().getName() + ":" + tag;
            Log.i(TAG, tag + " : " + msg);
        }
    }

    public static void i(String tag, String msg, Throwable error) {
        if (DEBUG) {
            tag = Thread.currentThread().getName() + ":" + tag;
            Log.i(TAG, tag + " : " + msg, error);
        }
    }

    public static void w(String tag, String msg) {
        if (DEBUG) {
            tag = Thread.currentThread().getName() + ":" + tag;
            Log.w(TAG, tag + " : " + msg);
        }
    }

    public static void w(String tag, String msg, Throwable error) {
        if (DEBUG) {
            tag = Thread.currentThread().getName() + ":" + tag;
            Log.w(TAG, tag + " : " + msg, error);
        }
    }

    public static void e(String tag, String msg) {
        if (DEBUG) {
            tag = Thread.currentThread().getName() + ":" + tag;
            Log.e(TAG, tag + " : " + msg);
        }
    }

    public static void e(String tag, String msg, Throwable error) {
        if (DEBUG) {
            tag = Thread.currentThread().getName() + ":" + tag;
            Log.e(TAG, tag + " : " + msg, error);
        }
    }
}
