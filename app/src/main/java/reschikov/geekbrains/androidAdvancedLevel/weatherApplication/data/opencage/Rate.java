package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.opencage;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Rate {

    @SerializedName("limit")
    @Expose
    private Integer limit;
    @SerializedName("remaining")
    @Expose
    private Integer remaining;
    @SerializedName("reset")
    @Expose
    private Integer reset;

    public Integer getLimit() {
        return limit;
    }

    public Integer getRemaining() {
        return remaining;
    }

    public Integer getReset() {
        return reset;
    }
}
