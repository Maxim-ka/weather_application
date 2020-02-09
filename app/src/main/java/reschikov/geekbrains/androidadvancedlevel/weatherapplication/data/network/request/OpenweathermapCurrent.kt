package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.request

import reschikov.geekbrains.androidadvancedlevel.weatherapplication.*
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.model.data.openweather.current.Current
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenweathermapCurrent {

    @GET(WEATHER)
    fun loadByCoordinates(@Query(LAT) lat: Double,
                          @Query(LON) lon: Double,
                          @Query(APP_ID) key: String,
                          @Query(UNITS) units: String,
                          @Query(LANG) lang: String): Call<Current>

    @GET(WEATHER)
    fun uploadByName(@Query(Q) q: String,
                     @Query(APP_ID) key: String,
                     @Query(UNITS) units: String,
                     @Query(LANG) lang: String): Call<Current>

    @GET(WEATHER)
    fun uploadByIndex(@Query(ZIP) zip: String,
                      @Query(APP_ID) key: String,
                      @Query(UNITS) units: String,
                      @Query(LANG) lang: String): Call<Current>
}