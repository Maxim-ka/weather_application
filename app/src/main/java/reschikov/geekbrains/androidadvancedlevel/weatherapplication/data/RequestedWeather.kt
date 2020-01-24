package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data

import reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain.Weather

interface RequestedWeather {

    suspend fun requestServerByCoordinatesAsync(lat: Double, lon: Double): Weather.Received
    suspend fun requestServerByNameAsync(q: String): Weather.Received
    suspend fun requestServerByIndexAsync(zip: String): Weather.Received
}