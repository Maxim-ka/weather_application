package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.model.data.opencage

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Result(@SerializedName("formatted") @Expose val formatted: String,
                  @SerializedName("geometry") @Expose val geometry: Geometry)