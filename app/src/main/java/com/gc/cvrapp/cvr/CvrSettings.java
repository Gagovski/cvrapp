package com.gc.cvrapp.cvr;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class CvrSettings {
    public static final String KEY_RECORD_TIME      = "record_time";
    public static final String KEY_RECORD_EN        = "record_en";
    public static final String KEY_LOG_EN           = "log_en";
    public static final String KEY_RECORD_VOICE_EN  = "voice_en";
    public static final String KEY_FORMAT_SD        = "format_sd";
    public static final String KEY_SHOW_LOG         = "show_log";
    private SharedPreferences mSettings;

    public CvrSettings(Context context) {
        mSettings = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setEnRecord(boolean enRecord) {
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putBoolean(KEY_RECORD_EN, enRecord);
        editor.commit();
    }

    public int getRecordTime() {
        int time;
        String recordTime = mSettings.getString(KEY_RECORD_TIME, "3min");
        if (recordTime.contentEquals("1min")) {
            time = 1;
        } else if (recordTime.contentEquals("2min")) {
            time = 2;
        } else if (recordTime.contentEquals("3min")) {
            time = 3;
        } else if (recordTime.contentEquals("5min")) {
            time = 5;
        } else {
            time = 3;
        }
        return time;
    }

    public int getEnVoice() {
        int voiceEn;
        if (mSettings.getBoolean(KEY_RECORD_VOICE_EN, false)) {
            voiceEn = 1;
        } else {
            voiceEn = 0;
        }
        return voiceEn;
    }

    public boolean getEnRecord() {
        return mSettings.getBoolean(KEY_RECORD_EN, false);
    }
}
