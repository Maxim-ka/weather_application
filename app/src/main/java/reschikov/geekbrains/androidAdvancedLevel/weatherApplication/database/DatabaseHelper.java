package reschikov.geekbrains.androidadvancedlevel.weatherapplication.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    static final String DATABASE = "weather.db";
    static final String TABLE_LOCATION = "cities";
    static final String ID_CITIES = BaseColumns._ID;
    static final String COL_CITY = "city";
    static final String COL_REGION = "region";
    static final String COL_COUNTRY = "country";
    static final String COL_LAT = "lat";
    static final String COL_LON = "lon";
    static final String COL_SHOWING = "isShowingCity";
    static final String TABLE_CURRENT = "current";
    private static final String ID_CURRENT = BaseColumns._ID;
    static final String COL_TEMP = "tempC";
    static final String COL_FEATURED_TEMP ="feelslikeC";
    static final String COL_WIND = "windDir";
    static final String COL_WIND_SPEED = "windMpC";
    static final String COL_HUMIDITY = "humidity";
    static final String COL_PRESSURE = "pressure";
    static final String COL_CLOUD = "cloud";
    static final String COL_RAINFALL = "precipMm";
    static final String COL_DATE_TIME = "date_time";
    static final String COL_STATE = "state";
    static final String COL_URL_IMAGE = "url_image";
    static final String TABLE_FORECASTDAY = "forecastday";
    private static final String ID_FORECASTDAY = BaseColumns._ID;
    static final String COL_DATE = "day";
    static final String COL_TEMP_MIN = "tempMinC";
    static final String COL_TEMP_MAX ="tempMaxC";
    static final String COL_MAX_WIND = "maxWind";
    static final String COL_AVG_HUMIDITY = "avgHumidity";
    static final String COL_TOTAL_PRECIP = "totalPrecipMm";
    static final String COL_FORECAST_STATE = "forecastState";
    static final String COL_IMAGE = "image";
    static final String COL_SUN_RISE = "sunrise";
    static final String COL_SUN_SET = "sunset";
    static final String COL_MOON_RISE = "moonrise";
    static final String COL_MOON_SET = "moonset";
    static final String COL_ID_CITY = "id_city";

    private static final String INDEX_LOCATION = "inCity";
    private static final String INDEX_PLACE = "inCur";
    private static final String INDEX_FORECAST = "inForecastday";

    DatabaseHelper(Context context) {
        super(context, DATABASE, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(String.format("CREATE TABLE %1$s (%2$s INTEGER PRIMARY KEY AUTOINCREMENT, %3$s TEXT, %4$s TEXT, %5$s TEXT, %6$s REAL, %7$s REAL, %8$s NUMERIC);",
                TABLE_LOCATION, ID_CITIES, COL_CITY, COL_REGION, COL_COUNTRY, COL_LAT, COL_LON, COL_SHOWING));
        db.execSQL(String.format("CREATE UNIQUE INDEX %1$s ON %2$s (%3$s, %4$s, %5$s);", INDEX_LOCATION, TABLE_LOCATION, COL_CITY, COL_LAT, COL_LON));
        db.execSQL(String.format("CREATE TABLE %1$s (%2$s INTEGER PRIMARY KEY AUTOINCREMENT, %3$s REAL, %4$s REAL, " +
            "%5$s TEXT, %6$s REAL, %7$s INTEGER, %8$s REAL, %9$s INTEGER, %10$s REAL, %11$s INTEGER, %12$s TEXT, %13$s TEXT," +
            " %14$s INTEGER, FOREIGN KEY (%14$s) REFERENCES %15$s (%16$s) ON DELETE CASCADE);",
            TABLE_CURRENT, ID_CURRENT, COL_TEMP, COL_FEATURED_TEMP, COL_WIND, COL_WIND_SPEED, COL_HUMIDITY,
            COL_PRESSURE, COL_CLOUD, COL_RAINFALL, COL_DATE_TIME, COL_STATE, COL_URL_IMAGE, COL_ID_CITY, TABLE_LOCATION, ID_CITIES));
        db.execSQL(String.format("CREATE INDEX %1$s ON %2$s (%3$s);", INDEX_PLACE, TABLE_CURRENT, COL_ID_CITY));
        db.execSQL(String.format("CREATE TABLE %1$s (%2$s INTEGER PRIMARY KEY AUTOINCREMENT, %3$s INTEGER, %4$s REAL, " +
            "%5$s REAL, %6$s REAL, %7$s REAL, %8$s REAL, %9$s TEXT, %10$s TEXT, %11$s TEXT, %12$s TEXT, " +
            "%13$s TEXT, %14$s TEXT, %15$s INTEGER, FOREIGN KEY (%15$s) REFERENCES %16$s (%17$s) ON DELETE CASCADE);",
            TABLE_FORECASTDAY, ID_FORECASTDAY, COL_DATE, COL_TEMP_MIN, COL_TEMP_MAX, COL_MAX_WIND, COL_AVG_HUMIDITY, COL_TOTAL_PRECIP,
            COL_FORECAST_STATE, COL_IMAGE, COL_SUN_RISE, COL_SUN_SET, COL_MOON_RISE, COL_MOON_SET, COL_ID_CITY, TABLE_LOCATION, ID_CITIES));
        db.execSQL(String.format("CREATE INDEX %1$s ON %2$s (%3$s);", INDEX_FORECAST, TABLE_FORECASTDAY, COL_ID_CITY));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
