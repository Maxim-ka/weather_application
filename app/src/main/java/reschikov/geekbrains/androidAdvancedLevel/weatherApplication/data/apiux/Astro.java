package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.apiux;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Astro implements Parcelable {

    @SerializedName("sunrise")
    @Expose
    private String sunrise;
    @SerializedName("sunset")
    @Expose
    private String sunset;
    @SerializedName("moonrise")
    @Expose
    private String moonrise;
    @SerializedName("moonset")
    @Expose
    private String moonset;

    public void setSunrise(String sunrise) {
        this.sunrise = sunrise;
    }

    public void setSunset(String sunset) {
        this.sunset = sunset;
    }

    public void setMoonrise(String moonrise) {
        this.moonrise = moonrise;
    }

    public void setMoonset(String moonset) {
        this.moonset = moonset;
    }

    public Astro() {}

    private Astro(Parcel in) {
        sunrise = in.readString();
        sunset = in.readString();
        moonrise = in.readString();
        moonset = in.readString();
    }

    public static final Creator<Astro> CREATOR = new Creator<Astro>() {
        @Override
        public Astro createFromParcel(Parcel in) {
            return new Astro(in);
        }

        @Override
        public Astro[] newArray(int size) {
            return new Astro[size];
        }
    };

    public String getSunrise() {
        return sunrise;
    }

    public String getSunset() {
        return sunset;
    }

    public String getMoonrise() {
        return moonrise;
    }

    public String getMoonset() {
        return moonset;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(sunrise);
        dest.writeString(sunset);
        dest.writeString(moonrise);
        dest.writeString(moonset);
    }
}
