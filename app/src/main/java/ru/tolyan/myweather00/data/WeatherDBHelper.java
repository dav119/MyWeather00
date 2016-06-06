package ru.tolyan.myweather00.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ru.tolyan.myweather00.data.WeatherContract.LocationEntry;
import ru.tolyan.myweather00.data.WeatherContract.WeatherEntry;
/**
 * Created by Tolyan on 05.06.2016.
 */
public class WeatherDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "weather.db";
    public static final int DATABASE_VERSION = 1;

    public WeatherDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    final String SQL_CREATE_LOCATION_TABLE = "CREATE TABLE " + LocationEntry.TABLE_NAME + " (" +
            LocationEntry._ID + " INTEGER PRIMARY KEY," +
            LocationEntry.COLUMN_LOCATION_SETTING + " TEXT UNIQUE NOT NULL, " +
            LocationEntry.COLUMN_CITY_NAME + " TEXT NOT NULL, " +
            LocationEntry.COLUMN_COORD_LAT + " REAL NOT NULL, " +
            LocationEntry.COLUMN_COORD_LONG + " REAL NOT NULL " +
            " );";

    final String SQL_CREATE_WEATHER_TABLE = "CREATE TABLE " + WeatherEntry.TABLE_NAME + " (" +
            // Why AutoIncrement here, and not above?
            // Unique keys will be auto-generated in either case.  But for weather
            // forecasting, it's reasonable to assume the user will want information
            // for a certain date and all dates *following*, so the forecast data
            // should be sorted accordingly.
            WeatherEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

            // the ID of the location entry associated with this weather data
            WeatherEntry.COLUMN_LOC_KEY + " INTEGER NOT NULL, " +
            WeatherEntry.COLUMN_DATE + " INTEGER NOT NULL, " +
            WeatherEntry.COLUMN_SHORT_DESC + " TEXT NOT NULL, " +
            WeatherEntry.COLUMN_WEATHER_ID + " INTEGER NOT NULL," +

            WeatherEntry.COLUMN_MIN_TEMP + " REAL NOT NULL, " +
            WeatherEntry.COLUMN_MAX_TEMP + " REAL NOT NULL, " +

            WeatherEntry.COLUMN_HUMIDITY + " REAL NOT NULL, " +
            WeatherEntry.COLUMN_PRESSURE + " REAL NOT NULL, " +
            WeatherEntry.COLUMN_WIND_SPEED + " REAL NOT NULL, " +
            WeatherEntry.COLUMN_DEGREES + " REAL NOT NULL, " +

            // Set up the location column as a foreign key to location table.
            " FOREIGN KEY (" + WeatherEntry.COLUMN_LOC_KEY + ") REFERENCES " +
            LocationEntry.TABLE_NAME + " (" + LocationEntry._ID + "), " +

            // To assure the application have just one weather entry per day
            // per location, it's created a UNIQUE constraint with REPLACE strategy
            " UNIQUE (" + WeatherEntry.COLUMN_DATE + ", " +
            WeatherEntry.COLUMN_LOC_KEY + ") ON CONFLICT REPLACE);";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_LOCATION_TABLE);
        db.execSQL(SQL_CREATE_WEATHER_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + LocationEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + WeatherEntry.TABLE_NAME);
        onCreate(db);
    }
}