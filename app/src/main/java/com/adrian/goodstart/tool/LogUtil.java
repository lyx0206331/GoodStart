package com.adrian.goodstart.tool;

import android.util.Log;

public class LogUtil {

    public static boolean IS_DEBUG = true;

    public static void e(String tag, String msg) {
        if (IS_DEBUG) {
            try {
                Log.e(tag, msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void i(String tag, String msg) {
        if (IS_DEBUG) {
            try {
                Log.i(tag, msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
