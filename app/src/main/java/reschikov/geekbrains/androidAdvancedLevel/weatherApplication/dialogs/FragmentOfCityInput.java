package reschikov.geekbrains.androidadvancedlevel.weatherapplication.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputEditText;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import reschikov.geekbrains.androidadvancedlevel.weatherapplication.Rules;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.Situateable;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.network.FragmentOfOutputOfProgressAction;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.R;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.network.RequestGeoCoordinates;

import static androidx.preference.PreferenceManager.getDefaultSharedPreferences;

public class FragmentOfCityInput extends DialogFragment {

    private static final int MAX_NUMBER_REQUESTS = 2500;
    private static final long DAY_IN_SEC = 86_400L;
    private Situateable situateable;
    private TextInputEditText placeInput;
    private boolean isGeoCoder;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.city_input_dialog, container, false);

        if (getContext() != null){
            SharedPreferences sp = getDefaultSharedPreferences(getContext());
            isGeoCoder = sp.getBoolean(Rules.KEY_ON_GEOCODER, false);
            if (isGeoCoder){
                int remainder = sp.getInt(Rules.KEY_NUMBER_OF_REQUESTS, MAX_NUMBER_REQUESTS);
                if (remainder < RequestGeoCoordinates.LIMIT){
                    long currDateTime = new Date().getTime();
                    long dateTime = sp.getLong(Rules.KEY_RESET_DATE, currDateTime + DAY_IN_SEC);
                    if (dateTime > currDateTime){
                        isGeoCoder = false;
                        String msg = getString(R.string.msg_limit_calls_geoservice,
                            DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.getDefault())
                                .format(new Date(dateTime * Rules.IN_MILLISEC)));
                        if (getActivity() != null) Admonition.newInstance(msg, Rules.WARNING, null)
                            .show(getActivity().getSupportFragmentManager(), Rules.TAG_LIMIT_IS_EXCEEDED);
                    }
                }
            }
        }
        int title = (isGeoCoder) ? R.string.specify_place : R.string.enter_city;
        getDialog().setTitle(title);
        int hint = (isGeoCoder) ? R.string.place : R.string.name_city;
        String string = (isGeoCoder) ? getString(R.string.district_city_region_country) : getString(R.string.enter_city);

        TextView textView = view.findViewById(R.id.enter_city);
        textView.setText(string);
        placeInput = view.findViewById(R.id.input_city);
        placeInput.setHint(hint);
        Button butOK = view.findViewById(R.id.but_ok);
        Button butCancel = view.findViewById(R.id.but_cancel);

        butOK.setOnClickListener((View v) -> {
            if (placeInput.getText() != null){
                String place = placeInput.getText().toString().trim();
                if (place.isEmpty() || !place.matches(getString(R.string.not_number))){
                    String msg = (isGeoCoder) ? getString(R.string.you_must_enter_place) : getString(R.string.you_must_enter_city);
                    Toast.makeText(v.getContext(), msg, Toast.LENGTH_LONG).show();
                }else{
                    if (getActivity() != null) getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(situateable.getIdFrameLayout(), FragmentOfOutputOfProgressAction.newInstance(place, isGeoCoder))
                        .commit();
                    dismiss();
                }
            }
        });
        butCancel.setOnClickListener((View v) -> dismiss());
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        situateable = (Situateable) context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }
}
