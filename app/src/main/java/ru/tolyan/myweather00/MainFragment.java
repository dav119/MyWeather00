package ru.tolyan.myweather00;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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

    ArrayList<String> listForecast;
    String forecastJASON;

    String cityId;
    SharedPreferences sp;
    // added n master
    ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sp = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        cityId = sp.getString("location", "");

        getForecastData(cityId);
        listForecast = new ArrayList<>();
        adapter = new ArrayAdapter<String>(getActivity(), R.layout.forecast_item, R.id.textView, listForecast);
        setHasOptionsMenu(true);


    }

    @Override
    public void onResume() {
        super.onResume();
        sp = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        cityId = sp.getString("location", "");
        Log.d(LOG_TAG, "current cityId: " + cityId);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_layout, container, false);




        listView = (ListView) root.findViewById(R.id.listView);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, listForecast.get(position).toString());
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
                getForecastData(cityId);
                break;
            case R.id.settings:
                Intent intent = new Intent(this.getActivity(), SettingsActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }



    private void getForecastData(String cityId) {

        OkHttpClient okHttpClient = new OkHttpClient();

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

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.getStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    forecastJASON = response.body().string();
                    listForecast.clear();
                    listForecast.addAll(WeatherDataParser.getDataFromJson(forecastJASON));

                    Log.d(LOG_TAG, "result JASON: " + forecastJASON);


                }
            }
        });

    }


}
