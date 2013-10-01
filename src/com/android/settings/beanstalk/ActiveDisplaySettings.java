/*
 * Copyright (C) 2013 The ChameleonOS Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.beanstalk;

import android.content.Context;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import static android.hardware.Sensor.TYPE_LIGHT;
import static android.hardware.Sensor.TYPE_PROXIMITY;

public class ActiveDisplaySettings extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {
    private static final String TAG = "PornSettings";

    private static final String KEY_ENABLED = "ad_enable";
    private static final String KEY_SHOW_TEXT = "ad_text";
    private static final String KEY_ALL_NOTIFICATIONS = "ad_all_notifications";
    private static final String KEY_POCKET_MODE = "ad_pocket_mode";
    private static final String KEY_SUNLIGHT_MODE = "ad_sunlight_mode";
    private static final String KEY_REDISPLAY = "ad_redisplay";
    private static final String KEY_BRIGHTNESS = "ad_brightness";
    private static final String KEY_TIMEOUT = "ad_timeout";

    private SwitchPreference mEnabledPref;
    private CheckBoxPreference mShowTextPref;
    private CheckBoxPreference mAllNotificationsPref;
    private CheckBoxPreference mPocketModePref;
    private CheckBoxPreference mSunlightModePref;
    private ListPreference mRedisplayPref;
    private SeekBarPreference mBrightnessLevel;
    private ListPreference mDisplayTimeout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.active_display_settings);

        mEnabledPref = (SwitchPreference) findPreference(KEY_ENABLED);
        mEnabledPref.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.ENABLE_ACTIVE_DISPLAY, 0) == 1));
        mEnabledPref.setOnPreferenceChangeListener(this);

        mShowTextPref = (CheckBoxPreference) findPreference(KEY_SHOW_TEXT);
        mShowTextPref.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.ACTIVE_DISPLAY_TEXT, 0) == 1));

        mAllNotificationsPref = (CheckBoxPreference) findPreference(KEY_ALL_NOTIFICATIONS);
        mAllNotificationsPref.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.ACTIVE_DISPLAY_ALL_NOTIFICATIONS, 0) == 1));

        mPocketModePref = (CheckBoxPreference) findPreference(KEY_POCKET_MODE);
        mPocketModePref.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.ACTIVE_DISPLAY_POCKET_MODE, 0) == 1));
        if (!hasProximitySensor()) {
            getPreferenceScreen().removePreference(mPocketModePref);
        }

        mSunlightModePref = (CheckBoxPreference) findPreference(KEY_SUNLIGHT_MODE);
        mSunlightModePref.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.ACTIVE_DISPLAY_SUNLIGHT_MODE, 0) == 1));
        if (!hasLightSensor()) {
            getPreferenceScreen().removePreference(mSunlightModePref);
        }

        PreferenceScreen prefSet = getPreferenceScreen();
        mRedisplayPref = (ListPreference) prefSet.findPreference(KEY_REDISPLAY);
        mRedisplayPref.setOnPreferenceChangeListener(this);
        long timeout = Settings.System.getLong(getContentResolver(),
                Settings.System.ACTIVE_DISPLAY_REDISPLAY, 0);
        mRedisplayPref.setValue(String.valueOf(timeout));
        updateRedisplaySummary(timeout);

        mBrightnessLevel = (SeekBarPreference) findPreference(KEY_BRIGHTNESS);
        mBrightnessLevel.setValue(Settings.System.getInt(getContentResolver(),
                Settings.System.ACTIVE_DISPLAY_BRIGHTNESS, 100));
        mBrightnessLevel.setOnPreferenceChangeListener(this);

	mDisplayTimeout = (ListPreference) prefSet.findPreference(KEY_TIMEOUT);
        mDisplayTimeout.setOnPreferenceChangeListener(this);
        timeout = Settings.System.getLong(getContentResolver(),
                Settings.System.ACTIVE_DISPLAY_TIMEOUT, 8000L);
        mDisplayTimeout.setValue(String.valueOf(timeout));
        updateTimeoutSummary(timeout);

    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mRedisplayPref) {
            int timeout = Integer.valueOf((String) newValue);
            updateRedisplaySummary(timeout);
            return true;
        } else if (preference == mEnabledPref) {
            Settings.System.putInt(getContentResolver(),
                    Settings.System.ENABLE_ACTIVE_DISPLAY,
                    ((Boolean) newValue).booleanValue() ? 1 : 0);
            return true;
        } else if (preference == mBrightnessLevel) {
            int brightness = ((Integer)newValue).intValue();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.ACTIVE_DISPLAY_BRIGHTNESS, brightness);
            return true;
	} else if (preference == mDisplayTimeout) {
            long timeout = Integer.valueOf((String) newValue);
            updateTimeoutSummary(timeout);
            return true;
        }
        return false;
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;

        if (preference == mShowTextPref) {
            value = mShowTextPref.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.ACTIVE_DISPLAY_TEXT,
                    value ? 1 : 0);
        } else if (preference == mAllNotificationsPref) {
            value = mAllNotificationsPref.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.ACTIVE_DISPLAY_ALL_NOTIFICATIONS,
                    value ? 1 : 0);
        } else if (preference == mPocketModePref) {
            value = mPocketModePref.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.ACTIVE_DISPLAY_POCKET_MODE,
                    value ? 1 : 0);
        } else if (preference == mSunlightModePref) {
            value = mSunlightModePref.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.ACTIVE_DISPLAY_SUNLIGHT_MODE,
                    value ? 1 : 0);
        } else {
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }

        return true;
    }

    private void updateRedisplaySummary(long value) {
        mRedisplayPref.setSummary(mRedisplayPref.getEntries()[mRedisplayPref.findIndexOfValue("" + value)]);
        Settings.System.putLong(getContentResolver(),
                Settings.System.ACTIVE_DISPLAY_REDISPLAY, value);
    }

   private void updateTimeoutSummary(long value) {
        try {
            mDisplayTimeout.setSummary(mDisplayTimeout.getEntries()[mDisplayTimeout.findIndexOfValue("" + value)]);
            Settings.System.putLong(getContentResolver(),
                    Settings.System.ACTIVE_DISPLAY_TIMEOUT, value);
        } catch (ArrayIndexOutOfBoundsException e) {
        }
   }

    private boolean hasProximitySensor() {
        SensorManager sm = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        return sm.getDefaultSensor(TYPE_PROXIMITY) != null;
    }

    private boolean hasLightSensor() {
        SensorManager sm = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        return sm.getDefaultSensor(TYPE_LIGHT) != null;
    }
}
