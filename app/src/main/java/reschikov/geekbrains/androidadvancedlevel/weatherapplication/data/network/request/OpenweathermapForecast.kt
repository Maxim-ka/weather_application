package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.request

import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.model.data.openweather.forecast.ForecastList
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenweathermapForecast {

    @GET("forecast")
    fun loadForecastWeather(@Query("lat") lat: Double,
                            @Query("lon") lon: Double,
                            @Query("appid") key: String,
                            @Query("units") units: String = "metric",
                            @Query("lang") lang: String): Call<ForecastList>
}