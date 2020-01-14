package reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain

import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.model.data.openweather.current.Coord

sealed class Place {

    data class Result(val name: String,
                      val lat: Double,
                      val lon: Double) : Place()
    data class Coordinates(val coord: Coord?, val error: Throwable?) : Place()
    data class Places(val list: List<Result>?, val error: Throwable?) : Place()
}