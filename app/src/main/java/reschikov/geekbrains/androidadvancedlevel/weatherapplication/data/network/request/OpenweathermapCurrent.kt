package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.request

import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.model.data.openweather.current.Current
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenweathermapCurrent {

    @GET("weather")
    fun loadByCoordinates(@Query("lat") lat: Double,
                          @Query("lon") lon: Double,
                          @Query("appid") key: String,
                          @Query("units") units: String,
                          @Query("lang") lang: String): Call<Current>

    @GET("weather")
    fun uploadByName(@Query("q") q: String,
                          @Query("appid") key: String,
                          @Query("units") units: String,
                          @Query("lang") lang: String): Call<Current>

    @GET("weather")
    fun uploadByIndex(@Query("zip") zip: String,
                     @Query("appid") key: String,
                     @Query("units") units: String,
                     @Query("lang") lang: String): Call<Current>
}