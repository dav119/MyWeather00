package ru.tolyan.myweather00;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Tolyan on 23.05.2016.
 */
public class DetailFragment extends Fragment {

    TextView txtDetailForecast;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_detail, container, false);

        txtDetailForecast = (TextView) root.findViewById(R.id.txtDetailForecast);

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            String info = intent.getStringExtra(Intent.EXTRA_TEXT);
            txtDetailForecast.setText(info);
        }

        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detail, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this.getActivity(), SettingsActivity.class);
                startActivity(intent);
        }

        return true;
    }
}
