package com.gc.cvrapp.utils;

import android.util.Log;

import com.gc.cvrapp.AppConfig;

public final class LogUtil {

    public static void v(String tag, String msg) {
        if (AppConfig.APP_LOG_ENABLE) {
            Log.v(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (AppConfig.APP_LOG_ENABLE) {
            Log.i(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (AppConfig.APP_LOG_ENABLE) {
            Log.d(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        Log.w(tag, msg);
    }

    public static void e(String tag, String msg) {
        Log.e(tag, msg);
    }
}
