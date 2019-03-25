package homounikumus1.com.myweatherviewer.screen.sync_screen;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.preference.ListPreference;
import android.preference.PreferenceFragment;

import android.preference.SwitchPreference;
import android.support.design.widget.Snackbar;

import homounikumus1.com.myweatherviewer.R;
import homounikumus1.com.myweatherviewer.WeatherApp;
import homounikumus1.com.myweatherviewer.utils.DatabaseUtils;

public class SyncSettings extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener, SyncSettingsActivity.GeoListener {
    private final String[] preferences = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
    private SwitchPreference geoSwitch;
    /**
     * Key for SwitchPreference - provision of access to geolocation
     */
    private static final String KEY_GEO = "KEY_GEO";
    /**
     * Key for SwitchPreference - ON or OFF notifications
     */
    private static final String KEY_NOTIFICATION = "KEY_NOTIFICATION";
    /**
     * Key for SwitchPreference - ON or OFF autosync
     */
    private static final String KEY_AUTO_SYNC = "KEY_AUTO_SYNC";
    /**
     * Key for ListPreference - to select auto sync intervals
     */
    private static final String KEY_AUTO_SYNC_INTERVAL = "KEY_AUTO_SYNC_INTERVAL";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        SyncSettingsActivity.setGeoListener(this);
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.sync_prefs);

        geoSwitch = (SwitchPreference) findPreference(KEY_GEO);
        if (getActivity().checkSelfPermission(preferences[0]) == PackageManager.PERMISSION_GRANTED && getActivity().checkSelfPermission(preferences[1]) == PackageManager.PERMISSION_GRANTED) {
            geoSwitch.setChecked(true);
        } else
            geoSwitch.setChecked(false);


        geoSwitch.setOnPreferenceChangeListener((pref, key) -> {
            /**
             * If permission is granted, we notify the user that he can recall it in the device settings
             */
            if (getActivity().checkSelfPermission(preferences[0]) == PackageManager.PERMISSION_GRANTED && getActivity().checkSelfPermission(preferences[1]) == PackageManager.PERMISSION_GRANTED) {
                Snackbar.make(getActivity().findViewById(R.id.settings), getString(R.string.geo_switch_explanetion), Snackbar.LENGTH_LONG).show();
                geoSwitch.setChecked(true);
            } else {
                /**
                 * In other cases - send an access request
                 */
                getActivity().requestPermissions(preferences, 1);
            }
            return false;
        });

        final ListPreference interval = (ListPreference) getPreferenceManager().findPreference(KEY_AUTO_SYNC_INTERVAL);
        if (interval != null)
            interval.setSummary(interval.getEntry());
    }


    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        switch (key) {
            case KEY_AUTO_SYNC:
                if (prefs.getBoolean(key, false)) {
                    /**
                     * If SwitchPreference is switched to the ON position  - we add auto-synchronization with the selected interval,
                     * if the interval is not selected, the default interval - “Two times a day” will be added
                     */
                    long interval = Long.parseLong(prefs.getString(KEY_AUTO_SYNC_INTERVAL, "auto_sync_interval_default"));
                    ContentResolver.addPeriodicSync(WeatherApp.sAccount, WeatherApp.AUTHORITY, Bundle.EMPTY, interval);
                } else {
                    /**
                     * If SwitchPreference is switched to the OFF position - removed autho-sync
                     */
                    ContentResolver.removePeriodicSync(WeatherApp.sAccount, WeatherApp.AUTHORITY, new Bundle());
                }
                break;
            case KEY_NOTIFICATION:
                /**
                 * When switching a SwitchPreference,
                 * we save the current value to the database, later it will be used during auto-sync,
                 * if the value is in the “true” position, the notification will not be shown
                 */
                if (DatabaseUtils.isShowNotification()) {
                    DatabaseUtils.setIsNotificationShow(false);
                } else {
                    DatabaseUtils.setIsNotificationShow(true);
                }

                break;
            case KEY_AUTO_SYNC_INTERVAL:
                /**
                 * If the SwitchPreference is in the "on" position,
                 * we can select the desired synchronization interval option
                 */
                final ListPreference interval = (ListPreference) getPreferenceManager().findPreference(key);
                if (interval != null) {
                    interval.setSummary(interval.getEntry());
                    ContentResolver.addPeriodicSync(WeatherApp.sAccount, WeatherApp.AUTHORITY, Bundle.EMPTY, Long.parseLong(interval.getValue()));
                }
                break;
        }
    }

    @Override
    public void onClick(boolean result) {
        if (result)
            geoSwitch.setChecked(true);
        else
            geoSwitch.setChecked(false);

    }
}
