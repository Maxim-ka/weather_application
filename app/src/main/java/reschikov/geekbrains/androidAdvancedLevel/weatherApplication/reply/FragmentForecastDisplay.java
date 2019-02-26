package reschikov.geekbrains.androidadvancedlevel.weatherapplication.reply;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.apiux.ServerResponse;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.myView.MyRecyclerView;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.R;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.Rules;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.apiux.Forecast;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.apiux.Forecastday;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.apiux.Location;

public class FragmentForecastDisplay extends Fragment implements Collectable {

    public static FragmentForecastDisplay newInstance(ServerResponse response){
        FragmentForecastDisplay fragment = new FragmentForecastDisplay();
        Bundle args = new Bundle();
        args.putParcelable(Rules.KEY_WEATHER, response);
        fragment.setArguments(args);
        return fragment;
    }

    private Forecast forecast;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_frame, container, false);
        if (getArguments() != null){
            ServerResponse response = getArguments().getParcelable(Rules.KEY_WEATHER);
            if (response != null){
                forecast = response.getForecast();
            }

            if (forecast != null){
                MyRecyclerView recyclerView = view.findViewById(R.id.items);
                ForecastRVAdapter forecastRVAdapter = new ForecastRVAdapter(forecast.getForecastday(), getContext());
                recyclerView.setAdapter(forecastRVAdapter);
                recyclerView.setHasFixedSize(true);
                recyclerView.scrollChange(false);
            }
        }
        return view;
    }

    @Override
    public String collectData(Context context, ServerResponse response){
        Location location = response.getLocation();
        Forecast forecast = response.getForecast();
        List<Forecastday> forecastdayList = forecast.getForecastday();
        SimpleDateFormat format_24 = new SimpleDateFormat(context.getString(R.string.pattern_H_mm), Locale.getDefault());
        SimpleDateFormat format_12 = new SimpleDateFormat(context.getString(R.string.pattern_h_mm_a), Locale.US);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < forecastdayList.size(); i++) {
            try {
                sb.append(context.getString(R.string.city)).append(location.getName()).append("\n")
                    .append(context.getString(R.string.region)).append(location.getRegion()).append("\n")
                    .append(context.getString(R.string.day)).append(DateFormat.getDateInstance(DateFormat.FULL, Locale.getDefault()).format(new Date(forecastdayList.get(i).getDateEpoch() * Rules.IN_MILLISEC))).append("\n")
                    .append(context.getString(R.string.state)).append(forecastdayList.get(i).getDay().getCondition().getText()).append("\n")
                    .append(context.getString(R.string.min_temp)).append(forecastdayList.get(i).getDay().getMintempC()).append(Rules.C).append("\n")
                    .append(context.getString(R.string.max_temp)).append(forecastdayList.get(i).getDay().getMaxtempC()).append(Rules.C).append("\n")
                    .append(context.getString(R.string.max_wind)).append(String.format(Locale.getDefault(),Rules.$_2F_$S, (float)(forecastdayList.get(i).getDay().getMaxwindKph() * Rules.KMH_IN_MC), context.getString(R.string.m_c))).append("\n")
                    .append(context.getString(R.string.aver_humidity)).append(forecastdayList.get(i).getDay().getAvghumidity()).append(context.getString(R.string.pct)).append("\n")
                    .append(context.getString(R.string.rainfall)).append(forecastdayList.get(i).getDay().getTotalprecipMm()).append(context.getString(R.string.mm)).append("\n")
                    .append(context.getString(R.string.sunrise)).append(format_24.format(format_12.parse(forecastdayList.get(i).getAstro().getSunrise()))).append("\n")
                    .append(context.getString(R.string.sunset)).append(format_24.format(format_12.parse(forecastdayList.get(i).getAstro().getSunset()))).append("\n")
                    .append(context.getString(R.string.moonrise)).append(format_24.format(format_12.parse(forecastdayList.get(i).getAstro().getMoonrise()))).append("\n")
                    .append(context.getString(R.string.moonset)).append(format_24.format(format_12.parse(forecastdayList.get(i).getAstro().getMoonset()))).append("\n\n");
            }catch (ParseException e) {
                sb.append("\n\n");
                Log.e("ParseException", e.getMessage());
            }
        }
        return sb.toString();
    }
}
