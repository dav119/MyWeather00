package ru.tolyan.myweather00;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class SettingsActivity extends AppCompatActivity {

    public static final String LOG_TAG = "my_log";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getFragmentManager().beginTransaction().add(R.id.settings_frag_container, new MyPrefFragment()).commit();
    }

}
