package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.apiux;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Day implements Parcelable {

    @SerializedName("maxtemp_c")
    @Expose
    private Double maxtempC;
    @SerializedName("mintemp_c")
    @Expose
    private Double mintempC;
    @SerializedName("maxwind_kph")
    @Expose
    private Double maxwindKph;
    @SerializedName("totalprecip_mm")
    @Expose
    private Double totalprecipMm;
    @SerializedName("avghumidity")
    @Expose
    private Double avghumidity;
    @SerializedName("condition")
    @Expose
    private Condition condition;

    public void setMaxtempC(Double maxtempC) {
        this.maxtempC = maxtempC;
    }

    public void setMintempC(Double mintempC) {
        this.mintempC = mintempC;
    }

    public void setMaxwindKph(Double maxwindKph) {
        this.maxwindKph = maxwindKph;
    }

    public void setTotalprecipMm(Double totalprecipMm) {
        this.totalprecipMm = totalprecipMm;
    }

    public void setAvghumidity(Double avghumidity) {
        this.avghumidity = avghumidity;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public Day() {}

    private Day(Parcel in) {
        if (in.readByte() == 0) {
            maxtempC = null;
        } else {
            maxtempC = in.readDouble();
        }
        if (in.readByte() == 0) {
            mintempC = null;
        } else {
            mintempC = in.readDouble();
        }
        if (in.readByte() == 0) {
            maxwindKph = null;
        } else {
            maxwindKph = in.readDouble();
        }
        if (in.readByte() == 0) {
            totalprecipMm = null;
        } else {
            totalprecipMm = in.readDouble();
        }
        if (in.readByte() == 0) {
            avghumidity = null;
        } else {
            avghumidity = in.readDouble();
        }
        condition = in.readParcelable(Condition.class.getClassLoader());
    }

    public static final Creator<Day> CREATOR = new Creator<Day>() {
        @Override
        public Day createFromParcel(Parcel in) {
            return new Day(in);
        }

        @Override
        public Day[] newArray(int size) {
            return new Day[size];
        }
    };

    public Double getMaxtempC() {
        return maxtempC;
    }

    public Double getMintempC() {
        return mintempC;
    }

    public Double getMaxwindKph() {
        return maxwindKph;
    }

    public Double getTotalprecipMm() {
        return totalprecipMm;
    }

    public Double getAvghumidity() {
        return avghumidity;
    }

    public Condition getCondition() {
        return condition;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (maxtempC == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(maxtempC);
        }
        if (mintempC == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(mintempC);
        }
        if (maxwindKph == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(maxwindKph);
        }
        if (totalprecipMm == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(totalprecipMm);
        }
        if (avghumidity == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(avghumidity);
        }
        dest.writeParcelable(condition, flags);
    }
}
