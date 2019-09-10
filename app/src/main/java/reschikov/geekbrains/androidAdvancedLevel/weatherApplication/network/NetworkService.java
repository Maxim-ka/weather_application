package reschikov.geekbrains.androidadvancedlevel.weatherapplication.network;

import android.Manifest;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.tasks.Task;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.Rules;
import static java.lang.Thread.interrupted;
import static reschikov.geekbrains.androidadvancedlevel.weatherapplication.Rules.ACTION_COORDINATES_NOT_DEFINED_ANDROID;

public class NetworkService extends Service {

    private static final int REPEAT = 3_000;
    private long period = 1_000L;
    private long repeat = 0;
    private boolean isOffline;
    private LocationManager lm;
    private LocationListener locationListener;
    private FusedLocationProviderClient locationProviderClient;
    private LocationCallback lcb;
    private Thread thread;
    private Runnable runnable;
    private float accuracy;
    private HandlerThread ht;
    private Handler handlerCoordinates;
    private final Handler handler = new Handler(new RequestHandler());
    private final Messenger messenger = new Messenger(handler);

    private class RequestHandler implements Handler.Callback {
        @Override
        public boolean handleMessage(Message msg) {
            try {
                if (isOffline){
                    msg.replyTo.send(Message.obtain(null, Rules.FAILURE));
                    return false;
                }
                switch (msg.what) {
                    case Rules.BREAKING:
                        stopGettingCoordinates(true);
                        return true;
                    case Rules.GOOGLE_COORDINATE_REQUEST:
                        determineCoordinatesViaGoogle(msg.replyTo);
                        return true;
                    case Rules.REQUEST_DETERMINE_COORDINATES_ANDROID:
                        determineCoordinates(msg.replyTo);
                        return true;
                    case Rules.REQUEST_CITY:
                        new RequestWeather(getBaseContext()).toRequest(msg);
                        return true;
                    case Rules.COORDINATE_DETERMINATION_REQUEST:
                        new RequestGeoCoordinates(getBaseContext()).toRequest(msg);
                        return true;
                }
            } catch (RemoteException e) {
                Log.e("handleMessage", e.getMessage());
            }
            return false;
        }
    }

    @Override
    public void onCreate() {
        if (isOffline = !connectionCheck()) {
            LocalBroadcastManager.getInstance(getBaseContext())
                    .sendBroadcast(new Intent(Rules.ACTION_NOT_CONNECTED));
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }

    private void determineCoordinates(final Messenger counting) {
        if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermission();
            return ;
        }
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_MEDIUM);
        String provider = lm.getBestProvider(criteria, true);
        if (provider == null) provider = LocationManager.NETWORK_PROVIDER;
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location != null) {
                    showLocation(counting, location);
                    if ((accuracy = location.getAccuracy()) <= Rules.MIN_ACCURACY) {
                        lm.removeUpdates(this);
                        thread.interrupt();
                        String coordinates = location.getLatitude() + "," + location.getLongitude();
                        try {
                            counting.send(Message.obtain(null, Rules.TIME_FINISH));
                            Message message = Message.obtain(handler, Rules.REQUEST_CITY, Rules.ZERO, Rules.DEFNIITION,coordinates);
                            message.replyTo = counting;
                            messenger.send(message);
                        } catch (RemoteException e) {
                            Log.e("onLocationChanged", e.getMessage());
                        }
                    } else period += period;
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                if (provider.equals(LocationManager.NETWORK_PROVIDER) &&
                        status == LocationProvider.OUT_OF_SERVICE) stopGettingCoordinates(false);
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
                if (provider.equals(LocationManager.NETWORK_PROVIDER)) {
                    stopGettingCoordinates(false);
                }
            }
        };
        lm.requestLocationUpdates(provider, period, 0, locationListener);
        countdown(counting);
    }

    private void countdown(final Messenger counting){
        thread = new Thread(new Runnable() {
            long beginning = 0;
            @Override
            public void run() {
                while (thread.isAlive() && !interrupted()){
                    try {
                        if (beginning == 0) {
                            counting.send(Message.obtain(null, Rules.TIME_START));
                            beginning = System.currentTimeMillis();
                        }
                        if (System.currentTimeMillis() - beginning > Rules.SEARCH_TIME){
                            counting.send(Message.obtain(null, Rules.TIME_FINISH));
                            counting.send(Message.obtain(null, Rules.FAILURE));
                            stopGettingCoordinates(false);
                        }
                    } catch (RemoteException e) {
                        stopGettingCoordinates(false);
                        Log.e("countdown", e.getMessage());
                    }
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    private void stopGettingCoordinates(boolean breaking){
        if (lm != null)lm.removeUpdates(locationListener);
        if (locationProviderClient != null)locationProviderClient.removeLocationUpdates(lcb);
        if (thread != null) thread.interrupt();
        if (handlerCoordinates != null) handlerCoordinates.removeCallbacks(runnable);
        if (ht != null) ht.quit();
        if (!breaking) {
            Intent intent = new Intent(ACTION_COORDINATES_NOT_DEFINED_ANDROID);
            intent.putExtra(Rules.KEY_ACCURACY, accuracy);
            LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent);
        }
    }

    private void showLocation(Messenger counting, Location location){
        try {
            counting.send(Message.obtain(null, Rules.SHOW_LOCATION, location));
        } catch (RemoteException e) {
            Log.e("showLocation", e.getMessage());
        }
    }

    private void determineCoordinatesViaGoogle(final Messenger counting){
        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(0);
        locationRequest.setFastestInterval(0);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> taskSetting = LocationServices.getSettingsClient(this)
                .checkLocationSettings(builder.build());

        ht = new HandlerThread("Determine Coordinates Via Google");
        ht.start();
        handlerCoordinates = new Handler(ht.getLooper());

        taskSetting.addOnSuccessListener(locationSettingsResponse -> {
            if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermission();
                return;
            }
            locationProviderClient = LocationServices.getFusedLocationProviderClient(getBaseContext());

            runnable = () -> {
                lcb = new LocationCallback() {
                    @Override
                    public void onLocationResult(final LocationResult locationResult) {
                        if (locationResult == null) return;
                        Location location = locationResult.getLastLocation();
                        showLocation(counting, location);
                        locationProviderClient.removeLocationUpdates(lcb);
                        if ((accuracy = location.getAccuracy()) <= Rules.MIN_ACCURACY) {
                            stopGettingCoordinates(true);
                            String coordinates = location.getLatitude() + "," + location.getLongitude();
                            try {
                                counting.send(Message.obtain(null, Rules.TIME_FINISH));
                                Message message = Message.obtain(handler, Rules.REQUEST_CITY, Rules.ZERO, Rules.DEFNIITION,coordinates);
                                message.replyTo = counting;
                                messenger.send(message);
                            } catch (RemoteException e) {
                                Log.e("onLocationResult", e.getMessage());
                            }
                        } else {
                            repeat += REPEAT;
                            handlerCoordinates.postDelayed(runnable, repeat);
                        }
                    }
                };

                if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    Task<Void> task = locationProviderClient.requestLocationUpdates(locationRequest, lcb, null);

                    task.addOnFailureListener(e -> {
                        try {
                            counting.send(Message.obtain(null, Rules.TIME_FINISH));
                        } catch (RemoteException e1) {
                            Log.e("OnFailureListener", e1.getMessage());
                        }finally {
                            stopGettingCoordinates(false);
                        }
                    });
                }
            };
            handlerCoordinates.postDelayed(runnable, repeat);
            countdown(counting);
        });

        taskSetting.addOnFailureListener(e -> {
            if (e instanceof ResolvableApiException) {
                ResolvableApiException resolvable = (ResolvableApiException) e;
                PendingIntent pendingIntent = resolvable.getResolution();
                Intent intent = new Intent(Rules.ACTION_ERROR_SETTING);
                intent.putExtra(Rules.KEY_ERROR_SETTING, pendingIntent);
                LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent);
            }
        });
    }

    private void requestPermission(){
        LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(new Intent(Rules.ACTION_NO_PERMISSION));
    }

    private boolean connectionCheck(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
