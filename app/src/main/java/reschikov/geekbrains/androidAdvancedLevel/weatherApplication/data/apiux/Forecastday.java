package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.apiux;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Forecastday implements Parcelable {

    @SerializedName("date_epoch")
    @Expose
    private final Integer dateEpoch;
    @SerializedName("day")
    @Expose
    private final Day day;
    @SerializedName("astro")
    @Expose
    private final Astro astro;

    public Forecastday(Integer dateEpoch, Day day, Astro astro) {
        this.dateEpoch = dateEpoch;
        this.day = day;
        this.astro = astro;
    }

    private Forecastday(Parcel in) {
        if (in.readByte() == 0) {
            dateEpoch = null;
        } else {
            dateEpoch = in.readInt();
        }
        day = in.readParcelable(Day.class.getClassLoader());
        astro = in.readParcelable(Astro.class.getClassLoader());
    }

    public static final Creator<Forecastday> CREATOR = new Creator<Forecastday>() {
        @Override
        public Forecastday createFromParcel(Parcel in) {
            return new Forecastday(in);
        }

        @Override
        public Forecastday[] newArray(int size) {
            return new Forecastday[size];
        }
    };

    public Integer getDateEpoch() {
        return dateEpoch;
    }

    public Day getDay() {
        return day;
    }

    public Astro getAstro() {
        return astro;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (dateEpoch == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(dateEpoch);
        }
        dest.writeParcelable(day, flags);
        dest.writeParcelable(astro, flags);
    }
}
