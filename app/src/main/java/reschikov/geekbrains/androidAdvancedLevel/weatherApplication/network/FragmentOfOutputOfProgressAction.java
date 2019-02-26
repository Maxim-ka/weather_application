package reschikov.geekbrains.androidadvancedlevel.weatherapplication.network;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ProgressBar;
import android.widget.Toast;
import java.util.ArrayList;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.R;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.Rules;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.opencage.Result;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.database.DatabaseService;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.dialogs.FragmentGeo;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.settings.FragmentOfListOfCities;

public class FragmentOfOutputOfProgressAction extends Fragment {

    public static Fragment newInstance(String location, boolean isGeoCoder){
        FragmentOfOutputOfProgressAction fragment = new FragmentOfOutputOfProgressAction();
        Bundle args = new Bundle();
        args.putString(Rules.KEY_PLACE, location);
        args.putBoolean(Rules.KEY_IS_GEO_CODER, isGeoCoder);
        fragment.setArguments(args);
        return fragment;
    }

    private ProgressBar progressLine;
    private ProgressBar progressCircle;
    private Chronometer chronometer;
    private Messenger messenger;
    private boolean binding;
    private boolean isGeoCoder;
    private String city;
    private boolean isXLargeLayout;
    private final FragmentGeo.Selectable selectable = new FragmentGeo.Selectable() {
        @Override
        public void getGeoCoordinates(String string) {
            getCoordinates(string);
        }

        @Override
        public void discontinue() {
            breakIn();
        }
    };
    private final Messenger counting = new Messenger(new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case Rules.FAILURE:
                    showPreviousWeather();
                    return true;
                case Rules.TIME_START:
                    chronometer.start();
                    return true;
                case Rules.TIME_FINISH:
                    chronometer.stop();
                    return true;
                case Rules.SHOW_LOCATION:
                    Location location = (Location) msg.obj;
                    if (getContext() != null){
                        String string = getContext().getString(R.string.toast_format_coordinates, location.getLatitude(), location.getLongitude(), location.getAccuracy());
                        Toast.makeText(getContext(), string, Toast.LENGTH_LONG).show();
                    }
                    return true;
                case Rules.PROGRESS:
                    if (progressLine.getVisibility() == View.GONE){
                        progressCircle.setVisibility(View.GONE);
                        chronometer.setVisibility(View.GONE);
                        progressLine.setVisibility(View.VISIBLE);
                    }
                    progressLine.setProgress((Integer) msg.obj);
                    return true;
                case Rules.CLARIFICATION:
                    if (getActivity() != null){
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        FragmentGeo fg;
                        if ((fg = (FragmentGeo) fm.findFragmentByTag(Rules.TAG_CHOICE_PLACE)) != null) {
                            fg.setSelectable(selectable);
                        } else {
                            ArrayList<Result> results = msg.getData().getParcelableArrayList(Rules.KEY_RESULTS);
                            fg = FragmentGeo.newInstance(results);
                            fg.setSelectable(selectable);
                            if (getResources().getBoolean(R.bool.is_large_layout) || isXLargeLayout){
                                fg.show(fm, Rules.TAG_CHOICE_PLACE);
                            } else fm.beginTransaction().replace(R.id.frame_master, fg, Rules.TAG_CHOICE_PLACE).commit();
                        }
                    }
                    return true;
                default:
                    return false;
            }
        }
    }));
    private final ServiceConnection connection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            messenger = new Messenger(service);
            binding = true;
            sendRequest(city);
        }

        public void onServiceDisconnected(ComponentName className) {
            messenger = null;
            binding = false;
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.progress_bar, container, false);

        isXLargeLayout = getResources().getBoolean(R.bool.is_X_large_layout);

        progressLine = view.findViewById(R.id.progressLine);
        progressCircle = view.findViewById(R.id.progressCircle);
        chronometer = view.findViewById(R.id.chronometer);
        chronometer.setBase(SystemClock.elapsedRealtime());

        if (getArguments() != null){
            city = getArguments().getString(Rules.KEY_PLACE);
            setVisibilityProgressBar(city);
            isGeoCoder = getArguments().getBoolean(Rules.KEY_IS_GEO_CODER);
            if (getActivity() != null){
                getActivity().bindService(new Intent(getContext(), NetworkService.class), connection, Context.BIND_AUTO_CREATE);
            }
        }
        return view;
    }

    private void sendRequest(String city){
        if (!binding) return;
        int what;
        if (isGeoCoder) what = Rules.COORDINATE_DETERMINATION_REQUEST;
        else what = (city == null) ? Rules.GOOGLE_COORDINATE_REQUEST :
            (Rules.KEY_NO_GOOGLE.equals(city)) ? Rules.REQUEST_DETERMINE_COORDINATES_ANDROID : Rules.REQUEST_CITY;
        Message msg = Message.obtain(null, what, city);
        msg.replyTo = counting;
        try {
            messenger.send(msg);
        } catch (RemoteException e) {
            Log.e("sendRequest", e.getMessage());
        }
    }

    private void setVisibilityProgressBar(String city){
        if (city == null || city.equals(Rules.KEY_NO_GOOGLE)){
            progressCircle.setVisibility(View.VISIBLE);
            chronometer.setVisibility(View.VISIBLE);
            progressLine.setVisibility(View.GONE);
        } else {
            progressCircle.setVisibility(View.GONE);
            chronometer.setVisibility(View.GONE);
            progressLine.setVisibility(View.VISIBLE);
        }
    }

    public void breakIn(){
        if (chronometer.isActivated()) chronometer.stop();
        if (getActivity() != null) {
            if (binding){
                try {
                    messenger.send(Message.obtain(null, Rules.BREAKING));
                } catch (RemoteException e) {
                    Log.e("breakIn", e.getMessage());
                }
                getActivity().unbindService(connection);
                binding = false;
            }
            if (isXLargeLayout) showPreviousWeather();
            else getActivity().getSupportFragmentManager()
                .beginTransaction().replace(R.id.frame_master, new FragmentOfListOfCities())
                .commit();
        }
    }

    private void showPreviousWeather(){
        if (getActivity() != null){
            Intent intent = new Intent(getContext(), DatabaseService.class);
            intent.setAction(Rules.GET_WEATHER_DB);
            getActivity().startService(intent);
            getActivity().getSupportFragmentManager().beginTransaction().remove(this).commitAllowingStateLoss();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getActivity() != null && binding)getActivity().unbindService(connection);
        binding = false;
    }

    private void getCoordinates(String string) {
        if (!binding) return;
        Message msg = Message.obtain(null, Rules.REQUEST_CITY, Rules.DEFNIITION, Rules.ZERO, string);
        msg.replyTo = counting;
        try {
            messenger.send(msg);
        } catch (RemoteException e) {
            Log.e("getCoordinates", e.getMessage());
        }
    }
}
