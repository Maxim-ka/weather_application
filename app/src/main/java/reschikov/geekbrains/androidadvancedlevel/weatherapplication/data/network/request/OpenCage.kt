package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.request

import reschikov.geekbrains.androidadvancedlevel.weatherapplication.KEY
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.Q
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.model.data.opencage.GeoCoordinates
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenCage {

    @GET("json")
    fun coordinateRequest(@Query(Q) place: String,
                          @Query(KEY) key: String,
                          @Query("countrycode") code: String,
                          @Query("language") lang: String,
                          @Query("limit") limit: Int,
                          @Query("min_confidence") minConfidence: Int,
                          @Query("no_annotations") noAnnotations: Int,
                          @Query("no_dedupe") noDedupe: Int,
                          @Query("no_record") noRecord: Int): Call<GeoCoordinates>
}