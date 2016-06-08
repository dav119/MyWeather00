package ru.tolyan.myweather00;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Tolyan on 20.05.2016.
 */
public class MainFragment extends Fragment {

    public static final String LOG_TAG = "my_log";

    ArrayAdapter<String> adapter;


    String cityId;
    SharedPreferences sp;
    // added n master
    ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sp = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        cityId = sp.getString("location", "");


        MyTask mt = new MyTask(this);
        mt.execute(cityId);

        setHasOptionsMenu(true);


    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        String[] data = {
                "Mon 6/23 - Sunny - 31/17",
                "Tue 6/24 - Foggy - 21/8",
                "Wed 6/25 - Cloudy - 22/17",
                "Thurs 6/26 - Rainy - 18/11",
                "Fri 6/27 - Foggy - 21/10",
                "Sat 6/28 - TRAPPED IN WEATHERSTATION - 23/18",
                "Sun 6/29 - Sunny - 20/7"
        };
        List<String> listForecast = new ArrayList<String>(Arrays.asList(data));

        adapter = new ArrayAdapter<String>(getActivity(), R.layout.forecast_item, R.id.textView, listForecast);

        View root = inflater.inflate(R.layout.fragment_layout, container, false);

        listView = (ListView) root.findViewById(R.id.listView);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, adapter.getItem(position));
                startActivity(intent);
            }
        });

        return root;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.refresh:
                sp = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
                cityId = sp.getString("location", "");
                MyTask mt = new MyTask(this);
                mt.execute(cityId);
                break;
            case R.id.settings:
                intent = new Intent(this.getActivity(), SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.action_map:
                openPrefferedLocationOnMap();
                break;
        }
        return true;
    }

    private void openPrefferedLocationOnMap() {
        sp = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        String location = sp.getString("location", "");

        Uri uri = Uri.parse("geo:0,0?").buildUpon().appendQueryParameter("q", location).build();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);



        if (intent.resolveActivity(this.getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }


}
