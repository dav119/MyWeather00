package ru.tolyan.myweather00;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.Vector;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ru.tolyan.myweather00.data.WeatherContract;

/**
 * Created by Tolyan on 08.06.2016.
 */
public class MyTask extends AsyncTask<String, Void, Void> {


    private final Context mContext;
    private int i;

    public MyTask(Context context) {
        this.mContext = context;
    }

    public Vector<ContentValues> parseJson(String jsonString, String settingsLocationId) {

        Vector<ContentValues> contentValuesVector = new Vector<>();
        ContentValues contentValues = new ContentValues();

        // Location information
        final String OWM_CITY = "city";
        final String OWM_CITY_NAME = "name";
        final String OWM_COORD = "coord";

        // Location coordinate
        final String OWM_LATITUDE = "lat";
        final String OWM_LONGITUDE = "lon";

        // Weather information.  Each day's forecast info is an element of the "list" array.
        final String OWM_LIST = "list";

        final String OWM_PRESSURE = "pressure";
        final String OWM_HUMIDITY = "humidity";
        final String OWM_WINDSPEED = "speed";
        final String OWM_WIND_DIRECTION = "deg";

        // All temperatures are children of the "temp" object.
        final String OWM_TEMPERATURE = "temp";
        final String OWM_MAX = "max";
        final String OWM_MIN = "min";

        final String OWM_WEATHER = "weather";
        final String OWM_DESCRIPTION = "main";
        final String OWM_WEATHER_ID = "id";


        final String LIST_NODE = "list";

        if (!jsonString.isEmpty()) {
            try {

                JSONObject rootObject = new JSONObject(jsonString);

                // Get Location data
                JSONObject cityObject = rootObject.getJSONObject(OWM_CITY);
                String city_name = cityObject.getString(OWM_CITY_NAME);
                double city_lat = cityObject.getJSONObject(OWM_COORD).getDouble(OWM_LATITUDE);
                double city_lon = cityObject.getJSONObject(OWM_COORD).getDouble(OWM_LONGITUDE);
                long city_id = addLocation(settingsLocationId, city_name, city_lat, city_lon);




                // Get Weather data for current City

                //get list array to parse it for every day
                JSONArray list = rootObject.getJSONArray(OWM_LIST);

                for (int i = 0; i < list.length(); i++) {

                    double windSpeed;
                    double windDirection;

                    double pressure;
                    int humidity;

                    double high;
                    double low;

                    int weatherId;
                    String description;

                    // get values from Json list array

                    JSONObject one_day = list.getJSONObject(i);

                    // Get temperature
                    JSONObject tempObject = one_day.getJSONObject(OWM_TEMPERATURE);
                    low = tempObject.getInt(OWM_MIN);
                    high = tempObject.getInt(OWM_MAX);

                    // Get weather id and description
                    JSONObject weatherObject = one_day.getJSONArray(OWM_WEATHER).getJSONObject(0);
                    weatherId = weatherObject.getInt(OWM_WEATHER_ID);
                    description = weatherObject.getString(OWM_DESCRIPTION);

                    // Get other weather values

                    pressure = one_day.getDouble(OWM_PRESSURE);
                    humidity = one_day.getInt(OWM_HUMIDITY);
                    windSpeed = one_day.getDouble(OWM_WINDSPEED);
                    windDirection = one_day.getDouble(OWM_WIND_DIRECTION);

                    // assign values to ContentValue object
                    contentValues.put(WeatherContract.WeatherEntry.COLUMN_LOC_KEY, city_id);

                    contentValues.put(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP, low);
                    contentValues.put(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP, high);
                    contentValues.put(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID, weatherId);
                    contentValues.put(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC, description);
                    contentValues.put(WeatherContract.WeatherEntry.COLUMN_PRESSURE, pressure);
                    contentValues.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY, humidity);
                    contentValues.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED, windSpeed);
                    contentValues.put(WeatherContract.WeatherEntry.COLUMN_DEGREES, windDirection);

                    contentValues.put(WeatherContract.WeatherEntry.COLUMN_DATE, getMyDate(i));

                    // add this Content Value to ContentValue Vector
                    contentValuesVector.add(contentValues);
                }

            contentValues = null;

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


        return contentValuesVector;
    }

    long addLocation(String settingsLocationId, String city_name, double city_lat, double city_lon) {
        long resultLocId;

        Cursor c = mContext.getContentResolver().query(
                WeatherContract.LocationEntry.CONTENT_URI,
                new String[] {WeatherContract.LocationEntry._ID},
                WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + "=?",
                new String[] {settingsLocationId},
                null);

        if (c.moveToFirst()) {
            int locationIdIndex = c.getColumnIndex(WeatherContract.LocationEntry._ID);
            resultLocId = c.getLong(locationIdIndex);
        } else {
            ContentValues cv = new ContentValues();
            cv.put(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING, settingsLocationId);
            cv.put(WeatherContract.LocationEntry.COLUMN_CITY_NAME, city_name);
            cv.put(WeatherContract.LocationEntry.COLUMN_COORD_LAT, city_lat);
            cv.put(WeatherContract.LocationEntry.COLUMN_COORD_LONG, city_lon);

            Uri uri = mContext.getContentResolver().insert(WeatherContract.LocationEntry.CONTENT_URI, cv);
            resultLocId = ContentUris.parseId(uri);
        }

        return resultLocId;
    }

    private long getMyDate(int k) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, k);
        return calendar.getTimeInMillis();
    }

    @Override
    protected Void doInBackground(String... params) {

        String resultJson = getWeatherJSONfromNetwrok(params[0]);
        Vector<ContentValues> contentValuesResult = parseJson(resultJson, params[0]);
        if (contentValuesResult.size() > 0) {
            ContentValues[] contentValuesArray = new ContentValues[contentValuesResult.size()];
            contentValuesResult.toArray(contentValuesArray);

            ContentResolver cr = mContext.getContentResolver();
            int i = cr.bulkInsert(WeatherContract.WeatherEntry.CONTENT_URI, contentValuesArray);
        }

        return null;
    }


    private String getWeatherJSONfromNetwrok(String cityId) {

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
