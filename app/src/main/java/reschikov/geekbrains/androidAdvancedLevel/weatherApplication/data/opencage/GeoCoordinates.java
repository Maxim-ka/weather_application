package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.opencage;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GeoCoordinates {

    @SerializedName("rate")
    @Expose
    private Rate rate;

    @SerializedName("results")
    @Expose
    private List<Result> results;

    @SerializedName("status")
    @Expose
    private Status status;

    public Rate getRate() {
        return rate;
    }

    public List<Result> getResults() {
        return results;
    }

    public Status getStatus() {
        return status;
    }
}
