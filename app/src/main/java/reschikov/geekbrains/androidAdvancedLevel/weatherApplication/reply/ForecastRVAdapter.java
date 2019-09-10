package reschikov.geekbrains.androidadvancedlevel.weatherapplication.reply;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import reschikov.geekbrains.androidadvancedlevel.weatherapplication.R;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.Rules;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.apiux.Astro;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.apiux.Day;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.apiux.Forecastday;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.myView.MyCardView;


class ForecastRVAdapter extends RecyclerView.Adapter<ForecastRVAdapter.ViewHolder>{

    private final List<Forecastday> items;
    private final Context context;

    ForecastRVAdapter(List<Forecastday> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_forecastday, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Forecastday item = items.get(i);
        viewHolder.bind(item);
    }

    @Override
    public int getItemCount() {
        if (items == null) return 0;
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        final ImageView image;
        final TextView dayView;
        final TextView state;
        final TextView minTempC;
        final TextView maxTempC;
        final TextView maxWind;
        final TextView avgHumidity;
        final TextView totalPrecipMM;
        final TextView sunrise;
        final TextView sunset;
        final TextView moonrise;
        final TextView moonset;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            dayView = itemView.findViewById(R.id.day);
            state = itemView.findViewById(R.id.state);
            minTempC = itemView.findViewById(R.id.minTemp);
            maxTempC = itemView.findViewById(R.id.maxTemp);
            maxWind = itemView.findViewById(R.id.maxWind);
            avgHumidity = itemView.findViewById(R.id.avghumidity);
            totalPrecipMM = itemView.findViewById(R.id.totalprecip_mm);
            sunrise = itemView.findViewById(R.id.sunrise);
            sunset = itemView.findViewById(R.id.sunset);
            moonrise = itemView.findViewById(R.id.moonrise);
            moonset = itemView.findViewById(R.id.moonset);
            ((MyCardView)itemView).turnScreen();
        }

        void bind(Forecastday item){
            long date = item.getDateEpoch() * Rules.IN_MILLISEC;
            Day day = item.getDay();
            Astro astro = item.getAstro();
            Picasso.get().load(context.getString(R.string.str_https) + day.getCondition().getIcon()).into(image);
            dayView.setText(DateFormat.getDateInstance(DateFormat.FULL, Locale.getDefault()).format(new Date(date)));
            state.setText(day.getCondition().getText());
            minTempC.setText(String.format(Locale.getDefault(),Rules.$_2F_$S, day.getMintempC(), Rules.C));
            maxTempC.setText(String.format(Locale.getDefault(),Rules.$_2F_$S, day.getMaxtempC(), Rules.C));
            ((MyCardView)itemView).setCardBackgroundColor(ContextCompat.getColor(context, new TemperatureColor(day.getMintempC(), day.getMaxtempC()).getColor()));
            float speed = (float) (day.getMaxwindKph() * Rules.KMH_IN_MC);

            maxWind.setText(String.format(Locale.getDefault(),Rules.$_2F_$S, speed, context.getString(R.string.m_c)));
            avgHumidity.setText(context.getString(R.string.percentage, day.getAvghumidity()));
            totalPrecipMM.setText(String.format(Locale.getDefault(),Rules.$_2F_$S, day.getTotalprecipMm(), context.getString(R.string.mm)));
            SimpleDateFormat format_24 = new SimpleDateFormat(context.getString(R.string.pattern_H_mm), Locale.getDefault());
            SimpleDateFormat format_12 = new SimpleDateFormat(context.getString(R.string.pattern_h_mm_a), Locale.US);
            try {
                sunrise.setText(format_24.format(format_12.parse(astro.getSunrise())));
                sunset.setText(format_24.format(format_12.parse(astro.getSunset())));
                moonrise.setText(format_24.format(format_12.parse(astro.getMoonrise())));
                moonset.setText(format_24.format(format_12.parse(astro.getMoonset())));
            } catch (ParseException e) {
                Log.e("ParseException", e.getMessage());
            }
        }
    }
}
