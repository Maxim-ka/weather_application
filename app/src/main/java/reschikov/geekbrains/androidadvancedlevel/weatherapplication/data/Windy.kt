package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data

import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.model.data.openweather.current.Wind

interface Windy {
    fun identifyWind(wind: Wind?): String
}