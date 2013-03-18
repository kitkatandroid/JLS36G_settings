/*
 * Copyright (C) 2012 The CyanogenMod project
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

package com.android.settings.cyanogenmod;

import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.view.IWindowManager;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

public class SystemSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {
    private static final String TAG = "SystemSettings";

    private static final String KEY_NAVIGATION_BAR = "navigation_bar";
    private static final String KEY_NAV_BUTTONS_EDIT = "nav_buttons_edit";
    private static final String KEY_NAV_BUTTONS_HEIGHT = "nav_buttons_height";
    private static final String KEY_PIE_CONTROL = "pie_control";

    private ListPreference mNavButtonsHeight;
   
    private PreferenceScreen mNotificationPulse;
    private PreferenceScreen mBatteryPulse;
    private PreferenceScreen mPieControl;
    private boolean mIsPrimary;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.system_settings);
	PreferenceScreen prefScreen = getPreferenceScreen();

	// Determine which user is logged in
        mIsPrimary = UserHandle.myUserId() == UserHandle.USER_OWNER;
        //if (mIsPrimary) {
            // Primary user only preferences
            // Battery lights
            //mBatteryPulse = (PreferenceScreen) findPreference(KEY_BATTERY_LIGHT);
            if (mBatteryPulse != null) {
                if (getResources().getBoolean(
                        com.android.internal.R.bool.config_intrusiveBatteryLed) == false) {
                    prefScreen.removePreference(mBatteryPulse);
                    mBatteryPulse = null;
                }
            }
	//}

        mNavButtonsHeight = (ListPreference) findPreference(KEY_NAV_BUTTONS_HEIGHT);
        mNavButtonsHeight.setOnPreferenceChangeListener(this);

        int statusNavButtonsHeight = Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                 Settings.System.NAV_BUTTONS_HEIGHT, 48);
        mNavButtonsHeight.setValue(String.valueOf(statusNavButtonsHeight));
        mNavButtonsHeight.setSummary(mNavButtonsHeight.getEntry());

        // Only show the hardware keys config on a device that does not have a navbar
        // Only show the navigation bar config on phones that has a navigation bar
        boolean removeKeys = false;
        boolean removeNavbar = false;
        IWindowManager windowManager = IWindowManager.Stub.asInterface(
                ServiceManager.getService(Context.WINDOW_SERVICE));
        try {
            if (windowManager.hasNavigationBar()) {
                removeKeys = true;
            } else {
                removeNavbar = true;
            }
        } catch (RemoteException e) {
            // Do nothing
        }

        // Act on the above
        if (removeNavbar) {
            getPreferenceScreen().removePreference(findPreference(KEY_NAVIGATION_BAR));
	    }

	// Pie controls
        mPieControl = (PreferenceScreen) findPreference(KEY_PIE_CONTROL);

        }

    @Override
    public void onResume() {
        super.onResume();

        // All users
        if (mNotificationPulse != null) {
            updateLightPulseDescription();
        }
        if (mPieControl != null) {
            updatePieControlDescription();
        }

        // Primary user only
        if (mIsPrimary && mBatteryPulse != null) {
            updateBatteryPulseDescription();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void updateLightPulseDescription() {
        if (Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.NOTIFICATION_LIGHT_PULSE, 0) == 1) {
            mNotificationPulse.setSummary(getString(R.string.notification_light_enabled));
        } else {
            mNotificationPulse.setSummary(getString(R.string.notification_light_disabled));
        }
    }

    private void updateBatteryPulseDescription() {
        if (Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.BATTERY_LIGHT_ENABLED, 1) == 1) {
            mBatteryPulse.setSummary(getString(R.string.notification_light_enabled));
        } else {
            mBatteryPulse.setSummary(getString(R.string.notification_light_disabled));
        }
     }

    private void updatePieControlDescription() {
        if (Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.PIE_CONTROLS, 0) == 1) {
            mPieControl.setSummary(getString(R.string.pie_control_enabled));
        } else {
            mPieControl.setSummary(getString(R.string.pie_control_disabled));
        }
    }
        
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if (preference == mNavButtonsHeight) {
            int statusNavButtonsHeight = Integer.valueOf((String) objValue);
            int index = mNavButtonsHeight.findIndexOfValue((String) objValue);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.NAV_BUTTONS_HEIGHT, statusNavButtonsHeight);
            mNavButtonsHeight.setSummary(mNavButtonsHeight.getEntries()[index]);
            return true;
        }
        return false;
    }
}

    //@Override
    //public void onResume() {
        //super.onResume();
    //}

    //@Override
    //public void onPause() {
        //super.onPause();

    //private void updatePieControlDescription() {
        //if (Settings.System.getInt(getActivity().getContentResolver(),
                //Settings.System.PIE_CONTROLS, 0) == 1) {
            //mPieControl.setSummary(getString(R.string.pie_control_enabled));
        //} else {
            //mPieControl.setSummary(getString(R.string.pie_control_disabled));
    //}
//}
