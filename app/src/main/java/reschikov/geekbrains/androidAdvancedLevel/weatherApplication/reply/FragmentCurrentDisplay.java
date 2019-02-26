package reschikov.geekbrains.androidadvancedlevel.weatherapplication.reply;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.apiux.Location;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.R;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.Rules;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.apiux.Condition;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.apiux.Current;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.apiux.ServerResponse;

public class FragmentCurrentDisplay extends Fragment implements Collectable{

    public static FragmentCurrentDisplay newInstance(ServerResponse response){
        FragmentCurrentDisplay fragment = new FragmentCurrentDisplay();
        Bundle args = new Bundle();
        args.putParcelable(Rules.KEY_WEATHER, response);
        fragment.setArguments(args);
        return fragment;
    }

    private ImageView icon;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.current_frame, container, false);
        TextView nameCity = view.findViewById(R.id.city);
        TextView day = view.findViewById(R.id.day);
        TextView time = view.findViewById(R.id.time);
        icon = view.findViewById(R.id.icon);
        TextView temperature = view.findViewById(R.id.temperature);
        TextView text = view.findViewById(R.id.text);
        TextView felt = view.findViewById(R.id.temperature__felt);
        TextView wind = view.findViewById(R.id.wind);
        TextView direction = view.findViewById(R.id.direction);
        TextView humidity = view.findViewById(R.id.humidity);
        TextView rainfall = view.findViewById(R.id.rainfall);
        TextView pressure = view.findViewById(R.id.pressure);
        TextView overcast = view.findViewById(R.id.overcast);

        if (getResources().getBoolean(R.bool.is_X_large_layout)){
            LinearLayout linear = view.findViewById(R.id.day_time);
            linear.setOrientation(LinearLayout.HORIZONTAL);
            linear.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);
            time.setGravity(Gravity.END);
        }

        if (getArguments() != null) {
            ServerResponse sr = getArguments().getParcelable(Rules.KEY_WEATHER);
            if (sr != null){
                Location location = sr.getLocation();
                Current current = sr.getCurrent();
                if (current != null && getContext() != null){
                    Condition condition = current.getCondition();
                    uploadPicture(condition.getIcon());
                    nameCity.setText(location.getName());
                    long lastUpdatedTime = current.getLastUpdatedEpoch() * Rules.IN_MILLISEC;
                    Date date = new Date(lastUpdatedTime);
                    day.setText(DateFormat.getDateInstance(DateFormat.FULL).format(date));
                    String lastTime = (System.currentTimeMillis() - lastUpdatedTime >= Rules.THREE_HOURS) ? getString(R.string.out_date):
                            DateFormat.getTimeInstance(DateFormat.LONG).format(date);
                    time.setText(lastTime);
                    temperature.setText(getTemp(current));
                    view.setBackgroundColor(ContextCompat.getColor(getContext(), new TemperatureColor(current.getTempC()).getColor()));
                    text.setText(condition.getText());
                    felt.setText(String.format(Locale.getDefault(),Rules.$_2F_$S, current.getFeelslikeC(), Rules.C));
                    direction.setText(getDirectionWind(current));
                    wind.setText(getWind(current, getContext()));
                    humidity.setText(getHumidity(current));
                    pressure.setText(getPressure(current, getContext()));
                    rainfall.setText(getRainfall(current, getContext()));
                    overcast.setText(getOvercast(current));
                }
            }
        }
        return view;
    }

    private void uploadPicture(final String iconPath){
        Picasso.get().load(getString(R.string.str_https) + iconPath).into(icon);
    }

    private String getTemp(Current current){
        return String.format(Locale.getDefault(),Rules.$_2F_$S, current.getTempC(), Rules.C);
    }

    private String getWind(Current current, Context context){
        float speed = (float) (current.getWindKph() * Rules.KMH_IN_MC);
        return String.format(Locale.getDefault(),Rules.$_2F_$S, speed, context.getString(R.string.m_c));
    }

    private String getDirectionWind(Current current){
        float speed = (float) (current.getWindKph() * Rules.KMH_IN_MC);
        if (speed > 0){
            String horizonsOfRhumbs = current.getWindDir().toUpperCase();
            if (Locale.getDefault().getLanguage().equals(Rules.RU)) horizonsOfRhumbs = translationIntoRussian(horizonsOfRhumbs);
            return horizonsOfRhumbs;
        }
        return null;
    }

    private String getPressure(Current current, Context context){
        float press = (float) (current.getPressureMb() * Rules.FROM_MBAR_IN_MMHG);
        return String.format(Locale.getDefault(),Rules.$_2F_$S, press, context.getString(R.string.mm_Hg));
    }

    private String getHumidity(Current current){
        return String.format(Locale.getDefault(),Rules.$D_, current.getHumidity());
    }

    private String getRainfall(Current current, Context context){
        return String.format(Locale.getDefault(),Rules.$_2F_$S, current.getPrecipMm(), context.getString(R.string.mm));
    }

    private String getOvercast(Current current){
        return String.format(Locale.getDefault(),Rules.$D_, current.getCloud());
    }

    @Override
    public String collectData(Context context, ServerResponse response){
        Location location = response.getLocation();
        Current current = response.getCurrent();
        return context.getString(R.string.city) + location.getName() + "\n" +
               context.getString(R.string.region) + location.getRegion() + "\n" +
               context.getString(R.string.data_update) +
                DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT, Locale.getDefault())
                    .format(new Date(current.getLastUpdatedEpoch() * Rules.IN_MILLISEC)) + "\n" +
               context.getString(R.string.temp) + getTemp(current) + "\n" +
               context.getString(R.string.state) + current.getCondition().getText() + "\n" +
               context.getString(R.string.wind) + getDirectionWind(current) + " " + getWind(current, context) + "\n" +
               context.getString(R.string.pressure) + getPressure(current, context) + "\n" +
               context.getString(R.string.humidity) + getHumidity(current) + "\n" +
               context.getString(R.string.rainfall) + getRainfall(current, context) + "\n" +
               context.getString(R.string.overcast) + getOvercast(current);
    }

    private String translationIntoRussian(String string){
        StringBuilder sb = new StringBuilder();
        char[] chars = string.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            switch (chars[i]){
                case 'N':
                    sb.append('C');
                    break;
                case 'E':
                    sb.append('В');
                    break;
                case 'S':
                    sb.append('Ю');
                    break;
                case 'W':
                    sb.append('З');
                    break;
            }
        }
        return sb.toString();
    }
}
