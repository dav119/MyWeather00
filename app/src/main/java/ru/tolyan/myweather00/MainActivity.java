package ru.tolyan.myweather00;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity   {

    public static final String LOG_TAG = "my_log";
    public static final String OK_URL = "api.openweathermap.org/data/2.5/forecast/daily?q=moscow,ru&cnt=7";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getFragmentManager().beginTransaction().add(R.id.frame_layout_container, new MainFragment()).commit();




    }


}
