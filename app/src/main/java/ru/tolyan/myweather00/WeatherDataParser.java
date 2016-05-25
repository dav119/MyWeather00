package ru.tolyan.myweather00;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.acl.LastOwnerException;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Tolyan on 22.05.2016.
 */
public class WeatherDataParser {

    public static final String LOG_TAG = "my_log";


    public static ArrayList<String> getDataFromJson(String jsonString) {
        ArrayList<String> result = new ArrayList<>();
        String row;

        final String LIST_NODE = "list";

        if (!jsonString.isEmpty()) {
            try {
                JSONObject rootObject = new JSONObject(jsonString);

                JSONArray list = rootObject.getJSONArray(LIST_NODE);

                for (int i = 0; i < list.length(); i++) {
                    row = getMyDate(i);
                    row += " - ";

                    JSONObject one_day = list.getJSONObject(i);

                    row += one_day.getJSONArray("weather").getJSONObject(0).getString("main");
                    row += " - ";

                    JSONObject temp = one_day.getJSONObject("temp");

                    row += temp.getInt("max");
                    row += "/";
                    row += temp.getInt("min");

                    result.add(row);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        for (String r : result) {
            Log.d(LOG_TAG, r);
        }
        return result;
    }

    private static String getMyDate(int i) {
        SimpleDateFormat formater = new SimpleDateFormat("EEE, MMM d");
        formater.setTimeZone(TimeZone.getTimeZone("UTC"));
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, i);
        Date date = new Date(calendar.getTimeInMillis());
        return formater.format(date);
    }
}
