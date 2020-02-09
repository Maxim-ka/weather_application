package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.request

import reschikov.geekbrains.androidadvancedlevel.weatherapplication.UNITS_METRIC
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.model.data.openweather.forecast.ForecastList
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenweathermapForecast {

    @GET("forecast")
    fun loadByCoordinates(@Query("lat") lat: Double,
                          @Query("lon") lon: Double,
                          @Query("appid") key: String,
                          @Query("units") units: String = UNITS_METRIC,
                          @Query("lang") lang: String): Call<ForecastList>

    @GET("forecast")
    fun uploadByName(@Query("q") q: String,
                     @Query("appid") key: String,
                     @Query("units") units: String,
                     @Query("lang") lang: String): Call<ForecastList>

    @GET("forecast")
    fun uploadByIndex(@Query("zip") zip: String,
                      @Query("appid") key: String,
                      @Query("units") units: String,
                      @Query("lang") lang: String): Call<ForecastList>
}