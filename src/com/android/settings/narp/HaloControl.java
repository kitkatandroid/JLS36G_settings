package com.android.settings.narp;

import android.app.AlertDialog;
import android.app.INotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceClickListener;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.bluetooth.DeviceListPreferenceFragment;
import com.android.settings.Utils;

public class HaloControl extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {

    private static final String KEY_HALO_STATE = "halo_state";
    private static final String KEY_HALO_HIDE = "halo_hide";
    private static final String KEY_HALO_REVERSED = "halo_reversed";
    private static final String KEY_HALO_SIZE = "halo_size";
    private static final String KEY_HALO_NINJA = "halo_ninja";
    private static final String KEY_HALO_MSGBOX = "halo_msgbox";
    private static final String KEY_HALO_MSGBOX_ANIMATION = "halo_msgbox_animation";
    private static final String KEY_HALO_NOTIFY_COUNT = "halo_notify_count";
    private static final String KEY_HALO_UNLOCK_PING = "halo_unlock_ping";

    private ListPreference mHaloState;
    private CheckBoxPreference mHaloHide;
    private CheckBoxPreference mHaloReversed;
    private ListPreference mHaloSize;
    private ListPreference mHaloNotifyCount;
    private ListPreference mHaloMsgAnimate;
    private CheckBoxPreference mHaloNinja;
    private CheckBoxPreference mHaloMsgBox;
    private CheckBoxPreference mHaloUnlockPing;

    private Context mContext;
    private INotificationManager mNotificationManager;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.halo_control);
        PreferenceScreen prefSet = getPreferenceScreen();
        mContext = getActivity();

        mNotificationManager = INotificationManager.Stub.asInterface(
                ServiceManager.getService(Context.NOTIFICATION_SERVICE));

        mHaloState = (ListPreference) prefSet.findPreference(KEY_HALO_STATE);
        mHaloState.setValue(String.valueOf((isHaloPolicyBlack() ? "1" : "0")));
        mHaloState.setOnPreferenceChangeListener(this);

        mHaloHide = (CheckBoxPreference) prefSet.findPreference(KEY_HALO_HIDE);
        mHaloHide.setChecked(Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.HALO_HIDE, 0) == 1);

        mHaloNinja = (CheckBoxPreference) prefSet.findPreference(KEY_HALO_NINJA);
        mHaloNinja.setChecked(Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.HALO_NINJA, 0) == 1);

        mHaloMsgBox = (CheckBoxPreference) prefSet.findPreference(KEY_HALO_MSGBOX);
        mHaloMsgBox.setChecked(Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.HALO_MSGBOX, 1) == 1);

        mHaloUnlockPing = (CheckBoxPreference) prefSet.findPreference(KEY_HALO_UNLOCK_PING);
        mHaloUnlockPing.setChecked(Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.HALO_UNLOCK_PING, 0) == 1);

        mHaloNotifyCount = (ListPreference) prefSet.findPreference(KEY_HALO_NOTIFY_COUNT);
        try {
            int haloCounter = Settings.System.getInt(mContext.getContentResolver(),
                    Settings.System.HALO_NOTIFY_COUNT, 4);
            mHaloNotifyCount.setValue(String.valueOf(haloCounter));
        } catch(Exception ex) {
            // fail...
        }
        mHaloNotifyCount.setOnPreferenceChangeListener(this);

        mHaloMsgAnimate = (ListPreference) prefSet.findPreference(KEY_HALO_MSGBOX_ANIMATION);
        try {
            int haloMsgAnimation = Settings.System.getInt(mContext.getContentResolver(),
                    Settings.System.HALO_MSGBOX_ANIMATION, 2);
            mHaloMsgAnimate.setValue(String.valueOf(haloMsgAnimation));
        } catch(Exception ex) {
            // fail...
        }
        mHaloMsgAnimate.setOnPreferenceChangeListener(this);

        mHaloReversed = (CheckBoxPreference) prefSet.findPreference(KEY_HALO_REVERSED);
        mHaloReversed.setChecked(Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.HALO_REVERSED, 1) == 1);

        mHaloSize = (ListPreference) prefSet.findPreference(KEY_HALO_SIZE);
        try {
            float haloSize = Settings.System.getFloat(mContext.getContentResolver(),
                    Settings.System.HALO_SIZE, 1.0f);
            mHaloSize.setValue(String.valueOf(haloSize));  
        } catch(Exception ex) {
            // So what
        }
        mHaloSize.setOnPreferenceChangeListener(this);

    }

    private boolean isHaloPolicyBlack() {
        try {
            return mNotificationManager.isHaloPolicyBlack();
        } catch (android.os.RemoteException ex) {
                // System dead
        }
        return true;
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mHaloHide) {	
            Settings.System.putInt(mContext.getContentResolver(),
                    Settings.System.HALO_HIDE, mHaloHide.isChecked()
                    ? 1 : 0);
        } else if (preference == mHaloNinja) {
            Settings.System.putInt(mContext.getContentResolver(),
                    Settings.System.HALO_NINJA, mHaloNinja.isChecked()
                    ? 1 : 0);
        } else if (preference == mHaloMsgBox) {
            Settings.System.putInt(mContext.getContentResolver(),
                    Settings.System.HALO_MSGBOX, mHaloMsgBox.isChecked()
                    ? 1 : 0);
        } else if (preference == mHaloUnlockPing) {
            Settings.System.putInt(mContext.getContentResolver(),
                    Settings.System.HALO_UNLOCK_PING, mHaloUnlockPing.isChecked()
                    ? 1 : 0);	
        } else if (preference == mHaloReversed) {	
            Settings.System.putInt(mContext.getContentResolver(),
                    Settings.System.HALO_REVERSED, mHaloReversed.isChecked()
                    ? 1 : 0);
	}	
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {

        if (preference == mHaloState) {
            boolean state = Integer.valueOf((String) newValue) == 1;
            try {
                mNotificationManager.setHaloPolicyBlack(state);
            } catch (android.os.RemoteException ex) {
                // System dead
            }          
            return true;
        } else if (preference == mHaloSize) {
            float haloSize = Float.valueOf((String) newValue);
            Settings.System.putFloat(getActivity().getContentResolver(),
                    Settings.System.HALO_SIZE, haloSize);
            return true;
        } else if (preference == mHaloMsgAnimate) {
            int haloMsgAnimation = Integer.valueOf((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.HALO_MSGBOX_ANIMATION, haloMsgAnimation);
            return true;
        } else if (preference == mHaloNotifyCount) {
            int haloNotifyCount = Integer.valueOf((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.HALO_NOTIFY_COUNT, haloNotifyCount);
            return true;
        }
        return false;
    }
}
