package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.model.data.openweather.current

import androidx.room.ColumnInfo
import androidx.room.Ignore
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Main{

    @SerializedName("temp") @Expose
    @ColumnInfo(name = "temp") var temp: Double = 0.0
    @SerializedName("feels_like") @Expose
    @ColumnInfo(name = "feelsLike") var feelsLike: Double = 0.0
    @SerializedName("humidity") @Expose
    @ColumnInfo(name = "humidity") var humidity: Int = 0
    @SerializedName("pressure") @Expose
    @Ignore var pressure: Int = 0
    @SerializedName("temp_min") @Expose
    @ColumnInfo(name = "tempMin") var tempMin: Double = 0.0
    @SerializedName("temp_max") @Expose
    @ColumnInfo(name = "tempMax") var tempMax: Double = 0.0
}