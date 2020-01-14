package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.model.data.openweather.current

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Current(@SerializedName("coord") @Expose val coord: Coord,
                   @SerializedName("weather") @Expose val weather: List<Weather>,
                   @SerializedName("main") @Expose val main: Main,
                   @SerializedName("wind") @Expose val wind: Wind?,
                   @SerializedName("cloud") @Expose val cloud: Clouds?,
                   @SerializedName("rain") @Expose val rain: Rain?,
                   @SerializedName("snow") @Expose val snow: Snow?,
                   @SerializedName("dt") @Expose val dt: Long,
                   @SerializedName("sys") @Expose val sys: Sys,
                   @SerializedName("name") @Expose val name: String)