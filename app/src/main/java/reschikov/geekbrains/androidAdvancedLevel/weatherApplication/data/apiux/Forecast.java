package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.apiux;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Forecast implements Parcelable {

    @SerializedName("forecastday")
    @Expose
    private final List<Forecastday> forecastday;

    public Forecast(List<Forecastday> forecastday) {
        this.forecastday = forecastday;
    }

    private Forecast(Parcel in) {
        forecastday = in.createTypedArrayList(Forecastday.CREATOR);
    }

    public static final Creator<Forecast> CREATOR = new Creator<Forecast>() {
        @Override
        public Forecast createFromParcel(Parcel in) {
            return new Forecast(in);
        }

        @Override
        public Forecast[] newArray(int size) {
            return new Forecast[size];
        }
    };

    public List<Forecastday> getForecastday() {
        return forecastday;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(forecastday);
    }
}
