package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data

import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.model.data.openweather.current.Current
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.model.data.openweather.forecast.ForecastList

interface RequestedWeather {

    suspend fun requestServerByCoordinatesAsync(lat: Double, lon: Double): Pair<Current, ForecastList>
    suspend fun requestServerByNameAsync(q: String): Pair<Current, ForecastList>
    suspend fun requestServerByIndexAsync(zip: String): Pair<Current, ForecastList>
}