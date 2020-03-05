package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.model.data.opencage

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class GeoCoordinates(@SerializedName("rate") @Expose val rate: Rate,
                          @SerializedName("results") @Expose val results: List<Result>,
                          @SerializedName("status") @Expose val status: Status)

