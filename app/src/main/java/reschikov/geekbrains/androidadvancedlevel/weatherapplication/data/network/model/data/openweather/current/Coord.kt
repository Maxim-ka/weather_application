package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.model.data.openweather.current

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Coord(@SerializedName("lon") @Expose val lon: Double,
                 @SerializedName("lat") @Expose val lat: Double)