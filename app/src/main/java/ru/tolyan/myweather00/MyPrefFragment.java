package ru.tolyan.myweather00;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by Tolyan on 23.05.2016.
 */
public class MyPrefFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String LOG_TAG = "my_log";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(LOG_TAG, "current location: " + sharedPreferences.getString("location", "empty"));

    }

    @Override
    public void onResume() {
            super.onResume();
        PreferenceManager.getDefaultSharedPreferences(this.getActivity()).registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onPause() {
        super.onPause();
        PreferenceManager.getDefaultSharedPreferences(this.getActivity()).unregisterOnSharedPreferenceChangeListener(this);
    }
}
