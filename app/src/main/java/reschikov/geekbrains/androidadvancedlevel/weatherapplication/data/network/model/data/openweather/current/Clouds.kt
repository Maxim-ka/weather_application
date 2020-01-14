package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.model.data.openweather.current

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Clouds(@SerializedName("all") @Expose val all: Int)