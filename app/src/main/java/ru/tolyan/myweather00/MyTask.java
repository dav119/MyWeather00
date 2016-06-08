package ru.tolyan.myweather00;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Tolyan on 08.06.2016.
 */
public class MyTask extends AsyncTask<String, Void, String> {


    private MainFragment mainFragment;

    public MyTask(MainFragment mainFragment) {
        this.mainFragment = mainFragment;
    }

    @Override
    protected String doInBackground(String... params) {
        return getForecastData(params[0]);
    }


    @Override
    protected void onPostExecute(String s) {
        mainFragment.adapter.clear();
        mainFragment.adapter.addAll(WeatherDataParser.getDataFromJson(s));
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

        Log.d(MainFragment.LOG_TAG, buildUri.toString());
        Request request = new Request.Builder().url(buildUri.toString()).build();

        try {
            Response response = okHttpClient.newCall(request).execute();

            if (response.isSuccessful()) {
                jsonResult = response.body().string();
                Log.d(MainFragment.LOG_TAG, jsonResult);
            }
        } catch (IOException e) {
            e.printStackTrace();

        }

        return jsonResult;


    }
}
