package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.apiux;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Location implements Parcelable {

    private static final double COMPARISON_ERROR = 0.01;
    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("region")
    @Expose
    private String region;

    @SerializedName("country")
    @Expose
    private String country;

    @SerializedName("lat")
    @Expose
    private Double lat;
    @SerializedName("lon")
    @Expose
    private Double lon;

    private boolean isShowingCity;

    private transient boolean selected;

    public boolean isShowingCity() {
        return isShowingCity;
    }

    public void setShowingCity(boolean showingCity) {
        isShowingCity = showingCity;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    private Location(Parcel in) {
        name = in.readString();
        region = in.readString();
        country = in.readString();
        lat = in.readDouble();
        lon = in.readDouble();
        boolean[] isLast = new boolean[1];
        in.readBooleanArray(isLast);
        isShowingCity = isLast[0];
    }

    public Location() {}

    public static final Creator<Location> CREATOR = new Creator<Location>() {
        @Override
        public Location createFromParcel(Parcel in) {
            return new Location(in);
        }

        @Override
        public Location[] newArray(int size) {
            return new Location[size];
        }
    };

    public String getName() {
        return name;
    }

    public String getRegion() {
        return region;
    }

    public String getCountry() {
        return country;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(region);
        dest.writeString(country);
        dest.writeDouble(lat);
        dest.writeDouble(lon);
        dest.writeBooleanArray(new boolean[]{isShowingCity});
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof Location)) return false;
        Location location = (Location) obj;
        return (Math.abs(lat - location.getLat()) <= COMPARISON_ERROR && Math.abs(lon - location.getLon()) <= COMPARISON_ERROR);
    }
}
