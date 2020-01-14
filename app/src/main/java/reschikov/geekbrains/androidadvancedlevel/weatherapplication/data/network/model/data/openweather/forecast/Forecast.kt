package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.model.data.openweather.forecast

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.model.data.openweather.current.*

class Forecast(@SerializedName("dt") @Expose val dt: Int,
               @SerializedName("main") @Expose val main: Main,
               @SerializedName("weather") @Expose val weather: List<Weather>,
               @SerializedName("clouds") @Expose val clouds: Clouds?,
               @SerializedName("wind") @Expose val wind: Wind,
               @SerializedName("rain") @Expose val rain: Rain?,
               @SerializedName("snow") @Expose val snow: Snow?,
               @SerializedName("dt_txt") @Expose val dt_txt: String)