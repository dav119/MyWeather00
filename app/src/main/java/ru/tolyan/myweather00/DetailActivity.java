package ru.tolyan.myweather00;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        getFragmentManager().beginTransaction().add(R.id.detail_fragment_container, new DetailFragment()).commit();
    }
}
