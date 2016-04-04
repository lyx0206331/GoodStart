package com.adrian.goodstart.tool;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by adrian on 16-4-2.
 */
public class SharePrefUtil {

    public static void setWifiSsid(Context context, String value) {
        SharedPreferences pref = context.getSharedPreferences("goodStartPref", Context.MODE_PRIVATE);
        pref.edit().putString("wifi_ssid", value).commit();
    }

    public static String getWifiSsid(Context context) {
        SharedPreferences pref = context.getSharedPreferences("goodStartPref", Context.MODE_PRIVATE);
        return pref.getString("wifi_ssid", null);
    }
}
