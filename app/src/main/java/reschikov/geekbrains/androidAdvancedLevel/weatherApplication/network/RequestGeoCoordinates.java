package reschikov.geekbrains.androidadvancedlevel.weatherapplication.network;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import java.util.ArrayList;

import reschikov.geekbrains.androidadvancedlevel.weatherapplication.R;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.Rules;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.opencage.GeoCoordinates;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.opencage.Result;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import static android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences;
import static reschikov.geekbrains.androidadvancedlevel.weatherapplication.Rules.KEY_ERR_SERVER;

public class RequestGeoCoordinates {

    interface OpenCage {

        @GET("json")
        Call<GeoCoordinates> coordinateRequest(@Query(Rules.Q) String place, @Query(Rules.KEY) String key,
           @Query("limit") int limit, @Query("min_confidence") int minConfidence,
           @Query("no_annotations") int noAnnotations, @Query("no_dedupe") int noDedupe, @Query("no_record") int noRecord);
    }

    private static final String HTTPS_OPENCAGE_COORDINATE = "https://api.opencagedata.com/geocode/v1/";
    public static final int LIMIT = 10;
    private static final int MIN_CONFIDENCE = 8;
    private static final int NO_ANNOTATIONS = 1;
    private static final int NO_DEDUPE = 1;
    private static final int NO_RECORD = 1;
    private static final int OK = 200;
    private final Context context;
    private final OpenCage request;
    private final String key;

    RequestGeoCoordinates(Context context) {
        this.context = context;
        SharedPreferences sp = getDefaultSharedPreferences(context);
        Retrofit client = new Retrofit.Builder()
                .baseUrl(HTTPS_OPENCAGE_COORDINATE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        request = client.create(OpenCage.class);
        key = sp.getString(Rules.KEY_OPEN_CAGE, context.getString(R.string.key_opencage));
    }

    void toRequest(Message message){
        final String place = (String) message.obj;
        final Messenger counting = message.replyTo;
        request.coordinateRequest(place, key, LIMIT, MIN_CONFIDENCE, NO_ANNOTATIONS, NO_DEDUPE, NO_RECORD)
            .enqueue(new Callback<GeoCoordinates>() {
                @Override
                public void onResponse(@NonNull Call<GeoCoordinates> call, @NonNull Response<GeoCoordinates> response) {
                    if (response.isSuccessful()) {
                        GeoCoordinates gc = response.body();
                        if (gc != null){
                            if (gc.getStatus().getCode() == OK){
                                ArrayList<Result> results = (ArrayList<Result>) gc.getResults();
                                if (results != null && !results.isEmpty()){
                                    try {
                                        Message msg = Message.obtain(null, Rules.CLARIFICATION);
                                        Bundle bundle = new Bundle();
                                        bundle.putParcelableArrayList(Rules.KEY_RESULTS, results);
                                        msg.setData(bundle);
                                        counting.send(msg);
                                        counting.send(Message.obtain(null, Rules.PROGRESS, Rules.DEFNIITION));
                                    } catch (RemoteException e) {
                                        Log.e("onResponse", e.getMessage());
                                    }
                                    SharedPreferences sp = getDefaultSharedPreferences(context);
                                    SharedPreferences.Editor editor= sp.edit();
                                    editor.putInt(Rules.KEY_NUMBER_OF_REQUESTS, gc.getRate().getRemaining());
                                    editor.putLong(Rules.KEY_RESET_DATE, gc.getRate().getReset());
                                    editor.apply();
                                } else getErrMessage(context.getString(R.string.msg_location_not_found));

                            } else getErrMessage(gc.getStatus().getMessage());
                        }
                    } else Log.i("response.errorBody()", String.valueOf(response.errorBody() != null));
                }

                @Override
                public void onFailure(@NonNull Call<GeoCoordinates> call, @NonNull Throwable t) {
                    getErrMessage(t.getMessage());
                }
            });
    }

    private void getErrMessage(String error){
        Intent intent = new Intent(Rules.ACTION_ERROR_SERVER);
        intent.putExtra(KEY_ERR_SERVER, error);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
