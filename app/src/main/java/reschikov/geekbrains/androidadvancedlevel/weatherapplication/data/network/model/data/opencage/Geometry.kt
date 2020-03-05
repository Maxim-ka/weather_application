package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.model.data.opencage

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Geometry(@SerializedName("lat") @Expose val lat: Double,
                    @SerializedName("lng") @Expose val lng: Double)
