package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.model.data.openweather.current

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Main(val temp: Double,
                @SerializedName("feels_like") @Expose
                val feelsLike: Double,
                val humidity: Int,
                @SerializedName("temp_min") @Expose
                val tempMin: Double,
                @SerializedName("temp_max") @Expose
                val tempMax: Double,
                val pressure: Int)