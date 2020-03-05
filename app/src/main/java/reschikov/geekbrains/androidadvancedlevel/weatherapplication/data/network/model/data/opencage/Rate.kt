package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.model.data.opencage

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Rate(@SerializedName("limit") @Expose val limit: Int,
                @SerializedName("remaining") @Expose val remaining: Int,
                @SerializedName("reset") @Expose val reset: Int)