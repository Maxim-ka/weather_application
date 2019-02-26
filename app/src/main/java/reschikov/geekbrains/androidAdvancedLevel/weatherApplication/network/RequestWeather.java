package reschikov.geekbrains.androidadvancedlevel.weatherapplication.network;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;
import com.google.gson.Gson;
import java.io.IOException;

import reschikov.geekbrains.androidadvancedlevel.weatherapplication.R;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.Rules;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.apiux.ServerResponse;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.database.DatabaseService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static reschikov.geekbrains.androidadvancedlevel.weatherapplication.Rules.ACTION_WEATHER;
import static reschikov.geekbrains.androidadvancedlevel.weatherapplication.Rules.KEY_ERR_SERVER;
import static reschikov.geekbrains.androidadvancedlevel.weatherapplication.Rules.RU;

class RequestWeather {

    interface ApiuxWeather {

        @GET("forecast.json")
        Call<ServerResponse> loadWeather(@Query(Rules.KEY) String key, @Query(Rules.Q) String city,
             @Query(DAY_S) int days, @Query(LANG) String lang);
    }

    private static final int DAYS = 7;
    private static final int RECEPTION = 50;
    private static final int FINISH = 100;
    private static final int CODE_ERR_Q = 1006;
    private static final String DAY_S = "DAYS";
    private static final String LANG = "lang";
    private static final String HTTPS_APIUX_POGODA = "https://api.apixu.com/v1/";
    private final ApiuxWeather request;
    private final String key;
    private final Context context;
    private final String lang;

    RequestWeather(Context context) {
        this.context = context;
        SharedPreferences preferences = getDefaultSharedPreferences(context);
        Retrofit client = new Retrofit.Builder()
                .baseUrl(HTTPS_APIUX_POGODA)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        request = client.create(ApiuxWeather.class);
        key = preferences.getString(Rules.KEY_APIUX, context.getString(R.string.key_apiux));
        lang = (preferences.getBoolean(Rules.KEY_LANG, false)) ? RU : context.getString(R.string.str_en);
    }

    void toRequest(Message message) throws RemoteException {
        final String city = (String) message.obj;
        Messenger counting = message.replyTo;
        if (message.arg1 == Rules.ZERO) counting.send(Message.obtain(null, Rules.PROGRESS, message.arg2));
        request.loadWeather(key, city, DAYS, lang).enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(@NonNull Call<ServerResponse> call, @NonNull Response<ServerResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null){
                        Intent intent = new Intent(ACTION_WEATHER);
                        intent.putExtra(Rules.KEY_WEATHER, response.body());
                        try {
                            counting.send(Message.obtain(null, Rules.PROGRESS, FINISH));
                        } catch (RemoteException e) {
                            Log.e("progress 100", e.getMessage());
                        }finally {
                            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                            writeToDatabase(response.body());
                        }
                    }
                } else {
                    getErrMessage(response, null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ServerResponse> call, @NonNull Throwable t) {
                getErrMessage(null, t.getMessage());
                try {
                    counting.send(Message.obtain(null, Rules.FAILURE));
                } catch (RemoteException e) {
                    Log.e("onFailure", e.getMessage());
                }
                Toast.makeText(context, context.getString(R.string.failure) + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        counting.send(Message.obtain(null, Rules.PROGRESS, RECEPTION));
    }

    private void writeToDatabase(ServerResponse sr){
        Intent intentBD = new Intent(context, DatabaseService.class);
        intentBD.setAction(Rules.RECORD);
        intentBD.putExtra(Rules.KEY_PLACE, sr.getLocation());
        intentBD.putExtra(Rules.KEY_CURRENT, sr.getCurrent());
        intentBD.putExtra(Rules.KEY_FORECAST, sr.getForecast());
        context.startService(intentBD);
    }

    private void getErrMessage(@Nullable Response<ServerResponse> response, String error){
        Intent intent = new Intent(Rules.ACTION_ERROR_SERVER);
        if (response != null && response.errorBody() != null){
            try {
                String errStr = response.errorBody().string();
                ServerResponse err = new Gson().fromJson(errStr, ServerResponse.class);
                StringBuilder sb = new StringBuilder();
                if (err.getError().getCode() == CODE_ERR_Q){
                    sb.append(response.message())
                            .append(" ")
                            .append(response.raw().request().url().queryParameter(Rules.Q))
                            .append("\n");
                }
                sb.append(err.getError().getCode())
                        .append(": ")
                        .append(err.getError().getMessage());
                intent.putExtra(KEY_ERR_SERVER, sb.toString());
            } catch (IOException e) {
                Log.e("response.errorBody", e.getMessage());
            } finally {
                response.errorBody().close();
            }
        } else intent.putExtra(KEY_ERR_SERVER, error);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
