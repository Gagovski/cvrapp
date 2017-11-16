package com.gc.cvrapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.os.Bundle;
import android.preference.SwitchPreference;

import com.gc.cvrapp.AppConfig;
import com.gc.cvrapp.cvr.CvrSettings;
import com.gc.cvrapp.R;
import com.gc.cvrapp.cvr.Cvr;
import com.gc.cvrapp.service.CvrService;
import com.gc.cvrapp.service.CvrServiceManager;
import com.gc.cvrapp.service.CvrServiceManager.CvrServiceConnection;
import com.gc.cvrapp.utils.LogUtil;

public class SettingActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener, CvrServiceConnection {

    private Cvr mCvr;
    private CvrServiceManager mServiceManager;
    private CvrSettings mSettings;
    private ListPreference mRecordTimePref;
    private SwitchPreference mRecordVoiceEnPref;
    private Preference preferenceLog;
    private SwitchPreference mLogEnPref;
    private static final String TAG = "SettingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting);
        mServiceManager = new CvrServiceManager(this, AppConfig.APP_SERVICE);
        mServiceManager.setCallback(this);
        mSettings = new CvrSettings(this);
        initSettings();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mServiceManager.bindCvrService();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Preference preferenceFormat = getPreferenceScreen().findPreference(CvrSettings.KEY_FORMAT_SD);
        preferenceLog = getPreferenceScreen().findPreference(CvrSettings.KEY_SHOW_LOG);
        preferenceLog.setOnPreferenceClickListener(this);

        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        mRecordTimePref.setSummary(sharedPreferences.getString(CvrSettings.KEY_RECORD_TIME, "3min"));
        if (sharedPreferences.getBoolean(CvrSettings.KEY_RECORD_VOICE_EN, false)) {
            mRecordVoiceEnPref.setSummary("on");
        } else {
            mRecordVoiceEnPref.setSummary("off");
        }

        if (sharedPreferences.getBoolean(CvrSettings.KEY_LOG_EN, false)) {
            getPreferenceScreen().addPreference(preferenceLog);
            mLogEnPref.setSummary("on");
        } else {
            mLogEnPref.setSummary("off");
            getPreferenceScreen().removePreference(preferenceLog);
        }

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        preferenceFormat.setOnPreferenceClickListener(this);

    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.i(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.i(TAG, "onDestory");
        mServiceManager.unbindCvrService();
    }

    private void initSettings() {
        mRecordTimePref = (ListPreference)findPreference(CvrSettings.KEY_RECORD_TIME);
        mRecordVoiceEnPref = (SwitchPreference)findPreference(CvrSettings.KEY_RECORD_VOICE_EN);
        mLogEnPref = (SwitchPreference)findPreference(CvrSettings.KEY_LOG_EN);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(CvrSettings.KEY_RECORD_TIME)) {
            setRecordtime(sharedPreferences.getString(CvrSettings.KEY_RECORD_TIME, "3min"));
        } else if (key.equals(CvrSettings.KEY_RECORD_VOICE_EN)) {
            switchRecordvoice(sharedPreferences.getBoolean(CvrSettings.KEY_RECORD_VOICE_EN, false));
        } else if (key.equals(CvrSettings.KEY_LOG_EN)) {
            switchLog(sharedPreferences.getBoolean(CvrSettings.KEY_LOG_EN, false));
        } else {
            LogUtil.w(TAG, "Key: " + key);
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        if (key.equals("format_sd")) {
            mCvr.formatSD();
        } else if (key.equals("show_log")) {
            startActivity(new Intent(this, LogActivity.class));
        } else {
            ;
        }
        return false;
    }

    private void setRecordtime(String time) {
        mCvr.setting(mSettings);
        mRecordTimePref.setSummary(time);
    }

    private void switchRecordvoice(boolean isOn) {
        mCvr.setting(mSettings);
        if (isOn) {
            mRecordVoiceEnPref.setSummary("on");
        } else {
            mRecordVoiceEnPref.setSummary("off");
        }
    }

    private void switchLog(boolean isOn) {
        if (isOn) {
            AppConfig.APP_LOG_ENABLE = true;
            mLogEnPref.setSummary("on");
            getPreferenceScreen().addPreference(preferenceLog);
        } else {
            AppConfig.APP_LOG_ENABLE = false;
            mLogEnPref.setSummary("off");
            getPreferenceScreen().removePreference(preferenceLog);
        }
    }

    @Override
    public void onCvrConnected(Cvr cvr, CvrService service) {
        mCvr = cvr;
    }

    @Override
    public void onCvrDisconnected(CvrService service) {

    }
}
