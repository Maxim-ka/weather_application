package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data

import reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain.DataWeather

interface RequestedWeather {

    suspend fun requestServerAsync(lat: Double, lon: Double): DataWeather
}