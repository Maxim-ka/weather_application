package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data

import reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain.Weather

interface RequestedWeather {

    suspend fun requestServerAsync(lat: Double, lon: Double): Weather.Data
}