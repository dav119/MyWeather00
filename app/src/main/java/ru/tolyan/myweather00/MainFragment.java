package ru.tolyan.myweather00;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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

        ;

        MyTask mt = new MyTask();
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
        switch (item.getItemId()) {
            case R.id.refresh:
                sp = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
                cityId = sp.getString("location", "");
                MyTask mt = new MyTask();
                mt.execute(cityId);
                break;
            case R.id.settings:
                Intent intent = new Intent(this.getActivity(), SettingsActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    public class MyTask extends AsyncTask<String, Void, String> {



        @Override
        protected String doInBackground(String... params) {
            return getForecastData(params[0]);
        }


        @Override
        protected void onPostExecute(String s) {
            adapter.clear();
            adapter.addAll(WeatherDataParser.getDataFromJson(s));
        }

        private String getForecastData(String cityId) {

            OkHttpClient okHttpClient = new OkHttpClient();

            String jsonResult = "empty";

            int numDays = 7;

            String units = "metric";
            String mode = "json";
            String appId = "4b1e84d68a329dde43f282c69bff8384";


            final String CITY_PARAM = "id";
            final String UNITS_PARAM = "units";
            final String MODE_PARAM = "mode";
            final String DAYS_PARAM = "cnt";
            final String APPID_PARAM = "appid";

            final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";


            Uri buildUri = Uri.parse(FORECAST_BASE_URL).buildUpon().
                    appendQueryParameter(CITY_PARAM, cityId).
                    appendQueryParameter(UNITS_PARAM, units).
                    appendQueryParameter(MODE_PARAM, mode).
                    appendQueryParameter(DAYS_PARAM, String.valueOf(numDays)).
                    appendQueryParameter(APPID_PARAM, appId).
                    build();

            Log.d(LOG_TAG, buildUri.toString());
            Request request = new Request.Builder().url(buildUri.toString()).build();

            try {
                Response response = okHttpClient.newCall(request).execute();

                if (response.isSuccessful()) {
                    jsonResult = response.body().string();
                    Log.d(LOG_TAG, jsonResult);
                }
            } catch (IOException e) {
                e.printStackTrace();

            }

           return jsonResult;


        }
    }






}
