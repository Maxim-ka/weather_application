package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.apiux;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Current implements Parcelable {

    @SerializedName("last_updated_epoch")
    @Expose
    private Integer lastUpdatedEpoch;

    @SerializedName("temp_c")
    @Expose
    private Double tempC;

    @SerializedName("is_day")
    @Expose
    private Integer isDay;

    @SerializedName("condition")
    @Expose
    private Condition condition;

    @SerializedName("wind_kph")
    @Expose
    private Double windKph;

    @SerializedName("wind_dir")
    @Expose
    private String windDir;
    @SerializedName("pressure_mb")
    @Expose
    private Double pressureMb;

    @SerializedName("precip_mm")
    @Expose
    private Double precipMm;

    @SerializedName("humidity")
    @Expose
    private Integer humidity;

    @SerializedName("cloud")
    @Expose
    private Integer cloud;

    @SerializedName("feelslike_c")
    @Expose
    private Double feelslikeC;

    public Integer getLastUpdatedEpoch() {
        return lastUpdatedEpoch;
    }

    public void setLastUpdatedEpoch(Integer lastUpdatedEpoch) {
        this.lastUpdatedEpoch = lastUpdatedEpoch;
    }

    public void setTempC(Double tempC) {
        this.tempC = tempC;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public void setWindKph(Double windKph) {
        this.windKph = windKph;
    }

    public void setWindDir(String windDir) {
        this.windDir = windDir;
    }

    public void setPressureMb(Double pressureMb) {
        this.pressureMb = pressureMb;
    }

    public void setPrecipMm(Double precipMm) {
        this.precipMm = precipMm;
    }

    public void setHumidity(Integer humidity) {
        this.humidity = humidity;
    }

    public void setCloud(Integer cloud) {
        this.cloud = cloud;
    }

    public void setFeelslikeC(Double feelslikeC) {
        this.feelslikeC = feelslikeC;
    }

    public Current() {}

    private Current(Parcel in) {
        if (in.readByte() == 0) {
            lastUpdatedEpoch = null;
        } else {
            lastUpdatedEpoch = in.readInt();
        }
        if (in.readByte() == 0) {
            tempC = null;
        } else {
            tempC = in.readDouble();
        }
        if (in.readByte() == 0) {
            isDay = null;
        } else {
            isDay = in.readInt();
        }
        if (in.readByte() == 0) {
            windKph = null;
        } else {
            windKph = in.readDouble();
        }
        windDir = in.readString();
        if (in.readByte() == 0) {
            pressureMb = null;
        } else {
            pressureMb = in.readDouble();
        }
        if (in.readByte() == 0) {
            precipMm = null;
        } else {
            precipMm = in.readDouble();
        }
        if (in.readByte() == 0) {
            humidity = null;
        } else {
            humidity = in.readInt();
        }
        if (in.readByte() == 0) {
            cloud = null;
        } else {
            cloud = in.readInt();
        }
        if (in.readByte() == 0) {
            feelslikeC = null;
        } else {
            feelslikeC = in.readDouble();
        }
        if ((in.readByte() == 0)){
            condition = null;
        } else {
            condition = in.readParcelable(getClass().getClassLoader());
        }
    }

    public static final Creator<Current> CREATOR = new Creator<Current>() {
        @Override
        public Current createFromParcel(Parcel in) {
            return new Current(in);
        }

        @Override
        public Current[] newArray(int size) {
            return new Current[size];
        }
    };

    public Double getTempC() {
        return tempC;
    }

    public Integer getIsDay() {
        return isDay;
    }

    public Condition getCondition() {
        return condition;
    }

    public Double getWindKph() {
        return windKph;
    }

    public String getWindDir() {
        return windDir;
    }

    public Double getPressureMb() {
        return pressureMb;
    }

    public Double getPrecipMm() {
        return precipMm;
    }

    public Integer getHumidity() {
        return humidity;
    }

    public Integer getCloud() {
        return cloud;
    }

    public Double getFeelslikeC() {
        return feelslikeC;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (lastUpdatedEpoch == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(lastUpdatedEpoch);
        }
        if (tempC == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(tempC);
        }
        if (isDay == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(isDay);
        }
        if (windKph == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(windKph);
        }
        dest.writeString(windDir);
        if (pressureMb == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(pressureMb);
        }
        if (precipMm == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(precipMm);
        }
        if (humidity == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(humidity);
        }
        if (cloud == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(cloud);
        }
        if (feelslikeC == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(feelslikeC);
        }
        if (condition == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeParcelable(condition, flags);
        }
    }
}
