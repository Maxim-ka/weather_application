package reschikov.geekbrains.androidadvancedlevel.weatherapplication.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.R;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.apiux.Astro;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.apiux.Condition;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.apiux.Current;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.apiux.Day;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.apiux.Forecast;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.apiux.Forecastday;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.apiux.Location;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.apiux.ServerResponse;
import static reschikov.geekbrains.androidadvancedlevel.weatherapplication.database.DatabaseHelper.COL_AVG_HUMIDITY;
import static reschikov.geekbrains.androidadvancedlevel.weatherapplication.database.DatabaseHelper.COL_CITY;

import static reschikov.geekbrains.androidadvancedlevel.weatherapplication.database.DatabaseHelper.COL_CLOUD;
import static reschikov.geekbrains.androidadvancedlevel.weatherapplication.database.DatabaseHelper.COL_COUNTRY;
import static reschikov.geekbrains.androidadvancedlevel.weatherapplication.database.DatabaseHelper.COL_DATE;
import static reschikov.geekbrains.androidadvancedlevel.weatherapplication.database.DatabaseHelper.COL_DATE_TIME;
import static reschikov.geekbrains.androidadvancedlevel.weatherapplication.database.DatabaseHelper.COL_FEATURED_TEMP;
import static reschikov.geekbrains.androidadvancedlevel.weatherapplication.database.DatabaseHelper.COL_FORECAST_STATE;
import static reschikov.geekbrains.androidadvancedlevel.weatherapplication.database.DatabaseHelper.COL_HUMIDITY;
import static reschikov.geekbrains.androidadvancedlevel.weatherapplication.database.DatabaseHelper.COL_ID_CITY;
import static reschikov.geekbrains.androidadvancedlevel.weatherapplication.database.DatabaseHelper.COL_IMAGE;
import static reschikov.geekbrains.androidadvancedlevel.weatherapplication.database.DatabaseHelper.COL_SHOWING;
import static reschikov.geekbrains.androidadvancedlevel.weatherapplication.database.DatabaseHelper.COL_LAT;
import static reschikov.geekbrains.androidadvancedlevel.weatherapplication.database.DatabaseHelper.COL_LON;
import static reschikov.geekbrains.androidadvancedlevel.weatherapplication.database.DatabaseHelper.COL_MAX_WIND;
import static reschikov.geekbrains.androidadvancedlevel.weatherapplication.database.DatabaseHelper.COL_MOON_RISE;
import static reschikov.geekbrains.androidadvancedlevel.weatherapplication.database.DatabaseHelper.COL_MOON_SET;
import static reschikov.geekbrains.androidadvancedlevel.weatherapplication.database.DatabaseHelper.COL_PRESSURE;
import static reschikov.geekbrains.androidadvancedlevel.weatherapplication.database.DatabaseHelper.COL_RAINFALL;
import static reschikov.geekbrains.androidadvancedlevel.weatherapplication.database.DatabaseHelper.COL_REGION;
import static reschikov.geekbrains.androidadvancedlevel.weatherapplication.database.DatabaseHelper.COL_STATE;
import static reschikov.geekbrains.androidadvancedlevel.weatherapplication.database.DatabaseHelper.COL_SUN_RISE;
import static reschikov.geekbrains.androidadvancedlevel.weatherapplication.database.DatabaseHelper.COL_SUN_SET;
import static reschikov.geekbrains.androidadvancedlevel.weatherapplication.database.DatabaseHelper.COL_TEMP;
import static reschikov.geekbrains.androidadvancedlevel.weatherapplication.database.DatabaseHelper.COL_TEMP_MAX;
import static reschikov.geekbrains.androidadvancedlevel.weatherapplication.database.DatabaseHelper.COL_TEMP_MIN;
import static reschikov.geekbrains.androidadvancedlevel.weatherapplication.database.DatabaseHelper.COL_TOTAL_PRECIP;
import static reschikov.geekbrains.androidadvancedlevel.weatherapplication.database.DatabaseHelper.COL_URL_IMAGE;
import static reschikov.geekbrains.androidadvancedlevel.weatherapplication.database.DatabaseHelper.COL_WIND;
import static reschikov.geekbrains.androidadvancedlevel.weatherapplication.database.DatabaseHelper.COL_WIND_SPEED;
import static reschikov.geekbrains.androidadvancedlevel.weatherapplication.database.DatabaseHelper.DATABASE;
import static reschikov.geekbrains.androidadvancedlevel.weatherapplication.database.DatabaseHelper.ID_CITIES;
import static reschikov.geekbrains.androidadvancedlevel.weatherapplication.database.DatabaseHelper.TABLE_CURRENT;
import static reschikov.geekbrains.androidadvancedlevel.weatherapplication.database.DatabaseHelper.TABLE_FORECASTDAY;
import static reschikov.geekbrains.androidadvancedlevel.weatherapplication.database.DatabaseHelper.TABLE_LOCATION;


class DatabaseQueries implements Closeable {

    private static final int TRUE = 1;
    private static final int FALSE = 0;
    private final String[] allColumns = {ID_CITIES, COL_CITY, COL_REGION, COL_COUNTRY, COL_LAT, COL_LON, COL_SHOWING};

    private final String[]colSaveCurrent = {COL_CITY, COL_TEMP, COL_FEATURED_TEMP, COL_WIND,
        COL_WIND_SPEED, COL_HUMIDITY, COL_PRESSURE, COL_CLOUD, COL_RAINFALL, COL_DATE_TIME, COL_STATE,
        COL_URL_IMAGE};

    private final String[] colSaveForecast = {COL_DATE, COL_TEMP_MIN, COL_TEMP_MAX, COL_MAX_WIND, COL_AVG_HUMIDITY,
        COL_TOTAL_PRECIP, COL_FORECAST_STATE, COL_IMAGE, COL_SUN_RISE, COL_SUN_SET, COL_MOON_RISE, COL_MOON_SET};

    private final DatabaseHelper dbHelper;
    private SQLiteDatabase database;
    private final Context context;

    DatabaseQueries(Context context){
        this.context = context;
        dbHelper = new DatabaseHelper(context);
    }

    void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
        database.setForeignKeyConstraintsEnabled(true);
    }

    ServerResponse getLastShownWeather(){
        String whereClause = context.getString(R.string.request_one_parameter, COL_SHOWING);
        String[] args = new String[]{String.valueOf(TRUE)};
        ServerResponse response = null;
        database.beginTransaction();
        Location location = getCity(whereClause, args);
        if (location != null && location.isShowingCity()){
            response = new ServerResponse();
            response.setLocation(location);
            response.setCurrent(getSavedCurrent(whereClause,args));
            response.setForecast(getSavedForecast(whereClause, args));
        }
        database.setTransactionSuccessful();
        database.endTransaction();
        return response;
    }

    private Location getCity(String whereClause, String[] args){
        Location location = null;
        try (Cursor cursor = readCities(whereClause, args)) {
            if (cursor.getCount() == 0) return null;
            if (cursor.moveToFirst()){
                location = new Location();
                location.setName(cursor.getString(cursor.getColumnIndex(COL_CITY)));
                location.setRegion(cursor.getString(cursor.getColumnIndex(COL_REGION)));
                location.setCountry(cursor.getString(cursor.getColumnIndex(COL_COUNTRY)));
                location.setLat(cursor.getDouble(cursor.getColumnIndex(COL_LAT)));
                location.setLon(cursor.getDouble(cursor.getColumnIndex(COL_LON)));
                location.setShowingCity(cursor.getInt(cursor.getColumnIndex(COL_SHOWING)) == TRUE);
            }
        }
        return location;
    }

    private Cursor toRequestSavedForecast(String whereClause, String[] args){
        String table = context.getString(R.string.request_inner_join, TABLE_FORECASTDAY, TABLE_LOCATION, COL_ID_CITY, ID_CITIES);
        return database.query(table, colSaveForecast, whereClause, args , null, null, null);
    }

    private Forecast getSavedForecast(String whereClause, String[] args){
        List<Forecastday> list;
        try (Cursor cursor = toRequestSavedForecast(whereClause, args)){
            if (cursor.getCount() == 0) return null;
            list = new ArrayList<>();
            while (cursor.moveToNext()){
                int date = cursor.getInt(cursor.getColumnIndex(COL_DATE));
                Day day = new Day();
                day.setMintempC(cursor.getDouble(cursor.getColumnIndex(COL_TEMP_MIN)));
                day.setMaxtempC(cursor.getDouble(cursor.getColumnIndex(COL_TEMP_MAX)));
                day.setMaxwindKph(cursor.getDouble(cursor.getColumnIndex(COL_MAX_WIND)));
                day.setAvghumidity(cursor.getDouble(cursor.getColumnIndex(COL_AVG_HUMIDITY)));
                day.setTotalprecipMm(cursor.getDouble(cursor.getColumnIndex(COL_TOTAL_PRECIP)));
                day.setCondition(new Condition(cursor.getString(cursor.getColumnIndex(COL_FORECAST_STATE)),
                        cursor.getString(cursor.getColumnIndex(COL_IMAGE))));
                Astro astro = new Astro();
                astro.setSunrise(cursor.getString(cursor.getColumnIndex(COL_SUN_RISE)));
                astro.setSunset(cursor.getString(cursor.getColumnIndex(COL_SUN_SET)));
                astro.setMoonrise(cursor.getString(cursor.getColumnIndex(COL_MOON_RISE)));
                astro.setMoonset(cursor.getString(cursor.getColumnIndex(COL_MOON_SET)));
                list.add(new Forecastday(date, day, astro));
            }
        }
        return new Forecast(list);
    }

    private Cursor toRequestSavedCurrent(String whereClause, String[] args){
        String table = context.getString(R.string.request_inner_join, TABLE_CURRENT, TABLE_LOCATION, COL_ID_CITY, ID_CITIES);
        return database.query(table, colSaveCurrent, whereClause, args , null, null, null);
    }

    private Current getSavedCurrent(String whereClause, String[] args){
        Current current;
        try (Cursor cursor = toRequestSavedCurrent(whereClause, args)){
            if (cursor.getCount() == 0) return null;
            if (cursor.moveToFirst()){
                current = new Current();
                current.setTempC(cursor.getDouble(cursor.getColumnIndex(COL_TEMP)));
                current.setFeelslikeC(cursor.getDouble(cursor.getColumnIndex(COL_FEATURED_TEMP)));
                current.setWindDir(cursor.getString(cursor.getColumnIndex(COL_WIND)));
                current.setWindKph(cursor.getDouble(cursor.getColumnIndex(COL_WIND_SPEED)));
                current.setHumidity(cursor.getInt(cursor.getColumnIndex(COL_HUMIDITY)));
                current.setPressureMb(cursor.getDouble(cursor.getColumnIndex(COL_PRESSURE)));
                current.setCloud(cursor.getInt(cursor.getColumnIndex(COL_CLOUD)));
                current.setPrecipMm(cursor.getDouble(cursor.getColumnIndex(COL_RAINFALL)));
                current.setLastUpdatedEpoch(cursor.getInt(cursor.getColumnIndex( COL_DATE_TIME)));
                Condition condition = new Condition(cursor.getString(cursor.getColumnIndex(COL_STATE)),
                        cursor.getString(cursor.getColumnIndex(COL_URL_IMAGE)));
                current.setCondition(condition);
            } else return null;
        }
        return current;
    }

    private Cursor readCities(String whereClause, String[] args){
        return database.query(TABLE_LOCATION, allColumns, whereClause, args, null, null, null);
    }

    public ArrayList<Location> getCities(){
        ArrayList<Location> locations;
        try (Cursor cursor = readCities(null, null)) {
            if (cursor.getCount() == 0) return null;
            locations = new ArrayList<>();
            while (cursor.moveToNext()) {
                Location location = new Location();
                location.setName(cursor.getString(cursor.getColumnIndex(COL_CITY)));
                location.setRegion(cursor.getString(cursor.getColumnIndex(COL_REGION)));
                location.setCountry(cursor.getString(cursor.getColumnIndex(COL_COUNTRY)));
                location.setLat(cursor.getDouble(cursor.getColumnIndex(COL_LAT)));
                location.setLon(cursor.getDouble(cursor.getColumnIndex(COL_LON)));
                location.setShowingCity(cursor.getInt(cursor.getColumnIndex(COL_SHOWING)) == TRUE);
                if (location.isShowingCity()) locations.add(0, location);
                else locations.add(location);
            }
        }
        return locations;
    }

    private Cursor checkCityAvailability(Location location){
        String selection = context.getString(R.string.request_two_parameters, COL_LAT, COL_LON);
        String[] args = new String[]{Double.toString(location.getLat()), Double.toString(location.getLon())};
        return database.query(TABLE_LOCATION, new String[]{ID_CITIES}, selection, args, null, null, null);
    }

    private boolean isRecordedForecastData(List<Forecastday> list, int id){
        for (int i = 0; i < list.size(); i++) {
            ContentValues valuesDay = collectData(list.get(i), id);
            if (database.insert(TABLE_FORECASTDAY,null, valuesDay) == -1) return false;
        }
        return true;
    }

    private boolean isUpdatedForecastData(List<Forecastday> list, String whereClause, String[] args){
        for (int i = 0; i < list.size(); i++) {
            ContentValues valuesDay = collectData(list.get(i), -1);
            if (database.update(TABLE_FORECASTDAY, valuesDay, whereClause, args) <= 0) return false;
        }
        return true;
    }

    private Location getLastShownCity(){
        String whereClause = context.getString(R.string.request_one_parameter, COL_SHOWING);
        String[] args = new String[]{String.valueOf(TRUE)};
        return getCity(whereClause, args);
    }

    public boolean add(Location location, Current current, List<Forecastday> list){
        int id;
        database.beginTransaction();
        Location saveLastShownCity = getLastShownCity();
        if (saveLastShownCity != null && !saveLastShownCity.equals(location)){
            ContentValues cv = new ContentValues();
            cv.put(COL_SHOWING, FALSE);
            String whereClause = context.getString(R.string.request_two_parameters, COL_LAT, COL_LON);
            String[] args = new String[]{Double.toString(saveLastShownCity.getLat()), Double.toString(saveLastShownCity.getLon())};
            if (database.update(TABLE_LOCATION, cv, whereClause, args) <= 0) return  false;
        }
        try (Cursor cursor = checkCityAvailability(location)){
            ContentValues curState = collectData(current);
            if (cursor.moveToFirst()){
                id = cursor.getInt(cursor.getColumnIndex(ID_CITIES));
                String whereClause = context.getString(R.string.request_one_parameter, COL_ID_CITY);
                String[] args = new String[]{String.valueOf(id)};
                ContentValues cv = new ContentValues();
                cv.put(COL_SHOWING, TRUE);
                if (database.update(TABLE_LOCATION, cv, context.getString(R.string.request_one_parameter, ID_CITIES), args) <= 0) return  false;
                if (database.update(TABLE_CURRENT, curState, whereClause, args) <= 0) return  false;
                if (!isUpdatedForecastData(list, whereClause, args)) return false;
            }else{
                ContentValues place = collectData(location);
                id = (int) database.insert(TABLE_LOCATION, null, place);
                curState.put(COL_ID_CITY, id);
                if (database.insert(TABLE_CURRENT,null, curState) <= 0) return  false;
                if (!isRecordedForecastData(list, id)) return false;
            }
            database.setTransactionSuccessful();
        }finally {
            database.endTransaction();
        }
        return true;
    }

    private ContentValues collectData(Forecastday forecastday, int id){
        ContentValues forecast = new ContentValues();
        forecast.put(COL_DATE, forecastday.getDateEpoch());
        forecast.put(COL_TEMP_MIN, forecastday.getDay().getMintempC());
        forecast.put(COL_TEMP_MAX, forecastday.getDay().getMaxtempC());
        forecast.put(COL_MAX_WIND, forecastday.getDay().getMaxwindKph());
        forecast.put(COL_AVG_HUMIDITY, forecastday.getDay().getAvghumidity());
        forecast.put(COL_TOTAL_PRECIP, forecastday.getDay().getTotalprecipMm());
        forecast.put(COL_FORECAST_STATE, forecastday.getDay().getCondition().getText());
        forecast.put(COL_IMAGE, forecastday.getDay().getCondition().getIcon());
        forecast.put(COL_SUN_RISE, forecastday.getAstro().getSunrise());
        forecast.put(COL_SUN_SET, forecastday.getAstro().getSunset());
        forecast.put(COL_MOON_RISE, forecastday.getAstro().getMoonrise());
        forecast.put(COL_MOON_SET, forecastday.getAstro().getMoonset());
        if (id > -1)forecast.put(COL_ID_CITY, id);
        return forecast;
    }

    private ContentValues collectData(Location location){
        ContentValues place = new ContentValues();
        place.put(COL_CITY, location.getName());
        place.put(COL_REGION, location.getRegion());
        place.put(COL_COUNTRY, location.getCountry());
        place.put(COL_LAT, location.getLat());
        place.put(COL_LON, location.getLon());
        place.put(COL_SHOWING, TRUE);
        return place;
    }

    private ContentValues collectData(Current current){
        ContentValues curWeather = new ContentValues();
        curWeather.put(COL_TEMP, current.getTempC());
        curWeather.put(COL_FEATURED_TEMP, current.getFeelslikeC());
        curWeather.put(COL_WIND, current.getWindDir());
        curWeather.put(COL_WIND_SPEED, current.getWindKph());
        curWeather.put(COL_HUMIDITY, current.getHumidity());
        curWeather.put(COL_PRESSURE, current.getPressureMb());
        curWeather.put(COL_CLOUD, current.getCloud());
        curWeather.put(COL_RAINFALL, current.getPrecipMm());
        curWeather.put(COL_DATE_TIME, current.getLastUpdatedEpoch());
        curWeather.put(COL_STATE, current.getCondition().getText());
        curWeather.put(COL_URL_IMAGE, current.getCondition().getIcon());
        return curWeather;
    }

    boolean delete(ArrayList<Location> listLocation){
        database.beginTransaction();
        try {
            String whereClause = context.getString(R.string.request_two_parameters, COL_LAT, COL_LON);
            for (int i = 0; i <listLocation.size() ; i++) {
                String[] args = new String[]{Double.toString(listLocation.get(i).getLat()), Double.toString(listLocation.get(i).getLon())};
                if (database.delete(TABLE_LOCATION, whereClause, args) == 0) return false;
            }
            database.setTransactionSuccessful();
        }finally {
            database.endTransaction();
        }
        database.execSQL(context.getString(R.string.vacuum));
        return true;
    }

    void clearDB(){
        database.delete(TABLE_LOCATION, null, null);
        database.execSQL(context.getString(R.string.vacuum));
        context.deleteDatabase(DATABASE);
    }

    @Override
    public void close(){
        database.close();
        dbHelper.close();
    }
}
