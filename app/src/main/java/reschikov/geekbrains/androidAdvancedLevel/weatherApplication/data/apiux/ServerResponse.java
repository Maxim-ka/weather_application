package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.apiux;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ServerResponse implements Parcelable {

    @SerializedName("current")
    @Expose
    private Current current;

    @SerializedName("location")
    @Expose
    private Location location;

    @SerializedName("forecast")
    @Expose
    private Forecast forecast;

    @SerializedName("error")
    @Expose
    private Error error;

    public ServerResponse(){}

    protected ServerResponse(Parcel in) {
        current = in.readParcelable(Current.class.getClassLoader());
        location = in.readParcelable(Location.class.getClassLoader());
        forecast = in.readParcelable(Forecast.class.getClassLoader());
        error = in.readParcelable(Error.class.getClassLoader());
    }

    public static final Creator<ServerResponse> CREATOR = new Creator<ServerResponse>() {
        @Override
        public ServerResponse createFromParcel(Parcel in) {
            return new ServerResponse(in);
        }

        @Override
        public ServerResponse[] newArray(int size) {
            return new ServerResponse[size];
        }
    };

    public void setCurrent(Current current) {
        this.current = current;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setForecast(Forecast forecast) {
        this.forecast = forecast;
    }

    public Location getLocation() {
        return location;
    }

    public Current getCurrent() {
        return current;
    }

    public Forecast getForecast() {
        return forecast;
    }

    public Error getError() {
        return error;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(current, flags);
        dest.writeParcelable(location, flags);
        dest.writeParcelable(forecast, flags);
        dest.writeParcelable(error, flags);
    }
}
