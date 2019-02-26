package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.opencage;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Result implements Parcelable {

    @SerializedName("formatted")
    @Expose
    private final String formatted;
    @SerializedName("geometry")
    @Expose
    private final Geometry geometry;

    private Result(Parcel in) {
        formatted = in.readString();
        geometry = in.readParcelable(Geometry.class.getClassLoader());
    }

    public static final Creator<Result> CREATOR = new Creator<Result>() {
        @Override
        public Result createFromParcel(Parcel in) {
            return new Result(in);
        }

        @Override
        public Result[] newArray(int size) {
            return new Result[size];
        }
    };

    public String getFormatted() {
        return formatted;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(formatted);
        dest.writeParcelable(geometry, flags);
    }
}
