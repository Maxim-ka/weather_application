package reschikov.geekbrains.androidadvancedlevel.weatherapplication.database;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import java.util.ArrayList;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.R;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.Rules;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.apiux.Current;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.apiux.Forecast;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.apiux.Location;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.apiux.ServerResponse;
import static reschikov.geekbrains.androidadvancedlevel.weatherapplication.Rules.ACTION_LOAD_FROM_DB;
import static reschikov.geekbrains.androidadvancedlevel.weatherapplication.Rules.ACTION_RECORD_DB;
import static reschikov.geekbrains.androidadvancedlevel.weatherapplication.Rules.ACTION_WEATHER;
import static reschikov.geekbrains.androidadvancedlevel.weatherapplication.Rules.GET_WEATHER_DB;
import static reschikov.geekbrains.androidadvancedlevel.weatherapplication.Rules.LOADING;

public class DatabaseService extends IntentService{

    public DatabaseService() {
        super("database");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        if (action != null){
            try (DatabaseQueries dbq = new DatabaseQueries(getBaseContext())){
                dbq.open();
                switch (action){
                    case LOADING:
                        ArrayList<Location> cities = dbq.getCities();
                        intent.setAction(ACTION_LOAD_FROM_DB);
                        intent.putParcelableArrayListExtra(Rules.KEY_LIST_OF_CITIES, cities);
                        break;
                    case Rules.RECORD:
                        Location place = intent.getParcelableExtra(Rules.KEY_PLACE);
                        Current current = intent.getParcelableExtra(Rules.KEY_CURRENT);
                        Forecast forecast = intent.getParcelableExtra(Rules.KEY_FORECAST);
                        boolean isRecorded = dbq.add(place, current, forecast.getForecastday());
                        intent.putExtra(Rules.KEY_RECORD, isRecorded);
                        if (getResources().getBoolean(R.bool.is_X_large_layout) && isRecorded){
                            Intent recorded = new Intent(Rules.ACTION_ANSWER_DB);
                            recorded.putParcelableArrayListExtra(Rules.KEY_LIST_OF_CITIES, dbq.getCities());
                            LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(recorded);
                        }
                        intent.setAction(ACTION_RECORD_DB);
                        break;
                    case Rules.GET_ALL_CITIES:
                        intent.setAction(Rules.ACTION_ANSWER_DB);
                        intent.putParcelableArrayListExtra(Rules.KEY_LIST_OF_CITIES, dbq.getCities());
                        break;
                    case Rules.CLEAR:
                        intent.setAction(Rules.ACTION_ANSWER_DB);
                        dbq.clearDB();
                        intent.putParcelableArrayListExtra(Rules.KEY_LIST_OF_CITIES, dbq.getCities());
                        break;
                    case Rules.REMOVE:
                        intent.setAction(Rules.ACTION_ANSWER_DB);
                        intent.putExtra(Rules.KEY_IS_DELETED, dbq.delete(intent.getParcelableArrayListExtra(Rules.KEY_SELECTED)));
                        intent.putParcelableArrayListExtra(Rules.KEY_LIST_OF_CITIES, dbq.getCities());
                        break;
                    case GET_WEATHER_DB:
                        ServerResponse response = dbq.getLastShownWeather();
                        if (response != null) {
                            intent.setAction(ACTION_WEATHER);
                            intent.putExtra(Rules.KEY_WEATHER, response);
                        } else intent = null;
                        break;
                }
            }
            if (intent != null) LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent);
        }
    }
}
